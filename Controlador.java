import Modelo.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Controlador {
    private ArrayList<Usuario> usuarios;
    private ArrayList<Retos> retos;

    public Controlador(){
        usuarios = new ArrayList<>();
        retos = new ArrayList<>();
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