package server;

import filter.ImageSharp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;

public class ImageSharpImpl implements ImageSharp {

    //маска:
    private static final int[][] filter = {
            {-1, -1, -1},
            {-1,  9, -1},
            {-1, -1, -1}
    };

    ImageSharpImpl() {

    }

    @Override
    public byte[] sharpImage(byte[] imageBytes) throws RemoteException{
        try{

            BufferedImage inputImage = convertFromByteToImage(imageBytes);
            BufferedImage extInputImage = extensionOfImage(inputImage);

            //обработка:
            BufferedImage outputImage = processImage(extInputImage);

            return convertToBytes(outputImage);
        }catch(Exception exc){
            throw new RemoteException("Произошла ошибка при обработке изображения",exc);
        }
    }


    //перевод массива байтов в изображение:
    private BufferedImage convertFromByteToImage(byte[] bytes) throws IOException {
            return ImageIO.read(new ByteArrayInputStream(bytes));
    }


    //перевод в массив байтов:
    private byte[] convertToBytes(BufferedImage image) throws IOException{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image,"jpg",outputStream);
        return outputStream.toByteArray();
    }


    private BufferedImage processImage(BufferedImage image){

        int w = image.getWidth() - 2;
        int h = image.getHeight() - 2;

        BufferedImage outputImage = new BufferedImage(w, h, image.getType());

        int r, g, b;

        for (int i = 0; i < w ; i++) {
            for (int j = 0; j < h; j++) {
                r = 0;
                g = 0;
                b = 0;
                for (int k = 0; k < filter.length; k++) {
                    for (int l = 0; l < filter[k].length; l++) {
                        Color pixel = new Color(image.getRGB(i+k,j+l));
                        r += pixel.getRed()*filter[k][l];
                        g += pixel.getGreen()*filter[k][l];
                        b += pixel.getBlue()*filter[k][l];
                    }
                }

                int red =  checkRange(r);
                int green = checkRange(g);
                int blue = checkRange(b);

                outputImage.setRGB(i,j, new Color(red, green, blue).getRGB());
            }
        }

        return outputImage;
    }


    //создание рамки толщиной в 1 пиксель для изображения:
    private BufferedImage extensionOfImage(BufferedImage image){

        int w = image.getWidth();
        int h = image.getHeight();

        //для хранения пикселей исходного изображения:
        int[] data =  new int[4 * h * w];

        //получаем растровое представление, чтобы сохранить все пиксели:
        WritableRaster imageRaster = image.getRaster();
        //получаем пиксели исходного:
        imageRaster.getPixels(0, 0, w, h, data);
        BufferedImage imageExtension = new BufferedImage(w+2, h+2, image.getType());
        WritableRaster imageExtRaster = imageExtension.getRaster();
        //получили изображение с черной рамкой:
        imageExtRaster.setPixels(1,1, w, h, data);


        //заполнение рамки соседними пикселями:
        fillColumn(imageExtRaster, imageExtension.getWidth(), imageExtension.getHeight());
        fillRow(imageExtRaster, imageExtension.getWidth(), imageExtension.getHeight());

        return imageExtension;
    }

    //заполнение столбиков:
    private void fillColumn(WritableRaster imageRaster, int w, int h){

        for (int i = 0; i < h; i++) {

            int[] pixelLeft = imageRaster.getPixel(1, i, new int[4]);
            int[] pixelRight = imageRaster.getPixel(w-2, i, new int[4]);

            imageRaster.setPixel(0,i, pixelLeft);
            imageRaster.setPixel(w-1,i,pixelRight);
        }
    }


    //заполнение строк:
    private void fillRow(WritableRaster imageRaster, int w, int h){
        for (int i = 0; i < w; i++) {

            int[] pixelTop = imageRaster.getPixel(i,1,new int[4]);
            int[] pixelBottom = imageRaster.getPixel(i, h-2,new int[4]);

            imageRaster.setPixel(i,0, pixelTop);
            imageRaster.setPixel(i,h-1, pixelBottom);
        }
    }

    //проверка диапазона:
    private int checkRange(int color){
        if(color > 255) return 255;
        if(color < 0) return 0;
        return color;
    }

}
