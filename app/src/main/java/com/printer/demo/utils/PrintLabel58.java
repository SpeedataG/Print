package com.printer.demo.utils;
import com.printer.sdk.PrinterConstants.LableFontSize;
import com.printer.sdk.PrinterConstants.LablePaperType;
import com.printer.sdk.PrinterConstants.PAlign;
import com.printer.sdk.PrinterConstants.PBarcodeType;
import com.printer.sdk.PrinterConstants.PRotate;
import com.printer.sdk.PrinterInstance;

public class PrintLabel58 {

	private static  int MULTIPLE = 5;
	private static final int line_width_border = 2;
	private static final int page_width = 75 * MULTIPLE;//384����
	private static final int page_height = 90 * MULTIPLE;
	private static final int margin_horizontal = 2 * MULTIPLE;
	private static final int top_left_x = margin_horizontal;
	private static final int margin_vertical = 2 * MULTIPLE;
	private static final int top_left_y = margin_vertical;//32
	private static final int border_width = page_width - 2 * margin_horizontal;
	private static final int border_height = page_height - 2 * margin_vertical;
	private static final int top_right_x = top_left_x + border_width;
	private static final int bottom_left_y = top_left_y + border_height;
	private static final int bottom_right_y = bottom_left_y;
	private static final int bottom_right_x = top_right_x;
	private static final int row36_column1_width = 10 * MULTIPLE;
	private static final int row37_column3_width = 20 * MULTIPLE;
	private static final int row36_sep1_x = top_left_x + row36_column1_width;
	private static final int row37_sep2_x = top_right_x - row37_column3_width;
	private static final int[] row_height = { 8 * MULTIPLE * 2, 10 * MULTIPLE, 10 * MULTIPLE,10 * MULTIPLE,10 * MULTIPLE ,10 * MULTIPLE};
	private static final String TAG = "PrintLabel";

	public  void doPrint(PrinterInstance iPrinter){


		int start_x = 20,start_y = 20;
		int end_x = 75 * MULTIPLE,end_y = 75 * MULTIPLE;
		iPrinter.pageSetup(LablePaperType.Size_58mm,page_width, page_height);
		String mac = "00:02:5B:EC:B8:6E";
		iPrinter.drawQrCode(start_x, start_y,mac, PRotate.Rotate_0, 6, 2);
		iPrinter.drawText(start_x-10, 180, "扫一扫  连接我", LableFontSize.Size_24, PRotate.Rotate_0, 1, 0, 0);
		iPrinter.drawText(start_x, start_y, end_x, 15*MULTIPLE, PAlign.CENTER, PAlign.CENTER, "中国国家图书馆",
				LableFontSize.Size_32, 1, 0, 0, 0, PRotate.Rotate_0);

		iPrinter.drawBarCode(start_x, 70, end_x, 160, PAlign.CENTER, PAlign.START, 0, 0,
				"20160531110", PBarcodeType.CODE128, 1, 60, PRotate.Rotate_0);

		iPrinter.drawText(start_x, 140, end_x, 160, PAlign.CENTER, PAlign.CENTER, "20160531110",
				LableFontSize.Size_24, 1, 0, 0, 0, PRotate.Rotate_0);

		iPrinter.print(PRotate.Rotate_0, 0);

	}

}
