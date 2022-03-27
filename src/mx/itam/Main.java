package mx.itam;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ArrayList<String> prueba = new ArrayList<String>();
        prueba.add("Hola");
        prueba.add("Adios");
        System.out.println(prueba.contains("Adios"));
    }
}
