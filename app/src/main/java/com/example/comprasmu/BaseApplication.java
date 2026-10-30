package com.example.comprasmu;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import net.gotev.uploadservice.UploadServiceConfig;

public class BaseApplication extends Application {

    public static final String UPLOAD_CHANNEL_ID = "upload_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        // 1. Crear el canal de notificaciones (obligatorio para Android 8.0+)
        createNotificationChannel();

        // 2. Configurar la librería de subida con el canal por defecto
        // El tercer parámetro 'true' activa los logs para depuración
        UploadServiceConfig.initialize(this, UPLOAD_CHANNEL_ID, true);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    UPLOAD_CHANNEL_ID,
                    "Subida de Archivos",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}