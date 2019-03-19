package task3513;

import java.util.*;

//будет содержать игровую логику и хранить игровое поле.
public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile [][] gameTiles;
    int score;
    int maxTile;
    Stack <Tile[][]>previousStates = new Stack();
    Stack <Integer>previousScores = new Stack();
    boolean isSaveNeeded = true;

    public Model() {
        resetGameTiles();
        score=0;
        maxTile=0;
    }
    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    // меняет вес пустой клетки на 2 или 4
    private void addTile(){
        List<Tile>list = getEmptyTiles();
        if (list.size()!=0&&list!=null)
        list.get((int)(list.size()*Math.random())).setValue(Math.random() < 0.9 ? 2 : 4);

    }
// получает список пустых клеток
    private List<Tile> getEmptyTiles(){
        List<Tile>list = new ArrayList<>();
        for (int i =0; i<FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value == 0) list.add(gameTiles[i][j]);
            }
        }
        return list;
    }
// сброс игрового поля на начало
    void resetGameTiles(){
        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i =0; i<FIELD_WIDTH; i++){
            for (int j = 0; j<FIELD_WIDTH; j++){
                gameTiles[i][j]=new Tile();
            }
        }
        addTile();
        addTile();
    }
// сжатие плиток влево, возвращает true если изменения выполнены
    private boolean compressTiles(Tile[] tiles){
        boolean isChanged = false;
        Tile temp;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tiles[j].value == 0 && tiles[j + 1].value != 0) {
                    temp = tiles[j];
                    tiles[j] = tiles[j + 1];
                    tiles[j + 1] = temp;
                    isChanged = true;
                }
            }
        }
        return isChanged;
    }
// сложение плиток одного номинала, возвращает true если изменения выполнены
    private boolean mergeTiles(Tile[] tiles){
        boolean isChanged = false;
        for (int i=0; i<tiles.length-1; i++){
            if((tiles[i].value==tiles[i+1].value)&&tiles[i].value!=0){
                tiles[i].setValue(tiles[i].value*2);
                tiles[i+1].setValue(0);
                score+=tiles[i].value;
                if (tiles[i].value>maxTile) maxTile=tiles[i].value;
                i=i+1;
                isChanged = true;
            }

        }
        compressTiles(tiles);
        return isChanged;
    }
// поворот массива на 90 градусов
    private void rotate(){
        for (int k=0; k<FIELD_WIDTH/2; k++) // border -> center
        {
            for (int j=k; j<FIELD_WIDTH-1-k; j++) // left -> right
            {
                // меняем местами 4 угла
                Tile tmp = gameTiles[k][j];
                gameTiles[k][j]= gameTiles[j][FIELD_WIDTH-1-k];
                gameTiles[j][FIELD_WIDTH-1-k] = gameTiles[FIELD_WIDTH-1-k][FIELD_WIDTH-1-j];
                gameTiles[FIELD_WIDTH-1-k][FIELD_WIDTH-1-j] = gameTiles[FIELD_WIDTH-1-j][k];
                gameTiles[FIELD_WIDTH-1-j][k] = tmp;
            }
        }
    }
    //перемещаем вниз элементы
    public void up(){
        saveState(gameTiles);
        rotate();
        left();
        rotate();
        rotate();
        rotate();
    }
    //перемещаем вправо
    public void right(){
        saveState(gameTiles);
        rotate();
        rotate();
        left();
        rotate();
        rotate();
    }
    // перемещаем вверх
    public void down(){
        saveState(gameTiles);
        rotate();
        rotate();
        rotate();
        left();
        rotate();
    }
    //перемещает эл-ты влево, добавляет если надо пустые клетки справа, вызывает compressTiles и mergeTiles
    public void left(){
        if(isSaveNeeded) saveState(gameTiles);
        boolean isChanged = false;
        for (int i=0; i<gameTiles.length;i++){
            if(compressTiles(gameTiles[i])|mergeTiles(gameTiles[i])) isChanged = true;
        }
        if (isChanged)addTile();
        isSaveNeeded=true;
    }
    //возвращающий true в случае, если в текущей позиции возможно сделать ход так, чтобы состояние игрового поля изменилось. Иначе - false
    public boolean canMove() {
        if (!getEmptyTiles().isEmpty()) return true;
        for (int i = 0; i < FIELD_WIDTH - 1; i++) {
            for (int j = 0; j < FIELD_WIDTH - 1; j++) {
                if (gameTiles[i][j].value == gameTiles[i][j + 1].value) return true;
                if (gameTiles[i][j].value == gameTiles[i + 1][j].value) return true;
            }
        }
        return false;
    }
    //сохраняем состояние в стек
    private void saveState(Tile[][] gameTiles){
        Tile[][] newMatrix = new Tile[gameTiles.length][gameTiles[0].length];
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[0].length; j++) {
                newMatrix[i][j] = new Tile(gameTiles[i][j].value);
            }
        }

        previousStates.push(newMatrix);
        previousScores.push(score);
        isSaveNeeded= false;
    }
    // достаем состояние игрового поля из стека
    public void rollback(){
        if(!previousStates.empty()) gameTiles = (Tile[][])previousStates.pop();
        if(!previousScores.empty()) score = (int)previousScores.pop();
    }
    //вызов метода движения случайным образом
    public void randomMove(){
    int n = ((int) (Math.random() * 100)) % 4;
    switch (n){
        case 0: left(); break;
        case 1: down(); break;
        case 2: right(); break;
        case 3: up(); break;
    }

    }
//будет возвращать true, в случае, если вес плиток в массиве gameTiles отличается от веса плиток в верхнем массиве стека previousStates
    private boolean hasBoardChanged(){
        int massa = 0;
        for (int i=0; i<gameTiles.length; i++){
            for (int j=0;j<gameTiles[0].length;j++){
                massa+=gameTiles[i][j].value;
            }
        }
        Tile[][] stackMassiv = previousStates.peek();
        int massaStack = 0;
        for (int i=0; i<stackMassiv.length; i++){
            for (int j=0;j<stackMassiv[0].length;j++){
                massaStack+=stackMassiv[i][j].value;
            }
        }
        if(massa!=massaStack ) return true;
        return false;
    }
// описывающий эффективность переданного хода
    MoveEfficiency getMoveEfficiency(Move move){
    MoveEfficiency moveEfficiency;
    move.move();
    if (hasBoardChanged()) moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
    else moveEfficiency = new MoveEfficiency(-1, 0, move);
    rollback();
    return moveEfficiency;
    }
    //выбирать лучший из возможных ходов и выполнять его
    void autoMove(){
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue(4, Collections.reverseOrder());
        queue.add(getMoveEfficiency(this::left));
        queue.add(getMoveEfficiency(this::right));
        queue.add(getMoveEfficiency(this::down));
        queue.add(getMoveEfficiency(this::up));
        Move move = queue.peek().getMove();
        move.move();
    }

}
