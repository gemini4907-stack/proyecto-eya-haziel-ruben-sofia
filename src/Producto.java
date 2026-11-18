public class Producto {
    private String codigo;
    private String nombre;
    private String categoria;
    private double precio;
    private int stock;

    public Producto(String codigo, String nombre, String categoria, double precio, int stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }

    public void actualizarStock(int cantidad){
        if (this.stock + cantidad>= 0) {
            this.stock += cantidad;
        }
    }
    public double getValorTotalInventario(){
        return this.precio * this.stock;
    }

    public String getCodigo(){
        return codigo;
    }
    public void setCodigo(String codigo){
        this.codigo= codigo;
    }
    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre=nombre;
    }
    public String getCategoria(){
        return categoria;
    }
    public void setCategoria(String categoria){
        this.categoria= categoria;
    }
    public double getPrecio(){
        return precio;
    }
    public void setPrecio(double precio){
        this.precio= precio;
    }
    public int getStock(){
        return stock;
    }
    public void setStock(int stock){
        this.stock= stock;
    }
    @Override
    public String toString(){
        return String.format("[%s] %s | Cat: %s | Precio: $%.2f | Stock: %d"
                ,codigo,nombre,categoria,precio,stock);
    }


}
