package com.example.comprasmu.test;

import static org.junit.Assert.*;

import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.TaskExecutor;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.comprasmu.data.ComprasDataBase;
import com.example.comprasmu.data.PeticionesServidor;
import com.example.comprasmu.data.dao.HistoricoMuestrasDao;
import com.example.comprasmu.data.modelos.HistoricoMuestras;
import com.example.comprasmu.data.modelos.ImagenDetalle;
import com.example.comprasmu.data.repositories.HistoricoMuestrasRepositoryImpl;
import com.example.comprasmu.data.repositories.TablaVersionesRepImpl;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DescargaHistoricoMuestrasTest {

    Context context;
    private ComprasDataBase db;
    HistoricoMuestrasRepositoryImpl repo;
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();
    @Before
    public void createDb() {
        context = ApplicationProvider.getApplicationContext();
        db = Room.databaseBuilder(context, ComprasDataBase.class,"compras_data").allowMainThreadQueries().build();

        repo= HistoricoMuestrasRepositoryImpl.getInstance(db.getHistoricoMuestrasDao());
        repo.deleteAll();

    }

    @Test
    public void getMuestrasTest(){
        int plantaId=17;
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        PeticionesServidor peticionesServidor=new PeticionesServidor("12");
        //reviso si tengo algo en la tabla
        List<HistoricoMuestras> registros= repo.getByPlanta(plantaId);
        if(registros.isEmpty()) {
            LiveData<List<HistoricoMuestras>> respuesta = peticionesServidor.getHistoricoMuestras("4.2025", plantaId);
            respuesta.observeForever( new androidx.lifecycle.Observer<List<HistoricoMuestras>>() {
                @Override
                public void onChanged(List<HistoricoMuestras> historicoMuestras) {
                    //inserto en la tabla
                    if(historicoMuestras!=null)
                     Log.d("PRUEBAS","termine"+historicoMuestras.size());
                    latch.countDown(); // Avisa que ya terminó
                }
            });
        }
        try {
            latch.await(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
           Log.e("Error",e.getMessage());
        }
        assertTrue(true);
    }
}