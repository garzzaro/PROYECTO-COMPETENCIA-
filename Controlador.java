import Modelo.*;
import java.sql.*;

public class Controlador {

    public Controlador() {
    }

    /**
     * Intentar login; si el usuario no existe se registra automáticamente.
     * Retorna el objeto Usuario (en memoria) o null si falla.
     */
    public Usuario loginOrRegister(String nombre, String correo, String carne, String contra) throws SQLException {
        try (Connection cn = BD.conectar()) {
            final String checkSQL = "SELECT * FROM usuarios WHERE correo=? OR carne=?";
            try (PreparedStatement check = cn.prepareStatement(checkSQL)) {
                check.setString(1, correo);
                check.setString(2, carne);
                ResultSet rs = check.executeQuery();

                if (rs.next()) {
                    String passBD = rs.getString("password");
                    if (passBD.equals(contra)) {
                        // crear objeto Usuario en memoria
                        return new Usuario(rs.getString("nombre"), rs.getString("correo"), contra, rs.getString("carne"));
                    } else {
                        return null; // contraseña incorrecta
                    }
                } else {
                    // registrar
                    final String insertSQL = "INSERT INTO usuarios(nombre, correo, carne, password) VALUES(?,?,?,?)";
                    try (PreparedStatement ps = cn.prepareStatement(insertSQL)) {
                        ps.setString(1, nombre);
                        ps.setString(2, correo);
                        ps.setString(3, carne);
                        ps.setString(4, contra);
                        ps.executeUpdate();
                        return new Usuario(nombre, correo, contra, carne);
                    }
                }
            }
        }
    }

    /**
     * Crea un grupo en la BD y lo asigna como admin y participante al usuario.
     * Retorna el objeto Grupo (en memoria) o lanza SQLException.
     */
    public Grupo crearGrupo(Usuario usuario, String nombreGrupo) throws SQLException {
        if (usuario == null) throw new IllegalArgumentException("Usuario no puede ser null.");
        try (Connection cn = BD.conectar()) {
            // Insert grupo
            String sqlGrupo = "INSERT INTO grupos(nombre) VALUES(?)";
            PreparedStatement psGrupo = cn.prepareStatement(sqlGrupo, Statement.RETURN_GENERATED_KEYS);
            psGrupo.setString(1, nombreGrupo);
            psGrupo.executeUpdate();
            ResultSet gen = psGrupo.getGeneratedKeys();
            if (!gen.next()) throw new SQLException("No se obtuvo id de grupo.");
            int grupoID = gen.getInt(1);

            // obtener id de usuario
            int usuarioID = obtenerIDUsuario(usuario.getCorreo(), cn);

            // Insert participante
            String sqlP = "INSERT INTO grupo_participantes(grupo_id, usuario_id) VALUES(?,?)";
            try (PreparedStatement psP = cn.prepareStatement(sqlP)) {
                psP.setInt(1, grupoID);
                psP.setInt(2, usuarioID);
                psP.executeUpdate();
            }

            // Insert admin
            String sqlA = "INSERT INTO grupo_admin(grupo_id, usuario_id) VALUES(?,?)";
            try (PreparedStatement psA = cn.prepareStatement(sqlA)) {
                psA.setInt(1, grupoID);
                psA.setInt(2, usuarioID);
                psA.executeUpdate();
            }

            // retornar objeto Grupo en memoria (solo con nombre)
            return new Grupo(nombreGrupo);
        }
    }

    /**
     * Añade usuario a grupo como participante (no admin).
     */
    public boolean unirseAGrupo(Usuario usuario, int grupoID) throws SQLException {
        if (usuario == null) throw new IllegalArgumentException("Usuario no puede ser null.");
        try (Connection cn = BD.conectar()) {
            int usuarioID = obtenerIDUsuario(usuario.getCorreo(), cn);

            // comprobar duplicado
            PreparedStatement check = cn.prepareStatement(
                "SELECT * FROM grupo_participantes WHERE grupo_id=? AND usuario_id=?"
            );
            check.setInt(1, grupoID);
            check.setInt(2, usuarioID);
            ResultSet rs = check.executeQuery();
            if (rs.next()) return false;

            PreparedStatement join = cn.prepareStatement(
                "INSERT INTO grupo_participantes(grupo_id, usuario_id) VALUES(?,?)"
            );
            join.setInt(1, grupoID);
            join.setInt(2, usuarioID);
            join.executeUpdate();
            return true;
        }
    }

    private int obtenerIDUsuario(String correo, Connection cn) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT id FROM usuarios WHERE correo=?");
        ps.setString(1, correo);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id");
        throw new SQLException("Usuario no encontrado para correo: " + correo);
    }
}
