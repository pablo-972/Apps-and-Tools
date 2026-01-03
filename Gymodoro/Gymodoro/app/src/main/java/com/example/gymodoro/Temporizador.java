package com.example.gymodoro;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Temporizador extends AppCompatActivity {

    private static final String CHANNEL_ID = "canal_temporizador";
    private static final int NOTIFICATION_ID = 1;

    private TextView temporizador;
    private int tiempoDescanso;

    private int tiempoTotal;

    private Button reiniciar, salir;

    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporizador);

        //Inicializamos
        Bundle bundle = getIntent().getExtras();
        tiempoDescanso = Integer.parseInt(bundle.getString("tiempoDescanso"));
        tiempoTotal = tiempoDescanso;
        temporizador = findViewById(R.id.tiempodescanso);
        temporizador.setText(String.valueOf(tiempoDescanso));
        reiniciar = findViewById(R.id.reiniciar);
        salir = findViewById(R.id.salir);
        createNotificationChannel();

        startTimer();

        //Boton reiniciar
        reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                tiempoDescanso = tiempoTotal;
                temporizador.setText(String.valueOf(tiempoDescanso));
                startTimer();
            }
        });

        //Boton salir
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Temporizador";
            String description = "Notificaciones para el temporizador";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void stopTimer(){
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(tiempoDescanso * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoDescanso = (int) (millisUntilFinished / 1000);
                temporizador.setText(String.valueOf(tiempoDescanso));
            }

            @Override
            public void onFinish() {
                showNotification();
            }
        };
        countDownTimer.start();
    }

    private void showNotification() {
        Intent intent = new Intent(this, Temporizador.class);
        intent.putExtra("tiempoDescanso", String.valueOf(tiempoTotal));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
}

