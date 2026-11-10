package com.example.comprasmu.ui.solcorreccion;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.comprasmu.data.ComprasDataBase;
import com.example.comprasmu.data.PeticionesServidor;
import com.example.comprasmu.data.dao.ImagenDetalleDao;
import com.example.comprasmu.data.dao.ListaCompraDao;
import com.example.comprasmu.data.modelos.ImagenDetalle;
import com.example.comprasmu.data.modelos.InformeCompra;
import com.example.comprasmu.data.modelos.InformeCompraDetalle;
import com.example.comprasmu.data.modelos.InformeEnvioPaq;
import com.example.comprasmu.data.modelos.InformeEtapa;
import com.example.comprasmu.data.modelos.InformeEtapaDet;
import com.example.comprasmu.data.modelos.InformeGastoDet;
import com.example.comprasmu.data.modelos.ListaCompra;
import com.example.comprasmu.data.modelos.ListaCompraDetalle;
import com.example.comprasmu.data.modelos.SolicitudCor;
import com.example.comprasmu.data.remote.MuestraCancelada;
import com.example.comprasmu.data.remote.NotificacionResponse;
import com.example.comprasmu.data.repositories.ImagenDetRepositoryImpl;
import com.example.comprasmu.data.repositories.InfEtapaDetRepoImpl;
import com.example.comprasmu.data.repositories.InfEtapaRepositoryImpl;
import com.example.comprasmu.data.repositories.InfGastoDetRepositoryImpl;
import com.example.comprasmu.data.repositories.InformeComDetRepositoryImpl;
import com.example.comprasmu.data.repositories.InformeCompraRepositoryImpl;
import com.example.comprasmu.data.repositories.InformeEnvioRepositoryImpl;
import com.example.comprasmu.data.repositories.ListaCompraDetRepositoryImpl;
import com.example.comprasmu.data.repositories.ListaCompraRepositoryImpl;
import com.example.comprasmu.data.repositories.SolicitudCorRepoImpl;
import com.example.comprasmu.ui.infetapa.ContInfEtaViewModel;
import com.example.comprasmu.ui.notificaciones.NotificacionGen;
import com.example.comprasmu.utils.ComprasLog;
import com.example.comprasmu.utils.Constantes;

import java.util.ArrayList;
import java.util.List;

public class ListaSolsViewModel extends AndroidViewModel {
    private  SolicitudCorRepoImpl repository;
    private  LiveData<Integer> size;
    private  LiveData<Boolean> empty;
    private final static String TAG="ListaSolsViewModel";
    ImagenDetRepositoryImpl imrepo;
    InfEtapaDetRepoImpl etadetRepo;
    InformeComDetRepositoryImpl infcrepo;
    InfEtapaRepositoryImpl infetarepo;
    InformeCompraRepositoryImpl infrepo;
    InformeEnvioRepositoryImpl infenvrepo;
    ListaCompraRepositoryImpl lcrepo;
    ListaCompraDetRepositoryImpl lcdrepo;
    Context context;
    ComprasLog milog;
    private  InfGastoDetRepositoryImpl gasdetrepo;
    MutableLiveData<Integer> totCancel;
    MutableLiveData<Integer> totMuestraAdic;
    private int itotCancel;

    public ListaSolsViewModel(Application application) {
        super(application);
      this.context=application;
        repository = new SolicitudCorRepoImpl(application);
        infcrepo=new InformeComDetRepositoryImpl(application);
        infetarepo=new InfEtapaRepositoryImpl(application);
        infrepo=new InformeCompraRepositoryImpl(application);
        infenvrepo=new InformeEnvioRepositoryImpl(application);
        ListaCompraDao dao= ComprasDataBase.getInstance(application).getListaCompraDao();
        lcrepo=ListaCompraRepositoryImpl.getInstance(dao);
        this.gasdetrepo = new InfGastoDetRepositoryImpl(application);
        lcdrepo=new ListaCompraDetRepositoryImpl(application);
        milog=ComprasLog.getSingleton();
        milog.crearLog(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath());

    }

    public LiveData<List<SolicitudCor>>  cargarDetalles(int etapa,String indiceSel, int estatus){
        LiveData<List<SolicitudCor>> listas =repository.getAll(etapa,indiceSel, estatus);
        size = Transformations.map(listas, res->{ return listas.getValue().size();});
        empty = Transformations.map(listas, res->{return listas.getValue().isEmpty();});
        return listas;
    }
    public LiveData<List<SolicitudCor>>  cargarDetallesAll(String indiceSel){
        LiveData<List<SolicitudCor>> listas =repository.getSolicitudPendAll(indiceSel);
        size = Transformations.map(listas, res->{ return listas.getValue().size();});
        empty = Transformations.map(listas, res->{return listas.getValue().isEmpty();});
        return listas;
    }
    public LiveData<List<SolicitudCor>>  cargarDetallesPlan(int etapa,String indiceSel,int plantaSel, int estatus){
        LiveData<List<SolicitudCor>> listas =repository.getAllPlan(etapa,indiceSel,plantaSel, estatus);
        size = Transformations.map(listas, res->{ return listas.getValue().size();});
        empty = Transformations.map(listas, res->{return listas.getValue().isEmpty();});
        return listas;
    }
    public LiveData<List<SolicitudCor>>  cargarDetallesVis(int etapa,String indiceSel, int estatus){
        LiveData<List<SolicitudCor>> listas =repository.getAllVisita(etapa,indiceSel, estatus);
        size = Transformations.map(listas, res->{ return listas.getValue().size();});
        empty = Transformations.map(listas, res->{return listas.getValue().isEmpty();});
        return listas;
    }
    public LiveData<Integer> getTotalSols( String indiceSel, int estatus){

        return repository.totalSols(indiceSel, estatus);
    }
    public int getTotalSolsGen( String indiceSel, int estatus){

        return repository.totalSolsGen(indiceSel, estatus);
    }


    public LiveData<Integer> getTotalSolsxCd(int etapa, String indiceSel, int estatus, String ciudad){
      //  Log.d(TAG,"wwww"+ Constantes.ETAPAACTUAL+","+Constantes.INDICEACTUAL);

        return repository.totalSolsxPlantaxCd(etapa,indiceSel, estatus,ciudad);
    }


    public LiveData<Integer> getTotSolsEtiqxCd(int etapa, String indiceSel, int estatus, String ciudad){

        return repository.getTotSolsEtiqxCd(etapa,indiceSel, estatus,ciudad);
    }
    public int  getTotalSolsxplanta(int etapa,String indiceSel,int estatus, int planta){
        Log.d(TAG,"getTotalSolsxplanta"+etapa+"--"+indiceSel+"--"+estatus+"--"+planta);
        return repository.totalSolsxPlanta(etapa,indiceSel, estatus, planta);
    }
    public int  getTotalSolsxplantaAll(String indiceSel,int estatus, int planta){
        Log.d(TAG,"getTotalSolsxplantaAll"+indiceSel+"--"+estatus+"--"+planta);
        return repository.totSolsxPlantaAll(indiceSel, estatus, planta);
    }
    public int  getTotSolsVis(String indiceSel,int estatus, String ciudad){
        Log.d(TAG,"getTotalSolsxplanta"+indiceSel+"--"+estatus+"--");
        return repository.getTotSolsVis(2,indiceSel, estatus, ciudad);
    }
    public LiveData<SolicitudCor>  getSolicitud(int id,int numfoto){
        LiveData<SolicitudCor> solicitud =repository.find(id,numfoto);
        return solicitud;
    }
    public MutableLiveData<Integer> getTotalCancell(String indiceSel){
        Log.d(TAG,"wwww*"+ Constantes.ETAPAACTUAL+","+Constantes.INDICEACTUAL);

        return infcrepo.gettotCancelados(indiceSel);
    }

    public List<InformeCompraDetalle> getTotalCancel(String indiceSel ){
        return infcrepo.getCanceladosSim(indiceSel);

    }
    //por ahora aplica preparacion y etiq donde se cancela todo el info
    public LiveData<List<InformeEtapa>> getTotalCancelEta(String indiceSel ){
        return infetarepo.getInformesxEstatusAll(indiceSel,0);

    }

    //por ahora aplica preparacion y etiq donde se cancela todo el info
   /* public LiveData<List<InformeEtapa>> getTotalCancelEtaxCd(String indiceSel, int etapa ,String cd){
        return infetarepo.getInformesxEstatus(indiceSel,etapa,0);

    }*/

    public void actualizarEstSolicitud(int id,int numfoto, int estatus){
        Log.d(TAG,"actalizando"+id+"--"+numfoto);
      repository.actualizarEstatus(id,numfoto,estatus);

    }
    public LiveData<ImagenDetalle> buscarImagenCom(int numfoto){
        ImagenDetalleDao imagenDetalleDao= ComprasDataBase.getInstance(context).getImagenDetalleDao();
        this.imrepo= ImagenDetRepositoryImpl.getInstance(imagenDetalleDao);
        return imrepo.find(numfoto);
    }

    public LiveData<InformeEtapaDet> buscarEtapaDet(int iddet){
        etadetRepo=new InfEtapaDetRepoImpl(context);
        return etadetRepo.find(iddet);
    }

    public LiveData<InformeEtapaDet> buscarFotoEta(int numfoto,int idinf, int etapa){
        etadetRepo=new InfEtapaDetRepoImpl(context);
        return etadetRepo.getBynumfoto(idinf,etapa,numfoto);
    }
    public LiveData<List<ImagenDetalle>> buscarFotosEta(int idinf, int etapa){
        etadetRepo=new InfEtapaDetRepoImpl(context);
        return etadetRepo.getImagenxInf(idinf,etapa);
    }

    public LiveData<Integer> getSize() {
        return size;
    }

    public LiveData<Boolean> getEmpty() {
        return empty;
    }

    public InformeCompraDetalle buscarInformeFoto(int informesId, int numfoto, String indiceactual) {
        return infcrepo.findByInformeFoto(informesId,numfoto);
    }
    public InformeCompraDetalle buscarInformeByFoto(int informesId, int numfoto, int descrId) {
        return infcrepo.getInformeByFoto(informesId,numfoto, descrId);
    }

    public void procesarCanceladas(MuestraCancelada cancelada){
        InformeCompraDetalle det=infcrepo.findsimple(cancelada.getInd_id());

        if(det!=null) {
            if (det.getEstatus() != 2&&det.getEstatus()!=4)//no está cancelada
            {
                Log.i(TAG,"procesando canceladas"+det.getId());
                String codigo=Constantes.sdfcaducidad.format(det.getCaducidad());
                ListaCompraDetRepositoryImpl lcdrepo = new ListaCompraDetRepositoryImpl(context);
                ListaCompraDetalle compradet = lcdrepo.findsimple(cancelada.getInd_comprasid(), cancelada.getInd_compraddetid());
                if (compradet != null) {
                    //quito la comprada
                    if (compradet.getComprados() > 0) {
                         milog.grabarError(TAG,"procesarCanceladas",det.getId()+"--"+det.getInformesId()+"--"+codigo);
                        int cantidad = compradet.getComprados() - 1;
                        lcdrepo.actualizarComprados(compradet.getId(), compradet.getListaId(), cantidad);
                    }
                  Log.d(TAG,"quitando el codigo"+compradet.getNvoCodigo());

                    //quito en nuevo codigo
                    if (compradet.getNvoCodigo()!=null&&compradet.getNvoCodigo() != "") {
                       milog.grabarError(TAG,"procesarCanceladas","quitando el codigo"+compradet.getNvoCodigo());
                        String nuevoscods = compradet.getNvoCodigo().replace(codigo + ";", "");//elimino elcodigo
                        nuevoscods = compradet.getNvoCodigo().replace(codigo, "");//elimino elcodigo

                       //Log.d(TAG,compradet.getId()+"--"+compradet.getListaId()+"--"+nuevoscods);
                        lcdrepo.actualizarNvosCodigos(compradet.getId(), compradet.getListaId(), nuevoscods);
                    }
                    det.setMotivoCancel(cancelada.getVas_observaciones());
                    det.setFechaCancel(cancelada.getVas_fecha());
                    det.setEstatus(2);
                    infcrepo.insert(det);
                    infcrepo.actualizarEstatus(det.getId(), 2);
                }
            }


            }


    }
    //el estatus viene como 2 pero en la app 0 es cancelado y 2 es finalizado
    public void procesarCanceladasEta(MuestraCancelada cancelada){
         InformeEtapa det=infetarepo.findsimple(cancelada.getInf_id());

        if(det!=null) {
            Log.e(TAG,"cancele");
            det.setMotivoCancel(cancelada.getVas_observaciones());
            det.setFechaCancel(cancelada.getVas_fecha());
            det.setEstatus(0);

            infetarepo.insert(det);
            infetarepo.actualizarEstatus(det.getId(), 0);

        }


    }

    public void procesarCanceladasEtaDet(MuestraCancelada cancelada) {
        etadetRepo=new InfEtapaDetRepoImpl(this.context);
        List<InformeEtapaDet> detalles = etadetRepo.getAllSencillo(cancelada.getInf_id());

        if (detalles != null) {
            for (InformeEtapaDet det:
                    detalles) {
                Log.e(TAG, "cancele etiquetado det:" + det.getId());
                etadetRepo.actEstatus(det.getId(), 0);
            }
        }
    }
    public void contarCanceladas(){
        int itotCanceleta=0;
        //busco si hay cancelacion de preparacion
        List<InformeEtapa> totCanceleta=getTotalCancelEtaSim(Constantes.INDICEACTUAL,1);
        Log.d(TAG,"contarCanceladas-tot prep"+(totCanceleta!=null?totCanceleta.size():0));
      /*  for (InformeEtapa informe:totCanceleta
        ) { //busco si no se ha vuelto a elaborar
            InformeEtapa inf=getInformexPlantaEtaEst(informe.getPlantasId(),informe.getEtapa(),Constantes.INDICEACTUAL,0);
            if(inf!=null){
                //corregido
                continue;
            }
            else
                itotCanceleta++;

        }*/
        itotCanceleta=totCanceleta!=null?totCanceleta.size():0;
        List<InformeCompraDetalle> informesCancel=getTotalCancel(Constantes.INDICEACTUAL);
        totCancel=new MutableLiveData<>();

        if(informesCancel!=null&&informesCancel.size()>0) {
            itotCancel = informesCancel.size();
            Log.d(TAG,"contarCanceladas- totcancelcompras"+itotCanceleta);

        }
        else {  //esto es para cuando se reactiva
            List<ListaCompra> listacomp = cargarClientesSimplxetReacsc(3, 1,3);
            if (listacomp != null && listacomp.size() > 0)
                setEtiquetadoCancel(3, 6,4); //si es reactivacion sin comprar puede estar en estatus 4 para completar etiquetado
            else {

                    //veo si ya puedo hacer empaque
                    listacomp = cargarClientesSimplxetReacsc(4, 1,3);
                    InformeEtapa nvoinf = new InformeEtapa();
                    List<InformeEtapa> listageneral = new ArrayList<>();
                    Log.d(TAG, "contarCanceladas- puedo hacer empaque?" + listacomp);
                    for (ListaCompra listaCompra : listacomp) {
                        if (listaCompra.getLis_reactivado() != null) {
                            //veo que no haya hecho informe para no esperar a la supervisión
                            // ContInfEtaViewModel conViewModel = new ViewModelProvider(this).get(ContInfEtaViewModel.class);
                            Log.d(TAG, "contarCanceladas-hay reactivacion");
                            InformeEtapa informesEtapa = getInformeNoCancel(Constantes.INDICEACTUAL, 4, listaCompra.getCiudadNombre(), listaCompra.getClientesId());
                            if (informesEtapa == null) {
                                nvoinf.setIndice(listacomp.get(0).getIndice());
                                // nvoinf.set = listacomp.get(0).getId();
                                nvoinf.setEstatus(listacomp.get(0).getEstatus());
                                nvoinf.setEtapa(4);

                                nvoinf.setCiudadNombre(listacomp.get(0).getCiudadNombre());
                                nvoinf.setClienteNombre(listacomp.get(0).getClienteNombre());

                                // nvoinf.mo
                                listageneral.add(nvoinf);
                            }
                        }
                    }

                    itotCancel = listageneral.size();



            }
        }
        totCancel.setValue(itotCancel+itotCanceleta);

    }

    private void setEtiquetadoCancel(int etapa, int estatus1, int estatus2) {
        List<InformeEtapa> listageneral=new ArrayList<>();
        //para ver si sigue etiquetado y empaque
        List<InformeEtapa> informes=getInfEtapax2Estatus(Constantes.INDICEACTUAL,etapa,estatus1, estatus2);
        Log.d(TAG,"setEtiquetadoCancel-hayinforme"+informes);
        //paso de informe etapa a informe compra
        for (InformeEtapa infeta : informes
        ) {
            //reviso si ya estoy en etapa 3
            List<ListaCompra> listacomp = cargarClientesSimplxet(infeta.getCiudadNombre(), 3);
            if (listacomp != null && listacomp.size() > 0 && listacomp.get(0)!=null&&listacomp.get(0).getClientesId() == infeta.getClientesId()) {

                listageneral.add(infeta);

            }

        }
       itotCancel=listageneral.size();

    }
    public MutableLiveData<Integer> contarMuestraAdic(){
        totMuestraAdic=new MutableLiveData<>();
        // lista de compra pendiente
        List<ListaCompra> listacomp = cargarClientesSimplxetReacsc( 2,2,4);

        if(listacomp.size()>0){
            Log.d(TAG,"contarMuestraAdic-hey reactivacion");
            int informesdetList=0;
            //busco el detalle
            for (ListaCompra compra:listacomp
            ) {

                List<ListaCompraDetalle> compraDetalles = getProductosPend(compra.getId());
                if (compraDetalles != null && compraDetalles.size() > 0) {
                    for (ListaCompraDetalle detalle : compraDetalles
                    ) {

                        informesdetList++;
                    }
                }
            }

            totMuestraAdic.setValue(informesdetList);

        }else {
            listacomp = cargarClientesSimplxetReacsc( 3,2,4);
            if (listacomp != null && listacomp.size() > 0){
                Log.i(TAG, "contarMuestraAdic esta en etiquetado");
                //busco etiquetado
                List<InformeEtapa> informes = getEtiquetadoAdicional(Constantes.INDICEACTUAL);
                int informesfinal = 0;//contador para saber cuantos informes hay
                Log.i(TAG, "contarMuestraAdic YA CARGÓ " + informes.size());
                for (InformeEtapa infeta : informes
                ) {
                    //reviso que ya pueda hacer esa etapa
                    //busco los clientes x ciudad
                    listacomp = cargarClientesSimplxet(infeta.getCiudadNombre(), 3);
                    if (listacomp != null && listacomp.size() > 0 && listacomp.get(0) != null && listacomp.get(0).getClientesId() == infeta.getClientesId()) {

                        informesfinal++;
                    }
                }
                totMuestraAdic.setValue(informesfinal);
            }else
            //veo si ya puedo hacer empaque
            {
                listacomp =cargarClientesSimplxetReacsc( 4,2,4);
                Log.i(TAG, "contarMuestraAdic puedo hacer empaque?"+listacomp!=null?(listacomp.size()+""):"0" );
                int listageneral = 0; //para contar los informes
                for(ListaCompra listaCompra:listacomp) {

                    if (listacomp != null && listacomp.size() > 0 && listaCompra.getLis_reactivado() != null ) {
                        Log.i(TAG, "contarMuestraAdic hay reactivacion");
                        InformeEtapa informesEtapa = getInformeNoCancel(Constantes.INDICEACTUAL, 4, listaCompra.getCiudadNombre(), listaCompra.getClientesId());
                        if (informesEtapa == null) {

                            listageneral++;
                        }
                    }
                }

                totMuestraAdic.setValue(listageneral);
            }
        }
        return totMuestraAdic;
    }

    public void procesarEliminadas(MuestraCancelada cancelada){
        InformeCompraDetalle det=infcrepo.findsimple(cancelada.getInd_id());
        if(det!=null) {

            det.setMotivoCancel(cancelada.getVas_observaciones());
            det.setFechaCancel(cancelada.getVas_fecha());
            det.setEstatus(6);
            infcrepo.insert(det);
            infcrepo.actualizarEstatus(det.getId(), 6);

        }
    }
    public InformeEtapa getInformexPlantaEtaEst(int plantasId, int etapa, String indice,int estatus) {
        return infetarepo.getInformexPlantEst(indice,etapa,plantasId,0);
    }
    public List<InformeEtapa> getTotalCancelEtaSim(String indiceSel, int etapa ) {
        return infetarepo.getInformesxEstatusSim(indiceSel, etapa,0);
    }

    public MutableLiveData<Integer> getTotCancel() {
        return totCancel;
    }

    public InformeEtapa getInformeNoCancel(String indice, int etapa, String ciudadNombre, int clientesId){

        return   infetarepo.getInformeNoCancelxCiudad(indice, etapa, ciudadNombre, clientesId);


    }

    //para buscar si hay un inf de etiquetado reabierto
    public List<InformeEtapa> getInfEtapaxEstatusSim(String indiceSel, int etapa, int estatus ){

        return infetarepo.getInformesxEstatusSim(indiceSel,etapa,estatus);

    }
    public  List<ListaCompra>  cargarClientesSimplxet(String ciudadSel, int etapa){

        return lcrepo.getClientesByIndiceCiudadSimplxet(Constantes.INDICEACTUAL,ciudadSel,etapa);


    }


    public  List<ListaCompra>  cargarClientesSimplxetReacsc(int etapa, int reactivado1, int reactivado2){

        return lcrepo.getClieByIndiceSimplxetReac(Constantes.INDICEACTUAL,etapa,  reactivado1, reactivado2);


    }

    public  List<ListaCompra>  getListaCompraSimplexetapaReac(int etapa, int reactivado){

        return lcrepo.getListaCompraSimplexetapaReac(Constantes.INDICEACTUAL,etapa,  reactivado);


    }
    public List<ListaCompraDetalle> getProductosPend(int idlista) {
        return lcdrepo.getPendientes(idlista);
    }
    public List<InformeEtapa> getEtiquetadoAdicional(String indiceSel ){

        return infetarepo.getInformesxEstatusSim(indiceSel,3,4);

    }
    public InformeCompra getInformeSol(int informesId) {
        return infrepo.findSimple(informesId);
    }

    public InformeEnvioPaq getInformeEnvSol(int informesId) {
        return infenvrepo.findInfsimple(informesId);
    }
    public InformeGastoDet getByNumfoto(int idInforme, int numfoto) {
        return gasdetrepo.getByNumfoto(idInforme, numfoto);
    }




    public MutableLiveData<List<NotificacionGen>> pedirNotificacionesGenerales(String indice) {
        PeticionesServidor ps=new PeticionesServidor(Constantes.CLAVEUSUARIO);

       return ps.getNotificacionesGen(indice);

    }
    public List<InformeEtapa> getInfEtapax2Estatus(String indiceSel, int etapa, int estatus1, int estatus2 ){
        Log.i(TAG,"indideSel:"+indiceSel+" etapa:"+etapa+" estatus:"+estatus1);
        return infetarepo.getInformesx2EstatusSimple(indiceSel,etapa,estatus1, estatus2);

    }
}