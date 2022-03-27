package mx.itam.Client;

import mx.itam.Interfaces.Registro;

import java.io.IOException;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class Cliente {
    public static void main(String[] args){
        //RMI
        System.setProperty("java.security.policy", "src/mx/itam/Client/client.policy");

        //En versiones recientes de la maquina virutal de java marca error
        /*if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/
        MulticastSocket socket = null;
        String name = "Registro";
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            Registro comp = (Registro) registry.lookup(name);
            String nombreJugador = "Diego";
            String datosRegistro = comp.registro(nombreJugador);
            System.out.println(datosRegistro);

            //Se registra el jugador en el sevidor Multicast UDP
            InetAddress group = InetAddress.getByName("228.5.6.7"); // destination multicast group
            socket = new MulticastSocket(49159);
            socket.joinGroup(group);
            byte[] buffer = new byte[1000];
            System.out.println("Waiting for messages...");
            DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
            socket.receive(messageIn);
            System.out.println("Message: " + new String(messageIn.getData()).trim() + " from: " + messageIn.getAddress());
            //El jugador se sale del servidor Multicast UDP
            socket.leaveGroup(group);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (socket != null) socket.close();
        }
    }
}
