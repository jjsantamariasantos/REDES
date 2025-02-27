package es.udc.redes.tutorial.copy;
/*
 * Librerías necesarias para la correcta implementación del programa.
 */
import java.io.*;

/*
 * Clase copy que permite copiar el contenido de un archivo de texto o binario a otro.
 */
public class Copy {
    /*
     *  Método principal que ejecuta la copia de archivos.
     *  @param args Argumentos de la línea de comandos. Se esperan dos parámetro:
     *      1. Nombre del archivo de origen.
     *      2. Nombre del archivo de destino.
     */
    public static void main (String[] args){

        //* Comprueba si el número de argumentos es correcto.
        if (args.length != 2) {
            System.err.println("Error: Invalid number of arguments\nUsage: java es.udc.redes.tutorial.copy.Copy <source file> <destination file>");
            System.exit(-1);
        }

        //* Obtención de los nombres de los archivos de los argumentos.
        String fileOrigin = args[0];
        String fileDestination = args[1];

        typeGuesser(fileOrigin, fileDestination);
    }

    /*
     * Método que determina si el archivo es de texto o binario.
     * Lee los primeros bytes del archivo y analiza su contenido.
     * Si el porcentaje de bytes no imprimibles es superior al 5%, se trata como archivo binario.
     * @param inputFile  Archivo de origen.
     * @param outputFile Archivo de destino.
     */
    private static void typeGuesser(String inputFile, String outputFile) {
        FileInputStream input = null;
        try{
            input = new FileInputStream(inputFile);
            byte[] buffer = new byte[512];
            int bytesRead = input.read(buffer);

            int nonTextBytes = 0;
            for (int i = 0; i < bytesRead; i++) {
                //* pasa el byte a entero sin signo.
                int b = buffer[i] & 0xFF;
                if((b < 32 || b > 126) && b != 9 && b != 10 && b != 13){
                    nonTextBytes++;
                }
            }
            if (nonTextBytes > bytesRead * 0.05) {
                //* Si más del 5% de los bytes no son caracteres imprimibles, se trata como binario.
                copyBinaryFile(inputFile, outputFile);
            } else{
                //* Caso contrario, se trata como archivo de texto.
                copyTextFile(inputFile, outputFile);
            }

        }catch (FileNotFoundException e){
            //* Manejo de error si el archivo no se encuentra.
            System.err.println("Error: "+ e.getMessage());
        } catch (IOException e){
            //* Manejo de error al leer el archivo.
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

    }

    /*
     * Copia un archivo de texto desde una ubicación de origen a una de destino.
     * Este método lee el contenido de un archivo de texto línea por línea y lo escribe
     * en otro archivo. Utiliza `BufferedReader` para la lectura y `PrintWriter` con `BufferedWriter`
     * para la escritura, mejorando la eficiencia en archivos grandes.
     * @param sourceFile      Ruta del archivo de origen.
     * @param destinationFile Ruta del archivo de destino.
     */
    private static void copyTextFile(String sourceFile, String destinationFile) {
        //* Declaración de flujos de entrada y salida.
        BufferedReader inputStream = null;
        PrintWriter outputStream = null;

        try{
            //* Apertura del archivo de origen en modo lectura con Buffer.
            inputStream = new BufferedReader(new FileReader(sourceFile));

            //* Apertura del archivo de destino en modo de escritura con Buffer.
            outputStream = new  PrintWriter(new BufferedWriter(new FileWriter(destinationFile)));

            //* Lectura y escritura carácter por carácter hasta el final del archivo.
            int c;
            while ((c = inputStream.read()) != -1) {
                outputStream.write(c);
            }

        }catch (FileNotFoundException e){
            //* Manejo de error si el archivo no se encuentra.
            System.err.println("Error: "+ e.getMessage());
        } catch (IOException e){
            //* Manejo de error de I/O.
            System.err.println("I/0 error: "+ e.getMessage());
        } finally {
            try{
                //* Cierre de los flujos si están abiertos.
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                //* Manejo de error en el cierre de archivos.
                System.err.println("I/0 error: " + e.getMessage());
            }
        }
    }

    /*
     * Copia un archivo binario de una ubicación a otra.
     * Este método lee un archivo binario desde la ubicación especificada en `sourceFile`
     * y lo copia en la ubicación especificada en `destinationFile`.
     * @param sourceFile      Ruta del archivo de origen.
     * @param destinationFile Ruta del archivo de destino.
     */
    private static void copyBinaryFile( String sourceFile, String destinationFile) {
        //* Declaración de flujos de entrada y salida.
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;

        try{
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[4096];

            //* Lee el archivo por bloques y lo copia en el destino.
            int b;
            while ((b = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, b);
            }
            outputStream.flush();

        } catch (FileNotFoundException e) {
            //* Manejo de error si el archivo no se encuentra.
            System.err.println("Error: "+ e.getMessage());
        } catch (IOException e){
            //* Manejo de error de I/0.
            System.err.println("I/0 error: "+ e.getMessage());
        } finally {
            try{
                //* Cierre de los flujos si están abiertos.
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                //* Manejo de error en el cierre de archivos.
                System.err.println("I/0 error: "+ e.getMessage());
            }
        }
    }
}