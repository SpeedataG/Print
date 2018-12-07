//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.printer.demo.ymode;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressLint({"NewApi"})
public class Modem {
    protected static final byte SOH = 1;
    protected static final byte STX = 2;
    protected static final byte EOT = 4;
    protected static final byte ACK = 6;
    protected static final byte NAK = 21;
    protected static final byte CAN = 24;
    protected static final byte CPMEOF = 26;
    protected static final byte ST_C = 67;
    protected static final int MAXERRORS = 10;
    protected static final int BLOCK_TIMEOUT = 1000;
    protected static final int REQUEST_TIMEOUT = 3000;
    protected static final int WAIT_FOR_RECEIVER_TIMEOUT = 60000;
    protected static final int SEND_BLOCK_TIMEOUT = 1000;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final byte[] shortBlockBuffer;
    private final byte[] longBlockBuffer;
    public static int bar_add = 10;
    public static Runnable r;

    protected Modem(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.shortBlockBuffer = new byte[128];
        this.longBlockBuffer = new byte[1024];
    }

    protected boolean waitReceiverRequest(Timer timer) throws IOException {
        int character;
        while (true) {
            try {
                character = readByte(timer);
                if (character == NAK)
                    return false;
                if (character == ST_C) {
                    return true;
                }
            } catch (TimeoutException e) {
                throw new IOException("Timeout waiting for receiver");
            }
        }
    }

    protected void sendDataBlocks(DataInputStream dataStream, int blockNumber, CRC crc, byte[] block) throws IOException {
        int dataLength;
        while ((dataLength = dataStream.read(block)) != -1) {
            sendBlock(blockNumber++, block, dataLength, crc);
            Log.d("updata", "第" + blockNumber + "数据");
        }

    }

    protected void sendEOT() throws IOException {
        int errorCount = 0;
        Timer timer = new Timer(BLOCK_TIMEOUT);
        int character;
        while (errorCount < 10) {
            sendByte(EOT);
            try {
                character = readByte(timer.start());

                if (character == ACK) {
                    return;
                } else if (character == CAN) {
                    throw new IOException("Transmission terminated");
                }
            } catch (TimeoutException ignored) {
            }
            errorCount++;
        }
    }

    public void sendFileName(int blockNumber, int dataLength, CRC crc) throws IOException {
        Timer timer = new Timer(6000);
        int errorCount = 0;
        int character;
        byte[] block = new byte[]{(byte)1,(byte)0,(byte)255,(byte)84,(byte)54,(byte)95,(byte)80,(byte)82, (byte)74,(byte)46,(byte)98,(byte)105,(byte)110,(byte)00,(byte)49,(byte)52,(byte)50,(byte)50,(byte)52,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
        while (errorCount < MAXERRORS) {
            timer.start();

                for (int i = 0;i<112;i++){
                    outputStream.write((byte)0);
                }
            outputStream.write(block);
            outputStream.write((byte)137);
            outputStream.write((byte)223);
            outputStream.flush();
            Log.d("updata","write file over");
            while (true) {
                try {
                    character = readByte(timer);
                    Log.d("updata","读取character"+character);
                    if (character == ACK) {
                        Log.d("updata","character:"+character);
                        return;
                    } else if (character == NAK) {
                        errorCount++;
                        break;
                    } else if (character == CAN) {
                        throw new IOException("Transmission terminated");
                    }
                } catch (TimeoutException e) {
                    errorCount++;
                    return;
                }
            }

        }
    }
    public static byte[] hex2byte(String hex){
        String str = "0123456789ABCDEF";
        char[]hex2char = hex.toCharArray();
        byte[]bytes = new byte[hex.length()/2];
        int temp;
        for (int i =0;i<bytes.length;i++){
            temp = str.indexOf(hex2char[2*i])*16;
            temp+= str.indexOf(hex2char[2*i+1]);
            bytes[i] = (byte)(temp&0xff);
        }
        return bytes;
    }
    public void sendBlock(int blockNumber, byte[] block, int dataLength, CRC crc) throws IOException {
        Timer timer = new Timer(1000L);
        if(dataLength < block.length) {
            Log.d("updata", "数据" + dataLength);

            for(int e = dataLength; e < 1024; ++e) {
                block[e] = 0;
            }
        }

        int errorCount = 0;

        label43:
        while(errorCount < 10) {
            timer.start();
            if(block.length == 1024) {
                this.outputStream.write(2);
                Log.d("updata", "STX");
            } else if(blockNumber == 0) {
                this.outputStream.write(1);
                Log.d("updata", "SOH");
                Log.d("updata", "block:" + block.length);
            } else {
                this.outputStream.write(1);
            }

            this.outputStream.write(blockNumber);
            this.outputStream.write(~blockNumber);
            this.outputStream.write(block);
            this.writeCRC(block, crc);
            this.outputStream.flush();

            try {
                byte character;
                do {
                    character = this.readByte(timer);
                    if(character == ACK) {
                        return;
                    }

                    if(character == NAK) {
                        ++errorCount;
                        continue label43;
                    }
                } while(character != CAN);

                throw new IOException("Transmission terminated");
            } catch (TimeoutException var9) {
                ++errorCount;
            }
        }

        throw new IOException("Too many errors caught, abandoning transfer");
    }

    private void writeCRC(byte[] block, CRC crc) throws IOException {
        byte[] crcBytes = new byte[crc.getCRCLength()];
        long crcValue = crc.calcCRC(block);
        for (int i = 0; i < crc.getCRCLength(); i++) {
            crcBytes[crc.getCRCLength() - i - 1] = (byte) ((crcValue >> (8 * i)) & 0xFF);
        }
        outputStream.write(crcBytes);
    }

    protected void processDataBlocks(CRC crc, int blockNumber, int blockInitialCharacter, DataOutputStream dataOutput) throws IOException {
        boolean result = false;

        while(true) {
            byte errorCount = 0;
            if(blockInitialCharacter == 4) {
                this.sendByte((byte)6);
                return;
            }

            boolean shortBlock = blockInitialCharacter == 1;

            int var14;
            try {
                byte[] block = this.readBlock(blockNumber, shortBlock, crc);
                dataOutput.write(block);
                ++blockNumber;
                boolean var15 = false;
                result = true;
                this.sendByte((byte)6);
            } catch (TimeoutException var10) {
                var14 = errorCount + 1;
                if(var14 == 10) {
                    this.interruptTransmission();
                    throw new IOException("Transmission aborted, error count exceeded max");
                }

                this.sendByte((byte)21);
                result = false;
            } catch (RepeatedBlockException var11) {
                this.sendByte((byte)6);
            } catch (SynchronizationLostException var12) {
                this.interruptTransmission();
                throw new IOException("Fatal transmission error", var12);
            } catch (InvalidBlockException var13) {
                var14 = errorCount + 1;
                if(var14 == 10) {
                    this.interruptTransmission();
                    throw new IOException("Transmission aborted, error count exceeded max");
                }

                this.sendByte((byte)21);
                result = false;
            }

            blockInitialCharacter = this.readNextBlockStart(result);
        }
    }

    protected void sendByte(byte b) throws IOException {
        this.outputStream.write(b);
        this.outputStream.flush();
    }

    protected int requestTransmissionStart(boolean useCRC16) throws IOException {
        int errorCount = 0;
        byte requestStartByte;
        if(!useCRC16) {
            requestStartByte = 21;
        } else {
            requestStartByte = 67;
        }

        Timer timer = new Timer(3000L);

        while(errorCount < 10) {
            this.sendByte(requestStartByte);
            timer.start();

            try {
                byte character;
                do {
                    character = this.readByte(timer);
                } while(character != 1 && character != 2);

                return character;
            } catch (TimeoutException var7) {
                ++errorCount;
            }
        }

        this.interruptTransmission();
        throw new RuntimeException("Timeout, no data received from transmitter");
    }

    protected int readNextBlockStart(boolean lastBlockResult) throws IOException {
        int errorCount = 0;
        Timer timer = new Timer(1000L);

        while(true) {
            timer.start();

            try {
                byte character;
                do {
                    character = this.readByte(timer);
                } while(character != 1 && character != 2 && character != 4);

                return character;
            } catch (TimeoutException var6) {
                ++errorCount;
                if(errorCount >= 10) {
                    this.interruptTransmission();
                    throw new RuntimeException("Timeout, no data received from transmitter");
                }

                this.sendByte((byte)(lastBlockResult?6:21));
            }
        }
    }

    private void shortSleep() {
        try {
            Thread.sleep(10L);
        } catch (InterruptedException var4) {
            try {
                this.interruptTransmission();
            } catch (IOException var3) {
                ;
            }

            throw new RuntimeException("Transmission was interrupted", var4);
        }
    }

    protected void interruptTransmission() throws IOException {
        this.sendByte((byte)24);
        this.sendByte((byte)24);
    }

    protected byte[] readBlock(int blockNumber, boolean shortBlock, CRC crc) throws IOException, TimeoutException, RepeatedBlockException, SynchronizationLostException, InvalidBlockException {
        Timer timer = (new Timer(1000L)).start();
        byte[] block;
        if(shortBlock) {
            block = this.shortBlockBuffer;
        } else {
            block = this.longBlockBuffer;
        }

        byte character = this.readByte(timer);
        if(character == blockNumber - 1) {
            throw new RepeatedBlockException();
        } else if(character != blockNumber) {
            throw new SynchronizationLostException();
        } else {
            character = this.readByte(timer);
            if(character != ~blockNumber) {
                throw new InvalidBlockException();
            } else {
                for(int i = 0; i < block.length; ++i) {
                    block[i] = this.readByte(timer);
                }

                while(this.inputStream.available() < crc.getCRCLength()) {
                    this.shortSleep();
                    if(timer.isExpired()) {
                        throw new TimeoutException();
                    }
                }

                if(crc.calcCRC(block) != this.readCRC(crc)) {
                    throw new InvalidBlockException();
                } else {
                    return block;
                }
            }
        }
    }

    private long readCRC(CRC crc) throws IOException {
        long checkSumma = 0L;

        for(int j = 0; j < crc.getCRCLength(); ++j) {
            checkSumma = (checkSumma << 8) + (long)this.inputStream.read();
        }

        return checkSumma;
    }

    private byte readByte(Timer timer) throws IOException, TimeoutException {
        while(this.inputStream.available() <= 0) {
            if(timer.isExpired()) {
                throw new TimeoutException();
            }

            this.shortSleep();
        }

        int b = this.inputStream.read();
        return (byte)b;
    }

    class InvalidBlockException extends Exception {
        InvalidBlockException() {
        }
    }

    class RepeatedBlockException extends Exception {
        RepeatedBlockException() {
        }
    }

    class SynchronizationLostException extends Exception {
        SynchronizationLostException() {
        }
    }
}
