package com.example.comprasmu.ui.visita;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.example.comprasmu.data.ComprasDataBase;
import com.example.comprasmu.ui.tiendas.LoadingAlert;
import com.example.comprasmu.utils.micamara.MiCamaraActivity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.comprasmu.R;
import com.example.comprasmu.data.dao.ProductoExhibidoDao;
import com.example.comprasmu.data.modelos.DescripcionGenerica;
import com.example.comprasmu.data.modelos.Geocerca;
import com.example.comprasmu.data.modelos.ImagenDetalle;
import com.example.comprasmu.data.modelos.InformeCompra;
import com.example.comprasmu.data.modelos.ListaCompra;
import com.example.comprasmu.data.modelos.Tienda;
import com.example.comprasmu.data.modelos.Visita;
import com.example.comprasmu.data.repositories.GeocercaRepositoryImpl;
import com.example.comprasmu.ui.RevisarFotoActivity;
import com.example.comprasmu.ui.informe.FotoExhibicionAdapter;
import com.example.comprasmu.ui.informe.NuevaFotoExhibViewModel;
import com.example.comprasmu.ui.informe.NuevoinformeViewModel;
import com.example.comprasmu.ui.listadetalle.ListaDetalleViewModel;
import com.example.comprasmu.utils.CampoForm;
import com.example.comprasmu.utils.ComprasLog;
import com.example.comprasmu.utils.ComprasUtils;
import com.example.comprasmu.utils.Constantes;
import com.example.comprasmu.utils.CreadorFormulario;
import com.example.comprasmu.utils.Preguntasino;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.PolyUtil;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.ACTIVITY_SERVICE;

public class AbririnformeFragment extends Fragment implements  FotoExhibicionAdapter.AdapterCallback {

    private static final int REQUEST_CHECK_SETTINGS = 0;
    public static final String IMG_PATH = "comprasmu.abririnf.img_path1";
    public static final String CODEREQ = "comprasmu.abririnf.codereq";
    private static final String BLATITUD ="comprasmu.abririnf.latitud";
    private static final String BLONGITUD ="comprasmu.abririnf.logitud";
    public static final String KEY_ETAPAACT = "comprasmu.ni_etapact";
    public static final String KEY_USUARIO = "comprasmu.ni_usuario";
    public static final String KEY_INDICEACT = "comprasmu.ni_indcieact";
    private static final String KEY_CDTRAB ="comprasmu.ni_cdtrab";
    // protected Validator validator;
    protected boolean guardado;

    private NuevoinformeViewModel mViewModel;
    private NuevaFotoExhibViewModel feviewModel;
    private LoadingAlert alert;
    CreadorFormulario cf2;
    List<CampoForm> camposForm;
    List<CampoForm> camposTienda;
    private FusedLocationProviderClient fusedLocationClient; //cliente servicio de ubicación
    LocationRequest locationRequest;
    private String country;
    Button guardar;
    ImageButton rotar;
    View root;
    boolean nuevaTienda;
    private long lastClickTime = 0;
    Tienda tienda;
    int nuevoId;
    private boolean yaTengoFoto;
    private int estatusPepsi; //para saber si la tienda se puede comprar pepsi 1- si puede, 0 no puede
    private int estatusPen;
    private int estatusElec;
    private int estatusJum;
    EditText txtubicacion,txtaiultubic;
    public static String EXTRAPREINFORME_ID = "comprasmu.preinformeid";
    private CreadorFormulario cf1;
    private static final int SELECT_FILE = 1;
    public static int REQUEST_CODE_TAKE_PHOTO = 1;
    public static int REQUEST_CODE_PROD1 = 2;
    public static int REQUEST_CODE_PROD2 = 3;
    public static int REQUEST_CODE_PROD3 = 4;
    public static int REQUEST_CODE_PROD4 = 5;
    private static final String TAG = "AbrirInformeFragment";

    private CreadorFormulario cf3;
    String nombre_foto;
    private int totClientes = 0;
    private RecyclerView recycler;
    private Visita visitaEdi;
    private List<InformeCompra> fotosExhibido;//para ir guardando las fotos de producto exhibido
    private ImagenDetalle efotoFachada;
    private FotoExhibicionAdapter mListAdapter;
    ImageView fotofac;
    ImageView fotoex1;
    ImageView fotoex2;
    ImageView fotoex3,fotoex4;
    private TextView mensajedir;
    LocationManager mlocManager;
    String provedorgps;

    Location ultimaLoc;
    File rutaArchivo;
    EditText txtcomplemento; // para complemento direccion
    private Spinner spinn;
    private Spinner spinn2;
    private Spinner spinn3,spinn4;
    private ListaDetalleViewModel lViewModel;
    private EditText txtfotofachada;
    private EditText txtfotoex1;
    private EditText txtfotoex2;
    private EditText txtfotoex3,txtfotoex4;
    CheckBox cbfotofac;
    CheckBox cbfotoex,cbfotoex2,cbfotoex3,cbfotoex4;
    LinearLayout aifotofacgroup;
    LinearLayout aiex1group;
    LinearLayout aiex2group;
    LinearLayout aiex3group,aiex4group;
    ImageButton btnrotar1;
    ImageButton btnrotar2;
    ImageButton btnrotar3,btnrotar4;
    Preguntasino snmascli1;
    Preguntasino snmascli2,snmascli3;
    public List<DescripcionGenerica> clientesAsignados;
    public List<DescripcionGenerica> clientesAsignados2;
    public List<DescripcionGenerica> clientesAsignados3;
    public List<DescripcionGenerica> clientesAsignados4;
    List<ProductoExhibidoDao.ProductoExhibidoFoto> fotosExh;
    private boolean isEdicion;
    private int globrequestcode;
    double ultlongitud, ultlatitud;
    ScrollView svprincipal;
    ComprasLog milog;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = false;
    private String coordenadasMapa;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(NuevoinformeViewModel.class);
        feviewModel =
                new ViewModelProvider(this).get(NuevaFotoExhibViewModel.class);
        lViewModel = new ViewModelProvider(this).get(ListaDetalleViewModel.class);
        root = inflater.inflate(R.layout.fragment_abririnforme, container, false);

        TextView indice = root.findViewById(R.id.txtaiindice);
        indice.setText(ComprasUtils.indiceLetra(Constantes.INDICEACTUAL));
        milog=ComprasLog.getSingleton();
        milog.crearLog(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath());

        guardar = root.findViewById(R.id.aibtnguardar);
        rotar = root.findViewById(R.id.btnairotar1);
        fotofac = root.findViewById(R.id.ivaifachada);
        fotoex1 = root.findViewById(R.id.ivaifotoex1);
        fotoex2 = root.findViewById(R.id.ivaifotoex2);
        fotoex3 = root.findViewById(R.id.ivaifotoex3);
        fotoex4 = root.findViewById(R.id.ivaifotoex4);
        rotar.setVisibility(View.GONE);
        txtfotofachada = root.findViewById(R.id.txtaifotofachada);
        txtfotoex1 = root.findViewById(R.id.txtaifotoex1);
        txtfotoex2 = root.findViewById(R.id.txtaifotoex2);
        txtfotoex3 = root.findViewById(R.id.txtaifotoex3);
        txtfotoex4 = root.findViewById(R.id.txtaifotoex4);
        svprincipal=root.findViewById(R.id.svaiprincipal);
        rotar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotar(view, txtfotofachada, fotofac);
            }
        });
        if(savedInstanceState!=null){
            Log.d(TAG,"aqui");
            milog.info(TAG,"oncreateView","en savedInstanceState");
            nombre_foto = savedInstanceState.getString(IMG_PATH);
            globrequestcode=savedInstanceState.getInt(CODEREQ);
            ultlatitud=savedInstanceState.getDouble(BLATITUD);
            ultlongitud=savedInstanceState.getDouble(BLONGITUD);
            Constantes.CLAVEUSUARIO = savedInstanceState.getString(KEY_USUARIO);
            Constantes.ETAPAACTUAL = savedInstanceState.getInt(KEY_ETAPAACT);
            Constantes.INDICEACTUAL = savedInstanceState.getString(KEY_INDICEACT);
            Constantes.CIUDADTRABAJO = savedInstanceState.getString(KEY_CDTRAB);

        }

        ImageButton fotoexhibido = root.findViewById(R.id.btnaifotoexhibido);
        guardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.setEnabled(false);
                long currentClickTime= SystemClock.elapsedRealtime();
                // preventing double, using threshold of 1000 ms
                if (currentClickTime - lastClickTime < 5000){
                    v.setEnabled(false);
                    return;
                }

                lastClickTime = currentClickTime;
                //      validator.validate();
                if (mViewModel.mIsNew)
                    sologuardar();
                else
                    actualizar();
                //  v.setEnabled(true);
            }
        });
        Constantes.TIPOTIENDA=new HashMap<>();
        Constantes.TIPOTIENDA.put(0,getString(R.string.seleccione_opcion));
        Constantes.TIPOTIENDA.put(1,getString(R.string.grande));
        Constantes.TIPOTIENDA.put(2,getString(R.string.mediana));
        Constantes.TIPOTIENDA.put(3,getString(R.string.chica));
        Constantes.TIPOTIENDA.put(4,getString(R.string.otras));
        fotoexhibido.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tomarFoto(txtfotoex1, fotoex1, REQUEST_CODE_PROD1);
            }
        });
        ImageButton fotofachada = root.findViewById(R.id.btnaifotofachada);

        fotofachada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txtubicacion.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Espere se active la ubicación antes de tomar la foto", Toast.LENGTH_SHORT).show();

                   // locationStart();
                    return;
                }
                //comparo las coordenadas con la del mapa
                tomarFoto(txtfotofachada, fotofac, REQUEST_CODE_TAKE_PHOTO);

            }
        });
        ImageButton fotoexhibido2 = root.findViewById(R.id.btnaifotoexhibido2);

        fotoexhibido2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tomarFoto(txtfotoex2, fotoex2, REQUEST_CODE_PROD2);
            }
        });
        ImageButton fotoexhibido3 = root.findViewById(R.id.btnaifotoexhibido3);

        fotoexhibido3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tomarFoto(txtfotoex3, fotoex3, REQUEST_CODE_PROD3);
            }
        });
        ImageButton fotoexhibido4 = root.findViewById(R.id.btnaifotoexhibido4);

        fotoexhibido4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tomarFoto(txtfotoex4, fotoex4, REQUEST_CODE_PROD4);
            }
        });
        /*para obtener la onre*/
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        rutaArchivo = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        setupSnackbar();
        //   initUi();
        txtubicacion = root.findViewById(R.id.txtaiubicacion);
        txtaiultubic= root.findViewById(R.id.txtaiultubic);
        txtcomplemento = root.findViewById(R.id.txtaicomplementodir);
        txtcomplemento.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        btnrotar1 = root.findViewById(R.id.btnairotarfe1);
        btnrotar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotar(view, txtfotoex1, fotoex1);
            }
        });
        btnrotar2 = root.findViewById(R.id.btnairotarfe2);
        btnrotar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotar(view, txtfotoex2, fotoex2);
            }
        });
        btnrotar3 = root.findViewById(R.id.btnairotarfe3);
        btnrotar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotar(view, txtfotoex3, fotoex3);
            }
        });
        btnrotar4 = root.findViewById(R.id.btnairotarfe4);
        btnrotar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotar(view, txtfotoex4, fotoex4);
            }
        });
        //llenar lista de clientes
        //input3.addTextChangedListener(new MayusTextWatcher());
        cbfotofac = root.findViewById(R.id.cbainpfachada);
        cbfotoex = root.findViewById(R.id.cbainpexhibidor);
        cbfotoex2 = root.findViewById(R.id.cbainpexhibidor2);
        cbfotoex3 = root.findViewById(R.id.cbainpexhibidor3);
        cbfotoex4 = root.findViewById(R.id.cbainpexhibidor4);
        aifotofacgroup=root.findViewById(R.id.aifotofacgroup);
        aiex1group=root.findViewById(R.id.aiex1group);
        aiex2group=root.findViewById(R.id.aiex2group);
        aiex3group=root.findViewById(R.id.aiex3group);
        aiex4group=root.findViewById(R.id.aiex4group);
        cbfotofac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup grupo = root.findViewById(R.id.aiubicar);
                if (cbfotofac.isChecked()) {
                    //muestro boton para ubicar
                    preguntarBorrarFoto(view,txtfotofachada,fotofac,rotar,aifotofacgroup);
                    grupo.setVisibility(View.VISIBLE);

                } else {
                    grupo.setVisibility(View.GONE);
                }
            }
        });
        Button btnubicar = root.findViewById(R.id.btnaiubic);
        btnubicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txtubicacion.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Espere se active la ubicación", Toast.LENGTH_SHORT).show();

                    //locationStart();
                    return;
                }
                guardarUbicacion();

            }
        });

        cbfotoex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preguntarBorrarFoto(view,txtfotoex1,fotoex1,btnrotar1,aiex1group);
            }
        });
        cbfotoex2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preguntarBorrarFoto(view,txtfotoex2,fotoex2,btnrotar2,aiex2group);
            }
        });
        cbfotoex3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preguntarBorrarFoto(view,txtfotoex3,fotoex3,btnrotar3,aiex3group);
            }
        });
        cbfotoex4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preguntarBorrarFoto(view,txtfotoex4,fotoex4,btnrotar4,aiex4group);
            }
        });
        mensajedir = root.findViewById(R.id.txtaimensajeubicacion);


        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        if (Constantes.CIUDADTRABAJO == null || Constantes.CIUDADTRABAJO.equals("")) {
            //falta definir
            Toast.makeText(getActivity(), "Falta definir ciudad de trabajo", Toast.LENGTH_SHORT).show();

            NavHostFragment.findNavController(this).navigate(R.id.action_nuevotociudad);

        }

        // locationStart();
        Bundle datosRecuperados = getArguments();
        if (datosRecuperados != null && getArguments().getInt(EXTRAPREINFORME_ID) > 0) {
            // No hay datos, manejar excepción

            Log.d(TAG, "******* es edicion");
            getActivity().setTitle(R.string.editar_informe);

        }


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            //  requestPermissionLauncher.launch(
            //        Manifest.permission.REQUESTED_PERMISSION);
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

        } else {
            // rastreoGPS();
            //  locationStart();
        }
        loadData();
        //   probarUbicacion();

    }

    private void alertaAbierto() {
        //pregunto si habrá más clientes
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getActivity());
        dialogo1.setTitle(R.string.importante);
        dialogo1.setMessage(R.string.informe_abierto);
        dialogo1.setCancelable(false);

        dialogo1.setNegativeButton(R.string.cerrar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                //  dialogo1.cancel();
                //envio a la lista
                NavHostFragment.findNavController(AbririnformeFragment.this).navigate(R.id.action_nuevotolista);

            }
        });
        dialogo1.show();

    }

    private void loadData() {
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        spinn = root.findViewById(R.id.spaiclientes1);
        spinn2 = root.findViewById(R.id.spaiclientes2);
        spinn3 = root.findViewById(R.id.spaiclientes3);
        spinn4 = root.findViewById(R.id.spaiclientes4);
        estatusPepsi = estatusPen=estatusElec=estatusJum=1;

        if (getArguments() != null) {

            //es edicion
            int categoryId = getArguments().getInt(EXTRAPREINFORME_ID);
            if (categoryId > 0) {
                //es edicion
                isEdicion = true;
                alert=new LoadingAlert(getActivity());
                //   alert.setMensaje(getString(R.string.validando_ubicacion));
                //  alert.startAlert();
                locationStart();
                nuevoId = mViewModel.start(categoryId, getActivity());
                fotosExh = feviewModel.cargarfotosSimpl(nuevoId);
                //lleno los campos
                mViewModel.visitaEdicion.observe(getViewLifecycleOwner(), new Observer<Visita>() {
                    @Override
                    public void onChanged(Visita visita) {
                        nuevaTienda = true;
                        if (visita.getTiendaId() > 0) {
                            //no es tienda nueva
                            nuevaTienda = false;
                            tienda = new Tienda();
                            tienda.setUne_id(visita.getTiendaId());
                            tienda.setUne_descripcion(visita.getTiendaNombre());
                            //   tienda.setUne_tipotienda();
                            try {
                                tienda.setUne_tipotienda(visita.getTipoId());
                            } catch (NumberFormatException ex) {
                                Log.d(TAG, "Error al convertir");
                            }
                            tienda.setUne_direccion(visita.getDireccion());
                        }
                        estatusPepsi = visita.getEstatusPepsi();
                        estatusPen = visita.getEstatusPen();
                        estatusElec = visita.getEstatusElec();
                        estatusJum = visita.getEstatusJum();
                        //cargo la lista de clientes
                        cargarClientes();
                        crearFormulario(visita);
                        ponerDatos(visita);
                        LinearLayout sv = root.findViewById(R.id.content_main);
                        sv.addView(cf1.crearFormulario());
                        //   sv.addView(cf2.crearFormulario());
                        visitaEdi = visita;
                        mViewModel.visitaEdicion.removeObservers(getViewLifecycleOwner());

                    }
                });
                return;
            } else {
                //es nuevo
                //reviso si no hay visitas abiertas
                boolean abierto = mViewModel.informesAbiertos();
                if (abierto){
                    alertaAbierto();
                    return;
                }

                alert=new LoadingAlert(getActivity());
                alert.setMensaje(getString(R.string.registrando_ubicacion));
                alert.startAlert();
                locationStart();

                nuevaTienda = getArguments().getBoolean("nuevatienda");
                coordenadasMapa=getArguments().getString("coordenasmapa");

                //  Log.d(TAG, "datosrec " + nuevaTienda);
                if (!nuevaTienda)// es una tienda existente
                {

                    tienda = new Tienda();
                    tienda.setUne_id(getArguments().getInt("idtienda"));
                    tienda.setUne_descripcion(getArguments().getString("nombretienda"));
                    tienda.setUne_tipotienda(getArguments().getInt("tipotienda"));
                    // tienda.setCliente(getArguments().getInt("cliente"));
                    tienda.setUne_direccion(getArguments().getString("direccion"));
                    String colortienda = getArguments().getString("color");
                    tienda.setEstpep(getArguments().getInt("estpep"));
                    tienda.setEstpen(getArguments().getInt("estpen"));
                    tienda.setEstele(getArguments().getInt("estele"));
                    tienda.setEstjum(getArguments().getInt("estjum"));
                    tienda.setColor(colortienda);
                    Log.d(TAG, "wwwww" + tienda.getColor());

                    estatusPepsi =tienda.getEstpep();

                    estatusPen = tienda.getEstpen();
                    estatusElec = tienda.getEstele();
                    estatusJum = tienda.getEstjum();


                }

                //cargo la lista de clientes
                cargarClientes();
                nuevoId = mViewModel.start(0, getActivity());
                crearFormulario(new Visita());

                LinearLayout sv = root.findViewById(R.id.content_main);
                sv.addView(cf1.crearFormulario());
                //    sv.addView(cf2.crearFormulario());
                getActivity().setTitle(R.string.nuevo_informe);
                // toolbar.setTitle(R.string.nuevo_informe);
            }
        } else {
            boolean abierto = mViewModel.informesAbiertos();

            if (abierto){
                alertaAbierto();
                return;
            }

            nuevoId = mViewModel.start(0, getActivity());
            estatusPepsi =estatusPen=estatusElec=estatusJum= 1;
            crearFormulario(new Visita());

            LinearLayout sv = root.findViewById(R.id.content_main);
            sv.addView(cf1.crearFormulario());
            //    sv.addView(cf2.crearFormulario());
            getActivity().setTitle(R.string.nuevo_informe);
            // toolbar.setTitle(R.string.nuevo_informe);

        }


    }

    public void ponerDatos(Visita visita) {
      /*  if(visita.getTiendaId()>0) { //es edicion
        }else
            nuevaTienda=true;*/

        if (isEdicion) {
            if (visita.getFotoFachada() > 0)
                mViewModel.getFotoLD(visita.getFotoFachada()).observe(this, new Observer<ImagenDetalle>() {
                    @Override
                    public void onChanged(ImagenDetalle s) {
                        // if(s!=null)
                        txtfotofachada.setText(s.getRuta());

                        Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + s.getRuta(), 80, 80);
                        fotofac.setVisibility(View.VISIBLE);
                        rotar.setVisibility(View.VISIBLE);
                        fotofac.setImageBitmap(bitmap1);
                        efotoFachada = s;
                    }
                });
            else {
                cbfotofac.setChecked(true);
            }

            //las fotos exhib
            // for(ProductoExhibidoDao.ProductoExhibidoFoto fotoe:fotosExh) {
            if (fotosExh != null && fotosExh.size() > 0) {
                //    Log.d(TAG, "fotos exh----" + fotosExh.size());
                //    Log.d(TAG, "fotos exh ruta" + fotosExh.get(0).ruta);
                if(fotosExh.get(0).imagenId==0){
                    cbfotoex.setChecked(true);
                }else
                    cargarFotos(fotosExh.get(0).ruta, txtfotoex1, btnrotar1, fotoex1);
                //  Log.d(TAG,);
//                 Log.d(TAG,"a ver"+fotosExh.get(0).clienteId+"-"+Constantes.clientesAsignados.indexOf(fotosExh.get(0).clienteId));
                int pos = buscarEnClientes(fotosExh.get(0).clienteId, clientesAsignados);
                if(pos>0) {
                    spinn.setSelection(pos, true);
                    //quito la opción en los otros
                    if (totClientes > 1) {
                        clientesAsignados2.remove(pos);
                        CreadorFormulario.cargarSpinnerDescr(getContext(), spinn2, clientesAsignados2);

                    }

                    if (totClientes > 2) {
                        clientesAsignados3.remove(pos);
                        CreadorFormulario.cargarSpinnerDescr(getContext(), spinn3, clientesAsignados3);

                    }
                    if (totClientes > 3) {
                        clientesAsignados4.remove(pos);
                        CreadorFormulario.cargarSpinnerDescr(getContext(), spinn4, clientesAsignados4);

                    }
                }
            }
            if (fotosExh != null && fotosExh.size() > 1) {
//            mostrarOcultarlayout("true",ll);
                if(fotosExh.get(1).imagenId==0){
                    cbfotoex2.setChecked(true);
                }else
                    cargarFotos(fotosExh.get(1).ruta, txtfotoex2, btnrotar2, fotoex2);
                int pos = buscarEnClientes(fotosExh.get(1).clienteId, clientesAsignados2);
                if (pos > -1) {
                    spinn2.setSelection(pos, true);

                    if (totClientes > 2) {
                        clientesAsignados3.remove(pos);

                        CreadorFormulario.cargarSpinnerDescr(getContext(), spinn3, clientesAsignados3);
                    }
                    if (totClientes > 3) {
                        clientesAsignados4.remove(pos);
                        CreadorFormulario.cargarSpinnerDescr(getContext(), spinn4, clientesAsignados4);

                    }
                }
            }
            if (fotosExh != null && fotosExh.size() > 2) {
                if(fotosExh.get(2).imagenId==0){
                    cbfotoex3.setChecked(true);
                }else
                    cargarFotos(fotosExh.get(2).ruta, txtfotoex3, btnrotar3, fotoex3);
                int pos = buscarEnClientes(fotosExh.get(2).clienteId, clientesAsignados3);
                if (pos > -1)
                    spinn3.setSelection(pos, true);
                if (totClientes > 3) {
                    clientesAsignados4.remove(pos);
                    CreadorFormulario.cargarSpinnerDescr(getContext(), spinn4, clientesAsignados4);

                }
            }
            if (fotosExh != null && fotosExh.size() > 3) {
                if(fotosExh.get(3).imagenId==0){
                    cbfotoex4.setChecked(true);
                }else
                    cargarFotos(fotosExh.get(3).ruta, txtfotoex4, btnrotar4, fotoex4);
                int pos = buscarEnClientes(fotosExh.get(3).clienteId, clientesAsignados4);
                if (pos > -1)
                    spinn4.setSelection(pos, true);

            }
            //}

            //el complemento
            txtcomplemento.setText(visita.getComplementodireccion());
            mensajedir = root.findViewById(R.id.txtaimensajeubicacion);
            mensajedir.setText(visita.getDireccion());

            txtaiultubic.setText(visita.getGeolocalizacion());
        }
    }

    public int buscarEnClientes(int seleccion, List<DescripcionGenerica> listaclientes) {
        if(listaclientes!=null)
            for (int i = 0; i < listaclientes.size(); i++) {
                //  Log.d(TAG, "clientesas " + listaclientes.get(i).getId() + "--" + seleccion);
                if (listaclientes.get(i).getId() == seleccion)
                    return i;
            }
        return -1;
    }
    public List<DescripcionGenerica>  buscarEnClientesAtr( List<DescripcionGenerica> listaclientes) {
        //busco la seleccion anterior
        DescripcionGenerica cliente = (DescripcionGenerica) spinn.getSelectedItem();
        int seleccion=cliente.getId();
        int pos=buscarEnClientes(seleccion,listaclientes);
        if(pos>-1)
            listaclientes.remove(pos);
        cliente = (DescripcionGenerica) spinn2.getSelectedItem();
        seleccion=cliente.getId();
        pos=buscarEnClientes(seleccion,listaclientes);
        if(pos>-1)
            listaclientes.remove(pos);
        cliente = (DescripcionGenerica) spinn3.getSelectedItem();
        seleccion=cliente.getId();
        pos=buscarEnClientes(seleccion,listaclientes);
        if(pos>-1)
            listaclientes.remove(pos);
        return listaclientes;
    }
    public void cargarFotos(String s, EditText txtruta, ImageButton boton, ImageView iv) {
        txtruta.setText(s);
        //  Log.d(TAG,"ruta "+s);
        Bitmap bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + s, 80, 80);
        iv.setVisibility(View.VISIBLE);
        boton.setVisibility(View.VISIBLE);
        iv.setImageBitmap(bitmap1);
    }

    /* private void setupListAdapter() {
         RecyclerView listaproductosex=root.findViewById(R.id.rvaiproductoex);
         mListAdapter = new FotoExhibicionAdapter(this);
         //mBinding.detalleList.setAdapter(mListAdapter);
         listaproductosex.setLayoutManager(new LinearLayoutManager(getActivity()));
         listaproductosex.setHasFixedSize(true);
         listaproductosex.setAdapter(mListAdapter);

     }*/
    private void locationStart() {

        // 1. Crear la solicitud de ubicación
        createLocationRequest();

        // 2. Crear el Callback para recibir las actualizaciones
        createLocationCallback();

        mlocManager=(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        final boolean networkEnabled = mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsEnabled&&!networkEnabled) {
            Log.i(TAG, "gps inactivo");
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            if(alert.isMostrando())
                alert.closeAlertDialog();
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            if(alert.isMostrando())
                alert.closeAlertDialog();
            return;
        }

        startLocationUpdates();

    }

    public void cargarClientes() {
        // Log.d(TAG, "regresó de la consulta " + Constantes.CIUDADTRABAJO);
        //   if (Constantes.clientesAsignados == null||Constantes.clientesAsignados.size()<1)

        List<ListaCompra> data=null;
        data = lViewModel.cargarClientesSimplxet(Constantes.CIUDADTRABAJO,2);
        List<ListaCompra> nvadata=new ArrayList<>();
        if (estatusPepsi == 0) {

            for (ListaCompra compra:data
            ) {
                if(compra.getClientesId()==4) //quito pepsi
                    continue;
                nvadata.add(compra);
            }
            data=nvadata;
        }


        if (estatusPen == 0) {
            nvadata=new ArrayList<>();
            for (ListaCompra compra:data
            ) {
                if(compra.getClientesId()==5) //quito penafiel
                    continue;
                nvadata.add(compra);
            }
            data=nvadata;
        }
        if (estatusElec == 0) {
            nvadata=new ArrayList<>();
            for (ListaCompra compra:data
            ) {
                if(compra.getClientesId()==6) //quito elec
                    continue;
                nvadata.add(compra);
            }
            data=nvadata;
        }
        Log.d(TAG,"estatusjum "+estatusJum);
        if (estatusJum == 0) {
            nvadata=new ArrayList<>();
            for (ListaCompra compra:data
            ) {
                if(compra.getClientesId()==7) //quito elec
                    continue;
                nvadata.add(compra);
            }
            data=nvadata;
        }
        if(data!=null)
            totClientes = data.size();
        if (totClientes == 0) {
            if (estatusPepsi == 0||estatusElec == 0||estatusPen == 0||estatusJum == 0) {
                Toast.makeText(getActivity(), "En esta tienda no puede comprar producto", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigate(R.id.action_nuevotolista);

                return;
            }
            Toast.makeText(getActivity(), "Falta actualizar la app", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigate(R.id.action_nuevotolista);

            return;
        }
        //   Log.d(TAG, "regresó de la consulta " + totClientes);

        clientesAsignados = ComprasUtils.convertirListaaClientes(data);
        if (totClientes > 1) {
            clientesAsignados2 = ComprasUtils.convertirListaaClientes(data);
            CreadorFormulario.cargarSpinnerDescr(getContext(), spinn2, clientesAsignados2);
        }
        if (totClientes > 2) {
            clientesAsignados3 = ComprasUtils.convertirListaaClientes(data);
            CreadorFormulario.cargarSpinnerDescr(getContext(), spinn3, clientesAsignados3);

        }
        if (totClientes > 3) {
            clientesAsignados4 = ComprasUtils.convertirListaaClientes(data);
            CreadorFormulario.cargarSpinnerDescr(getContext(), spinn4, clientesAsignados4);

        }
        CreadorFormulario.cargarSpinnerDescr(getContext(), spinn, clientesAsignados);

    }

    public void saliendoSinguardar() {

        //para avisar que sale sin guardar
        EditText fotofachada = root.findViewById(R.id.txtaifotofachada);
        EditText nombretienda = root.findViewById(1001);
        if (guardado) {
            super.onDestroyView();
        } else if (!fotofachada.getText().toString().equals("") && visitaEdi == null) {
            Log.d(TAG, "Estoy saliendoe" + fotofachada.getText().toString() + visitaEdi);

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getActivity());
            dialogo1.setTitle(R.string.importante);
            dialogo1.setMessage(R.string.salir_sin_guardar);
            dialogo1.setCancelable(true);
            dialogo1.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                    AbririnformeFragment.super.onDestroyView();

                }
            });
            dialogo1.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    return;

                }
            });
            dialogo1.show();
            // return;
        }


        //else
        //    super.onDestroyView();
        //  super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLocationUpdates();


        mViewModel=null;
        feviewModel=null;

        cf2=null;
        camposForm=null;
        camposTienda=null;
        fusedLocationClient=null; //cliente servicio de ubicación
        locationRequest=null;
        country=null;
        guardar=null;
        rotar=null;
        root=null;

        tienda=null;

        txtubicacion=null;
        txtaiultubic=null;

        cf1=null;
        cf3=null;

        totClientes = 0;
        recycler=null;
        visitaEdi=null;
        fotosExhibido=null;//para ir guardando las fotos de producto exhibido
        efotoFachada=null;
        mListAdapter=null;
        fotofac=null;
        fotoex1=null;
        fotoex2=null;
        fotoex3=null;
        mensajedir=null;
        mlocManager=null;
        provedorgps=null;

        ultimaLoc=null;
        rutaArchivo=null;
        txtcomplemento=null; // para complemento direccion
        spinn=null;
        spinn2=null;
        spinn3=null;
        lViewModel=null;
        txtfotofachada=null;
        txtfotoex1=null;
        txtfotoex2=null;
        txtfotoex3=null;
        cbfotofac=null;
        cbfotoex=null;
        cbfotoex2=null;
        cbfotoex3=null;
        aifotofacgroup=null;
        aiex1group=null;
        aiex2group=null;
        aiex3group=null;
        btnrotar1=null;
        btnrotar2=null;
        btnrotar3=null;
        snmascli1=null;
        snmascli2=null;
        clientesAsignados=null;
        clientesAsignados2=null;
        clientesAsignados3=null;
        fotosExh=null;


    }

    private void setupSnackbar() {
        // Mostrar snackbar en resultados positivos de operaciones (crear, editar y eliminar)
        mViewModel.getSnackbarText().observe(getActivity(), integerEvent -> {
            Integer stringId = integerEvent.getContentIfNotHandled();
            if (stringId != null) {
                Snackbar.make(getActivity().findViewById(R.id.coordinator3),
                        stringId, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void crearFormulario(Visita visita) {
        if (nuevaTienda) {
            camposTienda = new ArrayList<CampoForm>();
            CampoForm campo = new CampoForm();
            campo.label = getString(R.string.nombre_tienda);
            campo.nombre_campo = "tiendaNombre";
            campo.type = "inputtext";
            campo.value = visita.getTiendaNombre();
            campo.required = "required";
            campo.id = 1001;
            campo.style=R.style.formlabel;
            camposTienda.add(campo);

           /* campo = new CampoForm();
            campo.label = "Direccion";
            campo.nombre_campo = "direccion";
            campo.type = "inputtext";
            campo.value = visita.getDireccion();
            campo.required = "required";
            campo.id = 1002;
            camposTienda.add(campo);
         /*   campo = new CampoForm();
            campo.label = "Complmento dirección";
            campo.nombre_campo = "complementodireccion";
            campo.type = "inputtext";
            campo.value = null;
            campo.required = "required";
            campo.id = 1003;
            camposTienda.add(campo);*/
         /*   campo = new CampoForm();
            campo.label = "Punto cardinal";
            campo.nombre_campo = "puntoCardinal";
            campo.type = "inputtext";
            campo.value = visita.getPuntoCardinal();
            campo.required = "required";
            campo.id = 1004;
            camposTienda.add(campo);*/
         /*   campo = new CampoForm();
            campo.type = "listDivider";
            camposTienda.add(campo);*/
            campo = new CampoForm();
            campo.label = "TIPO TIENDA";
            campo.nombre_campo = "tipoTienda";
            campo.type = "select";
            campo.value = (visita.getTipoId()+1) + "";
            Log.d(TAG, "tienda sel " + visita.getTipoId());
            campo.select = Constantes.TIPOTIENDA;
            campo.required = "required";
            campo.id = 1005;
            campo.style=R.style.formlabel;
            camposTienda.add(campo);
            /*
            campo = new CampoForm();
            campo.label = "Cadena comercial";
            campo.nombre_campo = "cadenaComercial";
            campo.type = "inputtext";
            campo.value = visita!=null?visita.getCadenaComercial():null;
            campo.required = "required";
            campo.id = 1006;
            camposTienda.add(campo);*/

        } else //ya existe
        {
            camposTienda = new ArrayList<CampoForm>();
            CampoForm campo = new CampoForm();
            campo.label = getString(R.string.nombre_tienda);
            campo.nombre_campo = "tiendaNombre";
            campo.type = "inputtext";
            campo.value = tienda.getUne_descripcion();
            campo.required = "required";
            campo.id = 1001;
            campo.style=R.style.formlabel;
            campo.readonly = "readonly";
            camposTienda.add(campo);

         /*   no es necesaria la direccion 17-may
         campo = new CampoForm();
            campo.label = getString(R.string.direccion);
            campo.nombre_campo = "direccion";
            campo.type = "inputtext";
            campo.value = tienda.getUne_direccion();
            campo.required = "required";
            campo.readonly="readonly";
            campo.id = 1002;
            camposTienda.add(campo);*/
          /*  campo = new CampoForm();
            campo.type = "listDivider";
            camposTienda.add(campo);*/
            campo = new CampoForm();
            campo.label = getString(R.string.tipo_tienda);
            campo.style=R.style.formlabel;
            campo.nombre_campo = "tipoTienda";
            campo.type = "inputtext";
            campo.readonly = "readonly";

            campo.value = Constantes.TIPOTIENDA.get(tienda.getUne_tipotienda());
            campo.required = "required";
            campo.id = 1005;
            camposTienda.add(campo);


        }
        /***finaliza campos de tienda***/
        camposForm = new ArrayList<CampoForm>();
        //  CampoForm campo2 = new CampoForm();

        /***hago preguntas si no**/

        CampoForm campo = new CampoForm();
        campo.label = "¿FOTO DE EXHIBIDOR DE OTRO CLIENTE?";
        campo.nombre_campo = "mascliente";

        LinearLayout ll2 = root.findViewById(R.id.llfotoex2);
        LinearLayout ll3 = root.findViewById(R.id.llfotoex3);
        LinearLayout ll4 = root.findViewById(R.id.llfotoex4);
        if (fotosExh != null && fotosExh.size() > 1) {
            campo.value = "true";
            mostrarOcultarlayout(View.VISIBLE, ll2);
        }
        campo.funcionOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //muestro sig boton

                mostrarOcultarlayout(View.VISIBLE, ll2);
                //quito el cliente seleecionado del spinner
                DescripcionGenerica cliente = (DescripcionGenerica) spinn.getSelectedItem();
                //  Log.d(TAG,spinn.getId()+"<--"+spinn.getSelectedItemId());
                int pos = buscarEnClientes(cliente.id, clientesAsignados2);
                //lo bloqueo
                spinn.setEnabled(false);
                if (pos > -1) {
                    {
                        clientesAsignados2.remove(pos);

                    }
                    if (totClientes > 2) {
                        pos = buscarEnClientes(cliente.id, clientesAsignados3);
                        if (pos > -1) {

                            clientesAsignados3.remove(pos);
                        }
                        CreadorFormulario.cargarSpinnerDescr(getContext(), spinn3, clientesAsignados3);

                    }
                    Log.d(TAG, "CLIENTES ASIG QUITÉ UNO " + clientesAsignados2.size());
                    CreadorFormulario.cargarSpinnerDescr(getContext(), spinn2, clientesAsignados2);

                }
            }
        };
        campo.funcionOnClick2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //oculto el layout
                spinn.setEnabled(true);
                loopViews(ll2);
                mostrarOcultarlayout(View.GONE, ll2);
                //borrar si hay algo

            }
        };
        campo.required = "required";
        campo.id = 1010;
        snmascli1 = root.findViewById(R.id.snaimascli1);
        if (totClientes < 2) {
            snmascli1.setVisible(View.GONE);
            //      snmascli2.setVisible(View.GONE);
        } else {
            //camposForm.add(campo);


            snmascli1.setmLabel(campo.label);
            snmascli1.setStyleLabel(R.style.formlabel);
            if (campo.style > 0) {
                snmascli1.setStyleLabel(campo.style);
            }
            if (campo.value != null && campo.value.equals("true")) {
                snmascli1.setSi(true);

            }
            snmascli1.onclicksi(campo.funcionOnClick);
            snmascli1.onclickno(campo.funcionOnClick2);


            // }
            snmascli1.setId((campo.id));

            snmascli2 = root.findViewById(R.id.snmascli2);
            snmascli3 = root.findViewById(R.id.snmascli3);
            snmascli2.setVisible(View.GONE);
            snmascli3.setVisible(View.GONE);
            if (totClientes > 2) {
                snmascli2.setVisible(View.VISIBLE);

                snmascli2.setmLabel(campo.label);
                snmascli2.setStyleLabel(R.style.formlabel);
                if (campo.style > 0) {
                    snmascli2.setStyleLabel(campo.style);
                }
                if (fotosExh != null && fotosExh.size() > 2) {
                    campo.value = "true";
                    mostrarOcultarlayout(View.VISIBLE, ll3);
                } else
                    campo.value = "false";
                if (campo.value != null && campo.value.equals("true")) {
                    snmascli2.setSi(true);

                }
                snmascli2.onclicksi(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //muestro sig boton

                        mostrarOcultarlayout(View.VISIBLE, ll3);
                        DescripcionGenerica cliente = (DescripcionGenerica) spinn2.getSelectedItem();
                        int pos = buscarEnClientes(cliente.id, clientesAsignados3);
                        spinn2.setEnabled(false);
                        if (pos > -1) {
                            {

                                clientesAsignados3.remove(pos);
                            }


                            CreadorFormulario.cargarSpinnerDescr(getContext(), spinn3, clientesAsignados3);
                        }

                    }
                });
                snmascli2.onclickno(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //muestro sig boton
                        spinn3.setEnabled(true);
                        loopViews(ll3);
                        mostrarOcultarlayout(View.GONE, ll3);

                    }
                });


                // }
                snmascli2.setId(1011);
            }

            if (totClientes > 3) {
                snmascli3.setVisible(View.VISIBLE);
                Log.d(TAG,"eeee"+campo.label);
                snmascli3.setmLabel(campo.label);
                snmascli3.setStyleLabel(R.style.formlabel);
                if (campo.style > 0) {
                    snmascli3.setStyleLabel(campo.style);
                }
                if (fotosExh != null && fotosExh.size() > 3) {
                    campo.value = "true";
                    mostrarOcultarlayout(View.VISIBLE, ll4);
                } else
                    campo.value = "false";
                if (campo.value != null && campo.value.equals("true")) {
                    snmascli3.setSi(true);

                }
                snmascli3.onclicksi(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //muestro sig boton

                        mostrarOcultarlayout(View.VISIBLE, ll4);
                        DescripcionGenerica cliente = (DescripcionGenerica) spinn3.getSelectedItem();
                        spinn3.setEnabled(false);


                        clientesAsignados4=buscarEnClientesAtr(clientesAsignados4);



                        CreadorFormulario.cargarSpinnerDescr(getContext(), spinn4, clientesAsignados4);


                    }
                });
                snmascli3.setmLabel("¿FOTO DE EXHIBIDOR DE OTRO CLIENTE?");
                snmascli3.onclickno(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //muestro sig boton
                        spinn4.setEnabled(true);
                        loopViews(ll4);
                        mostrarOcultarlayout(View.GONE, ll4);

                    }
                });


                // }
                snmascli3.setId(1011);
            }
        }

        cf1 = new CreadorFormulario(camposTienda, getActivity());
        //  cf2=new CreadorFormulario(camposForm,getActivity());


    }

    public void mostrarOcultarlayout(int val, LinearLayout ll) {
        ll.setVisibility(val);

    }

    public void loopViews(LinearLayout layout) {

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);


            if (child instanceof EditText) {
                // Do something
                ((EditText) child).setText("");
            }
            if (child instanceof ImageView) {
                // Do something
                if (child.getTag() != null && child.getTag().toString().equals("fotoex"))
                    ((ImageView) child).setImageBitmap(null);
            } else if (child instanceof LinearLayout) {

                this.loopViews((LinearLayout) child);
            }
            /*if (child instanceof ViewGroup) {

                this.loopViews((ViewGroup) child);
            }*/


        }
    }

    /*  public void camposFotosProd(){
          List<CampoForm> camposForm=new ArrayList<>();
          //busco el num de clientes
          ListaCompraRepositoryImpl listaRepo=ListaCompraRepositoryImpl.getInstance(ComprasDataBase.getInstance(getActivity()).getListaCompraDao());

          listaRepo.getClientesByIndiceCiudad(Constantes.INDICEACTUAL,Constantes.IDCIUDADTRABAJO)
                  .observeForever(new Observer<List<ListaCompra>>() {
                      @Override
                      public void onChanged(List<ListaCompra> listaCompras) {
                          int i=0;
                          totClientes=listaCompras.size();
                          for (ListaCompra compra: listaCompras) {

                              CampoForm campo2=new CampoForm();
                              campo2.label="Foto exhibición "+compra.getClienteNombre();
                              campo2.nombre_campo="producto_exhibido";
                              campo2.type="agregarImagen";
                              campo2.value=null;
                              campo2.id=1030+i;
                              CampoForm finalCampo = campo2;
                              int finali=i;
                              campo2.funcionOnClick=new View.OnClickListener() {
                                  @Override
                                  public void onClick(View view) {
                                      tomarFoto(finalCampo.id,1040+finali);
                                  }
                              };
                              campo2.tomarFoto=true;
                              campo2.required="required";

                              camposForm.add(campo2);
                              campo2=new CampoForm();
                              campo2.nombre_campo="producto_exhibido";
                              campo2.type="imagenView";

                              campo2.id=1040+i;


                              camposForm.add(campo2);
                              campo2=new CampoForm();
                              campo2.nombre_campo="cliente";
                              campo2.type="inputtext";
                              campo2.id=1050+i;

                              i++;

                              camposForm.add(campo2);
                          }


                      }
                  });
          cf3=new CreadorFormulario(camposForm,getActivity());


      }*/
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_ETAPAACT,Constantes.ETAPAACTUAL );
        savedInstanceState.putString(KEY_USUARIO, Constantes.CLAVEUSUARIO );
        savedInstanceState.putString(KEY_INDICEACT, Constantes.INDICEACTUAL );
        savedInstanceState.putString(KEY_CDTRAB,  Constantes.CIUDADTRABAJO );

        savedInstanceState.putString(IMG_PATH, nombre_foto);
        savedInstanceState.putInt(CODEREQ,globrequestcode );
        if(ultimaLoc!=null)
        { savedInstanceState.putDouble(BLATITUD,ultimaLoc.getLatitude() );
            savedInstanceState.putDouble(BLONGITUD,ultimaLoc.getLongitude() );}

    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
           /* if (savedInstanceState.containsKey(LOCATION_KEY)) {
                mLastLocation = savedInstanceState.getParcelable(LOCATION_KEY);

                updateLocationUI();
            }*/
        }
    }

    public void guardarUbicacion() {

        Log.d("AbrirInformeFragment", "presione boton");
        //valido la ubicacion con las coordenadas del mapa
        if(coordenadasMapa!=null&&!coordenadasMapa.equals("")){
            String[] auxiliar=coordenadasMapa.split(",");
            double x=Double.parseDouble(auxiliar[0]);
            double y=Double.parseDouble(auxiliar[1]);

            //uso un error de 2 metros
            boolean respuesta=ComprasUtils.distancia2puntos(ultimaLoc.getLatitude(), ultimaLoc.getLongitude(),x,y,500);
        //    Log.d("AbrirInformeFragment", x+" presione boton "+y);
            if(!respuesta)
            {
                //no es el mismo punto
                Toast.makeText(getActivity(),getString(R.string.para_tomarfoto),Toast.LENGTH_LONG).show();
                txtaiultubic.setText(""); //borro la ubicacion para que se mueva
                txtubicacion.setText("");
                //borro la foto de fachada para que vuelva a tomarla
                txtfotofachada.setText("");
                fotofac.setImageBitmap(null);

                fotofac.setVisibility(View.GONE);
                rotar.setVisibility(View.GONE);
                return;
            }
        }
        txtaiultubic.setText(txtubicacion.getText().toString());
        buscarDireccion();

    }
    public boolean guardar(){
        try {
            EditText input7 = root.findViewById(1007);
            EditText input8 = root.findViewById(1008);
            EditText fotofachada = root.findViewById(R.id.txtaifotofachada);

            Log.d(TAG,"foto fachada"+fotofachada.getText().toString());
            // EditText input13 = root.findViewById(1013);
            mViewModel.visita=new Visita();
            mViewModel.visita.setId(nuevoId);
            EditText input1;
            if(nuevaTienda){
                input1 = root.findViewById(1001);
                if(input1.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.error_nombre_tienda), Toast.LENGTH_SHORT).show();
                    guardar.setEnabled(true);
                    return false;
                }
                //tipo tienda
                Spinner tipotien = root.findViewById(1005);
                if(tipotien!=null) {
                    int op=(int)tipotien.getSelectedItemId();

                    if (op<1){
                        Toast.makeText(getActivity(), "Seleccione el tipo de tienda", Toast.LENGTH_SHORT).show();
                        guardar.setEnabled(true);
                        return false;
                    }
                }
            }
            if (Build.PRODUCT.contains ("sdk")) {//pruebas y el lenovo//entro rapido
                txtaiultubic.setText("ubicacion reg");
            }
            if (txtaiultubic.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Falta foto de fachada o activar casilla de \"No se permite tomar foto\"", Toast.LENGTH_SHORT).show();
                guardar.setEnabled(true);
                return false;
            }
            if (fotofachada.getText().toString().equals("")&&!cbfotofac.isChecked()) {
                Toast.makeText(getActivity(), "Falta foto de fachada o activar casilla de \"No se permite tomar foto\"", Toast.LENGTH_SHORT).show();
                guardar.setEnabled(true);
                return false;
            }
            //Log.d(TAG,"xxxxxx"+txtfotoex1.getText().toString());
            if(estatusPepsi==0)//no puedo comprar pepsi{
            {
                DescripcionGenerica cliente=(DescripcionGenerica)spinn.getSelectedItem();
                Log.d(TAG,"valor combolist"+cliente.getId());
                if(cliente.getId()==4){
                    Toast.makeText(getActivity(), "En esta tienda no puede comprar producto de Pepsi", Toast.LENGTH_SHORT).show();
                    guardar.setEnabled(true);
                    return false ;
                }
                if(spinn2!=null) {
                    DescripcionGenerica cliente2 = (DescripcionGenerica) spinn2.getSelectedItem();
                    if (cliente2!=null&&cliente2.getId() == 4) {
                        Toast.makeText(getActivity(), "En esta tienda no puede comprar producto de Pepsi", Toast.LENGTH_SHORT).show();
                        guardar.setEnabled(true);
                        return false;
                    }
                }
                if(spinn3!=null) {
                    DescripcionGenerica cliente3 = (DescripcionGenerica) spinn3.getSelectedItem();
                    if (cliente3!=null&&cliente3.getId() == 4) {
                        Toast.makeText(getActivity(), "En esta tienda no puede comprar producto de Pepsi", Toast.LENGTH_SHORT).show();
                        guardar.setEnabled(true);
                        return false;
                    }
                }
                if(spinn4!=null) {
                    DescripcionGenerica cliente4 = (DescripcionGenerica) spinn4.getSelectedItem();
                    if (cliente4!=null&&cliente4.getId() == 4) {
                        Toast.makeText(getActivity(), "En esta tienda no puede comprar producto de Pepsi", Toast.LENGTH_SHORT).show();
                        guardar.setEnabled(true);
                        return false;
                    }
                }


            }


            if(nuevaTienda){
                input1 = root.findViewById(1001);
                EditText input2 = root.findViewById(1002);

                EditText input4 = root.findViewById(1004);
                Spinner input5 = root.findViewById(1005);
                String tipotiendasel=input5.getSelectedItem().toString();
                int idtipo=(int)input5.getSelectedItemId();
                EditText input6 = root.findViewById(1006);

                mViewModel.visita.setTiendaNombre(input1.getText().toString());
                mViewModel.visita.setTipoTienda(tipotiendasel);

                mViewModel.visita.setTipoId(idtipo);
                if(mensajedir!=null)
                    mViewModel.visita.setDireccion(mensajedir.getText().toString());


            }else
            {//ya existe

                EditText input5 = root.findViewById(1005);
                input1 = root.findViewById(1001);
                EditText input2 = root.findViewById(1002);
                mViewModel.visita.setEstatusPepsi(estatusPepsi);
                mViewModel.visita.setEstatusPen(estatusPen);
                mViewModel.visita.setEstatusElec(estatusElec);
                mViewModel.visita.setEstatusJum(estatusJum);
                mViewModel.visita.setTiendaId(tienda.getUne_id());
                mViewModel.visita.setTiendaNombre(tienda.getUne_descripcion());
                Log.d(TAG,"PPPPP"+tienda.getUne_tipotienda()+"--"+Constantes.TIPOTIENDA.get(tienda.getUne_tipotienda()));
                mViewModel.visita.setTipoTienda(Constantes.TIPOTIENDA.get(tienda.getUne_tipotienda()));
                mViewModel.visita.setTipoId(tienda.getUne_tipotienda());
                mViewModel.visita.setDireccion(mensajedir.getText().toString());


            }
            int punto=0;
            try {
                //busco el punto cardinal

                punto = buscarZona(txtaiultubic.getText().toString());
            }catch (Exception ex){
                ex.printStackTrace();

                milog.grabarError(ex.getMessage());

            }
            Log.i(TAG,"encontré la zona "+punto);
            mViewModel.visita.setPuntoCardinal(punto+"");

            mViewModel.visita.setComplementodireccion(txtcomplemento.getText().toString());
            mViewModel.visita.setIndice(Constantes.INDICEACTUAL);
            mViewModel.visita.setEstatusPepsi(estatusPepsi);
            mViewModel.visita.setEstatusPen(estatusPen);
            mViewModel.visita.setEstatusElec(estatusElec);
            mViewModel.visita.setEstatusJum(estatusJum);
            //guardo la foto
            //guardar imagen
            if(!cbfotofac.isChecked()) {
                mViewModel.fotoFachada = new ImagenDetalle();
                mViewModel.fotoFachada.setRuta(fotofachada.getText().toString());
                mViewModel.fotoFachada.setDescripcion("Foto fachada");
                mViewModel.fotoFachada.setEstatus(1);
                mViewModel.fotoFachada.setIndice(Constantes.INDICEACTUAL);
                mViewModel.fotoFachada.setEstatusSync(0);
                mViewModel.fotoFachada.setCreatedAt(new Date());
            }
            //la ciudad y el pais en el que está se definen en las propiedades

            mViewModel.visita.setCiudadId(Constantes.IDCIUDADTRABAJO);
            mViewModel.visita.setCiudad(Constantes.CIUDADTRABAJO);
            mViewModel.visita.setPaisId(Constantes.IDPAISTRABAJO);
            mViewModel.visita.setPais(Constantes.PAISTRABAJO);

            mViewModel.visita.setClaveUsuario(Constantes.CLAVEUSUARIO);

            //   EditText input2 = root.findViewById(1001);
            //    mViewModel.informe.setNombreTemporal(nombreTemporal.getText().toString());
            mViewModel.visita.setEstatus(1);
            mViewModel.visita.setEstatusSync(0);
            mViewModel.visita.setCreatedAt(new Date());
            mViewModel.visita.setGeolocalizacion(txtaiultubic.getText().toString());

            MutableLiveData<Integer> resv=mViewModel.guardarVisita(getActivity(),this);
            resv.observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    mViewModel.eliminarTblTemp();
                    limpiarVariables();
                    guardado=true;

                    if(!cbfotoex.isChecked()) {
                        guardarFotoEx(txtfotoex1.getText().toString(), nuevoId, spinn);
                    } else

                        guardarsinFotoEx( nuevoId, spinn);

                    if (snmascli1 != null && snmascli1.getRespuesta()&&!cbfotoex2.isChecked()) {
                        Log.d(TAG, "entre aqui" + spinn.getId() + "--" + spinn2.getId());
                        guardarFotoEx(txtfotoex2.getText().toString(), nuevoId, spinn2);
                    }
                    else  if (snmascli1 != null && snmascli1.getRespuesta())
                        guardarsinFotoEx( nuevoId, spinn2);

                    if (snmascli2 != null && snmascli2.getRespuesta()&&!cbfotoex3.isChecked()) {
                        guardarFotoEx(txtfotoex3.getText().toString(), nuevoId, spinn3);
                    }
                    else   if (snmascli2 != null && snmascli2.getRespuesta()) {
                        guardarsinFotoEx( nuevoId, spinn3);
                    }
                    if (snmascli3 != null && snmascli3.getRespuesta()&&!cbfotoex4.isChecked()) {
                        guardarFotoEx(txtfotoex4.getText().toString(), nuevoId, spinn4);
                    } else   if (snmascli3 != null && snmascli3.getRespuesta()) {
                        guardarsinFotoEx( nuevoId, spinn4);
                    }

                    stopLocationUpdates();
                    NavHostFragment.findNavController(AbririnformeFragment.this).navigate(R.id.action_nuevotolista);


                }
            });
        }catch(Exception ex){
            ex.printStackTrace();
            //  Log.e(TAG,ex.getMessage());
            Toast.makeText(getActivity(), "Hubo un error al guardar intente de nuevo", Toast.LENGTH_LONG).show();

        }

        return true;
    }
    public void guardarFotoEx(String ruta1,int visitasId,Spinner spinnx){
        if(ruta1.equals(""))
            return;
        DescripcionGenerica cliente=(DescripcionGenerica)spinnx.getSelectedItem();

        //    Log.d(TAG, "guardarfoto cliente" + cliente.getId());
        NuevoinformeViewModel ninfViewModel =
                new ViewModelProvider(this).get(NuevoinformeViewModel.class);

        feviewModel.guardarFoto(ruta1, cliente.getId(), cliente.getNombre(), visitasId, getActivity(), ninfViewModel);
        //  Toast.makeText(getActivity(), "Se agregó la foto", Toast.LENGTH_LONG).show();
        //  getActivity().finish();


    }
    public void guardarsinFotoEx(int visitasId,Spinner spinnx){

        DescripcionGenerica cliente=(DescripcionGenerica)spinnx.getSelectedItem();

        Log.d(TAG, "guardarfoto cliente" + cliente.getId());
        if(cliente!=null)
            feviewModel.guardarsinFoto(cliente.getId(), cliente.getNombre(), visitasId);
        //  Toast.makeText(getActivity(), "Se agregó la foto", Toast.LENGTH_LONG).show();
        //  getActivity().finish();


    }
    public void sologuardar(){
        lastClickTime=0;
        guardar();


    }

    //con la geolocalizacion de la tienda se busca la zona en la que está
    //devuelve el id de la zona de acuerdo al catalogo
    //se puede consultar el catalogo en MapdaCDFragment
    public int buscarZona(String puntotxt){

        // puntotxt=" 14.5928099,-90.5060026";
        milog.info(TAG,"buscarZona","punto "+puntotxt);
        if(puntotxt.equals("")){
            return 0;
        }
        String[] auxp=puntotxt.split(",");
        try {
            LatLng punto = new LatLng(Double.parseDouble(auxp[0]), Double.parseDouble(auxp[1]));

            //busco las zonas de la ciudad de trabajo
            GeocercaRepositoryImpl georep=GeocercaRepositoryImpl.getInstance(ComprasDataBase.getInstance(getContext()).getGeocercaDao());
            List<Geocerca> zonas=georep.findsimplexCd(Constantes.CIUDADTRABAJO);
            //  Log.i(TAG,Constantes.CIUDADTRABAJO+"--"+zonas.size());
            LatLng p1;
            LatLng p2;
            LatLng p3;
            LatLng p4;
            if(zonas!=null)
                for(Geocerca geo:zonas){
                    Log.d(TAG,"probando "+geo.getGeo_region());

                    milog.grabarError("probando geocercas buscar zona"+geo.getGeo_region());
                    String[] aux =geo.getGeo_p1().split(",");
                    p1 = new LatLng(Double.parseDouble(aux[0]), Double.parseDouble(aux[1]));
                    String[] aux2 =geo.getGeo_p2().split(",");
                    p2 =new LatLng(Double.parseDouble(aux2[0]), Double.parseDouble(aux2[1]));
                    String[] aux3 =geo.getGeo_p3().split(",");
                    p3 =new LatLng(Double.parseDouble(aux3[0]), Double.parseDouble(aux3[1]));
                    String[] aux4 =geo.getGeo_p4().split(",");
                    p4 =new LatLng(Double.parseDouble(aux4[0]), Double.parseDouble(aux4[1]));
                    List<LatLng> poly=new ArrayList<>();
                    poly.add(p1);
                    poly.add(p2);
                    poly.add(p3);
                    poly.add(p4);
                    //mMap.addPolygon(new PolygonOptions()

                    if(PolyUtil.containsLocation(punto,poly,true))
                        return geo.getGeo_region();
                }
        }catch(NumberFormatException ex){
            Log.e(TAG,"Error de formato "+ex.getMessage());
            Toast.makeText(getContext(), "Error de formato", Toast.LENGTH_SHORT).show();
        }
        return 0; //no estuvo lol
    }
    public void actualizar(){
        lastClickTime=0;
        EditText fotofachada = root.findViewById(R.id.txtaifotofachada);
        EditText input1;
        try{
            if(nuevaTienda){
                input1 = root.findViewById(1001);
                if(input1.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.error_nombre_tienda), Toast.LENGTH_SHORT).show();
                    guardar.setEnabled(true);
                    return;
                }
                Spinner tipotien = root.findViewById(1005);
                if(tipotien!=null) {
                    int op=(int)tipotien.getSelectedItemId();

                    if (op<1){
                        Toast.makeText(getActivity(), "Seleccione el tipo de tienda", Toast.LENGTH_SHORT).show();
                        guardar.setEnabled(true);
                        return ;
                    }
                }
            }
            if (txtaiultubic.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Espere se active la ubicación antes de tomar la foto", Toast.LENGTH_SHORT).show();
                guardar.setEnabled(true);
                return;
            }
            if (fotofachada.getText().toString().equals("")&&!cbfotofac.isChecked()) {

                Toast.makeText(getActivity(), "Falta foto de fachada", Toast.LENGTH_SHORT).show();
                guardar.setEnabled(true);
                return ;
            }
            if(estatusPepsi==0)//no puedo comprar pepsi{
            {
                DescripcionGenerica cliente=(DescripcionGenerica)spinn.getSelectedItem();
                if(cliente.getId()==4){
                    Toast.makeText(getActivity(), "En esta tienda no puede comprar producto de Pepsi", Toast.LENGTH_SHORT).show();
                    guardar.setEnabled(true);
                    return ;
                }
                if(spinn2!=null) {
                    DescripcionGenerica cliente2 = (DescripcionGenerica) spinn2.getSelectedItem();
                    if (cliente2!=null&&cliente2.getId() == 4) {
                        Toast.makeText(getActivity(), "En esta tienda no puede comprar producto de Pepsi", Toast.LENGTH_SHORT).show();
                        guardar.setEnabled(true);
                        return;
                    }
                }
                if(spinn3!=null) {
                    DescripcionGenerica cliente3 = (DescripcionGenerica) spinn3.getSelectedItem();
                    if (cliente3!=null&&cliente3.getId() == 4) {
                        Toast.makeText(getActivity(), "En esta tienda no puede comprar producto de Pepsi", Toast.LENGTH_SHORT).show();
                        guardar.setEnabled(true);
                        return;
                    }
                }


            }
            if(txtfotoex1.getText().toString().equals("")&&!cbfotoex.isChecked()){

                Toast.makeText(getActivity(), "Falta foto de producto exhibido", Toast.LENGTH_SHORT).show();
                guardar.setEnabled(true);
                return ;
            }if(snmascli1.getRespuesta())//veo que tenga foto etc
                if(txtfotoex2.getText().toString().equals("")&&!cbfotoex2.isChecked()){
                    Toast.makeText(getActivity(), "Falta foto de producto exhibido", Toast.LENGTH_SHORT).show();
                    guardar.setEnabled(true);
                    return ;
                }
            if(snmascli2!=null&&snmascli2.getRespuesta())//veo que tenga foto etc
                if(txtfotoex3.getText().toString().equals("")&&!cbfotoex3.isChecked()){
                    Toast.makeText(getActivity(), "Falta foto de producto exhibido", Toast.LENGTH_SHORT).show();
                    guardar.setEnabled(true);
                    return ;
                }

            if(snmascli3!=null&&snmascli3.getRespuesta())//veo que tenga foto etc
                if(txtfotoex4.getText().toString().equals("")&&!cbfotoex4.isChecked()){
                    Toast.makeText(getActivity(), "Falta foto de producto exhibido", Toast.LENGTH_SHORT).show();
                    guardar.setEnabled(true);
                    return ;
                }
            //actualizo campos
            if(nuevaTienda){
                input1 = root.findViewById(1001); //nombre de la tienda
                EditText input2 = root.findViewById(1002);

                EditText input4 = root.findViewById(1004);
                Spinner input5 = root.findViewById(1005);
                EditText input6 = root.findViewById(1006);



                visitaEdi.setTiendaNombre(input1.getText().toString());
                visitaEdi.setTipoTienda((input5.getSelectedItem()).toString());

                visitaEdi.setTipoId((int)input5.getSelectedItemId());
                // visitaEdi.setDireccion(input2.getText().toString());

                //visitaEdi.setCadenaComercial(input6.getText().toString());
                int punto=0;
                try {
                    punto = buscarZona(txtaiultubic.getText().toString());
                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.e(TAG,ex.getMessage());
                }
                Log.d(TAG,"encontré la zona "+punto);
                visitaEdi.setPuntoCardinal(punto+"");


            }

            EditText input3 = root.findViewById(R.id.txtaicomplementodir);
            visitaEdi.setComplementodireccion(input3.getText().toString());
            if(!cbfotofac.isChecked()) {
                if(efotoFachada!=null) {
                    mViewModel.fotoFachada = efotoFachada;
                    mViewModel.fotoFachada.setRuta(fotofachada.getText().toString());
                    mViewModel.fotoFachada.setEstatus(1);
                    mViewModel.fotoFachada.setEstatusSync(0);
                    mViewModel.fotoFachada.setUpdatedAt(new Date());
                }
                else
                {
                    mViewModel.fotoFachada = new ImagenDetalle();
                    mViewModel.fotoFachada.setRuta(fotofachada.getText().toString());
                    mViewModel.fotoFachada.setDescripcion("Foto fachada");
                    mViewModel.fotoFachada.setEstatus(1);
                    mViewModel.fotoFachada.setIndice(Constantes.INDICEACTUAL);
                    mViewModel.fotoFachada.setEstatusSync(0);
                    mViewModel.fotoFachada.setCreatedAt(new Date());

                }

            }
            //la ciudad y el pais en el que está se definen en las propiedades

            visitaEdi.setEstatusPepsi(estatusPepsi);
            visitaEdi.setEstatusPen(estatusPen);
            visitaEdi.setEstatusElec(estatusElec);
            visitaEdi.setEstatusJum(estatusJum);
            //   EditText input2 = root.findViewById(1001);
            //    mViewModel.informe.setNombreTemporal(nombreTemporal.getText().toString());
            visitaEdi.setEstatus(1);
            visitaEdi.setEstatusSync(0);

            visitaEdi.setUpdatedAt(new Date());
            visitaEdi.setGeolocalizacion(txtaiultubic.getText().toString());
            Log.d(TAG,"editando"+visitaEdi.getCiudad()+"--"+visitaEdi.getCiudadId());
            mViewModel.visita=visitaEdi;
            mViewModel.actualizarVisita();
            //borro las fotos y vuelvo a insertar

            for (ProductoExhibidoDao.ProductoExhibidoFoto foto : fotosExh) {
                feviewModel.eliminarFoto(foto);
                //
            }
            //Log.d(TAG,"**CLIENTES ASIG "+Constantes.clientesAsignados.size());
            if(!cbfotoex.isChecked()) {
                guardarFotoEx(txtfotoex1.getText().toString(), nuevoId, spinn);
            }else
                guardarsinFotoEx( nuevoId, spinn);

            if (snmascli1.getRespuesta()&&!cbfotoex2.isChecked())
                guardarFotoEx(txtfotoex2.getText().toString(), nuevoId, spinn2);
            else    if (snmascli1.getRespuesta())
                guardarsinFotoEx( nuevoId, spinn2);

            if (snmascli2 != null && snmascli2.getRespuesta()&&!cbfotoex3.isChecked())
                guardarFotoEx(txtfotoex3.getText().toString(), nuevoId, spinn3);
            else if (snmascli2 != null && snmascli2.getRespuesta())
                guardarsinFotoEx( nuevoId, spinn3);
            if (snmascli3 != null && snmascli3.getRespuesta()&&!cbfotoex4.isChecked())
                guardarFotoEx(txtfotoex4.getText().toString(), nuevoId, spinn4);
            else if (snmascli3 != null && snmascli3.getRespuesta())
                guardarsinFotoEx( nuevoId, spinn4);
            guardado=true;
            NavHostFragment.findNavController(this).navigate(R.id.action_nuevotolista);
        }catch(Exception ex){
            ex.printStackTrace();
            //  Log.e(TAG,ex.getMessage());
            Toast.makeText(getActivity(), "Hubo un error al guardar intente de nuevo", Toast.LENGTH_LONG).show();
            guardar.setEnabled(true);
        }
    }

    @Override
    public void onClickEliminar(ProductoExhibidoDao.ProductoExhibidoFoto foto) {
        //para eliminar las fotos exhibicion
        //Pregunto si está seguro
        Log.d(TAG,"eliminando"+foto.idprodex);
        feviewModel.eliminarFoto(foto);
        //actualizo la lista
        mListAdapter.notifyDataSetChanged();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000) // Intervalo deseado de 10 segundos
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000) // Intervalo mínimo de 5 segundos
                .build();
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if(getActivity()!=null&&txtubicacion!=null) {
                    for (Location location : locationResult.getLocations()) {
                        mostrarPosicion(location);
                    }
                }
            }
        };
    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // La comprobación de permisos ya se hizo, pero es requerida por el linter
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper()); // El Looper en el que se ejecutarán los callbacks
        requestingLocationUpdates = true;
        Log.d(TAG, "Iniciando actualizaciones de ubicación");
    }

    private void stopLocationUpdates() {
        if(requestingLocationUpdates)
            fusedLocationClient.removeLocationUpdates(locationCallback);
        requestingLocationUpdates = false;
        if(alert!=null&&alert.isMostrando())
            alert.closeAlertDialog();
        Log.d(TAG, "Deteniendo actualizaciones de ubicación");
    }
    public void mostrarPosicion(Location location){
        if(alert.isMostrando())
            alert.closeAlertDialog();
        String latitude = String.format("%.6f", location.getLatitude());
        String longitude =  String.format("%.6f",location.getLongitude());
        txtubicacion.setText(latitude + "," + longitude);
        ultimaLoc=location;
        Log.d(TAG,"****Ya tengo la ubicacion" + latitude + "," + longitude);

    }

    public void buscarDireccion(){


        //buscar direccion
        try {
            if(ultlongitud>0){
                ultimaLoc.setLongitude(ultlongitud);
                ultimaLoc.setLatitude(ultlatitud);
            }
            mensajedir.setText("Ubicación registrada");

            if (ComprasUtils.isOnlineNet(getContext())) {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> list = new ArrayList<>();
                milog.info(TAG,"buscarDireccion"," Buscando direccion ultimaloc:"+ultimaLoc);
                if (geocoder != null&&ultimaLoc!=null)
                    list = geocoder.getFromLocation(
                            ultimaLoc.getLatitude(), ultimaLoc.getLongitude(), 1);

                if (!list.isEmpty()) {
                    milog.info(TAG,"buscarDireccion"," Sí hubo");
                    Address DirCalle = list.get(0);
                    String state = DirCalle.getAdminArea();
                    country = DirCalle.getCountryName();
                    String dir=DirCalle.getAddressLine(0);
                    mensajedir.setText(dir.toUpperCase());
                    //   ubicacion.setText(state+","+country);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Error en buscarDireccion "+e.getMessage());
        }

    }



    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1000) {
            if (grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }


    public void tomarFoto(EditText origen, ImageView destino, int REQUEST_CODE) {

        if(getAvailableMemory(getActivity()).lowMemory)
        {
            Toast.makeText(getActivity(), "No hay memoria suficiente para esta accion", Toast.LENGTH_SHORT).show();

            return;
        }
        globrequestcode=REQUEST_CODE;
        File foto=null;
        Activity activity=this.getActivity();
        Intent intento1 = new Intent(getContext(), MiCamaraActivity.class);
        try{


            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");

            String dateString = format.format(new Date());


            nombre_foto = "img_" +Constantes.CLAVEUSUARIO+"_"+ dateString + ".jpg";
            foto = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), nombre_foto);
            Log.d(TAG, "****"+foto.getAbsolutePath());


            Uri photoURI = FileProvider.getUriForFile(activity,
                    "com.example.comprasmu.fileprovider",
                    foto);
            intento1.putExtra(MediaStore.EXTRA_OUTPUT, foto.getAbsolutePath()); //se pasa a la otra activity la referencia al archivo

            intento1.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if(destino!=null) {

                destino.setVisibility(View.VISIBLE);

                startActivityForResult(intento1, REQUEST_CODE);
            }

        } catch (OutOfMemoryError  ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "No hay memoria suficiente para esta accion", Toast.LENGTH_SHORT).show();



        } catch (Exception  ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "No se encontró almacenamiento externo", Toast.LENGTH_SHORT).show();

        }
    }
    /****vuelve el foco***/
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"Estoy aqui ptra vez");
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        try {
            if ((requestCode == REQUEST_CODE_TAKE_PHOTO || requestCode == REQUEST_CODE_PROD1 || requestCode == REQUEST_CODE_PROD2 || requestCode == REQUEST_CODE_PROD3 || requestCode == REQUEST_CODE_PROD4) && resultCode == RESULT_OK) {
                String state = Environment.getExternalStorageState();
                String baseDir;
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    File baseDirFile = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    if (baseDirFile == null) {
                        baseDir = getActivity().getFilesDir().getAbsolutePath();
                    } else {
                        baseDir = baseDirFile.getAbsolutePath();
                    }
                } else {
                    baseDir = getActivity().getFilesDir().getAbsolutePath();
                }
                if (baseDir != null && nombre_foto != null) {
                    File file = new File(baseDir, nombre_foto);
                    if (file.exists()) {

                        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                            //es la de fachada
                            Log.d(TAG, "*****" + file.exists() + "--" + file.getAbsolutePath());
                            yaTengoFoto = false;
                            txtfotofachada.setText(nombre_foto);
                            ComprasUtils cu = new ComprasUtils();
                            Bitmap bitmap1 = cu.comprimirImagen(file.getAbsolutePath());
                            bitmap1 = ComprasUtils.decodeSampledBitmapFromResource(file.getAbsolutePath(), 100, 100);
                            fotofac.setImageBitmap(bitmap1);
                            fotofac.setVisibility(View.VISIBLE);
                            rotar.setVisibility(View.VISIBLE);
                            aifotofacgroup.setVisibility(View.VISIBLE);
                            guardarUbicacion();
                            yaTengoFoto = true;
                            nombre_foto = null;
                            file = null;
                            //
                        }
                        if (requestCode == REQUEST_CODE_PROD1) {
                            txtfotoex1.setText(nombre_foto);
                            // Bitmap bitmap1 = BitmapFactory.decodeFile(getActivity().getExternalFilesDir(null) + "/" + nombre_foto);
                            ComprasUtils cu = new ComprasUtils();
                            Bitmap bitmap1 = cu.comprimirImagen(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + nombre_foto);
                            //Bitmap bitmap1 =  ComprasUtils.decodeSampledBitmapFromResource(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + nombre_foto,100,100);

                            fotoex1.setImageBitmap(bitmap1);
                            btnrotar1.setVisibility(View.VISIBLE);
                            aiex1group.setVisibility(View.VISIBLE);
                            //  agregarImagen();
                        }
                        if (requestCode == REQUEST_CODE_PROD2) {
                            txtfotoex2.setText(nombre_foto);
                            //  Bitmap bitmap2 = BitmapFactory.decodeFile(getActivity().getExternalFilesDir(null) + "/" + nombre_foto);
                            ComprasUtils cu = new ComprasUtils();
                            Bitmap bitmap2 = cu.comprimirImagen(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + nombre_foto);

                            fotoex2.setImageBitmap(bitmap2);
                            btnrotar2.setVisibility(View.VISIBLE);
                            //  agregarImagen();
                        }

                        if (requestCode == REQUEST_CODE_PROD3) {
                            txtfotoex3.setText(nombre_foto);
                            // Bitmap bitmap1 = BitmapFactory.decodeFile(getActivity().getExternalFilesDir(null) + "/" + nombre_foto);
                            ComprasUtils cu = new ComprasUtils();
                            Bitmap bitmap1 = cu.comprimirImagen(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + nombre_foto);

                            fotoex3.setImageBitmap(bitmap1);
                            btnrotar3.setVisibility(View.VISIBLE);
                            //   agregarImagen();
                        }
                        if (requestCode == REQUEST_CODE_PROD4) {
                            txtfotoex4.setText(nombre_foto);
                            // Bitmap bitmap1 = BitmapFactory.decodeFile(getActivity().getExternalFilesDir(null) + "/" + nombre_foto);
                            ComprasUtils cu = new ComprasUtils();
                            Bitmap bitmap1 = cu.comprimirImagen(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + nombre_foto);

                            fotoex4.setImageBitmap(bitmap1);
                            btnrotar4.setVisibility(View.VISIBLE);
                            //   agregarImagen();
                        }


                    } else
                        Toast.makeText(getContext(), "No se encontró el archivo intente de nuevo", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getContext(), "No se encontró la ruta del archivo intente de nuevo", Toast.LENGTH_LONG).show();

                    milog.grabarError(TAG + " onActivityResult Algo salió mal???" + baseDir + "--" + nombre_foto);
                    Log.d(TAG, "Algo salió mal???" + baseDir + "--" + nombre_foto);
                }


            } else {
                super.onActivityResult(requestCode, resultCode, data);
                Log.d(TAG, "Algo salió muy mal");
            }

        }catch (Exception ex){
            Toast.makeText(getActivity(), "Hubo un error, intente de nuevo", Toast.LENGTH_LONG).show();
            milog.grabarError(TAG ,"onActivityResult", ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void rotar(View view,EditText nombrefoto, ImageView foto) {
        String nombreArch="";
        if (AbririnformeFragment.getAvailableMemory(getActivity()).lowMemory) {
            Toast.makeText(getActivity(), "No hay memoria suficiente para esta accion", Toast.LENGTH_SHORT).show();

            return;
        } else {
            if (nombrefoto != null) {
                nombreArch = nombrefoto.getText().toString();
            }
            RevisarFotoActivity.rotarImagen(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + nombreArch, foto);
        }
    }
    public static ActivityManager.MemoryInfo getAvailableMemory(Activity act) {
        ActivityManager activityManager = (ActivityManager) act.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        return memoryInfo;
    }

    private void preguntarBorrarFoto(View cb,EditText txtruta,ImageView foto,ImageButton btnrotar,LinearLayout group) {
        if(((CheckBox)cb).isChecked()) {
            //veo si ya hay foto
            if(txtruta.getText()!=null&&!txtruta.getText().toString().equals("")) {
                //pregunto si habrá más clientes
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getActivity());
                // dialogo1.setTitle(R.string.);
                dialogo1.setMessage(R.string.eliminar_foto);
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //eliminar foto
                        txtruta.setText("");
                        foto.setImageBitmap(null);

                        foto.setVisibility(View.GONE);
                        btnrotar.setVisibility(View.GONE);
                    }
                });
                dialogo1.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        ((CheckBox) cb).setChecked(false);
                        dialogo1.cancel();
                        //envio a la lista
                        //  NavHostFragment.findNavController(AbririnformeFragment.this).navigate(R.id.action_nuevotolista);

                    }
                });
                dialogo1.show();
            }
        }
    }
    public void limpiarVariables(){
        Constantes.productoSel=null;
        Constantes.VarDetalleProd.tomadode=null;
        Constantes.VarDetalleProd.nvoatra=null;
        Constantes.VarDetalleProd.nvoatrb=null;
        Constantes.VarDetalleProd.nvoatrc=null;
        Constantes.DP_TIPOTIENDA=0;
        Constantes.NM_TOTALISTA=0;
        Constantes.ni_clientesel=null;
        mViewModel.limpiarVarInforme();
        mViewModel.setIdInformeNuevo(0);
        Constantes.DP_CONSECUTIVO=0;

    }

}