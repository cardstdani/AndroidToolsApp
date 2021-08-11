package com.unity.androidtools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CpuUsageInfo;
import android.os.Environment;
import android.os.HardwarePropertiesManager;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class MonitorActivity extends AppCompatActivity implements LocationListener, SurfaceHolder.Callback, SensorEventListener {

    WifiManager wifiManager;
    LocationManager locationManager;
    TextView cpuTemperatureText, batteryTemperatureText;
    Button changeBluetoothNameButton, sendFileButton;
    TextView latitudeText, altitudeText, longitudeText, locationAccuracyText, locationProviderText, speedText, wifiSSIDText, wifiBSSIDText, networkSpeedText, wifiFrequencytext, wifiLinkSpeed;
    TextView batteryStatusText, batteryChargeText, avaliableBatteryText, totalBattery, totalBatteryCapacitytext, batteryTechnologyText, batteryHealthText, batteryChargeModeText, cpuUsageText, ramUsageText, avaliableRamText, totalRamtext;
    TextView storageText, storageAvaliableText, deviceIPText, externalStorageText, externalAvaliableText, bluetoothPairedDevicesText, bluetoothAvailableDevicesText, ambientTemperatureText, deviceTemperatureText;
    EditText bluetoothDeviceName;
    Switch switchWifi, switchTorch, switchBluetooth;
    NetworkInfo mWifi;
    ConnectivityManager connManager;
    Camera cam;
    SurfaceHolder surfaceHolder;
    SurfaceView surfaceView;
    BluetoothAdapter bluetoothAdapter;
    Spinner dropdownCamera;
    int availableBluetoothDevices = 0;

    Intent fileIntent;
    String devicesInfo;

    private static DecimalFormat df = new DecimalFormat("0.00");
    public CpuUsageInfo usageCPU;
    float usage = 0;
    long total = 0;
    long idle = 0;
    int batteryPercentage;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        cpuTemperatureText = findViewById(R.id.labelCpuTemperature);
        batteryTemperatureText = findViewById(R.id.labelBatteryTemperature);
        switchWifi = findViewById(R.id.switchWifi);
        switchTorch = findViewById(R.id.switchTorch);
        switchBluetooth = findViewById(R.id.switchBluetooth);

        latitudeText = findViewById(R.id.labelLatitude);
        altitudeText = findViewById(R.id.labelAltitude);
        longitudeText = findViewById(R.id.labelLongitude);
        locationAccuracyText = findViewById(R.id.labelLocationAccuracy);
        locationProviderText = findViewById(R.id.labelLocationProvider);

        batteryChargeText = findViewById(R.id.labelBatteryCharge);
        batteryStatusText = findViewById(R.id.labelBatteryHealth);
        totalBattery = findViewById(R.id.labelTotalBattery);
        batteryTechnologyText = findViewById(R.id.labelBatteryTechnology);
        batteryHealthText = findViewById(R.id.labelBatteryHealth);
        batteryChargeModeText = findViewById(R.id.labelBatteryChargeMode);
        wifiSSIDText = findViewById(R.id.labelWifiSSID);
        wifiBSSIDText = findViewById(R.id.labelWifiBSSID);
        networkSpeedText = findViewById(R.id.labelNetworkSpeed);
        cpuUsageText = findViewById(R.id.labelCPUsage);
        speedText = findViewById(R.id.labelSpeed);
        totalBatteryCapacitytext = findViewById(R.id.labelBatteryTotalCapacity);
        ramUsageText = findViewById(R.id.labelRAMUsage);
        avaliableRamText = findViewById(R.id.labelAvaliableRAM);
        totalRamtext = findViewById(R.id.labelTotalRam);
        storageText = findViewById(R.id.labelStorageTotal);
        storageAvaliableText = findViewById(R.id.labelStorageAvaliable);
        deviceIPText = findViewById(R.id.labelIP);
        avaliableBatteryText = findViewById(R.id.labelAvaliableBattery);
        externalAvaliableText = findViewById(R.id.labelExternalAvaliableStorage);
        externalStorageText = findViewById(R.id.labelExternalStorage);
        bluetoothPairedDevicesText = findViewById(R.id.bluetoothPairedDevices);
        bluetoothAvailableDevicesText = findViewById(R.id.bluetoothAvailableDevices);
        wifiFrequencytext = findViewById(R.id.labelNetworkFrequency);
        wifiLinkSpeed = findViewById(R.id.labelNetworkLinkSpeed);
        bluetoothDeviceName = findViewById(R.id.bluetoothDeviceName);
        changeBluetoothNameButton = findViewById(R.id.changeBluetoothNameButton);
        sendFileButton = findViewById(R.id.sendFileButton);
        ambientTemperatureText = findViewById(R.id.labelAmbuientTemperature);
        deviceTemperatureText = findViewById(R.id.labelDeviceTemperature);
        dropdownCamera = findViewById(R.id.spinnerCamera);

        String[] items = new String[Camera.getNumberOfCameras()];
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                items[i] = "Camera " + (i + 1) + " front";
            } else {
                items[i] = "Camera " + (i + 1);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdownCamera.setAdapter(adapter);

        totalBatteryCapacitytext.setText("Battery capacity: " + getTotalBatteryCapacity() + " mAh");

        storageText.setText("Total storage: " + getTotalInternalMemorySize());
        storageAvaliableText.setText("Available storage: " + getAvailableInternalMemorySize());
        //externalStorageText.setText("Total space: " + df.format(Environment.getExternalStorageDirectory().getTotalSpace() / (1024 * 1024)) + " MB");
        //externalAvaliableText.setText("Avaliable space: " + df.format(Environment.getExternalStorageDirectory().getFreeSpace() / (1024 * 1024)) + " MB");

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 101);
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) this);
            } else if (locationManager.isProviderEnabled(LocationManager.EXTRA_PROVIDER_NAME)) {
                locationManager.requestLocationUpdates(LocationManager.EXTRA_PROVIDER_NAME, 0, 0, (LocationListener) this);
            } else if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, (LocationListener) this);
            } else {
                latitudeText.setText("Turn on location services");
                longitudeText.setText("Unable to get longitude");
                altitudeText.setText("Unable to get altitude");

                locationAccuracyText.setText("Unable to get accuracy");
                locationProviderText.setText("No provider enabled");
            }
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            switchTorch.setVisibility(View.VISIBLE);
        } else {
            switchTorch.setVisibility(View.INVISIBLE);
        }

        switchTorch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cam = Camera.open();
                    } catch (Exception e) {
                    }

                    Camera.Parameters parameters;
                    parameters = cam.getParameters();
                    parameters.setPreviewFrameRate(30);
                    if (switchTorch.isChecked()) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    } else {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    }
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    //parameters.setPreviewSize(352, 407);
                    cam.setParameters(parameters);
                    cam.setDisplayOrientation(90);
                    try {
                        cam.setPreviewDisplay(surfaceHolder);
                        cam.startPreview();
                    } catch (
                            IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        dropdownCamera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "selected" + dropdownCamera.getSelectedItemId(), Toast.LENGTH_LONG).show();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        Toast.makeText(getApplicationContext(), (int) dropdownCamera.getSelectedItemId(), Toast.LENGTH_LONG).show();
                        cam = Camera.open((int) dropdownCamera.getSelectedItemId());
                    } catch (Exception e) {
                    }

                    Camera.Parameters parameters;
                    parameters = cam.getParameters();
                    parameters.setPreviewFrameRate(30);
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    //parameters.setPreviewSize(352, 407);
                    cam.setParameters(parameters);
                    cam.setDisplayOrientation(90);
                    try {
                        cam.setPreviewDisplay(surfaceHolder);
                        cam.startPreview();
                    } catch (
                            IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                switchBluetooth.setChecked(true);
                bluetoothDeviceName.setText(bluetoothAdapter.getName());

                discover();
            } else {
                switchBluetooth.setChecked(false);
            }
        } else {
            switchBluetooth.setVisibility(View.INVISIBLE);
        }

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            Sensor tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            sensorManager.registerListener((SensorEventListener) this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            ambientTemperatureText.setText("Ambient temperature not available");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE) != null) {
            Sensor tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
            sensorManager.registerListener((SensorEventListener) this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            deviceTemperatureText.setText("Device temperature not available");
        }

        changeBluetoothNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isEnabled()) {
                    if (bluetoothDeviceName.getText() != null) {
                        bluetoothAdapter.setName(bluetoothDeviceName.getText().toString());
                        Toast.makeText(getApplicationContext(), "The name has been changed to " + bluetoothDeviceName.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth is disabled", Toast.LENGTH_LONG).show();
                }
            }
        });

        sendFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isEnabled()) {
                    fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    fileIntent.setType("*/*");
                    startActivityForResult(fileIntent, 101);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth is disabled", Toast.LENGTH_LONG).show();
                }
            }
        });

        switchBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                    bluetoothAvailableDevicesText.setText("Bluetooth not available");
                    bluetoothPairedDevicesText.setText("Bluetooth not available");

                    availableBluetoothDevices = 0;
                } else {
                    bluetoothAdapter.enable();
                    bluetoothDeviceName.setText(bluetoothAdapter.getName());
                    discover();
                }
            }
        });

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            switchWifi.setChecked(true);
        } else {
            switchWifi.setChecked(false);
        }

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        switchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchWifi.isChecked()) {
                    changeWifiState(true);
                } else {
                    changeWifiState(false);
                }
            }
        });

        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(1000);

                        runOnUiThread(new Runnable() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void run() {

                                temps();
                                cpuUsageText.setText("CPU");
                                loadBatterySection();

                                avaliableBatteryText.setText("Avaliable battery: " + ((batteryPercentage * getTotalBatteryCapacity()) / 100) + " mAh");
                                updateRAM();

                                if (wifiManager.isWifiEnabled()) {
                                    mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                                    String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                                    deviceIPText.setText("IP:" + ipAddress);
                                    wifiSSIDText.setText("Wifi SSID: " + wifiManager.getConnectionInfo().getSSID());
                                    wifiBSSIDText.setText("Wifi BSSID: " + wifiManager.getConnectionInfo().getBSSID());

                                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        String frequency;
                                        frequency = Float.toString(wifiInfo.getFrequency());
                                        frequency = frequency.substring(0, 1) + "," + frequency.substring(1, frequency.length() - 2);
                                        wifiFrequencytext.setText("Frequency: " + frequency + "GHz");
                                    } else {
                                        wifiFrequencytext.setText("Frequency not available");
                                    }

                                    wifiLinkSpeed.setText("Link speed: " + wifiInfo.getLinkSpeed() + " Mbps");

                                    NetworkCapabilities nc = connManager.getNetworkCapabilities(connManager.getActiveNetwork());
                                    networkSpeedText.setText("Net speed: " + nc.getLinkDownstreamBandwidthKbps() / (1024 * 1024));
                                } else {
                                    wifiSSIDText.setText("Wifi SSID: unknown");
                                    wifiBSSIDText.setText("Wifi BSSID: unknown");
                                    networkSpeedText.setText("Unable to get network speed");
                                    deviceIPText.setText("Unable to get IP adress");
                                    wifiLinkSpeed.setText("Link speed not available");
                                    wifiFrequencytext.setText("Unable to get frequency");
                                }

                                if (bluetoothAdapter != null) {
                                    bluetoothPairedDevicesText.setText("Bluetooth is deactivated");
                                    String bluetoothDeviceInfo = "";
                                    if (bluetoothAdapter.getBondedDevices().size() > 0) {
                                        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
                                            bluetoothDeviceInfo += device.getName() + "\n" + device.getAddress() + "\n";
                                            bluetoothPairedDevicesText.setText(bluetoothDeviceInfo);
                                        }
                                    }

                                    discover();
                                } else {
                                    bluetoothAvailableDevicesText.setText("No bluetooth available");
                                    bluetoothPairedDevicesText.setText("No paired devices available");
                                }
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return formatSize(availableBlocks * blockSize);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return formatSize(totalBlocks * blockSize);
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "GB";
                size /= 1024;
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            String filePath = data.getData().getPath();
            Toast.makeText(getApplicationContext(), filePath, Toast.LENGTH_LONG).show();
        }
    }

    void discover() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceName = device.getName();
                String deviceAdress = device.getAddress();

                if (deviceName == null) {
                    deviceName = "Unknown name";
                }

                devicesInfo += deviceName + "\n" + deviceAdress + "\n";
                bluetoothAvailableDevicesText.setText(devicesInfo);
                availableBluetoothDevices++;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void updateRAM() {
        ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem / (1024 * 1024);
        long avaliableMemory = memInfo.availMem / (1024 * 1024);

        totalRamtext.setText("Total RAM: " + totalMemory + "MB");
        avaliableRamText.setText("Avaliable RAM: " + avaliableMemory + "MB");
        ramUsageText.setText("RAM usage: " + (totalMemory - avaliableMemory) + "MB");
    }

    public Double getTotalBatteryCapacity() {

        Object mPowerProfile_ = null;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(this);

        } catch (Exception e) {

            // Class not found?
            e.printStackTrace();
        }

        try {

            // Invoke PowerProfile method "getAveragePower" with param
            // "battery.capacity"
            batteryCapacity = (Double) Class.forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");

        } catch (Exception e) {

            // Something went wrong
            e.printStackTrace();
        }

        return batteryCapacity;
    }

    void temps() {
        cpuTemperatureText.setText("CPU temperature: " + getCpuTemp() + "°C");
        batteryTemperatureText.setText("Battery temperature: " + batteryTemperature(this) + "°C");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                ambientTemperatureText.setText("Ambient temperature: " + event.values[0] + "°C");
                break;
            case Sensor.TYPE_TEMPERATURE:
                deviceTemperatureText.setText("Device temperature: " + event.values[0] + "°C");
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitudeText.setText("Latitude:" + location.getLatitude());
        longitudeText.setText("Longitude: " + location.getLongitude());
        altitudeText.setText("Altitude: " + location.getAltitude());

        locationAccuracyText.setText("Accuracy: " + location.getAccuracy() + "meters");
        locationProviderText.setText("Provider: " + location.getProvider());
        speedText.setText("Speed: " + location.getSpeed());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        latitudeText.setText("Turn on location services");
        longitudeText.setText("Unable to get longitude");
        altitudeText.setText("Unable to get altitude");

        locationAccuracyText.setText("Unable to get accuracy");
        locationProviderText.setText("No provider enabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        latitudeText.setText("Loading...");
        longitudeText.setText("Loading...");
        altitudeText.setText("Loading...e");
        locationAccuracyText.setText("Loading...");
        locationProviderText.setText("Loading...");
    }


    public void changeWifiState(boolean activated) {
        wifiManager.setWifiEnabled(activated);
    }

    public String getCpuTemp() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                HardwarePropertiesManager hardwarePropertiesManager = (HardwarePropertiesManager) getSystemService(Context.HARDWARE_PROPERTIES_SERVICE);
                float[] temp = hardwarePropertiesManager.getDeviceTemperatures(HardwarePropertiesManager.DEVICE_TEMPERATURE_CPU, HardwarePropertiesManager.TEMPERATURE_CURRENT);
                return String.valueOf(temp[0]);
            } else {
                return "Unavailable CPU temperature";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Unavailable CPU temperature";
        }
    }

    //BATTERY TEMPERATURE
    public static String batteryTemperature(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        float temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
        return String.valueOf(temp);
    }

    private void loadBatterySection() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(batteryInfoReceiver, intentFilter);
    }

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBatteryData(intent);
        }
    };

    private void updateBatteryData(Intent intent) {
        boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);

        if (present) {
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            int healthLbl = -1;

            switch (health) {
                case BatteryManager.BATTERY_HEALTH_COLD:
                    batteryHealthText.setText("Battery health: Cold");
                    break;

                case BatteryManager.BATTERY_HEALTH_DEAD:
                    batteryHealthText.setText("Battery health: Dead");
                    break;

                case BatteryManager.BATTERY_HEALTH_GOOD:
                    batteryHealthText.setText("Battery health: Good");
                    break;

                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    batteryHealthText.setText("Battery health: Over voltage");
                    break;

                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    batteryHealthText.setText("Battery health: Overheat");
                    break;

                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    batteryHealthText.setText("Battery health: Unspecified failure");
                    break;

                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    batteryHealthText.setText("Battery health: Unknown");
                default:
                    break;
            }

            // Calculate Battery Pourcentage ...
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if (level != -1 && scale != -1) {
                int batteryPct = (int) ((level / (float) scale) * 100f);
                batteryPercentage = batteryPct;
                batteryChargeText.setText("Battery percentage: " + batteryPercentage + " %");
            }

            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);

            switch (plugged) {
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    batteryChargeModeText.setText("Charge mode: Wireless");
                    break;

                case BatteryManager.BATTERY_PLUGGED_USB:
                    batteryChargeModeText.setText("Charge mode: Plugged USB");
                    break;

                case BatteryManager.BATTERY_PLUGGED_AC:
                    batteryChargeModeText.setText("Charge mode: Plugged AC");
                    break;

                default:
                    batteryChargeModeText.setText("Charge mode: Not plugged");
                    break;
            }


            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    batteryStatusText.setText("Status: Charging");
                    break;

                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    batteryStatusText.setText("Status: Discharging");
                    break;

                case BatteryManager.BATTERY_STATUS_FULL:
                    batteryStatusText.setText("Status: Full");
                    break;

                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    batteryStatusText.setText("Status: Unknown");
                    break;

                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                default:
                    batteryStatusText.setText("Status: Not charging");
                    break;
            }

            if (intent.getExtras() != null) {
                String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);

                if (!"".equals(technology)) {
                    batteryTechnologyText.setText("Technology : " + technology);
                }
            }

            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            if (voltage > 0) {
                totalBattery.setText("Voltage : " + voltage + " mV");
            }

            long capacity = getBatteryCapacity(this);

            //totalBattery.setText("Capacity : " + capacity + " mAh");


        } else {
            Toast.makeText(this, "No Battery present", Toast.LENGTH_SHORT).show();
        }

    }

    public long getBatteryCapacity(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager mBatteryManager = (BatteryManager) ctx.getSystemService(Context.BATTERY_SERVICE);
            Long chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            Long capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            if (chargeCounter != null && capacity != null) {
                long value = (long) (((float) chargeCounter / (float) capacity) * 100f);
                return value;
            }
        }

        return 0;
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                cam = Camera.open();
            } catch (Exception e) {
            }

            Camera.Parameters parameters;
            parameters = cam.getParameters();
            parameters.setPreviewFrameRate(30);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            //parameters.setPreviewSize(352, 407);
            cam.setParameters(parameters);
            cam.setDisplayOrientation(90);
            try {
                cam.setPreviewDisplay(surfaceHolder);
                cam.startPreview();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cam.stopPreview();
            cam.release();
            cam = null;
        }
    }
}