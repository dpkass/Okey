import java.io.IOException;

public class WaitingTester implements Runnable {
    private Thread t;
    private String name;
    private Match g;

    WaitingTester(String name, Match g) {
        this.name = name;
        this.g = g;
    }

    public void run() {
        try {
            g.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Starting " + name);
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }
}
