package exceed7.playaudio;

import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button b = (Button) findViewById(R.id.button);
        b.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playWithSoundPool();
                }
                return true;
            }
        });

        Button c = (Button) findViewById(R.id.button2);
        c.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playWithAudioTrackNormal();
                }
                return true;
            }
        });

        Button d = (Button) findViewById(R.id.button3);
        d.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playWithAudioTrackLowLatency();
                }
                return true;
            }
        });

        Button f = (Button) findViewById(R.id.button4);
        f.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playWithAudioTrackNormal48();
                }
                return true;
            }
        });

        Button g = (Button) findViewById(R.id.button5);
        g.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playWithAudioTrackLowLatency48();
                }
                return true;
            }
        });


        boolean hasLowLatencyFeature =
                getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_LOW_LATENCY);

        boolean hasProFeature =
                getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_PRO);
        Log.d("LOW LATENCY FEATURE : ",String.valueOf(hasLowLatencyFeature));
        Log.d("PRO AUDIO FEATURE : ", String.valueOf(hasProFeature));

        AudioManager myAudioMgr = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
        String nativeSampleRate = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        Log.d("NATIVE SAMPLING RATE : ", nativeSampleRate);

        String optimalBufferSize = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        Log.d("OPTIMAL BUFFER SIZE : ", optimalBufferSize);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void playWithSoundPool()
    {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                //.setFlags(AudioAttributes.FLAG_LOW_LATENCY)  //This is Nougat+ only (API 25) comment if you have lower
                .build();

        SoundPool sp = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(attributes)
                .build();
        int sound = sp.load(getApplicationContext(), R.raw.assist, 1);
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1, 1, 0, 0, 1);
            }
        });
    }

    public void playWithAudioTrackNormal() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        playWithAudioTrack(attributes, R.raw.assist, 44100);
    }

    public void playWithAudioTrackLowLatency() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_LOW_LATENCY) //This is Nougat+ only (API 25) comment if you have lower
                .build();
        playWithAudioTrack(attributes,R.raw.assist,44100);
    }

    public void playWithAudioTrackNormal48() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        playWithAudioTrack(attributes,R.raw.assist48,48000);
    }

    public void playWithAudioTrackLowLatency48() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        playWithAudioTrack(attributes,R.raw.assist48,48000);
    }

    AudioTrack at;
    byte[] music = null;
    public boolean playWithAudioTrack(AudioAttributes attributes, int audio, int samplingRate) {

        try {
            InputStream is = getResources().openRawResource(audio);
            music = new byte[(int)getResources().openRawResourceFd(audio).getLength()];
            is.read(music);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        AudioFormat af =  new AudioFormat.Builder()
                .setSampleRate(samplingRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .build();

        at = new AudioTrack.Builder()
                .setAudioAttributes(attributes)
                .setBufferSizeInBytes(200000)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .setSessionId(AudioManager.AUDIO_SESSION_ID_GENERATE)
                .setAudioFormat(af)
        .build();

        at.write(music, 46, music.length - 46);

        try{
            at.play();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        return true;

    }

}
