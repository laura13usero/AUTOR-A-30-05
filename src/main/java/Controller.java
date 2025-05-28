

import services.UsuarioAction;
import services.PeliculaAction;
import services.ComentarioAction; // <--- AÑADE ESTA LÍNEA

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "Controller", urlPatterns = {"/Controller"})
public class Controller extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");

        PrintWriter out = response.getWriter();
        String action = request.getParameter("ACTION");

        if (action == null || !action.contains(".")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Parámetro ACTION inválido o faltante.\"}");
            return;
        }

        String[] arrayAction = action.split("\\.");

        switch (arrayAction[0].toUpperCase()) {
            case "USUARIO":
                UsuarioAction usuarioAction = new UsuarioAction();
                String respUser = usuarioAction.execute(request, response);
                out.print(respUser);
                break;

            case "PELICULA":
                PeliculaAction peliculaAction = new PeliculaAction();
                String respPelicula = peliculaAction.execute(request, response);
                out.print(respPelicula);
                break;

            case "COMENTARIO": // <--- NUEVO CASO
                ComentarioAction comentarioAction = new ComentarioAction();
                String respComentario = comentarioAction.execute(request, response);
                out.print(respComentario);
                break;

            default:
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Acción no reconocida.\"}");
                break;
        }
    }
}