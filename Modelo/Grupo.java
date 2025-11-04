package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Grupo {
    private String grupoNombre;
    private List<Usuario> participantes;
    private List<Usuario> invitaciones;
    private String reto;
    private Boolean retoFinalizado;

    public Grupo(String grupoNombre) {
        this.grupoNombre = grupoNombre;
        this.participantes = new ArrayList<>();
        this.invitaciones = new ArrayList<>();
        this.retoFinalizado = false;
    }

    public String getGrupoNombre() { return grupoNombre; }
    public List<Usuario> getParticipantes() { return participantes; }
    public String getReto() { return reto; }
    public Boolean getRetoFinalizado() { return retoFinalizado; }

    public boolean Asignar(Usuario usuario) {
        return participantes.add(usuario);
    }

    public boolean Desasignar(Usuario usuario) {
        return participantes.remove(usuario);
    }

    public void asignarDesafio(Retos desafio) {
        this.reto = desafio.toString();
    }

    public void finalizarReto() {
        this.retoFinalizado = true;
    }

    public boolean agregarInvitacion(Usuario usuario) {
        if (!invitaciones.contains(usuario) && !participantes.contains(usuario)) {
            invitaciones.add(usuario);
            return true;
        }
        return false;
    }

    public boolean aceptarInvitacion(Usuario usuario) {
        if (invitaciones.contains(usuario)) {
            invitaciones.remove(usuario);
            return Asignar(usuario);
        }
        return false;
    }

    public List<Usuario> getInvitaciones() {
        return invitaciones;
    }
}