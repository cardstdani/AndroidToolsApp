package com.unity.androidtools;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.BuildCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    TextView deviceNameText, deviceModelText, deviceUserText, deviceAndroidVersion, deviceOS, screenResText, screenPixelsText, pixelDensityText;
    TextView deviceCPUText, deviceCPUCoresText, deviceCPUFrequencyText, deviceGPUText, kernelVersionText, kernelNameText, kernelArchText, deviceIDText;

    TextView deviceGyroText, deviceBluethootText, deviceNFC, deviceOTG, deviceStepCountertext, deviceHeartRateText, deviceAcelerometerText, deviceFingerprintText, deviceAudioProText, deviceFaceText, deviceARText, deviceFlashText, deviceIrisText, deviceCompassText, deviceEthernetText, device5GHZText, deviceStepDetectorText;

    Button exitButton;
    ScrollView scrollViewInfo;
    Switch autoRotationSwitch, autoBrightness;
    SeekBar brightnessBar;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Settings.System.canWrite(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
        }

        exitButton = findViewById(R.id.exitButton);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });

        autoRotationSwitch = findViewById(R.id.switchAutoRotationScreen);

        checkAutoRot();
        autoRotationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (autoRotationSwitch.isChecked()) {
                    setAutoOrientationEnabled(getApplicationContext(), true);
                } else {
                    setAutoOrientationEnabled(getApplicationContext(), false);
                }
            }
        });

        autoBrightness = findViewById(R.id.switchAutoBrightness);

        checkBright();
        autoBrightness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (autoBrightness.isChecked()) {
                    setAutoBrightness(true);
                } else {
                    setAutoBrightness(false);
                }
            }
        });

        brightnessBar = findViewById(R.id.brightnessBar);

        final int brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        brightnessBar.setProgress(brightness);

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (Settings.System.canWrite(getApplicationContext())) {
                    int brightness2 = progress * 1000 / 1000;
                    //Settings.System.putInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    Settings.System.putInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness2);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        scrollViewInfo = findViewById(R.id.scrollViewInformation);

        deviceNameText = findViewById(R.id.labelDeviceName);
        deviceModelText = findViewById(R.id.labelDeviceModel);
        deviceUserText = findViewById(R.id.labelDeviceUser);
        deviceAndroidVersion = findViewById(R.id.labelDeviceVersion);
        deviceOS = findViewById(R.id.labelDeviceOS);

        deviceCPUText = findViewById(R.id.labelDeviceCPU);
        deviceCPUCoresText = findViewById(R.id.labelDeviceCPUCores);
        deviceCPUFrequencyText = findViewById(R.id.labelDeviceCPUFrequency);

        deviceGPUText = findViewById(R.id.labelDeviceGPU);

        deviceFaceText = findViewById(R.id.labelDeviceFace);
        deviceGyroText = findViewById(R.id.labelDeviceGyro);
        deviceAcelerometerText = findViewById(R.id.labelDeviceAcelerometer);
        deviceFingerprintText = findViewById(R.id.labelDeviceFingerprint);
        deviceAudioProText = findViewById(R.id.labelDeviceAudioPro);
        deviceARText = findViewById(R.id.labelDeviceCameraAR);
        deviceFlashText = findViewById(R.id.labelDeviceFlash);
        deviceIrisText = findViewById(R.id.labelDeviceIris);
        deviceCompassText = findViewById(R.id.labelDeviceCompass);
        deviceEthernetText = findViewById(R.id.labelDeviceEthernet);
        device5GHZText = findViewById(R.id.labelDevice5GHZ);
        deviceStepDetectorText = findViewById(R.id.labelDeviceStepDetector);
        kernelArchText = findViewById(R.id.labelKernelArch);
        kernelNameText = findViewById(R.id.labelKernelName);
        kernelVersionText = findViewById(R.id.labelKernelVersion);
        deviceStepCountertext = findViewById(R.id.labelDeviceStepCounter);
        deviceHeartRateText = findViewById(R.id.labelDeviceHeartRateSensor);
        screenResText = findViewById(R.id.labelScreenres);
        screenPixelsText = findViewById(R.id.labelScreenPixels);
        pixelDensityText = findViewById(R.id.labelPixelDensity);
        deviceBluethootText = findViewById(R.id.labelDeviceBluethoot);
        deviceNFC = findViewById(R.id.labelDeviceNFC);
        deviceOTG = findViewById(R.id.labelDeviceOTG);
        deviceIDText = findViewById(R.id.labelDeviceID);

        deviceNameText.setText("Device brand: " + Build.MANUFACTURER);
        deviceModelText.setText("Android version name: " + androidVersionName());
        deviceIDText.setText("Device name: " + BluetoothAdapter.getDefaultAdapter().getName());
        deviceUserText.setText("Model: " + Build.PRODUCT);
        deviceAndroidVersion.setText("Android version/SDK: " + Build.VERSION.SDK);
        deviceOS.setText("Device OS: " + Build.VERSION.RELEASE);

        deviceCPUText.setText("CPU: " + getCpuName());
        deviceCPUCoresText.setText("CPU cores: " + getNumberOfCores());
        deviceCPUFrequencyText.setText("CPU abi: " + Build.CPU_ABI);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        screenResText.setText("Screen resolution: " + width + "x" + height);
        screenPixelsText.setText("Screen pixels: " + (width * height));
        DisplayMetrics dm = getResources().getDisplayMetrics();
        pixelDensityText.setText("Pixel density: " + dm.densityDpi + " Dpi");

        kernelVersionText.setText("Kernel version: " + java.lang.System.getProperty("os.version"));
        kernelArchText.setText("Kernel architecture: " + java.lang.System.getProperty("os.arch"));
        kernelNameText.setText("Kernel name: " + java.lang.System.getProperty("os.name"));

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }

        //DEVICE FEATURES
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE)) {
            deviceGyroText.setText("Support gyroscope");
        } else {
            deviceGyroText.setText("No gyroscope avaliable");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            deviceAcelerometerText.setText("Support accelerometer");
        } else {
            deviceAcelerometerText.setText("No accelerometer avaliable");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            deviceFingerprintText.setText("Support fingerprint");
        } else {
            deviceFingerprintText.setText("No fingerprint avaliable");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_PRO)) {
            deviceAudioProText.setText("Support professional audio");
        } else {
            deviceAudioProText.setText("No professional audio supported");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AR)) {
            deviceARText.setText("Motion tracking supported");
        } else {
            deviceARText.setText("Motion tracking not avaliable");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            deviceFlashText.setText("Flashlight avaliable");
        } else {
            deviceFlashText.setText("Flashlight not supported");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_IRIS)) {
            deviceIrisText.setText("Iris recognition avaliable");
        } else {
            deviceIrisText.setText("Iris recognition not supported");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)) {
            deviceCompassText.setText("Compass avaliable");
        } else {
            deviceCompassText.setText("Compass not avaliable");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_ETHERNET)) {
            deviceEthernetText.setText("Ethernet supported");
        } else {
            deviceEthernetText.setText("Ethernet not supported");
        }

        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.is5GHzBandSupported()) {
            device5GHZText.setText("5GHZ band supported");
        } else {
            device5GHZText.setText("5GHZ band not supported");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)) {
            deviceStepDetectorText.setText("Step detector avaliable");
        } else {
            deviceStepDetectorText.setText("Step detector not avaliable");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            deviceStepCountertext.setText("Step counter avaliable");
        } else {
            deviceStepCountertext.setText("Step counter not avaliable");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_HEART_RATE)) {
            deviceHeartRateText.setText("Heart rate sensor available");
        } else {
            deviceHeartRateText.setText("Heart rate sensor not available");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_FACE)) {
            deviceFaceText.setText("Face recognition available");
        } else {
            deviceFaceText.setText("Face recognition not available");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            deviceBluethootText.setText("Bluethoot available");
        } else {
            deviceBluethootText.setText("Bluethoot not available");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            deviceNFC.setText("NFC available");
        } else {
            deviceNFC.setText("NFC not available");
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST)) {
            deviceOTG.setText("OTG is supported");
        } else {
            deviceOTG.setText("OTG is not supported");
        }

        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(1000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                checkBright();
                                checkAutoRot();
                                brightnessBar.setProgress(Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0));
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    void checkBright() {
        if (getBrightMode() == 1) {
            autoBrightness.setChecked(true);
        } else {
            autoBrightness.setChecked(false);
        }
    }

    void setAutoBrightness(boolean value) {
        if (value) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        } else {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
    }

    int getBrightMode() {
        try {
            int brightnessmode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            return brightnessmode;
        } catch (Exception e) {
            return -1;
        }
    }

    public static void setAutoOrientationEnabled(Context context, boolean enabled) {
        try {
            if (Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 1) {
                Display defaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                Settings.System.putInt(context.getContentResolver(), Settings.System.USER_ROTATION, defaultDisplay.getRotation());
                Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
            } else {
                Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
            }

            Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    void checkAutoRot() {
        if (getRotationScreenFromSettingsIsEnabled(getApplicationContext())) {
            autoRotationSwitch.setChecked(true);
        } else {
            autoRotationSwitch.setChecked(false);
        }
    }

    public static boolean getRotationScreenFromSettingsIsEnabled(Context context) {
        int result = 0;
        try {
            result = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return result == 1;
    }

    public static String androidVersionName() {
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 16) {
            return "Ice cream sandwich";
        }

        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 19) {
            return "Jelly bean";
        }

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            return "KitKat";
        }

        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) {
            return "Lollipop";
        }

        if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 24) {
            return "Marshmallow";
        }

        if (Build.VERSION.SDK_INT >= 24 && Build.VERSION.SDK_INT < 26) {
            return "Nougat";
        }

        if (Build.VERSION.SDK_INT >= 26 && Build.VERSION.SDK_INT < 28) {
            return "Oreo";
        }

        if (Build.VERSION.SDK_INT >= 28 && Build.VERSION.SDK_INT < 29) {
            return "Pie";
        }

        if (Build.VERSION.SDK_INT >= 29 && Build.VERSION.SDK_INT < 30) {
            return "Q";
        }

        return "Version not available";
    }

    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            br.close();
            String[] array = text.split(":\\s+", 2);
            if (array.length >= 2) {
                return array[1];
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getNumberOfCores() {
        if (Build.VERSION.SDK_INT >= 17) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return getNumCoresOldPhones();
        }
    }

    private int getNumCoresOldPhones() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            //Default to return 1 core
            return 1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuapp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemDeviceInfo) {
            Intent intentClass = new Intent(this, MainActivity.class);
            startActivity(intentClass);
            ActivityCompat.finishAffinity(this);
        }

        if (item.getItemId() == R.id.itemDeviceMonitor) {
            Intent intentClass = new Intent(this, MonitorActivity.class);
            startActivity(intentClass);
            ActivityCompat.finishAffinity(this);
        }

        if (item.getItemId() == R.id.itemSensors) {
            Intent intentClass = new Intent(this, SensorsActivity.class);
            startActivity(intentClass);
            ActivityCompat.finishAffinity(this);
        }

        if (item.getItemId() == R.id.itemDataTransfer) {
            Intent intentClass = new Intent(this, DataTransferActivity.class);
            startActivity(intentClass);
            ActivityCompat.finishAffinity(this);
        }

        if (item.getItemId() == R.id.itemOperations) {
            Intent intentClass = new Intent(this, OperationsActivity.class);
            startActivity(intentClass);
            ActivityCompat.finishAffinity(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        deviceGPUText.setText("GPU: " + gl10.glGetString(GL10.GL_RENDERER));
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }
}