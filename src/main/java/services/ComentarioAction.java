package services;

import com.google.gson.Gson;
import model.dao.ComentarioDAO;
import model.entities.Comentario;
import model.factory.dbFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;

public class ComentarioAction implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String resp = "";
        String action = request.getParameter("ACTION");

        if (action == null || !action.contains(".")) {
            return "{\"success\": false, \"message\": \"Parámetro ACTION inválido o faltante en ComentarioAction.\"}"; // Consistente con JSON
        }

        String[] arrayAction = action.split("\\.");

        if (arrayAction.length < 2) {
            return "{\"success\": false, \"message\": \"Parámetro ACTION incompleto en ComentarioAction.\"}"; // Consistente con JSON
        }

        switch (arrayAction[1].toUpperCase()) {
            case "ADD":
                resp = addComentario(request);
                break;
            case "FINDALLBYPELICULA":
                resp = findAllByPelicula(request);
                break;
            default:
                resp = "{\"success\": false, \"message\": \"Sub-acción de COMENTARIO no reconocida.\"}"; // Consistente con JSON
                break;
        }
        return resp;
    }

    private String addComentario(HttpServletRequest req) {
        HttpSession session = req.getSession(false); // Usar false para no crear una nueva sesión si no existe
        String loggedInUsername = null;
        if (session != null) {
            loggedInUsername = (String) session.getAttribute("loggedInUser");
        }

        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            return "{\"success\": false, \"message\": \"Debes iniciar sesión para comentar.\"}";
        }

        try {
            int peliculaId = Integer.parseInt(req.getParameter("PELICULA_ID"));
            String texto = req.getParameter("TEXTO");

            if (texto == null || texto.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"El texto del comentario no puede estar vacío.\"}";
            }

            Comentario comentario = new Comentario(peliculaId, loggedInUsername, texto);
            ComentarioDAO comentarioDAO = new ComentarioDAO(dbFactory.POSTGRE);
            int rowsAffected = comentarioDAO.add(comentario);

            if (rowsAffected > 0) {
                return "{\"success\": true, \"message\": \"Comentario añadido con éxito.\"}";
            } else {
                return "{\"success\": false, \"message\": \"No se pudo añadir el comentario.\"}";
            }
        } catch (NumberFormatException e) {
            return "{\"success\": false, \"message\": \"ID de película inválido.\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"Error interno al añadir comentario: " + e.getMessage() + "\"}";
        }
    }

    private String findAllByPelicula(HttpServletRequest req) {
        try {
            int peliculaId = Integer.parseInt(req.getParameter("PELICULA_ID"));
            ComentarioDAO comentarioDAO = new ComentarioDAO(dbFactory.POSTGRE);
            Comentario filtro = new Comentario();
            filtro.setPeliculaId(peliculaId);
            ArrayList<Comentario> comentarios = comentarioDAO.findAll(filtro);
            // Si no hay comentarios, devolver un array vacío o un mensaje
            if (comentarios.isEmpty()) {
                return "[]"; // Array JSON vacío
            }
            return Comentario.fromArrayToJson(comentarios);
        } catch (NumberFormatException e) {
            return "{\"success\": false, \"message\": \"ID de película inválido.\"}"; // Consistente con JSON
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"Error interno al obtener comentarios: " + e.getMessage() + "\"}"; // Consistente con JSON
        }
    }
}