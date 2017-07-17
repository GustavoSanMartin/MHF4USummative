package ca.goose.mhf4usummative;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MasterActivity extends AppCompatActivity {
    int index;
    String substrResponse;
    String response;
    String blank;
    TextView status;
    TextView countdown;
    EditText code;
    Button submit;
    View root;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        status = (TextView) findViewById(R.id.status);
        countdown = (TextView) findViewById(R.id.countdown);
        code = (EditText) findViewById(R.id.code_input);
        submit = (Button)findViewById(R.id.submit);
        root = findViewById(R.id.master).getRootView();
        mp = MediaPlayer.create(this, R.raw.alarm);
        index = 0;
        substrResponse = "E";
        response = " ";
        blank = " ";

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new read().execute();

        submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("H", "" + code.getText());
                        if (code.getText().toString().equals("3469")) {
                            Log.v("H", "if");
                            countdown.setVisibility(View.INVISIBLE);
                            root.setBackgroundColor(Color.GREEN);
                            mp.stop();
                        }
                        else {
                            Toast.makeText(MasterActivity.this, "Incorrect Code", Toast.LENGTH_SHORT).show();
                            code.setText("");
                        }
                    }
                }
        );
    }

    protected void setResponse(String wantedResponse){
        substrResponse = wantedResponse;
        if (!substrResponse.equals("E")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mp.start();
                    root.setBackgroundColor(Color.RED);
                    status.setText("box " + substrResponse + " opened");
                    code.setVisibility(View.VISIBLE);
                    countdown.setVisibility(View.VISIBLE);

                    new CountDownTimer(120000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            countdown.setText("" + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            countdown.setText("dead");
                        }
                    }.start();
                }
            });
        }
    }

    class read extends AsyncTask<Object, Object, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            HttpURLConnection urlConnection;
            try {
                URL url = new URL("https://script.google.com/macros/s/AKfycbxN5AHSb3PdaNXVAh_6B_" +
                                  "yXnGGdcLryTWixYZtpe4ZEqA1rF0wy/exec?pCode=0&pType=r");
                while (substrResponse.equals("E")) {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    response = readStream(in);
                    Log.v("H", response);
                    index = response.indexOf("!MainActivity!");
                    Log.v("H", "" + index);
                    setResponse(response.substring(index + 7, index + 8));
                    Log.v("H", substrResponse);
                }
                Log.v("H", "out");
            } catch (MalformedURLException e) {
                Log.v("H", "malformed");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }

        private String readStream(InputStream is) {
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while (i != -1) {
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
