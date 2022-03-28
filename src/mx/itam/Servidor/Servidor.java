package mx.itam.Servidor;

import mx.itam.Clases.Jugador;
import mx.itam.Interfaces.Registro;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Servidor implements Registro {
    public static ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
    private static int playersCounter = 0;
    public static int N;
    public static boolean band = true;
    private static MulticastSocket socket = null;
    private static InetAddress group;
    public static Jugador ganador;

    public Servidor() throws RemoteException{
        super();
    }

    public Servidor(int N){
        super();
        this.N = N;
        ganador = new Jugador();
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

            this.group = InetAddress.getByName("228.5.6.7"); // destination multicast group
            this.socket = new MulticastSocket(49159);
            this.socket.joinGroup(this.group);

            boolean hayJugadores = jugadores.size()!=0;
            System.out.println("Esperando jugadores");
            while (!hayJugadores){
                hayJugadores = jugadores.size()!=0;
            }

            loopJuego();

            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loopJuego(){
        while(band){
            int posMonstruo = 0;
            posMonstruo = randomNumber(9,1);
            String mensaje = posMonstruo + ";null";
            System.out.println(mensaje);
            enviaMensajeUDP(mensaje);

            try {
                int serverPort = 49200;
                ServerSocket listenSocket = new ServerSocket(serverPort);
                while (true) {
                    System.out.println("Waiting for messages...");
                    Socket clientSocket = listenSocket.accept();  // Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.
                    Connection c = new Connection(clientSocket);
                    c.start();
                }

            } catch (IOException e) {
                System.out.println("Listen :" + e.getMessage());
            }
        }
        enviaMensajeUDP("0;" + ganador.getId());
    }

    public void enviaMensajeUDP(String mensaje){
        try {
            byte[] m = mensaje.getBytes();
            DatagramPacket messageOut =
                    new DatagramPacket(m, m.length, this.group, 49159);
            this.socket.send(messageOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String registro(String id) throws RemoteException{
        this.playersCounter++;
        String IP = "localhost";
        int portTCP = 49200;
        int portUDP = 49159;
        String inetA = "228.5.6.7";
        String resp = IP + ";" + portTCP + ";" + portUDP + ";" + inetA;
        Jugador aux = new Jugador(id,N);

        if(!jugadores.contains(aux)){
           jugadores.add(aux);
        }

        System.out.println(this.toString());
        return  resp;
    }

    private int randomNumber(int max, int min){
        int value = (int) (Math.random()*(max-min)) + min;
        return  value;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Jugador jugador : this.jugadores){
            sb.append(jugador.toString());
        }
        return sb.toString();
    }
}

class Connection extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private Socket clientSocket;

    public Connection(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // an echo server
            String nombreUsuario = in.readUTF();         // recibo solicitud
            int i = 0;
            boolean band = false;

            while(i<Servidor.jugadores.size() && !band){
                band = Servidor.jugadores.get(i).getId().equals(nombreUsuario);
                i++;
            }

            if(band){
                Servidor.band = Servidor.jugadores.get(i-1).incWinCount();
                if(Servidor.band){
                    Servidor.ganador = Servidor.jugadores.get(i-1);
                }
            }
        } catch (EOFException e) {

            //System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
