package com.spd.print.jx.textprint.presenter;

import com.printer.sdk.PrinterConstants;
import com.spd.lib.mvp.BasePresenter;
import com.spd.print.jx.R;
import com.spd.print.jx.application.BaseApp;
import com.spd.print.jx.textprint.PrintTextActivity;
import com.spd.print.jx.textprint.contract.PrintTextContract;
import com.spd.print.jx.textprint.model.PrintTextModel;
import com.spd.print.jx.utils.StringUtils;
import com.spd.print.jx.utils.XTUtils;


/**
 * @author zzc
 */
public class PrintTextPresenter extends BasePresenter<PrintTextActivity, PrintTextModel> implements PrintTextContract.Presenter {
    @Override
    protected PrintTextModel createModel() {
        return new PrintTextModel();
    }

    public void printNote() {
        XTUtils.printNote(getView().getResources(), BaseApp.getPrinterImpl());
    }

    public void printTable() {
        BaseApp.getPrinterImpl().printTable(XTUtils.getTable(getView().getResources()));
    }

    public void sendPrintData(String data, boolean isHex) {
        if (!data.isEmpty()) {
            if (isHex) {
                BaseApp.getPrinterImpl().sendBytesData(StringUtils.string2bytes(data));
            } else {
                BaseApp.getPrinterImpl().printText(data + "\r\n");
            }
        }
    }

    public void printCodePages(int cTypeId, String cType) {
        byte[] realData = new byte[]{
                (byte) 0x80, (byte) 0x81, (byte) 0x82, (byte) 0x83,
                (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87,
                (byte) 0x88, (byte) 0x89, (byte) 0x8A, (byte) 0x8B,
                (byte) 0x8C, (byte) 0x8D, (byte) 0x8E, (byte) 0x8F,

                (byte) 0x90, (byte) 0x91, (byte) 0x92, (byte) 0x93,
                (byte) 0x94, (byte) 0x95, (byte) 0x96, (byte) 0x97,
                (byte) 0x98, (byte) 0x99, (byte) 0x9A, (byte) 0x9B,
                (byte) 0x9C, (byte) 0x9D, (byte) 0x9E, (byte) 0x9F,

                (byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3,
                (byte) 0xA4, (byte) 0xA5, (byte) 0xA6, (byte) 0xA7,
                (byte) 0xA8, (byte) 0xA9, (byte) 0xAA, (byte) 0xAB,
                (byte) 0xAC, (byte) 0xAD, (byte) 0xAE, (byte) 0xAF,

                (byte) 0xB0, (byte) 0xB1, (byte) 0xB2, (byte) 0xB3,
                (byte) 0xB4, (byte) 0xB5, (byte) 0xB6, (byte) 0xB7,
                (byte) 0xB8, (byte) 0xB9, (byte) 0xBA, (byte) 0xBB,
                (byte) 0xBC, (byte) 0xBD, (byte) 0xBE, (byte) 0xBF,

                (byte) 0xC0, (byte) 0xC1, (byte) 0xC2, (byte) 0xC3,
                (byte) 0xC4, (byte) 0xC5, (byte) 0xC6, (byte) 0xC7,
                (byte) 0xC8, (byte) 0xC9, (byte) 0xCA, (byte) 0xCB,
                (byte) 0xCC, (byte) 0xCD, (byte) 0xCE, (byte) 0xCF,

                (byte) 0xD0, (byte) 0xD1, (byte) 0xD2, (byte) 0xD3,
                (byte) 0xD4, (byte) 0xD5, (byte) 0xD6, (byte) 0xD7,
                (byte) 0xD8, (byte) 0xD9, (byte) 0xDA, (byte) 0xDB,
                (byte) 0xDC, (byte) 0xDD, (byte) 0xDE, (byte) 0xDF,

                (byte) 0xE0, (byte) 0xE1, (byte) 0xE2, (byte) 0xE3,
                (byte) 0xE4, (byte) 0xE5, (byte) 0xE6, (byte) 0xE7,
                (byte) 0xE8, (byte) 0xE9, (byte) 0xEA, (byte) 0xEB,
                (byte) 0xEC, (byte) 0xED, (byte) 0xEE, (byte) 0xEF,

                (byte) 0xF0, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3,
                (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7,
                (byte) 0xF8, (byte) 0xF9, (byte) 0xFA, (byte) 0xFB,
                (byte) 0xFC, (byte) 0xFD, (byte) 0xFE, (byte) 0xFF,
        };

        if (cTypeId != 47) {

            BaseApp.getPrinterImpl().printText(getView().getResources().getString(R.string.print) + cType + getView().getResources().getString(R.string.str_show));
            BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

            BaseApp.getPrinterImpl().sendBytesData(new byte[]{(byte) 0x1c, (byte) 0x2E});

            BaseApp.getPrinterImpl().sendBytesData(new byte[]{(byte) 0x1B, (byte) 0x74, (byte) cTypeId});
            BaseApp.getPrinterImpl().sendBytesData(realData);
            BaseApp.getPrinterImpl().sendBytesData(new byte[]{(byte) 0x0A});

            BaseApp.getPrinterImpl().sendBytesData(new byte[]{(byte) 0x1c, (byte) 0x26});
            BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

        } else {
            for (cTypeId = 0; cTypeId < 46; cTypeId++) {
                if (cTypeId > 14 || cTypeId < 11) {

                    BaseApp.getPrinterImpl().printText(getView().getResources().getString(R.string.print) + cType + getView().getResources().getString(R.string.str_show));
                    BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

                    BaseApp.getPrinterImpl().sendBytesData(new byte[]{(byte) 0x1c, (byte) 0x2E});

                    BaseApp.getPrinterImpl().sendBytesData(new byte[]{(byte) 0x1B, (byte) 0x74, (byte) cTypeId});
                    BaseApp.getPrinterImpl().sendBytesData(realData);
                    BaseApp.getPrinterImpl().sendBytesData(new byte[]{(byte) 0x0A});

                    BaseApp.getPrinterImpl().sendBytesData(new byte[]{(byte) 0x1c, (byte) 0x26});
                    BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
                }
            }
        }
    }
}
