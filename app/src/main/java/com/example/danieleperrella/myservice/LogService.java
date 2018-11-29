package com.example.danieleperrella.myservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import com.github.nkzawa.emitter.Emitter;

public class LogService extends IntentService
{
    public static String TEXT;
    public static boolean vai = true;

    public LogService()
    {
        super("LogService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSocket.connect();

        //auth(0);
        mSocket.on("notify", onNotify);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        System.out.println("-------- service " + pm.isIgnoringBatteryOptimizations(this.getPackageName())+" --------");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("!!!!!!STARTCOMMAND!!!!!!");

        super.onStartCommand(intent, flags, startId);

        return IntentService.START_STICKY;
        ///// which return is better to keep the service running untill explicitly killed. contrary to system kill.
        ///// http://developer.android.com/reference/android/app/Service.html#START_FLAG_REDELIVERY

        //notes:-//  if you implement onStartCommand() to schedule work to be done asynchronously or in another thread,
        //then you may want to use START_FLAG_REDELIVERY to have the system re-deliver an Intent for you so that it does not get lost if your service is killed while processing it
    }

    private Emitter.Listener onNotify = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            TEXT = args[0].toString();
            Log.d("NOTIFY", TEXT);
            Intent in = new Intent();
            in.putExtra("MSG",TEXT);
            in.setAction("NOW");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);

        }

    };


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.10:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onHandleIntent(Intent i)
    {

        int n=0;

        while(vai)
        {

            Log.i("PROVA SERVICE", "Evento n."+n++);

            try {

                Intent in = new Intent();
                in.putExtra("MSG","-");
                in.setAction("NOW");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);

                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            { }
        }


    }
    @Override
    public void onDestroy()
    {
        //mSocket.disconnect();
        Log.i("PROVA SERVICE", "Distruzione Service");

        Intent intent = new Intent();
        intent.putExtra("MSG", "R");
        sendBroadcast(intent);
    }

}
