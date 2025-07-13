package ghazimoradi.soheil.soundrecorder;

import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import ghazimoradi.soheil.soundrecorder.helpers.CounterHelper;
import ghazimoradi.soheil.soundrecorder.helpers.PlayAudioHelper;
import ghazimoradi.soheil.soundrecorder.helpers.RecordeAudioHelper;
import ghazimoradi.soheil.soundrecorder.helpers.SaveRecordedFilesHelper;

public class MainActivity extends ActivityEnhanced
        implements PlayAudioHelper.OnPlaybackCompleteListener {

    private TextView txtTime;
    private ImageButton btnRecorde;
    private ImageButton btnPlay;
    private ParcelFileDescriptor pfd;
    private CounterHelper.CounterThread counterThread;
    private final RecordeAudioHelper recordeAudioHelper = new RecordeAudioHelper();

    private void findViews() {
        btnRecorde = findViewById(R.id.btnRecorde);
        btnPlay = findViewById(R.id.btnPlay);
        txtTime = findViewById(R.id.txtTime);
    }

    private void setUpView() {
        btnPlay.setVisibility(View.GONE);
        PlayAudioHelper.setOnPlaybackCompleteListener(this);
        btnRecorde.setOnClickListener(v -> {
                    try {
                        recordeAudioHelper.toggleRecording();
                        if (recordeAudioHelper.isRecording()) {
                            if (PlayAudioHelper.isPlaying()) {
                                PlayAudioHelper.stopPlayer();
                                btnPlay.setImageResource(R.drawable.play);
                            }
                            btnPlay.setVisibility(View.GONE);
                            counterThread = new CounterHelper.CounterThread(
                                    () -> txtTime.setText(
                                            CounterHelper.convertToTime(counterThread.getCounter())
                                    )
                            );
                            counterThread.resetCounter();
                            counterThread.startCounter();
                            String timeStamp = new SimpleDateFormat(
                                    "yyyyMMdd_HHmmss", Locale.getDefault()
                            ).format(new Date());
                            String fileName = "Recorded_" + timeStamp + ".ogg";

                            pfd = SaveRecordedFilesHelper.recordedFileDescriptor(this, fileName);
                            recordeAudioHelper.recordeAudio(pfd);
                            btnRecorde.setImageResource(R.drawable.recorder_on);
                        } else {
                            counterThread.stopCounter();
                            btnPlay.setVisibility(View.VISIBLE);
                            btnRecorde.setImageResource(R.drawable.recorder_off);
                        }
                    } catch (FileNotFoundException e) {
                        Log.e("FileDescriptorError", e.getMessage(), e);
                    }
                }
        );
        btnPlay.setOnClickListener(
                v -> {
                    if (PlayAudioHelper.isPlaying()) {
                        PlayAudioHelper.pausePlayer();
                        btnPlay.setImageResource(R.drawable.play);
                    } else {
                        PlayAudioHelper.playRecordedAudio(pfd);
                        btnPlay.setImageResource(R.drawable.pause);
                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (super.hasRecordPermission()) {
            findViews();
            setUpView();
        }
    }

    @Override
    public void onPlaybackComplete() {
        runOnUiThread(() ->
                btnPlay.setImageResource(R.drawable.play)
        );
    }
}