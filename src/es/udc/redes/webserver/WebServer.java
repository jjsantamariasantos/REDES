package es.udc.redes.webserver;

/*
 * Librerías necesarias para la correcta implementación del programa.
 */
import java.net.*;

/*
 *
 *
 */
public class WebServer {

    /*
     * Método principal que inicia el servidor web.
     * @param args Argumentos de la línea de comandos. Se espera un solo parámetro:
     *      1. Número de puerto en el que el servidor escuchará conexiones.
     */
    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("Format: es.udc.redes.webserver.WebServer <port>");
            System.exit(-1);
        }
        ServerSocket listeningSocket = null;
        int port = Integer.parseInt(args[0]);
        try{
            //* Crear un socket de servidor que escucha en el puerto especificado.
            listeningSocket = new ServerSocket(port);

            System.out.println("Web server started on port " + port);

            //* Establecer un tiempo de espera de 300 segundos (5 minutos).
            listeningSocket.setSoTimeout(300000);

            while(true) {
                //* Esperar una conexión entrante.
                Socket connectionSocket = listeningSocket.accept();

                //* Crear un hilo para manejar la conexión con el cliente.
                ServerThread thread = new ServerThread(connectionSocket);
                thread.start();
            }
        } catch (SocketTimeoutException e){
            //* Manejo de error de timeout.
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e){
            //* Manejo de error.
            System.err.println("Error: " + e.getMessage());
        } finally {
            try{
                //* Cerrar el socket si está abierto
                if (listeningSocket != null && !listeningSocket.isClosed()) {
                    listeningSocket.close();
                }
            } catch (Exception e){
                //* Manejo de error cerrando el socket.
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}