package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author LeftMidRight
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.

        /** 对于一块不为 null 的 方块，在执行 tilt 方法的时候有以下几种情况
         *  1. tile 要移动的目标位置 值和自己一样，而且目标位置上的值不是merge出来的，那么我们就直接move上去，并且记录下这次merge（以免新出来的值被merge）
         *  2. tile 要移动的目标位置为null，那么直接 move上去
        */
        //向北方倾斜
        if(side == Side.NORTH) {
            for(int c = 0; c < board.size(); c ++) {
                int isMerge = 0;
                for(int r = board.size() - 1; r >= 0; r --) {
                    Tile currentTile = board.tile(c, r);
                    //当currentTile不为空的时候，棋盘倾斜的时候需要移动
                    if(currentTile != null) {
                        int targetRow = board.size() - 1;
                        //merge出来的新值不能再被merge
                        targetRow -= isMerge;
                        //寻找 currentTile 要移动到的位置
                        while(targetRow > r && board.tile(c, targetRow) != null && board.tile(c, targetRow).value() != currentTile.value()) {
                            targetRow--;
                        }
                        System.out.println("r:" + r  + " " + "c:" + c + " " + "targetRow:" + targetRow);
                        //如果目标位置不是当前位置的话，我们就要 move
                        if(targetRow != r) {
                            if(board.move(c, targetRow, currentTile)) {
                                isMerge ++;
                                score += 2 * currentTile.value();
                            }
                            changed = true;
                        }
                    }
                }
            }
        } else if(side == Side.SOUTH) {
            for(int c = 0; c < board.size(); c ++) {
                int isMerge = 0;
                for(int r = 0; r < board.size(); r ++) {
                    Tile currentTile = board.tile(c, r);
                    if(currentTile != null) {
                        int targetRow = 0;
                        //merge出来的新值不能再被merge
                        targetRow += isMerge;
                        //寻找 currentTile 要移动到的位置
                        while(targetRow < r && board.tile(c, targetRow) != null && board.tile(c, targetRow).value() != currentTile.value()) {
                            targetRow ++;
                        }
                        //如果目标位置不是当前位置的话，我们就要 move
                        if(targetRow != r) {
                            if(board.move(c, targetRow, currentTile)) {
                                isMerge ++;
                                score += 2 * currentTile.value();
                            }
                            changed = true;
                        }
                    }
                }
            }
        } else if(side == Side.WEST) {
            for(int r = 0; r < board.size(); r ++) {
                int isMerge = 0;
                for(int c = 0; c < board.size(); c ++) {
                    Tile currentTile = board.tile(c, r);
                    if(currentTile != null) {
                        int targetCol = 0;
                        //merge出来的新值不能再被merge
                        targetCol += isMerge;
                        //寻找 currentTile 要移动到的位置
                        while(targetCol < c && board.tile(targetCol, r) != null && board.tile(targetCol, r).value() != currentTile.value()) {
                            targetCol ++;
                        }
                        //如果目标位置不是当前位置的话，我们就要 move
                        if(targetCol != c) {
                            if(board.move(targetCol, r, currentTile)) {
                                isMerge ++;
                                score += 2 * currentTile.value();
                            }
                            changed = true;
                        }
                    }
                }
            }
        } else {
            for(int r = 0; r < board.size(); r ++) {
                int isMerge = 0;
                for(int c = board.size() - 1; c >= 0; c --) {
                    Tile currentTile = board.tile(c, r);
                    if(currentTile != null) {
                        int targetCol = board.size() - 1;
                        //merge出来的新值不能再被merge
                        targetCol -= isMerge;
                        //寻找 currentTile 要移动到的位置
                        while(targetCol > c && board.tile(targetCol, r) != null && board.tile(targetCol, r).value() != currentTile.value()) {
                            targetCol --;
                        }
                        //如果目标位置不是当前位置的话，我们就要 move
                        if(targetCol != c) {
                            if(board.move(targetCol, r, currentTile)) {
                                isMerge ++;
                                score += 2 * currentTile.value();
                            }
                            changed = true;
                        }
                    }
                }
            }
        }
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        /**
         * get the size of board b, because b is a square, its length and width both equals b.size()
         *  then scan all element of b, if one of its element is null return true
         *  if we have scanned all the board and don't return true, that means all space is not null, so just return false
         */
        int boardSize = b.size();
        for(int i = 0; i < boardSize; i ++) {
            for(int j = 0; j < boardSize; j ++) {
                if(b.tile(i, j) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        /**
         * scan the board b, then before we check the value of one space, we should check weather it is null
         * if it is not null and its value equals MAX_PIECE, which means player win the game, so we return true
         * if all space we have scanned don't trigger the condition, just return false
         */
        int boardSize = b.size();
        for(int i = 0; i < boardSize; i ++) {
            for(int j = 0; j < boardSize; j ++) {
                Tile currentSpace = b.tile(i,j);
                if(currentSpace != null && currentSpace.value() == MAX_PIECE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        if(emptySpaceExists(b)) {
            return true;
        }

        int boardSize = b.size();
        int[] dx = new int[]{1, -1, 0, 0};
        int[] dy = new int[]{0, 0, 1, -1};
        for(int i = 0; i < boardSize; i ++) {
            for(int j = 0; j < boardSize; j ++) {
                Tile currentSpace = b.tile(i, j);
                int curX = currentSpace.col(), curY = currentSpace.row();
                // check adjacent space
                for(int k = 0; k < 4; k ++) {
                    int newX = curX + dx[k], newY = curY + dy[k];
                    if(newX < 0 || newX >= boardSize || newY < 0 || newY >= boardSize) {
                        continue;
                    }
                    if(currentSpace.value() == b.tile(newX, newY).value()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
