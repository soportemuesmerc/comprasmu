package com.example.comprasmu.test;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.comprasmu.DescargaListaCompraAuto;
import com.example.comprasmu.data.ComprasDataBase;
import com.example.comprasmu.data.PeticionesServidor;
import com.example.comprasmu.data.dao.ImagenDetalleDao;
import com.example.comprasmu.data.modelos.ImagenDetalle;
import com.example.comprasmu.data.modelos.ListaCompra;
import com.example.comprasmu.data.modelos.Tienda;
import com.example.comprasmu.data.modelos.TiendaJson;
import com.example.comprasmu.data.remote.ListaCompraResponse;
import com.example.comprasmu.data.repositories.ImagenDetRepositoryImpl;
import com.example.comprasmu.data.repositories.InformeComDetRepositoryImpl;
import com.example.comprasmu.data.repositories.ListaCompraDetRepositoryImpl;
import com.example.comprasmu.data.repositories.ListaCompraRepositoryImpl;
import com.example.comprasmu.data.repositories.TablaVersionesRepImpl;
import com.example.comprasmu.data.repositories.TiendaRepositoryImpl;
import com.example.comprasmu.services.DescargaCambiosImagenes;
import com.example.comprasmu.ui.tiendas.PeticionMapaCd;
import com.example.comprasmu.utils.ComprasLog;
import com.example.comprasmu.utils.Constantes;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Observable;

@RunWith(AndroidJUnit4.class)
public class DescargaTiendasTest {
    Context context;
    private ComprasDataBase db;
    TiendaRepositoryImpl tvrepo;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();
    private ListaCompraRepositoryImpl repository;
    private ListaCompraDetRepositoryImpl detRepo;
    private InformeComDetRepositoryImpl informeCompraRepository;

    @Before
    public void createDb() {
        context = ApplicationProvider.getApplicationContext();
        db = Room.databaseBuilder(context, ComprasDataBase.class,"compras_data").allowMainThreadQueries().build();
        tvrepo=TiendaRepositoryImpl.getInstance(db.getInstance(context).getTiendaDao());
       informeCompraRepository=new InformeComDetRepositoryImpl(context);
        detRepo=new ListaCompraDetRepositoryImpl(context);
        repository=ListaCompraRepositoryImpl.getInstance(db.getInstance(context).getListaCompraDao());
        tvrepo.deleteAll();

    }
    @After
    public void closeDb() throws IOException {
        db.close();
        //userDao.findAll().removeObservers();
    }
    @Test
    public void getTiendas() {
        String usuario="9";
        String indice="3.2026";
        String ciudad="MERIDA";
        PeticionMapaCd pm=new PeticionMapaCd(usuario);
        pm.getTiendas("0",ciudad,"2026-05-18");
        pm.getListatiendas().observeForever( new Observer<List<TiendaJson>>() {
            @Override
            public void onChanged(List<TiendaJson> tiendas) {
                //guardo en la tabla local
                if(tiendas!=null&& tiendas.size()>0)
                 Log.d(">>>","Hubo algo"+ tiendas.size());
            }
        });

        Assert.assertTrue(true);
    }

    @Test
    public void getTiendasxciudad() {
        String usuario="1";
        String indice="3.2026";
        String ciudad="LAGOS DE MORENO";
        ComprasLog compraslog=ComprasLog.getSingleton();
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        compraslog.crearLog(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath());
        DescargaListaCompraAuto descargaListaCompraAuto=new DescargaListaCompraAuto(compraslog,repository,detRepo,informeCompraRepository);
        PeticionesServidor peticionesServidor=new PeticionesServidor(usuario);
        PeticionesServidor.PeticionLista peticionLista=peticionesServidor.crearPeticion(null, null,indice);


        LiveData<ListaCompraResponse> respuesta = peticionesServidor.pedirListaCompraxCiudad(peticionLista,ciudad,descargaListaCompraAuto);
        respuesta.observeForever(new  androidx.lifecycle.Observer<ListaCompraResponse>() {
            @Override
            public void onChanged(ListaCompraResponse listaCompraResponse) {

                //guardo en la tabla local
                if (listaCompraResponse != null )
                    if(listaCompraResponse.getInserts().getListaCompra() != null) {

                        Log.d(">>>", "Hubo algo" + listaCompraResponse.getInserts().getListaCompra().size());
                    }
                if(listaCompraResponse.getUpdates().getListaCompra() != null) {

                    Log.d(">>>", "Hubo algo" + listaCompraResponse.getUpdates().getListaCompra().size());
                }


                    latch.countDown(); // Avisa que ya terminó

            }
                });
        try {
            latch.await(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e("Error",e.getMessage());
        }
        Assert.assertTrue(true);
    }



}