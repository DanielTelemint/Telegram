package com.lunamint.lunagram.secure;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

import static java.security.spec.RSAKeyGenParameterSpec.F4;


public class SecureKeyStore {
    private static volatile SecureKeyStore Instance = null;
    private static final String kProvider = "AndroidKeyStore";
    private static final int KEY_LENGTH_BIT = 2048;
    private static final int VALIDITY_YEARS = 100;
    private static final String ALGORITHM = KeyProperties.KEY_ALGORITHM_RSA + "/" + KeyProperties.BLOCK_MODE_ECB + "/" + KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1;
    private static final String CHARSET = "utf-8";

    private boolean isSupported = false;
    private KeyStore.Entry keyEntry;

    public static SecureKeyStore getInstance() {
        SecureKeyStore walletManagerInstance = Instance;
        if (walletManagerInstance == null) {
            synchronized (SecureKeyStore.class) {
                walletManagerInstance = Instance;
                if (walletManagerInstance == null) {
                    Instance = walletManagerInstance = new SecureKeyStore();
                }
            }
        }
        return walletManagerInstance;
    }

    public void init(Context appContext) {

        if (isSupported) return;

        String alias = appContext.getPackageName() + ".secure";

        try {
            KeyStore ks = KeyStore.getInstance(kProvider);
            ks.load(null);
            if (ks.containsAlias(alias)) {
                isSupported = true;
                return;
            }

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, kProvider);
                kpg.initialize(new KeyGenParameterSpec.Builder(alias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(KEY_LENGTH_BIT, F4))
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .setDigests(KeyProperties.DIGEST_SHA512,
                                KeyProperties.DIGEST_SHA384,
                                KeyProperties.DIGEST_SHA256)
                        .setUserAuthenticationRequired(false)
                        .build());

                kpg.generateKeyPair();
                isSupported = true;
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                isSupported = false;
            }
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                Calendar cal = Calendar.getInstance();
                Date now = cal.getTime();

                cal.add(Calendar.YEAR, VALIDITY_YEARS);
                Date end = cal.getTime();

                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", kProvider);
                kpg.initialize(new KeyPairGeneratorSpec.Builder(appContext)
                        .setAlias(alias)
                        .setStartDate(now)
                        .setEndDate(end)
                        .setSerialNumber(BigInteger.ONE)
                        .setSubject(new X500Principal("CN=" + alias))
                        .build());

                kpg.generateKeyPair();
                isSupported = true;
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                isSupported = false;
            }
        }
    }

    public boolean isSupported() {
        return isSupported;
    }

    public String encrypt(Context appContext, String text) {
        if (!isSupported) return null;

        String alias = appContext.getPackageName() + ".secure";

        try {
            if (keyEntry == null) {
                KeyStore ks = KeyStore.getInstance(kProvider);
                ks.load(null);
                keyEntry = ks.getEntry(alias, null);
            }

            if (keyEntry != null) {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, ((KeyStore.PrivateKeyEntry) keyEntry).getCertificate().getPublicKey());

                byte[] encrypted = cipher.doFinal(text.getBytes(CHARSET));
                return Base64.encodeToString(encrypted, Base64.DEFAULT);
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String decrypt(Context appContext, String text) {
        if (!isSupported) return null;

        String alias = appContext.getPackageName() + ".secure";

        try {
            if (keyEntry == null) {
                KeyStore ks = KeyStore.getInstance(kProvider);
                ks.load(null);
                keyEntry = ks.getEntry(alias, null);
            }

            if (keyEntry != null) {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, ((KeyStore.PrivateKeyEntry) keyEntry).getPrivateKey());

                byte[] base64EncryptedBytes = Base64.decode(text.getBytes(CHARSET), Base64.DEFAULT);
                return new String(cipher.doFinal(base64EncryptedBytes));
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return "";
    }
}
