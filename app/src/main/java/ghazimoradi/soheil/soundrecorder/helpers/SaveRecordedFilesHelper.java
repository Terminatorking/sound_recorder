package ghazimoradi.soheil.soundrecorder.helpers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import java.io.FileNotFoundException;

public class SaveRecordedFilesHelper {

    public static ParcelFileDescriptor recordedFileDescriptor(
            Activity activity,
            String fileName
    ) throws FileNotFoundException {
        ContentResolver resolver = activity.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");

        String appName = activity.getApplicationContext()
                .getApplicationInfo().
                loadLabel(activity.getPackageManager())
                .toString();

        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/" + appName);

        Uri audioCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            audioCollection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        Uri audioUri = resolver.insert(audioCollection, values);

        if (audioUri == null) {
            Log.e("Recorder", "Failed to create new MediaStore record.");
            return null;
        }
        return resolver.openFileDescriptor(audioUri, "w"); // "w" for write
    }
}