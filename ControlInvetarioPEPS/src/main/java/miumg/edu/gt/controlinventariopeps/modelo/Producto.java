package miumg.edu.gt.controlinventariopeps.modelo;

public class Producto {
    private int idProducto;
    private String nombreProducto;
    private String descripcion;
    private double precioUnitario;
    private int stockTotal;
    private boolean activo;

    public Producto() {}

    public Producto(int idProducto, String nombreProducto, String descripcion, 
                    double precioUnitario, int stockTotal) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.stockTotal = stockTotal;
    }

    // Getters y Setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public int getStockTotal() { return stockTotal; }
    public void setStockTotal(int stockTotal) { this.stockTotal = stockTotal; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}