package services;

import model.dao.UsuarioDAO;
import model.entities.Usuario;
import model.factory.dbFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Importar HttpSession

public class UsuarioAction implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String cadUser = "";
        String action = request.getParameter("ACTION");

        // Por si acaso el parámetro viene mal o es null:
        if (action == null || !action.contains(".")) {
            return "{\"error\": \"ACTION parameter is null or malformed.\"}"; // Devuelve JSON de error
        }

        String[] arrayAction = action.split("\\.");

        if (arrayAction.length < 2) { // Asegúrate de que hay un segundo elemento (ej. LOGIN)
            return "{\"error\": \"ACTION parameter is incomplete.\"}"; // Devuelve JSON de error
        }

        switch (arrayAction[1].toUpperCase()) {
            case "LOGIN":
                cadUser = login(request);
                break;
            case "LOGOUT": // Añadimos una acción de logout
                cadUser = logout(request);
                break;
            default:
                cadUser = "{\"error\": \"Sub-acción de USUARIO no reconocida.\"}";
                break;
        }

        return cadUser;
    }


    public String login(HttpServletRequest req) {
        String user = req.getParameter("USER");
        String password = req.getParameter("PASSWORD");

        if (user == null || password == null || user.trim().isEmpty() || password.trim().isEmpty()) {
            // Devuelve un JSON de error más específico para el frontend
            return "{\"success\": false, \"message\": \"Usuario y contraseña no pueden estar vacíos.\"}";
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO(dbFactory.POSTGRE);
        Usuario usuario = usuarioDAO.login(user, password);

        if (usuario != null) {
            // LOGIN EXITOSO: Guardar el nombre de usuario en la sesión HTTP del servidor
            HttpSession session = req.getSession(); // Obtener o crear la sesión
            session.setAttribute("loggedInUser", usuario.getUser()); // Guarda el username
            System.out.println("Usuario " + usuario.getUser() + " logueado en sesión."); // Para depuración

            // Devolver éxito y el usuario (aunque el frontend ya lo tiene en localStorage)
            return "{\"success\": true, \"User\": \"" + usuario.getUser() + "\", \"message\": \"Login exitoso.\"}";
        } else {
            // LOGIN FALLIDO
            return "{\"success\": false, \"message\": \"Usuario o contraseña incorrectos.\"}";
        }
    }

    public String logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false); // No crear una nueva sesión si no existe
        if (session != null) {
            String loggedOutUser = (String) session.getAttribute("loggedInUser");
            session.invalidate(); // Invalida la sesión actual
            System.out.println("Usuario " + loggedOutUser + " ha cerrado sesión."); // Para depuración
            return "{\"success\": true, \"message\": \"Sesión cerrada con éxito.\"}";
        }
        return "{\"success\": false, \"message\": \"No hay sesión activa para cerrar.\"}";
    }
}