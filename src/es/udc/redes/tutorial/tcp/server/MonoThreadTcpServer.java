package es.udc.redes.tutorial.tcp.server;

/*
 * Librerías necesarias para la correcta implementación del programa.
 */
import java.net.*;
import java.io.*;

/*
 * Servidor TCP monohilo (MonoThread TCP Echo Server).
 * Este servidor escucha conexiones entrantes en un puerto específico, recibe mensajes
 * de los clientes y los devuelve sin modificaciones (eco).
 */
public class MonoThreadTcpServer {
    /*
     * Método principal que inicia el servidor.
     * @param argv Argumentos de la línea de comandos. Se espera un parámetro:
     *      1. Número de puerto en el que el servidor escuchará conexiones.
     */
    public static void main(String[] argv) {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.tcp.server.MonoThreadTcpServer <port>");
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
                try{
                    //* Esperar una conexión entrante.
                    Socket clientSocket = listeningSocket.accept();
                    InetAddress clientAddress = clientSocket.getInetAddress();
                    int clientPort = clientSocket.getPort();

                    //* Crear un canal de entrada para recibir datos del cliente.
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    //* Crear un canal de salida para enviar datos al cliente.
                    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                    //* Recibir el mensaje del cliente.
                    String line = input.readLine();
                    System.out.println("SERVER: Received "+ line + " from " + clientAddress + ":" + clientPort);

                    //* Enviar la respuesta al cliente (eco).
                    output.writeBytes(line + "\n");
                    System.out.println("SERVER: Sending "+ line + " to " + clientAddress + ":" + clientPort);

                    //* Cerrar los flujos y el socket del cliente.
                    input.close();
                    output.close();
                    clientSocket.close();
                }catch (Exception e) {
                    //* Manejo de error del cliente.
                    System.err.println("Error handling client: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SocketTimeoutException e) {
            //* Manejo de error de timeout.
            System.err.println("Nothing received in 300 secs ");
        } catch (Exception e) {
            //* Manejo de error.
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            //* Cerrar el socket del servidor si está abierto.
            if (listeningSocket != null && !listeningSocket.isClosed()) {
                try{
                    listeningSocket.close();
                } catch (Exception e){
                    //* Manejo de error cerrando el socket.
                    System.err.println("Error closing socket: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
