package com.easydatalink.tech.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5辅助类
 * 
 * @author Terry
 * @date 2021/05/10
 */
public class Md5Util {
    private static final char HEX_DIGITS[] =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String toHexString(byte[] b) {
        // String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 对字符串进行md5加密
     * 
     * @param str
     *            待加密的字符串
     * @return
     */
    public static synchronized String md5(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            return toHexString(messageDigest).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param loginPassword
     *            用户输入的登录密码
     * @param md5Password
     *            数据库存储的md5加密过的密码
     * @return 返回登录密码是否匹配
     */
    public static synchronized boolean isMatchPassword(String loginPassword, String md5Password) {
        if (md5Password.equals(md5(loginPassword))) {
            return true;
        }
        return false;
    }

    /**
     * md5加密
     * 
     * @param inStr
     * @return
     * @throws NoSuchAlgorithmException
     */
    static public String EncoderByMd5(String inStr) throws NoSuchAlgorithmException {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte)charArray[i];

        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < md5Bytes.length; i++) {
            int val = (md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }
}
