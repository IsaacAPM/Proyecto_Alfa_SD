package mx.itam.Servidor;

import mx.itam.Clases.Jugador;
import mx.itam.Interfaces.Registro;
import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;

public class Servidor implements Registro {
    private static ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
    private static int playersCounter = 0;
    private static int N;
    private static MulticastSocket socket = null;
    private static InetAddress group;

    public Servidor() throws RemoteException{
        super();
    }

    public Servidor(int N){
        super();
        this.N = N;
    }

    public void deploy(String name){
        System.setProperty("java.security.policy", "src/mx/itam/Servidor/server.policy");
        //En versiones recientes de la maquina virutal de java marca error
        /*if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/
        try {
            Servidor engine = new Servidor();
            Registro stub = (Registro) UnicastRemoteObject.exportObject(engine, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("Servicio de registro desplegado\n");

            String currentDate = (new Date()).toString();
            String mensaje = currentDate + ";0;Este es un mensaje de prueba";

            this.group = InetAddress.getByName("228.5.6.7"); // destination multicast group
            this.socket = new MulticastSocket(49159);
            this.socket.joinGroup(this.group);

            loopJuego();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loopJuego(){
        while(true){
            int posMonstruo = 0;
            String mensaje = posMonstruo + ";";
            enviaMensajeUDP(mensaje);
        }
    }

    public void enviaMensajeUDP(String mensaje){
        try {
            byte[] m = mensaje.getBytes();
            DatagramPacket messageOut =
                    new DatagramPacket(m, m.length, this.group, 49159);
            this.socket.send(messageOut);
            Thread.sleep(2000);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }

    @Override
    public String registro(String id) throws RemoteException{
        String IP = "192.168.1.89";
        this.playersCounter++;
        int port = 1099;
        String resp = IP + ";" + port;
        Jugador aux = new Jugador(id,N);

        if(!jugadores.contains(aux)){
           jugadores.add(aux);
        }

        System.out.println(this.toString());
        return  resp;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Jugador jugador : this.jugadores){
            sb.append(jugador.toString());
        }
        return sb.toString();
    }
}
