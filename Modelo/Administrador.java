package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Administrador {

    private String nombre;
    private String correo;
    private String contrasena;

    private ArrayList<Usuario> usuarios;
    private ArrayList<Retos> retos;
    private ArrayList<Grupo> grupos;

    public Administrador(String nombre, String correo, String contrasena) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.usuarios = new ArrayList<>();
        this.retos = new ArrayList<>();
        this.grupos = new ArrayList<>();
    }

    public boolean registrarUsuario(String nombre, String correo, String carne) {
        if (!nombre.isEmpty() && !correo.isEmpty() && !carne.isEmpty()) {
            Usuario nuevo = new Usuario(nombre, correo, "123", carne);
            usuarios.add(nuevo);
            return true;
        }
        return false;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public boolean registrarReto(String nombre, String descripcion, int puntos, boolean estado) {
        if (!nombre.isEmpty() && !descripcion.isEmpty()) {
            Retos nuevo = new Retos(nombre, descripcion, puntos, estado);
            retos.add(nuevo);
            return true;
        }
        return false;
    }

    public List<Retos> getRetos() {
        return retos;
    }

    public Grupo crearGrupo(String nombreGrupo) {
        Grupo grupo = new Grupo(nombreGrupo);
        try {
            java.lang.reflect.Field nombre = grupo.getClass().getDeclaredField("grupoNombre");
            nombre.setAccessible(true);
            nombre.set(grupo, nombreGrupo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        grupos.add(grupo);
        return grupo;
    }

    public boolean asignarUsuarioAGrupo(Usuario usuario, Grupo grupo) {
        return grupo.Asignar(usuario);
    }

    public boolean asignarRetoAGrupo(Retos reto, Grupo grupo) {
        grupo.asignarDesafio(reto);
        return true;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void mostrarResumen() {
        System.out.println("=== ADMINISTRADOR ===");
        System.out.println("Usuarios registrados: " + usuarios.size());
        System.out.println("Retos disponibles: " + retos.size());
        System.out.println("Grupos creados: " + grupos.size());
    }  
}
