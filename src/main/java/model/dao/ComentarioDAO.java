package model.dao;

import model.entities.Comentario;
import model.factory.dbFactory;
import model.motorsql.MotorSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ComentarioDAO implements DAO<Comentario, Integer> {

    private MotorSQL motorSQL;

    public ComentarioDAO(String db) {
        motorSQL = dbFactory.getDataBase(db);
    }

    @Override
    public int add(Comentario bean) {
        int resp = 0;
        String sql = "INSERT INTO COMENTARIO (pelicula_id, usuario_username, texto) VALUES (" +
                bean.getPeliculaId() + ", '" +
                bean.getUsuarioUsername() + "', '" +
                bean.getTexto() + "')";
        try {
            motorSQL.connect();
            resp = motorSQL.execute(sql);
        } finally {
            motorSQL.disconnect();
        }
        return resp;
    }

    @Override
    public int delete(Integer id) {
        int resp = 0;
        String sql = "DELETE FROM COMENTARIO WHERE id = " + id;
        try {
            motorSQL.connect();
            resp = motorSQL.execute(sql);
        } finally {
            motorSQL.disconnect();
        }
        return resp;
    }

    @Override
    public int update(Comentario bean) {
        int resp = 0;
        String sql = "UPDATE COMENTARIO SET texto = '" + bean.getTexto() +
                "' WHERE id = " + bean.getId();
        try {
            motorSQL.connect();
            resp = motorSQL.execute(sql);
        } finally {
            motorSQL.disconnect();
        }
        return resp;
    }

    // findAll para comentarios, filtrando por pelicula_id
    @Override
    public ArrayList<Comentario> findAll(Comentario bean) {
        ArrayList<Comentario> comentarios = new ArrayList<>();
        String sql = "SELECT id, pelicula_id, usuario_username, texto, fecha_creacion FROM COMENTARIO";
        if (bean != null && bean.getPeliculaId() > 0) {
            sql += " WHERE pelicula_id = " + bean.getPeliculaId();
        }
        sql += " ORDER BY fecha_creacion DESC"; // Ordenar por fecha, los más recientes primero

        try {
            motorSQL.connect();
            ResultSet rs = motorSQL.executeQuery(sql);
            while (rs.next()) {
                Comentario comentario = new Comentario();
                comentario.setId(rs.getInt("id"));
                comentario.setPeliculaId(rs.getInt("pelicula_id"));
                comentario.setUsuarioUsername(rs.getString("usuario_username"));
                comentario.setTexto(rs.getString("texto"));
                comentario.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                comentarios.add(comentario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            motorSQL.disconnect();
        }
        return comentarios;
    }

    // Método para obtener un comentario por su ID (si es necesario para DELETE/UPDATE específico)
    public Comentario findById(Integer id) {
        Comentario comentario = null;
        String sql = "SELECT id, pelicula_id, usuario_username, texto, fecha_creacion FROM COMENTARIO WHERE id = " + id;
        try {
            motorSQL.connect();
            ResultSet rs = motorSQL.executeQuery(sql);
            if (rs.next()) {
                comentario = new Comentario();
                comentario.setId(rs.getInt("id"));
                comentario.setPeliculaId(rs.getInt("pelicula_id"));
                comentario.setUsuarioUsername(rs.getString("usuario_username"));
                comentario.setTexto(rs.getString("texto"));
                comentario.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            motorSQL.disconnect();
        }
        return comentario;
    }
}