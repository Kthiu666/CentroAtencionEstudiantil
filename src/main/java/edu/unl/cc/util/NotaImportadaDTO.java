package edu.unl.cc.util;

import edu.unl.cc.dominio.Nota;
import java.time.LocalDate;

public class NotaImportadaDTO {
    private String cedulaEstudiante;
    private String nombreEstudiante;
    private LocalDate fecha;
    private String observacion;

    public NotaImportadaDTO(String cedulaEstudiante, String nombreEstudiante, LocalDate fecha, String observacion) {
        this.cedulaEstudiante = cedulaEstudiante;
        this.nombreEstudiante = nombreEstudiante;
        this.fecha = fecha;
        this.observacion = observacion;
    }

    public Nota toNota() {

        return new Nota(this.observacion, this.fecha);
    }

    // Getters
    public String getCedulaEstudiante() {

        return cedulaEstudiante;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getObservacion() {
        return observacion;
    }
}