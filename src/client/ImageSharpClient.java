package client;

import filter.ImageSharp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;

public class ImageSharpClient {
    public static final String url = "rmi://localhost:6309/ImageSharp";
    public static void main(String[] args) {
//        if(args.length != 2){ System.err.println("Пожалуйста введите два названия файла:\n1.Имя файла, который хотите преобразовать\n2.Файл, в котором хотите увидеть результат.");
//        System.exit(-1);}
        String inputFile = "Cat.jpg";
        String outputFile = "result10.png";

        try{

            URL clientPolicyURL = ImageSharpClient.class.getResource("client.policy");
            System.setProperty("java.security.policy", clientPolicyURL.toString());

            if(System.getSecurityManager() == null)
                System.setSecurityManager(new SecurityManager());

            sharpImage(inputFile, outputFile);

        }catch(IOException exc){
            exc.printStackTrace();
        }catch(NotBoundException exc){
            System.out.println("Не обнаружено\n"+exc.getMessage());
        }
    }

    private static void sharpImage(String input, String output) throws IOException, NotBoundException{

        //доступ к удаленному объекту-заглушке:
        ImageSharp sharp = (ImageSharp) Naming.lookup(url);

        BufferedImage inputImage = readImage(input);

        byte[] inputImageBytes = convertToBytes(inputImage);
        byte[] sharpedImageBytes = sharp.sharpImage(inputImageBytes);

        BufferedImage sharpedImage = convertToImage(sharpedImageBytes);

        saveImage(sharpedImage, output);

    }

    //чтение изображение из файла:
    private static BufferedImage readImage(String nameOfFile) throws IOException{
        File imageFile = new File(nameOfFile);
        if(!imageFile.exists()) {
            System.out.println("Файл не найден");
            throw new FileNotFoundException();
        }
        return ImageIO.read(imageFile);
    }

    //перевод изображения в байтовый массив:
    private static byte[] convertToBytes(BufferedImage image) throws IOException{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image,"jpg", outputStream);
        return outputStream.toByteArray();
    }

    //восстановление изображения по байтовому массиву:
    private static BufferedImage convertToImage(byte[] bytes) throws IOException{
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }

    //сохранение изображения:
    private static void saveImage(BufferedImage image, String nameOfFile) throws IOException{
        ImageIO.write(image, "png", new File(nameOfFile));
    }

}
