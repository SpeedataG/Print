//package com.printer.demo;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.RectF;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.printer.demo.global.GlobalContants;
//import com.printer.demo.utils.PrefUtils;
//import com.printer.sdk.PrinterConstants.PAlign;
//import com.printer.sdk.PrinterInstance;
//
//import com.iprt.xzc_pc.android_print_sdk.pdfdocument.CodecDocument;
//import com.iprt.xzc_pc.android_print_sdk.pdfdocument.CodecPage;
//
//import org.vudroid.pdfdroid.codec.PdfContext;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//
//public class                                                                                                                                                                                                                                                                                                                          PdfPrintActivity extends BaseActivity implements OnClickListener {
//	private Button btnPrintPdf;
//	private Button btnPrintthis;
//	private Button btnPrevious;
//	private Button btnNext;
//	private ImageView ivShowPdf;
//	private TextView tvShowPage;
//	private LinearLayout header;
//
//	private int pageCount = 0;
//	private PdfContext pdf_conext;
//	private CodecPage vuPage;
//	private RectF rf;
//	private Bitmap bitmap;
//	private float screen_width = 0;
//	private float screen_height = 0;
//	private CodecDocument d;
//	private String TAG = "com.printer.demo.ui";
//	private int paperWidth = 58;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_pdf_print);
//		btnPrintPdf = (Button) findViewById(R.id.btn_printpdf);
//		btnPrevious = (Button) findViewById(R.id.btn_pre);
//		btnNext = (Button) findViewById(R.id.btn_next);
//		btnPrintthis = (Button) findViewById(R.id.btn_printthis);
//		btnPrintPdf.setOnClickListener(this);
//		btnPrevious.setOnClickListener(this);
//		btnNext.setOnClickListener(this);
//		btnPrintthis.setOnClickListener(this);
//		ivShowPdf = (ImageView) findViewById(R.id.iv_showpdf);
//		tvShowPage = (TextView) findViewById(R.id.tv_showpage);
//		header = (LinearLayout) findViewById(R.id.ll_headerview_Pdf_Printactivity);
//		paperWidth = PrefUtils.getInt(PdfPrintActivity.this,
//				GlobalContants.PAPERWIDTH, 80);
//		DisplayMetrics dm = new DisplayMetrics();NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		switch (paperWidth) {
//		case 58:
//			screen_w                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   idth = 386;
//			screen_height = 500;
//			break;
//		case 80:
//			screen_width = 576;
//			screen_height = 820;
//			break;
//		case 100:
//			screen_width = 724;
//			screen_height = 1000;
//			break;
//
//		default:
//			break;
//		}
//		// screen_width = dm.widthPixels;宽度
//		// screen_height=height = dm.heightPixels ;//高度
//		// float rate = screen_height / screen_width;
//		// screen_width = 576;.
//		// screen_height = 576 * rate;
//
//		copyFilesFromassets(this, "test2.pdf", "/sdcard/Download/test3.pdf");
//		pdf_conext = new PdfContext();
//		d = pdf_conext.openDocument("/sdcard/Download/test3.pdf");
////		 d = pdf_conext.openDocument("file:///android_asset/test2.pdf");
//
//		// 默认显示第一页
//		showImage(pageCount);
//		tvShowPage.setText(pageCount + 1 + "/" + d.getPageCount());
//		initHeader();
//	}
//
//	/**
//	 * 从assets目录中复制整个文件夹内容
//	 *
//	 * @param context
//	 *            Context 使用CopyFiles类的Activity
//	 * @param oldPath
//	 *            String 原文件路径 如：/aa
//	 * @param newPath
//	 *            String 复制后路径 如：xx:/bb/cc
//	 */
//	private void copyFilesFromassets(Context context, String oldPath,
//			String newPath) {
//		try {
//			String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
//			if (fileNames.length > 0) {// 如果是目录
//				File file = new File(newPath);
//				file.mkdirs();// 如果文件夹不存在，则递归
//				for (String fileName : fileNames) {
//					copyFilesFromassets(context, oldPath + "/" + fileName,
//							newPath + "/" + fileName);
//				}
//			} else {// 如果是文件
//				InputStream is = context.getAssets().open(oldPath);
//				FileOutputStream fos = new FileOutputStream(new File(newPath));
//				byte[] buffer = new byte[1024];
//				int byteCount = 0;
//				while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
//																// buffer字节
//					fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
//				}
//				fos.flush();// 刷新缓冲区
//				is.close();
//				fos.close();
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			// 如果捕捉到错误则通知UI线程
//			// MainActivity.handler.sendEmptyMessage(COPY_FALSE);
//		}
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
////		if (GlobalContants.ISCONNECTED) {
////			if ("".equals(GlobalContants.DEVICENAME)
////					|| GlobalContants.DEVICENAME == null) {
////				headerConnecedState.setText(R.string.unknown_device);
////
////			} else {
////
////				headerConnecedState.setText(GlobalContants.DEVICENAME);
////			}
////
////		}
//
//	}
//
//	/**
//	 * 初始化标题上的信息
//	 */
//	private void initHeader() {
//		setHeaderLeftText(header, getString(R.string.back),
//				new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						finish();
//
//					}
//				});
//		setHeaderLeftImage(header, new OnClickListener() {// 初始化了
//					// headerConnecedState
//					@Override
//					public void onClick(View v) {
//						finish();
//					}
//				});
//		headerConnecedState.setText(getTitleState());
//		setHeaderCenterText(header, getString(R.string.headview_PdfPrint));
//	}
//
//	@Override
//	public void onClick(View v) {
//
//		switch (v.getId()) {
//		case R.id.btn_printpdf:
//			// // 打印整份pdf
//			if (PrinterInstance.mPrinter == null && !SettingActivity.isConnected) {
//				Toast.makeText(PdfPrintActivity.this, getString(R.string.no_connected),
//						Toast.LENGTH_SHORT).show();
//			} else {
//				Log.i("fdh", "pdf文件共有 " + d.getPageCount() + "页");
//				for (int i = 0; i < d.getPageCount(); i++) {
//					vuPage = d.getPage(i);
//					rf = new RectF();
//					rf.top = 0;
//					rf.left = 0;
//					rf.bottom = (float) 1.0;
//					rf.right = (float) 1.0;
//
//					// 参数一二为生成图片的宽高,参数三Rect的top,left,bottom,right为截取部分在该页中的百分比位置
//					// 例如top=0.5,left=0.5,right=1.0,1.0则截取右下角四分之一的部分
//					bitmap = vuPage.renderBitmap((int)screen_width,
//							(int)screen_height, rf);
//					// Log.i("fdh",
//					// "图片宽： " + bitmap.getWidth() + "图片的高： "
//					// + bitmap.getHeight());
//					// PrinterInstance.mPrinter.printImage(bitmap);
//					new InnerAsyncTask().execute();
//
//				}
//
//			}
//
//			break;
//		case R.id.btn_printthis:
//			// 打印当前页码的pdf
//			if (PrinterInstance.mPrinter == null && !SettingActivity.isConnected) {
//				Toast.makeText(PdfPrintActivity.this, getString(R.string.no_connected),
//						Toast.LENGTH_SHORT).show();
//			} else {
//				if (pageCount >= d.getPageCount()) {
//					pageCount = d.getPageCount() - 1;
//				}
//				if (pageCount <= 0) {
//					pageCount = 0;
//				}
//				vuPage = d.getPage(pageCount);
//				rf = new RectF();
//				rf.top = 0;
//				rf.left = 0;
//				rf.bottom = (float) 1.0;
//				rf.right = (float) 1.0;
//				Log.v("TAG:screen_width",""+screen_width);
//				Log.v("TAG:screen_height",""+screen_height);
//				bitmap = vuPage.renderBitmap((int)screen_width,
//						(int) screen_height, rf);
//				new InnerAsyncTask().execute();
//			}
//			break;
//		case R.id.btn_pre:
//			// 显示上一页pdf
//			pageCount--;
//			if (pageCount <= 0) {
//				pageCount = 0;
//				showImage(pageCount);
//				tvShowPage.setText("1/" + d.getPageCount());
//
//			} else {
//				showImage(pageCount);
//				tvShowPage.setText(pageCount + 1 + "/" + d.getPageCount());
//			}
//			break;
//		case R.id.btn_next:
//			// 显示下页pdf
//			pageCount++;
//			Log.e(TAG, "当前的count值" + pageCount);
//			if (pageCount >= d.getPageCount()) {
//				Log.e(TAG, "count（" + pageCount + "）值大于或者等于" + d.getPageCount());
//
//				pageCount = d.getPageCount();
//				showImage(pageCount - 1);
//				tvShowPage.setText(d.getPageCount() + "/" + d.getPageCount());
//			} else {
//				showImage(pageCount);
//				tvShowPage.setText(pageCount + 1 + "/" + d.getPageCount());
//
//			}
//			break;
//		}
//
//	}
//
//	private void showImage(int count) {
//		RectF rf = new RectF();
//		rf.top = 0;
//		rf.left = 0;
//		rf.bottom = (float) 1.0;
//		rf.right = (float) 1.0;
//		pdf_conext = new PdfContext();
//		vuPage = d.getPage(count);
//		Bitmap bitmap = vuPage.renderBitmap((int) 576, (int) 820, rf);
//		ivShowPdf.setImageBitmap(bitmap);
//	}
//
//	private class InnerAsyncTask extends AsyncTask<Void, Void, Void> {
//
//		@Override
//		protected Void doInBackground(Void... params) {
//			Log.i(TAG, "宽："+bitmap.getWidth()+" 高："+bitmap.getHeight());
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					PrinterInstance.mPrinter.printImage(bitmap,PAlign.NONE, 0,false);
//				}
//			}).start();
//			return null;
//		}
//
//	}
//}
