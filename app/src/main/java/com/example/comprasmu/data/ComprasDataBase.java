package com.example.comprasmu.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.comprasmu.R;
import com.example.comprasmu.data.dao.AcuseReciboDao;
import com.example.comprasmu.data.dao.AtributoDao;
import com.example.comprasmu.data.dao.CatalogoDetalleDao;
import com.example.comprasmu.data.dao.ConfiguracionDao;
import com.example.comprasmu.data.dao.CorEtiquetadoCajaDao;
import com.example.comprasmu.data.dao.CorEtiquetadoCajaDetDao;
import com.example.comprasmu.data.dao.CorreccionDao;
import com.example.comprasmu.data.dao.DetalleCajaDao;
import com.example.comprasmu.data.dao.GeocercaDao;
import com.example.comprasmu.data.dao.HistoricoMuestrasDao;
import com.example.comprasmu.data.dao.ImagenDetalleDao;
import com.example.comprasmu.data.dao.InformeCompraDao;
import com.example.comprasmu.data.dao.InformeCompraDetDao;
import com.example.comprasmu.data.dao.InformeEnvioDetDao;
import com.example.comprasmu.data.dao.InformeEtapaDao;
import com.example.comprasmu.data.dao.InformeEtapaDetDao;
import com.example.comprasmu.data.dao.InformeGastoDetDao;
import com.example.comprasmu.data.dao.InformeTempDao;
import com.example.comprasmu.data.dao.ListaCompraDao;
import com.example.comprasmu.data.dao.ListaCompraDetalleDao;
import com.example.comprasmu.data.dao.ProductoExhibidoDao;
import com.example.comprasmu.data.dao.ReactivoDao;
import com.example.comprasmu.data.dao.SiglaDao;
import com.example.comprasmu.data.dao.SolicitudCorDao;
import com.example.comprasmu.data.dao.SustitucionDao;
import com.example.comprasmu.data.dao.TablaVersionesDao;
import com.example.comprasmu.data.dao.TiendaDao;
import com.example.comprasmu.data.dao.TiendaEstatusClienteDao;
import com.example.comprasmu.data.dao.VisitaDao;
import com.example.comprasmu.data.modelos.AcuseRecibo;
import com.example.comprasmu.data.modelos.Atributo;
import com.example.comprasmu.data.modelos.CatalogoDetalle;
import com.example.comprasmu.data.modelos.Configuracion;
import com.example.comprasmu.data.modelos.Contrato;
import com.example.comprasmu.data.modelos.CorEtiquetadoCaja;
import com.example.comprasmu.data.modelos.CorEtiquetadoCajaDet;
import com.example.comprasmu.data.modelos.Correccion;
import com.example.comprasmu.data.modelos.DetalleCaja;
import com.example.comprasmu.data.modelos.Geocerca;
import com.example.comprasmu.data.modelos.HistoricoMuestras;
import com.example.comprasmu.data.modelos.ImagenDetalle;
import com.example.comprasmu.data.modelos.InformeCompra;
import com.example.comprasmu.data.modelos.InformeCompraDetalle;
import com.example.comprasmu.data.modelos.InformeEnvioDet;
import com.example.comprasmu.data.modelos.InformeEtapa;
import com.example.comprasmu.data.modelos.InformeEtapaDet;
import com.example.comprasmu.data.modelos.InformeGastoDet;
import com.example.comprasmu.data.modelos.InformeTemp;
import com.example.comprasmu.data.modelos.ListaCompra;
import com.example.comprasmu.data.modelos.ListaCompraDetalle;
import com.example.comprasmu.data.modelos.ProductoExhibido;
import com.example.comprasmu.data.modelos.Reactivo;
import com.example.comprasmu.data.modelos.Sigla;
import com.example.comprasmu.data.modelos.SolicitudCor;
import com.example.comprasmu.data.modelos.Sustitucion;
import com.example.comprasmu.data.modelos.TablaVersiones;
import com.example.comprasmu.data.modelos.Tienda;
import com.example.comprasmu.data.modelos.TiendaEstatusCliente;
import com.example.comprasmu.data.modelos.Visita;
import com.example.comprasmu.utils.CreadorFormulario;
import java.util.ArrayList;
import java.util.List;

@Database(entities={ImagenDetalle.class,
        InformeCompra.class,
        InformeCompraDetalle.class,
        ListaCompra.class,
        ListaCompraDetalle.class,
        Reactivo.class,
        TablaVersiones.class,
        Visita.class,  InformeTemp.class,
        ProductoExhibido.class, Sustitucion.class,
        CatalogoDetalle.class, Atributo.class, Geocerca.class,
        InformeEtapa.class, InformeEtapaDet.class, DetalleCaja.class,
        SolicitudCor.class, Correccion.class, Sigla.class,
        Configuracion.class, CorEtiquetadoCaja.class, CorEtiquetadoCajaDet.class, InformeEnvioDet.class, InformeGastoDet.class, AcuseRecibo.class,
        Tienda.class,TiendaEstatusCliente.class,HistoricoMuestras.class},
        views = {InformeCompraDao.InformeCompravisita.class, ProductoExhibidoDao.ProductoExhibidoFoto.class}, version=39, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ComprasDataBase extends RoomDatabase {
    private static ComprasDataBase INSTANCE;
    static Context ctx;
    public abstract ListaCompraDetalleDao getListaCompraDetalleDao();
    public abstract ListaCompraDao getListaCompraDao();
    public abstract TablaVersionesDao getTableVersionesDao();
    public abstract ImagenDetalleDao getImagenDetalleDao();
    public abstract VisitaDao getVisitaDao();
    public abstract InformeCompraDetDao getInformeCompraDetDao();
    public abstract ReactivoDao getReactivoDao();
    public abstract CatalogoDetalleDao getCatalogoDao();
    public abstract ProductoExhibidoDao getProductoExhibidoDao();
    public abstract AtributoDao getAtributoDao();
    public abstract InformeCompraDao getInformeCompraDao();
    public abstract InformeTempDao getInformeTempDao();
    public abstract SustitucionDao getSustitucionDao();
    public abstract GeocercaDao getGeocercaDao();
    public abstract InformeEtapaDetDao getInformeEtapaDetDao();
    public abstract InformeEtapaDao getInformeEtapaDao();
    public abstract SolicitudCorDao getSolicitudCorDao();
    public abstract CorreccionDao getCorreccionDao();
    public abstract DetalleCajaDao getDetalleCajaDao();
    public abstract SiglaDao getSiglaDao();
    public abstract ConfiguracionDao getConfiguracionDao();
    public abstract CorEtiquetadoCajaDao getCorEtiquetadoCajaDao();
    public abstract CorEtiquetadoCajaDetDao getCorEtiquetadoCajaDetDao();
    public abstract InformeEnvioDetDao getInformeEnvioDetDao();
    public abstract InformeGastoDetDao getInformeGastoDetDao();
    public abstract AcuseReciboDao getAcuseReciboDao();
    public abstract TiendaDao getTiendaDao();
    public abstract TiendaEstatusClienteDao getTiendaEstatusClienteDao();
    public abstract HistoricoMuestrasDao getHistoricoMuestrasDao();
    public static ComprasDataBase getInstance(final Context context) {
        if (INSTANCE == null) {
            ctx=context;
            synchronized (ComprasDataBase.class) {
                if (INSTANCE == null) {

                    INSTANCE =  Room.databaseBuilder(context,
                            ComprasDataBase.class, "compras_data").allowMainThreadQueries()
                            .addMigrations(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4,MIGRATION_4_5, MIGRATION_5_6,MIGRATION_6_7,MIGRATION_7_8,
                                    MIGRATION_8_9,MIGRATION_9_10,MIGRATION_10_11,MIGRATION_11_12,MIGRATION_12_13,MIGRATION_13_14,MIGRATION_14_15
                                    ,MIGRATION_15_16,MIGRATION_16_17, MIGRATION_17_18,MIGRATION_18_19,MIGRATION_19_20, MIGRATION_20_21, MIGRATION_21_22,
                                    MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25,
                                    MIGRATION_25_26, MIGRATION_26_27, MIGRATION_27_28,MIGRATION_28_29,MIGRATION_29_30,MIGRATION_30_31,MIGRATION_31_32,
                                    MIGRATION_32_33, MIGRATION_33_34, MIGRATION_34_35, MIGRATION_35_36, MIGRATION_36_37, MIGRATION_37_38,
                                    MIGRATION_38_39)
                            .build();
                    INSTANCE.cargandodatos();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `sustitucion` (`id_sustitucion` INTEGER NOT NULL, su_tipoempaque INTEGER NOT NULL, "
                    + "`nomempaque` TEXT, su_producto INTEGER NOT NULL, su_tamanio INTEGER NOT NULL, nomproducto TEXT, nomtamanio TEXT," +
                    "categoriasId INTEGER NOT NULL,nomcategoria TEXT,PRIMARY KEY(`id_sustitucion`));");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE informe_detalle ADD COLUMN comprasIdbu INTEGER; " );
            database.execSQL( "ALTER TABLE informe_detalle ADD COLUMN comprasDetIdbu INTEGER;");

        }
    };
    static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE tabla_versiones ADD COLUMN indice TEXT; " );

        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4,5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE sustitucion ADD COLUMN clientesId INTEGER DEFAULT 0 NOT NULL; " );

            database.execSQL(
                    "ALTER TABLE lista_compras_detalle ADD COLUMN lid_orden INTEGER DEFAULT 0 NOT NULL; " );

        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5,6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "DELETE FROM informe_temp; " );

            database.execSQL(
                    "DELETE FROM lista_compras_detalle " );
            database.execSQL(
                    "DELETE FROM lista_compras " );

            database.execSQL(
                    "ALTER TABLE informe_temp ADD COLUMN clienteSel INTEGER DEFAULT 0 NOT NULL; " );
            database.execSQL(
                    "ALTER TABLE reactivos  ADD COLUMN clienteSel INTEGER DEFAULT 0 NOT NULL; " );


            database.execSQL(
                    "ALTER TABLE lista_compras_detalle ADD COLUMN lid_backup INTEGER DEFAULT -1 NOT NULL; " );


        }
    };
    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `geocerca` (`geo_id` INTEGER NOT NULL," +
                    " geo_n4id INTEGER NOT NULL, "
                    + "`geo_region` INTEGER NOT NULL ,  geo_p1 TEXT,geo_p2 TEXT, geo_p3 TEXT, geo_p4 TEXT, ciudad TEXT," +
                    " PRIMARY KEY(`geo_id`));");

             }
    };

    static final Migration MIGRATION_7_8 = new Migration(7,8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
              database.execSQL("ALTER TABLE lista_compras_detalle ADD COLUMN ordtam INTEGER DEFAULT 0 NOT NULL  ");
            database.execSQL("ALTER TABLE lista_compras_detalle ADD COLUMN ordemp INTEGER DEFAULT 0  NOT NULL");

            database.execSQL("ALTER TABLE lista_compras_detalle ADD COLUMN ordtipa INTEGER DEFAULT 0 NOT NULL ");
            database.execSQL("ALTER TABLE lista_compras_detalle ADD COLUMN ordtipm INTEGER DEFAULT 0 NOT NULL ");
            database.execSQL("ALTER TABLE visita ADD COLUMN estatusPepsi INTEGER DEFAULT 0 NOT NULL ");

        }
    };

    static final Migration MIGRATION_8_9 = new Migration(8,9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE informe_detalle ADD COLUMN siglas TEXT  ");

        }
    };

    static final Migration MIGRATION_9_10 = new Migration(9,10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP VIEW ProductoExhibidoFoto");
            database.execSQL("CREATE VIEW `ProductoExhibidoFoto` AS SELECT producto_exhibido.id as idprodex, producto_exhibido.visitasId, producto_exhibido.imagenId, producto_exhibido.clienteId, producto_exhibido.nombreCliente, producto_exhibido.estatusSync, ruta FROM producto_exhibido LEFT JOIN imagen_detalle ON producto_exhibido.imagenId = imagen_detalle.id");

        }
    };
    static final Migration MIGRATION_10_11 = new Migration(10,11) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE lista_compras_detalle ADD COLUMN lid_numtienbak INTEGER default 0 not null");

        }
    };
    static final Migration MIGRATION_11_12 = new Migration(11,12) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE correccion ADD COLUMN numfoto INTEGER not null default 0");
            database.execSQL("ALTER TABLE correccion ADD COLUMN indice TEXT ");

            database.execSQL("ALTER TABLE informe_detalle ADD COLUMN fechaCancel INTEGER");
            database.execSQL(" UPDATE informe_detalle SET fechaCancel = CURRENT_TIMESTAMP");
            database.execSQL("ALTER TABLE informe_detalle ADD COLUMN motivoCancel TEXT");
            database.execSQL("drop TABLE if exists informe_etapa ");
            database.execSQL("create  TABLE informe_etapa ( id integer not null," +
                    "consecutivo integer not null, plantasId integer not null, " +
                    "plantaNombre TEXT,clientesId INTEGER not null," +
                    " clienteNombre TEXT,    indice TEXT," +
                    "    comentarios TEXT," +
                    "     etapa INTEGER not null," +
                    "   total_cajas INTEGER not null," +
                    "    total_muestras INTEGER not null," +
                    "    estatus INTEGER not null," +
                    "    estatusSync INTEGER not null,createdAt INTEGER DEFAULT CURRENT_TIMESTAMP,  PRIMARY KEY(ID)) ");
            database.execSQL("drop TABLE if exists detalle_caja ");
            database.execSQL("create  TABLE detalle_caja ( id integer not null," +
             "informeEtapaId INTEGER not null,"+
            "num_caja INTEGER not null,"+
             "alto TEXT,"+
            "ancho TEXT,"+
             "largo TEXT,"+
           " peso TEXT,"+
                    "    estatusSync INTEGER not null,  PRIMARY KEY(id)) ");


            database.execSQL("drop TABLE if exists solicitud_cor ");
            database.execSQL("create  TABLE solicitud_cor ( id integer not null, " +
                            " informesId INTEGER not null," +
                            "   plantasId INTEGER not null," +
                            " plantaNombre TEXT," +
                            "   clientesId INTEGER not null," +
                            "    clienteNombre TEXT," +
                            "   indice TEXt," +
                            "  nombreTienda TEXT," +
                            "  descripcionFoto TEXT," +
                            "    descripcionId INTEGER not null," +
                            " descMostrar TEXT," +
          "numFoto INTEGER not null," +
                    "numFoto2 INTEGER not null," +
            "numfoto3 INTEGER not null," +

            "motivo TEXT," +
            "total_fotos INTEGER not null," +
             "etapa INTEGER not null," +
            "estatus INTEGER not null," +
            "estatusSync INTEGER not null," +
           " contador INTEGER not null," +
                    "createdAt INTEGER DEFAULT CURRENT_TIMESTAMP,  PRIMARY KEY(ID)) ");
            database.execSQL("drop TABLE if exists detalle_caja ");
            database.execSQL("create  TABLE detalle_caja ( id integer not null," +
                    "informeEtapaId INTEGER not null,"+
                    "num_caja INTEGER not null,"+
                    "alto TEXT,"+
                    "ancho TEXT,"+
                    "largo TEXT,"+
                    " peso TEXT,"+
                    "    estatusSync INTEGER not null,  PRIMARY KEY(id)) ");
        }
    };

    static final Migration MIGRATION_12_13 = new Migration(12,13) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE visitas ADD COLUMN consecutivocd INTEGER default 0 not null");

        }
    };

    static final Migration MIGRATION_13_14 = new Migration(13,14) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("drop TABLE if exists solicitud_cor ");
            database.execSQL("create  TABLE solicitud_cor ( id integer not null, " +
                    "informesId INTEGER not null," +
                    "plantasId INTEGER not null," +
                    "plantaNombre TEXT," +
                    "clientesId INTEGER not null," +
                    "clienteNombre TEXT," +
                    "indice TEXt," +
                    "nombreTienda TEXT," +
                    "descripcionFoto TEXT," +
                    "descripcionId INTEGER not null," +
                    "descMostrar TEXT," +
                    "numFoto INTEGER not null," +
                    "numFoto2 INTEGER not null," +
                    "numfoto3 INTEGER not null," +

                    "motivo TEXT," +
                    "total_fotos INTEGER not null," +
                    "etapa INTEGER not null," +
                    "estatus INTEGER not null," +
                    "estatusSync INTEGER not null," +
                    "contador INTEGER not null," +
                    "createdAt INTEGER DEFAULT CURRENT_TIMESTAMP,  PRIMARY KEY(id,numFoto )) ");

        }
    };
    static final Migration MIGRATION_14_15 = new Migration(14,15) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE solicitud_cor ADD COLUMN val_vis_id INTEGER default 0 not null");

        }
    };

    static final Migration MIGRATION_15_16 = new Migration(15,16) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE informe_etapa ADD COLUMN fechaCancel INTEGER");
          //  database.execSQL(" UPDATE informe_det SET fechaCancel = CURRENT_TIMESTAMP");
            database.execSQL("ALTER TABLE informe_etapa ADD COLUMN motivoCancel TEXT");
        }
    };
    static final Migration MIGRATION_16_17 = new Migration(16,17) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE informe_detalle ADD COLUMN atributod INTEGER; " );

        }
    };
    static final Migration MIGRATION_17_18 = new Migration(17,18) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("create  TABLE IF NOT EXISTS siglas ( id integer not null, " +
                    "siglas TEXT," +
                    "planta TEXT," +
                    "plantasId INTEGER not null," +
                    "clientesId INTEGER not null," +
                    "PRIMARY KEY(id )) ");

        }
    };

    static final Migration MIGRATION_18_19 = new Migration(18,19) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE informe_etapa ADD COLUMN ciudadesId INTEGER");
            //  database.execSQL(" UPDATE informe_det SET fechaCancel = CURRENT_TIMESTAMP");
            database.execSQL("ALTER TABLE informe_etapa ADD COLUMN ciudadNombre TEXT");
        }
    };

    static final Migration MIGRATION_19_20 = new Migration(19,20) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE correccion ADD COLUMN dato1 TEXT");
            database.execSQL(" ALTER TABLE correccion ADD COLUMN dato2 TEXT");
            database.execSQL("ALTER TABLE correccion ADD COLUMN dato3 TEXT");
        }
    };

    static final Migration MIGRATION_20_21 = new Migration(20,21) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `configuracion` (`id` INTEGER NOT NULL," +
                    "clave TEXT , "
                    +"valor TEXT, "+
                    " PRIMARY KEY(`id`));");

        }
    };

    static final Migration MIGRATION_21_22 = new Migration(21,22) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE sustitucion ADD COLUMN plantasId INTEGER DEFAULT 0 NOT NULL; " );


        }
    };
    static final Migration MIGRATION_22_23 = new Migration(22,23) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("drop TABLE if exists sustitucion ");
            database.execSQL("CREATE TABLE IF NOT EXISTS `sustitucion` (`id_sustitucion` INTEGER NOT NULL, " +
                    "clientesId INTEGER NOT NULL, "+
                    "su_tipoempaque INTEGER NOT NULL, "
                    + "`nomempaque` TEXT," +
                    "su_producto INTEGER NOT NULL, " +
                    "su_tamanio INTEGER NOT NULL," +
                    "nomproducto TEXT, " +
                    "nomtamanio TEXT," +
                    "categoriasId INTEGER NOT NULL," +
                    "nomcategoria TEXT, " +

                    "plantasId INTEGER NOT NULL, "+
                    "PRIMARY KEY(`id_sustitucion`, plantasId));");


        }
    };

    static final Migration MIGRATION_23_24 = new Migration(23,24) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("create  TABLE coretiquetado_caja ( id INTEGER not null," +
                    "indice TEXT," +
                    "solicitudId INTEGER not null," +
                    " numfoto INTEGER,  " +

                    "  estatus INTEGER not null," +
                    " estatusSync INTEGER not null," +
                    "createdAt INTEGER DEFAULT CURRENT_TIMESTAMP,  PRIMARY KEY(id)) ");

            database.execSQL("CREATE TABLE IF NOT EXISTS `coretiquetado_cajadet` (`id` INTEGER NOT NULL, " +
                    "coretiquetadocId INTEGER NOT NULL, "+
                    "numfotoant TEXT, "
                    + "ruta_fotonva TEXT," +
                    " descripcionId INTEGER NOT NULL, " +
                     "descripcion TEXT," +
                    "numcaja INTEGER NOT NULL," +
                    " estatus INTEGER not null," +
                    "  estatusSync INTEGER not null," +
                    "PRIMARY KEY(`id`));");


        }
    };


    static final Migration MIGRATION_24_25 = new Migration(24,25) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE informe_detalle ADD COLUMN causaSustId INTEGER; " );
            database.execSQL(
                    "ALTER TABLE informe_detalle ADD COLUMN causaSustitucion TEXT; " );

        }
    };
    static final Migration MIGRATION_25_26 = new Migration(25,26) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE informe_etapa_det ADD COLUMN estatus INTEGER");
            database.execSQL("ALTER TABLE detalle_caja ADD COLUMN estatus INTEGER");

        }
    };
    static final Migration MIGRATION_26_27 = new Migration(26,27) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
              database.execSQL("create  TABLE informe_envio_det ( informeEtapaId integer not null," +
                    "fotoSello INTEGER , " +
                      "nombreRecibe TEXT," +
                      "fechaEntrega INTEGER DEFAULT CURRENT_TIMESTAMP,"+
                    " estatus INTEGER," +
                    "    estatusSync INTEGER,  PRIMARY KEY(informeEtapaId)) ");

        }
    };
    static final Migration MIGRATION_27_28 = new Migration(27,28) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("drop TABLE if exists informe_envio_det ");
            database.execSQL("create  TABLE informe_envio_det ( informeEtapaId integer not null," +
                    "fotoSello INTEGER , " +
                    "nombreRecibe TEXT," +
                    "fechaEnvio INTEGER DEFAULT CURRENT_TIMESTAMP,"+
                    " estatus INTEGER," +
                    "    estatusSync INTEGER,  PRIMARY KEY(informeEtapaId)) ");

        }
    };
    static final Migration MIGRATION_28_29 = new Migration(28,29) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE informe_detalle ADD COLUMN  foto_atributod INTEGER; " );
            database.execSQL("ALTER TABLE correccion ADD COLUMN ruta_foto4 TEXT");

        }
    };

    static final Migration MIGRATION_29_30 = new Migration(29,30) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("create  TABLE informe_gasto_det ( id INTEGER not null, " +
                    " informeEtapaId INTEGER not null," +
                    "   conceptoId INTEGER not null," +
                    " concepto TEXT ," +
                    "   importe REAL not null," +
                    "    descripcion TEXT ," +
                    "   comprobante INTEGER not null," +
                    "fotocomprob INTEGER not null," +
                    "estatus INTEGER not null," +
                    "estatusSync INTEGER not null ," +
                  " PRIMARY KEY(id )) ");
            database.execSQL("alter  TABLE informe_etapa add column importemuestras real");

        }
    };

    static final Migration MIGRATION_30_31 = new Migration(30,31) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `acuse_recibo` (`id` INTEGER NOT NULL," +
                    " indice TEXT , "
                    + "aceptado INTEGER not null, "+
                    " PRIMARY KEY(`id`));");

            database.execSQL(
                    "ALTER TABLE lista_compras ADD COLUMN  lis_reactivado INTEGER; " );
            database.execSQL(
                    "DROP TABLE IF EXISTS coretiquetado_caja; " );
            database.execSQL(
                    "DROP TABLE IF EXISTS coretiquetado_cajadet; " );
            database.execSQL("create  TABLE coretiquetado_caja ( id INTEGER not null," +
                            "indice TEXT," +
                            "solicitudId INTEGER not null," +
                            " numfoto INTEGER,  " +
                            "  estatus INTEGER not null," +
                            " estatusSync INTEGER not null," +
                            "reubico INTEGER, "+
                            "createdAt INTEGER DEFAULT CURRENT_TIMESTAMP,  PRIMARY KEY(id)) ");

            database.execSQL("CREATE TABLE `coretiquetado_cajadet` (`id` INTEGER NOT NULL, " +
                            "coretiquetadocId INTEGER NOT NULL, "+

                             "ruta_fotonva INTEGER not null," +
                            " descripcionId INTEGER NOT NULL, " +
                            "descripcion TEXT," +
                            "numcaja INTEGER NOT NULL," +
                            " estatus INTEGER not null," +
                            "  estatusSync INTEGER not null," +
                            "PRIMARY KEY(`id`));");


        }
    };
    static final Migration MIGRATION_31_32 = new Migration(31,32) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE acuse_recibo ADD COLUMN ciudad TEXT; " );


        }
    };
    static final Migration MIGRATION_32_33 = new Migration(32,33) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("create  TABLE tienda ( une_id INTEGER not null,"+
                    "une_descripcion TEXT,"+
                    " tipoTienda TEXT,"+
                    " une_tipotienda INTEGER ,"+
                    "  une_direccion TEXT ,"+
                    " ciudad TEXT,"+
                    " une_cla_ciudad INTEGER,"+
                    " pais TEXT,"+
                    "  une_cla_pais INTEGER,"+
                    " une_puntocardinal TEXT,"+
                    " une_estatus INTEGER,"+
                    "  une_coordenadasxy TEXT,"+
                    " une_cadenacomercial INTEGER,"+
                    " une_dir_referencia TEXT,"+
                    " color TEXT,"+
                    " estpen INTEGER,"+ //para saber si compre en peñafiel
                    "  estpep INTEGER,"+
                    " estele INTEGER,"+
                    " estjum INTEGER,"+
                    " PRIMARY KEY(une_id )) ");

            database.execSQL("create  TABLE tienda_estatuscliente ( une_id INTEGER not null,"+
                    "clientesId INTEGER  not null,"+

                    " estatus INTEGER not null ,"+

                    " periodo INTEGER not null,"+

                    " PRIMARY KEY(une_id, clientesId )) ");


        }
    };
    static final Migration MIGRATION_33_34 = new Migration(33,34) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER  TABLE tienda add column periodo INTEGER");
            database.execSQL("drop  TABLE if exists tienda_estatuscliente ");
            database.execSQL("create  TABLE tienda_estatuscliente ( une_id INTEGER not null,"+
                    "clientesId INTEGER  not null,"+
                    " estatus INTEGER not null ,"+

                    " PRIMARY KEY(une_id, clientesId )) ");
        }
    };
    static final Migration MIGRATION_34_35 = new Migration(34,35) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "DROP TABLE IF EXISTS historico_muestras; " );
            database.execSQL("create  TABLE historico_muestras ( hisId INTEGER not null," +
                    "inf_indice TEXT," +
                    "plantaId INTEGER not null," +
                    "clienteId INTEGER not null,  " +
                    "ind_informes_id INTEGER not null," +
                    "ind_id INTEGER not null ," +
                    "productoId INTEGER not null, "+
                    "producto TEXT ," +
                    "tamanioId INTEGER not null ," +
                    "presentacion TEXT,  " +
                    "empaquesId INTEGER not null ," +
                    "empaque TEXT," +
                    "caducidad INTEGER, "+
                    "tipoAnalisis INTEGER not null, "+
                    "nombreAnalisis TEXT ," +
                    "categoriaId INTEGER not null," +
                    "categoriaNombre TEXT,  PRIMARY KEY(hisId)) ");

        }
    };

    static final Migration MIGRATION_35_36 = new Migration(35,36) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("drop  TABLE if exists tienda_estatuscliente ");
            database.execSQL("create  TABLE tienda_estatuscliente ( une_id INTEGER not null,"+
                    "clientesId INTEGER  not null,"+
                    "plantasId INTEGER not null," +
                    "plantasNombre TEXT,"+
                    " estatus INTEGER not null ,"+

                    " PRIMARY KEY(une_id, plantasId )) ");
        }
    };
    static final Migration MIGRATION_36_37 = new Migration(36,37) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE informe_compras ADD COLUMN causa_nocompraotras INTEGER; " );

        }
    };
    static final Migration MIGRATION_37_38 = new Migration(37,38) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER  TABLE tienda_estatuscliente ADD COLUMN ultimoindice TEXT ");

        }
    };

    static final Migration MIGRATION_38_39 = new Migration(38,39) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE lista_compras_detalle ADD COLUMN lid_qrpermitido TEXT; " );
            database.execSQL(
                    "ALTER TABLE lista_compras_detalle ADD COLUMN lid_nota TEXT; " );


        }
    };
    private void cargandodatos(){

        runInTransaction(new Runnable() {
            @Override
            public void run() {
                ReactivoDao dao = getReactivoDao();
                prepopulateder();
                prepopulatederpeni();
                prepopulatederele();
                prepopulatereaEmp();

                prepopulateCorEtiq();
            }
        });
    }



    private void prepopulateder( ) {
        String cliente="PEPSI";
        String cliid="4";
        Reactivo campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.planta));
        campo.setNombreCampo( "clientesId");
        campo.setType("selectDes");
        campo.setCatalogo(true);
        campo.setSigId(2);//abrir listacompra
        campo.setId(1);
        campo.setTabla("I");
        campo.setCliente("TODOS");
        campo.setClienteSel("0");
        List<Reactivo> camposForm = new ArrayList<Reactivo>();
        camposForm.add(campo);
         campo = new Reactivo();

        campo.setLabel(ctx.getString(R.string.se_compro_producto));
        campo.setNombreCampo( "primeraMuestra");
        campo.setType( "preguntasino");
        campo.setSigAlt(47);
        campo.setSigId(10000);//abrir listacompra
        campo.setId(2);
        campo.setTabla("I");
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.se_compro_segunda));
        campo.setNombreCampo( "segundaMuestra");
        campo.setId(3);
        campo.setType( "preguntasino");
        campo.setSigId(10000);//abrir listacompra
        campo.setSigAlt(5);
        campo.setTabla("I");
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.se_compro_terc));
        campo.setNombreCampo("terceraMuestra");
        campo.setTabla("I");
        campo.setType("preguntasino");
        campo.setSigId(10000);//abrir listacompra
        campo.setSigAlt(5);
        campo.setId( 4);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);


        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.comentarios));
        campo.setNombreCampo("comentarios");
        campo.setType("textarea");
        campo.setTabla("I");
        campo.setId(7);
        campo.setSigId(43);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.ticket_compra));
        campo.setNombreCampo("ticket_compra");
        campo.setType("agregarImagen");
        campo.setTabla("I");
        campo.setId(5);
        campo.setSigId(6);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.condiciones));
        campo.setNombreCampo("condiciones_traslado");
        campo.setType("agregarImagen");
        campo.setTabla("I");
        campo.setId(6);
        campo.setSigId(7);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.siglas_planta));
        campo.setNombreCampo(Contrato.TablaInformeDet.SIGLAS);
        campo.setType("inputtext");
        campo.setTabla("ID");
        campo.setId(23);
        campo.setSigId(24);
        campo.setBotonMicro(true);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.fecha_caducidad));
        campo.setNombreCampo("caducidad");
        campo.setType(CreadorFormulario.FECHAMASK);
        campo.setTabla("ID");
        campo.setId(24);
        campo.setSigId(25);
        campo.setBotonMicro(true);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.codigo_producto));
        campo.setNombreCampo("codigo");
        campo.setType("inputtext");
        campo.setTabla("ID");
        campo.setId(25);
        campo.setSigId(26);
        campo.setBotonMicro(true);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.origen));
        campo.setTabla("ID");
        campo.setNombreCampo( Contrato.TablaInformeDet.ORIGEN);
        campo.setType("selectCat");
        campo.setCatalogo(true);
        campo.setId(26);
        campo.setSigId(27);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.costo));
        campo.setNombreCampo(Contrato.TablaInformeDet.COSTO);
        campo.setType("decimalMask");
        campo.setTabla("ID");
        campo.setId(27);
       // campo.setSigId(28);
        campo.setSigId(32);
        campo.setBotonMicro(true);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_codigo_produccion));
        campo.setNombreCampo( Contrato.TablaInformeDet.FOTOCODIGOPRODUCCION);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(28);
        campo.setSigId(44);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel( ctx.getString(R.string.danio1));
        campo.setNombreCampo("danio1");
        campo.setType( "preguntasino");
        campo.setTabla("ID");
        campo.setCliente(cliente);

        campo.setId(32);
        campo.setSigId(33);
        campo.setSigAlt(41);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.atributoa));
        campo.setNombreCampo(Contrato.TablaInformeDet.ATRIBUTOA);
        campo.setType("selectCat");
        campo.setSigId(35);
        campo.setCatalogo(true);
        campo.setTabla("ID");
        campo.setId(33);
        //paso los atributos a catalogogen
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel( ctx.getString(R.string.danio2));
        campo.setId(35);
        campo.setSigId(36);
        campo.setTabla("ID");
        campo.setNombreCampo("danio2");
        campo.setType( "preguntasino");
        campo.setSigAlt(41);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);

        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.atributob));
        campo.setNombreCampo(Contrato.TablaInformeDet.ATRIBUTOB);
        campo.setType(CreadorFormulario.SELECTCAT);
        //campo.setSigId(37);
        campo.setSigId(38);
        campo.setId(36);
        campo.setCatalogo(true);
        campo.setTabla("ID");
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo = new Reactivo();
        campo.setLabel( ctx.getString(R.string.danio3));
        campo.setId(38);
        campo.setSigId(39);
        campo.setNombreCampo("danio3");
        campo.setType( "preguntasino");
        campo.setSigAlt(41);
        campo.setTabla("ID");
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.atributoc));
        campo.setNombreCampo(Contrato.TablaInformeDet.ATRIBUTOC);
        campo.setType(CreadorFormulario.SELECTCAT);
        campo.setTabla("ID");
        campo.setId(39);
      //  campo.setSigId(40);
        campo.setSigId(90);
        campo.setCatalogo(true);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel("QR");
        campo.setTabla("ID");
        campo.setNombreCampo(Contrato.TablaInformeDet.QR);
        campo.setType("botonqr");
        campo.setSigId(28);
       // campo.setSigId(42);
        campo.setId(41);
        campo.setClienteSel(cliid);
        campo.setCliente(cliente);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_posicion1));
        campo.setNombreCampo(Contrato.TablaInformeDet.FOTO_ATRIBUTOA);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(44);
        campo.setSigId(45);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);


        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_posicion2));
        campo.setNombreCampo(Contrato.TablaInformeDet.FOTO_ATRIBUTOB);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(45);
        campo.setSigId(46);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_posicion3));
        campo.setNombreCampo(Contrato.TablaInformeDet.FOTO_ATRIBUTOC);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
         campo.setId(46);
        campo.setSigId(130);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_posicion4));
        campo.setNombreCampo(Contrato.TablaInformeDet.FOTO_ATRIBUTOD);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(130);
        campo.setSigId(42);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.etiqueta_evaluacion));
        campo.setNombreCampo(Contrato.TablaInformeDet.ETIQUETA_EVALUACION);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(42);
        campo.setSigId(48);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        //insert into reactivos values(43,1,'¿CAPTURAR INFORME DE OTRO CLIENTE MISMA TIENDA?',"preguntasino","",0,0,0,null)
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.pregunta_mas_clientes));
        campo.setNombreCampo("");
        campo.setType("preguntasino");
        campo.setTabla("");
        campo.setClienteSel(cliid);
        campo.setCliente(cliente);

        campo.setId(43);
        campo.setSigId(1);
        campo.setSigAlt(500);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.selecciones_causa));
        campo.setNombreCampo(Contrato.TablaInformeDet.causa_nocompra);
        campo.setType("radiobutton");
        campo.setTabla("I");
        campo.setId(47);
        campo.setSigId(7);
        campo.setClienteSel(cliid);
        campo.setCatalogo(true);

        campo.setCliente(cliente);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.seguro_muestra));
        campo.setNombreCampo("confirmaMuestra");
        campo.setType("hidden");
        campo.setTabla("");
        campo.setId(48);
        campo.setClienteSel(cliid);
        campo.setCliente(cliente);
        camposForm.add(campo);
        campo = new Reactivo();
        campo.setLabel( ctx.getString(R.string.danio3));
        campo.setId(90);
        campo.setSigId(106);
        campo.setNombreCampo("danio4");
        campo.setType( "preguntasino");
        campo.setSigAlt(41);
        campo.setTabla("ID");
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.atributod));
        campo.setNombreCampo(Contrato.TablaInformeDet.ATRIBUTOD);
        campo.setType("selectCat");
        campo.setSigId(41);
        campo.setCatalogo(true);
        campo.setTabla("ID");
        campo.setId(106);
        //paso los atributos a catalogogen
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.selec_motivo_sust));
        campo.setNombreCampo(Contrato.TablaInformeDet.CAUSA_SUSTITUCIONID);
        campo.setType("selectCat");
        campo.setSigId(23);
        campo.setCatalogo(true);
        campo.setTabla("ID");
        campo.setId(126);
        //paso los atributos a catalogogen
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        getReactivoDao().insertAll(camposForm);


    }

    private void prepopulatederpeni( ) {
        Reactivo campo = new Reactivo();
        String cliente="PEÑAFIEL";
        String cliid="5,7";

        List<Reactivo> camposForm = new ArrayList<Reactivo>();
        campo = new Reactivo();

        campo.setLabel(ctx.getString(R.string.se_compro_producto));
        campo.setNombreCampo( "primeraMuestra");
        campo.setType( "preguntasino");
        campo.setSigAlt(69);
        campo.setSigId(20000);//abrir listacompra
        campo.setId(52);
        campo.setTabla("I");
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.se_compro_segunda));
        campo.setNombreCampo( "segundaMuestra");
        campo.setId(53);
        campo.setType( "preguntasino");
        campo.setSigId(20000);//abrir listacompra
        campo.setSigAlt(55);
        campo.setTabla("I");
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.se_compro_terc));
        campo.setNombreCampo("terceraMuestra");
        campo.setTabla("I");
        campo.setType("preguntasino");
        campo.setSigId(20000);//abrir listacompra
        campo.setSigAlt(55);
        campo.setId(54);
        campo.setCliente(cliente);    campo.setClienteSel("5");
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.se_compro_cuar));
        campo.setNombreCampo("cuartaMuestra");
        campo.setTabla("I");
        campo.setType("preguntasino");
        campo.setSigId(20000);//abrir listacompra
        campo.setSigAlt(55);
        campo.setId(67);
        campo.setCliente(cliente);    campo.setClienteSel("5");
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.ticket_compra));
        campo.setNombreCampo("ticket_compra");
        campo.setType("agregarImagen");
        campo.setTabla("I");
        campo.setId(55);
        campo.setSigId(56);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.condiciones));
        campo.setNombreCampo("condiciones_traslado");
        campo.setType("agregarImagen");
        campo.setTabla("I");
        campo.setId(56);
        campo.setSigId(57);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.comentarios));
        campo.setNombreCampo("comentarios");
        campo.setType("textarea");
        campo.setTabla("I");
        campo.setId(57);
        campo.setSigId(68);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.siglas_planta));
        campo.setNombreCampo(Contrato.TablaInformeDet.SIGLAS);
        campo.setType("inputtext");
        campo.setTabla("ID");
        campo.setId(58);
        campo.setSigId(59);
        campo.setBotonMicro(true);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);

        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.fecha_caducidad));
        campo.setNombreCampo("caducidad");
        campo.setType(CreadorFormulario.FECHAMASK);
        campo.setTabla("ID");
        campo.setId(59);
        campo.setSigId(60);
        campo.setSigAlt(115);
        campo.setBotonMicro(true);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);

        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.codigo_producto));
        campo.setNombreCampo("codigo");
        campo.setType("inputtext");
        campo.setTabla("ID");
        campo.setId(60);
        campo.setSigId(61);
        campo.setBotonMicro(true);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);

        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.origen));
        campo.setTabla("ID");
        campo.setNombreCampo( Contrato.TablaInformeDet.ORIGEN);
        campo.setType("selectCat");
        campo.setCatalogo(true);
        campo.setId(61);
        campo.setSigId(62);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.costo));
        campo.setNombreCampo(Contrato.TablaInformeDet.COSTO);
        campo.setType("decimalMask");
        campo.setTabla("ID");
        campo.setId(62);
        // campo.setSigId(28);
        campo.setSigId(107);
        campo.setBotonMicro(true);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);

        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_codigo_produccion));
        campo.setNombreCampo( Contrato.TablaInformeDet.FOTOCODIGOPRODUCCION);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(63);
        campo.setSigId(64);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_posicion1));
        campo.setNombreCampo(Contrato.TablaInformeDet.FOTO_ATRIBUTOA);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(64);
        campo.setSigId(65);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);


        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_posicion2));
        campo.setNombreCampo(Contrato.TablaInformeDet.FOTO_ATRIBUTOB);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(65);
        campo.setSigId(66);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_posicion3));
        campo.setNombreCampo(Contrato.TablaInformeDet.FOTO_ATRIBUTOC);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(66);
        campo.setSigId(131);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_posicion4));
        campo.setNombreCampo(Contrato.TablaInformeDet.FOTO_ATRIBUTOD);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(131);
        campo.setSigId(70);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.seguro_muestra));
        campo.setNombreCampo("confirmaMuestra");
        campo.setType("hidden");
        campo.setTabla("");
        campo.setId(70);

        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.esuncod_nop));
        campo.setNombreCampo("esuncodnop");
        campo.setType("preguntasino");
        campo.setTabla("");
        campo.setId(115);
        campo.setSigId(60);
        campo.setSigAlt(59);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        //insert into reactivos values(43,1,'¿CAPTURAR INFORME DE OTRO CLIENTE MISMA TIENDA?',"preguntasino","",0,0,0,null)
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.pregunta_mas_clientes));
        campo.setNombreCampo("");
        campo.setType("preguntasino");
        campo.setTabla("");
        campo.setCliente(cliente);    campo.setClienteSel(cliid);

        campo.setId(68);
        campo.setSigId(51);
        campo.setSigAlt(600);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.selecciones_causa));
        campo.setNombreCampo(Contrato.TablaInformeDet.causa_nocompra);
        campo.setType("radiobutton");
        campo.setTabla("I");
        campo.setId(69);
        campo.setSigId(57);
        campo.setCatalogo(true);

        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel("QR");
        campo.setTabla("ID");
        campo.setNombreCampo(Contrato.TablaInformeDet.QR);
        campo.setType("botonqr");
        campo.setSigId(63);
        // campo.setSigId(42);
        campo.setId(107);
        campo.setClienteSel(cliid);
        campo.setCliente(cliente);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.selec_motivo_sust));
        campo.setNombreCampo(Contrato.TablaInformeDet.CAUSA_SUSTITUCIONID);
        campo.setType("selectCat");
        campo.setId(127);
        campo.setSigId(58);
        campo.setCatalogo(true);
        campo.setTabla("ID");

        //paso los atributos a catalogogen
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        getReactivoDao().insertAll(camposForm);


    }

    private  void prepopulatederele( ) {
        Reactivo campo = new Reactivo();
        String cliente="ELECTROPURA";
        String cliid="6";

        List<Reactivo> camposForm = new ArrayList<Reactivo>();

        campo.setLabel(ctx.getString(R.string.se_compro_producto));
        campo.setNombreCampo( "primeraMuestra");
        campo.setType( "preguntasino");
        campo.setSigAlt(89);
        campo.setSigId(3000);//abrir listacompra
        campo.setId(72);
        campo.setTabla("I");
        campo.setCliente(cliente);    campo.setClienteSel(cliid);

        camposForm.add(campo);
        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.se_compro_segunda));
        campo.setNombreCampo( "segundaMuestra");
        campo.setId(73);
        campo.setType( "preguntasino");
        campo.setSigId(3000);//abrir listacompra
        campo.setSigAlt(75);
        campo.setTabla("I");
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.se_compro_terc));
        campo.setNombreCampo("terceraMuestra");
        campo.setTabla("I");
        campo.setType("preguntasino");
        campo.setSigId(3000);//abrir listacompra
        campo.setSigAlt(75);
        campo.setId( 74);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.comentarios));
        campo.setNombreCampo("comentarios");
        campo.setType("textarea");
        campo.setTabla("I");
        campo.setId(77);
        campo.setSigId(88);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.ticket_compra));
        campo.setNombreCampo("ticket_compra");
        campo.setType("agregarImagen");
        campo.setTabla("I");
        campo.setId(75);
        campo.setSigId(76);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.condiciones));
        campo.setNombreCampo("condiciones_traslado");
        campo.setType("agregarImagen");
        campo.setTabla("I");
        campo.setId(76);
        campo.setSigId(77);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.siglas_planta));
        campo.setNombreCampo(Contrato.TablaInformeDet.SIGLAS);
        campo.setType("inputtext");
        campo.setTabla("ID");
        campo.setId(78);
        campo.setSigId(79);
        campo.setBotonMicro(true);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);

        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.fecha_caducidad));
        campo.setNombreCampo("caducidad");
        campo.setType(CreadorFormulario.FECHAMASK);
        campo.setTabla("ID");
        campo.setId(79);
        campo.setSigId(80);
        campo.setSigAlt(116);

        campo.setBotonMicro(true);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);

        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.codigo_producto));
        campo.setNombreCampo("codigo");
        campo.setType("inputtext");
        campo.setTabla("ID");
        campo.setId(80);
        campo.setSigId(81);
        campo.setBotonMicro(true);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);

        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.origen));
        campo.setTabla("ID");
        campo.setNombreCampo( Contrato.TablaInformeDet.ORIGEN);
        campo.setType("selectCat");
        campo.setCatalogo(true);
        campo.setId(81);
        campo.setSigId(82);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.costo));
        campo.setNombreCampo(Contrato.TablaInformeDet.COSTO);
        campo.setType("decimalMask");
        campo.setTabla("ID");
        campo.setId(82);
        // campo.setSigId(28);
        campo.setSigId(108);
        campo.setBotonMicro(true);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);

        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_codigo_produccion));
        campo.setNombreCampo( Contrato.TablaInformeDet.FOTOCODIGOPRODUCCION);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(83);
        campo.setSigId(84);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_posicion1));
        campo.setNombreCampo(Contrato.TablaInformeDet.FOTO_ATRIBUTOA);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(84);
        campo.setSigId(85);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);


        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_posicion2));
        campo.setNombreCampo(Contrato.TablaInformeDet.FOTO_ATRIBUTOB);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(85);
        campo.setSigId(86);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_posicion3));
        campo.setNombreCampo(Contrato.TablaInformeDet.FOTO_ATRIBUTOC);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(86);
        campo.setSigId(132);
        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto_posicion4));
        campo.setNombreCampo(Contrato.TablaInformeDet.FOTO_ATRIBUTOD);
        campo.setType("agregarImagen");
        campo.setTabla("ID");
        campo.setId(132);
        campo.setSigId(87);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.seguro_muestra));
        campo.setNombreCampo("confirmaMuestra");
        campo.setType("hidden");
        campo.setTabla("");
        campo.setId(87);

        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        //insert into reactivos values(43,1,'¿CAPTURAR INFORME DE OTRO CLIENTE MISMA TIENDA?',"preguntasino","",0,0,0,null)
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.pregunta_mas_clientes));
        campo.setNombreCampo("");
        campo.setType("preguntasino");
        campo.setTabla("");
        campo.setCliente(cliente);    campo.setClienteSel(cliid);

        campo.setId(88);
        campo.setSigId(1);
        campo.setSigAlt(800);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.selecciones_causa));
        campo.setNombreCampo(Contrato.TablaInformeDet.causa_nocompra);
        campo.setType("radiobutton");
        campo.setTabla("I");
        campo.setId(89);
        campo.setSigId(77);
        campo.setCatalogo(true);

        campo.setCliente(cliente);    campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel("QR");
        campo.setTabla("ID");
        campo.setNombreCampo(Contrato.TablaInformeDet.QR);
        campo.setType("botonqr");
        campo.setSigId(83);
        // campo.setSigId(42);
        campo.setId(108);
        campo.setClienteSel(cliid);
        campo.setCliente(cliente);
        camposForm.add(campo);

        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.esuncod_nop));
        campo.setNombreCampo("esuncodnop");
        campo.setType("preguntasino");
        campo.setTabla("");
        campo.setId(116);
        campo.setSigId(80);
        campo.setSigAlt(79);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        campo=new Reactivo();
        campo.setLabel(ctx.getString(R.string.selec_motivo_sust));
        campo.setNombreCampo(Contrato.TablaInformeDet.CAUSA_SUSTITUCIONID);
        campo.setType("selectCat");
        campo.setId(128);
        campo.setSigId(78);
        campo.setCatalogo(true);
        campo.setTabla("ID");

        //paso los atributos a catalogogen
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);
        getReactivoDao().insertAll(camposForm);


    }
    public void catalogos(){
        CatalogoDetalle cat=new CatalogoDetalle();
        List<CatalogoDetalle> lista=new ArrayList<>();
        cat.setCad_idcatalogo(1);
        cat.setCad_idopcion(1);
        cat.setCad_descripcionesp("atributo 1");
        cat.setCad_nombreCatalogo("atributos");
        lista.add(cat);
        getCatalogoDao().insertAll(lista);
         cat=new CatalogoDetalle();

        cat.setCad_idcatalogo(1);
        cat.setCad_idopcion(2);
        cat.setCad_descripcionesp("atributo 2");
        cat.setCad_nombreCatalogo("atributos");
        lista.add(cat);
        getCatalogoDao().insertAll(lista);
        cat=new CatalogoDetalle();

        cat.setCad_idcatalogo(1);
        cat.setCad_idopcion(3);
        cat.setCad_descripcionesp("atributo 3");
        cat.setCad_nombreCatalogo("atributos");
        lista.add(cat);
        getCatalogoDao().insertAll(lista);
        cat=new CatalogoDetalle();

        cat.setCad_idcatalogo(1);
        cat.setCad_idopcion(4);
        cat.setCad_descripcionesp("atributo 4");
        cat.setCad_nombreCatalogo("atributos");
        lista.add(cat);
        getCatalogoDao().insertAll(lista);
        cat=new CatalogoDetalle();

        cat.setCad_idcatalogo(1);
        cat.setCad_idopcion(5);
        cat.setCad_descripcionesp("atributo 5");
        cat.setCad_nombreCatalogo("atributos");
        lista.add(cat);
        getCatalogoDao().insertAll(lista);
        getCatalogoDao().insertAll(lista);

    }


    private  void prepopulatereaEmp( ) {
        Reactivo campo = new Reactivo();
        String cliente = "PEPSI";
        String cliid = "4";


        List<Reactivo> camposForm = new ArrayList<Reactivo>();

      // ya no será por planta será nix cliente
        campo = new Reactivo();
        campo.setId(91);
        campo.setTabla("IE");
        campo.setLabel(ctx.getString(R.string.cliente));
        campo.setNombreCampo("clientesId");
        campo.setType("selectDes");

        campo.setSigId(93);

        campo.setCliente(cliente);
        campo.setClienteSel(cliid);

        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.relleno_esp));
        campo.setId(93);
        campo.setTabla("ED");
        campo.setNombreCampo("relleno_espacios");
        campo.setType("agregarImagen");
        campo.setSigId(94);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.proteccion_fin));
        campo.setId(94);
        campo.setTabla("ED");
        campo.setNombreCampo("proteccion_antes");
        campo.setType("agregarImagen");
        campo.setSigId(95);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.caja_cer));
        campo.setId(95);
        campo.setTabla("ED");
        campo.setNombreCampo("caja_cerrada");
        campo.setType("agregarImagen");
        campo.setSigId(96);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.etiqueta_ana));
        campo.setId(96);
        campo.setTabla("ED");
        campo.setNombreCampo("etiqueta_ana");
        campo.setType("agregarImagen");
        campo.setSigId(97);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.etiqueta_des));
        campo.setId(97);
        campo.setTabla("ED");
        campo.setNombreCampo("etiqueta_des");
        campo.setType("agregarImagen");
        campo.setSigId(98);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.largo)+" (CM)");
        campo.setId(98);
        campo.setTabla("DC");
        campo.setNombreCampo("dimlargo");
        campo.setType("inputtext");
        campo.setSigId(99);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto)+" "+ctx.getString(R.string.largo));
        campo.setId(99);
        campo.setTabla("ED");
        campo.setNombreCampo("largo");
        campo.setType("agregarImagen");
        campo.setSigId(100);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.ancho)+" (CM)");
        campo.setId(100);
        campo.setTabla("DC");
        campo.setNombreCampo("dimancho");
        campo.setType("inputtext");
        campo.setSigId(101);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto)+" "+ctx.getString(R.string.ancho));
        campo.setId(101);
        campo.setTabla("ED");
        campo.setNombreCampo("ancho");
        campo.setType("agregarImagen");
        campo.setSigId(102);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.alto)+" (CM)");
        campo.setId(102);
        campo.setTabla("DC");
        campo.setNombreCampo("dimalto");
        campo.setType("inputtext");
        campo.setSigId(103);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto)+" "+ctx.getString(R.string.alto));
        campo.setId(103);
        campo.setTabla("ED");
        campo.setNombreCampo("alto");
        campo.setType("agregarImagen");
        campo.setSigId(104);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.peso)+" (KG)");
        campo.setId(104);
        campo.setTabla("DC");
        campo.setNombreCampo("dimpeso");
        campo.setType("inputtext");
        campo.setSigId(113);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.foto)+" "+ctx.getString(R.string.peso));
        campo.setId(113);
        campo.setTabla("ED");
        campo.setNombreCampo("peso");
        campo.setType("agregarImagen");
        campo.setSigId(92);
        campo.setSigAlt(114);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.comentarios));
        campo.setId(114);
        campo.setTabla("IE");
        campo.setNombreCampo("comentarios_emp");
        campo.setType("inputtext");
        campo.setSigId(0);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        getReactivoDao().insertAll(camposForm);
    }

    private  void prepopulateCorEtiq( ) {
        Reactivo campo = new Reactivo();
        String cliente = "PEPSI";
        String cliid = "4";
        List<Reactivo> camposForm = new ArrayList<Reactivo>();
        // ya no será por planta será nix cliente
        campo = new Reactivo();
        campo.setId(120);
        campo.setTabla("C");
        campo.setLabel(ctx.getString(R.string.al_cambiarcaja));
        campo.setNombreCampo("reubica");
        campo.setType("preguntasino");
        campo.setSigAlt(123); //voy a fotos de caja
        campo.setSigId(121);

        campo.setCliente(cliente);
        campo.setClienteSel(cliid);

        camposForm.add(campo);
        campo = new Reactivo();
        campo.setLabel("QR");
        campo.setId(121);
        campo.setTabla("C");
        campo.setNombreCampo("qr");
        campo.setType("botonqr");
        campo.setSigId(122);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.reubicasteotra));
        campo.setId(122);
        campo.setTabla("C");
        campo.setNombreCampo("otramues");
        campo.setType("preguntasino");
        campo.setSigId(121); //pido fotos de caja
        campo.setSigAlt(123); //si contesta si voy a qr
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.caraa));
        campo.setId(123);
        campo.setTabla("C");
        campo.setNombreCampo("caraa");
        campo.setType("agregarImagen");
        campo.setSigId(124);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.carab));
        campo.setId(124);
        campo.setTabla("C");
        campo.setNombreCampo("carab");
        campo.setType("agregarImagen");
        campo.setSigId(125);
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        campo = new Reactivo();
        campo.setLabel(ctx.getString(R.string.acomodo_mues));
        campo.setId(125);
        campo.setTabla("C");
        campo.setNombreCampo("etiqueta_ana");
        campo.setType("agregarImagen");
        campo.setSigId(123);
        campo.setSigAlt(1000);//termina
        campo.setCliente(cliente);
        campo.setClienteSel(cliid);
        camposForm.add(campo);

        getReactivoDao().insertAll(camposForm);
    }

}


