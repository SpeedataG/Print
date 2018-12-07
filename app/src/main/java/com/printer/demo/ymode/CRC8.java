//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.printer.demo.ymode;

public class CRC8 implements CRC {
    public CRC8() {
    }

    public int getCRCLength() {
        return 1;
    }

    public long calcCRC(byte[] block) {
        byte checkSumma = 0;

        for(int i = 0; i < block.length; ++i) {
            checkSumma += block[i];
        }

        return (long)checkSumma;
    }
}
