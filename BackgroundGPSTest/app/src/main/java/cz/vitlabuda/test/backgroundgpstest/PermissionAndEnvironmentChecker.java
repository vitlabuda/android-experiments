package cz.vitlabuda.test.backgroundgpstest;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class PermissionAndEnvironmentChecker {
    private static final String TAG = "PermissionAndEnvironmen";
    
    private final Context context;

    public PermissionAndEnvironmentChecker(Context context) {
        this.context = context;
    }

    public boolean isLocationPermissionGranted() {
        return (isFineLocationPermissionGranted() && isBackgroundLocationPermissionGranted());
    }

    public boolean isFineLocationPermissionGranted() {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isBackgroundLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED);

        return isFineLocationPermissionGranted();
    }

    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isBatteryUsageUnrestricted() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        

        boolean isIgnoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(context.getApplicationContext().getPackageName());
        Log.d(TAG, "isBatteryUsageUnrestricted: isIgnoringBatteryOptimizations = " + isIgnoringBatteryOptimizations);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            boolean isBackgroundRestricted = activityManager.isBackgroundRestricted();
            Log.d(TAG, "isBatteryUsageUnrestricted: isBackgroundRestricted = " + isBackgroundRestricted);

            return isIgnoringBatteryOptimizations && !isBackgroundRestricted;
        }

        return isIgnoringBatteryOptimizations;
    }

    public boolean checkIfDeviceHasGPS() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER);
    }
}
