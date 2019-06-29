package it.polimi.ingsw.network.server;

/**
 * This class represent a single communication between server and client regardless the communication technology.
 */

class SingleCommunication implements Runnable {

    final String message;
    private final int number;
    private final ServerManager serverManager;
    boolean timeExceeded = false;

    SingleCommunication(int number, ServerManager serverManager, String message) {
        this.number = number;
        this.serverManager = serverManager;
        this.message = message;
    }

    @Override
    public void run() {
    }

    /**
     * Sets time exceed and print advise on server log.
     */

    void setTimeExceeded() {
        timeExceeded = true;
        showAndSetAnswer("TEMPO SCADUTO");
    }

    /**
     * Sets client's answer and prints it on server log.
     */

    void showAndSetAnswer(String answer) {
        serverManager.setAnswer(number, answer);
        System.out.println("User " + number + ": " + answer);
    }
}
