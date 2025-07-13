package ghazimoradi.soheil.soundrecorder.helpers;

import android.media.MediaRecorder;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.IOException;

public class RecordeAudioHelper {

    private MediaRecorder recorder;
    private boolean isRecording = false;

    public boolean isRecording() {
        return this.isRecording;
    }

    public void recordeAudio(ParcelFileDescriptor pfd) {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.OGG);

            if (pfd != null) {
                recorder.setOutputFile(pfd.getFileDescriptor());
            } else {
                Log.e("Recorder", "ParcelFileDescriptor is null for MediaStore URI.");
                return;
            }

            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.OPUS);
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Log.e("Recorder error", e.getMessage(), e);
        }
    }

    public void toggleRecording() {
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void stopRecording() {
        isRecording = false;
        recorder.release();
    }

    private void startRecording() {
        isRecording = true;
    }
}