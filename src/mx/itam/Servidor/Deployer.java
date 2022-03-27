package mx.itam.Servidor;

import mx.itam.Interfaces.Registro;

import java.rmi.RemoteException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Deployer {
    public static void main(String args[]) throws RemoteException{
        String serverAddress = "localhost";
        System.setProperty("java.rmi.server.hostname",serverAddress);

        LocateRegistry.createRegistry(1099);

        String name= "Registro";
        int N = 10;
        Servidor servidor = new Servidor(N);
        servidor.deploy(name);
    }
}