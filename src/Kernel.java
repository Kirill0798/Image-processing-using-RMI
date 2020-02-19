import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

public class Kernel {


    //маска:
    private static final int[][] filter = {
            {-1, -1, -1},
            {-1, 9, -1},
            {-1, -1, -1}
    };


    public static void main(String[] args) {
        BufferedImage bufferedImage = null;
        File file = null;
       // double[][] filter = {{-1,-1,-1},{-1,9,-1},{-1,-1,-1}};

        try{
            file = new File("/Users/kirill/IdeaProjects/TNP_lab_2/LXzQ-rzqaus.jpg");
            bufferedImage = ImageIO.read(file);
        }catch(IOException exc){

        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        WritableRaster imageRaster = bufferedImage.getRaster();

        //для хранения пикселей исходного изображения:
        int[] data =  new int[4 * height * width];
        //получаем пиксели исходного:
        imageRaster.getPixels(0,0,width,height, data);
        //создаем изображение для рамки:
        BufferedImage extensionImage = new BufferedImage(width+2, height+2, bufferedImage.getType());
        WritableRaster extensionImageRaster = extensionImage.getRaster();
        //заполняем новое так, чтобы вокруг была рамка:
        extensionImageRaster.setPixels(1,1, width, height, data);


        //заполнение столбиков:
        for (int i = 0; i < extensionImage.getHeight(); i++) {

            int[] pixelLeft = extensionImageRaster.getPixel(1, i, new int[4]);
            int[] pixelRight = extensionImageRaster.getPixel(extensionImage.getWidth()-2,i,new int[4]);

            extensionImageRaster.setPixel(0,i, pixelLeft);
            extensionImageRaster.setPixel(extensionImage.getWidth()-1,i,pixelRight);
        }

        //заполнение верха:
        for (int i = 0; i < extensionImage.getWidth() ; i++) {

            int[] pixelTop = extensionImageRaster.getPixel(i,1,new int[4]);
            int[] pixelBottom = extensionImageRaster.getPixel(i,extensionImage.getHeight()-2,new int[4]);

            extensionImageRaster.setPixel(i,0, pixelTop);
            extensionImageRaster.setPixel(i,extensionImage.getHeight()-1,pixelBottom);
        }

        BufferedImage result = new BufferedImage(bufferedImage.getWidth(),bufferedImage.getHeight(), bufferedImage.getType());
        WritableRaster resultRaster = result.getRaster();


        for(int i = 0 ; i < bufferedImage.getWidth(); i++)
        {
            for(int j = 0; j < bufferedImage.getHeight(); j++)
            {
                int r=0,g=0,b=0,a=0;
                for (int k = 0; k < filter.length; k++)
                {
                    for (int l = 0; l < filter[k].length; l++)
                    {
                        //int[] pixel = extensionImageRaster.getPixel(i+k, j+l, new int[4]);
                        int p = extensionImage.getRGB(i+k,j+l);
                        int red = (p & 0xFF0000) >>16;
                        int green = (p & 0x00FF00) >>8;
                        int blue = p & 0x0000FF;
                       // int alpha = (p & 0x0000FF)>>24;
                        r += red * filter[k][l];
                        g += green * filter[k][l];
                        b += blue * filter[k][l];
                      //  a += alpha * filter[k][l];
                    }
                }
                if(r>255) r = 255;
                if(r < 0) r = 0;
                if(g>255) g = 255;
                if(g < 0) g = 0;
                if(b>255) b = 255;
                if(b<0) b = 0;
                //if(a>255) a = 255;
              //  if(a < 0) a =0;
                resultRaster.setPixel(i,j,new int[]{r,g,b,255});

            }
        }
        result.setData(resultRaster);


        //запись в файл:
        try{
            file = new File("result5.jpg");
            ImageIO.write(result,"jpg",file);
        }catch(IOException exc){

        }
    }
}
