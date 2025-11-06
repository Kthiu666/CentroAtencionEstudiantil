package edu.unl.cc.util;

import edu.unl.cc.dominio.Nota;
import edu.unl.cc.dominio.Ticket;
import edu.unl.cc.dominio.Estudiante;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ManejadorNotasArchivo {

    private static final String SEPARADOR_CSV = ";";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Exporta notas junto con el estudiante asociado a un archivo CSV.
     * @param tickets La lista de tickets donde se extraerán las notas.
     * @param nombreArchivo El nombre del archivo de salida.
     */
    public void exportarNotasACSV(List<Ticket> tickets, String nombreArchivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
            bw.write("Cedula" + SEPARADOR_CSV + "Estudiante" + SEPARADOR_CSV + "Fecha" + SEPARADOR_CSV + "Observacion");
            bw.newLine();

            for (Ticket ticket : tickets) {
                Estudiante estudiante = ticket.getEstudiante();

                String cedula = (estudiante != null) ? estudiante.getCedula() : "N/A";
                String nombre = (estudiante != null) ? estudiante.getNombre() : "N/A";

                for (Nota nota : ticket.getNotas()) {
                    String observacionEscapada = nota.getObservacion().replace("\"", "\"\"");

                    String linea = String.format("\"%s\"%s\"%s\"%s%s%s\"%s\"", // Usamos comillas para los campos de texto
                            cedula, SEPARADOR_CSV,
                            nombre, SEPARADOR_CSV,
                            nota.getFecha().format(FORMATTER), SEPARADOR_CSV, // Formatear la fecha
                            observacionEscapada);

                    bw.write(linea);
                    bw.newLine();
                }
            }
            System.out.println("Notas exportadas a CSV exitosamente a " + nombreArchivo);

        } catch (IOException error) {
            System.err.println("ERROR al exportar notas a CSV: " + error.getMessage());
        }
    }

    /**
     * Importa notas desde un archivo CSV.
     * @param nombreArchivo El nombre del archivo CSV a importar.
     * @return Una lista de objetos Nota importados.
     */
    public List<NotaImportadaDTO> importarNotasDesdeCSV(String nombreArchivo) {

        List<NotaImportadaDTO> dtosImportados = new ArrayList<>();
        File archivo = new File(nombreArchivo);

        if (!archivo.exists()) {
            System.out.println("INFO: Archivo de historial " + nombreArchivo + " no encontrado. Iniciando limpio.");
            return dtosImportados;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            br.readLine();

            while ((linea = br.readLine()) != null) {

                String[] partes = linea.split(SEPARADOR_CSV);

                if (partes.length >= 4) {
                    // Limpieza y extracción de las 4 partes
                    String cedula = partes[0].replace("\"", "").trim();
                    String nombre = partes[1].replace("\"", "").trim();
                    // La fecha es el tercer elemento (índice 2) en el formato [Cedula, Estudiante, Fecha, Observacion]
                    LocalDate fecha = LocalDate.parse(partes[2].replace("\"", "").trim(), FORMATTER);
                    String observacion = partes[3].replace("\"", "").trim();

                    dtosImportados.add(new NotaImportadaDTO(cedula, nombre, fecha, observacion));
                }
            }
            System.out.println("LOG: " + dtosImportados.size() + " registros de notas importados desde CSV.");
        } catch (IOException e) {
            System.err.println("ERROR de lectura/escritura al importar CSV: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR al parsear una línea del CSV: " + e.getMessage());
        }
        return dtosImportados;
    }
    /**
     * Exporta una lista de notas a un archivo TXT con formato simple: [Fecha] Observacion.
     * @param tickets La lista de tickets
     * @param nombreArchivo El nombre del archivo de salida.
     */
    public void exportarNotasATXT(List<Ticket> tickets, String nombreArchivo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            for (Ticket ticket : tickets) {
                Estudiante estudiante = ticket.getEstudiante();
                String infoEstudiante = (estudiante != null) ?
                        "Estudiante: " + estudiante.getNombre() + " (Cédula: " + estudiante.getCedula() + ")" :
                        "Estudiante: N/A";

                for (Nota nota : ticket.getNotas()) {
                    // Formato: [Fecha] [Estudiante Info] Observacion
                    String linea = String.format("[%s] %s: %s",
                            nota.getFecha().format(FORMATTER),
                            infoEstudiante,
                            nota.getObservacion());
                    pw.println(linea);
                }
            }
            System.out.println("LOG: Notas exportadas a TXT exitosamente a -> " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("ERROR al exportar notas a TXT: " + e.getMessage());
        }
    }
}
