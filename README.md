# proyecto-eya-haziel-ruben-sofia
# Guía de Trabajo Interna – Proyecto Integrador
**Proyecto:** Sistema de Gestión de Inventario de Tienda  
**Equipo:** Sofía Altamirano, Rubén Carretero y Haziel Larios  
**Materia:** Estructuras de Datos y Algoritmos Avanzados (Prof. MSC Josimar Reyes Campos)

> **Propósito de este documento:** Este `README.md` es nuestro mapa de ruta oficial. Aquí están las reglas de código, los comandos exactos de Git para no pisarnos el trabajo y el paso a paso de lo que cada quien debe construir en cada sesión.

---

## Paso 0: Configuración Inicial y Reglas del Equipo
Antes de tocar código, los tres debemos cumplir con estas reglas:

1. **Identidad en Git (Obligatorio para la calificación):** Cada quien debe hacer commits desde su propia cuenta para que el historial refleje nuestro trabajo individual. Verifiquen en su terminal:
   ```bash
   git config user.name "Tu Nombre"
   git config user.email "tu_correo@ejemplo.com"
   ```
2. **La Regla de Oro de Arquitectura:** Ninguna clase de las capas **Modelo** o **Datos** puede usar `Scanner` ni `System.out.println`. Solo la clase `App.java` interactúa con el usuario por consola.
3. **Estrategia de Ramas:** Nunca programamos directo en `main`. Cada quien trabaja en su rama `feature/...` partiendo de `dev`, y unimos todo en `dev` antes de pasarlo a `main`.

### Comandos para compilar y ejecutar el proyecto
Siempre desde la carpeta raíz del repositorio:
```bash
javac src/*.java
java -cp src App
```

---

## Paso 1: ¿Qué estamos construyendo? (Nuestra Visión)
1. **¿Qué guarda nuestro sistema?** Guarda **productos** con **código**, **nombre**, **categoría**, **precio** y **stock**, además de un historial de operaciones recientes.
2. **¿Qué puede hacer el usuario?** Puede **registrar** un producto, **buscarlo** por código (o autocompletar por nombre), **listar** las categorías disponibles, **consultar** reportes estadísticos y **deshacer** la última operación.
3. **¿Qué problema resuelve?** Evita el descontrol de existencias y agiliza la consulta de artículos y reportes en un comercio pequeño.

---

## Paso 2 y Paso 3: Arquitectura y Archivos del Proyecto
Así está dividido nuestro código en la carpeta `src/` para que nadie edite el mismo archivo al mismo tiempo:

| Clase (`src/`) | Capa | Atributos (`private`) | Métodos que debe tener |
| :--- | :--- | :--- | :--- |
| `Producto.java` | **Modelo** | `codigo` (`String`), `nombre` (`String`), `categoria` (`String`), `precio` (`double`), `stock` (`int`) | `actualizarStock(int cantidad)`, `getValorTotalInventario()`, `getters/setters`, `toString()` |
| `Operacion.java` | **Modelo** | `tipo` (`String`: `"AGREGAR"` / `"ELIMINAR"`), `productoAfectado` (`Producto`) | `getTipo()`, `getProductoAfectado()` |
| `Inventario.java` | **Datos** | `productosPorCodigo` (`Map<String, Producto>`), `categoriasDisponibles` (`Set<String>`) | `registrarProducto(Producto p)`, `buscarPorCodigo(String codigo)`, `eliminarProducto(String codigo)`, `obtenerTodasLasCategorias()`, `obtenerListaProductos()` |
| `HistorialOperaciones.java` | **Datos** | `pilaAcciones` (`Deque<Operacion>`) | `registrarAccion(Operacion op)`, `deshacerUltimaAccion(Inventario inv)`, `hayAccionesPendientes()` |
| `ConsultasInventario.java` | **Consultas** | `inventario` (`Inventario`), `arbolPrefijos` (`ArbolPrefijos`) | `productosPorCategoria(String cat)`, `topProductosMasCaros(int limite)`, `valorTotalPorCategoria()`, `productoConMenorStock()` |
| `ArbolPrefijos.java` *(Extra)* | **Consultas** | `raiz` (`NodoTrie`) | `insertar(String palabra)`, `buscarPorPrefijo(String prefijo)` |
| `App.java` | **Interfaz** | `scanner` (`Scanner`), `inventario`, `historial`, `consultas` | `main(String[] args)`, `cargarDatosIniciales()`, `mostrarMenu()`, `ejecutarOpcion(int op)` |

---

## Paso 4: Nuestras Estructuras de Datos (Respuestas clave para la defensa)
En la defensa individual el profesor nos preguntará por qué elegimos cada estructura. Todos debemos dominar esta tabla:

1. **`Map<String, Producto>` (`HashMap`) en `Inventario.java`**
   * **Uso:** Guarda cada producto usando su `codigo` como clave.
   * **Justificación:** Nos permite buscar, actualizar o eliminar un producto en tiempo constante $O(1)$ sin tener que recorrer toda la lista con un `for`.
2. **`Set<String>` (`HashSet`) en `Inventario.java`**
   * **Uso:** Almacena las categorías únicas existentes en la tienda.
   * **Justificación:** Evita categorías duplicadas automáticamente sin que tengamos que programar validaciones manuales con `if` cada vez que entra un producto.
3. **`Deque<Operacion>` (`ArrayDeque`) en `HistorialOperaciones.java`**
   * **Uso:** Funciona como una Pila (LIFO) para guardar las últimas acciones realizadas.
   * **Justificación:** Para la función de "Deshacer" (*Undo*), necesitamos extraer exactamente la última operación que entró (`push` y `pop`).
4. **Estructura Extra (+10 pts): `Trie` (`ArbolPrefijos.java`)**
   * **Uso:** Indexa los nombres de los productos letra por letra.
   * **Justificación:** Permite autocompletar búsquedas cuando el usuario solo recuerda las primeras letras del nombre del artículo.

---

## Paso 5: Las 3 Consultas con Streams que debemos implementar
En `ConsultasInventario.java` no usamos ciclos `for` tradicionales para reportes; usamos Streams y lambdas:

1. **Filtrado y ordenamiento por categoría (`productosPorCategoria`):**
   * Filtra productos de una categoría con `stock > 0`, los ordena alfabéticamente por nombre y los transforma a texto (`filter` $\rightarrow$ `sorted` $\rightarrow$ `map` $\rightarrow$ `toList`).
2. **Top 3 artículos más caros (`topProductosMasCaros`):**
   * Ordena los productos de mayor a menor precio y toma los primeros 3 (`sorted` con `Comparator.comparingDouble(...).reversed()` $\rightarrow$ `limit(3)` $\rightarrow$ `toList`).
3. **Agrupación financiera y búsqueda segura (`valorTotalPorCategoria` y `productoConMenorStock`):**
   * Agrupa por categoría y suma `precio * stock` (`Collectors.groupingBy` + `Collectors.summingDouble`).
   * Devuelve un `Optional<Producto>` con el artículo de menor stock usando `min(Comparator.comparingInt(Producto::getStock))`.

---

## Paso 6: Asignación de Responsabilidades por Integrante

### 👩‍💻 Sofía – Capa Modelo y Datos Iniciales
* **Tus archivos:** `src/Producto.java`, `src/Operacion.java` y el método `cargarDatosIniciales()` (con mínimo 15 productos variados y realistas).
* **Tu rama en Git:** `feature/sofia-modelo`

### 👨‍💻 Rubén – Capa Datos y Estructuras Core
* **Tus archivos:** `src/Inventario.java` (`HashMap` + `HashSet`) y `src/HistorialOperaciones.java` (`ArrayDeque`).
* **Tu rama en Git:** `feature/ruben-estructuras`

### 👨‍💻 Haziel – Capa Consultas (Streams/Trie) e Interfaz
* **Tus archivos:** `src/ConsultasInventario.java`, `src/ArbolPrefijos.java` (extra) y el menú interactivo con validaciones en `src/App.java`.
* **Tu rama en Git:** `feature/haziel-streams-menu`

---

## Paso 7: Guía Paso a Paso de Comandos Git para Trabajar en Paralelo

### A. Crear el esqueleto inicial (Solo lo hace uno tras la aprobación del diseño)
```bash
mkdir src
touch src/Producto.java src/Operacion.java src/Inventario.java src/HistorialOperaciones.java src/ConsultasInventario.java src/ArbolPrefijos.java src/App.java
javac src/*.java
git checkout -b dev
git add README.md src/
git commit -m "chore: agrega guia del equipo y esqueleto de clases"
git push -u origin dev
```

### B. Flujo diario de cada integrante (Sofía, Rubén y Haziel)
Cada vez que vayan a programar su parte, sigan exactamente estos comandos:

```bash
# 1. Traer lo más reciente de la rama de integración (dev)
git checkout dev
git pull origin dev

# 2. Crear o cambiar a tu rama personal
git checkout -b feature/tu-nombre-tarea   # (o sin -b si ya la creaste antes)
git merge dev

# 3. ... Programar tus archivos asignados y probar que compilen con: javac src/*.java ...

# 4. Guardar y subir tus avances a GitHub
git add src/
git commit -m "feat: descripcion clara de lo que programaste"
git push origin feature/tu-nombre-tarea
```
*(Cuando tu parte esté lista y probada, haz un Pull Request en GitHub hacia `dev` para que los demás la descarguen con `git pull origin dev`).*

---

## Paso 8: Checklist de Avance por Sesiones (Semanas 14 a 16)

### 📅 Semana 14 – Construcción
* [ ] **Sesión 1 (Diseño y Esqueleto):** Profesor aprueba el diseño $\rightarrow$ subimos `src/` vacío a `dev` $\rightarrow$ los 3 hacemos `git pull`.
* [ ] **Sesión 2 (Sprint 1 - Núcleo):**
  * [ ] Sofía termina `Producto.java`, `Operacion.java` y los 15 registros de prueba.
  * [ ] Rubén termina `Inventario.java` y `HistorialOperaciones.java`.
  * [ ] Probamos en `dev` que los 15 productos se cargan en el `Map` y el `Set`.
* [ ] **Sesión 3 (Sprint 2 - Lógica y Menú):**
  * [ ] Haziel termina `ConsultasInventario.java` (las 3 consultas Streams) y el menú en `App.java`.
  * [ ] Conectamos el Trie (`ArbolPrefijos.java`) para puntos extra.

### 📅 Semana 15 – Blindaje y Cierre
* [ ] **Sesión 1 (Sprint 3 - Validaciones):** Los 3 probamos el menú metiendo letras en lugar de números, códigos inexistentes y opciones fuera de rango. Agregamos `try-catch` para que nunca se cierre con error.
* [ ] **Sesión 2 (Documentación y Revisión):** Verificamos con `git log --oneline --graph --all` que **Sofía, Rubén y Haziel** tengamos commits visibles.
* [ ] **Sesión 3 (Code Freeze):** Hacemos merge final de `dev` hacia `main`. Ya no se agregan funciones nuevas.

### 📅 Semana 16 – Defensa Final
* [ ] **Sesión 1 (Presentación):** Antes de entrar a clase, nos explicamos entre los 3 cómo funciona cada archivo (`Rubén` explica `Map`/`Deque` a `Sofía` y `Haziel`; `Haziel` explica los `Streams` a `Sofía` y `Rubén`; `Sofía` explica el `Modelo` y datos de prueba). ¡Todos debemos poder responder cualquier pregunta!