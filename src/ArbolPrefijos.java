import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArbolPrefijos {

    private static class NodoTrie {
        Map<Character, NodoTrie> hijos = new HashMap<>();
        boolean esFinDePalabra = false;
        String nombreOriginal = null;
    }

    private final NodoTrie raiz;

    public ArbolPrefijos() {
        this.raiz = new NodoTrie();
    }

    public void insertar(String palabra) {
        if (palabra == null || palabra.trim().isEmpty()) {
            return;
        }
        String limpia = palabra.trim();
        String minusculas = limpia.toLowerCase();
        NodoTrie actual = raiz;

        for (char c : minusculas.toCharArray()) {
            actual.hijos.putIfAbsent(c, new NodoTrie());
            actual = actual.hijos.get(c);
        }
        actual.esFinDePalabra = true;
        actual.nombreOriginal = limpia;
    }

    public List<String> buscarPorPrefijo(String prefijo) {
        List<String> resultados = new ArrayList<>();
        if (prefijo == null || prefijo.trim().isEmpty()) {
            return resultados;
        }

        String busqueda = prefijo.trim().toLowerCase();
        NodoTrie actual = raiz;

        for (char c : busqueda.toCharArray()) {
            actual = actual.hijos.get(c);
            if (actual == null) {
                return resultados;
            }
        }

        recolectarPalabras(actual, resultados);
        return resultados;
    }

    private void recolectarPalabras(NodoTrie nodo, List<String> resultados) {
        if (nodo.esFinDePalabra && nodo.nombreOriginal != null) {
            resultados.add(nodo.nombreOriginal);
        }
        for (NodoTrie hijo : nodo.hijos.values()) {
            recolectarPalabras(hijo, resultados);
        }
    }
}