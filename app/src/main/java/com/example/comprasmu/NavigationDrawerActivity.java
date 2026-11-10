
package com.example.comprasmu;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.comprasmu.data.PeticionesServidor;
import com.example.comprasmu.data.modelos.Contrato;
import com.example.comprasmu.data.modelos.InformeEtapa;
import com.example.comprasmu.data.modelos.ListaCompra;
import com.example.comprasmu.data.modelos.ListaCompraDetalle;
import com.example.comprasmu.data.modelos.SolicitudCor;
import com.example.comprasmu.data.modelos.TablaVersiones;
import com.example.comprasmu.data.modelos.Visita;
import com.example.comprasmu.data.remote.IActualListener;
import com.example.comprasmu.data.remote.MuestraCancelada;
import com.example.comprasmu.data.remote.RespInformesResponse;
import com.example.comprasmu.data.remote.SolCorreResponse;
import com.example.comprasmu.data.repositories.InformeComDetRepositoryImpl;
import com.example.comprasmu.data.repositories.InformeCompraRepositoryImpl;
import com.example.comprasmu.data.repositories.SolicitudCorRepoImpl;
import com.example.comprasmu.data.repositories.TablaVersionesRepImpl;
import com.example.comprasmu.data.repositories.VisitaRepositoryImpl;
import com.example.comprasmu.services.DescAutomaticasServiceManager;
import com.example.comprasmu.services.SubirFotoService;
import com.example.comprasmu.ui.home.HomeActivity;
import com.example.comprasmu.ui.infetapa.ContInfEtaViewModel;
import com.example.comprasmu.ui.listadetalle.ListaDetalleViewModel;
import com.example.comprasmu.ui.mantenimiento.LeerLogActivity;
import com.example.comprasmu.ui.notificaciones.NotificacionGen;
import com.example.comprasmu.ui.solcorreccion.ListaSolsViewModel;
import com.example.comprasmu.ui.visita.AbririnformeFragment;
import com.example.comprasmu.utils.ComprasLog;
import com.example.comprasmu.utils.Constantes;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.WorkManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/*esta es la clase principal con el menu***/
public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ETAPA = "comprasmu.ndetapa";
    private AppBarConfiguration mAppBarConfiguration;
    SubirFotoProgressReceiver rcv;
    String TAG="NavigationDrawerActivity";
    private ListaDetalleViewModel mViewModel;
    VisitaRepositoryImpl visRepo;
    InformeComDetRepositoryImpl infdrepo;
    InformeCompraRepositoryImpl infrepo;
   // private static final String DOWNLOAD_PATH = "https://muesmerc.mx/comprasv1/fotografias";
    private   String DESTINATION_PATH ;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private ListaSolsViewModel scViewModel;
    public static final String PROGRESS_UPDATE = "progress_update";
    public static final String PROGRESS_PEND = "progress_pend";
    public static final String NAVINICIAL="nd_navinicial";
    TextView txtcancel,gallery;
    LiveData<Integer> totCorrecciones;
    MutableLiveData<Integer> totMuestraAdic;
    SolicitudCorRepoImpl solRepo;
    TablaVersionesRepImpl tvRepo;
    boolean notificar=false;
    NavigationView navigationView;
    private ComprasLog flog;
    private LiveData<Integer> totCancel;
    private MutableLiveData<Integer> totalNotifGen;
    MutableLiveData<List<NotificacionGen>> listaNotificacionesGen;
    Toolbar toolbar;
    private boolean pausado = false;
    NavController navController;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            flog = ComprasLog.getSingleton();
            flog.crearLog(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath());

            definirTrabajo();

            setContentView(R.layout.activity_navigation_darawer);
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            if (savedInstanceState != null) {    // Restore value of members from saved state
                buscarEtapa();
            }
            //  FloatingActionButton fab = findViewById(R.id.fab);
            //busco el mes actual y le agrego 1

            mViewModel = new ViewModelProvider(this).get(ListaDetalleViewModel.class);
            scViewModel = new ViewModelProvider(this).get(ListaSolsViewModel.class);

            Bundle extras = getIntent().getExtras(); // Aquí es null
            String inicio = "";
            if (Constantes.ETAPAACTUAL == 0) {
                //mando al menu inicial
                Intent homeIntent = new Intent(this, HomeActivity.class);

                startActivity(homeIntent);
                finish();
            }
            if (extras != null) {
                inicio = extras.getString(NAVINICIAL);
                if (Constantes.ETAPAACTUAL == 0)
                    Constantes.ETAPAACTUAL = extras.getInt(ETAPA);
            }

            Log.d(TAG, "pso x aqui" + Constantes.ETAPAACTUAL);
            navigationView = findViewById(R.id.nav_view);
            //cargo el menu lateral de acuerdo a la etapa
            if (Constantes.ETAPAACTUAL == 1) {
                navigationView.getMenu().clear();

                navigationView.inflateMenu(R.menu.activity_main_drawerprep);
                View header = navigationView.getHeaderView(0);
                header.setBackgroundResource(R.drawable.side_nav_barpre);
                TextView mNameTextView = header.findViewById(R.id.txthmmodulo);
                mNameTextView.setText(R.string.preparacion);
            }
            if (Constantes.ETAPAACTUAL == 2) {
                navigationView.getMenu().clear();

                navigationView.inflateMenu(R.menu.activity_main_drawer);
                View header = navigationView.getHeaderView(0);
                header.setBackgroundResource(R.drawable.side_nav_bar);
                TextView mNameTextView = header.findViewById(R.id.txthmmodulo);
                mNameTextView.setText(R.string.compra);
            }
            if (Constantes.ETAPAACTUAL == 3) {
                navigationView.getMenu().clear();

                navigationView.inflateMenu(R.menu.activity_main_draweretiq);
                View header = navigationView.getHeaderView(0);
                header.setBackgroundResource(R.drawable.side_nav_bareti);
                TextView mNameTextView = header.findViewById(R.id.txthmmodulo);
                mNameTextView.setText(R.string.etiquetado);


            }
            if (Constantes.ETAPAACTUAL == 4) {
                navigationView.getMenu().clear();

                navigationView.inflateMenu(R.menu.activity_main_draweremp);
//            navigationView.getMenu().setGroupVisible(R.id.HelpGroup,false);
                //navigationView.getMenu().setGroupVisible(R.id.SetupGroup,false);

                View header = navigationView.getHeaderView(0);
                header.setBackgroundResource(R.drawable.side_nav_baremp);
                TextView mNameTextView = header.findViewById(R.id.txthmmodulo);
                mNameTextView.setTextColor(Color.BLACK);
                mNameTextView.setText(R.string.empaque);
            }
            if (Constantes.ETAPAACTUAL == 5) {
                navigationView.getMenu().clear();

                navigationView.inflateMenu(R.menu.activity_main_drawerenv);
//
                View header = navigationView.getHeaderView(0);
                header.setBackgroundResource(R.drawable.side_nav_barenv);
                TextView mNameTextView = header.findViewById(R.id.txthmmodulo);
                mNameTextView.setTextColor(Color.BLACK);
                mNameTextView.setText(R.string.envio);
            }
            if (Constantes.ETAPAACTUAL == 6) {
                navigationView.getMenu().clear();

                navigationView.inflateMenu(R.menu.activity_main_drawergas);
//
                View header = navigationView.getHeaderView(0);
                header.setBackgroundResource(R.drawable.side_nav_bargas);
                TextView mNameTextView = header.findViewById(R.id.txthmmodulo);
                mNameTextView.setTextColor(Color.BLACK);
                mNameTextView.setText(R.string.gastos);
            }

            //navigationView.setNavigationItemSelectedListener(this);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            drawer = findViewById(R.id.drawer_layout);
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.mobile_navigation)
                    .setDrawerLayout(drawer)
                    .build();
            //inicio el log


            navController = Navigation.findNavController(this, R.id.nav_host_fragment);

            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            //  navigationView.setNavigationItemSelectedListener(this);
            NavInflater navInflater = navController.getNavInflater();
            NavGraph graph = navInflater.inflate(R.navigation.mobile_navigation);

            IntentFilter filter = new IntentFilter();
            filter.addAction(SubirFotoService.ACTION_UPLOAD_IMG);

            rcv = new SubirFotoProgressReceiver();
            registerReceiver(rcv, filter);
            LocalBroadcastManager.getInstance(this).registerReceiver(rcv, filter);
            Log.e(TAG, "wwww" + inicio);
            Log.d(TAG, "***** indice " + Constantes.CLAVEUSUARIO);


            if (Constantes.CLAVEUSUARIO.equals("")) {
                graph.setStartDestination(R.id.nav_configurar);



            } else if (inicio != null && inicio.equals("listainforme")) {

                graph.setStartDestination(R.id.nav_listar);
            }
            if (inicio != null && inicio.equals("continuarinf")) {

                graph.setStartDestination(R.id.nav_listarvisitas);
            }
            if (inicio != null && inicio.equals("nav_reubicetiq")) {

                graph.setStartDestination(R.id.nav_reubicetiq);
            } else {
                //descargasIniciales();
                pedirCorrecciones(0, Constantes.ETAPAACTUAL);
                graph.setStartDestination(R.id.nav_home);
            }
            navController.setGraph(graph);
            MenuItem itemTutoriales =(MenuItem) navigationView.getMenu().findItem(R.id.nav_tutoriales);
          if(itemTutoriales!=null)
              itemTutoriales.setVisible(true);
          //  if(itemTutoriales!=null&&Constantes.CLAVEUSUARIO.equals("50"))
          //      itemTutoriales.setVisible(true);
          //  itemTutoriales.setVisible(true);
         //  Log.e(TAG, "menu " + itemTutoriales.getTitle());
            //pongo canceladas x etapa
            if (Constantes.ETAPAACTUAL == 1) {
                gallery = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                        findItem(R.id.nav_notificaciongen));
            }
            if (Constantes.ETAPAACTUAL == 2) {
                gallery = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                        findItem(R.id.nav_notificaciongen));
            }
            if (Constantes.ETAPAACTUAL == 3) {
                gallery = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                        findItem(R.id.nav_notificaciongen));
            }
            if (Constantes.ETAPAACTUAL == 4) {
                gallery = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                        findItem(R.id.nav_notificaciongen));
            }
            if (Constantes.ETAPAACTUAL == 5) {
                gallery = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                        findItem(R.id.nav_notificaciongen));
            }
            if (Constantes.ETAPAACTUAL == 6) {
                gallery = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                        findItem(R.id.nav_notificaciongen));
            }

            actualizarNotificaciones();
            revisarCiudades();
            if (checkPermission()) {
                //main logic or main code

                // . write your main code to execute, It will execute if the permission is already given.
            } else {
                requestPermission();
            }
          /*  Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build();


        PeriodicWorkRequest simpleRequest =
                new PeriodicWorkRequest.Builder(NotificacionesWork.class, 10, TimeUnit.SECONDS)
                        .setConstraints(constraints)
                        .addTag("comprassync_worker2")
                        .build();
            WorkManager
                    .getInstance(this)
                    .enqueueUniquePeriodicWork("comprassync_worker2", ExistingPeriodicWorkPolicy.KEEP,simpleRequest);
*/
            // Procesa el intent inicial
            navigateIfNeeded(getIntent());
           // DescAutomaticasServiceManager.getInstancia().iniciarServicio(this);
         //elimino todos los procesos que se hayan iniciado primera version
            WorkManager
                    .getInstance(this).cancelAllWorkByTag("comprassync_worker2");

        }catch(Exception ex){

            ex.printStackTrace();
            flog.grabarError(TAG,"oncreate",ex.getMessage());
        }

    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
   //   initializeCountDrawer();
        pausado=false;
        DescAutomaticasServiceManager.getInstancia().iniciarServicio(this);
        return super.onCreateView(parent, name, context, attrs);
    }

    public void actualizarNotificaciones(){
        int INTERVALO = 50*1000; // 10seg
        Handler handler;
        Runnable runnable;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(!pausado) {
                    initializeCountDrawer();
                }
                    handler.postDelayed(this, INTERVALO);

            }
        };
        handler.post(runnable);

    }

    //saber si tiene mas de una ciudad para mostrar seleccionar ciudad
    public void revisarCiudades(){

            Log.d(TAG,"ciudades"+Constantes.CIUDADTRABAJO);

            mViewModel.getCiudades().observe(this, data -> {
               // if(data!=null)
              //      Log.d(TAG,"....regresó de la consulta "+ data.size());
                if(data!=null&&data.size()>1)
                    Constantes.varciudades = true;


                else if(data.size()>0&&data.get(0)!=null) {
                    Log.d(TAG,"ciudades"+data.get(0).getCiudadNombre());
                    //para corregir erro de borrado
                    if(!Constantes.CIUDADTRABAJO.equals("")) //no está vacia y no es la que tengo
                    {
                        if(!Constantes.CIUDADTRABAJO.equals(data.get(0).getCiudadNombre())) {
                            Constantes.CIUDADTRABAJO = "";
                            Constantes.IDCIUDADTRABAJO = 0;
                            guardarCiudadPref("");
                        }
                    }
                    if (Constantes.CIUDADTRABAJO.equals("")){

                        Constantes.CIUDADTRABAJO = data.get(0).getCiudadNombre();
                        Log.d(TAG,"ciudades"+Constantes.CIUDADTRABAJO);

                        Constantes.IDCIUDADTRABAJO = data.get(0).getCiudadesId();
                         //guardo en pref

                         guardarCiudadPref(Constantes.CIUDADTRABAJO);
                }
                    Constantes.varciudades = false;
                 //   if(Constantes.ETAPAACTUAL==4||Constantes.ETAPAACTUAL==3)
                  //      return; //ya no importa la ciudad
                    NavigationView navigationView =  findViewById(R.id.nav_view);
                    navigationView.getMenu().findItem(R.id.nav_ciudad_trabajo).setVisible(false);

                }


            });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_darawer, menu);
        return true;
    }

    /*soporte para la bnavegacion hacia arriba*/
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController;
        switch (item.getItemId()) {
            case R.id.action_settings: //configuracion

                navController = Navigation.findNavController(this, R.id.nav_host_fragment);
                navController.navigate(R.id.nav_configurar);
                return true;


            case R.id.action_mapa: //enviar errores
                Intent homeIntent=new Intent(this, LeerLogActivity.class);
                startActivity(homeIntent);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void borrarUsuario(){
        SharedPreferences prefe=getSharedPreferences("comprasmu.datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefe.edit();

        editor.putString("usuario", "");
        editor.putString("password", "");
        editor.commit();

    }

    public void cerrarSesion(){

        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
       // finishAndRemoveTask();
    }
    private boolean checkPermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  //  Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    public static Boolean isOnlineNet(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "pausado");
        DescAutomaticasServiceManager.getInstancia().detenerServicio();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(rcv);
        pausado=true;
    }
    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(rcv);
        Log.i(TAG," detuve");
        WorkManager
                .getInstance(this).cancelAllWorkByTag("comprassync_worker2");
        DescAutomaticasServiceManager.getInstancia().detenerServicio();
        pausado=true;
    }
//esta funcion no funciona nunca se llama
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }





    public class SubirFotoProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SubirFotoService.ACTION_UPLOAD_IMG)) {
                int prog = intent.getIntExtra("uploadComplete", 0);

            }

        }
    }



    private void contarCorrecc(){
      /***mod marzo-24 ya no importa la etapa trae todas las solicitudes****/
        totCorrecciones=scViewModel.getTotalSols(Constantes.INDICEACTUAL,1);

        //la solicitudes de coreecciones ya no serán por cd sino x indice y etapa
        Log.d(TAG,"contarCorrecc"+totCorrecciones+"--"+Constantes.ETAPAACTUAL+","+Constantes.INDICEACTUAL);

    }



    private void initializeCountDrawer(){

        totCancel=new MutableLiveData<>();
        totMuestraAdic=new MutableLiveData<>();
        contarCorrecc();
        notificacionesGenerales();
        if(gallery!=null) {
            gallery.setGravity(Gravity.CENTER_VERTICAL);
            gallery.setTypeface(null, Typeface.BOLD);
            gallery.setTextColor(Color.RED);
            //  gallery.setTextSize(15);
            int totalnotif = 0;

        }

        totCorrecciones.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer totcor) {
                scViewModel.contarCanceladas();
                totCancel=scViewModel.getTotCancel();
                totCancel.observe(NavigationDrawerActivity.this, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer totcan) {
                         // Log.i(TAG,"regresaron canceladas");
                          totMuestraAdic=scViewModel.contarMuestraAdic();
                          totalNotifGen.observe(NavigationDrawerActivity.this, new Observer<Integer>() {
                          @Override
                          public void onChanged(Integer valor) {
                              Log.i(TAG,"regresaron generales");
                                      //  revRecibo.removeObservers(NavigationDrawerActivity.this);

                                    totMuestraAdic.observe(NavigationDrawerActivity.this, new Observer<Integer>() {
                                        @Override
                                        public void onChanged(Integer totma) {
                                            int totalnotif = totcor + totcan + totma +valor;
                                            Log.i(TAG,"poniendo total notificaciones");
                                            gallery.setText(totalnotif + "");
                                            totMuestraAdic.removeObservers(NavigationDrawerActivity.this);
                                        }
                                    });
                              totalNotifGen.removeObservers(NavigationDrawerActivity.this);
                          }
                          });
                          totCancel.removeObservers(NavigationDrawerActivity.this);
                    }


                });
                totCorrecciones.removeObservers(NavigationDrawerActivity.this);



            }

        });

    }


    @Override
    public void onBackPressed() {
     //   Log.e(TAG,"aprete atras");
        try {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (fragment instanceof AbririnformeFragment){
          //     Log.e(TAG,"aprete atras***");
                ((AbririnformeFragment)fragment).saliendoSinguardar();
            }else
                 super.onBackPressed();

        }catch(Exception ex){
            super.onBackPressed();
        }


    }
    private void definirTrabajo() {
        SharedPreferences prefe = getSharedPreferences("comprasmu.datos", Context.MODE_PRIVATE);
        Constantes.CIUDADTRABAJO = prefe.getString("ciudadtrabajo", "");
        Constantes.IDCIUDADTRABAJO=prefe.getInt("idciudadtrabajo",0);
        Constantes.CLAVEUSUARIO = prefe.getString("claveusuario", "");
        Constantes.INDICEACTUAL=prefe.getString("indiceact", "");
        Constantes.TIPOTIENDA=new HashMap<>();
        Constantes.TIPOTIENDA.put(0,getString(R.string.seleccione_opcion));
        Constantes.TIPOTIENDA.put(1,getString(R.string.grande));
        Constantes.TIPOTIENDA.put(2,getString(R.string.mediana));
        Constantes.TIPOTIENDA.put(3,getString(R.string.chica));
        Constantes.TIPOTIENDA.put(4,getString(R.string.otras));

        //  prefe.getString("ciudadtrabajo","");
    /*    Constantes.PAISTRABAJO=     prefe.getString("paistrabajo","");*/

    }

    private void buscarEtapa() {
        SharedPreferences prefe = getSharedPreferences("comprasmu.datos", Context.MODE_PRIVATE);
           Constantes.ETAPAACTUAL = prefe.getInt("etapaactual", 0); //no es la misma que estapaact que viene del servidor

    }
    public void guardarEtapaPref(int etapa){
        SharedPreferences prefe=getSharedPreferences("comprasmu.datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefe.edit();

        editor.putInt("etapaactual",etapa );

        editor.commit();

    }

    public void guardarCiudadPref(String cd){
        SharedPreferences prefe=getSharedPreferences("comprasmu.datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefe.edit();

        editor.putString("ciudadtrabajo",cd );
        editor.putInt("idciudadtrabajo",Constantes.IDCIUDADTRABAJO );//no es del catalogo ciudades es de plantas


        editor.commit();

    }
    public void pedirCorrecciones(int actualiza, int etapa) {
        tvRepo = new TablaVersionesRepImpl(this);
        solRepo = new SolicitudCorRepoImpl(this);
        PeticionesServidor ps = new PeticionesServidor(Constantes.CLAVEUSUARIO);
        TablaVersiones comp = tvRepo.getVersionByNombreTablasmd(Contrato.TBLSOLCORRECCIONES, Constantes.INDICEACTUAL);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
        if (NavigationDrawerActivity.isOnlineNet(this))
            ps.pedirSolicitudesCorr(Constantes.INDICEACTUAL, etapa, version, new ActualListener());
        else
            notificar = true;


    }
    public void reInflateMenu(){

        initializeCountDrawer();
    }
    public void setTitulo(String titulo){
        toolbar.setTitle(titulo);
    }

    public class ActualListener implements IActualListener {

        public int actualizarCorre(SolCorreResponse corrResp, int etapa) {

            //primero los inserts
            if (corrResp != null) {

                if (corrResp != null && corrResp.getInserts() != null) {
                    for (SolicitudCor sol : corrResp.getInserts()) {
                        //veo si ya existe
                       // Log.d(TAG,"solcorreccion"+sol.getId()+"--"+ sol.getNumFoto());
                        SolicitudCor solt = solRepo.findsimple(sol.getId(), sol.getNumFoto());
                        if (solt != null) {
                            if (solt.getEstatus() !=4||solt.getEstatus()!=5) {
                                //actualizo
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
                Log.d(TAG,"dddddd"+corrResp.getCanceladas().size());
                //veo las muestras canceladas
                if (corrResp.getCanceladas() != null)

                        for (MuestraCancelada cancel :
                                corrResp.getCanceladas()) {
                            if(cancel.getIne_etapa()>0&&cancel.getIne_etapa()!=2) {
                                //busco el informedetalle y actualizo el estatus

                                scViewModel.procesarCanceladasEta(cancel);
                                if(cancel.getIne_etapa()==3) //tambien cancelo los detalles
                                    scViewModel.procesarCanceladasEtaDet(cancel);
                            }else {
                                scViewModel.procesarCanceladas(cancel);
                               //canceladas será 0

                            }
                        }
                //muestras eliminada son las canceladas que no se reponen
                if (corrResp.getEliminadas() != null)
                    for (MuestraCancelada cancel :
                            corrResp.getEliminadas()) {
                       {
                            scViewModel.procesarEliminadas(cancel);
                            //canceladas será 0
                        }
                    }

            }
            return 1;

        }

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG," mori");
        WorkManager.getInstance(this).cancelAllWorkByTag("comprassync_worker2");
        DescAutomaticasServiceManager.getInstancia().detenerServicio();
        pausado=true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    //guardo la etapa seleccionada
        guardarEtapaPref(Constantes.ETAPAACTUAL);


    }
      @Override
    protected void onRestart() {
          super.onRestart();

          Log.d(TAG, "on restart");
          DescAutomaticasServiceManager.getInstancia().iniciarServicio(this);
          pausado=false;
          initializeCountDrawer();
      }


    public void subirImagenes(){
        NavController navController;
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navController.navigate(R.id.nav_borrarind);
    }
    private void notificacionesGenerales() {
        totalNotifGen=new MutableLiveData<>();

        listaNotificacionesGen= scViewModel.pedirNotificacionesGenerales(Constantes.INDICEACTUAL);
        listaNotificacionesGen.observe(this, new Observer<List<NotificacionGen>>() {
            @Override
            public void onChanged(List<NotificacionGen> notificacionGens) {

                    convertirListaNotif(notificacionGens);
            }
        });
    }

    //la lista de notificaciones la cuento
    private  void convertirListaNotif(List<NotificacionGen> lista) {
        int totalnotif=0;
        Log.i(TAG,"convirtiendo nofitificaciones generales");
        if(lista!=null)
         for (NotificacionGen noti:
                lista) {
           totalnotif+=noti.getTotal();

           if(noti.getTipo()==4&&noti.getTotal()>0){    //4-revisar recibo
                 //5-ajustar recibo
                 //6-estatus envio
                 //busco el informe
                 List<InformeEtapa> listaInformes=mViewModel.getInfGasxCiudad(Constantes.INDICEACTUAL, noti.getCiudad());
                 if(listaInformes!=null&&listaInformes.size()>0){
                     for (InformeEtapa info:listaInformes
                          ) {

                         mViewModel.actualizarEstatusGas(info.getId(),7);
                         flog.grabarError(TAG, "convertirListaNotif", "actualizando informe gastos ajuste" + info.getId());
                     }
                 }
             }
           //modifico el estatus del informe
             if(noti.getTipo()==5&&noti.getTotal()>0){    //4-revisar recibo
                      //5-ajustar recibo
                     //6-estatus envio
                //busco el informe finalizado con estatus 2
                 List<InformeEtapa> listaInf=mViewModel.getInfGastoxCiudad(Constantes.INDICEACTUAL, noti.getCiudad());
                if(listaInf!=null&&listaInf.size()>0){
                    mViewModel.actualizarEstatusGas(listaInf.get(0).getId(),5);
                    flog.grabarError(TAG,"convertirListaNotif","actualizando informe gastos ajuste"+listaInf.get(0).getId());
                }
             }


        }
        totalNotifGen.setValue(totalnotif);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        navigateIfNeeded(intent);
    }

    private void navigateIfNeeded(Intent intent) {
        if (intent != null && intent.hasExtra("NAV_DESTINATION")) {
            int destinationId = intent.getIntExtra("NAV_DESTINATION", 0);
            Bundle args =   getIntent().getExtras();


            if (destinationId != 0) {
                // Evita navegar si ya estás en ese destino
                if (navController.getCurrentDestination() != null
                        && navController.getCurrentDestination().getId() != destinationId) {
                    Log.d(TAG,"abriendo fragment"+ args.toString());
                    // Limpia el backstack y navega
                    navController.popBackStack(R.id.home, false);
                    navController.navigate(destinationId,args);
                }

                // Cierra el drawer por si acaso
                drawer.closeDrawer(GravityCompat.START);
            }
        }
    }


}