package cz.vitlabuda.test.backgroundgpstest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;
import java.util.Locale;

public class GPSScanningService extends Service {

    private static final String TAG = "GPSScanningService";
    private static final String WAKELOCK_TAG = "cz.vitlabuda.test.backgroundgpstest.GPSScanningService::wakeLock";


    private boolean stopWifiScanning = false;
    private boolean wifiScanCurrentlyInProgress = false;
    private WifiManager wifiManager;
    private PowerManager.WakeLock wakeLock;
    private LocationManager locationManager;

    private final LocationListener locationListener = new LocationListener() {
        private int locationUpdateCount = 0;

        @Override
        public void onLocationChanged(@NonNull Location location) {
            String message = String.format(Locale.US, "(%d) %f, %f (altitude: %f m, accuracy: %f m)", ++locationUpdateCount, location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy());

            Log.d(TAG, "onLocationChanged: " + message);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            Log.d(TAG, "onProviderEnabled: Location provider '" + provider + "' was enabled.");
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Log.d(TAG, "onProviderDisabled: Location provider '" + provider + "' was disabled.");
        }
    };
    private final BroadcastReceiver wifiScanningBroadcastReceiver = new BroadcastReceiver() {
        private int scannedNetworkCount = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            if(!intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) {
                Log.d(TAG, "onReceive: The Wi-Fi scanning broadcast receiver was triggered, but the scan was unsuccessful.");
                return;
            }

            List<ScanResult> scanResultList = wifiManager.getScanResults();
            for(ScanResult scanResult : scanResultList)
                handleScannedWifiNetwork(scanResult);

            wifiScanCurrentlyInProgress = false;
        }

        private void handleScannedWifiNetwork(ScanResult scanResult) {
            String message = String.format(Locale.US, "(%d) %s %s %s", ++scannedNetworkCount, scanResult.SSID, scanResult.BSSID, scanResult.capabilities);

            Log.d(TAG, "handleScannedWifiNetwork: " + message);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        // create & show foreground service notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(this, App.GPS_SERVICE_NOTIFICATION_CHANNEL)
                .setContentTitle("Background GPS test app")
                .setContentText("The GPS scanning service is running in the background.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSound(null)
                .setOngoing(true)
                .build();

        startForeground(App.GPS_SERVICE_NOTIFICATION_ID, notification);

        // acquire wakelock
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG);
        wakeLock.acquire(43200000); // 12 hours


        Log.d(TAG, "onCreate: GPS scanning service started.");

        startLocationScanning();
        startWifiScanning();
    }

    private void startLocationScanning() {
        Log.d(TAG, "startLocationScanning: Location listener is listening for location updates.");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, App.GPS_SCAN_INTERVAL, App.GPS_SCAN_DISTANCE_THRESHOLD, locationListener);
        } catch (SecurityException e) {
            throw new RuntimeException("The location permission should always be granted if the service could be started!");
        }
    }

    private void startWifiScanning() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        registerReceiver(wifiScanningBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


        Thread thread = new Thread(() -> {
            // in a real app, you should probably check if the wifi is on before scanning! (and listen for the scan changes)

            // Throttling
            // The following limitations apply to the frequency of scans using WifiManager.startScan().
            //
            // Android 8.0 and Android 8.1:
            // - Each background app can scan one time in a 30-minute period.
            //
            // Android 9:
            // - Each foreground app can scan four times in a 2-minute period. This allows for a burst of scans in a short time.
            // - All background apps combined can scan one time in a 30-minute period.
            //
            // Android 10 and higher:
            // - The same throttling limits from Android 9 apply.
            // - There is a new developer option to toggle the throttling off for local testing (under Developer Options > Networking > Wi-Fi scan throttling).

            while(!stopWifiScanning) {
                if(!wifiScanCurrentlyInProgress && wifiManager.startScan())
                    wifiScanCurrentlyInProgress = true;

                SystemClock.sleep(App.WIFI_SCAN_INTERVAL);
            }

            Log.d(TAG, "startWifiScanning: The Wi-Fi scanning was stopped.");
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // stop wifi scanning
        stopWifiScanning = true;
        unregisterReceiver(wifiScanningBroadcastReceiver);

        // unregister location listener
        locationManager.removeUpdates(locationListener);

        // release wakelock
        wakeLock.release();

        // dismiss notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(App.GPS_SERVICE_NOTIFICATION_ID);


        Log.d(TAG, "onDestroy: GPS scanning service stopped.");
    }
}