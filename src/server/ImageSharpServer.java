package server;

import filter.ImageSharp;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ImageSharpServer {
    public static final int PORT = 6309;
    public static void main(String[] args) {
        try{

            //создание серверной реализации:
            ImageSharp sharp = new ImageSharpImpl();

            //создание экземпляра локального объекта:
            ImageSharp stub = (ImageSharp)UnicastRemoteObject.exportObject(sharp, PORT);
            //регистрация объекта:
            Registry r = LocateRegistry.createRegistry(PORT);
            //связывание имени с объектом:
            r.rebind("ImageSharp", stub);
            System.out.println("Сервер работает");

        }catch(Exception exc){
            System.err.println("Ошибка сервера: "+exc.toString());
        }
    }
}
