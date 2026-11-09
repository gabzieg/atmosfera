"""Montagem de relatório com dados sintéticos, sem importar o uploader."""
import unittest

from report import attach_public_history


class ReportTest(unittest.TestCase):
    def setUp(self):
        self.current = {'bucket': 'destino-atual', 'public_access': 'not checked by this uploader'}
        self.previous = {'bucket': 'destino-antigo', 'public_access': 'verified',
                         'public_base_url': 'https://old.invalid/',
                         'public_manifest_url': 'https://old.invalid/manifest.json',
                         'public_verification': {'sha256': 'a' * 64}, 'catalog_bytes': 123}

    def test_previous_proof_does_not_approve_current_access(self):
        result = attach_public_history(self.current, self.previous)
        self.assertEqual(result['public_access'], 'not checked by this uploader')
        self.assertEqual(result['bucket'], 'destino-atual')

    def test_previous_urls_are_not_current_urls(self):
        result = attach_public_history(self.current, self.previous)
        self.assertNotIn('public_base_url', result)
        self.assertNotIn('public_manifest_url', result)

    def test_history_retains_original_destination_and_context(self):
        result = attach_public_history(self.current, self.previous)
        self.assertEqual(result.get('previous_public_evidence'), self.previous)
        result['previous_public_evidence']['public_verification']['sha256'] = 'b' * 64
        self.assertEqual(self.previous['public_verification']['sha256'], 'a' * 64)

    def test_same_bucket_does_not_make_old_proof_current(self):
        self.previous['bucket'] = self.current['bucket']
        self.assertEqual(attach_public_history(self.current, self.previous)['public_access'],
                         'not checked by this uploader')

    def test_missing_context_stays_unknown(self):
        result = attach_public_history(self.current, {'public_access': 'verified'})
        self.assertNotIn('bucket', result['previous_public_evidence'])
        self.assertNotIn('previous_public_evidence', self.current)

    def test_no_history_does_not_invent_evidence(self):
        self.assertEqual(attach_public_history(self.current, {}), self.current)
