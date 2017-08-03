package com.example.chenming.imageresizedemo;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "ImageResizeDemo";
    private static final int MSG_IMG_UPDATE = 1;
    private static final int DEFAULT_IMAGE_SOURCE = R.drawable.lenna;
    private ImageView imgShow;
    private TextView tResolusion;
    private Spinner sprScale;
    private RadioGroup radioGroup;
    private RadioButton rbtnNNI;
    private RadioButton rbtnBLI;
    private RadioButton rbtnBCI;
    private Button btnRun;
    private Bitmap bmpSrc = null;
    private Bitmap bmpBuffer = null;
    private int scalePercent = 100;
    private Resizer resizer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        imgShow = (ImageView) findViewById(R.id.imageView_show);
        tResolusion = (TextView) findViewById(R.id.textView_resolution);
        sprScale = (Spinner) findViewById(R.id.spinner_scale);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        rbtnNNI = (RadioButton) findViewById(R.id.radioButton_NNI);
        rbtnBLI = (RadioButton) findViewById(R.id.radioButton_bilinear);
        rbtnBCI = (RadioButton) findViewById(R.id.radioButton_bicubic);
        btnRun = (Button) findViewById(R.id.button_run);

        /* Initialize Image & Text */
        bmpSrc = BitmapFactory.decodeResource(getResources(), DEFAULT_IMAGE_SOURCE);
        bmpBuffer = bmpSrc;
        resetImageShow();

        /* Initialize Spinner */
        scalePercent = 100;

        final ArrayAdapter<CharSequence> scaleAdapter = ArrayAdapter.createFromResource(
                this, R.array.scale_percent, android.R.layout.simple_spinner_dropdown_item);
        sprScale.setAdapter(scaleAdapter);
        sprScale.setSelection(2);
        sprScale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CharSequence value = scaleAdapter.getItem(position);
                scalePercent = Integer.valueOf(value.subSequence(0, value.length() - 1).toString());
                Log.d(TAG, "Selected " + scalePercent + "%");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /* Initialize RadioGroup, RadioButton */
        resizer = new NNInterpolationResizer();

        rbtnNNI.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_NNI:
                        resizer = new NNInterpolationResizer();
                        Log.d(TAG, "Selected radioButton_NNI");
                        break;
                    case R.id.radioButton_bilinear:
                        resizer = new BilinearInterpolationResizer();
                        Log.d(TAG, "Selected radioButton_bilinear");
                        break;
                    case R.id.radioButton_bicubic:
                        resizer = new BicubicInterpolationResizer();
                        Log.d(TAG, "Selected radioButton_bicubic");
                        break;
                }
            }
        });

        /* Initialize Run Button */
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pDlg = new ProgressDialog(MainActivity.this);
                pDlg.setTitle(R.string.dlg_processing_title);
                pDlg.setMessage(getString(R.string.dlg_processing_message));
                pDlg.setIndeterminate(true);
                pDlg.setCancelable(false);
                pDlg.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(resizer != null)
                                bmpBuffer = resizer.doScale(bmpBuffer, scalePercent);

                            // Update ImageView
                            Message m = new Message();
                            m.what = MSG_IMG_UPDATE;
                            mHandler.sendMessage(m);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if(pDlg.isShowing())
                                pDlg.dismiss();
                        }
                    }
                }).start();
            }
        });
    }

    private void resetImageShow() {
        imgShow.setImageBitmap(bmpSrc);
        tResolusion.setText(getString(R.string.text_resolution, bmpSrc.getWidth(), bmpSrc.getHeight()));
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_IMG_UPDATE:
                    if(bmpBuffer != null) {
                        imgShow.setImageBitmap(bmpBuffer);
                        tResolusion.setText(getString(R.string.text_resolution, bmpBuffer.getWidth(), bmpBuffer.getHeight()));
                        Toast.makeText(getApplicationContext(), R.string.toast_process_ok, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.toast_process_fail, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
}
