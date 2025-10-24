package edu.unl.servicio;

import edu.unl.cc.dominio.Estudiante;
import edu.unl.cc.dominio.Ticket;
import edu.unl.cc.dominio.Nota;
import edu.unl.cc.dominio.Estado;
import edu.unl.cc.dominio.TipoTramite;
import edu.unl.cc.servicio.CentroAtencionEstudiantil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para CentroAtencionEstudiantil.
 */
public class CentroAtencionEstudiantilTest {

    private CentroAtencionEstudiantil centro;
    private Estudiante estudiante;

    @BeforeEach
    public void setUp() {
        centro = new CentroAtencionEstudiantil();
        estudiante = new Estudiante("Juan", "Pérez", "0102030405");
        centro.registrarEstudiante(estudiante);
    }

    @Test
    public void testRegistrarEstudiante() {
        // Verifica que el estudiante se registró correctamente en setUp()
        System.out.println("Mapa de estudiantes: " + centro.getEstudiantes());

        assertTrue(centro.getEstudiantes().containsKey("0102030405"));
        assertEquals("Juan", centro.getEstudiantes().get("0102030405").getNombre());
    }


    @Test
    public void testCrearTicketYAgregarloALaCola() {
        Ticket ticket = new Ticket("Certificado de estudios", TipoTramite.CERTIFICADO, estudiante);
        ticket.setNumero(centro.siguienteTicket());

        centro.crearTicket(ticket);

        // Verificamos que el ticket está en la cola
        assertEquals(1, centro.getCantidadTicketsEspera());
        assertEquals(ticket, centro.getTickets().peek());
    }

    @Test
    public void testAtenderTicketCambiaEstado() {
        Ticket ticket = new Ticket("Homologación", TipoTramite.HOMOLOGACION, estudiante);
        ticket.setNumero(centro.siguienteTicket());
        centro.crearTicket(ticket);

        centro.atenderTicket();

        assertNotNull(centro.getTicketAtencion());
        assertEquals(Estado.EN_ATENCION, centro.getTicketAtencion().getEstado());
    }

    @Test
    public void testFinalizarTicketMueveAAtendidos() {
        Ticket ticket = new Ticket("Constancia", TipoTramite.CONSTANCIA, estudiante);
        ticket.setNumero(centro.siguienteTicket());
        centro.crearTicket(ticket);
        centro.atenderTicket();

        centro.finalizarTicket();

        assertEquals(1, centro.getTicketsAtendidos().size());
        assertNull(centro.getTicketAtencion());
    }

    @Test
    public void testSiguienteTicketIncrementaNumero() {
        Ticket ticket1 = new Ticket("Certificado", TipoTramite.CERTIFICADO, estudiante);
        ticket1.setNumero(centro.siguienteTicket());
        centro.crearTicket(ticket1);

        int siguiente = centro.siguienteTicket();
        assertEquals(ticket1.getNumero() + 1, siguiente);
    }

    @Test
    public void testAgregarNotaAlTicketEnAtencion() {
        Ticket ticket = new Ticket("Homologación", TipoTramite.HOMOLOGACION, estudiante);
        ticket.setNumero(centro.siguienteTicket());
        centro.crearTicket(ticket);
        centro.atenderTicket();

        Nota nuevaNota = new Nota("Revisión de documentos", LocalDate.now());
        centro.agregarNota(nuevaNota);

        assertTrue(centro.getTicketAtencion().getNotas().stream()
                .anyMatch(n -> n.getObservacion().equals("Revisión de documentos")));
    }

    @Test
    public void testRecuperarEstudianteExistente() {
        Estudiante encontrado = centro.recuperarEstudiante("0102030405");
        assertNotNull(encontrado);
        assertEquals("Juan", encontrado.getNombre());
    }


    @Test
    public void testRecuperarEstudianteInexistente() {
        Estudiante noExiste = centro.recuperarEstudiante("9999999999");
        assertNull(noExiste);
    }
}
