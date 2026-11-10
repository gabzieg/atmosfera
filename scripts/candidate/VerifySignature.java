// Verifica JAR/AAB local com as APIs do JDK; saída só de fatos públicos, nunca DN.
import java.nio.file.*;
import java.io.*;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.jar.*;
class VerifySignature {
    public static void main(String[] args) {
        try {
            String expected = args[1].replace(":", "").toLowerCase(Locale.ROOT);
            if (!expected.matches("[a-f0-9]{64}")) throw new IllegalArgumentException();
            int entries = 0; boolean selfSigned = true; boolean timestamp = false;
            try (JarFile jar = new JarFile(args[0], true)) {
                if (jar.getManifest() == null) throw new SecurityException();
                for (JarEntry entry : Collections.list(jar.entries())) {
                    if (entry.isDirectory()) continue;
                    try (InputStream stream = jar.getInputStream(entry)) { stream.transferTo(OutputStream.nullOutputStream()); }
                    String name = entry.getName().toUpperCase(Locale.ROOT);
                    if (name.equals("META-INF/MANIFEST.MF") || name.matches("META-INF/[^/]+\\.(SF|RSA|DSA|EC)")) continue;
                    CodeSigner[] signers = entry.getCodeSigners();
                    if (signers == null || signers.length != 1) throw new SecurityException();
                    X509Certificate leaf = (X509Certificate) signers[0].getSignerCertPath().getCertificates().get(0);
                    leaf.checkValidity();
                    String digest = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(leaf.getEncoded()));
                    if (!digest.equals(expected)) throw new SecurityException();
                    boolean ownIssuer = leaf.getSubjectX500Principal().equals(leaf.getIssuerX500Principal());
                    if (ownIssuer) leaf.verify(leaf.getPublicKey());
                    selfSigned &= ownIssuer;
                    timestamp |= signers[0].getTimestamp() != null;
                    entries++;
                }
            }
            if (entries == 0) throw new SecurityException();
            System.out.println("{\"all_entries_signed\":true,\"certificate_currently_valid\":true,\"self_signed\":" + selfSigned
                    + ",\"has_timestamp\":" + timestamp + ",\"entries\":" + entries + ",\"leaf_sha256\":\"" + expected + "\"}");
        } catch (Exception error) {
            System.err.println("Integridade/assinatura/identidade/validade nao comprovadas; nenhum dado de certificado exposto");
            System.exit(1);
        }
    }
}
