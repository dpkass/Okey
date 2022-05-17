import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Player[] playerList = new Player[args.length];

        for (int i = 0; i < args.length; i++) playerList[i] = new Player(args[i]);

        Game game = new Game(playerList);

        game.start();
    }
}
