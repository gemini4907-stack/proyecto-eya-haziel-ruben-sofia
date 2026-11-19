import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConsultasInventario {

    private final Inventario inventario;
    private final ArbolPrefijos arbolPrefijos;

    public ConsultasInventario(Inventario inventario) {
        this.inventario = inventario;
        this.arbolPrefijos = new ArbolPrefijos();
        sincronizarArbol();
    }

    public void sincronizarArbol() {
        for (Producto p : inventario.obtenerListaProductos()) {
            arbolPrefijos.insertar(p.getNombre());
        }
    }

    public void indexarNombre(String nombre) {
        arbolPrefijos.insertar(nombre);
    }

    public List<String> autocompletarPorNombre(String prefijo) {
        return arbolPrefijos.buscarPorPrefijo(prefijo);
    }

    public List<String> productosPorCategoria(String cat) {
        if (cat == null || cat.trim().isEmpty()) {
            return List.of();
        }
        String categoriaBuscada = cat.trim();

        return inventario.obtenerListaProductos().stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(categoriaBuscada) && p.getStock() > 0)
                .sorted(Comparator.comparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER))
                .map(Producto::toString)
                .toList();
    }

    public List<Producto> topProductosMasCaros(int limite) {
        if (limite <= 0) {
            return List.of();
        }

        return inventario.obtenerListaProductos().stream()
                .sorted(Comparator.comparingDouble(Producto::getPrecio).reversed())
                .limit(limite)
                .toList();
    }

    public Map<String, Double> valorTotalPorCategoria() {
        return inventario.obtenerListaProductos().stream()
                .collect(Collectors.groupingBy(
                        Producto::getCategoria,
                        Collectors.summingDouble(Producto::getValorTotalInventario)
                ));
    }

    public Optional<Producto> productoConMenorStock() {
        return inventario.obtenerListaProductos().stream()
                .min(Comparator.comparingInt(Producto::getStock));
    }
}