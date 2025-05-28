package model.entities;

import com.google.gson.Gson;
import java.sql.Timestamp; // Para manejar la fecha
import java.util.ArrayList;

public class Comentario {
    private int id;
    private int peliculaId;
    private String usuarioUsername; // El nombre de usuario que hace el comentario
    private String texto;
    private Timestamp fechaCreacion;

    // Constructor vacío
    public Comentario() {
    }

    // Constructor para añadir (sin id y fecha_creacion)
    public Comentario(int peliculaId, String usuarioUsername, String texto) {
        this.peliculaId = peliculaId;
        this.usuarioUsername = usuarioUsername;
        this.texto = texto;
    }

    // Constructor completo
    public Comentario(int id, int peliculaId, String usuarioUsername, String texto, Timestamp fechaCreacion) {
        this.id = id;
        this.peliculaId = peliculaId;
        this.usuarioUsername = usuarioUsername;
        this.texto = texto;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPeliculaId() {
        return peliculaId;
    }

    public void setPeliculaId(int peliculaId) {
        this.peliculaId = peliculaId;
    }

    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Método para convertir un objeto Comentario a JSON
    public static String fromObjectToJSON(Comentario comentario) {
        Gson gson = new Gson();
        return gson.toJson(comentario);
    }

    // Método para convertir una lista de Comentarios a JSON
    public static String fromArrayToJson(ArrayList<Comentario> comentarios) {
        Gson gson = new Gson();
        return gson.toJson(comentarios);
    }
}