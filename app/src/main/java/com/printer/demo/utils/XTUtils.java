package com.printer.demo.utils;

import android.content.res.Resources;
import android.util.Log;

import com.printer.demo.R;
import com.printer.demo.ymode.CRC;
import com.printer.demo.ymode.CRC16;
import com.printer.demo.ymode.YModem;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterConstants.Command;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.Table;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class XTUtils {


	private static final String TAG = "XUtils";
	public YModem mo;
	public boolean aaa;
	public static  byte[] string2bytes(String content){

		Log.i(TAG, ""+content);
		char[] charArray = content.toCharArray();
		byte[] tempByte = new byte[512];
		tempByte[0] = 0x34;
		int count = 0;
		for (int i = 0; i < charArray.length; i++) {
			if(charArray[i] == 'x'){
				tempByte[count++] = (byte) (char2Int(charArray[i+1])*16+char2Int(charArray[i+2]));
			}
		}
		Log.i(TAG, "---------------");
		byte[] retByte = new byte[count];
		System.arraycopy(tempByte, 0, retByte, 0, count);
		for (int i = 0; i < retByte.length; i++) {
			Log.i(TAG, retByte[i]+"");
		}

		return tempByte;
	}


	private static int char2Int(char data){
		if(data>= 48 && data<= 57)//0~9
			data -= 48;
		else if(data >= 65 && data <= 70)//A~F
			data -= 55;
		else if(data >= 97 && data <= 102)//a~f
			data -= 87;
		return Integer.valueOf(data);
	}

	private static boolean is58mm = false;
	public static void printNote(Resources resources, PrinterInstance mPrinter) {
		if(PrinterConstants.paperWidth == 384)
			is58mm = true;
		else
			is58mm = false;
		mPrinter.initPrinter();

		mPrinter.setFont(0,0,0,0,0);
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		mPrinter.printText(resources.getString(R.string.str_note));
		mPrinter.printText("ອັດຕາຕົວເມືອງຂອງຈີນສູງຂຶ້ນເຖິງ");
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);


		StringBuffer sb = new StringBuffer();

		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
		mPrinter.setFont(0, 1, 1, 0, 0);
		mPrinter.printText(resources.getString(R.string.shop_company_title)
				+ "\n");

		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		// 字号使用默认
		mPrinter.setFont(0, 0, 0, 0, 0);
		sb.append(resources.getString(R.string.shop_num) + "574001\n");
		sb.append(resources.getString(R.string.shop_receipt_num)
				+ "S00003169\n");
		sb.append(resources.getString(R.string.shop_cashier_num)
				+ "s004_s004\n");

		sb.append(resources.getString(R.string.shop_receipt_date)
				+ "2012-06-17\n");
		sb.append(resources.getString(R.string.shop_print_time)
				+ "2012-06-17 13:37:24\n");
		mPrinter.printText(sb.toString()); // 打印

		printTable1(resources, mPrinter, is58mm); // 打印表格

		sb = new StringBuffer();
		if (is58mm) {
			sb.append(resources.getString(R.string.shop_goods_number)
					+ "                6.00\n");
			sb.append(resources.getString(R.string.shop_goods_total_price)
					+ "                35.00\n");
			sb.append(resources.getString(R.string.shop_payment)
					+ "                100.00\n");
			sb.append(resources.getString(R.string.shop_change)
					+ "                65.00\n");
		} else {
			sb.append(resources.getString(R.string.shop_goods_number)
					+ "                                6.00\n");
			sb.append(resources.getString(R.string.shop_goods_total_price)
					+ "                                35.00\n");
			sb.append(resources.getString(R.string.shop_payment)
					+ "                                100.00\n");
			sb.append(resources.getString(R.string.shop_change)
					+ "                                65.00\n");
		}

		sb.append(resources.getString(R.string.shop_company_name) + "\n");
		sb.append(resources.getString(R.string.shop_company_site)
				+ "www.jiangsuxxxx.com\n");
		sb.append(resources.getString(R.string.shop_company_address) + "\n");
		sb.append(resources.getString(R.string.shop_company_tel)
				+ "0574-12345678\n");
		sb.append(resources.getString(R.string.shop_Service_Line)
				+ "4008-123-456 \n");
		if (is58mm) {
			sb.append("==============================\n");
		} else {
			sb.append("==============================================\n");
		}
		mPrinter.printText(sb.toString());

		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
		mPrinter.setFont(0, 0, 1, 0, 0);
		mPrinter.printText(resources.getString(R.string.shop_thanks) + "\n");
		mPrinter.printText(resources.getString(R.string.shop_demo) + "\n\n\n");

		mPrinter.setFont(0, 0, 0, 0,0);
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

	}
	public static void printTable1(Resources resources,
			PrinterInstance mPrinter, boolean is58mm) {

		String column = resources.getString(R.string.note_title);
		Table table;
		if (is58mm) {
			table = new Table(column, ";", new int[] { 14, 6, 6, 6 });
		} else {
			table = new Table(column, ";", new int[] { 18, 10, 10, 12 });
		}
		table.addRow("" + resources.getString(R.string.bags) + ";10.00;1;10.00");
		table.addRow("" + resources.getString(R.string.hook) + ";5.00;2;10.00");
		table.addRow("" + resources.getString(R.string.umbrella)
				+ ";5.00;3;15.00");
		mPrinter.printTable(table);
	}



	public static void printTest(Resources resources,PrinterInstance mPrinter){

		mPrinter.initPrinter();

		mPrinter.printText(resources.getString(R.string.str_text));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);


		mPrinter.setFont(0, 0, 0, 0,0);
		mPrinter.setPrinter(Command.ALIGN, 0);
		mPrinter.printText(resources.getString(R.string.str_text_left));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);// ��2��


		mPrinter.setPrinter(Command.ALIGN, 1);
		mPrinter.printText(resources.getString(R.string.str_text_center));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);// ��2��

		mPrinter.setPrinter(Command.ALIGN, 2);
		mPrinter.printText(resources.getString(R.string.str_text_right));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3); // ��3��

		mPrinter.setPrinter(Command.ALIGN, 0);
		mPrinter.setFont(0,0, 0, 1, 0);
		mPrinter.printText(resources.getString(R.string.str_text_strong));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2); // ��2��

		mPrinter.setFont(0,0, 0, 0, 1);
		mPrinter.sendBytesData(new byte[]{(byte)0x1C,(byte)0x21,(byte)0x80});
		mPrinter.printText(resources.getString(R.string.str_text_underline));
		mPrinter.sendBytesData(new byte[]{(byte)0x1C,(byte)0x21,(byte)0x00});
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2); // ��2��

		mPrinter.setFont(0,0, 0, 0, 0);
		mPrinter.printText(resources.getString(R.string.str_text_height));

		for(int i=0;i<4;i++)
	    {
	    	mPrinter.setFont(0,i, i, 0, 0);
	   		mPrinter.printText((i+1)+resources.getString(R.string.times));
	    }
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

		  for(int i=0;i<4;i++)
	       {


	    	mPrinter.setFont(0,i, i, 0, 0);
	   		mPrinter.printText(resources.getString(R.string.bigger)+(i+1)+resources.getString(R.string.bigger1));
	   		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

	       }

		    mPrinter.setFont(0,0, 0, 0, 0);
		    mPrinter.setPrinter(Command.ALIGN, 0);
		    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
	}
	public static int update(Resources resources,PrinterInstance mPrinter,InputStream in) {
		DataInputStream dataStream = new DataInputStream(in);
		byte[] stream = hexStringToByte(resources.getString(R.string.file_e).replaceAll(" ",""));
		byte[] stream1 = hexStringToByte(resources.getString(R.string.file_f).replaceAll(" ",""));
		CRC16 crc = new CRC16();
		byte[] comein = new byte[]{(byte)27, (byte)9, (byte)27, (byte)250, (byte)117, (byte)112, (byte)103, (byte)114, (byte)97, (byte)100, (byte)101};
		byte[] send = new byte[]{(byte)85};
		byte[] ready = new byte[]{(byte)49};
     	byte[] block = new byte[]{(byte)1,(byte)0,(byte)255,(byte)84,(byte)54,(byte)95,(byte)80,(byte)82, (byte)74,(byte)46,(byte)98,(byte)105,(byte)110,(byte)00,(byte)49,(byte)51,(byte)52,(byte)50,(byte)50,(byte)52};
       	byte[] filename = hexStringToByte("01 00 FF 54 36 5F 50 52 4A 2E 62 69 6E 00 31 33 34 32 32 34 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 89 DF".replaceAll(" ",""));
        mPrinter.sendBytesData(comein);
		Log.d("update", "进入升级模式");
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mPrinter.sendBytesData(send);
		Log.d("update", "发送55");
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mPrinter.sendBytesData(ready);
		Log.d("update", "发送31");
		//发送文件名
			try {
				Log.d(TAG, "update: 大小："+dataStream.available());
			} catch (IOException e) {
				e.printStackTrace();
			}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mPrinter.sendBytesData(filename);
		Log.d(TAG, "update: 文件名发送over");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//发送文件
		byte[] files = new byte[1024];
		try {
			sendDataBlocks(stream,stream1,dataStream,1,crc,files,mPrinter);
			Log.d(TAG, "update: 文件发送over");
			} catch (IOException e) {
			e.printStackTrace();
			return 5;
			}
		mPrinter.closeConnection();
		return -2;
    }

    public static void sendDataBlocks(byte[] stream,byte[] stream1,DataInputStream dataStream, int blockNumber, CRC crc, byte[] block,PrinterInstance mPrinter) throws IOException {
		int dataLength;
		int star=0;
		int star1 = 0;
		int end = 1024;
		int end1 = 1024;
		int j = stream.length/1024;
		int k = stream1.length/1024+1;
		Log.d(TAG, "sendDataBlocks: length"+j);
		Log.d(TAG, "sendDataBlocks: length"+k);
		//循环发送数据
			for (int i=0;i<j;i++){
				block = Arrays.copyOfRange(stream,star,end);
				star = star+1024;
				end = end+1024;
				dataLength = block.length;
				sendBlock(blockNumber++,block, dataLength, crc,mPrinter);
			}
		for (int i=0;i<k;i++){
			block = Arrays.copyOfRange(stream1,star1,end1);
			star1 = star1+1024;
			end1 = end1+1024;
			dataLength = block.length;
			sendBlock(blockNumber++,block, dataLength, crc,mPrinter);
		}
//		while ((dataLength = dataStream.read(block)) != -1) {
//			//String[] data = bytesToHexStrings(block);
//            sendBlock(blockNumber++,block, dataLength, crc,mPrinter);
//            Log.d("updata", "第" + (blockNumber-1) + "数据");
//        }
    }
    public static void sendBlock(int blockNumber, byte[] block, int dataLength, CRC crc, PrinterInstance mPrinter) throws IOException {
		if(dataLength < block.length) {
			Log.d("updata", "数据" + dataLength);

			for(int e = dataLength; e < 1024; ++e) {
				block[e] = 0;
			}
		}
            if (block.length == 1024){
				Log.d(TAG, "sendBlock: 1024");
				mPrinter.sendBytesData(new byte[]{(byte)2});
            }
            else //128
            {
				Log.d(TAG, "sendBlock: 128");
				mPrinter.sendBytesData(new byte[]{(byte)1});
            }
            mPrinter.sendBytesData(new byte[]{(byte)blockNumber});
            mPrinter.sendBytesData(new byte[]{(byte)~blockNumber});
			mPrinter.sendBytesData(block);
            writeCRC(block, crc,mPrinter);
		Log.d(TAG, "sendBlock: blockNumber:"+blockNumber);
		Log.d(TAG, "sendBlock: ~blockNumber:"+~blockNumber);
		Log.d(TAG, "sendBlock: over");
//		int c = 0;
//		byte[] rev3=new byte[2000];
//		mPrinter.read(rev3);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		for (int i=0;i<rev3.length;i++){
//			Log.d(TAG, "update: revFile"+i+":"+rev3[i]);
//			if (rev3[i]==6){
//				break;
//			}
//		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

    private static void writeCRC(byte[] block, CRC crc,PrinterInstance mPrinter) throws IOException {
        byte[] crcBytes = new byte[crc.getCRCLength()];
        long crcValue = crc.calcCRC(block);
        for (int i = 0; i < crc.getCRCLength(); i++) {
            crcBytes[crc.getCRCLength() - i - 1] = (byte) ((crcValue >> (8 * i)) & 0xFF);
        }
        mPrinter.sendBytesData(crcBytes);
    }
		/**
		 * 把16进制字符串转换成字节数组 * @param hex * @return */
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
	private static byte toByte(char c){
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}



	public static String[] bytesToHexStrings(byte[] src){
		if (src == null || src.length <= 0) {
			return null;
			}
		String[] str = new String[src.length];

		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				str[i] = "0";
				}
			str[i] = hv;
			}
		return str;
		}

	/**
	 * @param is 输入流
	 * @return String 返回的字符串
	 * @throws IOException
	 */
	public static String readFromStream(InputStream is) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while((len = is.read(buffer))!=-1){
			baos.write(buffer, 0, len);
//			baos.flush();
		}
		is.close();
		String result = baos.toString();
		baos.close();
		return result;
	}
	/*
   * 实现字节数组向十六进制的转换方法一
   */
	public static String byte2HexStr(byte[] b)
	{
		String hs = "";
		String stmp = "";

		for (int n = 0; n < b.length; n++)
		{
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}
}
