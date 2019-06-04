package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.Message;
import it.polimi.ingsw.network.Protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Server {

    private static boolean keepAlive = true;
    private final static int MILLIS_TO_WAIT = 100;
    private final static int INPUT_UPDATE = -1;

    public static void main(String[] args) {
        ServerManager serverManager;
        Scanner stdin = new Scanner(System.in);
        String message;
        int client;
        int seconds;
        System.out.println("Avvio servers in corso...");
        try {
            System.out.println("local ip: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println("local ip: unknown");
        }
        System.out.println("Specifica il numero di secondi del timeout dopo la terza connessione:");
    seconds = stdin.nextInt();
        serverManager = new ServerManager(seconds);
        serverManager.run();
        while (!serverManager.allServerReady()) {
            try {
                sleep(MILLIS_TO_WAIT);
            } catch (InterruptedException e) {
            }
        }
        while (keepAlive) {
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
            }
        }
        serverManager.shutDownAllServers();
    }

    public void shutDown() {
        keepAlive = false;
    }
}
