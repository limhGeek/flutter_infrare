package com.limh.flutter_infrare;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author limh
 * @function
 * @date 2020/6/5 16:23
 */
class AudioUtils {
    private final String TAG = "AudioUtils";
    private final double freqOfTone = 19000;
    private int sampleRate = 44100;
    private static AudioUtils instance;
    private int buffSize;
    private byte[] generatedSnd;
    private AudioTrack audioTrack;
    private List<Integer> audioList;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public AudioUtils() {
        buffSize = AudioTrack.getMinBufferSize(this.sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT) * 4;
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT, buffSize, AudioTrack.MODE_STREAM);
    }

    public static AudioUtils getInstance() {
        if (null == instance) {
            synchronized (AudioUtils.class) {
                if (null == instance) {
                    instance = new AudioUtils();
                }
            }
        }
        return instance;
    }

    public void play(List<Integer> audioList) {
        this.audioList = audioList;
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                setSinWave();
                startPlay();
            }
        });
    }

    /**
     * 构造正弦波并且装载
     */
    private void setSinWave() {
        Log.d(TAG, "数据缓存区大小：" + buffSize);
        double[] sample = new double[buffSize];
        for (int i = 0; i < buffSize; i++) {
            sample[i] = Math.sin(2 * Math.PI * i * (freqOfTone / sampleRate));
        }
        int idx = 0;
        generatedSnd = new byte[buffSize * 4];
        for (final double dVal : sample) {
            final short val = (short) ((dVal * 32767));
            final short valMinus = (short) -val;
            //左声道
            generatedSnd[idx] = (byte) (val & 0x00ff);
            generatedSnd[idx + 1] = (byte) ((val & 0xff00) >>> 8);
            //16位双声道  右声道
            generatedSnd[idx + 2] = (byte) (valMinus & 0x00ff);
            generatedSnd[idx + 3] = (byte) ((valMinus & 0xff00) >>> 8);
            idx = idx + 4;
        }
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        Log.d(TAG,"开始播放:"+audioList.toString());
        List<Byte> listByte = new ArrayList<>();
        for (int j = 0; j < audioList.size(); j++) {
            int d = audioList.get(j);
            final int points = (int) ((((double) d / 1000000.0) * sampleRate) * 4);
            if (j % 2 == 0) {
                for (int i = 0; i < points; i++) {
                    listByte.add(generatedSnd[i]);
                }
            } else {
                for (int i = 0; i < points; i++) {
                    listByte.add((byte) 0);
                }
            }
        }
        try {
            audioTrack.play();
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
        }
        audioTrack.write(list2byte(listByte), 0, listByte.size());
        audioTrack.flush();
    }

    private byte[] list2byte(List<Byte> list) {
        if (list == null) {
            return null;
        } else {
            list.size();
        }
        byte[] bytes = new byte[list.size()];
        int i = 0;
        for (Byte aByte : list) {
            bytes[i] = aByte;
            i++;
        }
        return bytes;
    }
}
