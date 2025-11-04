package edu.unl.cc;

import edu.unl.cc.dominio.Estudiante;
import edu.unl.cc.dominio.Nota;
import edu.unl.cc.dominio.Ticket;
import edu.unl.cc.dominio.TipoTramite;
import edu.unl.cc.servicio.CentroAtencionEstudiantil;

import java.time.LocalDate;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Consola {
    private CentroAtencionEstudiantil centroAtencionEstudiantil;
    Scanner sc;

    public Consola() {
        this.centroAtencionEstudiantil = new CentroAtencionEstudiantil();
        this.sc = new Scanner(System.in);
    }

    public void mostrarMenuPrincipal() {

        int opcionMenuPrincipal;

        do {
            System.out.println("***CENTRO DE ATENCION AL ESTUDIANTE***");
            System.out.println("---------------------------------------");
            System.out.println("1. Registrar estudiantes");
            System.out.println("2. Crear ticket");
            System.out.println("3. Atender ticket");
            System.out.println("4. Regitrar observaciones/notas");
            System.out.println("5. Deshacer cambios");
            System.out.println("6. Rehacer cambios");
            System.out.println("7. Finalizar atencion de ticket");
            System.out.println("8. Consultar ticket en espera");
            System.out.println("9. Consultar ticket en historial");
            System.out.println("10. Marcar ticket actual como PENDIENTE");
            System.out.println("11. Reanudar ticket PENDIENTE");
            System.out.println("12. Salir");
            System.out.println("---------------------------------------");
            System.out.println("Elija una opcion:");
            opcionMenuPrincipal = sc.nextInt();
            sc.nextLine();
            switch (opcionMenuPrincipal) {
                case 1:
                    mostrarMenuRegistrarEstudiante();
                    break;
                case 2:
                    mostrarMenuAgregarTicket();
                    break;
                case 3:
                    centroAtencionEstudiantil.atenderTicket();
                    break;
                case 4:
                    mostrarMenuRegistrarObservaciones();
                    break;
                case 5:
                    centroAtencionEstudiantil.undo();
                    break;
                case 6:
                    centroAtencionEstudiantil.redo();
                    break;
                case 7:
                    centroAtencionEstudiantil.finalizarTicket();
                    break;
                case 8:
                    menuConsultarTicketEspera();
                    break;
                case 9:
                    consultarHistorialTicket();
                    break;
                case 10:
                    mostrarMenuMarcarPendiente();
                    break;
                case 11:
                    mostrarMenuReanudarTicket();
                    break;
                case 12:
                    System.out.println("Fin");
                    break;
                default:
                    System.out.println("Ingrese una opcion valida");
            }
        } while (opcionMenuPrincipal != 12);
    }

    public void mostrarMenuRegistrarEstudiante() {
        System.out.println("----------------------------------------");
        System.out.println("Ingrese el nombre del estudiante");
        String nombreEstudiante = sc.nextLine();
        System.out.println("Ingrese el apellido del estudiante:");
        String apellidoEstudiante = sc.nextLine();
        System.out.println("Ingrese la cedula del estudiante");
        String cedula = sc.nextLine();
        System.out.println("----------------------------------------");
        System.out.println("1. Añadir estudiante");
        System.out.println("2. Salir/Volver al menu principal");
        System.out.println("----------------------------------------");
        System.out.println("Elija una opcion:");
        int opcionMenuRegitrarEstudiante = sc.nextInt();
        sc.nextLine();
        switch (opcionMenuRegitrarEstudiante) {
            case 1:
                Estudiante nuevoEstudiante = new Estudiante(nombreEstudiante, apellidoEstudiante, cedula);
                centroAtencionEstudiantil.registrarEstudiante(nuevoEstudiante);
                System.out.println("Estudiante " + nuevoEstudiante.getNombre() + " registrado exitosamente :)");
                break;
            case 2:
                System.out.println("Volviendo al menu principal");
                break;
                default:
                    System.out.println("Ingrese una opcion valida");
        }
    }

    public void mostrarMenuAgregarTicket() {
        System.out.println("--------------------------------------");
        int proximoNumero = centroAtencionEstudiantil.siguienteTicket();
        System.out.println("--> Creando Ticket N°: " + proximoNumero + " <--");
        System.out.println("--------------------------------------");
        System.out.println("Ingrese la CÉDULA del estudiante:");
        String cedula = sc.nextLine();
        // Valida al estudiante llamando al servicio
        Estudiante estudiante = centroAtencionEstudiantil.recuperarEstudiante(cedula);
        if (estudiante == null) {
            System.out.println("ERROR: Estudiante no encontrado. Registre al estudiante primero.");
            return;
        }
        System.out.println("Estudiante: " + estudiante.getNombre());
        System.out.println("--------------------------------------");
        System.out.println("1. Certificado");
        System.out.println("2. Homologacion");
        System.out.println("3. Constancia");
        System.out.println("--------------------------------------");
        System.out.println("Escoja el tramite a realizar");
        int opcionTramite = sc.nextInt();
        sc.nextLine();
        TipoTramite tipo;
        switch (opcionTramite) {
            case 1: tipo = TipoTramite.CERTIFICADO; break;
            case 2: tipo = TipoTramite.HOMOLOGACION; break;
            case 3: tipo = TipoTramite.CONSTANCIA; break;
            default: System.out.println("Opción no válida."); return;
        }
        System.out.println("Ingrese una descripcion del tramite:");
        String descripcion = sc.nextLine();
        System.out.println("--------------------------------------");
        System.out.println("1. Guardar y continuar");
        System.out.println("2. Salir/Volver al menu principal");
        System.out.println("--------------------------------------");
        System.out.println("Elija una opcion:");
        int opcionMenuAgregarTicket = sc.nextInt();
        sc.nextLine();
        switch (opcionMenuAgregarTicket) {
            case 1:
                Ticket nuevoTicket = new Ticket(descripcion, tipo, estudiante);
                Nota notaInicial = new Nota(descripcion, LocalDate.now());
                nuevoTicket.setNumero(proximoNumero);
                nuevoTicket.agregarNota(notaInicial);
                centroAtencionEstudiantil.crearTicket(nuevoTicket);
                System.out.println("Ticket guardado correctamente!!");
                break;
            case 2:
                System.out.println("Volviendo al menu principal");
                break;
            default:
                System.out.println("Ingrese una opcion valida");
        }

    }

    public void mostrarMenuRegistrarObservaciones() {
        // Esta nota se agrega al ticket EN ATENCIÓN
        if (centroAtencionEstudiantil.getTicketAtencion() == null) {
            System.out.println("ERROR: No hay ningún ticket en atención activa.");
            return;
        }
        System.out.println("---------------------------------------");
        System.out.println("Observaciones:");
        String observaciones = sc.nextLine();
        System.out.println("--------------------------------------");
        System.out.println("1. Agregar observacion");
        System.out.println("2. Salir/Volver al menu principal");
        System.out.println("-------------------------------------");
        System.out.println("Elija una opcion:");
        int opcionMenuAgregarObservaciones = sc.nextInt();
        sc.nextLine();
        switch (opcionMenuAgregarObservaciones) {
            case 1:
                Nota nuevaNota = new Nota(observaciones, LocalDate.now());
                centroAtencionEstudiantil.agregarNota(nuevaNota);
                System.out.println("Observacion agregado correctamente!!");
                break;
            case 2:
                System.out.println("Volviendo al menu principal");
                break;
            default:
                    System.out.println("Ingrese una opcion valida");
        }

    }

    public void menuConsultarTicketEspera(){
        System.out.println("---------------------------------------");
        Queue<Ticket> colaEspera = centroAtencionEstudiantil.getTickets();
        int cantidad = colaEspera.size();
        System.out.println("Tickets en espera: " + cantidad);
        System.out.println("---------------------------------------");
        if (cantidad == 0) {
            System.out.println("(No hay tickets en la cola)");
        } else {
            System.out.println("Listado de tickets en espera (del primero al último):");
            for (Ticket ticket : colaEspera) {

                System.out.println("  -> Ticket N°" + ticket.getNumero()
                        + "   | Estudiante: " + ticket.getEstudiante().getNombre()
                        + "   | Trámite   : " + ticket.getTipoTramite());
            }
        }
        System.out.println("---------------------------------------");
    }

    public  void consultarHistorialTicket(){
        System.out.println("--------------------------------------");
        System.out.println("Historial del ticket: ");
        System.out.println("Ingrese el NÚMERO del ticket para ver su historial:");
        int numero = sc.nextInt();
        sc.nextLine();
        Ticket ticket = centroAtencionEstudiantil.buscarTicketPorNumero(numero);
        System.out.println("Buscando ticket #" + numero);
        centroAtencionEstudiantil.consultarHistorial(ticket);
        System.out.println("--- Notas y Observaciones  ---");
        if (ticket != null) {
            List<Nota> notasDelTicket = ticket.getNotas();

            if (notasDelTicket == null || notasDelTicket.isEmpty()) {
                System.out.println("(No hay notas registradas en este ticket)");
            } else {
                for (Nota nota : notasDelTicket) {
                    System.out.println("  -> " + nota.toString());
                }
            }
        } else {
            System.out.println("(Ticket no encontrado)");
        }
        System.out.println("---------------------------------------");
    }

    public void mostrarMenuMarcarPendiente() {
        if (centroAtencionEstudiantil.getTicketAtencion() == null) {
            System.out.println("ERROR: No hay ningún ticket en atención activa para marcar como pendiente.");
            return;
        }

        System.out.println("---------------------------------------");
        System.out.println("Ticket en atención: #" + centroAtencionEstudiantil.getTicketAtencion().getNumero());
        System.out.println("Ingrese el MOTIVO para marcar como PENDIENTE:");
        String motivo = sc.nextLine();

        if (motivo.trim().isEmpty()) {
            System.out.println("Cancelado. Debe ingresar un motivo.");
            return;
        }

        centroAtencionEstudiantil.marcarPendiente(motivo);
        System.out.println("Ticket #" + (centroAtencionEstudiantil.getTicketAtencion() != null ? centroAtencionEstudiantil.getTicketAtencion().getNumero() : "") + " movido a pendientes.");
        System.out.println("---------------------------------------");
    }

    public void mostrarMenuReanudarTicket() {
        if (centroAtencionEstudiantil.getTicketAtencion() != null) {
            System.out.println("ERROR: Ya hay un ticket en atención. Finalícelo primero.");
            return;
        }

        System.out.println("---------------------------------------");
        System.out.println("Reanudar Ticket Pendiente");
        System.out.println("Ingrese el NÚMERO del ticket que desea reanudar:");

        int numeroTicket;
        try {
            numeroTicket = sc.nextInt();
            sc.nextLine(); // Limpiar buffer
        } catch (java.util.InputMismatchException e) {
            System.out.println("ERROR: Debe ingresar un número.");
            sc.nextLine(); // Limpiar buffer
            return;
        }

        centroAtencionEstudiantil.reanudarTicket(numeroTicket);
        System.out.println("---------------------------------------");
    }
    public static void main(String[] args) {
        Consola consola = new Consola();
        consola.mostrarMenuPrincipal();
    }

}