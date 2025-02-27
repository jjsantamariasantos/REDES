package es.udc.redes.webserver;

/*
 * Librerías necesarias para la correcta implementación del programa.
 */
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * Clase ServerThread que maneja las conexiones individuales de clientes HTTP.
 * Extiende Thread para permitir múltiples conexiones simultáneas.
 */
public class ServerThread extends Thread {

    private final Socket socket;

    /*
     * Constructor de la clase ServerThread.
     * @param s Socket que se va a asociar a este hilo.
     */
    public ServerThread(Socket s) {
        this.socket = s;
    }

    /*
     * Método que se ejecuta al iniciar el hilo. Se encarga de gestionar la solicitud del cliente.
     */
    public void run() {
        try {
            //* Obtiene la dirección IP y el puerto del cliente.
            InetAddress address = socket.getInetAddress();
            int port = socket.getPort();

            //* Crea un BufferedReader para leer las peticiones del cliente
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //* Lee la primera línea de la petición HTTP.
            String request = input.readLine();
            System.out.println("SERVER: Received " + request + " from " + address + ":" + port);

            if (request != null) {
                //* Se divide la línea de la petición en partes (comando y recurso)
                String[] requestParts = request.split(" ");
                String command = requestParts[0];
                String requestResource = requestParts[1];
                String serverPath = "p1-files";

                //* Si el comando no es GET ni HEAD, se devuelve un error 400 (Bad Request).
                if(!command.equals("HEAD") && !command.equals("GET")) {
                    String filePath = serverPath + File.separator + "error400.html";
                    File error400File = new File(filePath);
                    handleBadRequest(socket, error400File);
                } else{
                    //* Si el comando es válido, se maneja la solicitud según el recurso.
                    File resource = new File(serverPath + requestResource);
                    File error404File = new File(serverPath + File.separator + "error404.html");

                    //* Si el comando es GET y el recurso existe, se maneja la solicitud GET.
                    if (command.equals("GET") && resource.exists()) {
                        handleGet(socket, input, resource);
                    }//* Si el comando es HEAD y el recurso existe, se maneja la solicitud HEAD.
                    else if(command.equals("HEAD") && resource.exists()) {
                        handleHead(socket,resource, "200 OK");
                    }//* Si el comando es GET y el recurso no existe, se devuelve un error 404 (Not Found).
                    else if(command.equals("GET") && !resource.exists()) {
                        handleNotFound(socket, error404File);
                    }//* Si el comando es HEAD y el recurso no existe, se devuelve un error 404 (Not Found).
                    else if(command.equals("HEAD") && !resource.exists()) {
                        handleHead(socket, error404File, "404 Not Found");
                    }
                }

                //* Cerrar los flujos
                input.close();
            }
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

    /*
     * Método que maneja una solicitud GET.
     * @param clientSocket Socket del cliente.
     * @param input Flujo de entrada para leer la solicitud.
     * @param resource Archivo que se solicita.
     * @throws IOException Si ocurre un error al leer o escribir datos.
     */
    public void handleGet(Socket clientSocket, BufferedReader input, File resource) throws IOException{
        boolean modifiedSince = true;
        String lastModified = getDateModified(resource);
        String response;

        //* Lee las cabeceras HTTP (como If-Modified-Since).
        String request = input.readLine();
        while (!request.isEmpty()) {
            String[] requestParts = request.split(": ");

            //* Si hay una cabecera If-Modified-Since, se verifica si el recurso ha sido modificado
            if (requestParts[0].equals("If-Modified-Since")) {
                modifiedSince = isModifiedSince(lastModified, requestParts[1]);
                System.out.println(request);
            }
            request = input.readLine();
        }
        //* Si el archivo ha sido modificado o no se recibió el encabezado If-Modified-Since, se responde con el recurso.
        if (modifiedSince) {
            response = getHTTPResponse("200 OK", resource, resource.toPath());
            sendHTTPResponse(socket, response);
            sendResource(clientSocket, resource);
        } else{
            //* Si el archivo no ha sido modificado, se responde con "304 Not Modified".
            response = getHTTPResponse("304 Not Modified", resource, null);
            sendHTTPResponse(socket, response);
        }
    }

    /*
     * Método que maneja una solicitud HEAD.
     * @param socket Socket del cliente.
     * @param resource Archivo solicitado.
     * @param code Código de estado HTTP a devolver.
     * @throws IOException Si ocurre un error al leer o escribir datos.
     */
    public void handleHead(Socket socket, File resource, String code) throws IOException{
        String response= getHTTPResponse(code, resource, resource.toPath());
        sendHTTPResponse(socket, response);
    }

    /*
     * Método que maneja una solicitud con error 400 (Bad Request).
     * @param socket Socket del cliente.
     * @param resource Archivo de error 400.
     * @throws IOException Si ocurre un error al leer o escribir datos.
     */
    public void handleBadRequest(Socket socket, File resource) throws IOException {
        String response = getHTTPResponse("400 Bad Request", resource, resource.toPath());
        sendHTTPResponse(socket, response);
        sendResource(socket, resource);
    }

    /*
     * Método que maneja una solicitud con error 404 (Not Found).
     * @param socket Socket del cliente.
     * @param resource Archivo de error 404.
     * @throws IOException Si ocurre un error al leer o escribir datos.
     */
    public void handleNotFound(Socket socket, File resource) throws IOException{
        String response = getHTTPResponse("404 Not Found", resource, resource.toPath());
        sendHTTPResponse(socket, response);
        sendResource(socket, resource);
    }

    /*
     * Método que obtiene la fecha y hora actual en formato HTTP.
     * @return Fecha y hora actual en formato HTTP.
     */
    private String getDate(){
        Date date = new Date();
        DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);

        return formatter.format(date);
    }

    /*
     * Método que obtiene la fecha de última modificación de un archivo en formato HTTP.
     * @param file Archivo del cual se obtiene la fecha de modificación.
     * @return Fecha de última modificación del archivo en formato HTTP.
     */
    private String getDateModified(File file){
        Date dateModified = new Date(file.lastModified());
        DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);

        return formatter.format(dateModified);
    }

    /*
     * Método que construye la respuesta HTTP a enviar al cliente.
     * @param code Código de estado HTTP (200 OK, 404 Not Found, etc.).
     * @param resource Recurso solicitado.
     * @param path Ruta del archivo solicitado.
     * @return La cabecera de la respuesta HTTP en forma de cadena.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    private String getHTTPResponse(String code, File resource, Path path) throws IOException{
        String n = "\r\n";
        StringBuilder sB = new StringBuilder();
        sB.append("HTTP/1.0 ").append(code).append(n);
        sB.append("Date: ").append(getDate()).append(n);
        sB.append("Server: ficServer/0.0.1 (Java)").append(n);
        if(path != null){
            sB.append("Last-Modified: ").append(getDateModified(resource)).append(n);
            long bitsLength = Files.size(path);
            sB.append("Content-Length: ").append(bitsLength).append(n);
            String contentType = Files.probeContentType(path);
            sB.append("Content-Type: ").append(contentType).append(n);
        }
        sB.append(n);

        return sB.toString();
    }

    /*
     * Método que verifica si un recurso ha sido modificado desde la última solicitud del cliente.
     * @param serverDate Fecha de la última modificación en el servidor.
     * @param clientDate Fecha de la última modificación enviada por el cliente.
     * @return true si el archivo ha sido modificado; false en caso contrario.
     */
    private boolean isModifiedSince(String serverDate, String clientDate) {
        DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        try{
            Date lastModifiedDate = formatter.parse(serverDate);
            Date modifiedSinceDate = new Date(clientDate);

            return lastModifiedDate.after(modifiedSinceDate);
        }catch (ParseException e) {
            //* Manejo de error al hacer Parse.
            System.err.println("Error parsing dates: " + e.getMessage());

            return false;
        }
    }

    /*
     * Método que envía la respuesta HTTP al cliente.
     * @param socket Socket del cliente.
     * @param response Respuesta HTTP que se enviará.
     * @throws IOException Si ocurre un error al escribir datos al cliente.
     */
    private void sendHTTPResponse(Socket socket, String response) throws IOException{
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        System.out.println("SERVER: Sending response from "
                + socket.getLocalAddress() + ":" + socket.getPort()
                + "\nRenponse:\n" + response);
        output.write(response);
        output.flush();
    }

    /*
     * Método que envía el recurso solicitado al cliente.
     * @param clientSocket Socket del cliente.
     * @param resource Archivo que se enviará.
     * @throws IOException Si ocurre un error al leer o escribir datos.
     */
    private void sendResource(Socket clientSocket, File resource) throws IOException{
        OutputStream output = clientSocket.getOutputStream();
        FileInputStream input = new FileInputStream(resource);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        input.close();
        output.close();
    }
}