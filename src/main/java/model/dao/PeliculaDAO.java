package model.dao;

import model.entities.Pelicula;
import model.factory.dbFactory;
import model.motorsql.MotorSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PeliculaDAO implements DAO<Pelicula, Integer> {

    private MotorSQL motorSQL;

    public PeliculaDAO(String db) {
        motorSQL = dbFactory.getDataBase(db);
    }

    @Override
    public int add(Pelicula bean) {
        int resp = 0;
        String sql = "INSERT INTO PELICULA (titulo, genero) VALUES ('" +
                bean.getTitulo() + "', '" +
                bean.getGenero() + "')";
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
        String sql = "DELETE FROM PELICULA WHERE id = " + id;
        try {
            motorSQL.connect();
            resp = motorSQL.execute(sql);
        } finally {
            motorSQL.disconnect();
        }
        return resp;
    }

    @Override
    public int update(Pelicula bean) {
        int resp = 0;
        String sql = "UPDATE PELICULA SET titulo = '" + bean.getTitulo() +
                "', genero = '" + bean.getGenero() +
                "' WHERE id = " + bean.getId();
        try {
            motorSQL.connect();
            resp = motorSQL.execute(sql);
        } finally {
            motorSQL.disconnect();
        }
        return resp;
    }

    @Override
    public ArrayList<Pelicula> findAll(Pelicula bean) {
        ArrayList<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT id, titulo, genero FROM PELICULA";
        if (bean != null && bean.getGenero() != null && !bean.getGenero().isEmpty()) {
            sql += " WHERE genero ILIKE '%" + bean.getGenero() + "%'"; // ILIKE para búsqueda insensible a mayúsculas/minúsculas en PostgreSQL
        }
        try {
            motorSQL.connect();
            ResultSet rs = motorSQL.executeQuery(sql);
            while (rs.next()) {
                Pelicula pelicula = new Pelicula();
                pelicula.setId(rs.getInt("id"));
                pelicula.setTitulo(rs.getString("titulo"));
                pelicula.setGenero(rs.getString("genero"));
                peliculas.add(pelicula);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            motorSQL.disconnect();
        }
        return peliculas;
    }

//Metodo buscador
    public ArrayList<String> findTitlesByPartialMatch(String partialTitle) {
        ArrayList<String> titles = new ArrayList<>();
        String sql = "SELECT DISTINCT titulo FROM PELICULA";
        if (partialTitle != null && !partialTitle.isEmpty()) {
            sql += " WHERE titulo ILIKE '%" + partialTitle + "%'"; // ILIKE para búsqueda insensible a mayúsculas/minúsculas
        }
        sql += " ORDER BY titulo ASC LIMIT 10"; // Limitar a 10 sugerencias para el autocompletado
        try {
            motorSQL.connect();
            ResultSet rs = motorSQL.executeQuery(sql);
            while (rs.next()) {
                titles.add(rs.getString("titulo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            motorSQL.disconnect();
        }
        return titles;
    }


    // Método específico para listar todos los géneros (opcional, pero útil)
    public ArrayList<String> findAllGeneros() {
        ArrayList<String> generos = new ArrayList<>();
        String sql = "SELECT DISTINCT genero FROM PELICULA ORDER BY genero";
        try {
            motorSQL.connect();
            ResultSet rs = motorSQL.executeQuery(sql);
            while (rs.next()) {
                generos.add(rs.getString("genero"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            motorSQL.disconnect();
        }
        return generos;
    }
}