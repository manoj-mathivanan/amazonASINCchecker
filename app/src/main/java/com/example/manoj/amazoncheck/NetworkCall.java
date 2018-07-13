package com.example.manoj.amazoncheck;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

class NetworkCall extends AsyncTask<String, Void, Set<String>> {

    private Context mContext;

    public NetworkCall(final Context context)
    {
        mContext = context;
    }

    protected Set<String> doInBackground(String... values) {

        HttpURLConnection urlConnection = null;
        Set<String> wrongasinc = new HashSet<String>();

        try{
        for(int i=0;i<values.length;i++) {

                String[] data = values[i].split(Pattern.quote(";"));
                URL url = new URL("https://sellercentral.amazon.com/fba/profitabilitycalculator/productmatches?searchKey=" + data[0] + "&language=en_US");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String output = readStream(in);
                JSONObject reader = new JSONObject(output);
                JSONObject obj = reader.getJSONArray("data").getJSONObject(0);
                if(obj.getDouble("length")!=Double.parseDouble(data[1]))
                    wrongasinc.add(data[0]+" length");
                if(obj.getDouble("width")!=Double.parseDouble(data[2]))
                    wrongasinc.add(data[0]+" width");
                if(obj.getDouble("height")!=Double.parseDouble(data[3]))
                    wrongasinc.add(data[0] + " height");
                if(obj.getDouble("weight")!=Double.parseDouble(data[4]))
                    wrongasinc.add(data[0] + " weight");
        }
        } catch (Exception e) {
            Toast.makeText(mContext,"Error fetching data from amazon",Toast.LENGTH_SHORT).show();
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            long[] v = {500,1000};
            Notification.Builder builder = new Notification.Builder(mContext)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setSound(uri)
                    .setVibrate(v)
                    .setContentTitle("Amazon Check")
                    .setContentText("Error from amazon" + e.getMessage());

            //Get current notification
            Notification mNotification = builder.getNotification();
            NotificationManager mNotificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            //Show the notification
            mNotificationManager.notify(002, mNotification);
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        //wrongasinc.add("Manoj");
        return wrongasinc;

    }

    private String readStream(InputStream in) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
// StandardCharsets.UTF_8.name() > JDK 7
        return result.toString("UTF-8");
    }

    protected void onPostExecute(Set<String> value) {

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        long[] v = {500,1000};



        if(!value.isEmpty()) {

            String NOTIFICATION_CHANNEL_ID = "my_notification_channel";

            NotificationManager mNotificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                // Configure the notification channel.
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setSound(uri)
                    .setVibrate(v)
                    .setContentTitle("Amazon Check")
                    .setContentText("Check ASINC : " + value.toString());

            //Get current notification
            Notification mNotification = builder.getNotification();

            //Show the notification
            mNotificationManager.notify(001, mNotification);
            Helper.lastUpdate = "Error : " + value.toString() + " at " + Calendar.getInstance().getTime();
            Toast.makeText(mContext, value.toString(), Toast.LENGTH_SHORT).show();
        }else{
            Helper.lastUpdate = "ALL ASINC FINE: " + Calendar.getInstance().getTime();
        }

    }
}
