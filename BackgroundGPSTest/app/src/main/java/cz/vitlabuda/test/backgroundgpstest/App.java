package cz.vitlabuda.test.backgroundgpstest;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final int GPS_SERVICE_NOTIFICATION_ID = 1;
    public static final String GPS_SERVICE_NOTIFICATION_CHANNEL = "cz.vitlabuda.test.backgroundgpstest.App.BACKGROUND_GPS_SERVICE_NOTIFICATION_CHANNEL";

    public static final int GPS_SCAN_INTERVAL = 1000; // milliseconds; must be non-zero
    public static final int GPS_SCAN_DISTANCE_THRESHOLD = 1; // meters; can be zero
    public static final int WIFI_SCAN_INTERVAL = 5000; // milliseconds; interval < 30000ms is working properly only if the wifi scan throttling is turned off in the developer settings

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(GPS_SERVICE_NOTIFICATION_CHANNEL, "GPS service notifications", NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
