package com.liberty.exchange.common.crypto;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 5:05
 * Description: EncryptUtils
 */
@Component
@Slf4j
public class EncryptUtils implements ApplicationContextAware {

    @Value("${spring.profiles.active}")
    private String env;

    private static PublicKey rsaPublicKey;
    private static PrivateKey rsaPrivate;

    /**
     * 公钥加密
     *
     * @param text 加密原文
     * @return String
     * @throws GeneralSecurityException GeneralSecurityException
     */
    public String encrypt(String text) throws GeneralSecurityException {
        byte[] rawText = text.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        return new String(Base64Utils.encode(cipher.doFinal(rawText)));
    }

    /**
     * 私钥加密
     *
     * @param text 加密原文
     * @return String
     * @throws GeneralSecurityException GeneralSecurityException
     */
    public String encryptByPrivate(String text) throws GeneralSecurityException {
        byte[] rawText = text.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, rsaPrivate);
        return new String(Base64Utils.encode(cipher.doFinal(rawText)));
    }

    /**
     * 私钥解密
     *
     * @param cipherText 秘文
     * @return String
     * @throws GeneralSecurityException GeneralSecurityException
     */
    public String decrypt(String cipherText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivate);
        return new String(cipher.doFinal(Base64Utils.decode(cipherText.getBytes())), StandardCharsets.UTF_8);
    }

    /**
     * 公钥解密
     *
     * @param cipherText 秘文
     * @return String
     * @throws GeneralSecurityException GeneralSecurityException
     */
    public String decryptByPublicKey(String cipherText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, rsaPublicKey);
        return new String(cipher.doFinal(Base64Utils.decode(cipherText.getBytes())), StandardCharsets.UTF_8);
    }

    /**
     * 字节数据转十六进制字符串
     *
     * @param bytes 输入数据
     * @return 十六进制内容
     */
    public static String bytesToHexFun(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", b & 0xff));
        }
        return buf.toString();
    }

    /**
     * 将16进制字符串转换为byte[]
     */
    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    /**
     * 初始化私钥
     */
    private static PrivateKey generatePrivateKey(KeyFactory factory, InputStream inputStream) throws InvalidKeySpecException, FileNotFoundException, IOException {
        PemFile pemFile = new PemFile(inputStream);
        byte[] content = pemFile.getPemObject().getContent();
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
        return factory.generatePrivate(privKeySpec);
    }

    /**
     * 初始化公钥
     */
    private static PublicKey generatePublicKey(KeyFactory factory, InputStream inputStream) throws InvalidKeySpecException, FileNotFoundException, IOException {
        PemFile pemFile = new PemFile(inputStream);
        byte[] content = pemFile.getPemObject().getContent();
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
        return factory.generatePublic(pubKeySpec);
    }

    /**
     * 初始化密钥
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Security.addProvider(new BouncyCastleProvider());
        log.info("BouncyCastle provider added.......");
        String realEnv = env;
        if (!Arrays.asList("dev", "prod", "test").contains(realEnv)) {
            if (realEnv.startsWith("test"))
                realEnv="test";
            if (realEnv.startsWith("prod"))
                realEnv="prod";
            if (realEnv.startsWith("dev"))
                realEnv="dev";
        }
        try {
            ClassPathResource publicPemResource = new ClassPathResource("keys/" + realEnv + "/rsa_public.key");
            ClassPathResource privatePemResource = new ClassPathResource("keys/" + realEnv + "/rsa_private.key");
            KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
            rsaPublicKey = generatePublicKey(factory, publicPemResource.getInputStream());
            rsaPrivate = generatePrivateKey(factory, privatePemResource.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

