public class Token {
    final int GELB = 0;
    final int ROT = 1;
    final int BLAU = 2;
    final int SCHWARZ = 3;

    private int color;
    private int number;

    Token(int color, int number) {
        this.color = color;
        this.number = number;
    }

    int getColor() {
        return color;
    }

    int getNumber() {
        return number;
    }

    void setNumberOfJoker(int number) {
        this.number = this.number == -1 ? number : -1;
    }

    boolean equals(Token s) {
        return (s.getColor() == color && s.getNumber() == number);
    }
}
