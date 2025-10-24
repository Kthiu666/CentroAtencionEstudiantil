package edu.unl.cc.dominio;

import java.util.ArrayList;
import java.util.List;

public class Ticket {

    private String descripcion;
    private int numero;
    private Estado estado;
    private TipoTramite tipoTramite;
    private Estudiante estudiante;

    private List<Nota> notas;

    public Nota removerUltimaNota() {
        if (this.notas.isEmpty()) {
            return null;
        }
        // Para ArrayList, el último elemento está en el índice size() - 1
        return this.notas.remove(this.notas.size() - 1);
    }

    public Ticket(String descripcion, TipoTramite tipoTramite, Estudiante estudiante) {
        this.descripcion = descripcion;
        this.estado = Estado.EN_COLA;//
        this.tipoTramite = tipoTramite;
        this.estudiante = estudiante;
        this.notas = new ArrayList<>();
    }

    public void agregarNota(Nota nota) {
        this.notas.add(nota);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public TipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(TipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public List<Nota> getNotas() {
        return notas;
    }

    public void setNotas(List<Nota> notas) {
        this.notas = notas;
    }

    @Override
    public String toString() {
        return "Ticket #" + numero + " - " + tipoTramite + " - " + estado + " (" + estudiante + ")";
    }
}
