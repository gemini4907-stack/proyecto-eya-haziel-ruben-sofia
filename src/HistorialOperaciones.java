import java.util.ArrayDeque;
import java.util.Deque;

public class HistorialOperaciones {
    private Deque<Operacion> pilaAcciones;

    public HistorialOperaciones() {
        this.pilaAcciones = new ArrayDeque<>();
    }

    public void registrarAccion(String tipo, Producto p) {
        if (tipo == null || p == null) {
            return;
        }
        Producto respaldo = new Producto(
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                p.getPrecio(),
                p.getStock()
        );
        pilaAcciones.push(new Operacion(tipo.toUpperCase(), respaldo));
    }


    public boolean hayAccionesPendientes() {
        return !pilaAcciones.isEmpty();
    }


    public String deshacerUltimaAccion(Inventario inv) {
        if (pilaAcciones.isEmpty()) {
            return "No hay operaciones recientes para deshacer.";
        }

        Operacion ultima = pilaAcciones.pop();
        String tipo = ultima.getTipo();
        Producto respaldo = ultima.getProductoAfectado();

        switch (tipo) {
            case "AGREGAR":
                inv.eliminarProducto(respaldo.getCodigo());
                return "Se deshizo el registro de: " + respaldo.getNombre() + " [" + respaldo.getCodigo() + "]";

            case "ELIMINAR":
                inv.registrarProducto(respaldo);
                return "Se restauró el producto eliminado: " + respaldo.getNombre() + " [" + respaldo.getCodigo() + "]";

            case "STOCK":
                Producto actual = inv.buscarPorCodigo(respaldo.getCodigo());
                if (actual != null) {
                    actual.setStock(respaldo.getStock());
                    return "Se restauró el stock anterior (" + respaldo.getStock() + ") de: " + respaldo.getNombre();
                }
                return "No se pudo restaurar el stock porque el producto ya no existe.";

            default:
                return "Operación desconocida: " + tipo;
        }
    }
}