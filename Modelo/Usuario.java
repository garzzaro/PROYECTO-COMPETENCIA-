
package Modelo;
import java.util.ArrayList;
import java.security.MessageDigest;

public class Usuario {
    private String nombre;
    private String correo;
    private String contra;
    private String carne;
    private ArrayList<Calificacion> calificacion;
    private ArrayList<Grupo> grupos;
    private ArrayList<Retos> retos;

    public Usuario(String nombre, String correo, String contra, String carne) {
        this.nombre = nombre;
        this.correo = correo;
        this.contra = encriptar(contra);
        this.carne = carne;
        this.calificacion = new ArrayList<>();
        this.grupos = new ArrayList<>();
        this.retos = new ArrayList<>();
    }

    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getCarne() { return carne; }
    public ArrayList<Calificacion> getCalificacion() { return calificacion; }
    public ArrayList<Grupo> getGrupos() { return grupos; }
    public ArrayList<Retos> getRetos() { return retos; }

    public void addCalificacion(Calificacion valor) {
        this.calificacion.add(valor);
    }

    private String encriptar(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return texto;
        }
    }

    public boolean verificarContra(String contra) {
        return this.contra.equals(encriptar(contra));
    }

    public String crearGrupo() {
        String nombreGrupo = "Grupo_" + this.nombre;
        return nombreGrupo;
    }

    public boolean unirseAGrupo(Grupo grupo) {
        if (!grupos.contains(grupo)) {
            grupos.add(grupo);
            return grupo.Asignar(this);
        }
        return false;
    }

    public boolean invitarAGrupo(Usuario usuario, Grupo grupo) {
        return grupo.Asignar(usuario);
    }

    
    public boolean asignarseAReto(Retos reto) {
        if (!retos.contains(reto)) {
            retos.add(reto);
            return true;
        }
        return false;
    }

    public void asignarRetoAGrupo(Retos reto, Grupo grupo) {
        grupo.asignarDesafio(reto);
    }
}