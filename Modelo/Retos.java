package Modelo;
public class Retos {

    // Atributos
    private String nombreReto;
    private String descripcion;
    private int puntos;
    private boolean estado;

    // Constructor vacío
    public Retos() {
    }

    // Constructor con parámetros
    public Retos(String nombreReto, String descripcion, int puntos, boolean estado) {
        this.nombreReto = nombreReto;
        this.descripcion = descripcion;
        this.puntos = puntos;
        this.estado = estado;
    }

    // Métodos del UML
    public void setReto(String nombreReto, String descripcion, boolean estado) {
        this.nombreReto = nombreReto;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    // Getters
    public String getNombreReto() {
        return nombreReto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPuntos() {
        return puntos;
    }

    public boolean isEstado() {
        return estado;
    }
}