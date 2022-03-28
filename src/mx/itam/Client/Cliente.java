package mx.itam.Client;

import mx.itam.Interfaces.Registro;
import mx.itam.Tablero.Tablero;

import javax.swing.*;
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

        MulticastSocket socketUDP = null;
        Socket socketTCP = null;
        String name = "Registro";
        try {
            Registry registry = LocateRegistry.getRegistry("192.168.1.89");
            Registro comp = (Registro) registry.lookup(name);
            String nombreJugador = "Rodrigo";
            String[] datosRegistro = comp.registro(nombreJugador).split(";");
            String IP = datosRegistro[0];
            int portTCP = Integer.parseInt(datosRegistro[1]);
            int portUDP = Integer.parseInt(datosRegistro[2]);

            //Se agrega el jugador al Socket para comunicación TCP y se crea el Tablero
            socketTCP = new Socket(IP, portTCP);
            Tablero tab = new Tablero(nombreJugador, socketTCP);

            //Se registra el jugador en el sevidor Multicast UDP
            InetAddress group = InetAddress.getByName(IP); // destination multicast group
            socketUDP = new MulticastSocket(portUDP);
            socketUDP.joinGroup(group);
            byte[] buffer = new byte[1000];
            DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
            socketUDP.receive(messageIn);

            String[] mensaje = new String(messageIn.getData()).trim().split(";");
            int posMonstruo = Integer.parseInt(mensaje[0]);
            String nomGanador = mensaje[1];

            if (nomGanador.equals(nombreJugador)){
                tab.muestra(posMonstruo,true);
            }else{
                tab.muestra(posMonstruo,false);
            }

            //El jugador se sale del servidor Multicast UDP
            socketUDP.leaveGroup(group);

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (socketUDP != null) socketUDP.close();
            if (socketTCP != null) try {
                socketTCP.close();
            } catch (IOException e) {
                System.out.println("close:" + e.getMessage());
            }
        }
    }
}
