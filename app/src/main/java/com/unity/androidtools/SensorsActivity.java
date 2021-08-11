package com.unity.androidtools;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.fingerprint.FingerprintManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class SensorsActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    Sensor lightSensor, magneticSensor, accelerometerSensor, proximitySensor, humiditySensor, stepCounter, pressureSensor, heartRateSensor, rotationVectorSensor, gravitySensor, orientationSensor, geoVectorSensor, gyroSensor;

    TextView orientationX, orientationY, orientationZ, gravityX, gravityY, gravityZ, rotationVectorX, rotationVectorY, rotationVectorZ, heartRateText, lightSensorText, labelMagneticXText, labelMagneticYText, labelMagneticZText, labelAccelerometerX, labelAccelerometerY, labelAccelerometerZ, labelProximity, labelHumidity, labelSteps, pressureText;

    TextView accelerometerNameText, accelerometerPowerText, accelerometerResolutionText, accelerometerMaxRangeText, accelerometerVendorText, accelerometerVersionText;
    TextView lightNameText, lightPowerText, lightResolutionText, lightMaxRangeText, lightVendorText, lightVersionText;
    TextView stepNameText, stepPowerText, stepVendorText;
    TextView proximityNameText, proximityPowerText, proximityResolutionText, proximityMaxRangeText, proximityVendorText, proximityVersionText;
    TextView humidityNameText, humidityPowerText, humidityResolutionText, humidityMaxRangeText, humidityVendorText, humidityVersionText, degreesXText, degreesYText, degreesZText;
    TextView magneticNameText, magneticPowerText, magneticResolutionText, magneticMaxRangeText, magneticVendorText, magneticVersionText, soundText, geoVectorXText, geoVectorYText, geoVectorZText, compassDegreesText;
    TextView gyroXText, gyroYText, gyroZText, gyroVendorText, gyroVersionText, gyroNameText, gyroPowerText, fingerprintText;
    ImageView fingerprintImage;

    private static DecimalFormat df = new DecimalFormat("0.00");

    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    int degrees;
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    MediaRecorder mRecorder = null;
    public FingerprintManager fingerprintManager;
    public KeyguardManager keyManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        lightSensorText = findViewById(R.id.labelLight);
        labelMagneticXText = findViewById(R.id.labelMagneticX);
        labelMagneticYText = findViewById(R.id.labelMagneticY);
        labelMagneticZText = findViewById(R.id.labelMagneticZ);

        labelAccelerometerX = findViewById(R.id.labelAccelerometerX);
        labelAccelerometerY = findViewById(R.id.labelAccelerometerY);
        labelAccelerometerZ = findViewById(R.id.labelAccelerometerZ);
        labelProximity = findViewById(R.id.labelProximity);
        labelHumidity = findViewById(R.id.labelHumidity);
        labelSteps = findViewById(R.id.labelSteps);
        pressureText = findViewById(R.id.labelPressure);
        heartRateText = findViewById(R.id.labelHeartRateSensor);
        rotationVectorX = findViewById(R.id.labelRotationX);
        rotationVectorY = findViewById(R.id.labelRotationY);
        rotationVectorZ = findViewById(R.id.labelRotationZ);
        gravityX = findViewById(R.id.labelGravityX);
        gravityY = findViewById(R.id.labelGravityY);
        gravityZ = findViewById(R.id.labelGravityZ);
        orientationX = findViewById(R.id.labelOrientationX);
        orientationY = findViewById(R.id.labelOrientationY);
        orientationZ = findViewById(R.id.labelOrientationZ);
        degreesXText = findViewById(R.id.labelDegreesX);
        degreesYText = findViewById(R.id.labelDegreesY);
        degreesZText = findViewById(R.id.labelDegreesZ);
        soundText = findViewById(R.id.labelSound);

        accelerometerNameText = findViewById(R.id.labelAccelerometerName);
        accelerometerMaxRangeText = findViewById(R.id.labelAccelerometerMaxRange);
        accelerometerResolutionText = findViewById(R.id.labelAccelerometerRes);
        accelerometerPowerText = findViewById(R.id.labelAccelerometerPower);
        accelerometerVendorText = findViewById(R.id.labelAccelerometerVendor);
        accelerometerVersionText = findViewById(R.id.labelAccelerometerVersion);
        lightMaxRangeText = findViewById(R.id.labelLightMaxRange);
        lightNameText = findViewById(R.id.labelLightName);
        lightPowerText = findViewById(R.id.labelLightPower);
        lightResolutionText = findViewById(R.id.labelLightRes);
        lightVendorText = findViewById(R.id.labelLightVendor);
        lightVersionText = findViewById(R.id.labelLightVersion);
        stepNameText = findViewById(R.id.labelStepName);
        stepPowerText = findViewById(R.id.labelStepPower);
        stepVendorText = findViewById(R.id.labelStepVendor);
        proximityMaxRangeText = findViewById(R.id.labelProximityMaxRange);
        proximityNameText = findViewById(R.id.labelProximityName);
        proximityPowerText = findViewById(R.id.labelProximityPower);
        proximityResolutionText = findViewById(R.id.labelProximityRes);
        proximityVendorText = findViewById(R.id.labelProximityVendor);
        proximityVersionText = findViewById(R.id.labelProximityVersion);
        humidityMaxRangeText = findViewById(R.id.labelHumidityMaxRange);
        humidityNameText = findViewById(R.id.labelHumidityName);
        humidityPowerText = findViewById(R.id.labelHumidityPower);
        humidityResolutionText = findViewById(R.id.labelHumidityRes);
        humidityVendorText = findViewById(R.id.labelHumidityVendor);
        humidityVersionText = findViewById(R.id.labelHumidityVersion);
        magneticMaxRangeText = findViewById(R.id.labelMagneticMaxRange);
        magneticNameText = findViewById(R.id.labelMagneticName);
        magneticPowerText = findViewById(R.id.labelMagneticPower);
        magneticResolutionText = findViewById(R.id.labelMagneticRes);
        magneticVendorText = findViewById(R.id.labelMagneticVendor);
        magneticVersionText = findViewById(R.id.labelMagneticVersion);

        geoVectorXText = findViewById(R.id.labelGeoVectorX);
        geoVectorYText = findViewById(R.id.labelGeoVectorY);
        geoVectorZText = findViewById(R.id.labelGeoVectorZ);
        compassDegreesText = findViewById(R.id.labelDegreesCompass);
        gyroXText = findViewById(R.id.labelGyroX);
        gyroYText = findViewById(R.id.labelGyroY);
        gyroZText = findViewById(R.id.labelGyroZ);
        gyroNameText = findViewById(R.id.labelGyroName);
        gyroPowerText = findViewById(R.id.labelGyroPower);
        gyroVendorText = findViewById(R.id.labelGyroVendor);
        gyroVersionText = findViewById(R.id.labelGyroVersion);
        fingerprintImage = findViewById(R.id.fingerprintImage);
        fingerprintText = findViewById(R.id.labelFingerprint);

        fingerprintImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fingerprintImage.getImageAlpha() != R.mipmap.fingerprint_icon) {
                    startFingerprint(false);
                }
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 101);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

            lightNameText.setText("Name: " + lightSensor.getName());
            lightPowerText.setText("Power: " + lightSensor.getPower() + " mAh");
            lightVendorText.setText("Vendor: " + lightSensor.getVendor());

            lightMaxRangeText.setText("max range: " + lightSensor.getMaximumRange());
            lightResolutionText.setText("Resolution: " + lightSensor.getResolution());
            lightVersionText.setText("Version: " + lightSensor.getVersion());
        } else {
            lightSensorText.setText("No light sensor");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);

            magneticNameText.setText("Name: " + magneticSensor.getName());
            magneticPowerText.setText("Power: " + magneticSensor.getPower() + " mAh");
            magneticVendorText.setText("Vendor: " + magneticSensor.getVendor());

            magneticMaxRangeText.setText("max range: " + magneticSensor.getMaximumRange());
            magneticResolutionText.setText("Resolution: " + magneticSensor.getResolution());
            magneticVersionText.setText("Version: " + magneticSensor.getVersion());
        } else {
            labelMagneticXText.setText("No magnetic field sensor");
            labelMagneticYText.setText("");
            labelMagneticZText.setText("");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

            accelerometerNameText.setText("Name: " + accelerometerSensor.getName());
            accelerometerPowerText.setText("Power: " + accelerometerSensor.getPower() + " mAh");
            accelerometerVendorText.setText("Vendor: " + accelerometerSensor.getVendor());

            accelerometerMaxRangeText.setText("max range: " + accelerometerSensor.getMaximumRange());
            accelerometerResolutionText.setText("Resolution: " + accelerometerSensor.getResolution());
            accelerometerVersionText.setText("Version: " + accelerometerSensor.getVersion());
        } else {
            labelAccelerometerZ.setText("No accelerometer sensor");
            labelAccelerometerX.setText("");
            labelAccelerometerY.setText("");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

            proximityNameText.setText("Name: " + proximitySensor.getName());
            proximityPowerText.setText("Power: " + proximitySensor.getPower() + " mAh");
            proximityVendorText.setText("Vendor: " + proximitySensor.getVendor());

            proximityMaxRangeText.setText("max range: " + proximitySensor.getMaximumRange());
            proximityResolutionText.setText("Resolution: " + proximitySensor.getResolution());
            proximityVersionText.setText("Version: " + proximitySensor.getVersion());
        } else {
            labelProximity.setText("No proximity sensor");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);

            humidityNameText.setText("Name: " + humiditySensor.getName());
            humidityPowerText.setText("Power: " + humiditySensor.getPower() + " mAh");
            humidityVendorText.setText("Vendor: " + humiditySensor.getVendor());

            humidityMaxRangeText.setText("max range: " + humiditySensor.getMaximumRange());
            humidityResolutionText.setText("Resolution: " + humiditySensor.getResolution());
            humidityVersionText.setText("Version: " + humiditySensor.getVersion());
        } else {
            labelHumidity.setText("No humidity sensor");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);

            stepNameText.setText("Name: " + stepCounter.getName());
            stepPowerText.setText("Power: " + stepCounter.getPower() + " mAh");
            stepVendorText.setText("Vendor: " + stepCounter.getVendor());
        } else {
            labelSteps.setText("No step counter sensor");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            pressureText.setText("No pressure sensor");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) != null) {
            heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            heartRateText.setText("No heart rate sensor");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            rotationVectorX.setText("No rotation vector sensor");
            rotationVectorY.setText("");
            rotationVectorZ.setText("");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            gravityX.setText("No gravity sensor");
            gravityY.setText("");
            gravityZ.setText("");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null) {
            orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            orientationX.setText("No orientation sensor");
            orientationY.setText("");
            orientationZ.setText("");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) != null) {
            geoVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
            sensorManager.registerListener(this, geoVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            geoVectorXText.setText("No geo vector sensor available");
            geoVectorYText.setText("");
            geoVectorZText.setText("");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);

            gyroNameText.setText("Name: " + gyroSensor.getName());
            gyroVendorText.setText("Vendor: " + gyroSensor.getVendor());
            gyroVersionText.setText("Version: " + gyroSensor.getVersion());
            gyroPowerText.setText("Power: " + gyroSensor.getPower() + " mAh");
        } else {
            gyroXText.setText("No geo vector sensor available");
            gyroYText.setText("");
            gyroZText.setText("");

            gyroNameText.setText("No gyro available");
            gyroVendorText.setText("");
            gyroVersionText.setText("");
            gyroPowerText.setText("");
        }

        startFingerprint(true);
        startRecorder();

        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(100);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                soundText.setText("Sound decibels: " + Math.abs(20 * Math.log10(getAmplitude())) + "dB");
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

    public void startRecorder()  {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null/");

            try {
                mRecorder.prepare();
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
            mRecorder.start();
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude() / 2700.0);
        else
            return 0;

    }

    Calendar calendar;
    @RequiresApi(api = Build.VERSION_CODES.M)
    void startFingerprint(boolean a) {
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        keyManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (!fingerprintManager.isHardwareDetected()) {
            fingerprintText.setText("No fingerprint available");
            fingerprintImage.setVisibility(View.INVISIBLE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            fingerprintText.setText("No fingerprint permission");
            fingerprintImage.setVisibility(View.INVISIBLE);
        } else if (!keyManager.isKeyguardSecure()) {
            fingerprintText.setText("Fingerprint lock mode is deactivated");
            fingerprintImage.setVisibility(View.INVISIBLE);
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            fingerprintText.setText("No fingerprints detected, add at least 1");
            fingerprintImage.setVisibility(View.INVISIBLE);
        } else {
            if (a) {
                fingerprintText.setText("Waiting for fingerprint");
                fingerprintImage.setImageResource(R.mipmap.fingerprint_icon);
            } else {
                fingerprintImage.setImageResource(R.mipmap.fingerprint_icon);
            }

            FingerPrintHandler fingerprintHandler = new FingerPrintHandler(this);
            fingerprintHandler.startAutentication(fingerprintManager, null);
        }
    }

    @SuppressLint("NewApi")
    public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {
        private Context context;

        public FingerPrintHandler(Context context) {
            this.context = context;
        }

        public void startAutentication(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
            CancellationSignal cancellationSignal = new CancellationSignal();
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            this.update("Auth error" + errString, false);
        }

        @Override
        public void onAuthenticationFailed() {
            this.update("Auth failed", false);
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            this.update("Error: " + helpString, false);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            Calendar cal = Calendar.getInstance();
            this.update("Auth success", true);
        }

        void update(String s, boolean b) {
            TextView label = findViewById(R.id.labelFingerprint);
            ImageView image = findViewById(R.id.fingerprintImage);

            if (b) {
                label.setText(s);
                image.setImageResource(R.mipmap.check_icon);
            } else {
                label.setText(s);
                image.setImageResource(R.mipmap.error_icon);
            }
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_LIGHT:
                lightSensorText.setText("Light power: " + event.values[0] + "lx");
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                labelMagneticXText.setText("Magnetic field X: " + event.values[0] + "μT");
                labelMagneticYText.setText("Magnetic field Y: " + event.values[1] + "μT");
                labelMagneticZText.setText("Magnetic field Z: " + event.values[2] + "μT");

                System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
                mLastMagnetometerSet = true;
                break;
            case Sensor.TYPE_ACCELEROMETER:
                labelAccelerometerX.setText("Input X: " + (event.values[0]));
                labelAccelerometerY.setText("Input Y: " + (event.values[1]));
                labelAccelerometerZ.setText("Input Z: " + (event.values[2]));

                System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
                mLastAccelerometerSet = true;
                break;
            case Sensor.TYPE_PROXIMITY:
                labelProximity.setText("Proximity: " + df.format(event.values[0]) + " cm");
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                labelHumidity.setText("Humidity: " + event.values[0] + " %");
                break;
            case Sensor.TYPE_STEP_COUNTER:
                labelSteps.setText("Steps: " + event.values[0]);
                break;
            case Sensor.TYPE_PRESSURE:
                pressureText.setText("Pressure: " + event.values[0]);
                break;
            case Sensor.TYPE_HEART_RATE:
                heartRateText.setText("Heart rate: " + event.values[0]);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                rotationVectorX.setText("Rotation X: " + event.values[0]);
                rotationVectorY.setText("Rotation Y: " + event.values[1]);
                rotationVectorZ.setText("Rotation Z: " + event.values[2]);

                SensorManager.getRotationMatrixFromVector(rMat, event.values);
                degrees = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
                break;
            case Sensor.TYPE_GRAVITY:
                gravityX.setText("Gravity X: " + event.values[0] + " m/s2");
                gravityY.setText("Gravity Y: " + event.values[1] + " m/s2");
                gravityZ.setText("Gravity Z: " + event.values[2] + " m/s2");
                break;
            case Sensor.TYPE_ORIENTATION:
                orientationX.setText("Orientation X: " + event.values[0]);
                orientationY.setText("Orientation Y: " + event.values[1]);
                orientationZ.setText("Orientation Z: " + event.values[2]);
                degreesXText.setText("Degrees X: " + (event.values[0]));
                degreesYText.setText("Degrees Y: " + (event.values[1]));
                degreesZText.setText("Degrees Z: " + (event.values[2]));
                break;
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                geoVectorXText.setText("Rotation X: " + event.values[0]);
                geoVectorYText.setText("Rotation Y: " + event.values[1]);
                geoVectorZText.setText("Rotation Z: " + event.values[2]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroXText.setText("Gyro X: " + event.values[0]);
                gyroYText.setText("Gyro Y: " + event.values[1]);
                gyroZText.setText("Gyro Z: " + event.values[2]);
                break;
            default:
                break;
        }

        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            degrees = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        compassDegreesText.setText("Degrees to north: " + degrees);
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI);
        } else {
            labelSteps.setText("No step counter sensor");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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

        if (item.getItemId() == R.id.itemDataTransfer) {
            Intent intentClass = new Intent(this, DataTransferActivity.class);
            startActivity(intentClass);
            ActivityCompat.finishAffinity(this);
        }

        if (item.getItemId() == R.id.itemSensors) {
            Intent intentClass = new Intent(this, SensorsActivity.class);
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
}