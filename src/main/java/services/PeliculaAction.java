package services;

import model.dao.PeliculaDAO;
import model.entities.Pelicula;
import model.factory.dbFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import com.google.gson.Gson; // Asegúrate de tener este import si no lo tienes

public class PeliculaAction implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String resp = "";
        String action = request.getParameter("ACTION");

        if (action == null || !action.contains(".")) {
            return "{\"error\": \"Parámetro ACTION inválido o faltante en PeliculaAction.\"}";
        }

        String[] arrayAction = action.split("\\.");

        if (arrayAction.length < 2) {
            return "{\"error\": \"Parámetro ACTION incompleto en PeliculaAction.\"}";
        }

        switch (arrayAction[1].toUpperCase()) {
            case "FINDALL":
                resp = findAll(request);
                break;
            case "FINDALLGENEROS":
                resp = findAllGeneros();
                break;
            case "FINDTITLES": // <--- NUEVO CASO PARA EL AUTOCOMPLETADO
                resp = findTitles(request);
                break;
            default:
                resp = "{\"error\": \"Sub-acción de PELICULA no reconocida.\"}";
                break;
        }
        return resp;
    }

    private String findAll(HttpServletRequest req) {
        PeliculaDAO peliculaDAO = new PeliculaDAO(dbFactory.POSTGRE);
        String genero = req.getParameter("GENERO");

        Pelicula filtroPelicula = null;
        if (genero != null && !genero.isEmpty() && !genero.equalsIgnoreCase("TODOS")) {
            filtroPelicula = new Pelicula();
            filtroPelicula.setGenero(genero);
        }

        ArrayList<Pelicula> peliculas = peliculaDAO.findAll(filtroPelicula);
        return Pelicula.fromArrayToJson(peliculas);
    }

    private String findAllGeneros() {
        PeliculaDAO peliculaDAO = new PeliculaDAO(dbFactory.POSTGRE);
        ArrayList<String> generos = peliculaDAO.findAllGeneros();
        return new Gson().toJson(generos);
    }

    // <--- NUEVO MÉTODO PARA EL AUTOCOMPLETADO
    private String findTitles(HttpServletRequest req) {
        PeliculaDAO peliculaDAO = new PeliculaDAO(dbFactory.POSTGRE);
        String partialTitle = req.getParameter("QUERY"); // El texto que el usuario está escribiendo

        ArrayList<String> titles = peliculaDAO.findTitlesByPartialMatch(partialTitle);
        return new Gson().toJson(titles); // Devuelve la lista de títulos como JSON
    }
}