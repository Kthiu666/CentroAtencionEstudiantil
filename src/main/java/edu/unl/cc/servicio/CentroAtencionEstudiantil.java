package edu.unl.cc.servicio;

import edu.unl.cc.dominio.Estado;
import edu.unl.cc.dominio.Estudiante;
import edu.unl.cc.dominio.Nota;
import edu.unl.cc.dominio.Ticket;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CentroAtencionEstudiantil {

    private Stack<String> acciones; //--> Pila para el historial de acciones
    private Stack<String> accionesRevertidas; //--> Pila para el historial de acciones revertidas
    private Queue<Ticket> tickets; //--> Cola que almacena los nuevos tickets que llegan para ser atendidos
    private Queue<Ticket> ticketsAtendidos; // Cola para tickets que ya fueron finalizados
    private Map<String, Estudiante> estudiantes;  // Mapa para  acceso a estudiantes por su cédula
    private Map<Integer, Ticket> ticketsPendientes;
    private Ticket ticketAtencion; // El ticket que está siendo atendido en ese momento (solo uno a la vez)

    public CentroAtencionEstudiantil() {
        this.acciones = new Stack<>();
        this.accionesRevertidas = new Stack<>();
        this.tickets = new LinkedList<>();
        this.ticketsAtendidos = new LinkedList<>();
        this.estudiantes = new HashMap<>();
        this.ticketsPendientes = new HashMap<>();
        this.ticketAtencion = null; // Nadie está en atención al inicio
    }

    public void registrarEstudiante(Estudiante estudiante) {
        if (estudiante == null || estudiante.getCedula() == null) {
            System.out.println("ERROR: El estudiante o su cédula no pueden ser nulos.");
            return;
        }

        // putIfAbsent añade solo si no existe y devuelve null cuando sea agrega correctamente
        if (this.estudiantes.putIfAbsent(estudiante.getCedula(), estudiante) == null) {
            this.acciones.push("Estudiante registrado: " + estudiante.getNombre());
        } else {
            System.out.println("ERROR: Ya existe un estudiante con la cédula " + estudiante.getCedula());
        }
    }

    /*
    Crea un nuevo ticket y guarda la accion en pila
    */
    public void crearTicket( Ticket ticket ) {
        if (ticket == null) {
            System.out.println("ERROR: Se intentó crear un ticket nulo.");
            return;
        }

        // Añadir a la cola y registrar acción
        this.tickets.add(ticket);
        // Registro para el historial interno
        this.acciones.push("Ticket " + ticket.getNumero() + " creado para " + ticket.getEstudiante().getNombre());
        System.out.println("Ticket " + ticket.getNumero() + " agregado a la cola de espera.");
    }

    /**
    Toma el primer ticket de tickets y lo cambia de estado a Atencion
    Agregar nota con inicio de atencion con fecha
     */
    public void atenderTicket() {
        // Valida si hay un ticket en atencion
        if (this.ticketAtencion != null) {
            System.out.println("ERROR: Ya hay un ticket (" + ticketAtencion.getNumero() + ") en atención.");
            return; }

        // Valida si existen tickets
        if (this.tickets.isEmpty()) {
            System.out.println("INFO: No hay tickets en la cola de espera.");
            return; }

        // Saca el ticket de la cola de espera .poll() remueve el primer elemento de la cola
        this.ticketAtencion = this.tickets.poll();
        this.ticketAtencion.setEstado(Estado.EN_ATENCION);  // Cambiar estado

        // Agregar nota de inicio
        Nota notaInicio = new Nota("Inicio de atención.", LocalDate.now());
        this.ticketAtencion.agregarNota(notaInicio);

        // Registrar la acción
        this.acciones.push("Ticket " + ticketAtencion.getNumero() + " pasa a atención.");
        System.out.println(" Atendiendo ticket " + ticketAtencion.getNumero());
    }

    // cambiar estado a completado con nota de tramite finalizadp
    // Ese ticket pasa (sin quitarlo de tickets, solo referencia) a ticketsAtendidos
    public void finalizarTicket() {
        // Valida si existe ticket para finalizar
        if (this.ticketAtencion == null) {
            System.out.println("ERROR: No hay ningún ticket en atención para finalizar.");
            return;
        }
        this.ticketAtencion.setEstado(Estado.COMPLETADO); // Cambia estado
        Nota notaFin = new Nota("Trámite finalizado.", LocalDate.now());
        this.ticketAtencion.agregarNota(notaFin);
        this.ticketsAtendidos.add(this.ticketAtencion);// Mueve a la cola de atendidos
        // 4. Registrar acción
        this.acciones.push("Ticket " + ticketAtencion.getNumero() + " finalizado.");
        System.out.println("LOG: Ticket " + ticketAtencion.getNumero() + " finalizado y movido a 'atendidos'.");

        // 5. Liberar la ventanilla de atención
        this.ticketAtencion = null;
    }

    //Esa nota se agrega a TicketAtencion
    // Agrega observacion
    public void agregarNota(Nota nota) {
        if (this.ticketAtencion == null) {
            System.out.println("ERROR: No hay ningún ticket en atención para agregarle una nota.");
            return;
        }

        this.ticketAtencion.agregarNota(nota);
        this.acciones.push("Nota agregada al ticket " + ticketAtencion.getNumero() + "| Contenido: " + nota.getObservacion());
        System.out.println("LOG: Nota agregada al ticket " + ticketAtencion.getNumero());
    }

    /**
     * Busca un ticket por su número en las tres colecciones.
     * @param numero El número del ticket.
     * @return El ticket encontrado, o null.
     */
    public Ticket buscarTicketPorNumero(int numero) {
        if (ticketAtencion != null && ticketAtencion.getNumero() == numero) return ticketAtencion;
        for (Ticket t : tickets) if (t.getNumero() == numero) return t;
        for (Ticket t : ticketsAtendidos) if (t.getNumero() == numero) return t;
        return null;
    }

    /**
     * Obtiene el historial de acciones registradas para un ticket.
     * @param ticket El ticket para el que se consulta el historial.
     * @return Una lista de strings con las acciones del historial. Retorna una lista vacía si el ticket es nulo o no tiene acciones.
     */
    public void consultarHistorial(Ticket ticket) {
        // Validar que el ticket no sea nulo
        if (ticket == null) {
            System.out.println("ERROR: Ticket nulo o no encontrado.");
            return;
        }

        //System.out.println("Historial del Ticket " + ticket.getNumero() + ":");

        final String identificador = "Ticket " + ticket.getNumero();

        // Recorrer la pila sin modificarla
        List<String> historialEncontrado = this.acciones.stream()
                .filter(accion -> accion.contains(identificador))
                .collect(Collectors.toList());

        // Informar al usuario que no se encontró ninguna acción relacionada
        if (historialEncontrado.isEmpty()) {
            System.out.println("(No hay acciones registradas para este ticket)");
        } else {
            historialEncontrado.forEach(accion -> System.out.println("  - " + accion));
        }
    }

    /**
    * Calcula y devuelve el siguiente número de ticket disponible.
    * @return El nuevo número de ticket.
    */
    public int siguienteTicket() {
        int numeroMaximo = 0;

        // Buscar el número máximo entre los tickets pendientes
        for (Ticket ticket : this.tickets){
            numeroMaximo = Math.max(numeroMaximo, ticket.getNumero());
        }
        
        // Actualizar el número máximo con los tickets atendidos
        for (Ticket ticket : this.ticketsAtendidos) {
            numeroMaximo = Math.max(numeroMaximo, ticket.getNumero());
        }

        if (this.ticketAtencion != null){
            numeroMaximo = Math.max(numeroMaximo, this.ticketAtencion.getNumero());
        }

        return numeroMaximo + 1;
    }

    //Para buscar en el mapa por el numero de cedula
    public Estudiante recuperarEstudiante(String cedula) {
        if (estudiantes.containsKey(cedula)) {
            return estudiantes.get(cedula);
        } else {
            System.out.println("Estudiante no encontrado con cédula: " + cedula);
            return null;
        }
    }

    /**
     * Deshace la última acción registrada (undo).
     * Mueve la acción a la pila de accionesRevertidas.
     */
    public void undo() {
        // Validar que haya acciones para deshacer
        if (acciones.isEmpty()) {
            System.out.println("No hay acciones para deshacer.");
            return;
        }

        // Sacar la última acción de la pila
        String ultimaAccion = acciones.pop();

        if (ultimaAccion.contains("Nota agregada al ticket")) {
            if (this.ticketAtencion != null) {
                Nota notaDeshecha = this.ticketAtencion.removerUltimaNota();

                if (notaDeshecha == null) {
                    acciones.push(ultimaAccion);
                    System.out.println("ERROR: No se pudo deshacer la nota (nota no encontrada).");
                    return;
                }
            } else {
                System.out.println("ERROR: No hay ticket en atención para deshacer la nota.");
                acciones.push(ultimaAccion); // Devolvemos la acción
                return;
            }

        } else if (ultimaAccion.contains("pasa a atención")) {
            // Revertir atención: Mover el ticket de atención de vuelta a la cola
            if (this.ticketAtencion != null) {
                this.ticketAtencion.setEstado(Estado.EN_COLA);
                this.tickets.add(this.ticketAtencion);
                this.ticketAtencion = null;
            }

        } else if (ultimaAccion.contains("finalizado.")) {
            // Revertir finalización: Mover el ticket de atendidos de vuelta a atención
            if (this.ticketsAtendidos.remove(this.ticketAtencion)) {
                this.ticketAtencion.setEstado(Estado.EN_ATENCION);
            }
        } else if (ultimaAccion.contains("marcado como PENDIENTE")) {
            // Revertir "marcar pendiente": Mover de pendientes a atención.
            System.out.println("Deshaciendo 'marcado como PENDIENTE'...");
            try {
                String numStr = ultimaAccion.split(" ")[1];
                int numTicket = Integer.parseInt(numStr);
                Ticket ticketAReanudar = this.ticketsPendientes.remove(numTicket);
                if (ticketAReanudar != null && this.ticketAtencion == null) {
                    this.ticketAtencion = ticketAReanudar;
                    this.ticketAtencion.setEstado(Estado.EN_ATENCION);
                    this.ticketAtencion.removerUltimaNota();
                } else {
                    System.out.println("ERROR: No se pudo revertir 'pendiente'.");
                    acciones.push(ultimaAccion);
                    return;
                }
            } catch (Exception e) {
                System.out.println("ERROR: Fallo acción: " + ultimaAccion);
            }

        } else if (ultimaAccion.contains("reanudado desde PENDIENTE")) {
            // Revertir "reanudar": Mover de atención a pendientes.
            System.out.println("Deshaciendo 'reanudado desde PENDIENTE'...");
            if (this.ticketAtencion == null) {
                System.out.println("ERROR (undo): No hay ticket en atención para revertir 'reanudar'.");
                acciones.push(ultimaAccion);
                return;
            }
            this.ticketAtencion.setEstado(Estado.PENDIENTE_DOCS);
            this.ticketAtencion.removerUltimaNota();
            this.ticketsPendientes.put(this.ticketAtencion.getNumero(), this.ticketAtencion);
            this.ticketAtencion = null;
        }

        // Guardarla en la pila de revertidas
        accionesRevertidas.push(ultimaAccion);

        // Mostrar al usuario
        System.out.println("Deshacer: " + ultimaAccion);
    }

    /**
     * Rehace la última acción deshecha (redo).
     * Mueve la acción desde la pila de revertidas a la pila principal.
     */
    public void redo() {
        // Validar que haya acciones para rehacer
        if (accionesRevertidas.isEmpty()) {
            System.out.println("No hay acciones para rehacer.");
            return;
        }

        // Sacar la última acción revertida
        String accionRehecha = accionesRevertidas.pop();

        if (accionRehecha.contains("Nota agregada al ticket")) {
            if (this.ticketAtencion != null) {

                String contenido = accionRehecha.substring(accionRehecha.indexOf("Contenido: ") + 11);

                Nota notaRehecha = new Nota(contenido, LocalDate.now());
                this.ticketAtencion.agregarNota(notaRehecha);
            } else {
                System.out.println("ERROR: No hay ticket en atención para rehacer la nota.");
                accionesRevertidas.push(accionRehecha); // Devolver la acción
                return;
            }
        } else if (accionRehecha.contains("pasa a atención")) {
            // Rehacer atención: Sacar de la cola y poner en atención
            if (this.ticketAtencion == null && !this.tickets.isEmpty()) {
                this.ticketAtencion = this.tickets.poll();
                this.ticketAtencion.setEstado(Estado.EN_ATENCION);
            }
        } else if (accionRehecha.contains("finalizado.")) {
            // Rehacer finalización: Mover el ticket de atención a atendidos y liberar ventanilla
            if (this.ticketAtencion != null) {
                this.ticketAtencion.setEstado(Estado.COMPLETADO);
                this.ticketsAtendidos.add(this.ticketAtencion);
                this.ticketAtencion = null;
            }
        } else if (accionRehecha.contains("marcado como PENDIENTE")) {
            // Rehacer "marcar pendiente"
            System.out.println("Rehaciendo 'marcado como PENDIENTE'...");
            if (this.ticketAtencion == null) {
                System.out.println("ERROR (redo): No hay ticket en atención para marcar como pendiente.");
                accionesRevertidas.push(accionRehecha);
                return;
            }
            String motivo = "Motivo desconocido";
            if (accionRehecha.contains("Motivo: ")) {
                motivo = accionRehecha.substring(accionRehecha.indexOf("Motivo: ") + 8);
            }
            Nota notaPendiente = new Nota("Ticket puesto en PENDIENTE. Motivo: " + motivo, LocalDate.now());
            this.ticketAtencion.agregarNota(notaPendiente);
            this.ticketAtencion.setEstado(Estado.PENDIENTE_DOCS);
            this.ticketsPendientes.put(this.ticketAtencion.getNumero(), this.ticketAtencion);
            this.ticketAtencion = null;
        } else if (accionRehecha.contains("reanudado desde PENDIENTE")) {
            // Rehacer "reanudar ticket"
            System.out.println("Rehaciendo 'reanudado desde PENDIENTE'...");
            if (this.ticketAtencion != null) {
                System.out.println("ERROR (redo): Ya hay un ticket en atención.");
                accionesRevertidas.push(accionRehecha);
                return;
            }
            try {
                String numStr = accionRehecha.split(" ")[1];
                int numTicket = Integer.parseInt(numStr);

                Ticket ticketAReanudar = this.ticketsPendientes.remove(numTicket);
                if (ticketAReanudar != null) {
                    this.ticketAtencion = ticketAReanudar;
                    this.ticketAtencion.setEstado(Estado.EN_ATENCION);
                    Nota notaReanuda = new Nota("Atención reanudada.", LocalDate.now());
                    this.ticketAtencion.agregarNota(notaReanuda);
                } else {
                    System.out.println("ERROR: No se encontró el ticket pendiente " + numTicket);
                }
            } catch (Exception e) {
                System.out.println("ERROR (redo): Fallo al parsear acción: " + accionRehecha);
            }
        }
                // Volver a colocarla en la pila principal
                acciones.push(accionRehecha);

                // Mostrar al usuario
                System.out.println("Rehacer: " + accionRehecha);

    }

    /**
     * Pone el ticket actual en estado PENDIENTE_DOCS.
     * El ticket sale de atención y se guarda en el mapa de pendientes.
     */
    public void marcarPendiente(String motivo) {
        if (this.ticketAtencion == null) {
            System.out.println("ERROR: No hay ningún ticket en atención para marcar como pendiente.");
            return;
        }

        int numTicket = this.ticketAtencion.getNumero();

        Nota notaPendiente = new Nota("Ticket puesto en PENDIENTE. Motivo: " + motivo, LocalDate.now());
        this.ticketAtencion.agregarNota(notaPendiente);
        this.ticketAtencion.setEstado(Estado.PENDIENTE_DOCS);
        this.ticketsPendientes.put(numTicket, this.ticketAtencion);
        this.acciones.push("Ticket " + numTicket + " marcado como PENDIENTE. Motivo: " + motivo);
        System.out.println("LOG: Ticket " + numTicket + " marcado como PENDIENTE.");
        this.ticketAtencion = null;
    }

    /**
     * Reanuda la atención de un ticket que estaba en PENDIENTE_DOCS.
     * Lo busca por su número, lo saca de pendientes y lo pone en atención.
     */
    public void reanudarTicket(int numeroTicket) {
        if (this.ticketAtencion != null) {
            System.out.println("ERROR: Ya hay un ticket en atención (" + this.ticketAtencion.getNumero() + "). Finalícelo o márquelo como pendiente primero.");
            return;
        }
        Ticket ticketAReanudar = this.ticketsPendientes.remove(numeroTicket);
        if (ticketAReanudar == null) {
            System.out.println("ERROR: No se encontró ningún ticket pendiente con el número " + numeroTicket);
            return;
        }
        this.ticketAtencion = ticketAReanudar;
        this.ticketAtencion.setEstado(Estado.EN_ATENCION);
        Nota notaReanuda = new Nota("Atención reanudada.", LocalDate.now());
        this.ticketAtencion.agregarNota(notaReanuda);
        this.acciones.push("Ticket " + numeroTicket + " reanudado desde PENDIENTE.");
        System.out.println("LOG: Reanudando atención del ticket " + numeroTicket);
    }

    public int getCantidadTicketsEspera () {
        return this.tickets.size();
    }

    public Queue<Ticket> getTickets () {
        return tickets;
    }

    public void setTickets (Queue < Ticket > tickets) {
        this.tickets = tickets;
    }

    public Queue<Ticket> getTicketsAtendidos () {
        return ticketsAtendidos;
    }

    public void setTicketsAtendidos (Queue < Ticket > ticketsAtendidos) {
        this.ticketsAtendidos = ticketsAtendidos;
    }

    public Map<String, Estudiante> getEstudiantes () {
        return estudiantes;
    }

    public void setEstudiantes (Map < String, Estudiante > estudiantes){
        this.estudiantes = estudiantes;
    }

    public Ticket getTicketAtencion () {
        return ticketAtencion;
    }

    public void setTicketAtencion (Ticket ticketAtencion){
        this.ticketAtencion = ticketAtencion;
    }

    public Stack<String> getAcciones () {
        return acciones;
    }

    public void setAcciones (Stack < String > acciones) {
        this.acciones = acciones;
    }
}