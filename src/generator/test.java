package generator;

import java.io.File;
import java.io.IOException;

public class test {
    public static void main(String[] args) {
        try {
            File source = new File("img.bmp");
            
            if (!source.exists()) {
                source.createNewFile();
            }
            
            
        } catch (IOException ex) {
        }
    }
}
