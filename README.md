
REQUERIMIENTOS:
- Recepcion de nuevos casos
- Atencion uno por uno
- Se pueden registrar notas
- Hacer y deshacer cambios en observaciones
- Generar un historial de acciones
- Cerrar el caso
- Consulta de casos en espera

CATALOGO DE ESTADOS POSIBLES DE UN TICKET
- EN COLA
- EN ATENCIÓN
- COMPLETADO

CASOS BORDES
- Estructuras vacías
- Eliminación de notas inexistentes
- Deshacer/rehacer 
- Cambio de estado
- Sin caso en atención

Lista
- Cada estudiante tiene una singly linked list para almacenar las notas/observaciones
- Se debe insertar notas al inicio de la lista
- Se debe eliminar la primera nota que coincida
- Se debe recorrer la lista para mostar todas las notas

Manual de ejecucion:
- Primeramente debe dirigirse a la clase Consola y ejecutarlo
- Se desplegará un menú, elija la opción 1 e ingrese el numero de estudiantes que desee 
- Posteriormente agende los tickets ingresando el numero de cedula del estudiante que quiere agendar un turno (opcion 2)
- En la opcion 3 podrá atender el ticket correspondiente, se dará a escoger el tipo de trámite
- Al elegir la opcion 4 se podrá registar una nota/observaciones del proceso que se guardaran con la fecha y hora
- En la opcion 5 se podra eliminar una nota creada
- En la opcion 6 se podra rehacer una nota eliminada
- Al escoger la opcion 7 se dara fin a la atencion del ticket
- Se podra consultar el ticket que está en espera en la opcion 8 
- En la opcion 9 se podra consultar el historial de notal del ticket con su fecha y hora

Diseño (UML)

 ____________________________        ---------------
 |          Ticket          |        | TipoTramite  |
 ----------------------------        | <Enumeration>|           _________________
 |-descripcion:String       |------> ----------------           |     Estado    |
 |-estado:Estado            |1      1|CERTIFICADO   |           | <Enumeration> |
 |-numero:int               |        |CONSTANCIA    |           ________________|
 |-tipoTramite:TipoTramite  |        |HOMOLOGACION  |           |EN_COLA        |
 |-estudiante:Estudiate     |        ----------------           |EN_ATENCIOM    |
 ----------------------------  ------------------------------>  |COMPLETADO     |
 |__________________________|                                   ----------------
    | n                 | 0    0.*    ___________________        
    | es ingresado      | --------> |       Nota        |
    V n                             ---------------------
_______________________             |-observacion:String|
|     Estudiante       |            |-fecha:LocalDate   |
------------------------            ---------------------
|-nombreCompleto:String|            |____________________|
|-cedula:String        |
------------------------
|______________________|


________________________________________________________         _______________________________________________________
|                CentroAtencionEstudiantil              |       |                      Consola                         |
--------------------------------------------------------         -------------------------------------------------------
|-acciones:Stack                                        |       |-centroAtencionEstudiantil : CentroAtencionEstudiantil|
|-tickets: Queue                                        |       --------------------------------------------------------
|-ticketsAtendidos                                      |       |+main(args:String)                                    |
|-estudiantes:Map<String Estudiante>                    |       |+mostrarMenuPrincipal()                               |
|-tickesAtencion:Ticket                                 |       |+manejarOpcion(opcion)                                |
|-accionesRevertidas: Stack                             |       |+mostrarMenuRegistrarEstudiantes()                    |
---------------------------------------------------------       |+mostrarMenuAgregarTicket()                           |
|+registarEstudiantes(Estudiante:estudiante)            |       |+mostrarMenuRegistrarObservaciones()                  |
|+crearTicket(Ticket:ticket)        s                    |       |+menuConsultarTicketEspera()                          |
|+atenderTicket()                                       |       |+consultarHistorialTicket()                           |
|+finalizarTicket()                                     |       --------------------------------------------------------
|+agregarNota(Nota nota):void                           |
|+buscarTicketPorNumero(int numero):Ticket              |
|+consultarHistorial(Ticket ticket)                     |
|+siguienteTicket():int                                 |
|+recuperarEstudiante(String cedula:Estudiante          |
|+undo():void                                           |
|+redo():void                                           |
---------------------------------------------------------