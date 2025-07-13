package ghazimoradi.soheil.soundrecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public abstract class ActivityEnhanced extends AppCompatActivity {
    private final static short PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;
    private final static String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private final static short RECORD_REQUEST_CODE = 0;

    private interface OnDialogButtonListener {
        void onDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!hasRecordPermission()) {
            request();
        }
    }

    boolean hasRecordPermission() {
        return ActivityCompat.checkSelfPermission(this, RECORD_AUDIO) == PERMISSION_GRANTED;
    }

    void request() {
        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, RECORD_REQUEST_CODE);
    }

    private boolean shouldShowReason() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, RECORD_AUDIO);
    }

    private void showDialog(String title, String message, String buttonTile, OnDialogButtonListener listener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setNeutralButton(buttonTile, (dialogInterface, i) -> listener.onDialog());
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0) {
                return;
            }
            if (grantResults[RECORD_REQUEST_CODE] == PERMISSION_GRANTED) {
                this.recreate();
            } else {
                if (shouldShowReason()) {
                    showDialog(
                            "Camera permission needed!",
                            "Camera permission needed for app work correctly!",
                            "OK",
                            this::request
                    );
                } else {
                    showDialog(
                            "Open settings!",
                            "You should open settings and grant camera permission!",
                            "Open settings",
                            () -> {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                finish();
                            }
                    );
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}