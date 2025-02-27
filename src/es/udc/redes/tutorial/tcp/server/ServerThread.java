package es.udc.redes.tutorial.tcp.server;

/*
 * Librerías necesarias para la correcta implementación del programa.
 */
import java.net.*;
import java.io.*;

/*
 * Hilo de ejecución que maneja una conexión de un cliente en el servidor TCP.
 * Esta clase extiende Thread y se encarga de recibir un mensaje del cliente,
 * imprimirlo en consola y enviarlo de vuelta como respuesta (eco).
 */
public class ServerThread extends Thread {
  //* Socket asociado a la conexión con el cliente.
  private final Socket socket;

  /*
   * Constructor de la clase ServerThread.
   * @param s Socket de comunicación con el cliente.
   */
  public ServerThread(Socket s) {

    this.socket = s;
  }

  /*
   * Método principal del hilo que maneja la comunicación con el cliente.
   *    - Recibe un mensaje del cliente.
   *    - Lo imprime en consola con la dirección y puerto del cliente.
   *    - Envía el mismo mensaje de vuelta (eco).
   *    - Cierra los flujos de entrada y salida.
   */
  public void run() {
    try {
      //* Obtener información del cliente.
      InetAddress address = socket.getInetAddress();
      int port = socket.getPort();

      //* Crear los canales de entrada y salida
      BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());

      //* Leer el mensaje del cliente
      String line = input.readLine();
      System.out.println("SERVER: Received "+ line + " from " + address + ":" + port);

      //* Enviar el mensaje de vuelta (eco)
      output.writeBytes(line +"\n");
      System.out.println("SERVER: Sending "+ line + " to " + address + ":" + port);

      //* Cerrar los flujos
      input.close();
      output.close();
    } catch (SocketTimeoutException e) {
      //* Manejo de error de timeout.
      System.err.println("Nothing received in 300 secs");
    } catch (Exception e) {
      //* Manejo de error.
      System.err.println("Error: " + e.getMessage());
    } finally {
      try {
        //* Cerrar el socket.
        socket.close();
      } catch (IOException e) {
        //* Manejo de error cerrando el socket.
        System.err.println("Error closing socket: " + e.getMessage());
      }
    }
  }
}
