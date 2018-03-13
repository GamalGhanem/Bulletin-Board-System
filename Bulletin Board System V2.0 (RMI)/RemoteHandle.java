import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteHandle extends Remote {

    String read(int id) throws RemoteException;

    String write(int value, int id) throws RemoteException;
}
