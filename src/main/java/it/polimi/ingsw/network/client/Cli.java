package it.polimi.ingsw.network.client;

import java.util.List;
import java.util.Scanner;

public class Cli implements Ui {

    Scanner stdin = new Scanner(System.in);

    public String showMessage(String toBeShown){
        System.out.println(toBeShown);
        return stdin.nextLine();
    }

    public String showMessage(String toBeShown, List<String> possibleAnswers){
        int choice;
        System.out.println(toBeShown);
        for (int i = 0; i < possibleAnswers.size(); i++)
            System.out.println("(" + (i + 1) + ") " + possibleAnswers.get(i));
        choice = stdin.nextInt();
        stdin.nextLine();
        while (choice > possibleAnswers.size() || choice < 1) {
            System.out.println("Risposta non valida. Riprova:");
            choice = stdin.nextInt();
            stdin.nextLine();
        }
        return possibleAnswers.get(choice - 1);
    }

    @Override
    public void run() {

    }
}
