package generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class bmpGenerator {

    /**
     * Metodo necesario para crear little endians, es el formato estandar para
     * valores numericos en bmps Java usa por defecto big endian, así que hay
     * que leer desde el final para escribirlo en LE. Se usa ints en caso de que
     * ocupe 4 bits
     *
     * @param value
     * @return
     */
    private static byte[] intToLittleEndianBytes(int value) {
        return new byte[]{
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)
        };
    }

    /**
     * Metodo necesario para crear little endians, es el formato estandar para
     * valores numericos en bmps Java usa por defecto big endian, así que hay
     * que leer desde el final para esribirlo en LE. Se usa shorts en caso de
     * que ocupe 2 bits
     *
     * @param value
     * @return
     */
    private static byte[] shortToLittleEndianBytes(short value) {
        return new byte[]{
            (byte) (value & 0xFF), // Byte menos significativo
            (byte) ((value >> 8) & 0xFF) // Byte más significativo
        };
    }

    /**
     * Genera el encabezado del archivo, 14 bytes con informacion sobre el
     * propio archivo
     *
     * @param tamaño
     * @return
     */
    private static byte[] FileHeader(int tamaño) {
        byte[] rt = new byte[14];

        //Bytes 0-1 de la cabecera, declaran el tipo
        rt[0] = (byte) 'B';
        rt[1] = (byte) 'M';

        //2-3-4-5, tamaño del archivo. Hay que generarlos como little endian
        byte[] bytesTamaño = intToLittleEndianBytes(tamaño);
        rt[2] = bytesTamaño[0];
        rt[3] = bytesTamaño[1];
        rt[4] = bytesTamaño[2];
        rt[5] = bytesTamaño[3];
        //NOTA: Estoy evitando usar bucles deliberadamente para mantener una claridad visual de que se guarda en cada byte

        //6-7-8-9, espacios reservados, deben ser 0
        rt[6] = 0;
        rt[7] = 0;
        rt[8] = 0;
        rt[9] = 0;

        //10-11-12-13, offset. AKA cual es el primer byte relativo al aspecto de la imagen, para un bmp es cuando termina la cabecera en el byte 54
        byte[] bytesOffset = intToLittleEndianBytes(54);
        rt[10] = bytesOffset[0];
        rt[11] = bytesOffset[1];
        rt[12] = bytesOffset[2];
        rt[13] = bytesOffset[3];

        //El array de la cabecera está listo
        return rt;
    }

    private static byte[] BMPHeader(int lado, int bitsPorPixel, int tamañoZonaPixeles) {
        byte[] rt = new byte[40];

        //14-15-16-17, tamaño del encabezado de la info del bmp, 40 en little endian
        byte[] bmpHeaderSize = intToLittleEndianBytes(40);
        rt[0] = bmpHeaderSize[0];
        rt[1] = bmpHeaderSize[1];
        rt[2] = bmpHeaderSize[2];
        rt[3] = bmpHeaderSize[3];

        //18-19-20-21, anchura de la imagen
        byte[] anchuraLE = intToLittleEndianBytes(lado);
        rt[4] = anchuraLE[0];
        rt[5] = anchuraLE[1];
        rt[6] = anchuraLE[2];
        rt[7] = anchuraLE[3];

        //22-23-24-25, altura de la imagen
        byte[] alturaLE = intToLittleEndianBytes(lado);
        rt[8] = alturaLE[0];
        rt[9] = alturaLE[1];
        rt[10] = alturaLE[2];
        rt[11] = alturaLE[3];

        //26-27, numero de planos, constante 1.
        byte[] planos = shortToLittleEndianBytes((short) 1);
        rt[12] = planos[0];
        rt[13] = planos[1];

        //28-29, tamaño de los puntos, es cuantos bits se necesitan para definir el color de un pixel por pantalla
        byte[] tamañoPuntos = shortToLittleEndianBytes((short) bitsPorPixel);
        rt[14] = tamañoPuntos[0];
        rt[15] = tamañoPuntos[1];

        //30-31-32-33, compresión, en este ejercicio no hacemos ninguna así que se mantiene en 0
        rt[16] = 0;
        rt[17] = 0;
        rt[18] = 0;
        rt[19] = 0;

        //34-35-36-37, tamaño necesario para todos los pixeles
        byte[] tamañoPixeles = intToLittleEndianBytes(tamañoZonaPixeles);
        rt[20] = tamañoPixeles[0];
        rt[21] = tamañoPixeles[1];
        rt[22] = tamañoPixeles[2];
        rt[23] = tamañoPixeles[3];

        //38-39-40-41, Resolución horizontal, no es muy importante y la podemos dejar a 0
        rt[24] = 0;
        rt[25] = 0;
        rt[26] = 0;
        rt[27] = 0;

        //42-43-44-45, Resolucion vertical, lo mismo
        rt[28] = 0;
        rt[29] = 0;
        rt[30] = 0;
        rt[31] = 0;

        //46-47-48-49, tabla de colores, irrelevante porque estamos usando todos los colores con 24 bits por pixel, se mantiene a 0
        rt[32] = 0;
        rt[33] = 0;
        rt[34] = 0;
        rt[35] = 0;

        //50-51-52-53, colores importantes, tampoco importa
        rt[36] = 0;
        rt[37] = 0;
        rt[38] = 0;
        rt[39] = 0;

        return rt;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Introduce el nombre de la imagen: ");
        String filename = sc.nextLine();

        File img = new File(filename + ".bmp");

        try (FileOutputStream fos = new FileOutputStream(img)) {

            if (!img.exists()) {
                img.createNewFile();
            }

            // Datos a pedir al usuario
            System.out.print("Introduce el tamaño del lado del cuadrado: ");
            int squareSize = sc.nextInt(); //Guardamos el tamaño del cuadrado
            int imgSize = squareSize * 2; // y preparamos el tamaño de la imagen correspondiente

            // Constante, cuantos bytes usamos por pixel y cuanto ocupa el encabezado de un
            // bmp
            int bitsPorPixel = 24; // 8 por cada color
            int bitsDelHeader = 54; // La cantidad de bits que vamos a ocupar para hacer el encabezado completo.

            // Variables calculadas
            int tamañoZonaPixeles = imgSize * imgSize * bitsPorPixel / 8; // todos los bits necesarios para mostrar cada pixel segun las dimensiones dadas por el usuario
            int tamañoArchivo = bitsDelHeader + tamañoZonaPixeles;

            // Inicio de la construcción
            byte[] fileHeader = FileHeader(tamañoArchivo);
            byte[] bmpHeader = BMPHeader(imgSize, bitsPorPixel, tamañoZonaPixeles);

            // Abrimos flujo para escribir el archivo BMP
            // Primero escribimos los encabezados
            fos.write(fileHeader);
            fos.write(bmpHeader);

            // Pedimos los colores del fondo y del cuadrado al usuario
            System.out.println("Introduce el color de fondo en RGB (0-255):");

            int fondoRojo;
            do {
                System.out.print("Rojo -> ");
                fondoRojo = sc.nextInt();
            } while (255 < fondoRojo || fondoRojo < 0);
            
            int fondoVerde;
            do {
                System.out.print("Verde -> ");
                fondoVerde = sc.nextInt();
            } while (255 < fondoVerde || fondoVerde < 0);

            int fondoAzul;
            do {
                System.out.print("Azul -> ");
                fondoAzul = sc.nextInt();
            } while (255 < fondoAzul || fondoAzul < 0);

            System.out.println("Introduce el color del cuadrado en RGB (0-255):");

            int cuadradoRojo;
            do {
                System.out.print("Rojo -> ");
                cuadradoRojo = sc.nextInt();
            } while (255 < cuadradoRojo || cuadradoRojo < 0);
            
            int cuadradoVerde;
            do {
                System.out.print("Verde -> ");
                cuadradoVerde = sc.nextInt();
            } while (255 < cuadradoVerde || cuadradoVerde < 0);
            
            int cuadradoAzul;
            do {
                System.out.print("Azul -> ");
                cuadradoAzul = sc.nextInt();
            } while (255 < cuadradoAzul || cuadradoAzul < 0);

            // Posición del cuadrado
            int inicio = (imgSize - squareSize) / 2; // posición inicial del cuadrado en el eje X
            int fin = inicio + squareSize - 1; // posición final del cuadrado en el eje X

            // BMP se escribe de abajo hacia arriba
            for (int y = imgSize - 1; y >= 0; y--) {
                for (int x = 0; x < imgSize; x++) {
                    boolean borde = (x >= inicio && x <= fin && (y == inicio || y == fin))
                            || (y >= inicio && y <= fin && (x == inicio || x == fin));

                    int R = borde ? cuadradoRojo : fondoRojo;
                    int G = borde ? cuadradoVerde : fondoVerde;
                    int B = borde ? cuadradoAzul : fondoAzul;

                    fos.write((byte) B);
                    fos.write((byte) G);
                    fos.write((byte) R);

                }

            }
            System.out.println("Imagen BMP generada correctamente: " + img.getName());

        } catch (IOException e) {
            System.out.println("Error al crear el .bmp");
        } catch (InputMismatchException e) {
            System.out.println("Has introducido un valor erroneo");
        }
    }
}
