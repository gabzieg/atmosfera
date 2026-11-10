// #86: referências locais no Windows; nunca copia a chave nem imprime valores.
import java.nio.file.*;
import java.util.Properties;
import java.io.*;
class PrepareSigning {
    public static void main(String[] args) {
        try {
            Path original = Path.of(args[0]).toAbsolutePath().normalize();
            Path target = Path.of(args[1]).toAbsolutePath().normalize();
            Properties values = new Properties();
            try (InputStream in = Files.newInputStream(original)) { values.load(in); }
            for (String key : new String[]{"storeFile", "storePassword", "keyAlias", "keyPassword", "playLicensePublicKey"}) {
                if (values.getProperty(key, "").isBlank()) throw new IllegalArgumentException();
            }
            // file(...) no módulo app: caminho relativo ao android-app/app original.
            Path store = Path.of(values.getProperty("storeFile"));
            if (!store.isAbsolute()) store = original.getParent().resolve("app").resolve(store);
            store = store.toAbsolutePath().normalize();
            if (!Files.isRegularFile(store)) throw new IllegalArgumentException();
            values.setProperty("storeFile", store.toString());
            Files.createDirectories(target.getParent());
            try (OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW)) {
                values.store(out, "Configuracao privada local do checkout isolado; nao compartilhar");
            }
            if (args.length == 4 && Files.isRegularFile(Path.of(args[2]))) {
                Path localOriginal = Path.of(args[2]).toAbsolutePath().normalize();
                Properties local = new Properties();
                try (InputStream in = Files.newInputStream(localOriginal)) { local.load(in); }
                String sdkValue = local.getProperty("sdk.dir", "");
                if (sdkValue.isBlank()) throw new IllegalArgumentException();
                Path sdk = Path.of(sdkValue);
                if (!sdk.isAbsolute()) sdk = localOriginal.getParent().resolve(sdk);
                sdk = sdk.toAbsolutePath().normalize();
                if (!Files.isDirectory(sdk)) throw new IllegalArgumentException();
                Properties sdkOnly = new Properties();
                sdkOnly.setProperty("sdk.dir", sdk.toString());
                try (OutputStream out = Files.newOutputStream(Path.of(args[3]), StandardOpenOption.CREATE_NEW)) {
                    sdkOnly.store(out, "SDK local existente; nao compartilhar");
                }
            }
            System.out.println("Configuracao local preparada; chave original preservada");
        } catch (Exception error) {
            System.err.println("Configuracao privada indisponivel/incompleta ou destino existente; nenhum valor exposto");
            System.exit(1);
        }
    }
}
