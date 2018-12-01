package com.example.khanj.wificontract.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.khanj.wificontract.MainActivity;
import com.example.khanj.wificontract.R;

import java.util.Date;
import java.util.List;

public class WifiService extends Service {

    WifiManager wifiManager;
    NotificationManager notiManager;
    NotificationChannelGroup group1;
    ServiceThread thread;
    Notification notifi;
    Date startTime;
    String ssid;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            group1 = new NotificationChannelGroup("channel_group_id", "channel_group_name");
            notiManager.createNotificationChannelGroup(group1);
            NotificationChannel notificationChannel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription("channel description");
            notificationChannel.setGroup("channel_group_id");
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.GREEN);
            //notificationChannel.setVibrationPattern(new long[]{0});
            //notificationChannel.enableVibration(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notiManager.createNotificationChannel(notificationChannel);
        }
        ssid = "\"".concat(intent.getStringExtra("ssid")).concat("\"");
        Log.d("intentBssid", ssid);
        registerIntent();
        long now = System.currentTimeMillis();
        startTime = new Date(now);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;
    }

    public void onDestoy() {
        thread.stopForever();
        thread = null;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                int currentStatus = wifiManager.getWifiState();
                if (currentStatus == WifiManager.WIFI_STATE_DISABLING ||
                        currentStatus == wifiManager.WIFI_STATE_DISABLED) {
                    Log.d("TAG", "stop service");
                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                    for (WifiConfiguration i : list) {
                        if (i.SSID == null) continue;
                        Log.d("delete: ssid", i.SSID);
                        // Log.d("delete bssid", i.BSSID);
                        if (ssid.equals(i.SSID)) {
                            Log.d("deleted", "i.SSID");
                            wifiManager.removeNetwork(i.networkId);
                            // wifiManager.saveConfiguration();
                            break;
                        }
                    }

                    PendingIntent pendingIntent = PendingIntent.getActivity(WifiService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notifi = new Notification.Builder(getApplicationContext(), "channel_name")
                                .setContentTitle("와이파이 사용 종료")
                                .setContentText("와이파이 사용을 종료했습니다")
                                .setSmallIcon(R.drawable.wifi_strength_3)
                                .setTicker("알림")
                                .setChannelId("channel_id")
                                .setContentIntent(pendingIntent)
                                .build();
                    } else {
                        notifi = new Notification.Builder(getApplicationContext())
                                .setContentTitle("와이파이 사용 종료")
                                .setContentText("와이파이 사용을 종료했습니다")
                                .setSmallIcon(R.drawable.wifi_strength_3)
                                .setTicker("알림")
                                .setContentIntent(pendingIntent)
                                .build();
                    }
                    notifi.defaults = Notification.DEFAULT_SOUND;
                    notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                    notifi.flags = Notification.FLAG_AUTO_CANCEL;
                    notiManager.notify(777, notifi);

                    Toast.makeText(getApplicationContext(), "와이파이 사용을 종료합니다", Toast.LENGTH_SHORT).show();
                    Intent serviceIntent = new Intent(getApplicationContext(), WifiService.class);
                    getApplicationContext().stopService(serviceIntent);
                    notiManager.cancelAll(); // notificationManager.cancel(NOTIFICATION_ID);
                    getApplicationContext().unregisterReceiver(receiver);
                    thread.stopForever(); // 임시 종료
                    thread = null; // 임시 종료
                    return;
                }
            }
        }
    };

    public void registerIntent() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter intentFilter = new IntentFilter(wifiManager.NETWORK_STATE_CHANGED_ACTION);
        getApplicationContext().registerReceiver(receiver, intentFilter);
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            long now = System.currentTimeMillis();
            Date currentTime = new Date(now);
            long diff = currentTime.getTime() - startTime.getTime();
            long sec = diff / 1000; // 초 계산

            // 1분 간격으로 노티 띄우기
            if (true) {
                Intent intent = new Intent(WifiService.this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(WifiService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notifi = new Notification.Builder(getApplicationContext(), "channel_name")
                            .setContentTitle("와이파이 이용중")
                            .setContentText(Long.toString(sec) + "초 이용")
                            .setSmallIcon(R.drawable.wifi_strength_3)
                            .setTicker("알림")
                            .setChannelId("channel_id")
                            .setContentIntent(pendingIntent)
                            .build();
                } else {
                    notifi = new Notification.Builder(getApplicationContext())
                            .setContentTitle("와이파이 이용중")
                            .setContentText("test")
                            .setSmallIcon(R.drawable.wifi_strength_3)
                            .setTicker("알림")
                            .setContentIntent(pendingIntent)
                            .build();
                }
                notifi.defaults = Notification.DEFAULT_SOUND;
                notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                notifi.flags = Notification.FLAG_AUTO_CANCEL;
                notiManager.notify(777, notifi);
                Log.d(getClass().getName(), "notification on");
            }
            if (sec == 0) return;
            if (sec % 60 == 0) {
                Log.d("TAG", "stop service");
                Intent intent = new Intent(getApplicationContext(), WifiService.class);
                getApplicationContext().stopService(intent);
                notiManager.cancelAll(); // notificationManager.cancel(NOTIFICATION_ID);로 대체해야 함
                thread.stopForever(); // 임시 종료
                thread = null; // 임시 종료
                return;
            }
            // refresh advertisement
            if (sec % 30 == 0) {
                Log.d("TAG", "advertisement call again");
                Intent advertisementIntent = new Intent(getApplicationContext(), MainActivity.class);
                advertisementIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(advertisementIntent);
            }
        }
    }

}