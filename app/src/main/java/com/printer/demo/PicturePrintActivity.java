package com.printer.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUriExposedException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.printer.demo.utils.CanvasUtils;
import com.printer.demo.utils.PictureUtils;
import com.printer.sdk.PrinterConstants.Command;
import com.printer.sdk.PrinterConstants.PAlign;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.monochrome.BitmapConvertor;

/**
 * @Description 处理图片打印相关
 */
public class PicturePrintActivity extends BaseActivity implements
        OnClickListener, OnItemSelectedListener {
    private Context mContext;
    private boolean is58mm = true;
    private boolean isStylus = false;
    private Button btn_photo_print, btn_select_photo, btn_canvas_print, btn_monochrome_print;
    private ImageView iv_Original_picture, iv_monochrome_picture;
    private LinearLayout header;
    private final static int IMAGE_CAPTURE_FROM_CAMERA = 1;
    private final static int IMAGE_CAPTURE_FROM_GALLERY = 2;
    private static final String TAG = "PicturePrintActivity";
    private BitmapConvertor convertor;
    private WindowManager wm;
    private ProgressDialog mPd;
    private String remp_dir = null;
    private static PrinterInstance mPrinter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_print_picture);
        init();
        Log.e(TAG, "onCreate");
        mPrinter = PrinterInstance.mPrinter;
        remp_dir = getFilePath();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    private void init() {
        header = (LinearLayout) findViewById(R.id.ll_header_picture);
        btn_photo_print = (Button) findViewById(R.id.btn_photo_print);
        btn_photo_print.setOnClickListener(this);
        btn_monochrome_print = (Button) findViewById(R.id.btn_monochrome_print);
        btn_monochrome_print.setOnClickListener(this);
        btn_select_photo = (Button) findViewById(R.id.btn_select_photo);
        btn_select_photo.setOnClickListener(this);
        btn_canvas_print = (Button) findViewById(R.id.btn_canvas_print);
        btn_canvas_print.setOnClickListener(this);
        // iv_Original_picture = (ImageView)
        // findViewById(R.id.iv_Original_picture);
        iv_monochrome_picture = (ImageView) findViewById(R.id.iv_monochrome_picture);
        initHeader(header);
        convertor = new BitmapConvertor(this);
        wm = this.getWindowManager();
    }

    /**
     * 初始化标题上的信息
     */
    private void initHeader(LinearLayout header) {
        setHeaderLeftText(header, getString(R.string.back),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();

                    }
                });
        headerConnecedState.setText(getTitleState());
        setHeaderLeftImage(header, new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderCenterText(header, getString(R.string.headview_PicturePrint));

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }



    @Override
    public void onClick(View view) {
        if (mPrinter == null || !SettingActivity.isConnected) {
            Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        if (view == btn_monochrome_print) {
            progressDialog = ProgressDialog.show(this,"请等待","正在打印图片");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    btn_monochrome_print.setEnabled(false);
                    btn_photo_print.setEnabled(false);
                    btn_select_photo.setEnabled(false);
                    btn_canvas_print.setEnabled(false);
                    mPrinter.initPrinter();
                    mPrinter.printText("\n");
                    mPrinter.printText("打印单色位图演示：\n");
                    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
                    BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
                    bfoOptions.inScaled = false;
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shilitu2, bfoOptions);
                    Matrix matrix = new Matrix();
                    //matrix.setScale(X轴缩放,Y轴缩放，，);//后面两个参数是相对于缩放的位置放置，尝试设置，建议数值>100以上进行设置
                    matrix.setScale(1f, 1f);
                    Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),
                            R.drawable.shoujian);
//                    mPrinter.printImage(resizeBmp, PAlign.NONE, 0, false);
                    mPrinter.printText("打印单色位图演示\n");
                    PictureUtils.printBitmapImage(mPrinter, resizeBmp, PAlign.NONE, 0, false);
                    mPrinter.initPrinter();
                    mPrinter.printText("\n");
                    mPrinter.printText("注意，这是一张图\n");
                    mPrinter.printText("\n");
                    btn_monochrome_print.setEnabled(true);
                    btn_photo_print.setEnabled(true);
                    btn_select_photo.setEnabled(true);
                    btn_canvas_print.setEnabled(true);
                    progressDialog.dismiss();
                }
            }).start();
        } else if (view == btn_photo_print) {
            /*try{
                remp_dir = getFilePath();
                // 拍照我们用Action为MediaStroe.ACTION_IMAGE_CAPTURE,
                // 有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(remp_dir)));
                startActivityForResult(intent, IMAGE_CAPTURE_FROM_CAMERA);
            }catch (FileUriExposedException e){
                e.printStackTrace();
            }*/

        } else if (view == btn_select_photo) {
            // 拍照我们用Action为Intent.ACTION_GET_CONTENT,
            // 有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
            Intent intent2 = new Intent();
            intent2.setType("image/*");
            intent2.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent2, IMAGE_CAPTURE_FROM_GALLERY);
        } else if (view == btn_canvas_print) {
            if (mPrinter == null) {
                Log.i(TAG, "mPrinter为空");
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        btn_monochrome_print.setEnabled(false);
                        btn_photo_print.setEnabled(false);
                        btn_select_photo.setEnabled(false);
                        btn_canvas_print.setEnabled(false);
                        new CanvasUtils().printCustomImage2(mContext.getResources(),
                                PrinterInstance.mPrinter, isStylus, is58mm);
                        btn_monochrome_print.setEnabled(true);
                        btn_photo_print.setEnabled(true);
                        btn_select_photo.setEnabled(true);
                        btn_canvas_print.setEnabled(true);
                    }
                }).start();
            }
        }
    }

    private String getFilePath() {

        String PATH_LOGCAT = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            PATH_LOGCAT = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "MyPicture";
            Log.i(TAG, "sdka ");
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            PATH_LOGCAT = PicturePrintActivity.this.getFilesDir()
                    .getAbsolutePath() + File.separator + "MyPicture";
            Log.i(TAG, "neicun");
        }
        File dir = new File(PATH_LOGCAT);
        if (!dir.exists()) {
            dir.mkdir(); // 创建文件夹
        }
        remp_dir = PATH_LOGCAT + File.separator + "tmpPhoto.jpg";
        Log.i(TAG, "remp_dir:" + remp_dir);
        return remp_dir;
    }

    Bitmap monoChromeBitmap = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Log.i(TAG, "windows height:" + height + "----" + "windows width:"
                + width);

        switch (requestCode) {
            case IMAGE_CAPTURE_FROM_CAMERA:
                if (remp_dir == null) {
                    Log.e(TAG, "remp_dir为空！");
                } else {
                    Log.i(TAG, "remp_dir" + remp_dir);
                }
                File f = new File(remp_dir);
                Uri capturedImage = null;
                Bitmap photoBitmap = null;
                try {
                    capturedImage = Uri
                            .parse(android.provider.MediaStore.Images.Media
                                    .insertImage(getContentResolver(),
                                            f.getAbsolutePath(), null, null));
                    Log.i(TAG, capturedImage.toString());
                    photoBitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), capturedImage);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("camera", "Selected image: " + capturedImage.toString());
                f.delete();
                Log.i(TAG, "height1:" + photoBitmap.getHeight() + "----"
                        + "width1:" + photoBitmap.getWidth());
                new ConvertInBackground().execute(photoBitmap);
                break;
            case IMAGE_CAPTURE_FROM_GALLERY:
                // 这个方法是根据Uri获取Bitmap图片的静态方法
                Uri mImageCaptureUri = data.getData();
                try {
                    // 选择相册
                    Bitmap photoBitmap2 = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), mImageCaptureUri);
                    new ConvertInBackground().execute(photoBitmap2);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 其实这里什么都不要做
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("remp_dir", remp_dir);
        Log.e(TAG, "onSaveInstanceState");

    }

    class ConvertInBackground extends AsyncTask<Bitmap, String, Void> {

        @Override
        protected Void doInBackground(Bitmap... params) {
            Bitmap compress = PictureUtils.compress(params[0]);
            Log.i(TAG, "heightC:" + compress.getHeight() + "----"
                    + "widthC:" + compress.getWidth());
            monoChromeBitmap = convertor.convertBitmap(compress);
            if (monoChromeBitmap == null) {
                Log.i(TAG, "monoChromeBitmap为空!");
                return null;
            }
            Log.i(TAG, "heightD:" + monoChromeBitmap.getHeight() + "----"
                    + "widthD:" + monoChromeBitmap.getWidth());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            iv_monochrome_picture.setImageBitmap(monoChromeBitmap);
            mPd.dismiss();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    if (PrinterInstance.mPrinter == null) {
                        Toast.makeText(mContext, getString(R.string.not_support), Toast.LENGTH_LONG).show();
                    } else {
                        mPrinter.printImage(monoChromeBitmap, PAlign.NONE, 0, false);
                    }
                }
            }).start();

        }

        @Override
        protected void onPreExecute() {
            mPd = ProgressDialog.show(PicturePrintActivity.this,
                    "Converting Image", "Please Wait", true, false, null);
        }
    }

}
