package mx.itam;

import java.util.ArrayList;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        ArrayList<String> prueba = new ArrayList<String>();
        prueba.add("Hola");
        prueba.add("Adios");
        for(int i=0;i<=10;i++){
            System.out.println(ramdomNumber(10,1));
        }
    }

    private static int ramdomNumber(int max, int min){
        int value = (int) (Math.random()*(max-min)) + min;
        return  value;
    }
}
