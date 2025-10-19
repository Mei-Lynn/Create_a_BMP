package generator;

import java.io.File;
import java.io.IOException;

public class test {
    //Metodo necesario para crear little endians, es el formato estandar para valores numericos en bmpsç
    //Java usa por defecto big endian, así que hay que leer desde el final
    private static byte[] intToLittleEndianBytes(int value) {
        return new byte[] {
            (byte) (value & 0xFF), 
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)  
        };
    }

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
        //Estoy evitando usar bucles deliberadamente para mantener una claridad visual de que se guarda en cada byte

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


    public static void main(String[] args) {
        try {
            File source = new File("img.bmp");
            
            if (!source.exists()) {
                source.createNewFile();
            }

            //Datos a pedir por el usuario
            int altura = 100;
            int anchura = 100;
            //Constante, cuantos bytes usamos por pixel y cuanto ocupa el encabezado de un bmp
            int bytesPorPixel = 24;
            int bytesPorHeader = 54;

            int tamaño = bytesPorHeader + altura * anchura * bytesPorPixel;
            
            byte[] BMPheader = FileHeader(tamaño);

            
        } catch (IOException ex) {
        }
    }
}
