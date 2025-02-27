package es.udc.redes.tutorial.udp.server;

/*
 * Librerías necesarias para la correcta implementación del programa.
 */
import java.net.*;

/*
 * Servidor UDP de eco.
 * Este servidor escucha conexiones entrantes en un puerto específico,
 * recibe mensajes de los clientes y los devuelve sin modificaciones (eco).
 */
public class UdpServer {
    /*
     * Método principal que inicia el servidor UDP.
     * @param argv Argumentos de la línea de comandos. Se espera un parámetro:
     *      1. Número de puerto en el que el servidor escuchará conexiones.
     */
    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.udp.server.UdpServer <port_number>");
            System.exit(-1);
        }
        DatagramSocket socket = null;
        try {
            int port = Integer.parseInt(argv[0]);

            //* Crear un socket UDP en el puerto especificado.
            socket = new DatagramSocket(port);

            //* Establecer un tiempo de espera de 300 segundos (5 minutos).
            socket.setSoTimeout(300000);

            while (true) {
                byte[] input = new byte[1024];

                //* Crear un paquete para recibir datos.
                DatagramPacket packet = new DatagramPacket(input, input.length);
                socket.receive(packet);

                //* Convertir los datos recibidos en un mensaje de texto.
                String message = new String(packet.getData(), 0, packet.getLength());
                InetAddress address = packet.getAddress();
                int clientPort = packet.getPort();
                System.out.println("SERVER: Received " + message + " from " + address + ":" + clientPort);

                //* Preparar el mensaje de respuesta.
                byte[] output = message.getBytes();
                DatagramPacket response = new DatagramPacket(output, output.length, address, clientPort);
                socket.send(response);
                System.out.println("SERVER: Sending " + message + " to " + address + ":" + clientPort);

            }
        } catch (SocketTimeoutException e) {
            System.err.println("No requests received in 300 secs ");
        } catch (Exception e) {
            //* Manejo de error.
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {

            //* Cerrar el socket si está abierto
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
