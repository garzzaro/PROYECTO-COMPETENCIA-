package Modelo;
import java.util.ArrayList;

public class Usuario {
    private String nombre;
    private String correo;
    private String contra;
    private String carne;
    private ArrayList<Calificacion> calificacion;

    public Usuario(String nombre, String correo, String contra, String carne) {
        this.nombre = nombre;
        this.correo = correo;
        this.contra = contra;
        this.carne = carne;
        calificacion = new ArrayList<Calificacion>();
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<Calificacion> getCalificacion() {
        return calificacion;
    }

    public void addCalificacion(Calificacion valor) {
        this.calificacion.add(valor);
    }

    public String crearGrupo() {
        String nombreGrupo = "Grupo_" + this.nombre;
        return nombreGrupo;
    }
}