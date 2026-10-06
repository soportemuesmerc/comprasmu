package com.example.comprasmu.ui.preparacion;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comprasmu.NavigationDrawerActivity;
import com.example.comprasmu.R;
import com.example.comprasmu.SubirInformeEtaTask;
import com.example.comprasmu.data.modelos.DescripcionGenerica;
import com.example.comprasmu.data.modelos.ImagenDetalle;
import com.example.comprasmu.data.modelos.InformeEtapa;
import com.example.comprasmu.data.modelos.InformeEtapaDet;
import com.example.comprasmu.data.modelos.ListaCompra;
import com.example.comprasmu.data.modelos.Reactivo;
import com.example.comprasmu.data.remote.InformeEtapaEnv;
import com.example.comprasmu.services.SubirFotoService;
import com.example.comprasmu.services.UploadFotoWorker;
import com.example.comprasmu.ui.RevisarFotoActivity;
import com.example.comprasmu.ui.infetapa.NuevoInfEtapaActivity;
import com.example.comprasmu.ui.infetapa.NuevoInfEtapaViewModel;
import com.example.comprasmu.ui.listadetalle.ListaDetalleViewModel;
import com.example.comprasmu.utils.CampoForm;
import com.example.comprasmu.utils.ComprasLog;
import com.example.comprasmu.utils.ComprasUtils;
import com.example.comprasmu.utils.Constantes;
import com.example.comprasmu.utils.CreadorFormulario;
import com.example.comprasmu.utils.Preguntasino;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.comprasmu.utils.micamara.MiCamaraActivity;
import static android.app.Activity.RESULT_OK;

public class NvaPreparacionFragment extends Fragment {

    private NvaPreparacionViewModel mViewModel;
    private View root;
    boolean isEdicion;
    private static final String TAG="NvaPrparacionFRAG";
    EditText textoint;
    ImageView fotomos;
    public static  int REQUEST_CODE_TAKE_PHOTO=1;
    private ImageButton btnrotar;
    String  ultimares;
    Button aceptar;
    private int plantaSel;
    private String nombrePlantaSel;
    private long lastClickTime = 0;
    private boolean yaestoyProcesando=false;
    public final static String ARG_NUEVOINFORME="comprasmu.ni_idinforme";
    private int preguntaAct;
    CreadorFormulario cf;
    List<CampoForm> camposForm;
    LinearLayout sv;
    private String clienteNombre;
    private int clienteId;
    private int preguntaSig;
    List<ListaCompra> listacomp;
    private ListaDetalleViewModel lcViewModel;
    private  ArrayList<DescripcionGenerica> listaPlantas;
    Spinner spclientes ;
    private InformeEtapa informeEdit;
    InformeEtapaDet detalleEdit;
    private NuevoInfEtapaViewModel infvm;
    int informesel;
    public final static String ARG_PREGACT="comprasmu.np_pregactp";
    public final static String ARG_ESEDI="comprasmu.np_esedip";
    public final static String ARG_INFORMEDET="comprasmu.np_infdet";
    ComprasLog complog;
    public static NvaPreparacionFragment newInstance() {
        return new NvaPreparacionFragment();
    }
    /*public NvaPreparacionFragment(int preguntaAct,boolean edicion, InformeEtapaDet informeEdit) {
        this.preguntaAct = preguntaAct;
        this.isEdicion=edicion;
        this.detalleEdit=informeEdit;
    }*/
    public NvaPreparacionFragment() {

    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.fragment_generic, container, false);
        sv = root.findViewById(R.id.content_generic);
        aceptar = root.findViewById(R.id.btngaceptar);
        mViewModel = new ViewModelProvider(requireActivity()).get(NvaPreparacionViewModel.class);
        lcViewModel=new ViewModelProvider(this).get(ListaDetalleViewModel.class);
        infvm =
                new ViewModelProvider(requireActivity()).get(NuevoInfEtapaViewModel.class);

        complog=ComprasLog.getSingleton();
        if (getArguments() != null) {

            this.preguntaAct = getArguments().getInt(ARG_PREGACT);
            this.isEdicion = getArguments().getBoolean(ARG_ESEDI);
            // mViewModel.setIdNuevo(this.informeSel);
            //BUSCAR DETALLE EDIT SI ES DIFERENTE DE NULL
            int detid = getArguments().getInt(ARG_INFORMEDET);
            InformeEtapaDet det = mViewModel.getDetalleEta(detid);
            if(det!=null)
                this.detalleEdit=det;
        }
        //busco si tengo varias plantas
        listacomp= lcViewModel.cargarPestanasSimp(Constantes.CIUDADTRABAJO);
        if(listacomp!=null) {
            if (!isEdicion && mViewModel.getIdNuevo() == 0)
                if (listacomp.size() > 1) {
                    //tengo varias plantas
                    preguntaAct = 0;
                    preguntaSig = 1;
                    convertirLista(listacomp);
                    mViewModel.variasClientes = true;
                } else if (listacomp.size() > 0) {
                    mViewModel.variasClientes = false;
                    //tengo el nombre de la planta y cliente

                    nombrePlantaSel = listacomp.get(0).getPlantaNombre();
                    plantaSel = listacomp.get(0).getPlantasId();
                    //reviso si ya tengo informe
                    InformeEtapa inf=mViewModel.getInformexPlantaEta(plantaSel,1,Constantes.INDICEACTUAL,0);
                    if(inf!=null&&inf.getEstatus()!=4) {
                        aceptar.setEnabled(false);
                        Toast.makeText(getActivity(), "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                        return root;
                    }
                    clienteId = listacomp.get(0).getClientesId();
                    clienteNombre = listacomp.get(0).getClienteNombre();
                    InformeEtapa informetemp = new InformeEtapa();
                    informetemp.setClienteNombre(clienteNombre);
                    informetemp.setClientesId(clienteId);
                    informetemp.setPlantasId(plantaSel);
                    informetemp.setPlantaNombre(nombrePlantaSel);
                    informetemp.setIndice(Constantes.INDICEACTUAL);
                    ((NuevoInfEtapaActivity) getActivity()).actualizarBarra(informetemp);
                } else
                    Log.e(TAG, "algo salió mal con la consulta de listas");

        }
        else{
            aceptar.setEnabled(false);
            Toast.makeText(getActivity(), "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
            return root;
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        //   lcrepo.getClientesByIndiceCiudad(Constantes.INDICEACTUAL,ciudadNombre).observe(getViewLifecycleOwner(), nameObserver);
        if(!isEdicion&&preguntaAct<2&&mViewModel.getIdNuevo()==0) {
            //es nuevo reviso si ya tengo uno abierto
            InformeEtapa informeEtapa = mViewModel.getInformePendPrep(Constantes.INDICEACTUAL);
            Log.d(TAG, "buscando pend");
            if (informeEtapa != null) {
                Log.d(TAG, "encontré 1");
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getActivity());
                dialogo1.setTitle(R.string.atencion);
                dialogo1.setMessage(R.string.informe_abierto);
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //lo mando a continuar
                        getActivity().finish();

                    }
                });

                dialogo1.show();
            }
        }
        if(isEdicion&&preguntaAct<100) {
            aceptar.setEnabled(true);
            Bundle datosRecuperados = getArguments();

            if(datosRecuperados!=null) { //vengo de continuar
                informesel= datosRecuperados.getInt(NuevoInfEtapaActivity.INFORMESEL);
            }

            if(preguntaAct==6){ //comentarios
                if(detalleEdit!=null){
                    //busco aqui el idinforme
                    mViewModel.setIdNuevo(detalleEdit.getInformeEtapaId());


                }
                //solo traigo el informe necesito el id
                mViewModel.getInformeEdit(mViewModel.getIdNuevo()).observe(getViewLifecycleOwner(), new Observer<InformeEtapa>() {
                    @Override
                    public void onChanged(InformeEtapa informeEtapa) {
                        if(informeEtapa!=null)
                        { ultimares=informeEtapa.getComentarios();
                            informeEdit=informeEtapa;}
                        crearFormulario();
                    }
                });

            }else if(preguntaAct==0) { //lista plantas
                if(listacomp==null){
                    Toast.makeText(getActivity(), "No hay mas plantas", Toast.LENGTH_SHORT).show();
                    return root;
                }
                convertirLista(listacomp);
                mViewModel.variasClientes =true;
                if(informesel>0)
                    mViewModel.setIdNuevo( informesel);
                Log.e(TAG,"--"+mViewModel.getIdNuevo());
                //solo traigo el informe necesito el id
                mViewModel.getInformeEdit(mViewModel.getIdNuevo()).observe(getViewLifecycleOwner(), new Observer<InformeEtapa>() {
                    @Override
                    public void onChanged(InformeEtapa informeEtapa) {
                        if(informeEtapa!=null) {
                            ultimares = informeEtapa.getPlantasId() + "";
                        }
                        informeEdit = informeEtapa;
                        mViewModel.setNvoinforme(informeEdit);
                        crearFormulario();
                        spclientes = root.findViewById(1001);
                        if(spclientes!=null){
                            spclientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                    aceptar.setEnabled(true);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                    // your code here
                                }

                            });
                        }
                    }
                });


            }else
            {
                if(detalleEdit!=null){

                    //busco aqui el idinforme
                    ultimares=detalleEdit.getRuta_foto();
                    mViewModel.setIdNuevo(detalleEdit.getInformeEtapaId());
                    mViewModel.getInformeEdit(mViewModel.getIdNuevo()).observe(getViewLifecycleOwner(), new Observer<InformeEtapa>() {
                        @Override
                        public void onChanged(InformeEtapa informeEtapa) {

                            informeEdit=informeEtapa;
                            crearFormulario();
                        }
                    });

                }else
                    mViewModel.getDetalleEtEdit(mViewModel.getIdNuevo(),preguntaAct).observe(getViewLifecycleOwner(), new Observer<InformeEtapaDet>() {
                        @Override
                        public void onChanged(InformeEtapaDet informeEtapaDet) {
                            Log.d(TAG,"edicion detalledit"+informeEtapaDet.getRuta_foto());
                            if(informeEtapaDet!=null)
                                ultimares=informeEtapaDet.getRuta_foto();
                            detalleEdit = informeEtapaDet;
                            crearFormulario();
                        }
                    });
            }
        }
        else {
            if(preguntaAct>100){
                //es pregunta si no, no guardo nada
                crearPregunta();
            }else {
                if (preguntaAct < 6)
                    aceptar.setEnabled(false);
                crearFormulario();
            }
        }

        Log.d(TAG,"p*************aso x aqui"+mViewModel.getIdNuevo());
        if(textoint!=null){ //los comentarios no son obligatorios
            if(preguntaAct<6)
                textoint.addTextChangedListener(new NvaPreparacionFragment.BotonTextWatcher());
            else //solo los comentarios a mayusculas
                textoint.setFilters(new InputFilter[]{new InputFilter.AllCaps(),new InputFilter.LengthFilter(200)});


        }

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aceptar.setEnabled(false);
                long currentClickTime= SystemClock.elapsedRealtime();
                // preventing double, using threshold of 1000 ms
                if (currentClickTime - lastClickTime < 5500){
                    //  Log.d(TAG,"doble click :("+lastClickTime);
                    return;
                }

                lastClickTime = currentClickTime;

                if(preguntaAct==0){
                    guardarCliente();
                }
                else
                    procesarResp();

            }
        });

        if(preguntaAct==6){
            //cambio el boton a finalizar y muestro alerta
            aceptar.setText(getString(R.string.enviar));
            aceptar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.botonvalido));
        }
        infvm.cont =preguntaAct;
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        if (Constantes.CIUDADTRABAJO == null || Constantes.CIUDADTRABAJO.equals("")) {
            //falta definir
            Toast.makeText(getActivity(), "Falta definir ciudad de trabajo", Toast.LENGTH_SHORT).show();

            Intent intento1 = new Intent(getActivity(), NavigationDrawerActivity.class);
            intento1.putExtra(NavigationDrawerActivity.NAVINICIAL,"nav_ciudad_trabajo");
            intento1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intento1);
            getActivity().finish();
        }
    }


    class BotonTextWatcher implements TextWatcher {

        boolean mEditing;

        public BotonTextWatcher() {
            mEditing = false;
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
    public void Reactivos(){

        List<Reactivo> camposForm = new ArrayList<Reactivo>();
        Reactivo foto1=new Reactivo();
        foto1.setId(1);
        foto1.setLabel("FOTO ");
        foto1.setSigId(2);
        foto1.setType("agregarImagen");
        foto1=new Reactivo();
        foto1.setLabel(getString(R.string.comentarios));
        foto1.setNombreCampo("comentarios");
        foto1.setType("textarea");
        foto1.setId(2);

        camposForm.add(foto1);

    }
    //btnaifotoexhibido
    public void crearFormulario(){
        camposForm=new ArrayList<>();
        CampoForm campo=new CampoForm();
        if(preguntaAct==0)
        {
            campo.label=getString(R.string.seleccione_planta);
            campo.nombre_campo="planta";
            campo.type="selectDes";
            campo.style=R.style.formlabel2;
            campo.id=1001;
            if(isEdicion)
                campo.value=ultimares;

            cargarPlantas(campo);


        }else
        if(preguntaAct<6){ //es para foto
            campo.label="ETIQUETAS Y/O MATERIALES FOTO "+preguntaAct;
            campo.nombre_campo="foto";
            campo.type="agregarImagen";
            campo.style=R.style.formlabel2;


            campo.id=1001;
            campo.funcionOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tomarFoto();
                }
            };
            campo.tomarFoto = true;

            fotomos=root.findViewById(R.id.ivgfoto);
            fotomos.setVisibility(View.VISIBLE);
            btnrotar=root.findViewById(R.id.btngrotar);
            btnrotar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rotar();
                }
            });

            if(isEdicion&&ultimares!=null&&!ultimares.equals("")){
                //busco la ruta en imagenes fotos
                Log.d(TAG,"buscando foto "+ultimares);
                ImagenDetalle foto=null;
                try {
                    foto = mViewModel.getFoto(Integer.parseInt(ultimares));
                }catch (NumberFormatException ex){
                    complog.grabarError(ex.getMessage());
                }
                // Bitmap bitmap1 = BitmapFactory.decodeFile(getActivity().getExternalFilesDir(null) + "/" + nombre_foto);
                //ComprasUtils cu=new ComprasUtils();
                // bitmap1=cu.comprimirImagen(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + ultimares.getValor());
                if(foto!=null) {
                    Log.d(TAG,"buscando foto "+foto.getRuta());
                    Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + foto.getRuta(), 100, 100);
                    campo.value = foto.getRuta();
                    fotomos.setImageBitmap(bitmap1);
                    // fotomos.setLayoutParams(new LinearLayout.LayoutParams(350,150));
                    fotomos.setVisibility(View.VISIBLE);

                    btnrotar.setVisibility(View.VISIBLE);
                    btnrotar.setFocusableInTouchMode(true);
                    btnrotar.requestFocus();
                }

            }
            // btnrotar.setVisibility(View.VISIBLE);


        }else{ //son los comentarios
            campo.label=getString(R.string.comentarios);
            campo.nombre_campo="comentarios";
            campo.type="textarea";
            campo.style=R.style.formlabel2;
            if(isEdicion)
                campo.value=ultimares;
            campo.id=1001;


        }

        campo.id=1001;
        camposForm.add(campo);
        Log.d(TAG,"haciendo form");
        cf=new CreadorFormulario(camposForm,getContext());
        sv.addView(cf.crearFormulario());
        if(preguntaAct==0) {
            spclientes = root.findViewById(1001);
            if(spclientes!=null){
                spclientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        aceptar.setEnabled(true);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });
            }
        }else
            textoint = root.findViewById(1001);
    }
    public void crearPregunta(){
        camposForm=new ArrayList<>();
        CampoForm campo=new CampoForm();
        campo.label=getString(R.string.req_foto);
        campo.nombre_campo="otrafoto";
        campo.type="preguntasino";
        campo.style=R.style.formlabel2;
        campo.id=1001;

        camposForm.add(campo);

        cf=new CreadorFormulario(camposForm,getContext());
        sv.addView(cf.crearFormulario());

    }
    public void guardarCliente(){
        lastClickTime=0;
        DescripcionGenerica opcionsel = (DescripcionGenerica) spclientes.getSelectedItem();
        if(opcionsel!=null) {
            //busco par de id, cliente
            String[] aux = opcionsel.getDescripcion().split(",");
            clienteId = Integer.parseInt(aux[0]);
            clienteNombre = aux[1];
            plantaSel = opcionsel.getId();
            nombrePlantaSel = opcionsel.getDescripcion2();
        }
        else{
            Toast.makeText(getActivity(), "Seleccione el cliente", Toast.LENGTH_SHORT).show();
            return ;
        }

        //creo el informe
        if ( !isEdicion&&mViewModel.getNvoinforme()==null) {
            Log.d(TAG, "creando nvo inf");

            mViewModel.setIdNuevo( mViewModel.insertarInformeEtapa(Constantes.INDICEACTUAL, nombrePlantaSel, plantaSel, clienteNombre, clienteId));
        } //   Log.d(TAG, "guardando informe"+mViewModel.numMuestra+"--"+mViewModel.getIdInformeNuevo());
        else{ //si es edicion
            //actualizo el cliente y la planta
            Log.d(TAG, "creando nvo inf---"+mViewModel.getNvoinforme().toString());

            mViewModel.getNvoinforme().setClienteNombre(clienteNombre);
            mViewModel.getNvoinforme().setClientesId(clienteId);
            mViewModel.getNvoinforme().setPlantasId(plantaSel);
            mViewModel.getNvoinforme().setPlantaNombre(nombrePlantaSel);
            mViewModel.actualizarInfEtapa(mViewModel.getNvoinforme());


        }
        ((NuevoInfEtapaActivity)getActivity()).actualizarBarra(mViewModel.getNvoinforme());
        avanzarPregunta(1);



    }
    public void procesarResp(){ //antes siguiente

        lastClickTime=0;
        aceptar.setEnabled(false);
        if(preguntaAct>100) {
            //busco la respuesta
            Preguntasino resp=root.findViewById(1001);
            if(resp.getRespuesta())
                avanzarPregunta(preguntaAct-100);
            else
                avanzarPregunta(6);//fin y voy a los comentarios
            return;
        }
        if(preguntaAct==6) //termine inf comentarios
        {
            if(yaestoyProcesando)
                return;
            yaestoyProcesando=true;
            //  loadingDialog = new LoadingDialog(getActivity());
            //   loadingDialog.startLoadingDialog();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                //guarda informe
                this.actualizarInforme();
                this.finalizar();
                //  mViewModel.eliminarTblTemp();
                //   loadingDialog.dismisDialog();
                Toast.makeText(getActivity(), getString(R.string.informe_finalizado), Toast.LENGTH_SHORT).show();
                yaestoyProcesando = false;
                salir();
                //  aceptar.setEnabled(true);
                return;


            }catch(Exception ex){
                ex.printStackTrace();
                yaestoyProcesando=false;
                complog.grabarError(TAG+"hubo un error al finalizar inf "+ex.getMessage());
                Toast.makeText(getActivity(), "algo salió mal", Toast.LENGTH_LONG).show();

            }

        }else
        {
            Log.d(TAG,"-----preguntaact"+preguntaAct);

            if(preguntaAct>0)
                guardarResp(preguntaAct+101);
            // avanzarPregunta(preguntaAct++);

        }
        aceptar.setEnabled(true);
    }
    //finalizar informe
    public void finalizar() {
        try {

            mViewModel.finalizarInf();

            InformeEtapaEnv informe=this.preparaInforme();
            if(informe.getInformeEtapa().getEstatus()==2)//ya está finalizado
            { SubirInformeEtaTask miTareaAsincrona = new SubirInformeEtaTask(informe,getActivity());
                miTareaAsincrona.execute();
                subirFotos(getActivity(),informe);}
            //necesito ver que sea una correccion
            //busco informe de la misma planta cancelado
            InformeEtapa inf=mViewModel.getInformexPlantaEtaEst(informe.getInformeEtapa().getPlantasId(),1,Constantes.INDICEACTUAL,0);
            if(inf!=null){
                //actualizo a 4 //corregido
                inf.setEstatus(4);
                mViewModel.actualizarInfEtapa(inf);
            }
           /* InformeEtapa inf2=mViewModel.getInformexPlantaEtaEst(informe.getInformeEtapa().getPlantasId(),1,Constantes.INDICEACTUAL,4);
            if(inf2!=null) {
                //actualizo a 4 //corregido
             Log.d(TAG,">>>>cambie estatus "+inf2.getEstatus());
            }*/

        }catch(Exception ex){
            ex.getStackTrace();
            Log.e(TAG,"Algo salió mal al enviar"+ex.getMessage());
            Toast.makeText(getContext(),"Algo salio mal al enviar",Toast.LENGTH_SHORT).show();
        }
        //limpio variables de sesion
        infvm.cont=0;
        mViewModel.setIdNuevo(0);
        mViewModel.setIddetalle(0);
        mViewModel.setNvoinforme(null);

    }

    public void rotar(){
        String foto=textoint.getText().toString();
        if(ComprasUtils.getAvailableMemory(getActivity()).lowMemory)
        {
            Toast.makeText(getActivity(), "No hay memoria suficiente para esta accion", Toast.LENGTH_SHORT).show();

            return;
        }else
            RevisarFotoActivity.rotarImagen(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" +foto,fotomos);
    }
    String nombre_foto;
    File archivofoto;
    public void tomarFoto(){
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
            //intento1.putExtra("origen", origen);
            if (fotomos != null) {

                startActivityForResult(intento1, REQUEST_CODE_TAKE_PHOTO);

            }
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"vars"+requestCode +"--"+ nombre_foto+"--"+resultCode);
        // archivofoto=null;
        if ((requestCode == REQUEST_CODE_TAKE_PHOTO) && resultCode == RESULT_OK) {
            //   super.onActivityResult(requestCode, resultCode, data);

            if (!nombre_foto.equals("")&&archivofoto!=null&&archivofoto.exists()) {

                textoint.setText(nombre_foto);
                if(ComprasUtils.getAvailableMemory(getActivity()).lowMemory)
                {
                    Toast.makeText(getActivity(), "No hay memoria suficiente para esta accion", Toast.LENGTH_SHORT).show();
                    aceptar.setEnabled(false);
                    return;
                }else {
                    // Bitmap bitmap1 = BitmapFactory.decodeFile(getActivity().getExternalFilesDir(null) + "/" + nombre_foto);
                    try {
                        ComprasUtils cu = new ComprasUtils();
                        cu.comprimirImagen(archivofoto.getAbsolutePath());
                        Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(archivofoto.getAbsolutePath(), 100, 100);
                        Glide.with(getContext())
                                .load(archivofoto.getAbsolutePath())
                                //  .placeholder(R.drawable.ic_cloud_off_red)

                                .into(fotomos);
                        // fotomos.setImageBitmap(bitmap1);
                        // fotomos.setLayoutParams(new LinearLayout.LayoutParams(350,150));
                        fotomos.setVisibility(View.VISIBLE);

                        btnrotar.setVisibility(View.VISIBLE);
                        btnrotar.setFocusableInTouchMode(true);
                        btnrotar.requestFocus();
                        nombre_foto = null;
                        archivofoto = null;
                    }catch(Exception ex){
                        //algo salió mal intente de nuevo
                        errorFoto();
                    }
                }




            }
            else{
                errorFoto();
                Log.e(TAG,"Algo salió mal???");
            }


        }
        else
        {
            errorFoto();
            Log.e(TAG,"Algo salió muy mal**");
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
    public void errorFoto(){
        Toast.makeText(getActivity(), "Intente tomar la foto de nuevo", Toast.LENGTH_LONG).show();

        aceptar.setEnabled(false);
    }
    public void guardarFoto(int sig){
        try {

            //Creo el informe en nuevo informe y lo busco aqui
            //necestio saber si ya habia guardado informe
            //veo si ya existe el informe o hay que crearlo
            Log.d(TAG, "guardando foto");
            //   exit(0);
            if (preguntaAct == 1 && !isEdicion&&mViewModel.getNvoinforme()==null) {
                Log.d(TAG, "creando nvo inf");
                //busco el consecutivo
                mViewModel.setIdNuevo( mViewModel.insertarInformeEtapa(Constantes.INDICEACTUAL, nombrePlantaSel, plantaSel, clienteNombre, clienteId));

            } //   Log.d(TAG, "guardando informe"+mViewModel.numMuestra+"--"+mViewModel.getIdInformeNuevo());

            //
            //  idInformeNuevo.observe(getViewLifecycleOwner(), new Observer<Integer>() {
                /*    @Override
                    public void onChanged(Integer idnvo) {
                        Log.d(TAG, "se creo el informe" + idnvo);
                        mViewModel.informe.setId(idnvo);
                        mViewModel.setIdInformeNuevo(idnvo);
                        // mViewModel.informe.setSinproducto();

                            //si tengo detalle
                            Log.d(TAG,"guardando  foto "+preguntaAct);

                            //    List<Integer> muestras= dViewModel.muestrasTotales();
                            //  for(int x:muestras) {
                            int nuevoid = dViewModel.insertarDetalle(mViewModel.getIdInformeNuevo(), mViewModel.numMuestra);
                            //guardo la muestra
                            if (nuevoid > 0&&dViewModel.icdNuevo!=null) {
                                dViewModel.setIddetalleNuevo(nuevoid);

                            }
                            else{
                                //algo salio mal
                                Toast.makeText(getActivity(), "No se pudo guardar la muestra", Toast.LENGTH_LONG).show();
                                yaestoyProcesando=false;
                                return;
                            }
                            yaestoyProcesando=false;

                                avanzarPregunta(sig);
                                //preguntar si hay otro cliente, para agregar otro o cerrar


                        //  idInformeNuevo.removeObservers(DetalleProductoFragment.this);

                    }

                });

            }else //ya tengo informe y solo guardo el det
          {*/
            //  si tengo detalle
            Log.d(TAG,"guardando  detalle"+mViewModel.getIdNuevo());
            int nuevoid =0;
            if(isEdicion){
                //todo solo actualizaria imagen detalle
                nuevoid = mViewModel.actualizarImagenDet(Integer.parseInt(detalleEdit.getRuta_foto()),"foto_preparacion" + preguntaAct,textoint.getText().toString(),Constantes.INDICEACTUAL);

            }else {   //guardo la muestra



                nuevoid = mViewModel.insertarInfEtaDetIm(mViewModel.getIdNuevo(), 10, "foto_preparacion" + preguntaAct,textoint.getText().toString(),0,Constantes.INDICEACTUAL);


            }
            if (nuevoid > 0) {


                yaestoyProcesando=false;
                Log.d(TAG,"en guaradar"+sig);
                avanzarPregunta(sig);
            }

        }catch (Exception ex){
            ex.printStackTrace();
            Log.e(TAG,ex.getMessage());
            Toast.makeText(getActivity(), "No se pudo guardar la muestra", Toast.LENGTH_LONG).show();

        }

    }
    public InformeEtapaEnv preparaInforme(){
        InformeEtapaEnv envio=new InformeEtapaEnv();
        //busco el informe

        envio.setInformeEtapa(mViewModel.getInformexId(mViewModel.getIdNuevo()));
        envio.setClaveUsuario(Constantes.CLAVEUSUARIO);
        envio.setIndice(Constantes.INDICEACTUAL);
        envio.setInformeEtapaDet(mViewModel.getInformeDet(mViewModel.getIdNuevo()));
        //busco las imagenes
        if(envio.getInformeEtapa().getEtapa()==1) //por ahora solo tengo prep
        {
            List<ImagenDetalle> imagenes=mViewModel.buscarImagenes(envio.getInformeEtapaDet());

            envio.setImagenDetalles(imagenes);
        }

        return envio;
    }

    public  void subirFotos(Activity activity, InformeEtapaEnv informe){
        //las imagenes
        if(informe.getInformeEtapa().getEtapa()==1){
            //busco la imagenes
            for(ImagenDetalle imagen:informe.getImagenDetalles()){
                //
                //subo cada una
               /* Intent msgIntent = new Intent(activity, SubirFotoService.class);
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
                        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS) // Reintentos inteligentes
                        .build();

                // Encolar
                if(getContext()!=null)
                 WorkManager.getInstance(getContext()).enqueue(uploadRequest);

            }


        }else
            for(InformeEtapaDet imagen:informe.getInformeEtapaDet()){
                //subo cada una
                Intent msgIntent = new Intent(activity, SubirFotoService.class);
                msgIntent.putExtra(SubirFotoService.EXTRA_IMAGE_ID, imagen.getId());
                msgIntent.putExtra(SubirFotoService.EXTRA_IMG_PATH,imagen.getRuta_foto());
                msgIntent.putExtra(SubirFotoService.EXTRA_INDICE,informe.getIndice());
                // Constantes.INDICEACTUAL
                Log.d(TAG,"subiendo fotos"+activity.getLocalClassName());

                msgIntent.setAction(SubirFotoService.ACTION_UPLOAD_ETA);

                //cambio su estatus a subiendo
                imagen.setEstatusSync(1);
                activity.startService(msgIntent);
                //cambio su estatus a subiendo



            }

    }

    public void avanzarPregunta(int sig){
        Log.d(TAG,"voy a la sig pregunta"+sig);
        //pregunto si otra foto
        //pregunto si habrá más clientes
        if(sig==1){
            preguntaSig = sig;
            Bundle bundle=new Bundle();
            bundle.putInt(NvaPreparacionFragment.ARG_PREGACT,preguntaSig);
            bundle.putBoolean(NvaPreparacionFragment.ARG_ESEDI,false);
            NvaPreparacionFragment nvofrag=    new NvaPreparacionFragment();
            nvofrag.setArguments(bundle);

            // NvaPreparacionFragment nvofrag = new NvaPreparacionFragment(preguntaSig, false,null);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
// Definir una transacción
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
// Remplazar el contenido principal por el fragmento
            fragmentTransaction.replace(R.id.continfeta_fragment, nvofrag);
            //     fragmentTransaction.addToBackStack(null);
// Cambiar
            fragmentTransaction.commit();
        }
        else if(sig<6) {

            //siguiente queda igual
            preguntaSig = sig;
            Bundle bundle=new Bundle();
            bundle.putInt(NvaPreparacionFragment.ARG_PREGACT,preguntaSig);
            bundle.putBoolean(NvaPreparacionFragment.ARG_ESEDI,false);
            NvaPreparacionFragment nvofrag=    new NvaPreparacionFragment();
            nvofrag.setArguments(bundle);
            // NvaPreparacionFragment nvofrag = new NvaPreparacionFragment(preguntaSig, false,null);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
// Definir una transacción
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
// Remplazar el contenido principal por el fragmento
            fragmentTransaction.replace(R.id.continfeta_fragment, nvofrag);
            //     fragmentTransaction.addToBackStack(null);
// Cambiar
            fragmentTransaction.commit();


        }else { //son los comentarios
            Bundle bundle=new Bundle();
            bundle.putInt(NvaPreparacionFragment.ARG_PREGACT,sig);
            bundle.putBoolean(NvaPreparacionFragment.ARG_ESEDI,false);
            NvaPreparacionFragment nvofrag=    new NvaPreparacionFragment();
            nvofrag.setArguments(bundle);
            //NvaPreparacionFragment nvofrag = new NvaPreparacionFragment(sig, false,null);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
// Definir una transacción
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
// Remplazar el contenido principal por el fragmento
            fragmentTransaction.replace(R.id.continfeta_fragment, nvofrag);
            //     fragmentTransaction.addToBackStack(null);
// Cambiar
            fragmentTransaction.commit();

        }


    }


    //guardo en tabla temp
    public void guardarResp(int sig) {
        //  Log.d(TAG, "guardando en temp*****" + preguntaAct.getId());
        String valor = null;
        if (textoint != null) {
            valor = textoint.getText().toString();
            valor = valor.toUpperCase();
        }
        //paso a mayusculas
        Log.d(TAG, "guardando en temp" + preguntaAct + "val" + valor);
        if (preguntaAct > 0 && valor != null && valor.length() > 0) {
            guardarFoto( sig);
        }

    }


    public void actualizarInforme() throws Exception {
        String valor = null;
        if (textoint != null) {
            valor = textoint.getText().toString();
            valor = valor.toUpperCase();
        }
        mViewModel.actualizarComentarios(mViewModel.getIdNuevo(),valor);
    }
    public void cargarPlantas(CampoForm campo) {

        campo.selectdes= listaPlantas;

        //  }
        //  else
        //    campo.selectdes= Constantes.clientesAsignados;
    }

    private  void convertirLista(List<ListaCompra>lista){
        listaPlantas=new ArrayList<DescripcionGenerica>();
        for (ListaCompra listaCompra: lista ) {
            //reviso si ya tengo informe no cancelado
            InformeEtapa inf=mViewModel.getInformexPlantaEta(listaCompra.getPlantasId(),1,Constantes.INDICEACTUAL,0);
            /*String tupla=Integer.toString(listaCompra.getClienteId())+";"+
            listaCompra.getPlantaNombre();*/
            //puede ser el que tengo
            if(inf!=null&&inf.getId()!= informesel&&inf.getEstatus()!=4)
                continue;
            listaPlantas.add(new DescripcionGenerica(listaCompra.getPlantasId(), listaCompra.getClienteNombre()+" "+listaCompra.getPlantaNombre(),listaCompra.getClientesId()+","+listaCompra.getClienteNombre(),listaCompra.getPlantaNombre()));

        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel = null;
        cf=null;
        camposForm=null;
        root=null;
        textoint=null;
        fotomos=null;
        sv=null;
        btnrotar=null;
        aceptar=null;
        nombre_foto=null;
        archivofoto=null;
    }


}