package com.company;

import java.awt.image.BufferedImage;

public class Image implements Runnable{
    private final BufferedImage image;
    private final int width;
    private final int height;
    private final int threadWidth;
    private final int processNumber;
    private final String parameter;
    private final String paramValue;

    public Image(BufferedImage img, int w, int i, String param, String paramValue) {
        this.image = img;
        width = image.getWidth();
        height = image.getHeight();
        this.threadWidth = w;
        this.processNumber = i;
        this.parameter = param;
        this.paramValue = paramValue;
    }

    @Override
    public void run() {
        HSI_Operations(parameter, paramValue);
    }

    private void greyscale() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                int avg = (r + g + b) / 3;

                p = (a << 24) | (avg << 16) | (avg << 8) | avg;
                image.setRGB(x, y, p);
            }
        }
    }

    private void negative() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                r = 255 - r;
                g = 255 - g;
                b = 255 - b;

                p = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, p);
            }
        }
    }

    private static int[] RGBtoHSI(int R, int G, int B) {

        //convert to r + g + b = 1
        double r = (double) R / (R + G + B);
        double g = (double) G / (R + G + B);
        double b = (double) B / (R + G + B);

        //calculate Hue
        double h, L, M, hCalculation;
        L = (0.5 * (2 * r - g - b));

        //to avoid dividing by zero
        if ((R == G) && (R == B)) {
            M = Math.sqrt(Math.pow((r - g), 2) + (r - b) * (g - b) + 0.0001);
        } else {
            M = Math.sqrt(Math.pow((r - g), 2) + (r - b) * (g - b));
        }
        hCalculation = Math.acos(L / M);

        if (b <= g) {
            h = hCalculation;
        } else {
            h = 2 * Math.PI - hCalculation;
        }

        //calculate Saturation
        double s = 1 - 3 * Math.min(r, Math.min(g, b));

        //calculate Intensity
        double i = (double) (R + G + B) / (3 * 255);

        //normalising values
        int H = (int) (h * 360);
        int S = (int) (s * 100);
        int I = (int) (i * 255);

        return new int[]{H, S, I};
    }

    private static int[] HSItoRGB(int H, int S, int I) {
        int R = 0;
        int G = 0;
        int B = 0;

        //boundaries
        //if (H > 360) { H = H - 360; } else if (H < 0) { H = H + 360; }
        if (S > 100) { S = 100; } else if (S < 0) { S = 0; }   //S ++ not working properly
        if (I > 255) { I = 255; } else if (I < 0) { I = 0; }   //I ++ same

        double h = (double) H / 360;
        double s = (double) S / 100;
        double i = (double) I / 255;

        boolean flag;

        do {
            if (h < 2 * Math.PI / 3) {
                double x = i * (1 - s);
                double y = i * (1 + s * Math.cos(h) / Math.cos(Math.PI / 3 - h));
                double z = 3 * i - (x + y);

                B = (int) (x * 255);
                R = (int) (y * 255);
                G = (int) (z * 255);

                flag = false;
            } else if ((h >= 2 * Math.PI / 3) && (h < 4 * Math.PI / 3)) {
                h = h - 2 * Math.PI / 3;

                double x = i * (1 - s);
                double y = i * (1 + s * Math.cos(h) / Math.cos(Math.PI / 3 - h));
                double z = 3 * i - (x + y);

                R = (int) (x * 255);
                G = (int) (y * 255);
                B = (int) (z * 255);

                flag = false;
            } else if ((h >= 4 * Math.PI / 3) && (h < 2 * Math.PI)) {
                h = h - 4 * Math.PI / 3;

                double x = i * (1 - s);
                double y = i * (1 + s * Math.cos(h) / Math.cos(Math.PI / 3 - h));
                double z = 3 * i - (x + y);

                G = (int) (x * 255);
                B = (int) (y * 255);
                R = (int) (z * 255);

                flag = false;
            } else {
                h = h - 2 * Math.PI;
                flag = true;
            }
        } while (flag);
        return new int[]{R, G, B};
    }

    public synchronized void HSI_Operations(String parameter, String paramValue) {
        int value = Integer.parseInt(paramValue);
        for (int y = 0; y < height; y++) {
            for (int x = processNumber * threadWidth - threadWidth; x < threadWidth * processNumber; x++) {

                int p = image.getRGB(x, y);
                int A = (p >> 24) & 0xff;
                int R = (p >> 16) & 0xff;
                int G = (p >> 8) & 0xff;
                int B = p & 0xff;

                int[] HSI  = RGBtoHSI(R, G, B);
                int[] RGB = HSItoRGB(HSI[0], HSI[1], HSI[2]);

                switch (parameter) {
                    case "1":
                        RGB = HSItoRGB(HSI[0] + value, HSI[1], HSI[2]);
                        break;
                    case "2":
                        RGB = HSItoRGB(HSI[0], HSI[1] + value, HSI[2]);
                        break;
                    case "3":
                        RGB = HSItoRGB(HSI[0], HSI[1], HSI[2] + value);
                        break;
                    default:
                        break;
                }

                R = RGB[0];
                G = RGB[1];
                B = RGB[2];

                p = (A << 24) | (R << 16) | (G << 8) | B;
                image.setRGB(x, y, p);
            }
        }
    }
}
