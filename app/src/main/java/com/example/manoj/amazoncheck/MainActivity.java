package com.example.manoj.amazoncheck;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Intent mServiceIntent;
    private TimeService mTimeService;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            registerReceiver(new RestartBroadcastReceiver(),
                    new IntentFilter("com.example.manoj.amazoncheck.broadcast"));
        }catch(Exception e){

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this;
        mTimeService = new TimeService(getCtx());
        mServiceIntent = new Intent(getCtx(), mTimeService.getClass());

        EditText et = (EditText) findViewById(R.id.editText2);
        String str = et.getText().toString();

        // Intent intent = new Intent(MainActivity.this, TimeService.class);
        //intent.putExtra("lines",str);
        //startService(intent);
        Helper.lines = str.split("\\r?\\n");

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        final TextView tv = findViewById(R.id.textView2);
        if(Helper.lastUpdate!=null)
            tv.setText(Helper.lastUpdate);

        final Button button = (Button)findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView result = findViewById(R.id.textView2);
                result.setText("Fetching and validating details. You will be notified if any error");

                EditText et = (EditText) findViewById(R.id.editText2);
                String str = et.getText().toString();

               // Intent intent = new Intent(MainActivity.this, TimeService.class);
                //intent.putExtra("lines",str);
                //startService(intent);
                Helper.lines = str.split("\\r?\\n");
                //if (!isMyServiceRunning(mTimeService.getClass())) {
                    startService(mServiceIntent);
                //}
            }
        });

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

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


}
