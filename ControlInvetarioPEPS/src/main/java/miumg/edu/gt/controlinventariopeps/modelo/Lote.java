package miumg.edu.gt.controlinventariopeps.modelo;

import java.sql.Date;

public class Lote {
    private int idLote;
    private int idProducto;
    private String codigoLote;
    private Date fechaIngreso;
    private Date fechaVencimiento;
    private int cantidadInicial;
    private int stockActual;
    private double costoUnitario;
    private boolean activo;

    // Getters y Setters
    public int getIdLote() { return idLote; }
    public void setIdLote(int idLote) { this.idLote = idLote; }
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }
    public Date getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Date fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public int getCantidadInicial() { return cantidadInicial; }
    public void setCantidadInicial(int cantidadInicial) { this.cantidadInicial = cantidadInicial; }
    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }
    public double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario = costoUnitario; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}