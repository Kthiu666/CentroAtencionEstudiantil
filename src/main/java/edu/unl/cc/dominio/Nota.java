package edu.unl.cc.dominio;

import java.time.LocalDate;
import java.util.Objects;

public class Nota {
    private String observacion;
    private LocalDate fecha;

    public Nota(String observacion, LocalDate fecha) {
        this.observacion = observacion;
        this.fecha = fecha;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nota nota = (Nota) o;
        return Objects.equals(observacion, nota.observacion) && Objects.equals(fecha, nota.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(observacion, fecha);
    }

    @Override
    public String toString() {
        return "[" + fecha + "] " + observacion;
    }

}
