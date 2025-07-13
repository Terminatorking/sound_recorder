package ghazimoradi.soheil.soundrecorder.helpers;

import android.media.MediaPlayer;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.IOException;

public class PlayAudioHelper {

    private static MediaPlayer mediaPlayer;
    private static OnPlaybackCompleteListener playbackCompleteListener;

    public interface OnPlaybackCompleteListener {
        void onPlaybackComplete();
    }

    public static void setOnPlaybackCompleteListener(OnPlaybackCompleteListener listener) {
        playbackCompleteListener = listener;
    }

    public static void playRecordedAudio(ParcelFileDescriptor pfd) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && mediaPlayer.getCurrentPosition() > 0) {
            mediaPlayer.start();
            return;
        }
        stopPlayer();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(pfd.getFileDescriptor());
            mediaPlayer.setOnCompletionListener(
                    mp -> {
                        stopPlayer();
                        if (playbackCompleteListener != null) {
                            playbackCompleteListener.onPlaybackComplete();
                        }
                    }
            );
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("PlayAudioHelper", "Error playing audio", e);
            stopPlayer();
            if (playbackCompleteListener != null) {
                playbackCompleteListener.onPlaybackComplete();
            }
        }
    }

    public static void pausePlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void stopPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}