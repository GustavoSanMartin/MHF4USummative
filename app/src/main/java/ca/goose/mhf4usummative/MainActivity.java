package ca.goose.mhf4usummative;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    EditText boxID;
    TextView counter;
    Button slave;
    Button master;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //declarations
        slave = (Button) findViewById(R.id.slave);
        master = (Button) findViewById(R.id.master);
        boxID = (EditText) findViewById(R.id.boxNum);
        counter = (TextView) findViewById(R.id.count);

        slave.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        new CountDownTimer(10000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                counter.setText(" " + millisUntilFinished / 1000);
                            }
                            public void onFinish() {
                                counter.setText("10");

                                Log.v("H", boxID.getText().toString());
                                Intent slave = new Intent(MainActivity.this, ChildActivity.class);
                                slave.putExtra("boxID", boxID.getText().toString());//send ID to other screen
                                startActivity(slave);
                            }
                        }.start();
                    }
                }
        );
        master.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent master = new Intent(MainActivity.this, MasterActivity.class);
                        startActivity(master);
                    }
                }
        );
    }
}
