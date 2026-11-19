import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Inventario {
    private Map<String, Producto> productosPorCodigo;
    private Set<String> categoriasDisponibles;

    public Inventario() {
        this.productosPorCodigo = new HashMap<>();
        this.categoriasDisponibles = new HashSet<>();
    }

    public boolean registrarProducto(Producto p) {
        if (p == null || p.getCodigo() == null || p.getCodigo().trim().isEmpty()) {
            return false;
        }
        String codigoNormalizado = p.getCodigo().trim().toUpperCase();
        if (productosPorCodigo.containsKey(codigoNormalizado)) {
            return false;
        }
        p.setCodigo(codigoNormalizado);
        productosPorCodigo.put(codigoNormalizado, p);
        reconstruirCategorias();
        return true;
    }

    public Producto buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        return productosPorCodigo.get(codigo.trim().toUpperCase());
    }

    public Producto eliminarProducto(String codigo) {
        if (codigo == null) {
            return null;
        }
        Producto eliminado = productosPorCodigo.remove(codigo.trim().toUpperCase());
        if (eliminado != null) {
            reconstruirCategorias();
        }
        return eliminado;
    }

    public boolean modificarStock(String codigo, int delta) {
        Producto p = buscarPorCodigo(codigo);
        if (p == null || (p.getStock() + delta < 0)) {
            return false;
        }
        p.actualizarStock(delta);
        return true;
    }

    private void reconstruirCategorias() {
        categoriasDisponibles.clear();
        for (Producto p : productosPorCodigo.values()) {
            if (p.getCategoria() != null && !p.getCategoria().trim().isEmpty()) {
                categoriasDisponibles.add(p.getCategoria().trim());
            }
        }
    }

    public Set<String> obtenerTodasLasCategorias() {
        return new HashSet<>(categoriasDisponibles);
    }

    public List<Producto> obtenerListaProductos() {
        return new ArrayList<>(productosPorCodigo.values());
    }

    public int totalProductosRegistrados() {
        return productosPorCodigo.size();
    }
}