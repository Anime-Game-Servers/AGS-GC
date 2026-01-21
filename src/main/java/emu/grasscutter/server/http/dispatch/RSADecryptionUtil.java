package emu.grasscutter.server.http.dispatch;


import emu.grasscutter.Grasscutter;
import emu.grasscutter.utils.FileUtils;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSADecryptionUtil {
    private static PrivateKey privateKey;
    private static PublicKey publicKey = null;

    static {
        try {
            Grasscutter.getLogger().info("Loading RSA key");

            byte[] keyBytes = FileUtils.readResource("/keys/private_key.der");

            if (keyBytes == null || keyBytes.length == 0) {
                Grasscutter.getLogger().error("Unable to find private_key.der in resources folder");
                throw new RuntimeException("private_key.der not found in resources");
            }

            // PKCS8
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(spec);

            Grasscutter.getLogger().info("Loaded RSA priv key");
            Grasscutter.getLogger().info("Format: " + privateKey.getFormat());

        } catch (Exception e) {
            Grasscutter.getLogger().error("Unable to load RSA priv key", e);
            e.printStackTrace();
            throw new RuntimeException("Unable to load RSA priv key", e);
        }
    }

    public static String decrypt(String encryptedBase64) throws Exception {
        if (encryptedBase64 == null || encryptedBase64.isEmpty()) {
            throw new IllegalArgumentException("Encrypted data is null or empty");
        }

        Grasscutter.getLogger().debug("Decrypting RSA key");

        try {
            // Decode base64
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);

            // Decrypt using RSA/ECB/PKCS1Padding
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            String result = new String(decryptedBytes, StandardCharsets.UTF_8);
            Grasscutter.getLogger().debug("Successfully decrypted");

            return result;

        } catch (javax.crypto.IllegalBlockSizeException e) {
            Grasscutter.getLogger().error("Invalid public key, is it replaced?");
            throw new Exception("RSA key mismatch", e);
        } catch (javax.crypto.BadPaddingException e) {
            Grasscutter.getLogger().error("Private key doesn't match public key in use by client");
            throw new Exception("RSA decryption failed, key mismatch", e);
        } catch (Exception e) {
            Grasscutter.getLogger().error("Unexpected error", e);
            throw e;
        }
    }
}
