package com.example.comprasmu.ui.etiquetado;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.comprasmu.ActInformeEtaTask;
import com.example.comprasmu.NavigationDrawerActivity;
import com.example.comprasmu.R;
import com.example.comprasmu.SubirInformeEtaTask;
import com.example.comprasmu.data.modelos.DescripcionGenerica;
import com.example.comprasmu.data.modelos.ImagenDetalle;
import com.example.comprasmu.data.modelos.InformeCompraDetalle;
import com.example.comprasmu.data.modelos.InformeEtapa;
import com.example.comprasmu.data.modelos.InformeEtapaDet;
import com.example.comprasmu.data.modelos.ListaCompra;
import com.example.comprasmu.data.remote.InformeEtapaEnv;
import com.example.comprasmu.services.SubirFotoService;
import com.example.comprasmu.services.UploadFotoWorker;
import com.example.comprasmu.ui.RevisarFotoActivity;
import com.example.comprasmu.ui.empaque.NvoEmpaqueFragment;
import com.example.comprasmu.ui.infetapa.NuevoInfEtapaActivity;
import com.example.comprasmu.ui.infetapa.NuevoInfEtapaViewModel;
import com.example.comprasmu.ui.listadetalle.ListaDetalleViewModel;
import com.example.comprasmu.ui.preparacion.NvaPreparacionViewModel;
import com.example.comprasmu.utils.ComprasLog;
import com.example.comprasmu.utils.ComprasUtils;
import com.example.comprasmu.utils.Constantes;
import com.example.comprasmu.utils.Preguntasino;
import com.example.comprasmu.utils.micamara.MiCamaraActivity;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

//las cajas se numeran x ciudad cliente
//ciudad x cajas 1,2,3 no importa el cliente
//ciudad y 1,2,3 no importa el cliente
public class NvoEtiqCajaFragment extends Fragment {

    private InformeEtapaDet detalleEdit;
    LinearLayout sv5, svpregcaja;
    private static final String TAG = "NvoEtiqCajaFragment";
    Button   btnneacfotocaj;
    //eliminarCaja
    Button  guardar;
    private long lastClickTime = 0;
    private boolean yaestoyProcesando = false;
    EditText ttxtrutacaja, txtcajaact, txtdescidfoto,txtrutacaja,txtcomentarios;
    TextView  txtdescfotocaj, txtcajafoto;
    ImageView  fotomoscaj;
    private ImageButton  btntomarf,  btnrotar2, btntomarcaja;
    public int REQUEST_CODE_TAKE_PHOTO = 1;
    NuevoInfEtapaViewModel niviewModel;
    private View root;
    private NvaPreparacionViewModel mViewModel;
    private int informeSel;

    private InformeEtapa infomeEdit;
    int preguntaAct;

    private static final int REQUEST_CODEQR = 345;

    int  contcajaf; //contador para fotos caja
    int  totcajas; //cajaact

    private boolean isEdicion;
    private boolean isActualizacion; //para saber si es actualizacion
    ComprasLog milog;
    List<String> spinnerValues;
    ArrayAdapter<String> adaptercaja;
    private TextView txttotcaj;
    public final static String ARG_PREGACT = "comprasmu.ne_pregactp";
    public final static String ARG_ESEDI = "comprasmu.ne_esedip";
    public final static String ARG_INFORMEDET = "comprasmu.ne_infdet";
    public final static String ARG_INFORMESEL = "comprasmu.neinfsel";
    public final static String ARG_ESACT = "comprasmu.necesact";
    public String ciudadInf;
    public String[] descripfoto = {"FOTO CALIDAD DE CAJA CARA A", "FOTO CALIDAD DE CAJA CARA B", "FOTO ACOMODO DE MUESTRAS DENTRO DE CAJA"};
    public int[] descripcionid = {12, 13, 14};
    private int cajainif; //caja actual
    private Button btnreubicar;

    public NvoEtiqCajaFragment() {

    }

    public static NvoEtiqCajaFragment newInstance() {
        return new NvoEtiqCajaFragment();
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_nvoetiqcaja, container, false);

        sv5 = root.findViewById(R.id.llnepre5);
        svpregcaja = root.findViewById(R.id.llnepregcaja);
        txttotcaj = root.findViewById(R.id.txtnetotcaj);
        txtcajafoto = root.findViewById(R.id.txtnecajafoto);
        txtcomentarios = root.findViewById(R.id.txtnecomentarios);
        //  svcoin = root.findViewById(R.id.llnecoincide);

        btnneacfotocaj = root.findViewById(R.id.btnneacfotocaj);
        guardar = root.findViewById(R.id.btnneguardar);
        btnrotar2 = root.findViewById(R.id.btnnerotar2);
        btntomarf = root.findViewById(R.id.btnnefoto);
        btntomarcaja = root.findViewById(R.id.btnnefotocaj);

        niviewModel = new ViewModelProvider(requireActivity()).get(NuevoInfEtapaViewModel.class);

        mViewModel = new ViewModelProvider(requireActivity()).get(NvaPreparacionViewModel.class);
        milog = ComprasLog.getSingleton();
        contcajaf=1;


        svpregcaja.setVisibility(View.GONE);

        fotomoscaj = root.findViewById(R.id.ivnefotocaj);

        txtdescidfoto = root.findViewById(R.id.txtnedescfotoid);
        txtdescfotocaj = root.findViewById(R.id.txtnedescfoto);
        // pcoincide=root.findViewById(R.id.sinonecoincide);
        // potra=root.findViewById(R.id.sinoneotramu);

        txtcajaact = root.findViewById(R.id.txtnecajaact);
        txtrutacaja = root.findViewById(R.id.txtnefotocaj);
        if (getArguments() != null) {
            // Log.d(TAG,"aqui");
            this.preguntaAct = getArguments().getInt(ARG_PREGACT);
            this.informeSel = getArguments().getInt(ARG_INFORMESEL);
            // mViewModel.setIdNuevo(this.informeSel);
            //BUSCAR DETALLE EDIT SI ES DIFERENTE DE NULL
            int detid = getArguments().getInt(ARG_INFORMEDET);
            InformeEtapaDet det = mViewModel.getDetalleEta(detid);
            if (det != null)
                this.detalleEdit = det;

            this.isEdicion = getArguments().getBoolean(ARG_ESEDI);
            this.isActualizacion = getArguments().getBoolean(ARG_ESACT);
        }
         Log.d(TAG,"aqui"+informeSel);
       // cajaini = 1;
        totcajas=0;
        //  txtcajaact=root.findViewById(R.id.txtnenumcaja);

        btnneacfotocaj.setEnabled(false);
       // txtcomentarios.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtcomentarios.setFilters(new InputFilter[]{new InputFilter.AllCaps(),new InputFilter.LengthFilter(200)});
        adaptercaja = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerValues);



        mViewModel.preguntaAct = preguntaAct;
        milog.grabarError(TAG + " o x aca"+informeSel);
        //busco si tengo varias plantas
        ciudadInf = Constantes.CIUDADTRABAJO;
        cajainif=1;
        if (isEdicion) { //busco el informe

            //busco el informe y el detalle

            infomeEdit = mViewModel.getInformexId(informeSel);
            if(this.detalleEdit==null)
                this.detalleEdit =niviewModel.getDetalleEtEdit(informeSel, 3);
           try {
               int totmuestras = mViewModel.getTotalMuestrasxCliXcd(infomeEdit.getClientesId(), Constantes.CIUDADTRABAJO);
                infomeEdit.setTotal_muestras(totmuestras);
               ((NuevoInfEtapaActivity) getActivity()).actualizarBarraEtiq(infomeEdit);
           }catch(ClassCastException ex){
               //entre por notificacion
            }
            mViewModel.setIdNuevo(informeSel);



            if (detalleEdit != null ) {
                editarCapCaja();
            }

        } else
        {
            capturarFotoCaja();
            contcajaf = 1; //para el for de cuantas veces pedir fotos

        }

        //lleno el total de cajas
          //me voy a fotos de caja
        svpregcaja.setVisibility(View.VISIBLE);
        sv5.setVisibility(View.GONE);



        //busco total de cajas final
        totcajas = mViewModel.getTotCajasEtiqxInf(this.informeSel);

        txttotcaj.setText("TOTAL CAJAS:" + totcajas);






        btnneacfotocaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnneacfotocaj.setEnabled(false);

                avanzar();


            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar.setEnabled(false);
                long currentClickTime = SystemClock.elapsedRealtime();
                // preventing double, using threshold of 1000 ms
                if (currentClickTime - lastClickTime < 5500) {
                    //  Log.d(TAG,"doble click :("+lastClickTime);
                    return;
                }

                lastClickTime = currentClickTime;
                if(isActualizacion){
                    actualizarInf();
                }else {
                    actualizarComent();
                    finalizarInf();
                }
                Toast.makeText(getActivity(), getString(R.string.informe_finalizado), Toast.LENGTH_SHORT).show();
                yaestoyProcesando = false;
                salir();


            }
        });

        btnrotar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotar(R.id.txtnefotocaj);
            }
        });

        btntomarcaja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFoto(2);
            }
        });


        return root;
    }


public void iraReubicar(){
    Intent intento1 = new Intent(getActivity(), NavigationDrawerActivity.class);
    intento1.putExtra(NavigationDrawerActivity.NAVINICIAL,"nav_reubicetiq");
    startActivity(intento1);
    getActivity().finish();
}



    public void avanzar() {
        //Log.d(TAG, "--" + preguntaAct);
       // Log.d(TAG, "ananza"+ contcajaf+" <="+ totcajas);
     //   Log.d(TAG, "cajaact "+ cajainif);

        switch (preguntaAct) {

            case 5:
            case 6://foto caja
                guardarDetCaja();
                svpregcaja.setVisibility(View.VISIBLE);
                preguntaAct = preguntaAct + 1;
                capturarFotoCaja();
                break;
            case 7: //foto caja

                guardarDetCaja();
                svpregcaja.setVisibility(View.VISIBLE);


                //si es la ultima foto de la caja y hay más cajas avanzo
                if (contcajaf < totcajas) {
                    preguntaAct = 5;
                    contcajaf++;
                    cajainif++;

                    capturarFotoCaja();
                } else //no hay más cajas me voy a comentarios
                {
                    svpregcaja.setVisibility(View.GONE);
                    txttotcaj.setText("TOTAL CAJAS:" + totcajas);
                    sv5.setVisibility(View.VISIBLE);
                    preguntaAct = preguntaAct + 1;
                }
                break;

            case 8: //comentarios

                preguntaAct = preguntaAct + 1;

                //  pantallaPlantas();
                salir();
                break;
        }

        mViewModel.preguntaAct = preguntaAct;
    }


    public void capturarFotoCaja() {

        // ver si ya existe esta foto
      //  Log.d(TAG,"capturarFoto"+(preguntaAct - 5));
        mostrarCapturaCajaxDesc(descripfoto[preguntaAct - 5]);
      //  mViewModel.preguntaAct = preguntaAct;
        //busco descripcion
        txtdescfotocaj.setText(descripfoto[preguntaAct - 5]);
        txtdescidfoto.setText(descripcionid[preguntaAct - 5] + "");
        txtcajafoto.setText("CAJA " + cajainif);//la caja actual
        txtcajaact.setText(cajainif + "");
        txtrutacaja.setText("");
        fotomoscaj.setImageBitmap(null);


    }


    public void editarCapCaja() {

        svpregcaja.setVisibility(View.VISIBLE);
        int primerCaja =1;
        Log.d(TAG,"primer caja"+primerCaja);
        //veo en que pregunta voy de acuerdo a la descripcion


        ImagenDetalle foto;


            //busco en la bd para regresar a la primer muestra
            foto = mViewModel.getFoto(Integer.parseInt(detalleEdit.getRuta_foto()));
            txtrutacaja.setText(foto.getRuta());
            Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + foto.getRuta(), 80, 80);
            fotomoscaj.setImageBitmap(bitmap1);
            fotomoscaj.setVisibility(View.VISIBLE);
            btnrotar2.setVisibility(View.VISIBLE);
            btnneacfotocaj.setEnabled(true);
            // contmuestra = detalleEdit.getNum_muestra();
            //veo en que pregunta voy de acuerdo a la descripcion

            preguntaAct = detalleEdit.getDescripcionId()-7;
             mViewModel.preguntaAct = preguntaAct;
            cajainif = detalleEdit.getNum_caja();
            txtdescfotocaj.setText(descripfoto[preguntaAct - 5]);
            txtdescidfoto.setText(descripcionid[preguntaAct - 5] + "");
            txtcajafoto.setText("CAJA " + cajainif);//la caja actual
            txtcajaact.setText(cajainif + "");

        //empiezo el cont en la caja actual
        contcajaf=cajainif;
            // totcajas=infomeEdit.getTotal_cajas();

        Log.e(TAG, "contcajaf" + contcajaf+"--"+preguntaAct);

    }

    public void mostrarCapturaCajaxDesc(String numdescr) {
        Log.d(TAG,"-----"+numdescr);
        //busco la foto anterior
        InformeEtapaDet informeEtapaDet= mViewModel.getDetallexDescCajaSim(informeSel,numdescr,cajainif);
        Log.d(TAG,"-----"+informeEtapaDet);
         if(informeEtapaDet!=null) {
             detalleEdit=informeEtapaDet;
             isEdicion=true;

             txtdescfotocaj.setText(detalleEdit.getDescripcion());
             txtdescidfoto.setText(detalleEdit.getDescripcionId() + "");
             //busco la foto
             ImagenDetalle foto = mViewModel.getFoto(Integer.parseInt(detalleEdit.getRuta_foto()));
             if(foto!=null) {
                 Log.d(TAG, "aqui esta " + foto.getRuta());
                 txtrutacaja.setText(foto.getRuta());

                 Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + foto.getRuta(), 80, 80);
                 fotomoscaj.setImageBitmap(bitmap1);
                 fotomoscaj.setVisibility(View.VISIBLE);
                 btnrotar2.setVisibility(View.VISIBLE);
                 btnneacfotocaj.setEnabled(true);
             }
         }
         txtcajafoto.setText("CAJA " + cajainif);//la caja actual
         txtcajaact.setText(cajainif + "");
        // lddetalle.removeObservers(getViewLifecycleOwner());



    }

    public void atras(){
        Log.d(TAG,"**atras contcajaf"+contcajaf);
        Log.d(TAG,"**atras preguntaAct"+preguntaAct);
        isEdicion=true; //siempre es edicion
        switch (preguntaAct){

            case 5://foto cara a

                //puede ser que tenga que volver a otra caja
                if(cajainif>1){
                    preguntaAct=7;
                    mViewModel.preguntaAct=preguntaAct;
                    contcajaf--;
                    cajainif--;
                    mostrarCapturaCajaxDesc(descripfoto[2]);
                }else {

                    mViewModel.preguntaAct = preguntaAct;
                    //voy a otro fragmento
                    //editarMuestra();
                    return;
                }
                break;

            case 6://foto cara b
                preguntaAct=preguntaAct-1;
                mViewModel.preguntaAct=preguntaAct;
                mostrarCapturaCajaxDesc(descripfoto[0]);

                break;
            case 7://acomodo

                preguntaAct=preguntaAct-1;
                mViewModel.preguntaAct=preguntaAct;
                mostrarCapturaCajaxDesc(descripfoto[1]);
                break;
            case 8://comentarios
                svpregcaja.setVisibility(View.VISIBLE);
                sv5.setVisibility(View.GONE);
                preguntaAct=preguntaAct-1;
                mViewModel.preguntaAct=preguntaAct;
                mostrarCapturaCajaxDesc(descripfoto[2]);
                break;
        }
        Log.d(TAG,"**"+preguntaAct);

    }

    private void editarMuestra() {
        Bundle args = new Bundle();
        args.putInt(NvoEtiquetadoFragment.ARG_PREGACT,3 );
        args.putBoolean(NvoEtiquetadoFragment.ARG_ESEDI,true);

        args.putInt(NvoEtiquetadoFragment.ARG_INFORMESEL,this.informeSel);
        NvoEtiquetadoFragment nvofrag = new NvoEtiquetadoFragment();
        nvofrag.setArguments(args);

            //busco la pregunta actual en la decripcion

       // args.putInt(NvoEtiquetadoFragment.ARG_INFORMEDET,det.getId() );

       FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
// Definir una transacción
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
// Remplazar el contenido principal por el fragmento
        fragmentTransaction.replace(R.id.continfeta_fragment, nvofrag);
        //     fragmentTransaction.addToBackStack(null);
// Cambiar
        fragmentTransaction.commit();
    }


    public void guardarDetCaja(){
        try{
            String rutafoto = null;
            rutafoto=txtrutacaja.getText().toString();
            int descripfotoid=0;
            String descripfoto=txtdescfotocaj.getText().toString();
            int numcaja=0;
            try {
                 numcaja = Integer.parseInt(txtcajaact.getText().toString());
                 descripfotoid=Integer.parseInt(txtdescidfoto.getText().toString());
            }catch (NumberFormatException ex){
                milog.grabarError(TAG+" guardarDetCaja NumberFormatException"+ex.getMessage());
            }

            if(isEdicion&&detalleEdit!=null){
                mViewModel.actualizarEtiqDet(mViewModel.getIdNuevo(),descripfotoid,descripfoto,rutafoto,detalleEdit.getId(),numcaja,null,0,detalleEdit.getRuta_foto(),Constantes.INDICEACTUAL);
                isEdicion=false;

            }else
            if(mViewModel.getIdNuevo()>0)
                //guardo el detalle
                mViewModel.insertarEtiqDet(mViewModel.getIdNuevo(),descripfotoid,descripfoto,rutafoto,0,numcaja,null,0,Constantes.INDICEACTUAL);
            //limpio campos
            txtrutacaja.setText("");
            txtcajaact.setText("");
            txtdescidfoto.setText("");
            fotomoscaj.setImageBitmap(null);
            fotomoscaj.setVisibility(View.GONE);
            btnrotar2.setVisibility(View.GONE);
            detalleEdit=null;
            btnneacfotocaj.setEnabled(false);

        }catch (Exception ex){
            ex.printStackTrace();
            Log.e(TAG,"Algo salió mal al guardarDet"+ex.getMessage());
            Toast.makeText(getContext(),"Hubo un error al guardar intente de nuevo",Toast.LENGTH_SHORT).show();

        }

    }

    public void finalizarInf() {
        try {
            mViewModel.finalizarInf();
            InformeEtapaEnv envio=mViewModel.preparaInformeEtiq(mViewModel.getIdNuevo());
            SubirInformeEtaTask miTareaAsincrona = new SubirInformeEtaTask(envio,getActivity());
            miTareaAsincrona.execute();
            subirFotos(getActivity(),envio);
        }catch(Exception ex){
            ex.printStackTrace();
            Log.e(TAG,"Algo salió mal al finalizar"+ex.getMessage());
            Toast.makeText(getContext(),"Algo salio mal al enviar",Toast.LENGTH_SHORT).show();
        }
        // limpio variables de sesion

        mViewModel.setIdNuevo(0);
        mViewModel.setIddetalle(0);
        mViewModel.setNvoinforme(null);

    }

    public void actualizarInf() {
        try {
            //actualizo total muestras
            InformeEtapa informe=mViewModel.getInformexId(mViewModel.getIdNuevo());
           int totalmues= mViewModel.getTotalMuestrasxCliXcd(informe.getClientesId(), ciudadInf);
            informe.setTotal_muestras(totalmues);
            mViewModel.actualizarInfEtapa(informe);
            mViewModel.finalizarInf();
            InformeEtapaEnv envio=mViewModel.preparaInformeEtiqAct(mViewModel.getIdNuevo());
            ActInformeEtaTask miTareaAsincrona = new ActInformeEtaTask(envio,getActivity());
            miTareaAsincrona.execute("","act");
            subirFotos(getActivity(),envio);
            //cambio el estatus para que no vuelva a pedir en el task
           if(envio.getInformeEtapaDet()!=null)
            for (InformeEtapaDet iddet:
                    envio.getInformeEtapaDet()) {


                mViewModel.actualizarEstatusDet(iddet.getId(),1);
            }
            //necesito tiempo para no actualizar el estatus
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            Log.e(TAG,"Algo salió mal al finalizar"+ex.getMessage());
            Toast.makeText(getContext(),"Algo salio mal al enviar",Toast.LENGTH_SHORT).show();
        }
        // lmpio variables de sesion

        mViewModel.setIdNuevo(0);
        mViewModel.setIddetalle(0);
        mViewModel.setNvoinforme(null);

    }
    public void actualizarComent(){

        String comentarios = null;

        if(txtcomentarios.getText()!=null)
            comentarios=txtcomentarios.getText().toString().toUpperCase();

        try{
            mViewModel.actualizarComentEtiq(mViewModel.getIdNuevo(),comentarios);
            //  mViewModel.actualizarMuestras(mViewModel.getIdNuevo(), contmuestra-1);
        }catch(Exception ex){
            ex.printStackTrace();
            Log.e(TAG,"Algo salió mal al enviar"+ex.getMessage());
            Toast.makeText(getContext(),"Algo salio mal al enviar",Toast.LENGTH_SHORT).show();
        }
    }
    public void rotar(int idcampo){
        EditText txtruta = root.findViewById(idcampo);
        String foto=txtruta.getText().toString();
        if(ComprasUtils.getAvailableMemory(getActivity()).lowMemory)
        {
            Toast.makeText(getActivity(), "No hay memoria suficiente para esta accion", Toast.LENGTH_SHORT).show();

            return;
        }else
        {

            RevisarFotoActivity.rotarImagen(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" +foto,fotomoscaj);

        }
    }

    String nombre_foto;
    File archivofoto;
    public void tomarFoto(int REQUEST_CODE){
        REQUEST_CODE_TAKE_PHOTO=REQUEST_CODE;
        if(ComprasUtils.getAvailableMemory(getActivity()).lowMemory)
        {
            Toast.makeText(getActivity(), "No hay memoria suficiente para esta accion", Toast.LENGTH_SHORT).show();

            return;
        }else {
            Activity activity = this.getActivity();
            Intent intento1 = new Intent(getContext(), MiCamaraActivity.class);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");

            String dateString = format.format(new Date());
            String state = Environment.getExternalStorageState();

            File baseDirFile;
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                baseDirFile = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (baseDirFile == null) {
                    Toast.makeText(activity, "No se encontró almacenamiento externo", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(activity, "No se encontró almacenamiento externo", Toast.LENGTH_SHORT).show();
                return;
            }
            //  baseDir = baseDirFile.getAbsolutePath();

            try {
                nombre_foto = "img_" + Constantes.CLAVEUSUARIO + "_" + dateString + ".jpg";
                archivofoto = new File(baseDirFile, nombre_foto);
                Log.e(TAG, "****" + archivofoto.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(activity, "No se encontró almacenamiento externo", Toast.LENGTH_SHORT).show();
                return;

            }
            Uri photoURI = FileProvider.getUriForFile(activity,
                    "com.example.comprasmu.fileprovider",
                    archivofoto);
            intento1.putExtra(MediaStore.EXTRA_OUTPUT, archivofoto.getAbsolutePath()); //se pasa a la otra activity la referencia al archivo

            if (fotomoscaj != null) {

                startActivityForResult(intento1, REQUEST_CODE);

            }
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"vars"+requestCode +"--"+ nombre_foto);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            //   super.onActivityResult(requestCode, resultCode, data);

            if (archivofoto!=null&&archivofoto.exists()) {
                if(requestCode == REQUEST_CODE_TAKE_PHOTO) {

                    if(requestCode==2) //foto de caja
                        mostrarFoto(txtrutacaja,fotomoscaj,btnrotar2);
                }

                if(requestCode==2) {

                    btnneacfotocaj.setEnabled(true);
                }

            }
            else{
                Log.e(TAG,"Algo salió mal???");
            }


        }    else
        {
            Log.e(TAG,"Algo salió muy mal**");
        }

    }

    public void mostrarFoto( EditText textorut,ImageView xfotomos, ImageButton xbtnrotar){


        textorut.setText(nombre_foto);
        if(ComprasUtils.getAvailableMemory(getActivity()).lowMemory)
        {
            Toast.makeText(getActivity(), "No hay memoria suficiente para esta accion", Toast.LENGTH_SHORT).show();

            return;
        }else {
            // Bitmap bitmap1 = BitmapFactory.decodeFile(getActivity().getExternalFilesDir(null) + "/" + nombre_foto);
            ComprasUtils cu = new ComprasUtils();
            cu.comprimirImagen(archivofoto.getAbsolutePath());
            Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(archivofoto.getAbsolutePath(), 100, 100);
            xfotomos.setImageBitmap(bitmap1);
            // fotomos.setLayoutParams(new LinearLayout.LayoutParams(350,150));
            xfotomos.setVisibility(View.VISIBLE);

            xbtnrotar.setVisibility(View.VISIBLE);
            xbtnrotar.setFocusableInTouchMode(true);
            xbtnrotar.requestFocus();
            nombre_foto=null;
            archivofoto=null;
        }


    }
    public void salir(){
        //mViewModel.eliminarTblTemp();
        //me voy a la lista de informes
        getActivity().finish();
        Intent intento1 = new Intent(getActivity(), NavigationDrawerActivity.class);
        intento1.putExtra(NavigationDrawerActivity.NAVINICIAL,"listainformeeta");
        startActivity(intento1);
        // NavHostFragment.(this).navigate(R.id.action_selclientetolistacompras,bundle);


    }




    public  void subirFotos(Activity activity, InformeEtapaEnv informe){
        //las imagenes
        for(ImagenDetalle imagen:informe.getImagenDetalles()){
            //
            //subo cada una
          /*  Intent msgIntent = new Intent(activity, SubirFotoService.class);
            msgIntent.putExtra(SubirFotoService.EXTRA_IMAGE_ID, imagen.getId());
            msgIntent.putExtra(SubirFotoService.EXTRA_IMG_PATH,imagen.getRuta());
            msgIntent.putExtra(SubirFotoService.EXTRA_INDICE,informe.getIndice());
            // Constantes.INDICEACTUAL
            Log.d(TAG,"subiendo fotos"+activity.getLocalClassName());

            msgIntent.setAction(SubirFotoService.ACTION_UPLOAD_ETA);

            //cambio su estatus a subiendo
            imagen.setEstatusSync(1);
            activity.startService(msgIntent);*/

            //cambiar el fotoservice por un workmanager que gestiona todo

            Constraints restricciones = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            // Pasar los IDs necesarios
            Data inputData = new Data.Builder()
                    .putInt(UploadFotoWorker.EXTRA_IMAGE_ID, imagen.getId())
                    .putString(UploadFotoWorker.EXTRA_IMG_PATH,imagen.getRuta())
                    .putString(UploadFotoWorker.EXTRA_INDICE,informe.getIndice())
                    .build();

            // Crear la petición
            OneTimeWorkRequest uploadRequest = new OneTimeWorkRequest.Builder(UploadFotoWorker.class)
                    .setConstraints(restricciones)
                    .setInputData(inputData)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 60, TimeUnit.SECONDS) // Reintentos inteligentes
                    .build();

            // Encolar
            if(getContext()!=null)
                WorkManager.getInstance(getContext()).enqueue(uploadRequest);


        }

    }


    class BotonTextWatcher implements TextWatcher {

        boolean mEditing;
        Button aceptar;
        public BotonTextWatcher() {
            mEditing = false;
        }
        public BotonTextWatcher(Button botonac) {
            mEditing = false;
            aceptar=botonac;
        }
        public synchronized void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //count es cantidad de caracteres que tiene
            aceptar.setEnabled(charSequence.length() > 0);

        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel = null;

        root=null;

        fotomoscaj=null;
        svpregcaja=sv5=null;
        btnrotar2=null;

        nombre_foto=null;
        archivofoto=null;
    }




}

