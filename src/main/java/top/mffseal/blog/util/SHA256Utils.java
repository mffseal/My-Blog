package top.mffseal.blog.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Utils {
    public static String code(String str) {
        MessageDigest messageDigest;
        StringBuffer encodeStr = new StringBuffer("");
        int i;

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));

            //byte数组转字符串
            for (int offset = 0; offset < hash.length; offset++) {
                i = hash[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    encodeStr.append("0");
                encodeStr.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return encodeStr.toString();
    }

    public static void main(String[] args) {
        System.out.println(code("test"));
    }
}
