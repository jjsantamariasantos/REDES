package es.udc.redes.tutorial.tcp.server;

/*
 * Librerías necesarias para la correcta implementación del programa.
 */
import java.net.*;

/*
 * Servidor web que maneja solicitudes HTTP en un puerto específico.
 * Este servidor escucha conexiones entrantes en un puerto determinado
 * y crea un hilo independiente para manejar cada cliente de forma concurrente.
 * El servidor responde a solicitudes GET y HEAD, y maneja errores como solicitudes incorrectas (400) y archivos no encontrados (404).
 */
public class TcpServer {

  /*
   * Método principal que inicia el servidor.
   * @param argv Argumentos de la línea de comandos. Se espera un parámetro:
   *      1. Número de puerto en el que el servidor escuchará conexiones.
   */
  public static void main(String argv[]) {
    if (argv.length != 1) {
      System.err.println("Format: es.udc.redes.tutorial.tcp.server.TcpServer <port>");
      System.exit(-1);
    }
    ServerSocket listeningSocket = null;
    try {
      int port = Integer.parseInt(argv[0]);

      //* Crear un socket de servidor que escucha en el puerto especificado.
      listeningSocket = new ServerSocket(port);

      //* Establecer un tiempo de espera de 300 segundos (5 minutos).
      listeningSocket.setSoTimeout(300000);

      while (true) {
        //* Esperar una conexión entrante.
        Socket connectionSocket = listeningSocket.accept();

        //* Crear un hilo para manejar la conexión con el cliente.
        ServerThread thread = new ServerThread(connectionSocket);
        thread.start();
      }
    } catch (SocketTimeoutException e) {
      //* Manejo de error de timeout.
      System.err.println("Nothing received in 300 secs");
    } catch (Exception e) {
      //* Manejo de error.
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace();
     } finally{
      try {
        //* Cerrar el socket del servidor si está abierto.
        if (listeningSocket != null && !listeningSocket.isClosed()) {
          listeningSocket.close();
        }
      } catch (Exception e) {
        //* Manejo de error cerrando el socket.
        System.err.println("Error closing socket: " + e.getMessage());
      }
    }
  }
}
