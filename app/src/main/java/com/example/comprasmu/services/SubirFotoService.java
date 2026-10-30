package com.example.comprasmu.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.comprasmu.NavigationDrawerActivity;
import com.example.comprasmu.data.ComprasDataBase;
import com.example.comprasmu.data.dao.ImagenDetalleDao;
import com.example.comprasmu.data.modelos.ImagenDetalle;
import com.example.comprasmu.data.remote.SubirFoto;
//import com.example.comprasmu.data.remote.SubirFotoAlt;
import com.example.comprasmu.data.repositories.CorreccionRepoImpl;
import com.example.comprasmu.data.repositories.ImagenDetRepositoryImpl;
import com.example.comprasmu.data.repositories.InfEtapaDetRepoImpl;
import com.example.comprasmu.utils.ComprasLog;
import com.example.comprasmu.utils.Constantes;

import java.text.SimpleDateFormat;
import java.util.Random;

/****un servicio x foto****/
public class SubirFotoService extends IntentService
{
    private static final String TAG = "SubirFotoService";
    public static String EXTRA_INDICE="comprasmu.extraindice";
    InfEtapaDetRepoImpl etapadetRepo;
    CorreccionRepoImpl correccionRepo;

    String userId, indiceimagen, cadenarutas;
    ImagenDetalle imagenSubir;
    //PostInformeViewModel pvm;
    private String tipo; //para saber que tabla actualizare
    ComprasLog milog;

    public static final String ACTION_UPLOAD_IMG = "com.example.comprasmu.intentservice.action.PROGRESO";
    public static final String ACTION_UPLOAD_ETA = "comprasmu.intentservice.action.etapa";
    public static final String ACTION_UPLOAD_COR = "comprasmu.intentservice.action.correccion";
    public static final String ACTION_UPLOAD_LISTA = "com.example.comprasmu.intentservice.action.lista";

    public static final String EXTRA_IMG_PATH = "com.example.comprasmu.intentservice.extra.EXTRA_IMG_PATH";
    public static final String EXTRA_IMAGE_ID = "com.example.comprasmu.intentservice.extra.EXTRA_IMAGE_ID";
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    public SubirFotoService()
    {
        super("SubirFotoService");
        //  pvm=new PostInformeViewModel(getApplicationContext());
        // pvm.iniciarConexiones();
    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        milog=ComprasLog.getSingleton();
        //  Log.d(TAG,"action");
        if (intent != null)
        {
            final String action = intent.getAction();
            // intent.setAction(ACTION_UPLOAD_IMG);

            if (ACTION_UPLOAD_IMG.equals(action)) //para informe compra
            {
                //  Log.d(TAG,"action"+action);
                imagenSubir=new ImagenDetalle();
                imagenSubir.setRuta(intent.getStringExtra(EXTRA_IMG_PATH));
                imagenSubir.setId(intent.getIntExtra(EXTRA_IMAGE_ID,0));
                indiceimagen=intent.getStringExtra(EXTRA_INDICE);
                handleUploadImg();
            }
            else

            {
                //  Log.d(TAG,"action"+action);
                imagenSubir=new ImagenDetalle();
                imagenSubir.setRuta(intent.getStringExtra(EXTRA_IMG_PATH));
                imagenSubir.setId(intent.getIntExtra(EXTRA_IMAGE_ID,0));
                indiceimagen=intent.getStringExtra(EXTRA_INDICE);
                handleUploadImg2(intent);
            }
        }
    }
    private void handleUploadImg()
    {


        // Instanciar y registrar un Observador
        SubirFotoListener objObservador  = new SubirFotoListener();

        try {
            // notificar();
            SubirFoto sf = new SubirFoto();
            sf.agregarObservador(objObservador);
            //   Log.d(TAG,"ahora si voy a subir*"+imagenSubir.getRuta());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dir=   this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/";
            ImagenDetalleDao imagenDetalleDao= ComprasDataBase.getInstance(this).getImagenDetalleDao();
            ImagenDetRepositoryImpl idrepo= ImagenDetRepositoryImpl.getInstance(imagenDetalleDao);
            milog.info(TAG,"handleUploadImg","ahora si voy a subir ruta:"+imagenSubir.getRuta());

            sf.subirFoto(Constantes.CLAVEUSUARIO,dir, imagenSubir,indiceimagen, this,idrepo);

        }catch (Exception ex){

            milog.grabarError(TAG+"error"+ex.getMessage());
            ex.printStackTrace();
        }


    }
    private void handleUploadImg2(Intent intent)
    {

        // Instanciar y registrar un Observador
        SubirFotoListener objObservador  = new SubirFotoListener();

        try {
            // notificar();
            String action = intent.getAction();
            SubirFoto sf = new SubirFoto();
            sf.agregarObservador(objObservador);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dir=   this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/";
            if(action.equals(ACTION_UPLOAD_COR)){
                tipo="correccion";
                correccionRepo=new CorreccionRepoImpl(getApplicationContext());
            }
            if(action.equals(ACTION_UPLOAD_ETA)){
                tipo="etapa";
                etapadetRepo=new InfEtapaDetRepoImpl(getApplicationContext());
            }
            milog.info(TAG,"handleUploadImg2","ahora si voy a subir ruta:"+imagenSubir.getRuta()+" tipo:"+tipo);

            sf.subirFotoGen(Constantes.CLAVEUSUARIO,dir, imagenSubir,indiceimagen, this,tipo);

        }catch (Exception ex){

            milog.grabarError(TAG+" error"+ex.getMessage());
            ex.printStackTrace();
        }


    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        //Log.e(TAG,"onCreate");
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        milog.grabarError(TAG+" se eliminó el servicio");
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //notificationManager.cancel(0);
        milog.grabarError(TAG+" task removed");
    }



    public void sendBroadcastMeaasge(String message) {
        Intent localIntent = new Intent("my.own.broadcast");
        localIntent.putExtra("result", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void notificar(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription("no sound");
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        assert notificationManager != null;
        notificationBuilder = new NotificationCompat.Builder(this, "id")
                .setSmallIcon(android.R.drawable.stat_sys_upload
                )
                .setContentTitle("Compras")
                .setContentText("Subiendo imagen")
                .setDefaults(0)
                .setAutoCancel(true);
        notificationManager.notify(m, notificationBuilder.build());
        sendBroadcastMeaasge("terminé");
    }




    private void sendProgressUpdate(boolean downloadComplete) {

        Intent intent = new Intent(NavigationDrawerActivity.PROGRESS_UPDATE);
        intent.putExtra("uploadComplete", downloadComplete);
        LocalBroadcastManager.getInstance(SubirFotoService.this).sendBroadcast(intent);
    }
    /*
 Impresión del progreso en tiempo real
   */
    private void updateNotification(int currentProgress) {


        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText("Enviado: " + currentProgress + "%");
        notificationManager.notify(0, notificationBuilder.build());
    }
    private void onDownloadComplete(boolean downloadComplete) {
        sendProgressUpdate(downloadComplete);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_upload_done);

        notificationBuilder.setContentText("Envio completado");
        notificationManager.notify(0, notificationBuilder.build());

    }



    public class SubirFotoListener {

        boolean downloadComplete = false;

        public SubirFotoListener() {


        }


        public void onProgress(int progress)
        {
            // updateNotification(progress);
        }
        public void onSuccess(){

            downloadComplete = true;
            //  onDownloadComplete(downloadComplete);
            //todo
            //veo si puedo cambiar el estatus del informe
            // pvm.actualizarEstatuscoloInf(0);
            Log.i(TAG,"ya termino");
            Constantes.SINCRONIZANDO=0;
        }
        public void onSuccess2(ImagenDetalle imagen){
            downloadComplete = true;
            Log.i(TAG," onSuccess2 ya termino tipo:"+tipo);
            if(tipo.equals("correccion"))
                //actualizo correccion
                correccionRepo.actualizarEstatusSync(imagen.getId(), Constantes.ENVIADO);
            else
            if(tipo.equals("etapa")){
                etapadetRepo.actualizarEstatusSync(imagen.getId(), Constantes.ENVIADO);
            }
            //  onDownloadComplete(downloadComplete);
            Constantes.SINCRONIZANDO=0;
        }
        public void onSuccessEtapa(ImagenDetalle imagen){
            downloadComplete = true;


        }

    }

}
