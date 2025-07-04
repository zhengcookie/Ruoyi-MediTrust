package com.ruoyi.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Set;

/**
 * 哈希工具类
 *
 * @author jiachen
 * @since 2023/08/28 21:01
 */
public class HashUtils {

    private static final String MD5 = "MD5";
    private static final String SHA_1 = "SHA-1";
    private static final String SHA_224 = "SHA-224";
    private static final String SHA_256 = "SHA-256";
    private static final String SHA_384 = "SHA-384";
    private static final String SHA_512 = "SHA-512";
    private static final String SHA3_224 = "SHA3-224";
    private static final String SHA3_256 = "SHA3-256";
    private static final String SHA3_384 = "SHA3-384";
    private static final String SHA3_512 = "SHA3-512";
    private static final String SHA3_512_224 = "SHA3-512/224";
    private static final String SHA3_512_256 = "SHA3-512/256";


    /**
     * 列举出jdk可用的hash算法
     */
    private static void listJDKAvailableAlgorithms() {
        // 获取所有已注册的安全提供程序
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            System.out.println("Provider: " + provider.getName());
            Set<Object> keys = provider.keySet();
            for (Object key : keys) {
                System.out.println("  Algorithm: " + provider.get(key));
            }
        }
    }

    /**
     * 摘要十六进制字符串
     *
     * @param algorithm  算法
     * @param dataToHash 需要散列的数据
     * @param salt       盐, 相当于在dataToHash后面直接追加了salt
     * @return {@link String}
     * @throws NoSuchAlgorithmException 没有这样算法异常
     */
    public static String digestHexString(String algorithm, String dataToHash, String salt) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        byte[] dataToHashBytes = dataToHash.getBytes(StandardCharsets.UTF_8);
        messageDigest.update(dataToHashBytes);
        if (!(salt == null || salt.isEmpty())) {
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
            //相当于在原内容dataToHashBytes上，追加了saltBytes
            messageDigest.update(saltBytes);
        }
        byte[] digest = messageDigest.digest();
        // 将哈希值转换为十六进制字符串
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : digest) {
//            String hexString = Integer.toHexString(b & 0xff);
//            hexStringBuilder.append(hexString);//这个做法转换出来的，比如0f,只会显示为f，并不会补0
            /**
             * 将byte转换为十六进制字符串
             * 0-结果不足指定宽度时，用0填充
             * 2-宽度为2
             * @see https://stackoverflow.com/questions/2817752/java-code-to-convert-byte-to-hexadecimal
             */
            String hexString = String.format("%02x", b);
            hexStringBuilder.append(hexString);
        }
        return hexStringBuilder.toString();
    }

    public static String digestHexStringByMD5(String dataToHash, String salt) throws NoSuchAlgorithmException {
        return digestHexString(MD5, dataToHash, salt);
    }

    public static String digestHexStringByMD5(String dataToHash) throws NoSuchAlgorithmException {
        return digestHexString(MD5, dataToHash, null);
    }

    public static String digestHexStringBySHA1(String dataToHash, String salt) throws NoSuchAlgorithmException {
        return digestHexString(SHA_1, dataToHash, salt);
    }

    public static String digestHexStringBySHA1(String dataToHash) throws NoSuchAlgorithmException {
        return digestHexString(SHA_1, dataToHash, null);
    }

    public static String digestHexStringBySHA224(String dataToHash, String salt) throws NoSuchAlgorithmException {
        return digestHexString(SHA_224, dataToHash, salt);
    }

    public static String digestHexStringBySHA224(String dataToHash) throws NoSuchAlgorithmException {
        return digestHexString(SHA_224, dataToHash, null);
    }


    public static String digestHexStringBySHA256(String dataToHash, String salt) throws NoSuchAlgorithmException {
        return digestHexString(SHA_256, dataToHash, salt);
    }

    public static String digestHexStringBySHA256(String dataToHash) throws NoSuchAlgorithmException {
        return digestHexString(SHA_256, dataToHash, null);
    }

    public static String digestHexStringBySHA384(String dataToHash, String salt) throws NoSuchAlgorithmException {
        return digestHexString(SHA_384, dataToHash, salt);
    }

    public static String digestHexStringBySHA384(String dataToHash) throws NoSuchAlgorithmException {
        return digestHexString(SHA_384, dataToHash, null);
    }

    public static String digestHexStringBySHA512(String dataToHash, String salt) throws NoSuchAlgorithmException {
        return digestHexString(SHA_512, dataToHash, salt);
    }

    public static String digestHexStringBySHA512(String dataToHash) throws NoSuchAlgorithmException {
        return digestHexString(SHA_512, dataToHash, null);
    }

    public static String digestHexStringBySHA3_224(String dataToHash, String salt) throws NoSuchAlgorithmException {
        return digestHexString(SHA3_224, dataToHash, salt);
    }

    public static String digestHexStringBySHA3_224(String dataToHash) throws NoSuchAlgorithmException {
        return digestHexString(SHA3_224, dataToHash, null);
    }

    public static String digestHexStringBySHA3_256(String dataToHash, String salt) throws NoSuchAlgorithmException {
        return digestHexString(SHA3_256, dataToHash, salt);
    }

    public static String digestHexStringBySHA3_256(String dataToHash) throws NoSuchAlgorithmException {
        return digestHexString(SHA3_256, dataToHash, null);
    }

    public static String digestHexStringBySHA3_384(String dataToHash, String salt) throws NoSuchAlgorithmException {
        return digestHexString(SHA3_384, dataToHash, salt);
    }

    public static String digestHexStringBySHA3_384(String dataToHash) throws NoSuchAlgorithmException {
        return digestHexString(SHA3_384, dataToHash, null);
    }

    public static String digestHexStringBySHA3_512(String dataToHash, String salt) throws NoSuchAlgorithmException {
        return digestHexString(SHA3_512, dataToHash, salt);
    }

    public static String digestHexStringBySHA3_512(String dataToHash) throws NoSuchAlgorithmException {
        return digestHexString(SHA3_512, dataToHash, null);
    }

    public static String digestHexStringBySHA3_512_224(String dataToHash, String salt) throws NoSuchAlgorithmException {
        return digestHexString(SHA3_512_224, dataToHash, salt);
    }

    public static String digestHexStringBySHA3_512_224(String dataToHash) throws NoSuchAlgorithmException {
        return digestHexString(SHA3_512_224, dataToHash, null);
    }

    public static String digestHexStringBySHA3_512_256(String dataToHash, String salt) throws NoSuchAlgorithmException {
        return digestHexString(SHA3_512_256, dataToHash, salt);
    }

    public static String digestHexStringBySHA3_512_256(String dataToHash) throws NoSuchAlgorithmException {
        return digestHexString(SHA3_512_256, dataToHash, null);
    }


}
