package com.example.comprasmu.data.modelos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "lista_compras_detalle",primaryKeys = {"id", "listaId"})
public class ListaCompraDetalle {

    protected int id;
    protected int listaId;
    protected int productosId;
    protected String productoNombre;
    protected String tamanio;
    protected int tamanioId;
    protected String empaque;
    protected int empaquesId;
    protected String tipoAnalisis;
    protected int analisisId;
    protected int cantidad;
    protected String codigosNoPermitidos;
    protected String nvoCodigo;
    protected int estatus;
    protected int comprados;
    protected int tipoMuestra;
    protected String nombreTipoMuestra;
    protected int categoriaid;
    protected String categoria;
    protected String lid_fechapermitida;
    protected String lid_fecharestringida;
    protected int lid_orden;
    @ColumnInfo(defaultValue = "0")
    protected int ordtam; //ordenar x tamanio
    @ColumnInfo( defaultValue = "0")
    protected int ordemp; //ordenar x empaque
    @ColumnInfo( defaultValue = "0")
    protected int ordtipa;
    @ColumnInfo(defaultValue = "0")
    protected int ordtipm;

    @ColumnInfo(name="lid_backup", defaultValue = "-1")
    protected int lid_backup;
    @ColumnInfo(defaultValue = "0")
    protected int lid_numtienbak;
    protected String lid_qrpermitido;
    protected String lid_nota;

    public ListaCompraDetalle(){

    }
    public void iniciarListaCompraDetalle(int id, int listaId, int productosId, String productoNombre, String tamanio, int tamanioId, String empaque,
                                          int empaquesId, String tipoAnalisis, int analisisId, int cantidad,
                                          String codigosNoPermitidos, String nvoCodigo, int estatus,
                                          int comprados, int tipoMuestra, String nombreTipoMuestra,
                                          int categoriaid, String categoria, String lid_fechapermitida,
                                          String lid_fecharestringida, int lid_orden, int lid_backup, String lid_nota) {
        this.id = id;
        this.listaId = listaId;
        this.productosId = productosId;
        this.productoNombre = productoNombre;
        this.tamanio = tamanio;
        this.tamanioId = tamanioId;
        this.empaque = empaque;
        this.empaquesId = empaquesId;
        this.tipoAnalisis = tipoAnalisis;
        this.analisisId = analisisId;
        this.cantidad = cantidad;
        this.codigosNoPermitidos = codigosNoPermitidos;
        this.nvoCodigo = nvoCodigo;
        this.estatus = estatus;
        this.comprados = comprados;
        this.tipoMuestra = tipoMuestra;
        this.nombreTipoMuestra = nombreTipoMuestra;
        this.categoriaid = categoriaid;
        this.categoria = categoria;
        this.lid_fechapermitida = lid_fechapermitida;
        this.lid_fecharestringida = lid_fecharestringida;
        this.lid_orden = lid_orden;
        this.lid_backup = lid_backup;
        this.lid_nota=lid_nota;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getListaId() {
        return listaId;
    }

    public void setListaId(int listaId) {
        this.listaId = listaId;
    }

    public int getProductosId() {
        return productosId;
    }

    public void setProductosId(int productosId) {
        this.productosId = productosId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public String getTamanio() {
        return tamanio;
    }

    public void setTamanio(String tamanio) {
        this.tamanio = tamanio;
    }

    public String getEmpaque() {
        return empaque;
    }

    public void setEmpaque(String empaque) {
        this.empaque = empaque;
    }

    public int getEmpaquesId() {
        return empaquesId;
    }

    public void setEmpaquesId(int empaquesId) {
        this.empaquesId = empaquesId;
    }

    public String getTipoAnalisis() {
        return tipoAnalisis;
    }

    public void setTipoAnalisis(String tipoAnalisis) {
        this.tipoAnalisis = tipoAnalisis;
    }

    public int getAnalisisId() {
        return analisisId;
    }

    public void setAnalisisId(int analisisId) {
        this.analisisId = analisisId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getCodigosNoPermitidos() {
        return codigosNoPermitidos;
    }

    public void setCodigosNoPermitidos(String codigosNoPermitidos) {
        this.codigosNoPermitidos = codigosNoPermitidos;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public int getComprados() {
        return comprados;
    }

    public void setComprados(int comprados) {
        this.comprados = comprados;
    }

    public String getPresentacion(){
        return this.empaque+" "+this.tamanio;
    }

    public int getTipoMuestra() {
        return tipoMuestra;
    }

    public void setTipoMuestra(int tipoMuestra) {
        this.tipoMuestra = tipoMuestra;
    }

    public String getNombreTipoMuestra() {
        return nombreTipoMuestra;
    }

    public void setNombreTipoMuestra(String nombreTipoMuestra) {
        this.nombreTipoMuestra = nombreTipoMuestra;
    }

    public int getCategoriaid() {
        return categoriaid;
    }

    public void setCategoriaid(int categoriaid) {
        this.categoriaid = categoriaid;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getLid_fechapermitida() {
        return lid_fechapermitida;
    }

    public void setLid_fechapermitida(String lid_fechapermitida) {
        this.lid_fechapermitida = lid_fechapermitida;
    }

    public String getLid_fecharestringida() {
        return lid_fecharestringida;
    }

    public void setLid_fecharestringida(String lid_fecharestringida) {
        this.lid_fecharestringida = lid_fecharestringida;
    }

    public String getNvoCodigo() {
        return nvoCodigo;
    }

    public void setNvoCodigo(String nvoCodigo) {
        this.nvoCodigo = nvoCodigo;
    }

    public int getTamanioId() {
        return tamanioId;
    }

    public void setTamanioId(int tamanioId) {
        this.tamanioId = tamanioId;
    }

    public int getLid_orden() {
        return lid_orden;
    }

    public void setLid_orden(int lid_orden) {
        this.lid_orden = lid_orden;
    }

    public int getLid_backup() {
        return lid_backup;
    }

    public void setLid_backup(int lid_backup) {
        this.lid_backup = lid_backup;
    }

    public int getOrdtam() {
        return ordtam;
    }

    public void setOrdtam(int ordtam) {
        this.ordtam = ordtam;
    }

    public int getOrdemp() {
        return ordemp;
    }

    public void setOrdemp(int ordemp) {
        this.ordemp = ordemp;
    }

    public int getOrdtipa() {
        return ordtipa;
    }

    public void setOrdtipa(int ordtipa) {
        this.ordtipa = ordtipa;
    }

    public int getOrdtipm() {
        return ordtipm;
    }

    public void setOrdtipm(int ordtipm) {
        this.ordtipm = ordtipm;
    }

    public int getLid_numtienbak() {
        return lid_numtienbak;
    }

    public void setLid_numtienbak(int lid_numtienbak) {
        this.lid_numtienbak = lid_numtienbak;
    }

    public String getLid_nota() {
        return lid_nota;
    }

    public void setLid_nota(String lid_nota) {
        this.lid_nota = lid_nota;
    }

    public String getLid_qrpermitido() {
        return lid_qrpermitido;
    }

    public void setLid_qrpermitido(String lid_qrpermitido) {
        this.lid_qrpermitido = lid_qrpermitido;
    }
}
