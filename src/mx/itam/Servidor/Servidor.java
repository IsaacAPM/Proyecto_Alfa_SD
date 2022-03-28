package mx.itam.Servidor;

import mx.itam.Clases.Jugador;
import mx.itam.Interfaces.Registro;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

public class Servidor implements Registro {
    public static ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
    private static int playersCounter = 0;
    public static int N;
    public static boolean encuentraGanador = false;
    public static boolean recibeTCP = false;
    private static ServerSocket listenSocket = null;
    private static MulticastSocket socketUDP = null;
    private static Socket clientSocket = null;
    private static InetAddress group = null;
    public static String nomGanador;
    private static String IP = "localhost";
    private static int portTCP = 49200;
    private static int portUDP = 49159;
    private static String inetA = "228.5.6.7";

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

            //Se genera el servidor Multicast UDP
            this.group = InetAddress.getByName(this.inetA); // destination multicast group
            this.socketUDP = new MulticastSocket(this.portUDP);
            this.socketUDP.joinGroup(this.group);

            //Se instancia el Socket TCP
            this.listenSocket = new ServerSocket(this.portTCP);

            //Espera a que haya al menos un jugador
            System.out.println("Esperando jugadores");
            while (jugadores.size()==0){
                Thread.sleep(1000);
            }

            System.out.println("Jugador encontrado");

            Thread.sleep(1000);

            System.out.println("Arranca el juego");
            //Arranca el juego
            while(true) {
                loopJuego();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loopJuego() throws InterruptedException, IOException {
        while (!encuentraGanador){
            int posMonstruo = randomNumber(9,1);
            enviaMensajeUDP(posMonstruo + ";null");

            clientSocket = this.listenSocket.accept();  // Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.
            while (!recibeTCP){
                Connection c = new Connection(clientSocket);
                c.start();
            }
        }
        enviaMensajeUDP("0;" + this.nomGanador);
        encuentraGanador = false;
        Thread.sleep(1000);
    }

    @Override
    public String registro(String id) throws RemoteException{
        this.playersCounter++;
        String resp = this.IP + ";" + this.portTCP + ";" + this.portUDP + ";" + this.inetA;
        Jugador aux = new Jugador(id,N);

        if(!jugadores.contains(aux)){
           jugadores.add(aux);
        }

        System.out.println(this.toString());
        return  resp;
    }

    private int randomNumber(int max, int min){
        Random random = new Random();

        int value = random.nextInt(max - min) + min;
        return  value;
    }

    private void enviaMensajeUDP(String mensaje) throws IOException {
        System.out.println(mensaje);
        byte[] m = mensaje.getBytes();
        DatagramPacket messageOut =
                new DatagramPacket(m, m.length, this.group, this.portUDP);
        this.socketUDP.send(messageOut);
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
    private Socket clientSocket;

    public Connection(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            int length = in.readInt();
            byte[] array = new byte[length];
            in.readFully(array);
            String nombreUsuario = new String(array);
            System.out.println(nombreUsuario);
            int i = 0;
            boolean encuentraJugador = false;

            while (i < Servidor.jugadores.size() && !encuentraJugador) {
                encuentraJugador = Servidor.jugadores.get(i).getId().equals(nombreUsuario);
                i++;
            }

            if (encuentraJugador) {
                Servidor.encuentraGanador = Servidor.jugadores.get(i - 1).incWinCount();
                System.out.println("Puntos: " + Servidor.jugadores.get(i - 1).getWinCount());
                Servidor.nomGanador = Servidor.jugadores.get(i - 1).getId();
                Servidor.recibeTCP = true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
