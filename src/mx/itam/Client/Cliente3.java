package mx.itam.Client;

import mx.itam.Interfaces.Registro;
import mx.itam.Tablero.Tablero;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente3 {
    public static void main(String[] args){
        //RMI
        System.setProperty("java.security.policy", "src/mx/itam/Client/client.policy");

        //En versiones recientes de la maquina virutal de java marca error
        /*if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/

        MulticastSocket socketUDP = null;
        String name = "Registro";
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            Registro comp = (Registro) registry.lookup(name);
            String nombreJugador = "Diego";
            String[] datosRegistro = comp.registro(nombreJugador).split(";");

            String IP = datosRegistro[0];
            int portTCP = Integer.parseInt(datosRegistro[1]);
            int portUDP = Integer.parseInt(datosRegistro[2]);
            String inetA = datosRegistro[3];

            //Se crea el Tablero
            Tablero tab = new Tablero();
            tab.conectar(nombreJugador, IP, portTCP);

            //Se registra el jugador en el sevidor Multicast UDP
            InetAddress group = InetAddress.getByName(inetA); // destination multicast group
            socketUDP = new MulticastSocket(portUDP);
            socketUDP.joinGroup(group);

            //Espera los mensajes del servidor Multicast UDP y los envía al tablero
            while(true) {
                byte[] buffer = new byte[1000];
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(messageIn);

                String[] mensaje = new String(messageIn.getData()).trim().split(";");
                int posMonstruo = Integer.parseInt(mensaje[0]);
                String nomGanador = mensaje[1];
                System.out.println(posMonstruo + nomGanador);

                //Se muestra el monstruo o el mensaje del ganador en el juego
                if (nomGanador.equals(nombreJugador)) {
                    tab.muestra(posMonstruo, true);
                } else {
                    tab.muestra(posMonstruo, false);
                }
            }
        } catch (RemoteException | NotBoundException e) {
            System.out.println("Connect: " + e.getMessage());
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (socketUDP != null) socketUDP.close();
        }
    }
}
