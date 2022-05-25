package Game;

import Game.Game;

public class WaitingTester implements Runnable {
    private Thread t;
    private String name;
    private Game g;

    WaitingTester(String name, Game g) {
        this.name = name;
        this.g = g;
    }

    public void run() {
        g.start();
    }

    public void start() {
        System.out.println("Starting " + name);
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }
}
