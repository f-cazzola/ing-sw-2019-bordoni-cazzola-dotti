package it.polimi.ingsw.network.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Server {

    private static boolean keepAlive = true;
    private static int socketPort;
    private static int rmiPort;
    private final static int MILLIS_TO_WAIT = 100;
    private final static int INPUT_UPDATE = -1;
    private final static int MIN_PORT = 1000;
    private final static int MAX_PORT = 50000;
    private final static int MIN_CONNECTION_TIME = 5;
    private final static int MAX_CONNECTION_TIME = 5000;
    private final static int MIN_TURN_TIME = 5;
    private final static int MAX_TURN_TIME = 5000;

    public static void main(String[] args) {
        ServerManager serverManager;
        Scanner stdin = new Scanner(System.in);
        String message;
        int client;
        int secondsAfterThirdConnection;
        int secondsDuringTurn;
        System.out.println("Avvio servers in corso...");
        try {
            System.out.println("local ip: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println("local ip: unknown");
        }
        System.out.println("Specifica il numero della porta per il SocketServer:");
        socketPort = manageIntInput(stdin, MIN_PORT, MAX_PORT, 0);
        System.out.println("Specifica il numero della porta per il RmiServer:");
        rmiPort = manageIntInput(stdin, MIN_PORT, MAX_PORT, socketPort);
        System.out.println("Specifica il numero di secondi del timeout dopo la terza connessione:");
        secondsAfterThirdConnection = manageIntInput(stdin, MIN_CONNECTION_TIME, MAX_CONNECTION_TIME, 0);
        System.out.println("Specifica il numero di secondi concessi ai giocatori per ogni mossa:");
        secondsDuringTurn = manageIntInput(stdin, MIN_TURN_TIME, MAX_TURN_TIME, 0);
        serverManager = new ServerManager(secondsAfterThirdConnection, secondsDuringTurn, socketPort, rmiPort);
        serverManager.run();
        while (!serverManager.allServerReady()) {
            try {
                sleep(MILLIS_TO_WAIT);
            } catch (InterruptedException e) {
            }
        }
        while (keepAlive) {
            /*
            System.out.printf("Clients attivi: [");
            serverManager.printClients();
            System.out.println("] Scrivi con quale client vuoi comunicare, " + INPUT_UPDATE + " per aggiornare la lista.");
            while (true) {
                try {
                    client = Integer.parseInt(stdin.nextLine());
                    if (serverManager.isActive(client) || client == INPUT_UPDATE)
                        break;
                    else
                        System.out.println("L'user " + client + " non è attivo. Riprova:");
                } catch (NumberFormatException e) {
                    System.out.println("Non è un numero valido, riprova:");
                }
            }
            if (client != INPUT_UPDATE) {
                System.out.println("Scrivi il messaggio:");
                message = stdin.nextLine();
                serverManager.sendMessageAndWaitForAnswer(client, new Message(Protocol.TRY, message, null, 0));
            }*/
        }
        //serverManager.shutDownAllServers();
    }

    private static int manageIntInput(Scanner stdin, int minValue, int maxValue, int forbiddenValue) {
        int output;
        try {
            output = stdin.nextInt();
        } catch (InputMismatchException e) {
            output = minValue - 1;
            stdin.nextLine();
        }
        while (output > maxValue || output < minValue || output == forbiddenValue) {
            System.out.println("Il valore deve essere compreso fra " + minValue + " e " + maxValue + ". Riprova:");
            try {
                output = stdin.nextInt();
            } catch (InputMismatchException e) {
                output = minValue - 1;
                stdin.nextLine();
            }
        }
        return output;
    }
}
