public class Token implements Comparable {
    final int JOKER = -1;
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

    Token(String color, int number) {
        this.number = number;
        switch (color) {
            case "Gelb" -> this.color = GELB;
            case "Rot" -> this.color = ROT;
            case "Blau" -> this.color = BLAU;
            case "Schwarz" -> this.color = SCHWARZ;
            default -> throw new IllegalArgumentException();
        }
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

    @Override
    public String toString() {
        String s = "";
        switch (color) {
            case GELB -> s += "Gelb ";
            case ROT -> s += "Rot ";
            case BLAU -> s += "Blau ";
            case SCHWARZ -> s += "Schwarz ";
            case JOKER -> {return "Joker";}
        }
        return s + number;
    }

    @Override
    public int compareTo(Object o) {
        int i = this.color - ((Token) o).getColor();
        return i == 0 ? (this.number - ((Token) o).getNumber()) : i;
    }
}
