package edu.unl.servicio;

import edu.unl.cc.dominio.*;
import edu.unl.cc.servicio.CentroAtencionEstudiantil;
import org.junit.jupiter.api.BeforeEach; // Importante: JUnit 5
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

// Importaciones estáticas para legibilidad (assertEquals, assertTrue, etc.)
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de prueba para CentroAtencionEstudiantil.
 * Utiliza JUnit 5 (Jupiter).
 */
class CentroAtencionEstudiantilTest {

    // --- Variables de instancia para las pruebas ---
    private CentroAtencionEstudiantil centro;
    private Estudiante est1;
    private Estudiante est2;
    private Ticket ticketNormal;
    private Ticket ticketUrgente;

    /**
     * Este método se ejecuta ANTES de CADA prueba (@Test).
     * Asegura que cada prueba comience con un "centro" limpio y nuevo.
     */
    @BeforeEach
    void setUp() {
        // 1. Inicializa el objeto principal a probar
        centro = new CentroAtencionEstudiantil();

        // 2. Crea datos de prueba comunes
        est1 = new Estudiante("Juan", "Perez", "1100111001");
        est2 = new Estudiante("Ana", "Gomez", "1100211002");

        // 3. Pre-registra estudiantes para que los tickets sean válidos
        centro.registrarEstudiante(est1);
        centro.registrarEstudiante(est2);

        // 4. Pre-define tickets comunes
        ticketNormal = new Ticket("desc", TipoTramite.CERTIFICADO, est1, false);
        ticketUrgente = new Ticket("desc", TipoTramite.HOMOLOGACION, est2, true);
    }

    // --- Pruebas de Registrar Estudiante ---

    @Test
    @DisplayName("Debe registrar un estudiante nuevo correctamente")
    void testRegistrarEstudiante_Exitoso() {
        Estudiante est3 = new Estudiante("Luis", "Soto", "1122334455");

        // Acción
        boolean resultado = centro.registrarEstudiante(est3);

        // Verificación
        assertTrue(resultado, "El registro de un nuevo estudiante debe devolver true");
        assertNotNull(centro.recuperarEstudiante("1122334455"), "El estudiante debe existir en el mapa");
        assertEquals("Luis", centro.recuperarEstudiante("1122334455").getNombre());
        assertEquals(3, centro.getEstudiantes().size(), "Debe haber 3 estudiantes registrados");
    }

    @Test
    @DisplayName("No debe registrar un estudiante duplicado (misma cédula)")
    void testRegistrarEstudiante_Duplicado() {
        Estudiante est1Duplicado = new Estudiante("Juan", "Perez Repetido", "1100111001");

        // Acción
        boolean resultado = centro.registrarEstudiante(est1Duplicado);

        // Verificación
        assertFalse(resultado, "El registro de un duplicado debe devolver false");
        assertEquals(2, centro.getEstudiantes().size(), "El tamaño del mapa no debe cambiar");
        assertEquals("Juan", centro.recuperarEstudiante("1100111001").getNombre(), "Debe conservar el estudiante original");
    }

    // --- Pruebas de Crear y Atender Ticket ---

    @Test
    @DisplayName("Crear ticket debe encolar en 'normal' si no es urgente")
    void testCrearTicket_Normal() {
        centro.crearTicket(ticketNormal);
        assertEquals(1, centro.getCantidadTicketsEspera(), "La cola normal debe tener 1 ticket");
        assertTrue(centro.getTicketsUrgentes().isEmpty(), "La cola urgente debe estar vacía");
    }

    @Test
    @DisplayName("Crear ticket debe encolar en 'urgente' si es urgente")
    void testCrearTicket_Urgente() {
        centro.crearTicket(ticketUrgente);
        assertEquals(0, centro.getCantidadTicketsEspera(), "La cola normal debe estar vacía");
        assertEquals(1, centro.getTicketsUrgentes().size(), "La cola urgente debe tener 1 ticket");
    }

    @Test
    @DisplayName("Atender ticket debe priorizar la cola urgente")
    void testAtenderTicket_PriorizaUrgentes() {
        // Añadimos primero el normal, luego el urgente
        centro.crearTicket(ticketNormal);
        centro.crearTicket(ticketUrgente);

        // Acción
        centro.atenderTicket(); // Debe tomar el URGENTE (ticket 2)

        // Verificación
        assertNotNull(centro.getTicketAtencion());
        assertEquals(2, centro.getTicketAtencion().getNumero(), "El ticket en atención debe ser el urgente");
        assertEquals(Estado.EN_ATENCION, centro.getTicketAtencion().getEstado());
        assertEquals(1, centro.getCantidadTicketsEspera(), "El ticket normal debe seguir en espera");
        assertTrue(centro.getTicketsUrgentes().isEmpty(), "La cola urgente debe quedar vacía");
        assertTrue(centro.getAcciones().peek().contains("URGENTE"), "La acción debe registrar que fue urgente");
    }

    @Test
    @DisplayName("Atender ticket debe tomar de cola normal si no hay urgentes")
    void testAtenderTicket_TomaNormal() {
        centro.crearTicket(ticketNormal);

        // Acción
        centro.atenderTicket(); // Debe tomar el normal (ticket 1)

        // Verificación
        assertNotNull(centro.getTicketAtencion());
        assertEquals(1, centro.getTicketAtencion().getNumero());
        assertEquals(Estado.EN_ATENCION, centro.getTicketAtencion().getEstado());
        assertTrue(centro.getTickets().isEmpty(), "La cola normal debe quedar vacía");
        assertFalse(centro.getAcciones().peek().contains("URGENTE"));
    }

    @Test
    @DisplayName("Atender ticket no debe hacer nada si las colas están vacías")
    void testAtenderTicket_ColasVacias() {
        centro.atenderTicket();
        assertNull(centro.getTicketAtencion(), "No debe haber nadie en atención");
        assertTrue(centro.getAcciones().isEmpty(), "No se debe registrar ninguna acción");
    }

    @Test
    @DisplayName("Atender ticket debe fallar si ya hay uno en atención")
    void testAtenderTicket_YaEnAtencion() {
        centro.crearTicket(ticketNormal);
        centro.atenderTicket(); // ticket 1 está en atención

        centro.crearTicket(ticketUrgente); // ticket 2 llega a la cola urgente

        // Acción
        centro.atenderTicket(); // Intento atender ticket 2, debe fallar

        // Verificación
        assertEquals(1, centro.getTicketAtencion().getNumero(), "Debe seguir el ticket 1 en atención");
        assertEquals(1, centro.getTicketsUrgentes().size(), "El ticket 2 debe seguir en la cola urgente");
    }

    // --- Pruebas de Finalizar, Pendiente y Reanudar ---

    @Test
    @DisplayName("Debe finalizar un ticket en atención correctamente")
    void testFinalizarTicket_Exitoso() {
        centro.crearTicket(ticketUrgente);
        centro.atenderTicket(); // ticket 2 en atención

        // Acción
        centro.finalizarTicket();

        // Verificación
        assertNull(centro.getTicketAtencion(), "La ventanilla debe quedar libre");
        assertEquals(1, centro.getTicketsAtendidos().size(), "Debe haber 1 ticket en la cola de atendidos");
        Ticket tAtendido = centro.getTicketsAtendidos().peek();
        assertEquals(2, tAtendido.getNumero());
        assertEquals(Estado.COMPLETADO, tAtendido.getEstado());
        assertTrue(centro.getAcciones().peek().contains("finalizado"), "La acción debe registrar la finalización");
    }

    @Test
    @DisplayName("Debe marcar un ticket como pendiente")
    void testMarcarPendiente_Exitoso() {
        centro.crearTicket(ticketNormal);
        centro.atenderTicket(); // ticket 1 en atención

        String motivo = "Falta foto";
        centro.marcarPendiente(motivo);

        assertNull(centro.getTicketAtencion(), "La ventanilla debe quedar libre");
        // El ticket ya no está en las colas, pero podemos buscarlo
        Ticket tPendiente = centro.buscarTicketPorNumero(1);
        assertNotNull(tPendiente);
        assertEquals(Estado.PENDIENTE_DOCS, tPendiente.getEstado());
        assertTrue(centro.getAcciones().peek().contains(motivo), "La acción debe registrar el motivo");
    }

    @Test
    @DisplayName("Debe reanudar un ticket pendiente si la atención está libre")
    void testReanudarTicket_Exitoso() {
        centro.crearTicket(ticketNormal);
        centro.atenderTicket();
        centro.marcarPendiente("Falta foto"); // ticket 1 queda pendiente

        // Acción
        boolean resultado = centro.reanudarTicket(1);

        // Verificación
        assertTrue(resultado);
        assertNotNull(centro.getTicketAtencion());
        assertEquals(1, centro.getTicketAtencion().getNumero());
        assertEquals(Estado.EN_ATENCION, centro.getTicketAtencion().getEstado());
        assertTrue(centro.getAcciones().peek().contains("reanudado"), "La acción debe registrar la reanudación");
    }

    @Test
    @DisplayName("No debe reanudar ticket pendiente si la atención está ocupada")
    void testReanudarTicket_AtencionOcupada() {
        centro.crearTicket(ticketNormal); // t1
        centro.atenderTicket();
        centro.marcarPendiente("Falta foto"); // t1 pendiente

        centro.crearTicket(ticketUrgente); // t2
        centro.atenderTicket(); // t2 en atención

        // Acción
        boolean resultado = centro.reanudarTicket(1); // Intentamos reanudar t1

        // Verificación
        assertFalse(resultado, "No debe reanudar si la atención está ocupada");
        assertEquals(2, centro.getTicketAtencion().getNumero(), "t2 debe seguir en atención");
        assertNotNull(centro.buscarTicketPorNumero(1), "t1 debe seguir existiendo");
    }

    // --- Pruebas de Undo/Redo (Casos simples) ---

    @Test
    @DisplayName("Debe deshacer (undo) una nota agregada")
    void testUndo_AgregarNota() {
        centro.crearTicket(ticketNormal);
        centro.atenderTicket(); // t1 en atención, tiene 1 nota ("Inicio de atención")

        Nota notaExtra = new Nota("Esta es una nota de prueba", LocalDate.now());
        centro.agregarNota(notaExtra); // t1 ahora tiene 2 notas

        assertEquals(2, centro.getTicketAtencion().getNotas().size());

        // Acción
        centro.undo(); // Deshacer "Nota agregada"

        // Verificación
        assertEquals(1, centro.getTicketAtencion().getNotas().size(), "Debe quedar solo la nota de inicio");
        assertFalse(centro.getAccionesRevertidas().isEmpty(), "La pila de revertidas debe tener 1 elemento");
    }

    @Test
    @DisplayName("Debe rehacer (redo) una nota deshecha")
    void testRedo_AgregarNota() {
        centro.crearTicket(ticketNormal);
        centro.atenderTicket();
        Nota notaExtra = new Nota("Nota de prueba", LocalDate.now());
        centro.agregarNota(notaExtra);

        centro.undo(); // Deshace la nota, t1 vuelve a tener 1 nota
        assertEquals(1, centro.getTicketAtencion().getNotas().size());

        // Acción
        centro.redo(); // Rehace la nota

        // Verificación
        assertEquals(2, centro.getTicketAtencion().getNotas().size(), "Debe volver a tener 2 notas");
        assertEquals("Nota de prueba", centro.getTicketAtencion().getNotas().get(1).getObservacion());
        assertTrue(centro.getAccionesRevertidas().isEmpty(), "La pila de revertidas debe quedar vacía");
    }
}