package cogedatos2;

import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PruebaContenedor {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        int nIteraciones = 50;
        int nElementos = 100000;
        float[] extraccion = new float[nIteraciones];
        int[] vec = new int[nElementos];
        int count = 0;
        long tiempo_parcial = 0;
        float tiempo_total_milis = 0;

        FileWriter fichesc;
        ContenedorDeEnteros c = new ContenedorDeEnteros();

        //Creamos el árbol con  un tamaño de datos de tipo float (8 bytes)
        c.crear("AB1", 4, 70);

        try {
            fichesc = new FileWriter("ArbolB.txt");
            RandomAccessFile fichero = new RandomAccessFile("datos.dat", "r");

            // Rellenamos el vector con los datos de datos.dat.
            while (fichero.getFilePointer() < fichero.length()) {
                vec[count] = fichero.readInt();
                count++;
            }
            fichero.close();

            fichesc.write("\r\n·->Insertar en el contenedor de " + nElementos + " en 10000\r\n");
            System.out.println("Insertar en el contenedor " + nElementos + " elementos");

            for (int j = 0; j < nIteraciones; j++) {
                //PARTE INSERCION
                tiempo_parcial = System.nanoTime();
                for (int i = 0; i < nElementos; i++) {
                    c.insertar(vec[i]);
                }
                tiempo_total_milis = (float) ((System.nanoTime() - tiempo_parcial) / 1000000);
                fichesc.write("Tiempo de inserción " + (j + 1) + ": " + tiempo_total_milis + " ms\r\n");
                System.out.println("Tiempo de inserción " + (j + 1) + ": " + tiempo_total_milis + " ms");

                //PARTE EXTRACCION
                tiempo_parcial = System.nanoTime();
                for (int i = 0; i < nElementos; i++) {
                    c.extraer(vec[i]);
                }
                tiempo_total_milis = (float) ((System.nanoTime() - tiempo_parcial) / 1000000);
                extraccion[j]=tiempo_total_milis;

            }
            fichesc.write("\r\n\r\n·->Extraer en el contenedor "+ nElementos+" elementos\r\n");
            System.out.println("Extraer en el contenedor "+ nElementos+" elementos");
            for (int j = 0; j < nIteraciones; j++) {
                fichesc.write("Tiempo de extracción " + (j+1) + ": " + extraccion[j] + " ms\r\n");
                System.out.println("Tiempo de extracción " + (j+1) + ": " + extraccion[j] + " ms");
            }
            
            
            fichesc.close();
        } catch (IOException e) {
            System.out.println("Error al leer o escribir el fichero: " + e.getMessage());
        }
    }
}
