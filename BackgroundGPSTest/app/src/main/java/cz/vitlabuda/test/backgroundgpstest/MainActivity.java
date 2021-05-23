package cz.vitlabuda.test.backgroundgpstest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_CODE_BACKGROUND_LOCATION = 2;

    private PermissionAndEnvironmentChecker permissionAndEnvironmentChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionAndEnvironmentChecker = new PermissionAndEnvironmentChecker(this);
    }

    private boolean isGPSScanningServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(GPSScanningService.class.getName().equals(runningServiceInfo.service.getClassName()))
                return true;
        }

        return false;
    }

    public void startGPSScanning(View view) {
        if(!permissionAndEnvironmentChecker.checkIfDeviceHasGPS()) {
            Toast.makeText(this, "Your device doesn't have a GPS module!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!permissionAndEnvironmentChecker.isLocationPermissionGranted()) {
            Toast.makeText(this, "The location permission wasn't granted.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!permissionAndEnvironmentChecker.isGPSEnabled()) {
            Toast.makeText(this, "The GPS is disabled.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!permissionAndEnvironmentChecker.isBatteryUsageUnrestricted()) {
            Toast.makeText(this, "The app's battery usage is restricted.", Toast.LENGTH_SHORT).show();
            return;
        }


        if(isGPSScanningServiceRunning()) {
            Toast.makeText(this, "The GPS scanning service is already running.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GPSScanningService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent);
        else
            startService(intent);

    }

    public void stopGPSScanning(View view) {
        if(!isGPSScanningServiceRunning()) {
            Toast.makeText(this, "The GPS scanning service is not running.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GPSScanningService.class);
        stopService(intent);
    }



    // Caution: If your app targets Android 11 (API level 30) or higher, the system enforces this best practice.
    // If you request a foreground location permission and the background location permission at the same time,
    // the system ignores the request and doesn't grant your app either permission.
    public void requestFineLocationPermissionButtonClicked(View view) {
        if(permissionAndEnvironmentChecker.isFineLocationPermissionGranted()) {
            Toast.makeText(this, "The fine location permission was already granted.", Toast.LENGTH_SHORT).show();

        } else if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Fine location permission needed")
                    .setMessage("The fine location permission is needed for scanning the GPS location.")
                    .setPositiveButton("OK", (dialog, which) -> requestFineLocationPermission())
                    .setNegativeButton("Cancel", null)
                    .show();

        } else {
            requestFineLocationPermission();
        }
    }

    public void requestBackgroundLocationPermissionButtonClicked(View view) {
        if(permissionAndEnvironmentChecker.isBackgroundLocationPermissionGranted()) {
            Toast.makeText(this, "The background location permission was already granted.", Toast.LENGTH_SHORT).show();

        } else if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Background location permission needed")
                    .setMessage("The background location permission is needed for scanning the GPS location.")
                    .setPositiveButton("OK", (dialog, which) -> requestBackgroundLocationPermission())
                    .setNegativeButton("Cancel", null)
                    .show();

        } else {
            requestBackgroundLocationPermission();
        }
    }

    private void requestFineLocationPermission() {
        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_FINE_LOCATION);
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_CODE_BACKGROUND_LOCATION);
        else
            requestFineLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case PERMISSION_REQUEST_CODE_FINE_LOCATION:
                if(permissionAndEnvironmentChecker.isFineLocationPermissionGranted())
                    Toast.makeText(this, "The fine location permission was granted successfully!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "The fine location permission was denied!", Toast.LENGTH_SHORT).show();
                break;

            case PERMISSION_REQUEST_CODE_BACKGROUND_LOCATION:
                if(permissionAndEnvironmentChecker.isBackgroundLocationPermissionGranted())
                    Toast.makeText(this, "The background location permission was granted successfully!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "The background location permission was denied!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}