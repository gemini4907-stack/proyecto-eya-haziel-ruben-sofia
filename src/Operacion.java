public class Operacion {
    private String tipo;
    private Producto productoAfectado;
    public Operacion(String tipo, Producto productoAfectado){
        this.tipo= tipo;
        this.productoAfectado= productoAfectado;
    }
    public String getTipo(){
        return tipo;
    }
    public Producto getProductoAfectado(){
        return productoAfectado;
    }
    @Override
    public String toString(){
        return "Operacion:" + tipo + "->" + productoAfectado.getNombre() + "(" + productoAfectado.getCodigo()+ ")";
    }
}
