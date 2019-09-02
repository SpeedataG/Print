package com.spd.jxprint.utils;

import android.content.res.Resources;

import com.printer.sdk.PrinterConstants.Command;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.Table;
import com.spd.jxprint.R;
import com.spd.print.jx.impl.PrintImpl;

public class XTUtils {

    /**
     * 打印小票示例
     *
     * @param resources -
     * @param mPrinter  -
     */
    public static void printNote(Resources resources, PrintImpl mPrinter) {
        mPrinter.initPrinter();

        mPrinter.setFont(0, 0, 0, 0, 0);
        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
        mPrinter.printText(resources.getString(R.string.str_note));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
        mPrinter.setFont(0, 1, 1, 0, 0);
        mPrinter.printText(resources.getString(R.string.shop_company_title) + "\n");

        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
        // 字号使用默认
        mPrinter.setFont(0, 0, 0, 0, 0);
        // 打印
        mPrinter.printText(getText(resources));
        // 打印表格
        mPrinter.printTable(getTable(resources));

        mPrinter.printText(getText2(resources));

        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
        mPrinter.setFont(0, 0, 1, 0, 0);
        mPrinter.printText(resources.getString(R.string.shop_thanks) + "\n");
        mPrinter.printText(resources.getString(R.string.shop_demo) + "\n\n\n");

        mPrinter.setFont(0, 0, 0, 0, 0);
        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
    }

    public static String getText(Resources resources) {
        StringBuilder sb = new StringBuilder();
        sb.append(resources.getString(R.string.shop_num)).append("574001\n");
        sb.append(resources.getString(R.string.shop_receipt_num)).append("S00003169\n");
        sb.append(resources.getString(R.string.shop_cashier_num)).append("s004_s004\n");
        sb.append(resources.getString(R.string.shop_receipt_date)).append("2012-06-17\n");
        sb.append(resources.getString(R.string.shop_print_time)).append("2012-06-17 13:37:24\n");
        return sb.toString();
    }

    public static String getText2(Resources resources) {
        StringBuffer sb = new StringBuffer();
        sb.append(resources.getString(R.string.shop_goods_number)).append("                6.00\n");
        sb.append(resources.getString(R.string.shop_goods_total_price)).append("                35.00\n");
        sb.append(resources.getString(R.string.shop_payment)).append("                100.00\n");
        sb.append(resources.getString(R.string.shop_change)).append("                65.00\n");
        sb.append(resources.getString(R.string.shop_company_name)).append("\n");
        sb.append(resources.getString(R.string.shop_company_site)).append("www.jiangsuxxxx.com\n");
        sb.append(resources.getString(R.string.shop_company_address)).append("\n");
        sb.append(resources.getString(R.string.shop_company_tel)).append("0574-12345678\n");
        sb.append(resources.getString(R.string.shop_Service_Line)).append("4008-123-456 \n");
        sb.append("==============================\n");
        return sb.toString();
    }

    public static Table getTable(Resources resources) {
        String column = resources.getString(R.string.note_title);
        Table table = new Table(column, ";", new int[]{14, 6, 6, 6});
        table.addRow("" + resources.getString(R.string.bags) + ";10.00;1;10.00");
        table.addRow("" + resources.getString(R.string.hook) + ";5.00;2;10.00");
        table.addRow("" + resources.getString(R.string.umbrella) + ";5.00;3;15.00");
        return table;
    }

    /**
     * 全黑打印
     *
     * @param mPrinter -
     */
    public static void printBlackTest(PrintImpl mPrinter) {
        mPrinter.initPrinter();
        mPrinter.printText("███████████████████████████████████████████████████████████████████████████████████████████");
        mPrinter.printText("███████████████████████████████████████████████████████████████████████████████████████████");
        mPrinter.printText("███████████████████████████████████████████████████████████████████████████████████████████");
        mPrinter.printText("███████████████████████████████████████████████████████████████████████████████████████████");
        mPrinter.printText("███████████████████████████████████████████████████████████████████████████████████████████\n");
        mPrinter.initPrinter();
    }

    public static void printTest(Resources resources, PrinterInstance mPrinter) {

        mPrinter.initPrinter();

        mPrinter.printText(resources.getString(R.string.str_text));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);


        mPrinter.setFont(0, 0, 0, 0, 0);
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
        mPrinter.setFont(0, 0, 0, 1, 0);
        mPrinter.printText(resources.getString(R.string.str_text_strong));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2); // ��2��

        mPrinter.printText("███████████████████████████████████████████████████████████████████████████████████████████");
        mPrinter.printText(resources.getString(R.string.str_text_print));

        mPrinter.setFont(0, 0, 0, 0, 1);
        mPrinter.sendBytesData(new byte[]{(byte) 0x1C, (byte) 0x21, (byte) 0x80});
        mPrinter.printText(resources.getString(R.string.str_text_underline));
        mPrinter.sendBytesData(new byte[]{(byte) 0x1C, (byte) 0x21, (byte) 0x00});
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2); // ��2��

        mPrinter.setFont(0, 0, 0, 0, 0);
        mPrinter.printText(resources.getString(R.string.str_text_height));

        for (int i = 0; i < 4; i++) {
            mPrinter.setFont(0, i, i, 0, 0);
            mPrinter.printText((i + 1) + resources.getString(R.string.times));
        }
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

        for (int i = 0; i < 4; i++) {


            mPrinter.setFont(0, i, i, 0, 0);
            mPrinter.printText(resources.getString(R.string.bigger) + (i + 1) + resources.getString(R.string.bigger1));
            mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
            mPrinter.printText("███████████████████████████████████████████████████████████████████████████████████████████");
            mPrinter.printText(resources.getString(R.string.str_text_print));
        }

        mPrinter.setFont(0, 0, 0, 0, 0);
        mPrinter.setPrinter(Command.ALIGN, 0);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
    }

    public static void setAdjusting(PrinterInstance mPrinter) {
        byte[] bytes = new byte[]{0x0C};
        mPrinter.sendBytesData(bytes);
    }

    public static void printLabelTest(Resources resources, PrinterInstance mPrinter) {
        mPrinter.initPrinter();
        byte[] backBytes = new byte[]{0x1B, 0x4B, (byte) (10 * 8), 0x1B, 0x4A, (byte) 8};
        mPrinter.sendBytesData(backBytes);
        mPrinter.initPrinter();
        mPrinter.printText(resources.getString(R.string.str_text));
        mPrinter.setFont(0, 0, 0, 0, 0);
        mPrinter.setPrinter(Command.ALIGN, 0);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        setAdjusting(mPrinter);
    }

    public static void printNormalTest(Resources resources, PrinterInstance mPrinter) {
        mPrinter.initPrinter();
        mPrinter.printText(resources.getString(R.string.str_text));
        mPrinter.setFont(0, 0, 0, 0, 0);
        mPrinter.setPrinter(Command.ALIGN, 0);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
    }

}
