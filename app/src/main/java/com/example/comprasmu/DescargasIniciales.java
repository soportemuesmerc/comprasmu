package com.example.comprasmu;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.comprasmu.data.ComprasDataBase;
import com.example.comprasmu.data.PeticionesServidor;
import com.example.comprasmu.data.dao.ListaCompraDao;
import com.example.comprasmu.data.modelos.Contrato;
import com.example.comprasmu.data.modelos.Geocerca;
import com.example.comprasmu.data.modelos.InformeCompraDetalle;
import com.example.comprasmu.data.modelos.InformeEtapa;
import com.example.comprasmu.data.modelos.InformeEtapaDet;
import com.example.comprasmu.data.modelos.ListaCompraDetalle;
import com.example.comprasmu.data.modelos.SolicitudCor;
import com.example.comprasmu.data.modelos.TablaVersiones;
import com.example.comprasmu.data.modelos.Visita;
import com.example.comprasmu.data.remote.CambiosInformesReponse;
import com.example.comprasmu.data.remote.IActualListener;
import com.example.comprasmu.data.remote.ListaCompraResponse;
import com.example.comprasmu.data.remote.MuestraCancelada;
import com.example.comprasmu.data.remote.NotificacionResponse;
import com.example.comprasmu.data.remote.PostResponse;
import com.example.comprasmu.data.remote.RespInfEtapaResponse;
import com.example.comprasmu.data.remote.RespInformesResponse;
import com.example.comprasmu.data.remote.SolCorreResponse;
import com.example.comprasmu.data.repositories.GeocercaRepositoryImpl;
import com.example.comprasmu.data.repositories.InfEtapaDetRepoImpl;
import com.example.comprasmu.data.repositories.InfEtapaRepositoryImpl;
import com.example.comprasmu.data.repositories.InformeComDetRepositoryImpl;
import com.example.comprasmu.data.repositories.InformeCompraRepositoryImpl;
import com.example.comprasmu.data.repositories.ListaCompraDetRepositoryImpl;
import com.example.comprasmu.data.repositories.ListaCompraRepositoryImpl;
import com.example.comprasmu.data.repositories.SolicitudCorRepoImpl;
import com.example.comprasmu.data.repositories.TablaVersionesRepImpl;
import com.example.comprasmu.data.repositories.VisitaRepositoryImpl;
import com.example.comprasmu.ui.gasto.IListenerRevRec;
import com.example.comprasmu.ui.notificaciones.NotificacionGen;
import com.example.comprasmu.ui.solcorreccion.ListaSolsViewModel;
import com.example.comprasmu.utils.ComprasLog;
import com.example.comprasmu.utils.Constantes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*****proceso para descargar de forma automatica y periodica los datos desde el servidor***/
public class DescargasIniciales {

        TablaVersionesRepImpl tvRepo;
        SimpleDateFormat sdfdias;
        ListaCompraDetRepositoryImpl lcdrepo;
        ListaCompraRepositoryImpl lcrepo;
        InformeComDetRepositoryImpl infdrepo;
        GeocercaRepositoryImpl georep;
        SolicitudCorRepoImpl solRepo;
        InfEtapaRepositoryImpl  infetarepo;
        InfEtapaDetRepoImpl infEtapaDetRepo;
        VisitaRepositoryImpl visRepo;
        InformeCompraRepositoryImpl infrepo;
        Context act;
        int actualiza;
        int procesos=0;
        int procesos_lev=0; //para saber cuantos si se corrieron
        private ComprasLog flog;
        DescargaIniListener listenprin;
        final String TAG="DescargasIniciales";
        boolean notificar=false;

        public DescargasIniciales(Context act) {
            this.act=act;
            sdfdias=new SimpleDateFormat("dd-MM-yyyy");
            flog = ComprasLog.getSingleton();
            flog.crearLog(act.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath());

        }

        public void ejecutar() {

            flog.grabarError(TAG,"ejecutar", "iniciando descargas programadas");
            actualiza=1;
            tvRepo=new TablaVersionesRepImpl(this.act);
            ListaCompraDao dao= ComprasDataBase.getInstance(this.act).getListaCompraDao();
            lcdrepo=new ListaCompraDetRepositoryImpl(this.act);
            lcrepo=ListaCompraRepositoryImpl.getInstance(dao);
            infdrepo=new InformeComDetRepositoryImpl(act);
            georep=GeocercaRepositoryImpl.getInstance(ComprasDataBase.getInstance(act).getGeocercaDao());
            listenprin=new DescargaIniListener();
            solRepo=new SolicitudCorRepoImpl(this.act);
            infetarepo=new InfEtapaRepositoryImpl(this.act);
            visRepo=new VisitaRepositoryImpl(this.act);
            infrepo=new InformeCompraRepositoryImpl(this.act);
            listacompras();
            pedirCorrecciones(0,0);
            notificacionesGenerales();
            //descargo cambios informes
            this.actualizarInformesAll(Constantes.INDICEACTUAL);
            //incluye
            //descargo notificaciones etiquetado
            DescRespInformesEta desetiq=new DescRespInformesEta( act,listenprin,tvRepo);

            desetiq.getCambiosEtiq();
        }


        private void listacompras(){
            Log.d(TAG, "descargando listas"+actualiza);
            flog.grabarError(TAG,"listacompras","descargando listas actualiza="+actualiza);
            PeticionesServidor ps=new PeticionesServidor(Constantes.CLAVEUSUARIO);
           //por ahora no tiene caso la fecha de actualizacion porque no se registra en la tabla, falta mejorar esto en la app web
            // TablaVersiones comp=tvRepo.getVersionByNombreTablasmd(Contrato.TBLLISTACOMPRAS,Constantes.INDICEACTUAL);
           // TablaVersiones det=tvRepo.getVersionByNombreTablasmd(Contrato.TBLLISTACOMPRASDET,Constantes.INDICEACTUAL);

            //siempre actualizo
            ps.getListasdeCompra(null,null,Constantes.INDICEACTUAL,listenprin);
            flog.grabarError(TAG,"listacompras"," siempre actualizo"+actualiza);

        }



    public void pedirCorrecciones(int actualiza, int etapa) {
        PeticionesServidor ps = new PeticionesServidor(Constantes.CLAVEUSUARIO);
        TablaVersiones comp = tvRepo.getVersionByNombreTablasmd(Contrato.TBLSOLCORRECCIONES, Constantes.INDICEACTUAL);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String version;
        if (comp != null && comp.getVersion() != null) {
            version = sdf.format(comp.getVersion());
        } else //es la 1a vez
        {
            version = "1999-09-09"; //una fecha muy antigua
        }
        if (actualiza == 1) {
            version = "1999-09-09"; //una fecha muy antigua
        }
        //siempre actualizo
        if (NavigationDrawerActivity.isOnlineNet(this.act))
            ps.pedirSolicitudesCorr(Constantes.INDICEACTUAL, etapa, version, new DescargaIniListener());
        else
            notificar = true;

    }

    private void notificacionesGenerales( ) {

        Log.d(TAG, "notificacionesGenerales "+procesos_lev);

        PeticionesServidor ps=new PeticionesServidor(Constantes.CLAVEUSUARIO);
        DescargaIniListener listener=new DescargaIniListener();
        MutableLiveData<List<NotificacionGen>> listaNotificaciones= ps.getNotificacionesGen(Constantes.INDICEACTUAL);
        Observer myObserver=new Observer<List<NotificacionGen>>() {
            @Override
            public void onChanged(List<NotificacionGen> notificacionGens) {
                convertirListaNotif(notificacionGens);
                Log.d(TAG,"finalizando notificaciones voy en el"+procesos);
                finalizar();
                listaNotificaciones.removeObserver(this);
            }
        };
        listaNotificaciones.observeForever(myObserver);

    }
    private  void convertirListaNotif(List<NotificacionGen> lista) {
        InfEtapaRepositoryImpl informeEtapaRepo=new InfEtapaRepositoryImpl(act);
        if(lista!=null)
            for (NotificacionGen noti:
                lista) {

            //modifico el estatus del informe
            if(noti.getTipo()==5&&noti.getTotal()>0){    //4-revisar recibo
                //5-ajustar recibo
                //6-estatus envio
                //busco el informe finalizado con estatus 2
                List<InformeEtapa> listaInf=informeEtapaRepo.getInfxEstatusCiuSim(Constantes.INDICEACTUAL,6,2, noti.getCiudad());
                if(listaInf!=null&&listaInf.size()>0){
                    informeEtapaRepo.actualizarEstatus(listaInf.get(0).getId(),5);
                    flog.grabarError(TAG,"convertirListaNotif","actualizando informe gastos ajuste"+listaInf.get(0).getId());
                }
            }


        }

    }
    public void finalizar(){
        Log.d(TAG,"finalizo descarga"+procesos+"--"+procesos_lev);

    }
    //actualiza todos los informes cada 10 seg con las ultimas modificaciones
    public void actualizarInformesAll(String indice){
        TablaVersiones comp = tvRepo.getVersionByNombreTablasmd(Contrato.TBLINFORMESDET, Constantes.INDICEACTUAL);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String version;
        if (comp != null && comp.getVersion() != null) {
            version = sdf.format(comp.getVersion());
        } else //es la 1a vez
        {
            version = "1999-09-09"; //una fecha muy antigua
        }
       // version = "1999-09-09";
        Log.e(TAG,">>>"+version);
        InfEtapaDetRepoImpl infEtapaDetRepo=new InfEtapaDetRepoImpl(this.act);
        PeticionesServidor peticionesServidor=new PeticionesServidor(Constantes.CLAVEUSUARIO);
        LiveData<CambiosInformesReponse> lcambiosInformesResponse=peticionesServidor.getCambiosInformes(indice,version);
        Observer observadorCambios= new Observer<CambiosInformesReponse>() {
            @Override
            public void onChanged(CambiosInformesReponse cambiosInformesReponse) {
                if(cambiosInformesReponse!=null) {
                    if(cambiosInformesReponse.getV()!=null){
                        flog.info(TAG,"actualizarInformesAll","hubo cambios en visitas"+cambiosInformesReponse.getV().toString());
                        actualizarVisitas(cambiosInformesReponse.getV());

                    }
                    if (cambiosInformesReponse.getID() != null) {
                        flog.info(TAG,"actualizarInformesAll","hubo cambios en informes"+cambiosInformesReponse.getID().toString());

                        actualizarInformeCompraDet(cambiosInformesReponse.getID());
                    }
                    if (cambiosInformesReponse.getDC() != null) {
                        //todo
                    }
                    if (cambiosInformesReponse.getIGD() != null) {
                        //todo
                    }
                    if (cambiosInformesReponse.getIED() != null) {
                        flog.info(TAG,"actualizarInformesAll","hubo cambios en informes etapas"+cambiosInformesReponse.getIED());
                        RespInfEtapaResponse infoResponse = new RespInfEtapaResponse();
                        infoResponse.setInformeEtapaDet(cambiosInformesReponse.getIED());
                        DescargaListaCompraAuto.actualizarInformeDetalle(infEtapaDetRepo, infoResponse);
                    }
                    lcambiosInformesResponse.removeObserver(this);
                    //actualizo en tabla versiones la fecha

                    TablaVersiones tinfo = new TablaVersiones();
                    tinfo.setNombreTabla(Contrato.TBLINFORMESDET);
                    Date fecha1 = new Date();

                    tinfo.setVersion(fecha1);
                    tinfo.setIndice(Constantes.INDICEACTUAL);
                    tinfo.setTipo("I");

                    tvRepo.insertUpdate(tinfo);
                }
            }
        };
        lcambiosInformesResponse.observeForever(observadorCambios);

    }
    private void actualizarInformeCompraDet(List<InformeCompraDetalle> infComprasDetalle){
        InformeCompraDetalle informeDetOrig;
        if (infComprasDetalle.size() > 0) {

            for (InformeCompraDetalle det:infComprasDetalle
            ) {
                //busco

                 informeDetOrig = infdrepo.findsimple(det.getId());
                if(informeDetOrig!=null) {
                    //modifico
                    informeDetOrig.setOrigen(det.getOrigen());
                    informeDetOrig.setQr(det.getQr());
                    flog.info(TAG,"actualizarInformeCompraDet","hubo cambios en informes"+det.getId()+" caducidad:"+det.getCaducidad());

                    informeDetOrig.setCaducidad(det.getCaducidad());
                    flog.info(TAG,"actualizarInformeCompraDet","fecha despues:"+informeDetOrig.getCaducidad());


                    informeDetOrig.setCodigo(det.getCodigo());
                    informeDetOrig.setCosto(det.getCosto());
                    informeDetOrig.setAtributoa(det.getAtributoa());
                    informeDetOrig.setAtributob(det.getAtributob());
                    informeDetOrig.setAtributoa(det.getAtributoc());
                    informeDetOrig.setAtributob(det.getAtributod());
                    //ahora puede modificarse el producto
                    if(informeDetOrig.getProductoId()!=det.getProductoId()||informeDetOrig.getEmpaquesId()!=det.getEmpaquesId()||informeDetOrig.getTamanioId()!=det.getTamanioId()){
                        informeDetOrig.setProductoId(det.getProductoId());
                        informeDetOrig.setProducto(det.getProducto());
                        informeDetOrig.setTamanioId(det.getTamanioId());
                        informeDetOrig.setEmpaquesId(det.getEmpaquesId());
                        informeDetOrig.setEmpaque(det.getEmpaque());
                        informeDetOrig.setPresentacion(det.getPresentacion());
                        informeDetOrig.setTipoAnalisis(det.getTipoAnalisis());
                        informeDetOrig.setNombreAnalisis(det.getNombreAnalisis());
                        //si se modificó tambien se modificó la lista de compra
                        ListaCompraDetalle compradet = lcdrepo.findsimple(informeDetOrig.getComprasId(), informeDetOrig.getComprasDetId());
                        if (compradet != null) {
                            //quito la comprada
                            if (compradet.getComprados() > 0) {
                                // Log.i(TAG,"procesarCanceladas",informeDetOrig.getId()+"--"+informeDetOrig.getInformesId());
                                int cantidad = compradet.getComprados() - 1;
                                lcdrepo.actualizarComprados(compradet.getId(), compradet.getListaId(), cantidad);
                            }
                        }
                        compradet = lcdrepo.findsimple(det.getComprasId(), det.getComprasDetId());
                        if (compradet != null) {
                            //sumo la comprada
                            // Log.i(TAG,"procesarCanceladas",informeDetOrig.getId()+"--"+informeDetOrig.getInformesId());
                            int cantidad = compradet.getComprados() + 1;
                            lcdrepo.actualizarComprados(compradet.getId(), compradet.getListaId(), cantidad);

                        }
                        if(informeDetOrig.getTipoMuestra()==3){
                            informeDetOrig.setComprasDetIdbu(det.getComprasDetIdbu());
                            informeDetOrig.setComprasIdbu(det.getComprasIdbu());
                        }else{
                            informeDetOrig.setComprasDetId(det.getComprasDetId());
                            informeDetOrig.setComprasId(det.getComprasId());
                        }

                    }
                }
                else
                    informeDetOrig=det;
                //actualizo
                infdrepo.insert(informeDetOrig);
            }

        }

    }
    public void actualizarVisitas(List<Visita> visitas ) {
        // Log.d(TAG, "actualizando bd informes");
        //primero los inserts


        for (Visita vis : visitas) {

            //busco la visita
            Visita original = visRepo.findsimple(vis.getId());
            if (original != null) {
                original.setTiendaNombre(vis.getTiendaNombre());
                original.setCadenaComercial(vis.getCadenaComercial());
                original.setTipoId(vis.getTipoId());
                original.setTipoTienda(vis.getTipoTienda());
                original.setComplementodireccion(vis.getComplementodireccion());
                original.setDireccion(vis.getDireccion());
                original.setGeolocalizacion(vis.getGeolocalizacion());
                original.setPuntoCardinal(vis.getPuntoCardinal());


                visRepo.insert(original); //inserto blblbl
            }

        }

    }



    public class DescargaIniListener implements  IDescargaIniListener, IActualListener, IListenerRevRec, DescRespInformesEta.ProgresoRespIEListener {
        public DescargaIniListener(){


        }
        public void finalizar(){
            Log.d(TAG,"finalizo descarga"+procesos+"--"+procesos_lev);

        }


        public void insertarZonas(List<Geocerca> zonas){
            Log.d(TAG,"fin zonas ");
            if (zonas != null && zonas.size() > 0) {

                //insertar

                for(Geocerca geo:zonas) {
                    georep.insert(geo);
                }
                //actualizo tabla versiones
                TablaVersiones tv=new TablaVersiones();
                tv.setNombreTabla("geocercas");
                tv.setTipo("C");
                tv.setVersion(new Date());
                tvRepo.insertUpdate(tv);

                tv=null;
            }

            finalizar();
        }


        public void actualizar(ListaCompraResponse compraResp) {
            infdrepo = new InformeComDetRepositoryImpl(act);
           DescargasListaCompraImpl descargasListaCompra=new DescargasListaCompraImpl(flog);
           descargasListaCompra.actualizarListaCompra(compraResp,lcrepo,lcdrepo,tvRepo,infdrepo);
           finalizar();

        }
        public int actualizarCorre(SolCorreResponse corrResp, int etapa) {

            //primero los inserts
            if (corrResp != null) {

                if (corrResp != null && corrResp.getInserts() != null) {
                    for (SolicitudCor sol : corrResp.getInserts()) {
                        //veo si ya existe
                      //   Log.d(TAG,"solcorreccion"+sol.getId()+"--"+ sol.getNumFoto());
                        SolicitudCor solt = solRepo.findsimple(sol.getId(), sol.getNumFoto());
                        if (solt != null) {
                            if (solt.getEstatus() !=4||solt.getEstatus()!=5) {
                                //actualizo
                               // Log.d(TAG,"actualizo solcorreccion"+solt.getId()+"--"+ solt.getEstatus());
                                solRepo.actualizarEst(sol.getMotivo(), sol.getContador(), sol.getCreatedAt(), sol.getEstatus(), sol.getId(), sol.getNumFoto());
                            } else if (sol.getContador() > 1)
                                solRepo.actualizarEst(sol.getMotivo(), sol.getContador(), sol.getCreatedAt(), sol.getEstatus(), sol.getId(), sol.getNumFoto());
                            else
                                solRepo.actualizar(sol.getMotivo(), sol.getContador(), sol.getCreatedAt(), sol.getId(), sol.getNumFoto());

                        } else
                            solRepo.insert(sol);

                    }


                }

                //los updates
                if (corrResp != null && corrResp.getUpdates() != null) {

                    if (corrResp.getUpdates() != null)
                        solRepo.insertAll(corrResp.getUpdates()); //inserto blblbl
                }

                //actualizar version en tabla
                TablaVersiones tinfo = new TablaVersiones();
                tinfo.setNombreTabla(Contrato.TBLSOLCORRECCIONES);
                Date fecha1 = new Date();

                tinfo.setVersion(fecha1);
                tinfo.setIndice(Constantes.INDICEACTUAL);
                tinfo.setTipo("I");

                tvRepo.insertUpdate(tinfo);
                //  Log.d(TAG,"dddddd"+corrResp.getCanceladas().size());
                //veo las muestras canceladas
                if (corrResp.getCanceladas() != null)

                        for (MuestraCancelada cancel :
                                corrResp.getCanceladas()) {
                            //busco el informedetalle y actualizo el estatus
                            if(cancel.getIne_etapa()>0&&cancel.getIne_etapa()!=2) {
                                //busco el informedetalle y actualizo el estatus

                                this.procesarCanceladasEta(cancel);
                                if(cancel.getIne_etapa()==3) //tambien cancelo los detalles
                                    this.procesarCanceladasEtaDet(cancel);

                            }else {
                                this.procesarCanceladas(cancel);
                                //canceladas será 0

                            }

                        }
                //muestras eliminada son las canceladas que no se reponen
                if (corrResp.getEliminadas() != null)
                    for (MuestraCancelada cancel :
                            corrResp.getEliminadas()) {
                        {
                            this.procesarEliminadas(cancel);
                            //canceladas será 0
                        }
                    }

            }
            return 1;

        }




        //el estatus viene como 2 pero en la app 0 es cancelado y 2 es finalizado
        public void procesarCanceladasEta(MuestraCancelada cancelada) {
            InformeEtapa det = infetarepo.findsimple(cancelada.getInf_id());

            if (det != null) {
                Log.e(TAG, "cancele");
                det.setMotivoCancel(cancelada.getVas_observaciones());
                det.setFechaCancel(cancelada.getVas_fecha());
                det.setEstatus(0);

                infetarepo.insert(det);
                infetarepo.actualizarEstatus(det.getId(), 0);


            }
        }

        public void procesarCanceladas(MuestraCancelada cancelada){
            infdrepo=new InformeComDetRepositoryImpl(act);
            InformeCompraDetalle det=infdrepo.findsimple(cancelada.getInd_id());

            if(det!=null) {
                if (det.getEstatus() != 2&&det.getEstatus()!=4)//no está cancelada
                {
                    Log.d(TAG,"procesando canceladas"+det.getId());
                    String codigo=Constantes.sdfcaducidad.format(det.getCaducidad());
                     ListaCompraDetalle compradet = lcdrepo.findsimple(cancelada.getInd_comprasid(), cancelada.getInd_compraddetid());
                    if (compradet != null) {
                        //quito la comprada
                        if (compradet.getComprados() > 0) {
                            flog.grabarError(TAG,"procesarCanceladas",det.getId()+"--"+det.getInformesId()+"--"+codigo);
                            int cantidad = compradet.getComprados() - 1;
                            lcdrepo.actualizarComprados(compradet.getId(), compradet.getListaId(), cantidad);
                        }
                        Log.d(TAG,"quitando el codigo"+compradet.getNvoCodigo());

                        //quito en nuevo codigo
                        if (compradet.getNvoCodigo()!=null&&compradet.getNvoCodigo() != "") {
                            flog.grabarError(TAG,"procesarCanceladas","quitando el codigo"+compradet.getNvoCodigo());


                            String nuevoscods = compradet.getNvoCodigo().replace(codigo + ";", "");//elimino elcodigo
                            nuevoscods = compradet.getNvoCodigo().replace(codigo, "");//elimino elcodigo

                            //Log.d(TAG,compradet.getId()+"--"+compradet.getListaId()+"--"+nuevoscods);
                            lcdrepo.actualizarNvosCodigos(compradet.getId(), compradet.getListaId(), nuevoscods);
                        }
                        det.setMotivoCancel(cancelada.getVas_observaciones());
                        det.setFechaCancel(cancelada.getVas_fecha());
                        det.setEstatus(2);

                        infdrepo.insert(det);
                        infdrepo.actualizarEstatus(det.getId(), 2);
                    }
                }

                }

        }

        public void procesarEliminadas(MuestraCancelada cancelada){
            InformeCompraDetalle det=infdrepo.findsimple(cancelada.getInd_id());
            if(det!=null) {

                det.setMotivoCancel(cancelada.getVas_observaciones());
                det.setFechaCancel(cancelada.getVas_fecha());
                det.setEstatus(6);
                infdrepo.insert(det);
                infdrepo.actualizarEstatus(det.getId(), 6);

            }
        }

        @Override
        public void actualizarInformes(RespInformesResponse infoResp) {
            // Log.d(TAG, "actualizando bd informes");
            //primero los inserts
            if (infoResp != null) {

                if (infoResp.getVisita() != null) {
                    //reviso cada uno y las inserto
                    for (Visita vis : infoResp.getVisita()) {
                        visRepo.insert(vis); //inserto blblbl
                    }
                }
                if (infoResp.getInformeCompra() != null && infoResp.getInformeCompra().size() > 0) {
                    //como puede que ya existan reviso primero e inserto unoxuno
                    infrepo.insertAll(infoResp.getInformeCompra());
                }
                if (infoResp.getInformeCompraDetalles() != null && infoResp.getInformeCompraDetalles().size() > 0) {
                    //como puede que ya existan reviso primero e inserto unoxuno
                    infdrepo.insertAll(infoResp.getInformeCompraDetalles());
                }

            }
            //actualizar version en tabla
            TablaVersiones tinfo = new TablaVersiones();
            tinfo.setNombreTabla(Contrato.TBLINFORMESCOMP);
            Date fecha1 = new Date();
            Log.d("DescargasAsyncTask", "insertando fecha version 1" + fecha1);

            tinfo.setVersion(fecha1);
            tinfo.setIndice(Constantes.INDICEACTUAL);
            tinfo.setTipo("I");

            tvRepo.insertUpdate(tinfo);

        }


        @Override
        public void guardarEstatus(PostResponse response) {

        }


        @Override
        public void guardarRes(PostResponse respuesta) {

        }

        @Override
        public void guardarResNotif(NotificacionResponse response) {
            Log.d(TAG,"actualizando notificaciones");
            if(response!=null&&response.getData()!=null) {
                convertirListaNotif(response.getData());

            }
            Log.d(TAG,"finalizando notificaciones voy en el"+procesos);
            finalizar();
        }

        @Override
        public void finalizarrespie() {

            procesos++;

            finalizar();

        }

        public void procesarCanceladasEtaDet(MuestraCancelada cancelada) {
            infEtapaDetRepo=new InfEtapaDetRepoImpl(act);
            List<InformeEtapaDet> detalles = infEtapaDetRepo.getAllSencillo(cancelada.getInf_id());

            if (detalles != null) {
                for (InformeEtapaDet det:
                        detalles) {
                    Log.e(TAG, "cancele etiquetado det:" + det.getId());
                    infEtapaDetRepo.actEstatus(det.getId(), 0);
                }
            }
        }
}
}
