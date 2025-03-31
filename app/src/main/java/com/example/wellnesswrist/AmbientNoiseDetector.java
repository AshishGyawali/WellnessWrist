package com.example.wellnesswrist;

import android.content.Context;
import android.media.MediaRecorder;
import android.widget.Toast;

import java.io.IOException;

public class AmbientNoiseDetector {
    private Context context;
    private MediaRecorder recorder;

    public AmbientNoiseDetector(Context context) {
        this.context = context;
    }

    public void detectNoiseLevel() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile("/dev/null");
            recorder.prepare();
            recorder.start();

            Thread.sleep(1000); // Let it gather some data
            int maxAmplitude = recorder.getMaxAmplitude();

            if (maxAmplitude > 25000) {
                Toast.makeText(context, "It's too loud around you!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                }
            } catch (Exception ignored) {}
        }
    }
}
