package com.spd.print.jx.utils;

public class StringUtils {

    /**
     * 把16进制字符串转换成字节数组 * @param hex * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * 把十六进制字符串转换成byte[]字节数组
     *
     * @param content 如 "0x54 0x68"
     * @return byte[]
     */
    public static byte[] string2bytes(String content) {
        char[] charArray = content.toCharArray();
        byte[] tempByte = new byte[512];
        tempByte[0] = 0x34;
        int count = 0;
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == 'x') {
                tempByte[count++] = (byte) (char2Int(charArray[i + 1]) * 16 + char2Int(charArray[i + 2]));
            }
        }
        byte[] retByte = new byte[count];
        System.arraycopy(tempByte, 0, retByte, 0, count);
        return tempByte;
    }

    private static int char2Int(char data) {
        if (data >= 48 && data <= 57) {
            //0~9
            data -= 48;
        } else if (data >= 65 && data <= 70) {
            //A~F
            data -= 55;
        } else if (data >= 97 && data <= 102) {
            //a~f
            data -= 87;
        }
        return Integer.valueOf(data);
    }
}
