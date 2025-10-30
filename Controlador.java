import Modelo.*;
import java.util.ArrayList;

public class Controlador {
    private ArrayList<Usuario> usuarios;
    private ArrayList<Retos> retos;
    private Administrador admin;

    public Controlador(){
        usuarios = new ArrayList<>();
        retos = new ArrayList<>();
    }

    public boolean agregarUsuarioAdmin(String nombre, String correo, String carne){
        return admin != null && admin.registrarUsuario(nombre, correo, carne);
    }

    public boolean agregarRetoAdmin(String nombre, String descripcion, int puntos, boolean estado){
        return admin != null && admin.registrarReto(nombre, descripcion, puntos, estado);
    }

    public boolean agregarUsuario(String nombre, String correo, String carne){
        if (!nombre.isEmpty() && !correo.isEmpty() && !carne.isEmpty()) {
            Usuario usuario = new Usuario(nombre, correo, "123", carne);
            usuarios.add(usuario);
            return true;
        } else {
            return false;
        }
    }

    public boolean agregarReto(String nombre, String descripcion, int puntos, boolean estado){
        if (!nombre.isEmpty() && !descripcion.isEmpty()) {
            Retos reto = new Retos(nombre, descripcion, puntos, estado);
            retos.add(reto);
            return true;
        } else {
            return false;
        }
    }
}
