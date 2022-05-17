public class Spielstein {
    public final int GELB = 0;
    public final int ROT = 1;
    public final int BLAU = 2;
    public final int SCHWARZ = 3;

    private int color;
    private int number;

    public Spielstein(int color, int number) {
        this.color = color;
        this.number = number;
    }

    public int getColor() {
        return color;
    }

    public int getNumber() {
        return number;
    }
}
