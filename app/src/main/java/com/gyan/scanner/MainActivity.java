package com.gyan.scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    Button btnFlash;
    private static final int CAMERA_REQUEST = 123;
    boolean hasCameraFlash = false;
    boolean isPressed;
    Toolbar toolbar;
    private static final String TAG = "ScanQRActivity";
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide status bar or notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //toolbar set
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //scanner implement
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        contentFrame.addView(mScannerView);

        //flash On/Off
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        btnFlash = findViewById(R.id.btnflash);
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasCameraFlash) {
                    if (isPressed) {
                        btnFlash.setBackgroundResource(R.drawable.ic_baseline_flash_off_24);
                        //flashLightOff();
                        mScannerView.setFlash(false);
                    } else {
                        btnFlash.setBackgroundResource(R.drawable.ic_baseline_flash_on_24);
                        // flashLightOn();
                        mScannerView.setFlash(true);
                    }
                    isPressed = !isPressed;
                } else {
                    Toast.makeText(MainActivity.this, "No flash available on your device", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Scanner code
    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }
    @Override
    public void handleResult(Result result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), ResultActivity.class);
                i.putExtra("QrResult",result.getText());
                startActivity(i);
                mScannerView.setFlash(false);
                btnFlash.setBackgroundResource(R.drawable.ic_baseline_flash_off_24);
                isPressed = !isPressed;
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mScannerView.setFlash(false);
        btnFlash.setBackgroundResource(R.drawable.ic_baseline_flash_off_24);
        isPressed = !isPressed;
    }

    //Permission Camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasCameraFlash = getPackageManager().
                            hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                } else {
                    btnFlash.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}