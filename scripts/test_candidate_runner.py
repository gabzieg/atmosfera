"""Fixtures de auditoria/PowerShell; não gera AAB real, chave ou assinatura."""
import base64
import hashlib
import importlib.util
import json
import os
import re
from pathlib import Path
import shutil
import subprocess
import sys
import unittest
from unittest.mock import patch
import zipfile

import test_candidate_catalog as composition_fixtures

ROOT = Path(__file__).resolve().parents[1]
HELPERS = ROOT / 'scripts/candidate'
spec = importlib.util.spec_from_file_location('verify_final_candidate', HELPERS / 'verify_final_candidate.py')
audit = importlib.util.module_from_spec(spec)
spec.loader.exec_module(audit)
sys_path_before = list(sys.path)
sys.path.insert(0, str(HELPERS))
import rehearsal
sys.path[:] = sys_path_before


class CandidateAuditTest(unittest.TestCase):
    def setUp(self):
        fixture = composition_fixtures.CompositionTest('test_valid_fixture_and_cut_are_idempotent')
        fixture.setUp()
        self.addCleanup(fixture.doCleanups)
        self.root = fixture.root
        self.assets = fixture.base
        self.out = self.root / 'receipt'
        self.out.mkdir()
        self.aab = self.root / 'app-release.aab'
        self.cert = b'FAKE_PUBLIC_CERTIFICATE_FIXTURE'
        self.digest = hashlib.sha256(self.cert).hexdigest()
        self.pem = '-----BEGIN CERTIFICATE-----\n' + base64.b64encode(self.cert).decode() + '\n-----END CERTIFICATE-----\n'
        (self.out / 'upload-certificate.log').write_text(self.pem)
        (self.out / 'tools-public.json').write_text(json.dumps(dict(java='17', python='3.11', powershell='7.4', bundletool='1.18.3')))
        (self.out / 'audit-existing.json').write_text('{"fixture":true}')
        (self.out / 'signature-strict.log').write_text('jar verified.\n')
        (self.out / 'signature-strict.stderr.log').write_text('')
        self.strict = dict(exit_code=0, certificate_reference='previously_confirmed_upload_certificate')
        self.integrity = dict(all_entries_signed=True, certificate_currently_valid=True,
                              self_signed=True, has_timestamp=False, entries=10, leaf_sha256=self.digest)
        self.write_signature()
        self.manifest = ('<manifest xmlns:android="http://schemas.android.com/apk/res/android" '
                         'package="com.terra.wallpaper" android:versionCode="8" android:versionName="1.0.5">'
                         '<uses-sdk android:minSdkVersion="26" android:targetSdkVersion="36"/>'
                         '<uses-permission android:name="android.permission.INTERNET"/>'
                         '<application><activity android:name=".debug.DebugActivity" android:exported="false"/></application></manifest>')
        (self.out / 'manifest.log').write_text(self.manifest)
        self.config = self.root / 'android-app/app/build/generated/source/buildConfig/release/com/terra/wallpaper/BuildConfig.java'
        self.config.parent.mkdir(parents=True)
        self.config.write_text('boolean DEBUG = false; String ACERVO_URL = "https://acervo.terrawallpaper.com.br/"; String PLAY_LICENSE_PUBLIC_KEY = "FAKE_PUBLIC_KEY";')
        self.r8 = self.root / 'android-app/app/build/outputs/mapping/release'
        self.r8.mkdir(parents=True)
        (self.r8 / 'mapping.txt').write_text('FAKE_MAPPING')
        (self.r8 / 'configuration.txt').write_text('-keep class example.Fake\n')
        (self.root / 'docs/legal').mkdir(parents=True)
        (self.root / 'docs/legal/manifesto-documentos.json').write_text('{"fixture":true}')
        self.entries = {'base/assets/' + p.relative_to(self.assets).as_posix(): p.read_bytes()
                        for p in self.assets.rglob('*') if p.is_file()}
        self.entries['META-INF/FAKE.RSA'] = b'FAKE_SIGNATURE_BLOCK_NOT_CRYPTO_PROOF'
        self.entries['BUNDLE-METADATA/com.android.tools.build.obfuscation/proguard.map'] = b'FAKE_MAPPING'
        self.write_aab()

    def write_aab(self):
        with zipfile.ZipFile(self.aab, 'w') as z:
            for name, data in self.entries.items():
                z.writestr(name, data)

    def write_signature(self):
        (self.out / 'strict-result.json').write_text(json.dumps(self.strict))
        (self.out / 'signature-integrity.log').write_text(json.dumps(self.integrity))

    def run_audit(self, digest=None):
        with patch.object(audit.subprocess, 'check_output', return_value=audit.APP_COMMIT + '\n'):
            return audit.audit(self.root, self.aab, audit.APP_COMMIT, self.out, digest or self.digest)

    def test_runner_and_receipts_target_app_with_legal_links(self):
        expected = 'e45de4c81cccb88af8dd56ef0247b0931b6b33dd'
        self.assertEqual(audit.APP_COMMIT, expected)
        self.assertEqual(rehearsal.APP_COMMIT, expected)
        powershell = (HELPERS / 'run-final-candidate.ps1').read_text()
        self.assertEqual(re.search(r"\$ExpectedCommit = '([a-f0-9]{40})'", powershell).group(1), expected)
        self.assertIn('`' + expected + '`', (HELPERS / 'README.md').read_text())

    def test_valid_fixture_exports_only_allowlist_without_private_canary(self):
        for name in ['keystore.properties', 'local.properties', 'private.log']:
            (self.out / name).write_text('FAKE_CANARIO_PRIVADO')
        result = self.run_audit()
        self.assertEqual(result['versionCode'], 8)
        with zipfile.ZipFile(self.out / 'candidate-receipt.zip') as z:
            self.assertEqual(set(z.namelist()), {'app-release.aab', 'mapping.txt', 'configuration.txt',
                'manifest.xml', 'provenance-final.json', 'signature-public.json', 'audit-existing.json', 'tools-public.json'})
            self.assertTrue(all(b'FAKE_CANARIO_PRIVADO' not in z.read(n) for n in z.namelist()))

    def test_unsigned_wrong_certificate_and_mapping_rejected(self):
        with self.assertRaises(ValueError):
            self.run_audit('0' * 64)
        del self.entries['META-INF/FAKE.RSA']
        self.write_aab()
        with self.assertRaises(ValueError):
            self.run_audit()
        self.entries['META-INF/FAKE.RSA'] = b'FAKE_SIGNATURE_BLOCK'
        self.entries['BUNDLE-METADATA/com.android.tools.build.obfuscation/proguard.map'] = b'WRONG'
        self.write_aab()
        with self.assertRaises(ValueError):
            self.run_audit()

    def test_tool_versions_reject_private_text_and_unknown_fields(self):
        for value in [dict(java='17', python='3.11', powershell='7.4', bundletool='private value /Users/FAKE_CANARIO_PRIVADO'),
                      dict(java='17', python='3.11', powershell='7.4', bundletool='1.18.3', extra='FAKE_CANARIO_PRIVADO')]:
            (self.out / 'tools-public.json').write_text(json.dumps(value))
            with self.assertRaises(ValueError):
                self.run_audit()

    def test_symlink_preflight_checks_real_resolution_and_cleans_up(self):
        before = set(self.out.iterdir())
        rehearsal.verify_environment(self.out)
        self.assertEqual(set(self.out.iterdir()), before)
        with patch.object(Path, 'symlink_to', side_effect=OSError(1314, 'FAKE_PRIVATE_PATH')):
            with self.assertRaisesRegex(ValueError, 'sem suporte a symlink'):
                rehearsal.verify_environment(self.out)
        self.assertEqual(set(self.out.iterdir()), before)
        # CLI falha fechada e não expõe exceção/caminho privado.
        with patch.object(sys, 'argv', ['rehearsal.py', '--check-environment', '--output', str(self.out)]), \
                patch.object(Path, 'symlink_to', side_effect=OSError(1314, 'FAKE_PRIVATE_PATH')), \
                patch('builtins.print') as output:
            self.assertEqual(rehearsal.main(), 1)
        self.assertNotIn('FAKE_PRIVATE_PATH', str(output.call_args_list))

    def test_rehearsal_device_and_acceptance_fail_closed(self):
        device_log = self.out / 'devices.log'
        for text in ['List of devices attached\n', 'List of devices attached\na device\nb device\n',
                     'List of devices attached\na unauthorized\n', 'List of devices attached\na offline\n']:
            device_log.write_text(text)
            with self.assertRaises(ValueError):
                rehearsal.verify_device(self.out)
        device_log.write_text('List of devices attached\nFAKE_PRIVATE_SERIAL device\n')
        rehearsal.verify_device(self.out)
        receipt = self.out / 'accepted.json'
        valid = dict(app_commit=audit.APP_COMMIT, apk_sha256=audit.file_sha256(self.aab), accepted_by_lead=True,
                     lead_acceptance_url='https://github.com/gabzieg/atmosfera/issues/86#issuecomment-123')
        receipt.write_text(json.dumps(valid))
        rehearsal.verify_acceptance(receipt, self.aab)
        with self.assertRaises(ValueError):
            rehearsal.verify_acceptance(receipt, self.root / 'missing.apk')
        changed = self.root / 'wrong.apk'
        changed.write_bytes(b'changed')
        with self.assertRaises(ValueError):
            rehearsal.verify_acceptance(receipt, changed)
        for change in [dict(app_commit='6ccd02c09b527bd45cdc44f8929636864e1fd179'), dict(app_commit='wrong'), dict(accepted_by_lead=False), dict(apk_sha256=''),
                       dict(lead_acceptance_url='https://evil.invalid/')]:
            receipt.write_text(json.dumps(dict(valid, **change)))
            with self.assertRaises(ValueError):
                rehearsal.verify_acceptance(receipt, self.aab)

    def test_rehearsal_exports_apk_and_receipt_without_serial(self):
        (self.out / 'devices.log').write_text('List of devices attached\nFAKE_PRIVATE_SERIAL device\n')
        apk = self.root / 'app-debug.apk'
        with zipfile.ZipFile(apk, 'w') as package:
            for name, data in self.entries.items():
                if name.startswith('base/assets/'):
                    package.writestr(name[len('base/'):], data)
        with patch.object(rehearsal.subprocess, 'check_output', return_value=audit.APP_COMMIT + '\n'):
            result = rehearsal.export_rehearsal(self.root, apk, self.out)
        self.assertFalse(result['accepted_by_lead'])
        with zipfile.ZipFile(self.out / 'rehearsal-package.zip') as package:
            self.assertEqual(set(package.namelist()), {'app-debug.apk', 'rehearsal-receipt.json'})
            self.assertNotIn(b'FAKE_PRIVATE_SERIAL', package.read('rehearsal-receipt.json'))

    def test_extra_missing_and_modified_assets_rejected(self):
        original = dict(self.entries)
        for kind in ['extra', 'missing', 'changed']:
            self.entries = dict(original)
            target = next(n for n in self.entries if n.startswith('base/assets/'))
            if kind == 'extra':
                self.entries['base/assets/unexpected.bin'] = b'extra'
            elif kind == 'missing':
                del self.entries[target]
            else:
                self.entries[target] = b'changed'
            self.write_aab()
            with self.subTest(kind=kind), self.assertRaises(ValueError):
                self.run_audit()

    def test_code_endpoint_and_empty_billing_rejected(self):
        (self.out / 'manifest.log').write_text(self.manifest.replace('versionCode="8"', 'versionCode="7"'))
        with self.assertRaises(ValueError):
            self.run_audit()
        (self.out / 'manifest.log').write_text(self.manifest)
        config = self.config.read_text()
        for text in [config.replace('https://acervo.terrawallpaper.com.br/', 'https://wrong.invalid/'),
                     config.replace('"FAKE_PUBLIC_KEY"', '""'), config.replace('DEBUG = false', 'DEBUG = true')]:
            self.config.write_text(text)
            with self.assertRaises(ValueError):
                self.run_audit()

    def test_only_specific_self_signed_case_can_classify_strict_four(self):
        self.strict['exit_code'] = 4
        self.write_signature()
        (self.out / 'signature-strict.log').write_text('jar verified, with signer errors.\n\nError:\nThis jar contains entries whose signer certificate is self-signed.\n\nWarning:\nNo timestamp.\n')
        self.assertEqual(self.run_audit()['signature']['classification'], 'trusted_self_signed_valid_integrity_checked')
        for change in [dict(certificate_currently_valid=False), dict(has_timestamp=True), dict(all_entries_signed=False), dict(leaf_sha256='0' * 64)]:
            original = dict(self.integrity)
            self.integrity.update(change)
            self.write_signature()
            with self.subTest(change=change), self.assertRaises(ValueError):
                self.run_audit()
            self.integrity = original
        self.write_signature()
        (self.out / 'signature-strict.log').write_text('Error:\nAn algorithm is disabled.\nWarning:\n')
        with self.assertRaises(ValueError):
            self.run_audit()
        for code in [1, 8, 16, 64]:
            self.strict['exit_code'] = code
            self.write_signature()
            with self.subTest(code=code), self.assertRaises(ValueError):
                self.run_audit()

    def test_r8_private_comment_is_sanitized_but_directive_never_hidden(self):
        (self.r8 / 'configuration.txt').write_text('# original /Users/FAKE_OWNER/sdk/rules.pro\n-keep class example.Fake\n')
        report = self.run_audit()
        self.assertNotIn('FAKE_OWNER', (self.out / 'configuration.txt').read_text())
        self.assertTrue(report['artifacts']['configuration.txt']['sanitized_comments_only'])
        (self.r8 / 'configuration.txt').write_text('-injars C:\\Users\\FAKE_OWNER\\input.jar\n')
        with self.assertRaises(ValueError):
            self.run_audit()

    def test_pending_app_target_blocks_both_phases_before_any_operation(self):
        pwsh = os.environ.get('CANDIDATE_PWSH') or shutil.which('pwsh')
        self.assertIsNotNone(pwsh)
        before = set(self.root.rglob('*'))
        for phase in ['Rehearsal', 'Final']:
            result = subprocess.run([pwsh, '-NoProfile', '-NonInteractive', '-File',
                str(HELPERS / 'run-final-candidate.ps1'), '-Phase', phase,
                '-Repository', str(self.root), '-BundletoolJar', str(self.root / 'missing.jar'),
                '-ExistingProperties', str(self.root / 'missing.properties')], capture_output=True,
                text=True, timeout=15)
            self.assertEqual(result.returncode, 1)
            self.assertIn('FALHA candidato: etapa target-refresh-required', result.stdout)
            self.assertNotIn('FIM candidato', result.stdout)
            self.assertEqual(set(self.root.rglob('*')), before)

    def test_powershell_syntax_and_native_failure_controls(self):
        pwsh = os.environ.get('CANDIDATE_PWSH') or shutil.which('pwsh')
        self.assertIsNotNone(pwsh, 'PowerShell requerido para validar o runner')
        result = subprocess.run([pwsh, '-NoProfile', '-NonInteractive', '-File', str(HELPERS / 'test-runner.ps1')], capture_output=True, text=True, timeout=40)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertIn('FIM testes PowerShell', result.stdout)
        self.assertNotIn('FAKE_CANARIO_PRIVADO', result.stdout + result.stderr)

    def test_java_configuration_references_existing_key_without_overwrite_or_leak(self):
        original = self.root / 'original/android-app'
        (original / 'app').mkdir(parents=True)
        key = original / 'fake-key.bin'
        key.write_bytes(b'FAKE_NOT_A_KEY')
        sdk = original / 'fake-sdk'
        sdk.mkdir()
        (original / 'local.properties').write_text('sdk.dir=fake-sdk\n')
        properties = original / 'keystore.properties'
        values = ('storeFile=../fake-key.bin\nstorePassword=FAKE_CANARIO_PRIVADO\nkeyAlias=fake\n'
                  'keyPassword=FAKE_CANARIO_PRIVADO\nplayLicensePublicKey=FAKE_PUBLIC_KEY\n')
        properties.write_text(values)
        target = self.root / 'isolated/android-app/keystore.properties'
        local = self.root / 'isolated/android-app/local.properties'
        command = ['java', str(HELPERS / 'PrepareSigning.java'), str(properties), str(target), str(original / 'local.properties'), str(local)]
        result = subprocess.run(command, capture_output=True, text=True, timeout=30)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(properties.read_text(), values)
        self.assertEqual(key.read_bytes(), b'FAKE_NOT_A_KEY')
        self.assertIn(str(key.absolute()), target.read_text())
        self.assertTrue(local.is_file())
        result = subprocess.run(command, capture_output=True, text=True, timeout=30)
        self.assertNotEqual(result.returncode, 0)
        self.assertNotIn('FAKE_CANARIO_PRIVADO', result.stdout + result.stderr)

    def test_java_signature_verifier_rejects_unsigned_fixture_without_owner_key(self):
        result = subprocess.run(['java', str(HELPERS / 'VerifySignature.java'), str(self.aab), self.digest], capture_output=True, text=True, timeout=30)
        self.assertNotEqual(result.returncode, 0)
        self.assertNotIn(self.pem, result.stdout + result.stderr)


if __name__ == '__main__':
    unittest.main()
