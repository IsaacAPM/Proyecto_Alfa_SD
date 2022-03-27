package mx.itam.Client;

import mx.itam.Interfaces.Registro;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente {
    public static void main(String[] args){
        //RMI
        System.setProperty("java.security.policy", "src/mx/itam/Client/client.policy");

        //En versiones recientes de la maquina virutal de java marca error
        /*if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/

        String name = "Registro";
        try {
            Registry registry = LocateRegistry.getRegistry("192.168.1.89");
            Registro comp = (Registro) registry.lookup(name);
            String nombreJugador = "Diego";
            String datosRegistro = comp.registro(nombreJugador);
            System.out.println(datosRegistro);


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
