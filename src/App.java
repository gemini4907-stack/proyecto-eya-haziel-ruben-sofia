import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class App {

    private final Scanner scanner;
    private final Inventario inventario;
    private final HistorialOperaciones historial;
    private final ConsultasInventario consultas;

    public App() {
        this.scanner = new Scanner(System.in);
        this.inventario = new Inventario();
        this.historial = new HistorialOperaciones();
        cargarDatosIniciales();
        this.consultas = new ConsultasInventario(this.inventario);
    }

    public static void main(String[] args) {
        App app = new App();
        app.iniciar();
    }

    public void iniciar() {
        int opcion = -1;
        System.out.println("====================================================");
        System.out.println("   SISTEMA DE GESTIÓN DE INVENTARIO DE TIENDA");
        System.out.println("====================================================");

        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEntero("Selecciona una opción: ");
            ejecutarOpcion(opcion);
        }
        scanner.close();
    }

    public void mostrarMenu() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Registrar nuevo producto");
        System.out.println("2. Buscar producto por código");
        System.out.println("3. Autocompletar producto por nombre (Trie)");
        System.out.println("4. Modificar stock de un producto");
        System.out.println("5. Eliminar producto");
        System.out.println("6. Listar categorías disponibles");
        System.out.println("7. Reporte: Productos disponibles por categoría");
        System.out.println("8. Reporte: Top 3 productos más caros");
        System.out.println("9. Reporte: Valor total del inventario por categoría");
        System.out.println("10. Reporte: Producto con menor stock");
        System.out.println("11. Ver todos los productos");
        System.out.println("12. Deshacer última operación (Undo)");
        System.out.println("0. Salir");
    }

    public void ejecutarOpcion(int op) {
        switch (op) {
            case 1 -> opcionRegistrarProducto();
            case 2 -> opcionBuscarPorCodigo();
            case 3 -> opcionAutocompletar();
            case 4 -> opcionModificarStock();
            case 5 -> opcionEliminarProducto();
            case 6 -> opcionListarCategorias();
            case 7 -> opcionReportePorCategoria();
            case 8 -> opcionTopMasCaros();
            case 9 -> opcionValorPorCategoria();
            case 10 -> opcionMenorStock();
            case 11 -> opcionListarTodos();
            case 12 -> opcionDeshacer();
            case 0 -> System.out.println("Saliendo del sistema. ¡Hasta luego!");
            default -> System.out.println("Opción no válida. Intenta con un número del 0 al 12.");
        }
    }

    private void opcionRegistrarProducto() {
        System.out.println("\n--- Registrar Producto ---");
        String codigo = leerTextoNoVacio("Código (ej. P016): ").toUpperCase();
        if (inventario.buscarPorCodigo(codigo) != null) {
            System.out.println("Error: Ya existe un producto con el código " + codigo);
            return;
        }

        String nombre = leerTextoNoVacio("Nombre: ");
        String categoria = leerTextoNoVacio("Categoría: ");
        double precio = leerDoublePositivo("Precio: $");
        int stock = leerEnteroNoNegativo("Stock inicial: ");

        Producto nuevo = new Producto(codigo, nombre, categoria, precio, stock);
        if (inventario.registrarProducto(nuevo)) {
            historial.registrarAccion("AGREGAR", nuevo);
            consultas.indexarNombre(nuevo.getNombre());
            System.out.println("Producto registrado exitosamente: " + nuevo);
        } else {
            System.out.println("No se pudo registrar el producto.");
        }
    }

    private void opcionBuscarPorCodigo() {
        String codigo = leerTextoNoVacio("Ingresa el código del producto: ");
        Producto encontrado = inventario.buscarPorCodigo(codigo);
        if (encontrado != null) {
            System.out.println("Encontrado: " + encontrado);
        } else {
            System.out.println("No se encontró ningún producto con el código: " + codigo.toUpperCase());
        }
    }

    private void opcionAutocompletar() {
        String prefijo = leerTextoNoVacio("Escribe las primeras letras del nombre: ");
        List<String> coincidencias = consultas.autocompletarPorNombre(prefijo);
        if (coincidencias.isEmpty()) {
            System.out.println("No hay productos que empiecen con '" + prefijo + "'.");
        } else {
            System.out.println("Sugerencias encontradas:");
            coincidencias.forEach(nombre -> System.out.println(" - " + nombre));
        }
    }

    private void opcionModificarStock() {
        String codigo = leerTextoNoVacio("Código del producto a actualizar: ");
        Producto p = inventario.buscarPorCodigo(codigo);
        if (p == null) {
            System.out.println("Producto no encontrado.");
            return;
        }

        System.out.println("Producto actual: " + p);
        int delta = leerEntero("Cantidad a sumar o restar (ej. 5 o -3): ");

        Producto estadoPrevio = new Producto(p.getCodigo(), p.getNombre(), p.getCategoria(), p.getPrecio(), p.getStock());

        if (inventario.modificarStock(codigo, delta)) {
            historial.registrarAccion("STOCK", estadoPrevio);
            System.out.println("Stock actualizado. Nuevo estado: " + p);
        } else {
            System.out.println("Operación inválida: El stock no puede quedar en números negativos.");
        }
    }

    private void opcionEliminarProducto() {
        String codigo = leerTextoNoVacio("Código del producto a eliminar: ");
        Producto eliminado = inventario.eliminarProducto(codigo);
        if (eliminado != null) {
            historial.registrarAccion("ELIMINAR", eliminado);
            System.out.println("Producto eliminado: " + eliminado);
        } else {
            System.out.println("No se encontró ningún producto con ese código.");
        }
    }

    private void opcionListarCategorias() {
        Set<String> categorias = inventario.obtenerTodasLasCategorias();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías registradas.");
        } else {
            System.out.println("Categorías disponibles (" + categorias.size() + "):");
            categorias.forEach(cat -> System.out.println(" * " + cat));
        }
    }

    private void opcionReportePorCategoria() {
        String cat = leerTextoNoVacio("Ingresa la categoría a consultar: ");
        List<String> lista = consultas.productosPorCategoria(cat);
        if (lista.isEmpty()) {
            System.out.println("No hay productos con stock disponible en la categoría '" + cat + "'.");
        } else {
            System.out.println("Productos disponibles en '" + cat + "':");
            lista.forEach(System.out::println);
        }
    }

    private void opcionTopMasCaros() {
        List<Producto> top = consultas.topProductosMasCaros(3);
        if (top.isEmpty()) {
            System.out.println("El inventario está vacío.");
        } else {
            System.out.println("--- TOP 3 PRODUCTOS MÁS CAROS ---");
            for (int i = 0; i < top.size(); i++) {
                System.out.println((i + 1) + ". " + top.get(i));
            }
        }
    }

    private void opcionValorPorCategoria() {
        Map<String, Double> totales = consultas.valorTotalPorCategoria();
        if (totales.isEmpty()) {
            System.out.println("No hay datos para calcular.");
        } else {
            System.out.println("--- VALOR TOTAL DEL INVENTARIO POR CATEGORÍA ---");
            totales.forEach((categoria, total) ->
                    System.out.printf(" - %-18s : $%,.2f%n", categoria, total));
        }
    }

    private void opcionMenorStock() {
        Optional<Producto> menor = consultas.productoConMenorStock();
        menor.ifPresentOrElse(
                p -> System.out.println("Producto con menor stock: " + p),
                () -> System.out.println("El inventario está vacío.")
        );
    }

    private void opcionListarTodos() {
        List<Producto> todos = inventario.obtenerListaProductos();
        if (todos.isEmpty()) {
            System.out.println("No hay productos registrados.");
        } else {
            System.out.println("--- INVENTARIO COMPLETO (" + todos.size() + " artículos) ---");
            todos.forEach(System.out::println);
        }
    }

    private void opcionDeshacer() {
        String resultado = historial.deshacerUltimaAccion(inventario);
        consultas.sincronizarArbol();
        System.out.println(resultado);
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor ingresa un número entero.");
            }
        }
    }

    private int leerEnteroNoNegativo(String mensaje) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= 0) {
                return valor;
            }
            System.out.println("El valor no puede ser negativo.");
        }
    }

    private double leerDoublePositivo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                double valor = Double.parseDouble(scanner.nextLine().trim());
                if (valor > 0) {
                    return valor;
                }
                System.out.println("El precio debe ser mayor a 0.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor ingresa un número decimal válido.");
            }
        }
    }

    private String leerTextoNoVacio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("Este campo no puede estar vacío.");
        }
    }

    public void cargarDatosIniciales() {
        inventario.registrarProducto(new Producto("P001", "Laptop Lenovo IdeaPad", "Electrónica", 14500.00, 5));
        inventario.registrarProducto(new Producto("P002", "Mouse Inalámbrico Logitech", "Electrónica", 450.00, 20));
        inventario.registrarProducto(new Producto("P003", "Teclado Mecánico Redragon", "Electrónica", 1100.00, 12));
        inventario.registrarProducto(new Producto("P004", "Monitor Samsung 24 pulgadas", "Electrónica", 3200.00, 7));
        inventario.registrarProducto(new Producto("P005", "Audífonos Sony WH-CH520", "Electrónica", 1250.00, 9));
        inventario.registrarProducto(new Producto("P006", "Cuaderno Profesional Raya", "Papelería", 45.00, 50));
        inventario.registrarProducto(new Producto("P007", "Pluma Gel Negra Bic", "Papelería", 18.00, 100));
        inventario.registrarProducto(new Producto("P008", "Paquete 500 Hojas Blancas", "Papelería", 135.00, 30));
        inventario.registrarProducto(new Producto("P009", "Marcadores Permanentes 4 pzas", "Papelería", 85.00, 15));
        inventario.registrarProducto(new Producto("P010", "Café Soluble Nescafé 200g", "Abarrotes", 115.00, 18));
        inventario.registrarProducto(new Producto("P011", "Galletas Marías Gamesa", "Abarrotes", 22.00, 40));
        inventario.registrarProducto(new Producto("P012", "Leche Entera Alpura 1L", "Abarrotes", 28.50, 25));
        inventario.registrarProducto(new Producto("P013", "Silla Ergonómica Oficina", "Muebles", 2800.00, 4));
        inventario.registrarProducto(new Producto("P014", "Escritorio Minimalista Madera", "Muebles", 3500.00, 3));
        inventario.registrarProducto(new Producto("P015", "Lámpara de Escritorio LED", "Muebles", 390.00, 10));
    }
}