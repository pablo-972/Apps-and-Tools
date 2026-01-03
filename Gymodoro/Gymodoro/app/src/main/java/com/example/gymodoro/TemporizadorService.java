package com.example.gymodoro;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class TemporizadorService extends Service {

    private static final String CHANNEL_ID = "canal_temporizador";
    private static final int NOTIFICATION_ID = 1;
    private int tiempoDescanso;
    private int tiempoTotal;
    private CountDownTimer countDownTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Obtener el tiempo de descanso desde la intent
        tiempoDescanso = Integer.parseInt(intent.getStringExtra("tiempoDescanso"));
        tiempoTotal = tiempoDescanso;

        startTimer();

        startForeground(NOTIFICATION_ID, createNotification());

        return START_STICKY;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(tiempoDescanso * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoDescanso = (int) (millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                showNotification();
                stopSelf();
            }
        };
        countDownTimer.start();
    }

    private Notification createNotification() {
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, new Intent(this, TemporizadorService.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Temporizador en marcha")
                .setContentText("Restando: " + tiempoDescanso + " segundos")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void showNotification() {
        Intent intent = new Intent(this, Temporizador.class);
        intent.putExtra("tiempoDescanso", String.valueOf(tiempoTotal));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("¡Descanso terminado!")
                .setContentText("¡Los " + this.tiempoTotal + " segundos de descanso han terminado!")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Temporizador";
            String description = "Notificaciones para el temporizador";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

