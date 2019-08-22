package com.spd.print.jx.impl;

import com.printer.sdk.PrinterInstance;
import com.spd.print.jx.utils.CRC;
import com.spd.print.jx.utils.CRC16;
import com.spd.print.jx.utils.StringUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 升级打印机SDK
 *
 * @author zzc
 */
public class UpdatePrinter {

    public static int update(InputStream inputStream, PrinterInstance mPrinter) {
        DataInputStream dataStream = new DataInputStream(inputStream);
        CRC16 crc = new CRC16();
        byte[] comein = new byte[]{(byte) 31, (byte) 17, (byte) 31, (byte) 66, (byte) 117, (byte) 112, (byte) 103, (byte) 114, (byte) 97, (byte) 100, (byte) 101};
        byte[] send = new byte[]{(byte) 85};
        byte[] ready = new byte[]{(byte) 49};
        byte[] block = new byte[]{(byte) 1, (byte) 0, (byte) 255, (byte) 84, (byte) 54, (byte) 95, (byte) 80, (byte) 82, (byte) 74, (byte) 46, (byte) 98, (byte) 105, (byte) 110, (byte) 00, (byte) 49, (byte) 51, (byte) 52, (byte) 50, (byte) 50, (byte) 52};
        // 35 31 34 31 32 bin文件的大小
        byte[] filename = StringUtils.hexStringToByte("01 00 FF 54 36 5F 50 52 4A 2E 62 69 6E 00 35 31 34 30 34 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 A9 25".replaceAll(" ", ""));
        mPrinter.sendBytesData(comein);
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPrinter.sendBytesData(send);
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPrinter.sendBytesData(ready);
        //发送文件名
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPrinter.sendBytesData(filename);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //发送文件
        byte[] files = new byte[1024];
        try {
            sendDataBlocks(dataStream, 1, crc, files, mPrinter);
        } catch (IOException e) {
            e.printStackTrace();
            return 5;
        }
        mPrinter.closeConnection();
        return -2;
    }

    public static void sendDataBlocks(DataInputStream dataStream, int blockNumber, CRC crc, byte[] block, PrinterInstance mPrinter) throws IOException {
        int dataLength;
        while ((dataLength = dataStream.read(block)) != -1) {
            sendBlock(blockNumber++, block, dataLength, crc, mPrinter);
        }
    }

    public static void sendBlock(int blockNumber, byte[] block, int dataLength, CRC crc, PrinterInstance mPrinter) throws IOException {
        if (dataLength < block.length) {
            for (int e = dataLength; e < 1024; ++e) {
                block[e] = 0;
            }
        }
        if (block.length == 1024) {
            mPrinter.sendBytesData(new byte[]{(byte) 2});
        } else {
            //128
            mPrinter.sendBytesData(new byte[]{(byte) 1});
        }
        mPrinter.sendBytesData(new byte[]{(byte) blockNumber});
        mPrinter.sendBytesData(new byte[]{(byte) ~blockNumber});
        mPrinter.sendBytesData(block);
        writeCRC(block, crc, mPrinter);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void writeCRC(byte[] block, CRC crc, PrinterInstance mPrinter) throws IOException {
        byte[] crcBytes = new byte[crc.getCRCLength()];
        long crcValue = crc.calcCRC(block);
        for (int i = 0; i < crc.getCRCLength(); i++) {
            crcBytes[crc.getCRCLength() - i - 1] = (byte) ((crcValue >> (8 * i)) & 0xFF);
        }
        mPrinter.sendBytesData(crcBytes);
    }
}
