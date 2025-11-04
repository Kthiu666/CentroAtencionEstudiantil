package edu.unl.cc;

import edu.unl.cc.dominio.Estudiante;
import edu.unl.cc.dominio.Nota;
import edu.unl.cc.dominio.Ticket;
import edu.unl.cc.dominio.TipoTramite;
import edu.unl.cc.servicio.CentroAtencionEstudiantil;
import edu.unl.cc.util.Validaciones;

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
            System.out.println("10. Salir");
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
                    System.out.println("Fin");
                    break;
                default:
                    System.out.println("Ingrese una opcion valida");
            }
        } while (opcionMenuPrincipal != 10);
    }

    public void mostrarMenuRegistrarEstudiante() {
        System.out.println("----------------------------------------");
        String nombreEstudiante ;
        do {
            System.out.println("Ingrese el nombre del estudiante:");
            nombreEstudiante = sc.nextLine();
            if (!Validaciones.validarNombreApellido(nombreEstudiante)) {
                System.out.println("ERROR: Nombre inválido. Use solo letras y espacios.");
            }
        } while (!Validaciones.validarNombreApellido(nombreEstudiante));


        String apellidoEstudiante ;
        do {
            System.out.println("Ingrese el apellido del estudiante:");
            apellidoEstudiante = sc.nextLine();
            if (!Validaciones.validarNombreApellido(apellidoEstudiante)) {
                System.out.println("ERROR: Apellido inválido. Use solo letras y espacios.");
            }
        } while (!Validaciones.validarNombreApellido(apellidoEstudiante));

        String cedula ;
        do {
            System.out.println("Ingrese la cedula del estudiante (10 dígitos):");
            cedula = sc.nextLine();
            if (!Validaciones.validarCedula(cedula)) {
                System.out.println("ERROR: Cédula inválida. Debe tener 10 dígitos numéricos.");
            }
        } while (!Validaciones.validarCedula(cedula));

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

        String descripcion ;
        do {
            System.out.println("Ingrese una descripcion del tramite:");
            descripcion = sc.nextLine();
            if (Validaciones.estaVacio(descripcion)) {
                System.out.println("ERROR: La descripción no puede estar vacía.");
            }
        } while (Validaciones.estaVacio(descripcion));

        // ---  PREGUNTAR URGENCIA ---
        System.out.println("¿El trámite es URGENTE? (S/N):");
        String urgenteInput = sc.nextLine();
        boolean esUrgente = urgenteInput.trim().equalsIgnoreCase("S");

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
                nuevoTicket.setUrgente(esUrgente);
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
        String observaciones;
        do {
            System.out.println("Observaciones:");
            observaciones = sc.nextLine();
            if (Validaciones.estaVacio(observaciones)) {
                System.out.println("ERROR: Las observaciones no pueden estar vacías.");
            }
        } while (Validaciones.estaVacio(observaciones));
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
        Queue<Ticket> colaUrgente = centroAtencionEstudiantil.getTicketsUrgentes();
        int cantidad = colaEspera.size() + colaUrgente.size();
        System.out.println("Tickets en espera: " + cantidad);
        System.out.println("---------------------------------------");
        if (cantidad == 0) {
            System.out.println("(No hay tickets en la cola)");
        } else {
            System.out.println("Listado de tickets en espera :");
            // Iteramos sobre la cola para imprimir cada ticket
            // Un for-each sobre una Queue que la lee.
            for (Ticket ticket : colaUrgente) {
                System.out.println("  -> Ticket N°" + ticket.getNumero()
                        + "   | Estudiante: " + ticket.getEstudiante().getNombre()
                        + "   | Trámite   : " + ticket.getTipoTramite()
                        + "   | Es Urgente   : " + ticket.isUrgente());
            }

            for (Ticket ticket : colaEspera) {
                System.out.println("  -> Ticket N°" + ticket.getNumero()
                        + "   | Estudiante: " + ticket.getEstudiante().getNombre()
                        + "   | Trámite   : " + ticket.getTipoTramite()
                        + "   | Es Urgente   : " + ticket.isUrgente());
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
        // imprime historial de acciones
        centroAtencionEstudiantil.consultarHistorial(ticket);
        System.out.println("--- Notas y Observaciones  ---");
        if (ticket != null) {
            List<Nota> notasDelTicket = ticket.getNotas();

            if (notasDelTicket == null || notasDelTicket.isEmpty()) {
                System.out.println("(No hay notas registradas en este ticket)");
            } else {
                // Iteramos sobre la lista de notas y las mostramos
                for (Nota nota : notasDelTicket) {
                    System.out.println("  -> " + nota.toString());
                }
            }
        } else {
            // Esta línea solo se mostraría si buscarTicketPorNumero devolvió null
            System.out.println("(Ticket no encontrado)");
        }
        System.out.println("---------------------------------------");
    }

    public static void main(String[] args) {
        Consola consola = new Consola();
        consola.mostrarMenuPrincipal();
    }

}