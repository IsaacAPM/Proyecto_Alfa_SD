package mx.itam.Tablero;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class Tablero extends JFrame{
    private Socket socket = null;
    private JPanel panel1;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JButton button7;
    private JButton button8;
    private JButton button9;

    Icon img = new ImageIcon("src/mx/itam/Tablero/monstruo.png");

    public void limpiar(){
        button1.setIcon(null);
        button2.setIcon(null);
        button3.setIcon(null);
        button4.setIcon(null);
        button5.setIcon(null);
        button6.setIcon(null);
        button7.setIcon(null);
        button8.setIcon(null);
        button9.setIcon(null);
    }

    public void muestra(int posMonstruo, boolean ganador){
        System.out.println(posMonstruo);
        Timer tiempo;
        tiempo = new Timer(10,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    switch (posMonstruo){
                        case 1:
                            limpiar();
                            button1.setIcon(img);
                            break;
                        case 2:
                            limpiar();
                            button2.setIcon(img);
                            break;
                        case 3:
                            limpiar();
                            button3.setIcon(img);
                            break;
                        case 4:
                            limpiar();
                            button4.setIcon(img);
                            break;
                        case 5:
                            limpiar();
                            button5.setIcon(img);
                            break;
                        case 6:
                            limpiar();
                            button6.setIcon(img);
                            break;
                        case 7:
                            limpiar();
                            button7.setIcon(img);
                            break;
                        case 8:
                            limpiar();
                            button8.setIcon(img);
                            break;
                        case 9:
                            limpiar();
                            button9.setIcon(img);
                            break;
                        default:
                            limpiar();
                            if (ganador) JOptionPane.showMessageDialog(null,"Ganaste");
                            else JOptionPane.showMessageDialog(null,"Mejor suerte la próxima");
                            break;
                    }
                }
        });
        tiempo.start();
    }

    public Tablero(String nombreJugador, Socket socket) {
        this.socket = socket;
        this.setContentPane(this.panel1);
        this.setTitle("Tablero");
        this.setSize(700,800);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button1.getIcon() != null){
                    try {
                        mensajeTCP(nombreJugador);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button2.getIcon() != null){
                    try {
                        mensajeTCP(nombreJugador);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button3.getIcon() != null){
                    try {
                        mensajeTCP(nombreJugador);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button4.getIcon() != null){
                    try {
                        mensajeTCP(nombreJugador);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button5.getIcon() != null){
                    try {
                        mensajeTCP(nombreJugador);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button6.getIcon() != null){
                    try {
                        mensajeTCP(nombreJugador);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        button7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button7.getIcon() != null){
                    try {
                        mensajeTCP(nombreJugador);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        button8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button8.getIcon() != null){
                    try {
                        mensajeTCP(nombreJugador);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        button9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button9.getIcon() != null){
                    try {
                        mensajeTCP(nombreJugador);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void mensajeTCP(String mensaje) throws RemoteException {
        try {
            System.out.println(mensaje);
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
            byte[] data = mensaje.getBytes();
            out.writeInt(data.length);
            out.write(data);
        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: -" + e.getMessage());
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
