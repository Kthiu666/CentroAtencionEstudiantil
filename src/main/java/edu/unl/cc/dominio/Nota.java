package edu.unl.cc.dominio;

import java.time.LocalDate;

public class Nota {
    private String observacion;
    private LocalDate fecha;

    public Nota(String observacion, LocalDate fecha) {
        this.observacion = observacion;
        this.fecha = LocalDate.now();
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "[" + fecha + "] " + observacion;
    }

}
