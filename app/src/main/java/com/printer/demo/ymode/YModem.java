//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.printer.demo.ymode;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;


public class YModem {
    private Modem modem;

    public YModem(InputStream inputStream, OutputStream outputStream) {
        this.modem = new Modem(inputStream, outputStream);
    }

    @SuppressLint({"NewApi"})
    public int send(InputStream in) {
        if(in == null) {
            return 6;
        } else {
            String file = "UL801C01V1.5";
            "UL801C01V1.5.bin".matches("\\w{1,8}\\.\\w{1,3}");
            DataInputStream dataStream = new DataInputStream(in);
            Timer timer = new Timer(Modem.WAIT_FOR_RECEIVER_TIMEOUT).start();
            CRC16 crc = new CRC16();
            try {

                //send block 0
                this.modem.waitReceiverRequest(timer);
//                String fileNameString = file +(char)0+ in.available();
                Log.d("updata","文件长度:"+in.available());
//                byte[] fileNameBytes = Arrays.copyOf(fileNameString.getBytes(), 128);
                this.modem.sendFileName(0,128,crc);
                //modem.sendBlock(0, Arrays.copyOf(fileNameBytes, 128), 128, crc);
                this.modem.waitReceiverRequest(timer);
                //send data
                byte[] block = new byte[1024];
                modem.sendDataBlocks(dataStream, 1, crc, block);
                modem.sendEOT();
                return -2;
            } catch (IOException var19) {
                return 5;
            } finally {
                try {
                    dataStream.close();
                    in.close();
                } catch (IOException var18) {
                    return 5;
                }
            }
        }
    }
}
