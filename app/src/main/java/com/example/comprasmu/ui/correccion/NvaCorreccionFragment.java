package com.example.comprasmu.ui.correccion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.comprasmu.NavigationDrawerActivity;
import com.example.comprasmu.R;

import com.example.comprasmu.data.modelos.Correccion;
import com.example.comprasmu.data.modelos.ImagenDetalle;
import com.example.comprasmu.data.modelos.InformeCompra;
import com.example.comprasmu.data.modelos.InformeCompraDetalle;
import com.example.comprasmu.data.modelos.InformeEtapaDet;
import com.example.comprasmu.data.modelos.SolicitudCor;
import com.example.comprasmu.data.remote.CorreccionEnvio;

import com.example.comprasmu.services.DescAutomaticasServiceManager;
import com.example.comprasmu.services.SubirFotoService;
import com.example.comprasmu.services.UploadFotoWorker;
import com.example.comprasmu.ui.RevisarFotoActivity;
import com.example.comprasmu.ui.infetapa.NuevoInfEtapaActivity;
import com.example.comprasmu.ui.solcorreccion.ListaSolsViewModel;
import com.example.comprasmu.utils.CampoForm;
import com.example.comprasmu.utils.ComprasUtils;
import com.example.comprasmu.utils.Constantes;
import com.example.comprasmu.utils.CreadorFormulario;
import com.example.comprasmu.SubirCorreccionTask;
import com.example.comprasmu.utils.micamara.MiCamaraActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;

/****correccion de compras***/
public class NvaCorreccionFragment extends Fragment {


    CreadorFormulario cf;
    List<CampoForm> camposForm;
    LinearLayout sv,sv2,sv3,sv4;
    private static final String TAG = "NvaCorreccionFragment";
    Button aceptar;
    private long lastClickTime = 0;
    private final boolean yaestoyProcesando=false;
    int solicitudSel;
    EditText textoint,txtrutaim2,txtrutaim3,txtrutaim4;
    ImageView fotomos,fotomos2,fotomos3,fotomos4, fotoori1,fotoori2,fotoori3,fotoori4;
    private ImageButton btnrotar,btnrotar2,btnrotar3, btnrotar4;
    public static  int REQUEST_CODE_TAKE_PHOTO=1;
    public static int REQUEST_CODE2 = 2;
    public static int REQUEST_CODE3 = 3;
    public static int REQUEST_CODE4=4;
    String rutafotoo;
    String rutafotoo3;
    String rutafotoo2;
    String rutafotoo4;
    private View root;
    private NvaCorreViewModel mViewModel;
    ListaSolsViewModel solViewModel;
    SolicitudCor solicitud;
    TextView txtmotivo;
    int numfoto;


    public static NvaCorreccionFragment newInstance() {
        return new NvaCorreccionFragment();
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root= inflater.inflate(R.layout.fragment_correccion, container, false);
        sv = root.findViewById(R.id.content_generic);
        sv2 = root.findViewById(R.id.content_generic2);
        sv3 = root.findViewById(R.id.content_generic3);
        sv4 = root.findViewById(R.id.content_generic4);
        aceptar = root.findViewById(R.id.btngaceptar);
        txtmotivo=root.findViewById(R.id.txtcmotivo);
        mViewModel = new ViewModelProvider(requireActivity()).get(NvaCorreViewModel.class);
        solViewModel = new ViewModelProvider(requireActivity()).get(ListaSolsViewModel.class);
        aceptar.setEnabled(false);
        //buscar la solicitud
        Bundle datosRecuperados = getArguments();

        if(datosRecuperados!=null) {
            solicitudSel = datosRecuperados.getInt(NuevoInfEtapaActivity.INFORMESEL);
            numfoto = datosRecuperados.getInt(NuevoInfEtapaActivity.NUMFOTO);

        }
        fotoori1=root.findViewById(R.id.ivcoriginal);
        fotoori2=root.findViewById(R.id.ivcoriginal2);
        fotoori3=root.findViewById(R.id.ivcoriginal3);
        fotoori4=root.findViewById(R.id.ivcoriginal4);
        LiveData<SolicitudCor> solicitudCorreccionLiveData=solViewModel.getSolicitud(solicitudSel,numfoto);
        solicitudCorreccionLiveData.observe(getViewLifecycleOwner(), new Observer<SolicitudCor>() {
            @Override
            public void onChanged(SolicitudCor solicitudCor) {
                solicitud=solicitudCor;
                Log.e(TAG,"estatus "+solicitud.getInformesId()+numfoto);
                //busco el consecutivo de la tienda
                int constienda=0;
                if(solicitudCor.getInformesId()>0) {
                    InformeCompra informe = solViewModel.getInformeSol(solicitudCor.getInformesId());

                    if(informe!=null) {
                        constienda = informe.getConsecutivo();
                    }
                }
                ((NuevoInfEtapaActivity)getActivity()).actualizarBarraCor(solicitud, constienda);
                crearFormulario();
                solicitudCorreccionLiveData.removeObservers(getViewLifecycleOwner());
                buscarFoto();
                fotoori1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        verImagen(rutafotoo);
                    }
                });
            }
        });
        aceptar.setText(getString(R.string.enviar));
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aceptar.setEnabled(false);
                long currentClickTime=SystemClock.elapsedRealtime();
                 // preventing double, using threshold of 1000 ms
                if (currentClickTime - lastClickTime < 5000){
                    //  Log.d(TAG,"doble click :("+lastClickTime);
                    return;
                }

                lastClickTime = currentClickTime;


                    guardar();


            }
        });


        return root;
    }

    private void buscarFoto(){
        //BUSCO LA FOTO ORIGINAL que tambien puede ser la ultima correccion
        //en donde la busco
        List<Correccion> listaCorrecciones=mViewModel.getCorreccionxsolicitudDesc(solicitudSel,solicitud.getNumFoto());
        switch (solicitud.getEtapa()){
            case 1:
                if(listaCorrecciones==null||listaCorrecciones.size()<1||solicitud.getEstatus()==4&&(listaCorrecciones.size()==1)) {//uso la original

                    solViewModel.buscarEtapaDet(solicitud.getNumFoto()).observe(getViewLifecycleOwner(), new Observer<InformeEtapaDet>() {
                        @Override
                        public void onChanged(InformeEtapaDet informeEtapaDet) {
                            rutafotoo = informeEtapaDet.getRuta_foto();
                            Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + rutafotoo, 80, 80);
                            fotoori1.setImageBitmap(bitmap1);
                        }
                    });
                }else
                {
                    buscarFotoCorreccion(listaCorrecciones,solicitud.getEstatus());
                    Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + rutafotoo, 80, 80);
                    fotoori1.setImageBitmap(bitmap1);
                }

                break;

            case 2:
                String producto="";
                InformeCompraDetalle corrige=null;

                if(listaCorrecciones!=null&&listaCorrecciones.size()>0) //ya hay alguna correccion
                {
                    buscarFotoCorreccion(listaCorrecciones,solicitud.getEstatus());
                    Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + rutafotoo, 80, 80);
                    fotoori1.setImageBitmap(bitmap1);
                    if(solicitud.getDescripcionFoto().equals("foto_atributoa")){
                        corrige = solViewModel.buscarInformeFoto(solicitud.getInformesId(), solicitud.getNumFoto(), Constantes.INDICEACTUAL);

                        Bitmap bitmap2= ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + rutafotoo2, 80, 80);
                        if(bitmap2!=null)
                            fotoori2.setImageBitmap(bitmap2);

                        fotoori2.setVisibility(View.VISIBLE);
                        root.findViewById(R.id.gpocfototo2).setVisibility(View.VISIBLE);
                        Bitmap bitmap3 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + rutafotoo3, 80, 80);
                        if(bitmap3!=null)
                            fotoori3.setImageBitmap(bitmap3);
                        root.findViewById(R.id.gpofotoo3).setVisibility(View.VISIBLE);
                        fotoori3.setVisibility(View.VISIBLE);
                        Bitmap bitmap4 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + rutafotoo4, 80, 80);
                        if(bitmap4!=null)
                            fotoori4.setImageBitmap(bitmap4);
                        root.findViewById(R.id.gpofotoo4).setVisibility(View.VISIBLE);
                        fotoori4.setVisibility(View.VISIBLE);
                    }

                }else {  //pongo original
                    if (solicitud.getDescripcionFoto().equals("foto_atributoa")) {
                        //busco las otras fotos
                        //busco el informe
                        corrige = solViewModel.buscarInformeFoto(solicitud.getInformesId(), solicitud.getNumFoto(), Constantes.INDICEACTUAL);
                        int numfoto2 = corrige.getFoto_atributob();

                        int numfoto3 = corrige.getFoto_atributoc();
                        int numfoto4 = 0;
                        if (corrige.getFoto_atributod() != null)
                            numfoto4 = corrige.getFoto_atributod();
                        //pongo datos del producto
                        //  producto=corrige.getProducto()+" "+corrige.getPresentacion();
                        solViewModel.buscarImagenCom(numfoto2).observe(getViewLifecycleOwner(), new Observer<ImagenDetalle>() {
                            @Override
                            public void onChanged(ImagenDetalle imagenDetalle) {
                                if (imagenDetalle != null) {
                                    rutafotoo2 = imagenDetalle.getRuta();

                                    Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + rutafotoo2, 80, 80);
                                    if (bitmap1 != null)
                                        fotoori2.setImageBitmap(bitmap1);

                                    fotoori2.setVisibility(View.VISIBLE);
                                    root.findViewById(R.id.gpocfototo2).setVisibility(View.VISIBLE);

                                }
                                //como consigo las otras?
                            }
                        });
                        solViewModel.buscarImagenCom(numfoto3).observe(getViewLifecycleOwner(), new Observer<ImagenDetalle>() {
                            @Override
                            public void onChanged(ImagenDetalle imagenDetalle) {
                                if (imagenDetalle != null) {
                                    rutafotoo3 = imagenDetalle.getRuta();

                                    Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + rutafotoo3, 80, 80);
                                    if (bitmap1 != null)
                                        fotoori3.setImageBitmap(bitmap1);
                                    root.findViewById(R.id.gpofotoo3).setVisibility(View.VISIBLE);
                                    fotoori3.setVisibility(View.VISIBLE);
                                }
                                //como consigo las otras?
                            }
                        });
                        solViewModel.buscarImagenCom(numfoto4).observe(getViewLifecycleOwner(), new Observer<ImagenDetalle>() {
                            @Override
                            public void onChanged(ImagenDetalle imagenDetalle) {
                                if (imagenDetalle != null) {
                                    rutafotoo4 = imagenDetalle.getRuta();

                                    Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + rutafotoo4, 80, 80);
                                    if (bitmap1 != null)
                                        fotoori4.setImageBitmap(bitmap1);
                                    root.findViewById(R.id.gpofotoo4).setVisibility(View.VISIBLE);
                                    fotoori4.setVisibility(View.VISIBLE);
                                }

                            }
                        });

                    }
                    solViewModel.buscarImagenCom(solicitud.getNumFoto()).observe(getViewLifecycleOwner(), new Observer<ImagenDetalle>() {
                        @Override
                        public void onChanged(ImagenDetalle imagenDetalle) {
                            if(imagenDetalle!=null) {
                                rutafotoo = imagenDetalle.getRuta();
                                Log.d(TAG,"buscando "+rutafotoo);
                                Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + rutafotoo, 80, 80);
                                if(bitmap1!=null)
                                    fotoori1.setImageBitmap(bitmap1);
                            }

                        }
                    });
                }
                    fotoori2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            verImagen(rutafotoo2);
                        }
                    });
                    fotoori3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            verImagen(rutafotoo3);
                        }
                    });
                    fotoori4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            verImagen(rutafotoo4);
                        }
                    });

                    if(solicitud.getDescripcionId()==9||solicitud.getDescripcionId()==7)
                        //busco el producto
                        corrige= solViewModel.buscarInformeByFoto(solicitud.getInformesId(), solicitud.getNumFoto(),solicitud.getDescripcionId());

                if(corrige!=null) {
                    producto = corrige.getProducto() + " "+corrige.getPresentacion()+" "+corrige.getEmpaque() ;
                    Log.e(TAG, "qqq" + producto);
                    ((NuevoInfEtapaActivity) getActivity()).actualizarAtributo3(producto);
                    ((NuevoInfEtapaActivity) getActivity()).actualizarAtributo4(corrige.getNombreAnalisis());
                }

                break;
        }
    }

    public void buscarFotoCorreccion(List<Correccion> listaCorreccion, int estatusFoto){
        //uso la penultima correccion
        int i=0;
        String imagenRuta;
        for (Correccion correccion:listaCorreccion) {

            if(i==0){

                rutafotoo= correccion.getRuta_foto1();
                if(solicitud.getDescripcionId()==4) //es de 3 fotos
                {


                    rutafotoo2=correccion.getRuta_foto2();
                    rutafotoo3=correccion.getRuta_foto3();
                    rutafotoo4=correccion.getRuta_foto4();


                }

                break;
            }

        }
    }
    public void crearFormulario(){

        camposForm=new ArrayList<>();
        CampoForm campo=new CampoForm();
        campo.label=solicitud.getMotivo();
        campo.nombre_campo="label";
        campo.type="label";
        campo.style=R.style.formlabel2;
        camposForm.add(campo);
        campo=new CampoForm();
        campo.label="NUEVA";
        campo.nombre_campo="label";
        campo.type="label";
        camposForm.add(campo);
         campo=new CampoForm();
        campo.label=solicitud.getDescMostrar();
        campo.nombre_campo="foto";
        campo.type="agregarImagen";
        campo.style=R.style.formlabel2;
        campo.id=1001;
        campo.funcionOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tomarFoto(REQUEST_CODE_TAKE_PHOTO);
                }
        };
        campo.tomarFoto = true;
        fotomos=root.findViewById(R.id.ivgfoto);

       // fotomos.setVisibility(View.VISIBLE);
        btnrotar=root.findViewById(R.id.btngrotar);
        btnrotar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rotar(1001,1);
                }
            });


        camposForm.add(campo);
       // Log.d(TAG,"haciendo form");
        cf=new CreadorFormulario(camposForm,getContext());
        sv.addView(cf.crearFormulario());

        if(solicitud.getDescripcionFoto().equals("foto_atributoa")) { //es 360
            camposForm = new ArrayList<>();
            campo = new CampoForm();
            campo.label = getString(R.string.foto_posicion2);
            campo.nombre_campo = "foto2";
            campo.type = "agregarImagen";
            campo.style = R.style.formlabel2;
            campo.id = 1002;
            campo.funcionOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tomarFoto(REQUEST_CODE2);
                }
            };
            campo.tomarFoto = true;

            fotomos2 = root.findViewById(R.id.ivgfoto2);
        //    fotomos2.setVisibility(View.VISIBLE);
            btnrotar2 = root.findViewById(R.id.btngrotar2);
            btnrotar2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rotar(1002,2);
                }
            });

            camposForm.add(campo);

            cf = new CreadorFormulario(camposForm, getContext());
            sv2.addView(cf.crearFormulario());
            sv2.setVisibility(View.VISIBLE);
            txtrutaim2 = root.findViewById(1002);

            camposForm = new ArrayList<>();
            campo = new CampoForm();
            campo.label = getString(R.string.foto_posicion3);
            campo.nombre_campo = "foto3";
            campo.type = "agregarImagen";
            campo.style = R.style.formlabel2;
            campo.id = 1003;
            campo.funcionOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tomarFoto(REQUEST_CODE3);
                }
            };
            campo.tomarFoto = true;

            camposForm.add(campo);
            Log.d(TAG, "haciendo form");
            cf = new CreadorFormulario(camposForm, getContext());
            sv3.addView(cf.crearFormulario());
            sv3.setVisibility(View.VISIBLE);
            txtrutaim3 = root.findViewById(1003);
            fotomos3 = root.findViewById(R.id.ivgfoto3);
            //  fotomos3.setVisibility(View.VISIBLE);
            btnrotar3 = root.findViewById(R.id.btngrotar3);
            btnrotar3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rotar(1003,3);
                }
            });

            camposForm = new ArrayList<>();
            campo = new CampoForm();
            campo.label =getString(R.string.foto_posicion4);
            campo.nombre_campo = "foto4";
            campo.type = "agregarImagen";
            campo.style = R.style.formlabel2;
            campo.id = 1004;
            campo.funcionOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tomarFoto(REQUEST_CODE4);
                }
            };
            campo.tomarFoto = true;

            camposForm.add(campo);
            Log.d(TAG, "haciendo form");
            cf = new CreadorFormulario(camposForm, getContext());

            sv4.addView(cf.crearFormulario());
            sv4.setVisibility(View.VISIBLE);
            txtrutaim4 = root.findViewById(1004);
            fotomos4 = root.findViewById(R.id.ivgfoto4);
            //  fotomos3.setVisibility(View.VISIBLE);
            btnrotar4 = root.findViewById(R.id.btngrotar4);
            btnrotar4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rotar(1004,4);
                }
            });
        }
        textoint = root.findViewById(1001);
    }
    public void guardar(){
        try {
            lastClickTime = 0;
            String valor = null;
            String valor2 = null;
            String valor3 = null;
            String valor4 = null;
            if (textoint != null) {
                valor = textoint.getText().toString();
                valor = valor.toUpperCase();
            }
            if (txtrutaim2 != null) {
                valor2 = txtrutaim2.getText().toString();
                valor2 = valor2.toUpperCase();
            }
            if (txtrutaim3 != null) {
                valor3 = txtrutaim3.getText().toString();
                valor3 = valor3.toUpperCase();
            }
            if (txtrutaim4 != null) {
                valor4 = txtrutaim4.getText().toString();
                valor4 = valor4.toUpperCase();
            }
            //valido q haya 3 fotos si es de atributo
            if(solicitud.getDescripcionFoto().equals("foto_atributoa")) { //es 360
                if(valor2.equals("")||valor3.equals("")||valor4.equals("")){
                    Toast.makeText(getContext(),"Favor de capturar las 4 fotos",Toast.LENGTH_LONG).show();
                    return;
                }
            }

                //paso a
            //creo el informe
            mViewModel.setIdNuevo(mViewModel.insertarCorreccion(solicitud.getId(), Constantes.INDICEACTUAL,solicitud.getNumFoto(),valor, valor2, valor3, valor4));
            Log.i(TAG,"guardando correccion solicitud:"+solicitud.getId()+" id correccion:"+mViewModel.getIdNuevo());
            actualizarSolicitud();
            Toast.makeText(getContext(),"Informe guardado correctamente",Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            salir();

        }catch (Exception ex){
            ex.getStackTrace();
            Log.e(TAG,"Algo salió mal al enviar"+ex.getMessage());
            Toast.makeText(getContext(),"Hubo un error al guardar intente de nuevo",Toast.LENGTH_SHORT).show();
            aceptar.setEnabled(true);
        }

    }

    //cambiar estatus sol
    public void actualizarSolicitud() {
        try {
            //cancelo las actualizaciones
            DescAutomaticasServiceManager.getInstancia().pausarServicio();
            solViewModel.actualizarEstSolicitud(solicitudSel,numfoto,4);
            CorreccionEnvio envio=mViewModel.prepararEnvio(mViewModel.getNvocorreccion());
            SubirCorreccionTask miTareaAsincrona = new SubirCorreccionTask(envio,getActivity());
            miTareaAsincrona.execute();

            subirFotos(getActivity(),envio.getCorreccion().getId(),envio.getCorreccion().getRuta_foto1());


            if(envio.getCorreccion().getRuta_foto2()!=null&&envio.getCorreccion().getRuta_foto2().length()>1)
                subirFotos(getActivity(),envio.getCorreccion().getId(),envio.getCorreccion().getRuta_foto2());
            if(envio.getCorreccion().getRuta_foto3()!=null&&envio.getCorreccion().getRuta_foto3().length()>1)
                subirFotos(getActivity(),envio.getCorreccion().getId(),envio.getCorreccion().getRuta_foto3());
            if(envio.getCorreccion().getRuta_foto4()!=null&&envio.getCorreccion().getRuta_foto4().length()>1)
                subirFotos(getActivity(),envio.getCorreccion().getId(),envio.getCorreccion().getRuta_foto4());

        }catch(Exception ex){
            ex.getStackTrace();
            Log.e(TAG,"Algo salió mal al enviar"+ex.getMessage());
            Toast.makeText(getContext(),"Algo salio mal al enviar",Toast.LENGTH_SHORT).show();
        }
        //todo limpio variables de sesion
        mViewModel.setIdNuevo(0);

        mViewModel.setNvocorreccion(null);

    }

    public void rotar(int idcampo,int numimagen){
        EditText txtruta = root.findViewById(idcampo);
        String foto=txtruta.getText().toString();
        if(ComprasUtils.getAvailableMemory(getActivity()).lowMemory)
        {
            Toast.makeText(getActivity(), "No hay memoria suficiente para esta accion", Toast.LENGTH_SHORT).show();

            return;
        }else
        {
            if(numimagen==1)
            RevisarFotoActivity.rotarImagen(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" +foto,fotomos);
            if(numimagen==2)
                RevisarFotoActivity.rotarImagen(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" +foto,fotomos2);

            if(numimagen==3)
                RevisarFotoActivity.rotarImagen(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" +foto,fotomos3);
            if(numimagen==4)
                RevisarFotoActivity.rotarImagen(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" +foto,fotomos4);

        }
    }
    public void verImagen(String nombrearch){
        //  ImageView imagen=(ImageView)v;
        // imagen.get
        Log.e(TAG,"arch"+nombrearch);
        Intent iverim=new Intent(getActivity(), RevisarFotoActivity.class);
        iverim.putExtra(RevisarFotoActivity.IMG_PATH1,nombrearch);
        startActivity(iverim);
    }
    String nombre_foto;
    File archivofoto;
    public void tomarFoto(int REQUEST_CODE){
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

            if (fotomos != null) {

                startActivityForResult(intento1, REQUEST_CODE);

            }
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"vars"+requestCode +"--"+ nombre_foto);
        if ((requestCode == REQUEST_CODE_TAKE_PHOTO||requestCode == REQUEST_CODE2||requestCode == REQUEST_CODE3||requestCode == REQUEST_CODE4) && resultCode == RESULT_OK) {
            //   super.onActivityResult(requestCode, resultCode, data);

            if (archivofoto!=null&&archivofoto.exists()) {
                if(requestCode == REQUEST_CODE_TAKE_PHOTO) {
                    mostrarFoto(textoint,fotomos,btnrotar);
                }
                if(requestCode == REQUEST_CODE2) {
                    View grupo=root.findViewById(R.id.gpofoto2);
                    grupo.setVisibility(View.VISIBLE);
                    mostrarFoto(txtrutaim2,fotomos2,btnrotar2);

                }else
                if(requestCode == REQUEST_CODE3) {
                    View grupo=root.findViewById(R.id.gpofoto3);
                    grupo.setVisibility(View.VISIBLE);
                    mostrarFoto(txtrutaim3,fotomos3,btnrotar3);
                }else
                if(requestCode == REQUEST_CODE4) {
                    View grupo=root.findViewById(R.id.gpofoto4);
                    grupo.setVisibility(View.VISIBLE);
                    mostrarFoto(txtrutaim4,fotomos4,btnrotar4);
                }
                aceptar.setEnabled(true);

            }
            else{
                Log.e(TAG,"Algo salió mal???");
            }


        }
        else
        {
            Log.e(TAG,"Algo salió muy mal**");
        }
    }

    public void mostrarFoto(EditText textorut,ImageView xfotomos, ImageButton xbtnrotar){


            textorut.setText(nombre_foto);
            if(ComprasUtils.getAvailableMemory(getActivity()).lowMemory)
            {
                Toast.makeText(getActivity(), "No hay memoria suficiente para esta accion", Toast.LENGTH_SHORT).show();

                return;
            }else {
                // Bitmap bitmap1 = BitmapFactory.decodeFile(getActivity().getExternalFilesDir(null) + "/" + nombre_foto);
                ComprasUtils cu = new ComprasUtils();
                cu.comprimirImagen(archivofoto.getAbsolutePath());
                Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(archivofoto.getAbsolutePath(), 80, 80);
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
       // getActivity().finish();
        Intent intento1 = new Intent(getActivity(), NavigationDrawerActivity.class);
        intento1.putExtra(NavigationDrawerActivity.NAVINICIAL,"listainformeeta");
        startActivity(intento1);
        getActivity().finish();
        // NavHostFragment.(this).navigate(R.id.action_selclientetolistacompras,bundle);


    }




    public void subirFotos(Activity activity, int id, String ruta){

        //subo cada una
        /*Intent msgIntent = new Intent(activity, SubirFotoService.class);
        msgIntent.putExtra(SubirFotoService.EXTRA_IMAGE_ID,id);
        msgIntent.putExtra(SubirFotoService.EXTRA_IMG_PATH,ruta);

        msgIntent.putExtra(SubirFotoService.EXTRA_INDICE,Constantes.INDICEACTUAL);
        // Constantes.INDICEACTUAL
        Log.d(TAG,"subiendo fotos"+activity.getLocalClassName());

        msgIntent.setAction(SubirFotoService.ACTION_UPLOAD_COR);

        activity.startService(msgIntent);*/

        //cambiar el fotoservice por un workmanager que gestiona todo

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Pasar los IDs necesarios
        Data inputData = new Data.Builder()
                .putInt(UploadFotoWorker.EXTRA_IMAGE_ID, id)
                .putString(UploadFotoWorker.EXTRA_IMG_PATH, ruta)
                .putString(UploadFotoWorker.EXTRA_INDICE,Constantes.INDICEACTUAL)
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel = null;
        solViewModel=null;
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

