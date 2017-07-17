package ca.goose.mhf4usummative;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ChildActivity extends AppCompatActivity {
    private SensorManager sensormanager;
    private boolean triggered = false;
    private SensorEventListener sensorlistener;
    private String ID;
    TextView brightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        Log.v("H", "ChildActivity create");

        ID = getIntent().getExtras().getString("boxID","0");
        sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);
        brightness = (TextView) findViewById(R.id.brightness);

        triggered = false;

        sensorlistener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] values = event.values;
                brightness.setText("" + values[0]);

                if (values[0] > 10 && !triggered) { //if bright (box opened), contact server
                    //wakeLock.release();
                    new update().execute();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }
    @Override
    public void onResume(){
        super.onResume();
        sensormanager.registerListener(sensorlistener, sensormanager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    public void onStop(){
        super.onStop();
        sensormanager.unregisterListener(sensorlistener);
    }

    class update extends AsyncTask<Object, Object, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            triggered = true;

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("https://script.google.com/macros/s/AKfycbxN5AHSb3PdaNXVAh_6B_" +
                        "yXnGGdcLryTWixYZtpe4ZEqA1rF0wy/exec?pCode=" + ID + "&pType=w");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                Log.v("H", readStream(in));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }
        private String readStream(InputStream is) {
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while(i != -1) {
                    bo.write(i);
                    i = is.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }
    }
}
