package com.example.comprasmu;

import com.example.comprasmu.data.modelos.Geocerca;
import com.example.comprasmu.data.remote.ListaCompraResponse;
import com.example.comprasmu.data.remote.MuestraCancelada;

import java.util.List;

public interface IDescargaIniListener {

     void finalizar();
     void insertarZonas(List<Geocerca> zonas);
     void actualizar(ListaCompraResponse compraResp) ;

}
