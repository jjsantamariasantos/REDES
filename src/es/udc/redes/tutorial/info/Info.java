package es.udc.redes.tutorial.info;
/*
 * Librerías necesarias para la correcta implementación del programa.
 */
import java.io.File;
import java.util.Date;

/*
 * Clase Info que permite obtener información detallada sobre un archivo o directorio.
 */
public class Info {
    /*
     * Método principal que ejecuta la obtención de información del archivo o directorio.
     * @param args Argumentos de la línea de comandos. Se espera un parámetro:
     *      1. Nombre del archivo o directorio a analizar.
     */
    public static void main(String[] args) {

        //* Comprueba si el número de argumentos es correcto.
        if (args.length != 1) {
            System.err.println("Error: Invalid number of arguments\nUsage: java es.udc.redes.tutorial.info.Info <relative path>");
            System.exit(-1);
        }

        //* Obtención del nombre del archivo o directorio desde los argumentos.
        String input = args[0];
        File inputFile = new File(input);

        //* Verificación de la existencia del archivo o directorio.
        if (!inputFile.exists()){
            System.err.println("Error: Input file " + inputFile + " does not exist");
            System.exit(1);
        }

        //* Obtención del tamaño del archivo en bytes.
        long size = inputFile.length();

        //* Obtención de la última fecha de modificación.
        long lastModified = inputFile.lastModified();
        Date lastModifiedDate = new Date(lastModified);

        //* Obtención del nombre y la extensión del archivo.
        String name = inputFile.getName();
        String nameExtension, extension;
        int lastPoint = name.lastIndexOf(".");
        if (lastPoint != -1 && lastPoint < name.length()-1) {
            nameExtension = name.substring(0, lastPoint);
            extension = name.substring(lastPoint + 1);
        } else{
            nameExtension = name;
            extension = "";
        }

        //* Determinación del tipo de archivo.
        String fileType = getFileType(extension, inputFile);

        //* Obtención de la ruta absoluta del archivo o directorio.
        String path = inputFile.getAbsolutePath();

        //* Impresión de la información obtenida.
        System.out.println("size: " + size +
                "\nlast modification: " + lastModifiedDate +
                "\nname: " + nameExtension);
        if (!extension.isEmpty()) {
            System.out.println("extension: " + extension);
        }
        System.out.println("filetype: " + fileType +
                "\nabsolute path: " + path);
    }

    /*
     * Método que determina el tipo de archivo basado en su extensión o si es un directorio.
     * @param extension Extensión del archivo.
     * @param file Objeto File que representa el archivo o directorio.
     * @return Tipo de archivo en forma de cadena de texto.
     */
    private static String getFileType(String extension, File file) {
        String fileType;

        if (!extension.isEmpty()){
            if (extension.equals("jpeg") || extension.equals("jpg") || extension.equals("png") || extension.equals("bmp")) {
                fileType = "image";
            } else if (extension.equals("txt") || extension.equals("doc") || extension.equals("docx") || extension.equals("odt")) {
                fileType = "text";
            } else {
                fileType = "unknown";
            }
        } else {
            if (file.isDirectory()) {
                fileType = "directory";
            } else {
                fileType = "unknown";
            }
        }
        return fileType;
    }
}
