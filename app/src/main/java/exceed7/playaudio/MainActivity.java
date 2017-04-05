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
                    playWithAudioTrack();
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

        for (int rate : new int[] {8000, 11025, 16000, 22050, 44100, 48000}) {  // add the rates you wish to check against
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                Log.d("SAMPLE RATE TEST", String.valueOf(rate) + " supported.");

            }
        }
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
                .setFlags(AudioAttributes.FLAG_LOW_LATENCY)  //This is Nougat+ only (API 25) comment if you have lower
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

    AudioTrack at;
    byte[] music = null;
    public boolean playWithAudioTrack() {

        try {
            InputStream is = getResources().openRawResource(R.raw.assist);
            music = new byte[(int)getResources().openRawResourceFd(R.raw.assist).getLength()];
            is.read(music);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_LOW_LATENCY) //This is Nougat+ only (API 25) comment if you have lower
                .build();

        AudioFormat af =  new AudioFormat.Builder()
                .setSampleRate(44100)
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
