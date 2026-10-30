package com.example.comprasmu.ui.mantenimiento;


import android.content.Intent;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import com.example.comprasmu.R;
import com.example.comprasmu.services.SubirLogService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class LeerLogActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leer_log);

        mTextView = findViewById(R.id.txtlllog);
        Log.d("hola","hola");

        //envio el log al servidor que lo guardará en la carpeta del usuario

        Intent msgIntent = new Intent(this, SubirLogService.class);
        msgIntent.putExtra(SubirLogService.EXTRA_LOG_PATH, "compraslog.txt");
        msgIntent.setAction(SubirLogService.ACTION_UPLOAD_LOG);
        startService(msgIntent);

        pruebalog();
         msgIntent = new Intent(this, SubirLogService.class);
        msgIntent.putExtra(SubirLogService.EXTRA_LOG_PATH, "compraslogcat.txt");
        msgIntent.setAction(SubirLogService.ACTION_UPLOAD_LOG);
        startService(msgIntent);
        respaldarBd();
        Toast.makeText(getApplicationContext(), "Informe de errores enviado correctamente", Toast.LENGTH_LONG).show();

        finish();
    }

    public void pruebalog(){
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }

           // txtlog.setText("fin");
            File fichero=crearLog(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath());
            if(fichero!=null) {
                BufferedWriter br = new BufferedWriter(new FileWriter(fichero, true));

                br.write(log.toString());

                br.close();
            }
        }
        catch (IOException e) {}
    }
    public File crearLog(String dir) {
        //talvez borrarlo cada semana

        try {
            String ruta =dir + "/compraslogcat.txt";

            File fichero = new File(ruta);
            FileOutputStream fileOutputStream = null;
            if (!fichero.exists()) {
                try {
                    fichero.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("BORRAR DATOS", "Error statusFile" + e.getMessage());
                }
            }
            return fichero;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    /****para respaldar la bd
     *
     */
    public void respaldarBd(){
       // String actualtDBPath=getDatabasePath("compras_data.db").getAbsolutePath();
        String actualtDBPath=getDatabasePath("compras_data").getAbsolutePath();

        Intent msgIntent = new Intent(this, SubirLogService.class);
      //  msgIntent.putExtra(SubirLogService.EXTRA_LOG_PATH, "compraslog.txt");
        msgIntent.putExtra(SubirLogService.EXTRA_LOG_DIR, actualtDBPath);

        msgIntent.setAction(SubirLogService.ACTION_UPLOAD_LOG);
        startService(msgIntent);

    }

}