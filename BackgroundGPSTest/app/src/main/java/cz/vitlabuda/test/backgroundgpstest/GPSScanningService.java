package cz.vitlabuda.test.backgroundgpstest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Locale;

public class GPSScanningService extends Service {

    private static final String TAG = "GPSScanningService";
    private static final String WAKELOCK_TAG = "cz.vitlabuda.test.backgroundgpstest.GPSScanningService::wakeLock";

    private PowerManager.WakeLock wakeLock;
    private LocationManager locationManager;
    private final LocationListener locationListener = new LocationListener() {
        private int locationUpdateCount = 0;

        @Override
        public void onLocationChanged(@NonNull Location location) {
            String message = String.format(Locale.US, "(%d) %f, %f (altitude: %f m, accuracy: %f m)", locationUpdateCount, location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy());

            Log.d(TAG, "onLocationChanged: " + message);

            locationUpdateCount++;
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