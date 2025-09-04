package Modelo;
import java.util.ArrayList;

public class Usuario{
    private String nombre,
    private String correo,
    private String contra,
    private String carné,
    public ArrayLis<Calificacion> calificacion;
    
    public Usuario(String nombre, String correo, String contra, String carné){
        this.nombre = nombre;
        this.correo = correo;
        this.contra = contra;
        this.carné = carné;
        calificacion = new ArrayList<Calificacion>()
    }
}