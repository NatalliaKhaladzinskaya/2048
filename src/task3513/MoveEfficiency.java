package task3513;

public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency ob) {
        int code = 0;
        if (this == ob) code = 0;
        if (this.numberOfEmptyTiles == ob.numberOfEmptyTiles) {
            if (this.score == ob.score) code = 0;
            else if (this.score > ob.score) code = 1;
            else if (this.score < ob.score) code = -1;
        }
        if (this.numberOfEmptyTiles > ob.numberOfEmptyTiles) code = 1;
        if (this.numberOfEmptyTiles < ob.numberOfEmptyTiles) code = -1;
        return code;
    }
}
