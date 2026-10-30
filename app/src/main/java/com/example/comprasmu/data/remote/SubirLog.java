package com.example.comprasmu.data.remote;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.comprasmu.BaseApplication;
import com.example.comprasmu.data.repositories.ImagenDetRepositoryImpl;
import com.example.comprasmu.services.SubirLogService;
import com.example.comprasmu.utils.Constantes;
import net.gotev.uploadservice.data.UploadNotificationStatusConfig;
import net.gotev.uploadservice.data.UploadNotificationConfig;
import net.gotev.uploadservice.network.ServerResponse;
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest;


import java.io.FileNotFoundException;
import java.util.ArrayList;


public class SubirLog {
    MultipartUploadRequest upload;
    String URL_SUBIRPICTURE="api/public/subirlog";
    private SubirLogService.SubirLogListener observadores;
    ImagenDetRepositoryImpl idrepo;
    private final String TAG="SubirLog";

   public void agregarObservador(SubirLogService.SubirLogListener obs){
       observadores=obs;
   }
    public void notificarObservadores(String mensaje)
    {
        // Enviar la notificación a cada observador a través de su propio método

        observadores.onSuccess();

    }

    public void notificarAvance(int progress)
    {
        // Enviar la notificación a cada observador a través de su propio método

        observadores.onProgress(progress);

    }



    public void subirlog( String idusuario,String arch, String nombrearch, Context context) throws Exception {

//filenameGaleria=getFilename();

        try {

            String uploadFileArrayList=arch;
            Log.d(TAG,"ahora si voy a subir"+uploadFileArrayList+"  "+Constantes.URLSERV+URL_SUBIRPICTURE);
            String serverurl="";
            if (Build.PRODUCT.contains ("sdk")||Build.MODEL.contains ("2006C3MG2")) {//pruebas y el lenovo
                serverurl=Constantes.URLPRUEBAS1;

            }else
            {
                serverurl=Constantes.URLSERV;

            }
                upload= new MultipartUploadRequest(context, serverurl+URL_SUBIRPICTURE)
                    .setMaxRetries(2)
                   // .setNotificationConfig(new UploadNotificationConfig())
                        // Configuración de la notificación (opcional pero recomendada)
                        .setNotificationConfig((context1, uploadId) -> {
                            // Configuración para cuando está en PROGRESO
                            UploadNotificationStatusConfig progress = new UploadNotificationStatusConfig(
                                    "Subiendo archivo...", // Título
                                    "Por favor espera"    // Mensaje

                            );

                            // Configuración para cuando tiene ÉXITO
                            UploadNotificationStatusConfig success = new UploadNotificationStatusConfig(
                                    "Muesmerc: Envío completado",
                                    "El log se subió correctamente"

                            );

                            // Configuración para cuando hay ERROR
                            UploadNotificationStatusConfig error = new UploadNotificationStatusConfig(
                                    "Muesmerc: Error al enviar",
                                    "No se pudo subir el log de errores"

                            );

                            // Configuración para cuando se CANCELA
                            UploadNotificationStatusConfig cancelled = new UploadNotificationStatusConfig(
                                    "Muesmerc: Envío cancelado",
                                    "Se ha cancelado la subida"

                            );

                            return new UploadNotificationConfig(
                                    BaseApplication.UPLOAD_CHANNEL_ID,
                                    false, // isRingToneEnabled
                                    progress,
                                    success,
                                    error,
                                    cancelled
                            );
                        })

                        .addParameter ("usuario", idusuario)
                    .addParameter ("archivo", nombrearch);
                     // .setUtf8Charset();
          /*  upload.setDelegate(new UploadStatusDelegate(){

                @Override
                public void onProgress(UploadInfo uploadInfo) {
                    Log.d(TAG,"Subiendo log....."+uploadFileArrayList);
                    notificarAvance(uploadInfo.getProgressPercent());
                }

                @Override
                public void onError(UploadInfo uploadInfo, Exception exception) {
                    Log.d(TAG,"Error al subir"+uploadInfo.getUploadedBytes());
                }

                @Override
                public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                   String mensaje="TODO BIEN";
                    if(serverResponse.getHttpCode()==200) {
                        Log.d(TAG,"todo bien");

                    }else { //hubo un error
                        Log.d(TAG,"HUBO UN ERROR");
                        //lo registro en el log
                        mensaje="HUBO UN ERROR";
                            }

                      notificarObservadores(mensaje);


                }

                @Override
                public void onCancelled(UploadInfo uploadInfo) {
                    Log.d("ejemplo", "se cancelo");
                }


            });*/



            addFileToUploadRequest(uploadFileArrayList,1);

            upload.startUpload();
        } catch (Exception e) {
            throw e;
        }

    }



    public void addFileToUploadRequest(String uf,int i) throws Exception {
        try {
            upload.addFileToUpload(uf, "file").setMethod("POST");
        } catch (FileNotFoundException e) {
            throw new Exception("No se encontró el archivo "+uf+" Verifique");
        }
    }


}
