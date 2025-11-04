package Modelo;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class Usuario {
    private String nombre;
    private String correo;
    private String contra;
    private String carne;
    private ArrayList<Calificacion> calificacion;
    private ArrayList<Grupo> gruposCreados;
    private ArrayList<Grupo> gruposUnidos;
    private ArrayList<Reto> retosAsignados;

    public Usuario(String nombre, String correo, String contra, String carne) {
        this.nombre = nombre;
        this.correo = correo;
        this.contra = encriptarContrasena(contra);
        this.carne = carne;
        this.calificacion = new ArrayList<Calificacion>();
        this.gruposCreados = new ArrayList<Grupo>();
        this.gruposUnidos = new ArrayList<Grupo>();
        this.retosAsignados = new ArrayList<Reto>();
    }

  
    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getCarne() {
        return carne;
    }

    public ArrayList<Calificacion> getCalificacion() {
        return calificacion;
    }

    public void addCalificacion(Calificacion valor) {
        this.calificacion.add(valor);
    }


    private String encriptarContrasena(String contrasena) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contrasena.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return contrasena; 
        }
    }

    public boolean verificarContrasena(String contrasenaIngresada) {
        return this.contra.equals(encriptarContrasena(contrasenaIngresada));
    }

    
    public boolean unirseAGrupo(Grupo grupo) {
        if (!gruposUnidos.contains(grupo)) {
            gruposUnidos.add(grupo);
            return true;
        }
        return false;
    }

    public Grupo crearGrupo(String nombreGrupo) {
        Grupo nuevoGrupo = new Grupo(nombreGrupo, this);
        gruposCreados.add(nuevoGrupo);
        gruposUnidos.add(nuevoGrupo); 
        return nuevoGrupo;
    }

    public boolean enviarInvitacionGrupo(Usuario usuario, Grupo grupo) {
        if (gruposCreados.contains(grupo)) {
            // Solo el creador puede enviar invitaciones
            return grupo.agregarInvitacion(usuario);
        }
        return false;
    }

    public ArrayList<Grupo> getGruposCreados() {
        return gruposCreados;
    }

    public ArrayList<Grupo> getGruposUnidos() {
        return gruposUnidos;
    }
    
    public boolean asignarseAReto(Reto reto) {
        if (!retosAsignados.contains(reto)) {
            retosAsignados.add(reto);
            return true;
        }
        return false;
    }

    public boolean asignarRetoAGrupo(Reto reto, Grupo grupo) {
        if (gruposCreados.contains(grupo)) {
            return grupo.agregarReto(reto);
        }
        return false;
    }

    public ArrayList<Reto> getRetosAsignados() {
        return retosAsignados;
    }

    public boolean completarReto(Reto reto) {
        if (retosAsignados.contains(reto)) {
            reto.marcarComoCompletado(this);
            return true;
        }
        return false;
    }
}