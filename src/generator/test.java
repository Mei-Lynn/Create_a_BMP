package generator;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class test {
    /**
     * Metodo necesario para crear little endians, es el formato estandar para valores numericos en bmps
     * Java usa por defecto big endian, así que hay que leer desde el final para escribirlo en LE.
     * Se usa ints en caso de que ocupe 4 bits
     * @param value
     * @return
     */
    private static byte[] intToLittleEndianBytes(int value) {
        return new byte[] {
            (byte) (value & 0xFF), 
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)  
        };
    }

    /**
     * Metodo necesario para crear little endians, es el formato estandar para valores numericos en bmps
     * Java usa por defecto big endian, así que hay que leer desde el final para esribirlo en LE.
     * Se usa shorts en caso de que ocupe 2 bits
     * @param value
     * @return
     */
    private static byte[] shortToLittleEndianBytes(short value) {
    return new byte[] {
        (byte) (value & 0xFF),         // Byte menos significativo
        (byte) ((value >> 8) & 0xFF)   // Byte más significativo
    };
}

    /**
     * Genera el encabezado del archivo, 14 bytes con informacion sobre el propio archivo
     * @param tamaño
     * @return
     */
    private static byte[] FileHeader(int tamaño) {
        byte[] rt = new byte[14];

        //Bytes 1 y 2 de la cabecera, declaran el tipo
        rt[0] = (byte) 'B';
        rt[1] = (byte) 'M';
        
        //bytes 3-4-5-6, tamaño del archivo. Hay que generarlos como little endian
        byte[] bytesTamaño = intToLittleEndianBytes(tamaño);
        rt[2] = bytesTamaño[0];
        rt[3] = bytesTamaño[1];
        rt[4] = bytesTamaño[2];
        rt[5] = bytesTamaño[3];
        //NOTA: Estoy evitando usar bucles deliberadamente para mantener una claridad visual de que se guarda en cada byte

        //Bytes 7-8-9-10, espacios reservados, deben ser 0
        rt[6] = 0;
        rt[7] = 0;
        rt[8] = 0;
        rt[9] = 0;

        //11-12-13-14, offset. AKA cual es el primer byte relativo al aspecto de la imagen, para un bmp es cuando termina la cabecera en el byte 54
        byte[] bytesOffset = intToLittleEndianBytes(54);
        rt[10] = bytesOffset[0];
        rt[11] = bytesOffset[1];
        rt[12] = bytesOffset[2];
        rt[13] = bytesOffset[3];

        //El array de la cabecera está listo
        return rt;
    }
    
    private static byte[] BMPHeader (int anchura, int altura) {
        byte[] rt = new byte[40];

        //15-16-17-18, tamaño del encabezado de la info del bmp, 40 en little endian
        byte[] bmpHeaderSize = intToLittleEndianBytes(40);
        rt[0] = bmpHeaderSize[0];
        rt[1] = bmpHeaderSize[1];
        rt[2] = bmpHeaderSize[2];
        rt[3] = bmpHeaderSize[3];

        //18-19-20-21, anchura de la imagen
        byte[] anchuraLE = intToLittleEndianBytes(anchura);
        rt[4] = anchuraLE[0];
        rt[5] = anchuraLE[1];
        rt[6] = anchuraLE[2];
        rt[7] = anchuraLE[3];

        //22-23-24-25, altura de la imagen
        byte[] alturaLE = intToLittleEndianBytes(altura);
        rt[8] = alturaLE[0];
        rt[9] = alturaLE[1];
        rt[10] = alturaLE[2];
        rt[11] = alturaLE[3];

        //26-27, numero de planos, constante 1.
        byte[] planos = shortToLittleEndianBytes((short) 1);
        rt[12] = planos[0];
        rt[13] = planos[1];

        //28-29, tamaño de los puntos, es 

        return rt;
    }


    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) { //Convenientemente declaramos el scanner con el try necesario para manejar files
            
            //Hay que pedir el nombre del archivo
            System.out.print("Introduce el nombre de la imagen: ");
            String filename = sc.nextLine();

            File img = new File(filename+".bmp");
            
            if (!img.exists()) {
                img.createNewFile();
            }

            //Datos a pedir por el usuario
            int altura = 100;
            int anchura = 100;

            //Constante, cuantos bytes usamos por pixel y cuanto ocupa el encabezado de un bmp
            int bitsPorPixel = 24; //8 por cada color
            int bitsDelHeader = 54; //La cantidad de bits que vamos a ocupar para hacer el encabezado completo.

            //Variables calculadas
            int tamaño = bitsDelHeader + altura * anchura * bitsPorPixel;
            
            //Inicio de la construcción
            byte[] fileHeader = FileHeader(tamaño);
            byte[] bmpHeader = BMPHeader(anchura, altura);
            
        } catch (IOException ex) {
        }
    }
}
