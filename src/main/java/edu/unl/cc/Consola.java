package edu.unl.cc;

import edu.unl.cc.dominio.*;
import edu.unl.cc.servicio.CentroAtencionEstudiantil;
import edu.unl.cc.util.Validaciones;

import java.sql.SQLOutput;
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
            System.out.println("5. Deshacer Nota");
            System.out.println("6. Rehacer Nota");
            System.out.println("7. Finalizar atencion de ticket");
            System.out.println("8. Consultar ticket por Estado");
            System.out.println("9. Marcar ticket actual como PENDIENTE");
            System.out.println("10. Reanudar ticket PENDIENTE");
            System.out.println("11. Usuarios Registrados");
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
                    mostrarMenuConsultarPorEstado();
                    break;
                case 9:
                    mostrarMenuMarcarPendiente();
                    break;
                case 10:
                    mostrarMenuReanudarTicket();
                    break;
                case 11:
                    mostrarMenuUsuarios();
                    break;
                case 12:
                    centroAtencionEstudiantil.guardarNotasAutomaticamente();
                    System.out.println("Historial de notas exportado.");
                    System.out.println("Fin del programa.");
                    break;
                default:
                    System.out.println("Ingrese una opcion valida");
            }
        } while (opcionMenuPrincipal != 12);
    }

    public void mostrarMenuUsuarios(){
        List<Estudiante> lista = centroAtencionEstudiantil.getTodosLosEstudiantes();
        System.out.println("--- Lista de Estudiantes Registrados ---");
        if (lista.isEmpty()) {
            System.out.println("No hay estudiantes registrados.");
        } else {
            for (Estudiante est : lista) {
                // La consola se encarga de formatear la salida
                System.out.println("   | Cedula   : " + est.getCedula()
                        + "   | Nombre: " + est.getNombre()
                        + "   | Apellido: " + est.getApellido());
            }
        }
    }
    public void mostrarMenuRegistrarEstudiante() {
        System.out.println("----------------------------------------");
        String nombreEstudiante;
        do {
            System.out.println("Ingrese el nombre del estudiante:");
            nombreEstudiante = sc.nextLine();
            if (!Validaciones.validarNombreApellido(nombreEstudiante)) {
                System.out.println("ERROR: Nombre inválido. Use solo letras y espacios.");
            }
        } while (!Validaciones.validarNombreApellido(nombreEstudiante));

        String apellidoEstudiante;
        do {
            System.out.println("Ingrese el apellido del estudiante:");
            apellidoEstudiante = sc.nextLine();
            if (!Validaciones.validarNombreApellido(apellidoEstudiante)) {
                System.out.println("ERROR: Apellido inválido. Use solo letras y espacios.");
            }
        } while (!Validaciones.validarNombreApellido(apellidoEstudiante));

        String cedula;
        do {
            System.out.println("Ingrese la cédula del estudiante (10 dígitos):");
            cedula = sc.nextLine();
            if (!Validaciones.validarCedula(cedula)) {
                System.out.println("ERROR: Cédula inválida. Debe tener 10 dígitos numéricos.");
            }
        } while (!Validaciones.validarCedula(cedula));

        System.out.println("----------------------------------------");
        System.out.println("1. Añadir estudiante");
        System.out.println("2. Salir/Volver al menú principal");
        System.out.println("----------------------------------------");
        System.out.println("Elija una opción:");
        int opcionMenuRegistrarEstudiante = sc.nextInt();
        sc.nextLine();

        switch (opcionMenuRegistrarEstudiante) {
            case 1:
                Estudiante nuevoEstudiante = new Estudiante(nombreEstudiante, apellidoEstudiante, cedula);

                boolean registrado = centroAtencionEstudiantil.registrarEstudiante(nuevoEstudiante);

                if (registrado) {
                    System.out.println("Estudiante " + nuevoEstudiante.getNombre() +" "+ nuevoEstudiante.getApellido() + " registrado exitosamente :)");
                } else {
                    System.out.println("No se pudo registrar el estudiante.");
                }
                break;

            case 2:
                System.out.println("Volviendo al menú principal...");
                break;

            default:
                System.out.println("Ingrese una opción válida.");
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
        System.out.println("Agregando observación al siguiente ticket:"); // <--- NUEVO
        System.out.println("  -> Ticket N°: " + centroAtencionEstudiantil.getTicketAtencion().getNumero()); // <--- NUEVO
        // Asumo que tienes métodos como getEstudiante() y getNombre()
        System.out.println("  -> Estudiante: " + centroAtencionEstudiantil.getTicketAtencion().getEstudiante().getNombre()); // <--- NUEVO
        System.out.println("  -> Tramite: " + centroAtencionEstudiantil.getTicketAtencion().getTipoTramite()); // <--- NUEVO
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
        System.out.println("Buscando ticket #" + numero);
        Ticket ticket = centroAtencionEstudiantil.buscarTicketPorNumero(numero);
        if (ticket != null) {
            // imprime historial de acciones
            System.out.println("--- Historial de Acciones ---");
            centroAtencionEstudiantil.consultarHistorial(ticket);
            System.out.println("--- Notas y Observaciones  ---");
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

    public void mostrarMenuMarcarPendiente() {
        Ticket ticketActual = centroAtencionEstudiantil.getTicketAtencion();
        if (centroAtencionEstudiantil.getTicketAtencion() == null) {
            System.out.println("ERROR: No hay ningún ticket en atención activa para marcar como pendiente.");
            return;
        }
        System.out.println("---------------------------------------");
        System.out.println("Marcando como PENDIENTE el siguiente ticket:");
        System.out.println("  -> Ticket N°: " + ticketActual.getNumero());
        if (ticketActual.getEstudiante() != null) {
            System.out.println("  -> Estudiante: " + ticketActual.getEstudiante().getNombre());
        }
        System.out.println("  -> Trámite: " + ticketActual.getTipoTramite());
        System.out.println("---------------------------------------");
        System.out.println("Ingrese el MOTIVO para marcar como PENDIENTE:");
        String motivo = sc.nextLine();

        if (motivo.trim().isEmpty()) {
            System.out.println("Cancelado. Debe ingresar un motivo.");
            return;
        }
        centroAtencionEstudiantil.marcarPendiente(motivo);
        System.out.println("Ticket #" + ticketActual.getNumero() + " movido a pendientes.");
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
        boolean exito = centroAtencionEstudiantil.reanudarTicket(numeroTicket);
        if (exito) {
            System.out.println("Ticket #" + numeroTicket + " reanudado exitosamente y ahora está en atención.");
        }
        System.out.println("---------------------------------------");
    }

    /**
     * Muestra el menú para que el usuario elija un estado y presenta los resultados.
     */
    public void mostrarMenuConsultarPorEstado() {
        int opcion;
        Estado estadoConsulta = null;

        do {
            System.out.println("\n--- CONSULTAR TICKETS POR ESTADO ---");
            System.out.println("1. EN_COLA");
            System.out.println("2. EN_ATENCION");
            System.out.println("3. PENDIENTE_DOCS");
            System.out.println("4. COMPLETADO");
            System.out.println("5. Volver al menú principal");
            System.out.println("-------------------------------------");
            System.out.print("Elija un estado para consultar: ");

            // Verificación básica de entrada para evitar errores graves
            if (sc.hasNextInt()) {
                opcion = sc.nextInt();
                sc.nextLine(); // Consumir salto de línea
            } else {
                System.out.println("Error: Ingrese un número válido.");
                sc.next(); // Consumir la entrada no válida
                opcion = 0;
            }

            switch (opcion) {
                case 1: estadoConsulta = Estado.EN_COLA; break;
                case 2: estadoConsulta = Estado.EN_ATENCION; break;
                case 3: estadoConsulta = Estado.PENDIENTE_DOCS; break;
                case 4: estadoConsulta = Estado.COMPLETADO; break;
                case 5: return; // Vuelve al menú principal
                default: System.out.println("Opción no válida."); continue;
            }

            if (estadoConsulta != null) {
                List<Ticket> resultados = centroAtencionEstudiantil.consultarTicketsPorEstado(estadoConsulta);

                System.out.println("\n--- RESULTADOS PARA ESTADO: " + estadoConsulta + " (" + resultados.size() + " Tickets) ---");
                if (resultados.isEmpty()) {
                    System.out.println("No se encontraron tickets.");
                } else {
                    for (int i = 0; i < resultados.size(); i++) {
                        Ticket t = resultados.get(i);
                        // El toString() de Ticket debería ser suficiente para mostrar la información clave
                        System.out.println((i + 1) + ". " + t.toString());
                        System.out.println("--- Notas y Observaciones  ---");
                        List<Nota> notasDelTicket = t.getNotas();
                        if (notasDelTicket == null || notasDelTicket.isEmpty()) {
                            System.out.println("(No hay notas registradas en este ticket)");
                        } else {
                            // Iteramos sobre la lista de notas y las mostramos
                            for (Nota nota : notasDelTicket) {
                                System.out.println("  -> " + nota.toString());
                            }
                        }

                    }
                }
                System.out.println("----------------------------------------\n");
                estadoConsulta = null;
            }

        } while (opcion != 5);
    }

    public static void main(String[] args) {
        Consola consola = new Consola();
        consola.mostrarMenuPrincipal();
    }

}