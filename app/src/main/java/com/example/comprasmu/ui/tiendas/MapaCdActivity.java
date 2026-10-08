package com.example.comprasmu.ui.tiendas;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.comprasmu.DescargarListaAsyncTask;
import com.example.comprasmu.NavigationDrawerActivity;
import com.example.comprasmu.R;
import com.example.comprasmu.data.ComprasDataBase;
import com.example.comprasmu.data.PeticionesServidor;
import com.example.comprasmu.data.dao.ListaCompraDao;
import com.example.comprasmu.data.dao.TiendaEstatusClienteDao;
import com.example.comprasmu.data.modelos.CatalogoDetalle;
import com.example.comprasmu.data.modelos.Contrato;
import com.example.comprasmu.data.modelos.Correccion;
import com.example.comprasmu.data.modelos.DescripcionGenerica;
import com.example.comprasmu.data.modelos.Geocerca;
import com.example.comprasmu.data.modelos.ListaCompra;
import com.example.comprasmu.data.modelos.ListaCompraDetalle;
import com.example.comprasmu.data.modelos.MuestrasxZona;
import com.example.comprasmu.data.modelos.Tienda;
import com.example.comprasmu.data.modelos.TiendaEstatusCliente;
import com.example.comprasmu.data.remote.RespInfEtapaResponse;
import com.example.comprasmu.data.remote.RespInformesResponse;
import com.example.comprasmu.data.repositories.ListaCompraDetRepositoryImpl;
import com.example.comprasmu.data.repositories.ListaCompraRepositoryImpl;
import com.example.comprasmu.data.repositories.TablaVersionesRepImpl;
import com.example.comprasmu.services.DescAutomaticasServiceManager;
import com.example.comprasmu.ui.listadetalle.ListaDetalleViewModel;
import com.example.comprasmu.utils.ComprasLog;
import com.example.comprasmu.utils.ComprasUtils;
import com.example.comprasmu.utils.Constantes;
import com.example.comprasmu.utils.CreadorFormulario;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapaCdActivity extends AppCompatActivity implements OnMapReadyCallback ,
        GoogleMap.OnInfoWindowCloseListener,
GoogleMap.OnMapClickListener,
GoogleMap.OnMarkerClickListener,
GoogleMap.OnInfoWindowClickListener,
        DescargarListaAsyncTask.ProgresoDLListener {
    private static final int LOCATION_REQUEST_CODE = 1;
    private static  boolean viendoRecorrido =false;
    private GoogleMap mMap;
    String[] coloreszon={"#1E90FF","#FF1493", "#32CD32", "#FF8C00", "#4B0082"};
    Map<String,Float> coloresTienda;
    private  final String TAG="MapaCdActivity";
    private ArrayList<DescripcionGenerica> listaPlantasEnv;
    LiveData<List<Tienda>> listatiendas;
    List<Tienda> nollistatiendas;
    Button btnverfil;
    List<Geocerca> listageocercas;
    List<Polygon> regionPolygon;
    List<Marker> martiendas;
    int cdId;
    boolean doubleBackToExitPressedOnce = false;
    ListaDetalleViewModel lcviewModel;
    Button btncancel;
    Marker markerSel;
    LinearLayout llfiltros;
    //llcancel,
    LinearLayout lltipotienda,llcadena;
    private final long lastClickTime = 0;
    private static final int DEFAULT_ZOOM = 4;
    Spinner spplantas;
  //  List<DescripcionGenerica>clientesAsignados;

    private boolean locationPermissionGranted;
    private final LatLng defaultLocation = new LatLng(19.36884,  -99.16410);

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    private boolean requestingLocationUpdates;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private int cliente;
    private int plantaId;
    private Spinner sptipoti, spcadena, spfecha,spseleccion;

    private boolean verfiltros;
    LoadingAlert alert;
    private LocationCallback locationCallback;
    private LocationManager fusedLocationClient;
    private MapaCdActivity.miLocationListener locallis;
    ComprasLog compraslog;
    LinearLayout mensajetienda;
    TextView txtcerrarmensaje;
    Circle circleNuevaTienda;
    Button btnvatienda;
    private TextView txtnuevatmensaje;
    Button botonRecorrido;
    private Toolbar myChildToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_cd);
        myChildToolbar =findViewById(R.id.toolbarmapa);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        //ActionBar ab = getSupportActionBar();

        // Enable the Up button
   //     ab.setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment = FirstMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mapcd_container, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
        lcviewModel = new ViewModelProvider(this).get(ListaDetalleViewModel.class);
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            //   cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        mapFragment.getMapAsync(this);
        btnvatienda=findViewById(R.id.btnmcdnvati);
        mensajetienda=findViewById(R.id.llmapamensajetienda);
        txtcerrarmensaje=findViewById(R.id.txtmapcerrarmensaje);
        txtnuevatmensaje=findViewById(R.id.txtmapnvatiendamsj);
        verfiltros=false;
        compraslog=ComprasLog.getSingleton();
        spplantas=findViewById(R.id.spmcdplanta);
        spcadena=findViewById(R.id.spmccadenati);
        sptipoti=findViewById(R.id.spmctipoti);
        // spfecha=findViewById(R.id.spmctiempo);
        spseleccion=findViewById(R.id.spmcdseltienda);
        llfiltros=findViewById(R.id.llmfiltros);
        // llcancel=findViewById(R.id.llmcancel);
        lltipotienda=findViewById(R.id.llmcdtipotienda);
        llcadena=findViewById(R.id.llmcdcadena);
        llfiltros.setVisibility(View.GONE);
        // llcancel.setVisibility(View.GONE);
        mensajetienda.setVisibility(View.GONE);
        btnverfil=findViewById(R.id.btnmfiltros);
        btnvatienda.setEnabled(false);
        viendoRecorrido=false;
        BuscadorTiendas buscador=new BuscadorTiendas();
        // Log.d(TAG,"res "+buscador.dentroDelCirculo(16.7648672,-93.0821975,16.764111,-93.081486));
        btnverfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verFiltros();

            }
        });
     /*   btncancel=view.findViewById(R.id.btnmccancel);
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(markerSel!=null) {
                    Tienda tiendaSel=(Tienda)markerSel.getTag();
                    if(tiendaSel!=null) {
                        new AlertDialog.Builder(this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.importante)
                                .setMessage(getString(R.string.cancelar_tienda) + " " + tiendaSel.getUne_descripcion() + "?")
                                .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //hago la peticion en el servidor y recargo
                                        //this.finish();
                                        PeticionMapaCd petmap = new PeticionMapaCd(Constantes.CLAVEUSUARIO);
                                        petmap.cancelTienda(tiendaSel.getUne_id());
                                        //  martiendas.remove(markerSel);
                                        markerSel.remove();

                                    }
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }
                }
            }
        });
*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getDeviceLocation();
        } else {
            compraslog.grabarError(TAG, "create","no hay permiso para gps "+LOCATION_REQUEST_CODE);
            // Solicitar permiso
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);

        }
        //actualizo tiendas

        //actualizo la lista de compra
        actualizarListaCompra();

        cargarCatalogos();
        botonRecorrido=findViewById(R.id.btnmcdtemp);
        //  cargarIndices();
        Button btnbuscar=findViewById(R.id.btnmcdbuscar);
        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DescripcionGenerica plantasel=(DescripcionGenerica)spplantas.getSelectedItem();
                viendoRecorrido=false;
                btnvatienda.setEnabled(true);
                botonRecorrido.setSelected(false);
                if(plantasel!=null) {
                    plantaId = plantasel.id;
                    //calculo indice fin
                    buscarTiendas(plantaId);
                }
                else
                    irAcdSel();

            }
        });

        btnvatienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mensajetienda.getVisibility()==View.GONE)
                    if(!viendoRecorrido) //desde el recorrido ya no se puede hacer una nueva tienda es solo consulta
                        nuevaTienda();
            }
        });

        botonRecorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DescripcionGenerica plantasel=(DescripcionGenerica)spplantas.getSelectedItem();
                viendoRecorrido=true;
                btnvatienda.setEnabled(false);
                botonRecorrido.setSelected(true);
                if(plantasel!=null) {
                    plantaId = plantasel.id;

                    buscarTiendasActuales(plantaId);
                }
            }
        });
        //inicio colores tienda
        coloresTienda=new HashMap<>();
        coloresTienda.put("3",BitmapDescriptorFactory.HUE_GREEN);//verde
        coloresTienda.put("2",BitmapDescriptorFactory.HUE_YELLOW);//amarillo
        coloresTienda.put("1",BitmapDescriptorFactory.HUE_RED);

        ArrayList<DescripcionGenerica> listaop;
        listaop=new ArrayList<DescripcionGenerica>();
        listaop.add(new DescripcionGenerica(0,"SELECCIONAR OPCION"));
        listaop.add(new DescripcionGenerica(1,"POR TIPO TIENDA"));
        listaop.add(new DescripcionGenerica(2,"POR CADENA"));
        CreadorFormulario.cargarSpinnerDescr(this,spseleccion,listaop);
        spseleccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the value selected by the user
                // e.g. to store it as a field or immediately call a method
                DescripcionGenerica opcion = (DescripcionGenerica) parent.getSelectedItem();
                if(opcion.getId()==1){
                    //muestro lista de tipo tiendas
                    lltipotienda.setVisibility(View.VISIBLE);
                }
                else
                {
                    lltipotienda.setVisibility(View.GONE);
                    sptipoti.setSelection(0);
                }
                if(opcion.getId()==2){
                    //muestro lista de tipo tiendas
                    llcadena.setVisibility(View.VISIBLE);
                }
                else
                {
                    llcadena.setVisibility(View.GONE);
                    spcadena.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        txtcerrarmensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mensajetienda.setVisibility(View.GONE);
                circleNuevaTienda.remove();
                btnvatienda.setEnabled(true);

            }
        });
        txtnuevatmensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iraAbrirInforme();
            }
        });


    }

    private void actualizarListaCompra() {
        //desactivo descargas automaticas
        DescAutomaticasServiceManager.getInstancia().pausarServicio();
        if (NavigationDrawerActivity.isOnlineNet(this)) {
            alert = new LoadingAlert(this);
            alert.startAlert();
            TablaVersionesRepImpl tvRepo = new TablaVersionesRepImpl(this);
            ListaCompraDao dao = ComprasDataBase.getInstance(this).getListaCompraDao();
            ListaCompraDetRepositoryImpl lcdrepo = new ListaCompraDetRepositoryImpl(this);
            ListaCompraRepositoryImpl lcrepo = ListaCompraRepositoryImpl.getInstance(dao);
            PeticionesServidor ps = new PeticionesServidor(Constantes.CLAVEUSUARIO);
            DescargarListaAsyncTask task = new DescargarListaAsyncTask(this, tvRepo, lcdrepo, lcrepo, this, ps, Constantes.CIUDADTRABAJO);
            task.execute("");
        }
        else{
            Toast.makeText(this,"Esta acción requiere conexión a internet, verifique",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "mapa listo ");
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();

        } else {
            Log.d(TAG, "no tengo ubicaacion "+LOCATION_REQUEST_CODE);
            if(alert.isMostrando())
                alert.closeAlertDialog();
            // Solicitar permiso
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            getDeviceLocation();

        }

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Get the current location of the device and set the position of the map.

    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        Log.d(TAG, "buscando ");
        try {
            if (locationPermissionGranted) {

                fusedLocationClient=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                locallis=new MapaCdActivity.miLocationListener();
                // this.lastKnownLocation=fusedLocationClient.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                final boolean networkEnabled = fusedLocationClient.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                final boolean gpsEnabled = fusedLocationClient.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!gpsEnabled&&!networkEnabled) {
                    Log.d(TAG, "1");
                    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(settingsIntent);
                    return;
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                    Log.d(TAG, "2");
                    return;
                }
                if (Build.PRODUCT.contains ("sdk")||Build.MODEL.contains (Constantes.modelo)) {//pruebas y el lenovo//entro rapido
                    //primero gps
                    if (fusedLocationClient.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                        //  if (Local == null) { //Validación que evita NullPointerException
                        //Requiere actualización
                        fusedLocationClient.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locallis);

                        // }
                        Log.d(TAG, "4");
                    } else if (fusedLocationClient.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                        fusedLocationClient.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 1, locallis);

                        Log.d(TAG, "3");

                    } else
                        Toast.makeText(this, "No hay gps?", Toast.LENGTH_SHORT).show();

                }else {

                    if (fusedLocationClient.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                        fusedLocationClient.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 1, locallis);

                        Log.d(TAG, "3");

                    } else if (fusedLocationClient.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                        //  if (Local == null) { //Validación que evita NullPointerException
                        //Requiere actualización
                        fusedLocationClient.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locallis);

                        // }
                        Log.d(TAG, "4");
                    } else
                        Toast.makeText(this, "No hay gps?", Toast.LENGTH_SHORT).show();
                }

            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);

        }
    }

    public void guardarPosicion(Location loc)
    {
        lastKnownLocation=loc;
        if (lastKnownLocation != null&&mMap!=null) {
            Log.d(TAG, "Current location is "+lastKnownLocation.getLatitude()+","+
                    lastKnownLocation.getLongitude());
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        //  btncancel.setVisibility(View.VISIBLE);
        // llcancel.setVisibility(View.VISIBLE);
        markerSel=marker;
        return false;
    }

    public void nuevaTienda(){
        BuscadorTiendas bt=new BuscadorTiendas();
        if(lastKnownLocation!=null) {
            compraslog.info(TAG,".nuevatienda lastKnownLocation:",lastKnownLocation.getLatitude()+"--"+lastKnownLocation.getLongitude());
            if(nollistatiendas==null){
                DescripcionGenerica plantasel=(DescripcionGenerica)spplantas.getSelectedItem();
                if(plantasel!=null)
                    plantaId=plantasel.getId();
                //nollistatiendas=lcviewModel.getTiendasByPlantaSimp(Constantes.CIUDADTRABAJO,plantaId);
                nollistatiendas=lcviewModel.getTiendasSimp(Constantes.CIUDADTRABAJO,0,0,0,0);

            }
            if(nollistatiendas!=null) {
                Log.d(TAG, ".nuevatienda tot tiendas:"+nollistatiendas.size());

                compraslog.info(TAG, ".nuevatienda ", "tot tiendas:"+nollistatiendas.size());
                if (bt.hayTiendas(nollistatiendas, lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude(), compraslog)) {
                    compraslog.info(TAG, ".nuevatienda ", "ya existe");
                    //solo informativo te recomendamos visitar una tienda existente
                  /*  AlertDialog nuevaTiendaDialog= new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.importante)
                            .setMessage(getString(R.string.recomend_tienda))
                         /*   .setPositiveButton(R.string.nueva_tienda, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("nuevatienda", true);
                                    //  bundle.putString("ciudadNombre", listaSeleccionable.get(i).getNombre());
                                    NavController nav = NavHostFragment.findNavController(MapaCdFragment.this);
                                    Log.d(TAG, nav.getCurrentDestination().getId() + "--" + R.id.nav_tiendas);
                                    if (nav.getCurrentDestination().getId() == R.id.nav_tiendas) {

                                        nav.navigate(R.id.action_buscartonuevo, bundle);
                                        //  NavHostFragment.findNavController(this).navigate(R.id.action_ciudadtohome);
                                    }

                                }
                            })*/

                       /*     .setNegativeButton(R.string.regresar, null)
                            .create();
                    nuevaTiendaDialog.show();
                    Window window = nuevaTiendaDialog.getWindow();
                    if(window!=null)
                        window.setGravity(Gravity.TOP);*/
                    mensajetienda.setVisibility(View.VISIBLE);
                    btnvatienda.setEnabled(false);
                    if(circleNuevaTienda!=null)
                        circleNuevaTienda.remove();
                    circleNuevaTienda = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                            .radius(200)
                            .strokeColor(Color.RED));


                }else {
                   /* Bundle bundle = new Bundle();
                    bundle.putBoolean("nuevatienda", true);
                    NavController nav = NavHostFragment.findNavController(MapaCdActivity.this);
                    Log.d(TAG, nav.getCurrentDestination().getId() + "--" + R.id.nav_tiendas);
                    if (nav.getCurrentDestination().getId() == R.id.nav_tiendas) {

                        nav.navigate(R.id.action_buscartonuevo, bundle);
                        //  NavHostFragment.findNavController(this).navigate(R.id.action_ciudadtohome);
                    }*/

                    Intent intent = new Intent(this, NavigationDrawerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("nuevatienda", true);
                    bundle.putInt("NAV_DESTINATION", R.id.nav_nuevoinforme);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }

            }else{
                Intent intent = new Intent(this, NavigationDrawerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("nuevatienda", true);
                bundle.putInt("NAV_DESTINATION", R.id.nav_nuevoinforme); // id del fragment en nav_graph
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();


            }

        }else{
            Toast.makeText(this,"Espere para registrar su ubicación",Toast.LENGTH_LONG).show();

        }
    }

    public void verFiltros(){
        if(!verfiltros){
            btnverfil.setText("OCULTAR FILTROS");
            llfiltros.setVisibility(View.VISIBLE);
            verfiltros=true;
        }else {
            btnverfil.setText("VER FILTROS");
            llfiltros.setVisibility(View.GONE);
            verfiltros=false;
        }
    }


    public void dibujarZonas(List<Geocerca> zonas){
        regionPolygon=new ArrayList<Polygon>();

        for(Geocerca geo:zonas){
            String[] aux =geo.getGeo_p1().split(",");
            LatLng p1 = new LatLng(Double.parseDouble(aux[0]), Double.parseDouble(aux[1]));
            String[] aux2 =geo.getGeo_p2().split(",");
            LatLng p2 =new LatLng(Double.parseDouble(aux2[0]), Double.parseDouble(aux2[1]));
            String[] aux3 =geo.getGeo_p3().split(",");
            LatLng p3 =new LatLng(Double.parseDouble(aux3[0]), Double.parseDouble(aux3[1]));
            String[] aux4 =geo.getGeo_p4().split(",");
            LatLng p4 =new LatLng(Double.parseDouble(aux4[0]), Double.parseDouble(aux4[1]));

            regionPolygon.add( mMap.addPolygon(new PolygonOptions()
                    .add(p1,p2,p3,p4)
                    .strokeColor(Color.parseColor(coloreszon[geo.getGeo_region()-1]))
                    .strokeWidth(5)
            ));
            //busco el centro para poner la camara
            if(geo.getGeo_region()==5){
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p4, 12));


            }
        }

    }
    public void buscarTiendas( int planta){

        //usaria la ciudad de trabajo

        markerSel=null;
        botonRecorrido.setSelected(false);
        // llcancel.setVisibility(View.GONE);
        //calculo el fin
        //cambio 29/09/25 siempre es 1
        int anios=1;

        //busco el pais y cd de la planta
        int[] aux =lcviewModel.buscarClienCdxPlan(planta, Constantes.INDICEACTUAL);
        cliente=aux[0];
        String ciudad=Constantes.CIUDADTRABAJO;
        //   Log.d(TAG,"--"+0+"--"+ciudad+"..."+planta+".."+cliente+"--"+fini);
        int tipo=((CatalogoDetalle)sptipoti.getSelectedItem()).getCad_idopcion();
        int cadena=((CatalogoDetalle)spcadena.getSelectedItem()).getCad_idopcion();

        //  Log.d(TAG,"pidiendo tiendas"+(new Date()));
        mMap.clear();
        this.listatiendas=lcviewModel.getTiendas(ciudad,anios,tipo,cadena,cliente);
        this.listageocercas= lcviewModel.getGeocercas(ciudad);
        if(listageocercas!=null&&listageocercas.size()>0) {

            dibujarZonas(listageocercas);
        }
        //observo
        this.listatiendas.observe(this, new Observer<List<Tienda>>() {
            @Override
            public void onChanged(List<Tienda> tiendas) {

                //  Log.d(TAG," antes de dibujar"+(new Date()));
                dibujarTiendas(tiendas, planta);

                //   alert.closeAlertDialog();
                listatiendas.removeObservers(MapaCdActivity.this);
            }
        });




    }

    public void dibujarTiendas(List<Tienda> listiendas, int plantaId){
        martiendas=new ArrayList<>();
        LatLng japon2 = null;

        int estatusPepsi;
        int estatusElectro;
        int estatusPeniafiel;
        int estatusJumex;
        TiendaEstatusClienteDao tiendaEstatusClienteDao=ComprasDataBase.getInstance(this).getTiendaEstatusClienteDao();

        if(listiendas!=null) {
            // Log.d(TAG,"--tiendas"+listiendas.size());
            List<TiendaEstatusCliente> estatusTienda;
            StringBuilder estatusClientes = new StringBuilder();
            String color="3";
            ArrayList<DescripcionGenerica> plantasDisponibles;
            HashMap<Integer,Integer> totalPlantas=lcviewModel.getTotalPlantasxCliente(Constantes.CIUDADTRABAJO);
            for (Tienda tienda : listiendas) {
                Log.d(TAG, tienda.getUne_id() + "--" + tienda.getUne_descripcion() + "--" + tienda.getEstpep() + "--" + tienda.getEstpen());

                //busco los estatus por planta
                estatusTienda = lcviewModel.buscarEstatusTienda(tienda.getUne_id(), tiendaEstatusClienteDao);
                estatusClientes = new StringBuilder();
                color = "3";
                estatusPepsi = 1;
                estatusPeniafiel = 1;
                estatusJumex = 1;
                estatusElectro = 1;
                //armo lista de plantas de la ciudad
                plantasDisponibles = new ArrayList<>();
                plantasDisponibles.addAll(listaPlantasEnv);
                //  Log.i(TAG,"size antes>>"+estatusTienda.size());
                //veo si tengo estatus rojo

                //busco el cliente de la planta seleecionada
                String nombreCliente = this.buscarCliente(plantaId);
                switch (nombreCliente) {
                    case "PEPSI":
                        if (tienda.getEstpep()!=null&&tienda.getEstpep() == 2) {
                            color = "1";

                        }
                        break;
                    case "PEÑAFIEL":
                        if (tienda.getEstpen()!=null&&tienda.getEstpen() == 2) {
                            color = "1";

                        }
                        break;
                    case "ELECTROPURA":
                        if (tienda.getEstele()!=null&&tienda.getEstele() == 2) {
                            color = "1";

                        }
                        break;
                    case "JUMEX":
                        if (tienda.getEstjum()!=null&&tienda.getEstjum() == 2) {
                            color = "1";

                        }
                        break;
                }
                if (estatusTienda != null)

                    for (TiendaEstatusCliente estatus : estatusTienda
                    ) {
                        if (estatus.getPlantasId() == plantaId) {
                            color = validarColorTienda(estatus.getEstatus());

                        }
                        //para poner en que tiendas no puedo comprar


                        if (estatus.getEstatus() == 2) {
                            if (plantasDisponibles != null)
                                plantasDisponibles = quitarPlanta(plantasDisponibles, estatus.getPlantasId());


                        }


                    }
                if(tienda.getEstpep()!=null&&tienda.getEstpep()==2) {

                }else{
                    //validar estatuscliente
                    estatusPepsi = lcviewModel.getEstatusCliente(tienda.getUne_id(), totalPlantas, 4, tiendaEstatusClienteDao);
                    tienda.setEstpep(estatusPepsi);
                }
                if(tienda.getEstpen()!=null&&tienda.getEstpen()==2) {

                }else{
                    estatusPeniafiel = lcviewModel.getEstatusCliente(tienda.getUne_id(), totalPlantas, 5, tiendaEstatusClienteDao);
                    tienda.setEstpen(estatusPeniafiel);
                }
                if(tienda.getEstele()!=null&&tienda.getEstele()==2) {

                }else{
                    estatusElectro = lcviewModel.getEstatusCliente(tienda.getUne_id(), totalPlantas, 6, tiendaEstatusClienteDao);
                    tienda.setEstele(estatusElectro);
                }
                if(tienda.getEstjum()!=null&&tienda.getEstjum()==2) {
                }else{
                    estatusJumex = lcviewModel.getEstatusCliente(tienda.getUne_id(), totalPlantas, 7, tiendaEstatusClienteDao);
                    tienda.setEstjum(estatusJumex);
                }
                //busco los estatus por cliente

                Log.d(TAG,"despues"+tienda.getUne_id()+"--"+tienda.getUne_descripcion()+"--"+tienda.getEstpep()+"--"+tienda.getEstpen()+"--"+tienda.getEstele()+"--"+tienda.getEstjum());

                //  Log.i(TAG,"size>>"+plantasDisponibles.size());
                //el estatus es 1-rojo, 2 amarillo, 3.verde solo en verde puedo comprar o con 0
                int i=0;
                if (!plantasDisponibles.isEmpty()) {
                    for (DescripcionGenerica descripcion:plantasDisponibles
                    ) {
                        estatusClientes.append(", ");
                        estatusClientes.append(descripcion.getNombre());
                        i++;
                    }

                    estatusClientes = new StringBuilder(estatusClientes.substring(2, estatusClientes.length()));
                }
                tienda.setColor(color);

                //latitud es x longitud es y
                //  Log.d(TAG,"--"+tienda.getUne_descripcion()+tienda.getCiudad()+".."+tienda.getUne_descripcion());
                if (tienda.getUne_coordenadasxy() != null && tienda.getUne_coordenadasxy().length() > 0) {
                    String[] aux = tienda.getUne_coordenadasxy().split(",");

                    try {
                        japon2 = new LatLng(Double.parseDouble(aux[0]), Double.parseDouble(aux[1]));
                        MarkerOptions moptions = new MarkerOptions();
                        moptions.position(japon2)
                                .title(tienda.getUne_descripcion())
                                .icon(BitmapDescriptorFactory.defaultMarker(coloresTienda.get(color)));
                        if (estatusClientes.length() > 0) {
                            moptions.snippet(estatusClientes.toString());
                        }
                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            @Override
                            public View getInfoWindow(Marker arg0) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {

                                Context context = MapaCdActivity.this; //or this, YourActivity.this, etc.

                                LinearLayout info = new LinearLayout(context);
                                info.setOrientation(LinearLayout.VERTICAL);

                                TextView title = new TextView(context);
                                title.setTextColor(Color.BLACK);
                                title.setGravity(Gravity.CENTER);
                                title.setTypeface(null, Typeface.BOLD);
                                title.setText(marker.getTitle());

                                TextView snippet = new TextView(context);
                                snippet.setTextColor(Color.GRAY);
                                snippet.setTextSize(10);
                                snippet.setText(marker.getSnippet());

                                info.addView(title);
                                info.addView(snippet);

                                return info;
                            }
                        });
                        Marker marker = mMap.addMarker(moptions
                        );
                        marker.setTag(tienda);

                        martiendas.add(marker);
                    } catch (NumberFormatException ex) {
                        Log.e(TAG, "error de formato " + ex.getMessage() + "  " + tienda.getUne_descripcion());
                    }

                }

            }
        }
        if(japon2!=null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(japon2,10));
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 4));
        Log.e(TAG, "viendoRecorrido" + viendoRecorrido);
        if(!viendoRecorrido)
            btnvatienda.setEnabled(true);
    }

    public String validarColorTienda(int estatus){
        if( estatus > 0) {

            return estatus+"";
        }
        else
            return "3";
    }
   /* public void buscarClientes(){
        //   Log.d(TAG,"cd "+Constantes.CIUDADTRABAJO);
        if(Constantes.CIUDADTRABAJO==null||Constantes.CIUDADTRABAJO.equals("")){

            if(alert!=null)
                alert.closeAlertDialog();
            irAcdSel();

            return;
        }
        List<ListaCompra> data=lcviewModel.cargarClientesSimplxet(Constantes.CIUDADTRABAJO, 2);
        List<ListaCompraDetalle> listaDetalle;
        //para cada cliente reviso si le falta comprar
        for (ListaCompra listaCompra: data
             ) {
            listaDetalle=lcviewModel.cargarDetallesSimple(listaCompra.getId());
            if(listaDetalle!=null)
                if(!calcularTotales(listaDetalle)) //ya termino de comprar
                    //lo saco de la lista
                    data.remove(listaCompra);

        }
        Log.d(TAG, "regresó de la consulta de clientes " + data.size()+"--"+Constantes.CIUDADTRABAJO);
       // clientesAsignados = convertirListaaClientes(data);
        // CreadorFormulario.cargarSpinnerDescr(getContext(),spclientes,clientesAsignados);
    }*/

    public  List<DescripcionGenerica> convertirListaaClientes(List<ListaCompra> lista){
        int i=0;
        List<DescripcionGenerica> mapa=new ArrayList<>();

        if(lista!=null)
            for (ListaCompra listaCompra: lista ) {
                DescripcionGenerica item=new DescripcionGenerica();
                //  Log.d(TAG,"-estoy aqui"+listaCompra.getClientesId());
                item.setId(listaCompra.getClientesId());
                item.setNombre(listaCompra.getClienteNombre());
                mapa.add(item);

            }
        return mapa;
    }
    public void buscarPlantas(String ciudadNombre){
        //para buscar las plantas
        List<ListaCompra> listacomp = lcviewModel.cargarPestanasxEtaSimp(ciudadNombre);


        List<ListaCompraDetalle> listaDetalle;
        //para cada cliente reviso si le falta comprar

        Iterator<ListaCompra> iterator = listacomp.iterator();

        while (iterator.hasNext()) {
            ListaCompra listaCompra = iterator.next();
            listaDetalle=lcviewModel.cargarDetallesSimple(listaCompra.getId());
            if(listaDetalle!=null)
                if(!calcularTotales(listaDetalle)) //ya termino de comprar
                            //lo saco de la lista
                    iterator.remove();

        }
        convertirLista(listacomp);
        // setLista(listaClientesEnv);
        // siguiente(0);
        Log.d(TAG,"------- "+listacomp.size());
        if(listacomp.size()>0) {
                    //cargo el spinner
            CreadorFormulario.cargarSpinnerDescr(this,spplantas,listaPlantasEnv);
            DescripcionGenerica plantasel=(DescripcionGenerica)spplantas.getSelectedItem();
            if(plantasel==null) {
                        //busco el 1o de la lista
                        DescripcionGenerica primero=listaPlantasEnv.get(0);
                        buscarTiendas(primero.id);
                    }else {

                        int planta = plantasel.id;
                        buscarTiendas(planta);
                    }


                }
                else
                    Log.d(TAG,"algo salió mal con la consulta de listas");


    }

    public void cargarCatalogos(){
        CatalogoDetalle ins=new CatalogoDetalle();
        ins.setCad_idopcion(0);
        ins.setCad_idcatalogo(1);
        ins.setCad_descripcionesp("TODAS");
        List<CatalogoDetalle> listacadena=new ArrayList<>();
        listacadena.add(ins);

        listacadena.addAll(lcviewModel.buscarCadenaComer());

        if(listacadena.size()>0) {
            //cargo el spinner

            CreadorFormulario.cargarSpinnerCat(this,spcadena,listacadena);
        }
        List<CatalogoDetalle> listatipo=new ArrayList<>();
        ins.setCad_idcatalogo(Contrato.CatalogosId.CADENACOMER);
        listatipo.add(ins);
        listatipo.addAll(lcviewModel.buscarTipoTienda());
        if(listatipo.size()>0) {
            //cargo el spinner

            CreadorFormulario.cargarSpinnerCat(this,sptipoti,listatipo);
        }
    }

    /* public void setupListAdapter() {
         adaptadorLista = new ListaSelecFragment.AdaptadorListas((AppCompatActivity) this,mViewModel);

         objetosLV.setAdapter(adaptadorLista);

     }*/
    public void irAcdSel(){
      /*  NavHostFragment navHostFragment =
                (NavHostFragment) this.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        if(navController!=null)
            navController.navigate(R.id.action_buscartocdtrab);*/
        Intent intent = new Intent(this, NavigationDrawerActivity.class);
        intent.putExtra("NAV_DESTINATION", R.id.nav_ciudad_trabajo); // id del fragment en nav_graph
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    private  void convertirLista(List<ListaCompra>lista){
        listaPlantasEnv=new ArrayList<DescripcionGenerica>();
        for (ListaCompra listaCompra: lista ) {
          /*String tupla=Integer.toString(listaCompra.getClienteId())+";"+
          listaCompra.getPlantaNombre();*/

            listaPlantasEnv.add(new DescripcionGenerica(listaCompra.getPlantasId(), listaCompra.getClienteNombre()+" "+listaCompra.getPlantaNombre(),listaCompra.getClienteNombre()));

        }

    }

    public ArrayList<DescripcionGenerica> quitarPlanta(ArrayList<DescripcionGenerica> listaPlantas,int planta){
        DescripcionGenerica elementoBorrar=null;
        for (DescripcionGenerica descipcion: listaPlantas
        ) {
            //  Log.i(TAG,"<<<"+descipcion.getNombre());
            if(descipcion.getId()==planta) {
                elementoBorrar=descipcion;
                //      Log.i(TAG,"quitando:"+planta);
                break;
            }
        }
        listaPlantas.remove(elementoBorrar);
        return listaPlantas;
    }

    public String buscarCliente(int planta){
        DescripcionGenerica elementoBorrar=null;
        for (DescripcionGenerica descripcion: listaPlantasEnv
        ) {
            //   Log.i(TAG,"<<<"+descripcion.getNombre());
            if(descripcion.getId()==planta) {
                return descripcion.getDescripcion();
            }
        }
        return "";
    }
    @Override
    public void onResume() {
        super.onResume();
        if(lastKnownLocation==null&&this.locationPermissionGranted){
            getDeviceLocation();
        }
    }


    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        //reviso si hay mas clientes si no no tiene caso
        Tienda tienda=(Tienda)marker.getTag();
        if(viendoRecorrido){
            return;
        }
        if(tienda.getColor()=="2"){
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("nuevatienda",false);
        bundle.putInt("idtienda", tienda.getUne_id());
        bundle.putString("nombretienda", tienda.getUne_descripcion());
        bundle.putInt("tipotienda", tienda.getUne_tipotienda());
        bundle.putString("direccion", tienda.getUne_direccion());
        bundle.putString("color", tienda.getColor());
        bundle.putInt("estpep", tienda.getEstpep());
        bundle.putInt("estpen", tienda.getEstpen());
        bundle.putInt("estele", tienda.getEstele());
        bundle.putInt("estjum", tienda.getEstjum());
        bundle.putInt("NAV_DESTINATION", R.id.nav_nuevoinforme);
        this.doubleBackToExitPressedOnce = false;
        if(lastKnownLocation!=null) {
            bundle.putString("coordenasmapa", tienda.getUne_coordenadasxy() );  //ahora paso las coordenadas
         /*   NavHostFragment.findNavController(MapaCdActivity.this).navigate(R.id.action_buscartonuevo, bundle);*/
            Intent intent = new Intent(this, NavigationDrawerActivity.class);
            // id del fragment en nav_graph
            intent.putExtras( bundle);  //ahora paso las coordenadas)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this,"Espere para registrar su ubicación",Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onInfoWindowClose(@NonNull Marker marker) {
        Log.d(TAG,"eto cuando es???????");
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        //   Log.d(TAG,"ocultando++"+btncancel.getVisibility());
        //  llcancel.setVisibility(View.GONE);
        markerSel=null;
    }


    @Override
    public void onDestroy() {
        if(alert!=null) {
            alert.closeAlertDialog();

        }
        if (this.locallis != null)
            locallis.desactivar();
        super.onDestroy();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.locallis != null)
            locallis.desactivar();

    }

    @Override
    public void todoBien(RespInfEtapaResponse maininfoetaResp, RespInformesResponse maininfoResp, List<Correccion> mainRespcor) {
        if(this!=null&&Constantes.INDICEACTUAL!=null) {


            String ffin= "";
            ffin= ComprasUtils.indiceaFecha2(Constantes.INDICEACTUAL);
            descargarTiendas(Constantes.CIUDADTRABAJO,ffin).observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {

                    buscarPlantas(Constantes.CIUDADTRABAJO);
                    //buscarClientes();

                    alert.closeAlertDialog();
                    if(!aBoolean)
                        Toast.makeText(MapaCdActivity.this,"Hubo un error en la consulta de tiendas, revise su conexión a internet e intente de nuevo",Toast.LENGTH_LONG).show();
                }
            });
        }
        else
            if(alert!=null ) alert.closeAlertDialog();
    }

    @Override
    public void notificarSinConexion() {
        //puede seguir trabajando
        alert.closeAlertDialog();
    }
    //descarga las tiendas del servidor, ffin en el indice actual
    public MutableLiveData<Boolean> descargarTiendas(String ciudad, String ffin){
        return lcviewModel.descargarTiendas(ciudad,ffin,MapaCdActivity.this);
    }
    public void iraAbrirInforme(){
     /*   Bundle bundle = new Bundle();
        bundle.putBoolean("nuevatienda", true);
        //  bundle.putString("ciudadNombre", listaSeleccionable.get(i).getNombre());
        NavController nav = NavHostFragment.findNavController(MapaCdFragment.this);
        Log.d(TAG, nav.getCurrentDestination().getId() + "--" + R.id.nav_tiendas);
        if (nav.getCurrentDestination().getId() == R.id.nav_tiendas) {

            nav.navigate(R.id.action_buscartonuevo, bundle);
        }*/

        Intent intent = new Intent(this, NavigationDrawerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("nuevatienda", true);
        bundle.putInt("NAV_DESTINATION", R.id.nav_nuevoinforme); // id del fragment en nav_graph
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_continuar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.csalir:
                finish();
        }
        return true;
    }

    //todo se tendrá que agregar al otro activity
    public void buscarTiendasActuales( int planta){


        markerSel=null;

        //cambio 29/09/25 siempre es 1
        int anios=1;

        //busco el pais y cd de la planta
        int[] aux =lcviewModel.buscarClienCdxPlan(planta, Constantes.INDICEACTUAL);
        cliente=aux[0];
        String ciudad=Constantes.CIUDADTRABAJO;
        int tipo=((CatalogoDetalle)sptipoti.getSelectedItem()).getCad_idopcion();
        int cadena=((CatalogoDetalle)spcadena.getSelectedItem()).getCad_idopcion();
        compraslog.info(TAG, ".buscarTiendasActuales ", "pLANTA: "+planta+"-tipo:"+tipo+"-cadena:"+cadena+"-cliente:"+cliente);
        mMap.clear();
        this.listatiendas=lcviewModel.getTiendasActuales(planta,Constantes.INDICEACTUAL, tipo,cadena);
        this.listageocercas= lcviewModel.getGeocercas(ciudad);
        if(listageocercas!=null&&listageocercas.size()>0) {

            dibujarZonas(listageocercas);

            dibujarPorcentajes(listageocercas);
        }
        String nombreCliente = this.buscarCliente(planta);
        //observo
        this.listatiendas.observe(this, new Observer<List<Tienda>>() {
            @Override
            public void onChanged(List<Tienda> tiendas) {

                //  Log.d(TAG," antes de dibujar"+(new Date()));
                dibujarTiendasActuales(tiendas,nombreCliente);

                //   alert.closeAlertDialog();
                listatiendas.removeObservers(MapaCdActivity.this);
            }
        });


    }

    public void dibujarTiendasActuales(List<Tienda> listiendas,  String nombreCliente){
        martiendas=new ArrayList<>();
        LatLng japon2 = null;

        int estatusPepsi;
        int estatusElectro;
        int estatusPeniafiel;
        int estatusJumex;
        TiendaEstatusClienteDao tiendaEstatusClienteDao=ComprasDataBase.getInstance(this).getTiendaEstatusClienteDao();

        if(listiendas!=null) {
            // Log.d(TAG,"--tiendas"+listiendas.size());
            List<TiendaEstatusCliente> estatusTienda;
            StringBuilder estatusClientes = new StringBuilder();
            String color="3";
            ArrayList<DescripcionGenerica> plantasDisponibles;
            HashMap<Integer,Integer> totalPlantas=lcviewModel.getTotalPlantasxCliente(Constantes.CIUDADTRABAJO);
            for (Tienda tienda : listiendas) {
               // Log.d(TAG, tienda.getUne_id() + "--" + tienda.getUne_descripcion() + "--" + tienda.getEstpep() + "--" + tienda.getEstpen());

                //busco los estatus por planta
                estatusTienda = lcviewModel.buscarEstatusTienda(tienda.getUne_id(), tiendaEstatusClienteDao);
                estatusClientes = new StringBuilder();
                color = "2";
                estatusPepsi = 1;
                estatusPeniafiel = 1;
                estatusJumex = 1;
                estatusElectro = 1;
                //armo lista de plantas de la ciudad
                plantasDisponibles = new ArrayList<>();
                plantasDisponibles.addAll(listaPlantasEnv);
                //  Log.i(TAG,"size antes>>"+estatusTienda.size());
                //veo si tengo estatus rojo

                //busco el cliente de la planta seleecionada

                switch (nombreCliente) {
                    case "PEPSI":
                        if (tienda.getEstpep()!=null&&tienda.getEstpep() == 2) {
                            color = "1";

                        }
                        break;
                    case "PEÑAFIEL":
                        if (tienda.getEstpen()!=null&&tienda.getEstpen() == 2) {
                            color = "1";

                        }
                        break;
                    case "ELECTROPURA":
                        if (tienda.getEstele()!=null&&tienda.getEstele() == 2) {
                            color = "1";

                        }
                        break;
                    case "JUMEX":
                        if (tienda.getEstjum()!=null&&tienda.getEstjum() == 2) {
                            color = "1";

                        }
                        break;
                }
                if (estatusTienda != null)

                    for (TiendaEstatusCliente estatus : estatusTienda
                    ) {
                        if (estatus.getPlantasId() == plantaId) {
                            color = validarColorTienda(estatus.getEstatus());

                        }
                        //para poner en que tiendas no puedo comprar
                        if (estatus.getEstatus() == 2) {
                            if (plantasDisponibles != null)
                                plantasDisponibles = quitarPlanta(plantasDisponibles, estatus.getPlantasId());

                        }


                    }
                else  {
                    color="2";
                }
                if(tienda.getEstpep()!=null&&tienda.getEstpep()==2) {

                }else{
                    //validar estatuscliente
                    estatusPepsi = lcviewModel.getEstatusCliente(tienda.getUne_id(), totalPlantas, 4, tiendaEstatusClienteDao);
                    tienda.setEstpep(estatusPepsi);
                }
                if(tienda.getEstpen()!=null&&tienda.getEstpen()==2) {

                }else{
                    estatusPeniafiel = lcviewModel.getEstatusCliente(tienda.getUne_id(), totalPlantas, 5, tiendaEstatusClienteDao);
                    tienda.setEstpen(estatusPeniafiel);
                }
                if(tienda.getEstele()!=null&&tienda.getEstele()==2) {

                }else{
                    estatusElectro = lcviewModel.getEstatusCliente(tienda.getUne_id(), totalPlantas, 6, tiendaEstatusClienteDao);
                    tienda.setEstele(estatusElectro);
                }
                if(tienda.getEstjum()!=null&&tienda.getEstjum()==2) {
                }else{
                    estatusJumex = lcviewModel.getEstatusCliente(tienda.getUne_id(), totalPlantas, 7, tiendaEstatusClienteDao);
                    tienda.setEstjum(estatusJumex);
                }
                //busco los estatus por cliente

              //  Log.d(TAG,"despues"+tienda.getUne_id()+"--"+tienda.getUne_descripcion()+"--"+tienda.getEstpep()+"--"+tienda.getEstpen()+"--"+tienda.getEstele()+"--"+tienda.getEstjum());

                //  Log.i(TAG,"size>>"+plantasDisponibles.size());
                //el estatus es 1-rojo, 2 amarillo, 3.verde solo en verde puedo comprar o con 0
                int i=0;
                if (!plantasDisponibles.isEmpty()) {
                    for (DescripcionGenerica descripcion:plantasDisponibles
                    ) {
                        estatusClientes.append(", ");
                        estatusClientes.append(descripcion.getNombre());
                        i++;
                    }

                    estatusClientes = new StringBuilder(estatusClientes.substring(2, estatusClientes.length()));
                }
                tienda.setColor(color);

                //latitud es x longitud es y
                //  Log.d(TAG,"--"+tienda.getUne_descripcion()+tienda.getCiudad()+".."+tienda.getUne_descripcion());
                if (tienda.getUne_coordenadasxy() != null && tienda.getUne_coordenadasxy().length() > 0) {
                    String[] aux = tienda.getUne_coordenadasxy().split(",");

                    try {
                        japon2 = new LatLng(Double.parseDouble(aux[0]), Double.parseDouble(aux[1]));
                        MarkerOptions moptions = new MarkerOptions();
                        moptions.position(japon2)
                                .title(tienda.getUne_descripcion())
                                .icon(BitmapDescriptorFactory.defaultMarker(coloresTienda.get(color)));
                     /*   if (estatusClientes.length() > 0) {
                            moptions.snippet(estatusClientes.toString());
                        }*/
                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            @Override
                            public View getInfoWindow(Marker arg0) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {

                                Context context = MapaCdActivity.this    ; //or getActivity(), YourActivity.this, etc.

                                LinearLayout info = new LinearLayout(context);
                                info.setOrientation(LinearLayout.VERTICAL);

                                TextView title = new TextView(context);
                                title.setTextColor(Color.BLACK);
                                title.setGravity(Gravity.CENTER);
                                title.setTypeface(null, Typeface.BOLD);
                                title.setText(marker.getTitle());

                                TextView snippet = new TextView(context);
                                snippet.setTextColor(Color.GRAY);
                                snippet.setTextSize(10);
                                snippet.setText(marker.getSnippet());

                                info.addView(title);
                                info.addView(snippet);

                                return info;
                            }
                        });
                        Marker marker = mMap.addMarker(moptions
                        );
                        marker.setTag(tienda);

                        martiendas.add(marker);
                    } catch (NumberFormatException ex) {
                        Log.e(TAG, "error de formato " + ex.getMessage() + "  " + tienda.getUne_descripcion());
                    }

                }

            }
        }
        if(japon2!=null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(japon2,10));
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 4));
        if(!viendoRecorrido)
            btnvatienda.setEnabled(true);
    }

    public boolean calcularTotales(List<ListaCompraDetalle> detalles)
    {
        int totalPedidos=0;
        int totalcomprados=0;
        for(ListaCompraDetalle detalle:detalles){

            totalcomprados=totalcomprados+detalle.getComprados();
            totalPedidos=totalPedidos+detalle.getCantidad();
        }
        Log.i(TAG,"calcularTotales comprados:"+totalcomprados+"--pedidos:"+totalPedidos);

        if(totalcomprados<totalPedidos)
            return true; //puedo seguir comprando
        return false;


    }
    private BitmapDescriptor crearIconoCuadro(String texto) {
        int tamanoCuadro = 100; // Tamaño en píxeles
        Bitmap bitmap = Bitmap.createBitmap(tamanoCuadro, tamanoCuadro, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 1. Dibujar el fondo del cuadro (Rectángulo)
        Paint paintFondo = new Paint();
        paintFondo.setColor(Color.BLUE); // Puedes cambiar el color
        paintFondo.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, tamanoCuadro, tamanoCuadro, paintFondo);

        // 2. Dibujar el borde del cuadro
        Paint paintBorde = new Paint();
        paintBorde.setColor(Color.WHITE);
        paintBorde.setStyle(Paint.Style.STROKE);
        paintBorde.setStrokeWidth(5);
        canvas.drawRect(0, 0, tamanoCuadro, tamanoCuadro, paintBorde);

        // 3. Dibujar el texto (Número)
        Paint paintTexto = new Paint();
        paintTexto.setColor(Color.WHITE);
        paintTexto.setTextSize(30);
        paintTexto.setTextAlign(Paint.Align.CENTER);
        paintTexto.setFakeBoldText(true);

        // Centrar el texto verticalmente
        float xPos = tamanoCuadro / 2f;
        float yPos = (tamanoCuadro / 2f) - ((paintTexto.descent() + paintTexto.ascent()) / 2f);

        canvas.drawText(texto, xPos, yPos, paintTexto);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    public void dibujarCuadrosEnZonas( List<LatLng> puntos , String textoAMostrar ) {
        // las listas de puntos que usaste en dibujarzonas()

        LatLng centro = obtenerCentroide(puntos);


        mMap.addMarker(new MarkerOptions()
                        .position(centro)
                        .icon(crearIconoCuadro(textoAMostrar))
                        .anchor(0.5f, 0.5f) // Centra el marcador exactamente
                        .flat(true)); // Hace que el cuadro rote con el mapa


    }

    private LatLng obtenerCentroide(List<LatLng> puntos) {
        double latitud = 0;
        double longitud = 0;
        for (LatLng punto : puntos) {
            latitud += punto.latitude;
            longitud += punto.longitude;
        }
        return new LatLng((latitud / puntos.size())+0.005, longitud / puntos.size());
    }

    private void dibujarPorcentajes(List<Geocerca> zonas){
        int banderaPorcentaje=0;
        List<LatLng> puntos;
        //busco las muestras x zonas
        List<MuestrasxZona> muestrasxZona=lcviewModel.getMuestrasxZona(plantaId,Constantes.INDICEACTUAL);

        //busco el porcentaje
        for (Geocerca geocerca:zonas
             ) {

            String[] aux = geocerca.getGeo_p1().split(",");
            LatLng p1 = new LatLng(Double.parseDouble(aux[0]), Double.parseDouble(aux[1]));
            String[] aux2 = geocerca.getGeo_p2().split(",");
            LatLng p2 = new LatLng(Double.parseDouble(aux2[0]), Double.parseDouble(aux2[1]));
            String[] aux3 = geocerca.getGeo_p3().split(",");
            LatLng p3 = new LatLng(Double.parseDouble(aux3[0]), Double.parseDouble(aux3[1]));
            String[] aux4 = geocerca.getGeo_p4().split(",");
            LatLng p4 = new LatLng(Double.parseDouble(aux4[0]), Double.parseDouble(aux4[1]));
            puntos = new ArrayList<>();
            puntos.add(p1);
            puntos.add(p2);
            puntos.add(p3);
            puntos.add(p4);
            banderaPorcentaje = 0;
            if (muestrasxZona != null)
                for (MuestrasxZona zona : muestrasxZona
                ) {
                    if (zona.getZona() == geocerca.getGeo_region()) {
                        dibujarCuadrosEnZonas(puntos, zona.getMuestras() + "%");
                        banderaPorcentaje = 1;
                        break;
                    }

                }
            //puede ser que no hacomprado nada. Pongo 0
            if (banderaPorcentaje == 0)
                dibujarCuadrosEnZonas(puntos, "0%");
        }

    }
    public class miLocationListener implements LocationListener {
        public void desactivar() {
            if ( fusedLocationClient!=null) {
                Log.d(TAG,"desactivando");
                fusedLocationClient.removeUpdates(locallis);
            }
            fusedLocationClient=null;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            guardarPosicion(loc);


        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            //  Toast.makeText(this, "Falta foto de producto exhibido", Toast.LENGTH_SHORT).show();

        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Log.d(TAG, "---------------gps activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }


    }
}