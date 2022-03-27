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

public class Servidor implements Registro {
    private static ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
    private static int playersCounter = 0;
    private static int N;

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
        } catch (RemoteException e) {
            e.printStackTrace();
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
