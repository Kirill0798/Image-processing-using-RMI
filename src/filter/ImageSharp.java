package filter;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ImageSharp extends Remote{
    byte[] sharpImage(byte[] imageBytes) throws RemoteException;
}

