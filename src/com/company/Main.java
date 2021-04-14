/*Na ocenę 5.0 do 5.5:
Należy zaimplementować aplikację wielowątkową, która będzie służyła do przetwarzania obrazu
(zmiana saturacji, zakresu tonalnego, etc). W tym na ocenę 5.5 w architekturze CUDA lub OpenCL.
Ewentualnie inna propozycja, proszę kontaktować się drogą mailową celem ustalenia projektu.
 */
package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        File file;
        Desktop desktop = Desktop.getDesktop();
        try {
            //opening image and saving it to BufferedImage
            file = new File("C:\\Users\\Michal\\Desktop\\PO\\landscape.png");
            BufferedImage bI = ImageIO.read(file);

            //opening starting image
            System.out.println("Opening starting image... ");
            desktop.open(file);

            //menu
            System.out.println("Co chcesz z nim zrobić: ");
            System.out.println("1. Zmienić kolorystyke (Podaj wartosć od -360 do 360)");
            System.out.println("2. Zmienić nasycenie kolorów (Podaj wartosć od -100 do 100)");
            System.out.println("3. Zmienić intensywność (Podaj wartosć od -255 do 255)");
            final String[] param = new String[2];
            param[0] = scanner.next();
            param[1] = scanner.next();

            //operations in i threads
            IntStream.rangeClosed(1, 20).mapToObj(i -> new Thread(new Image(bI, bI.getWidth() / 20, i, param[0], param[1]))).forEach(Thread::start);

            //Saving image
            file = new File("C:\\Users\\Michal\\Desktop\\PO\\out.png");
            ImageIO.write(bI, "png", file);

            //Opening image
            System.out.println("Opening the image... ");
            desktop.open(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
