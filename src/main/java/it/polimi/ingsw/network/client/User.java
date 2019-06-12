package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.Message;
import it.polimi.ingsw.network.Protocol;
import it.polimi.ingsw.utils.Parser;
import it.polimi.ingsw.view.gui.GuiManager;

import java.util.Arrays;

import static java.lang.StrictMath.random;
import static java.lang.Thread.sleep;

public class User {

    private final static String CLI = "CLI";
    private final static String GUI = "GUI";
    private final static String SOCKET = "Socket";
    private final static String RMI = "RMI";
    private final static int SOCKET_PORT = 9000;
    private final static int RMI_PORT = 10000;
    private final static int MILLIS_IN_SECOND = 1000;

    public static void main(String[] args) {
        String connectionType;
        String uiChoice;
        Ui ui = new Cli();
        String ip;
        Client client = new Client(ui);
        Parser parser = new Parser();
        uiChoice = client.manageMessage(parser.serialize(new Message(Protocol.CHOOSE_UI, "", Arrays.asList(CLI, GUI), 0)));
        if (uiChoice.equals(GUI)) {
            Gui gui = new Gui();
            new Thread(gui).start();
            GuiManager.setGui(gui);
            System.out.printf("Avvio Gui in corso.");
            while (!gui.isReady()) {
                try {
                    sleep(MILLIS_IN_SECOND);
                } catch (InterruptedException e) {
                }
                System.out.printf(".");
            }
            client.setUi(gui);
            ui = gui;
        }
        connectionType = client.manageMessage(parser.serialize(new Message(Protocol.CHOOSE_CONNECTION, "", Arrays.asList(SOCKET, RMI), 0)));
        ip = client.manageMessage(parser.serialize(new Message(Protocol.INSERT_IP, "", null, 0)));
        while (!client.isValidIp(ip)) {
            ip = client.manageMessage(parser.serialize(new Message(Protocol.INSERT_IP_AGAIN, "", null, 0)));
        }
        if (connectionType.equals(SOCKET))
            client = new SocketClient(ip, SOCKET_PORT, ui);
        else {
            //TODO: resolve in another way port conflicts
            client = new RmiClient(ip, (int) (random() * RMI_PORT), ui);
        }
        new Thread(client).start();
    }
}
