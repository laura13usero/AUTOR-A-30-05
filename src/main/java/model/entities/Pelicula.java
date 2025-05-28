package model.entities;

import com.google.gson.Gson;
import java.util.ArrayList;

public class Pelicula {
    private int id;
    private String titulo;
    private String genero;

    // Constructor vacío (necesario para GSON y para crear objetos)
    public Pelicula() {
    }

    // Constructor con todos los campos (útil para crear objetos fácilmente)
    public Pelicula(int id, String titulo, String genero) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    // Método para convertir un objeto Pelicula a JSON
    public static String fromObjectToJSON(Pelicula pelicula) {
        Gson gson = new Gson();
        return gson.toJson(pelicula);
    }

    // Método para convertir una lista de Peliculas a JSON
    public static String fromArrayToJson(ArrayList<Pelicula> peliculas) {
        Gson gson = new Gson();
        return gson.toJson(peliculas);
    }
}
