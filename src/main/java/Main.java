import Game.Game;
import Game.Player;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Player[] playerList = new Player[args.length];

        for (int i = 0; i < args.length; i++) playerList[i] = new Player(args[i]);

        Game game;
        if (args.length != 0)
            game = new Game(playerList);
        else
            game = new Game();

        game.start();
    }
}
