package it.polimi.ingsw.network.server;

public class GameCountDown extends Thread {

    private int secondsToWait;
    private int seconds;
    private ServerManager serverManager;
    private boolean stopped = false;
    private final static int MILLIS_IN_SECOND = 1000;

    public GameCountDown(ServerManager serverManager, int secondsToWait) {
        this.serverManager = serverManager;
        this.secondsToWait = secondsToWait;
        seconds = secondsToWait;
    }

    public void reset() {
        stopped = true;
        seconds = 0;
    }

    public void restore() {
        stopped = false;
        seconds = secondsToWait;
    }

    public int getTimeLeft() {
        return seconds;
    }

    public void run() {
        while (seconds > 0 && !stopped) {
            try {
                sleep(MILLIS_IN_SECOND);
            } catch (InterruptedException e) {
                break;
            }
            seconds--;
            System.out.println(seconds);
        }
        if (!stopped)
            serverManager.checkAllConnections();
    }
}
