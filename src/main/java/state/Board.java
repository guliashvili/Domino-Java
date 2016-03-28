package state;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import helper.TransHelper;
import moves.Move;
import moves.MoveI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gguliash on 3/25/16.
 */
public class Board implements Cloneable{
    private static final Logger LOG = LoggerFactory.getLogger(Board.class);

    private boolean isFirstMove;
    private int boardScore;
    private int firstPair;
    private int[] outersSingles;
    private int[] outerDoubles;

    public Board() {
        this.isFirstMove = true;
        this.boardScore = 0;
        this.firstPair = -1;
        this.outersSingles = new int[TransHelper.MAX_NUM + 1];
        this.outerDoubles = new int[TransHelper.MAX_NUM + 1];


        this.checkConsistency();
    }

    public Board(boolean isFirstMove, int boardScore, int firstPair, int[] outersSingles, int[] outerDoubles) {
        if(boardScore < 0) LOG.error("Wrong board score = {}", boardScore);
        if(firstPair < -1 || firstPair > 6) LOG.error("Wrong firstPair = {}", firstPair);

        this.isFirstMove = isFirstMove;
        this.boardScore = boardScore;
        this.firstPair = firstPair;
        this.outersSingles = outersSingles;
        this.outerDoubles = outerDoubles;

        this.checkConsistency();
    }
    public Board(Board board){
        this(board.isFirstMove, board.boardScore, board.firstPair, board.outersSingles.clone(), board.outerDoubles.clone());
    }

    public int getBoardScore() {
        return boardScore;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public int getFirstPair() {
        return firstPair;
    }

    public int getOutersSingle(int index) {
        return outersSingles[index];
    }

    public int getOuterDouble(int index) {
        return outerDoubles[index];
    }

    private void add(List<MoveI> lst, int touch, Brick brick){
        Move m = new Move(brick, touch, false);
        if(isLegal(m)) lst.add(m);

        m = new Move(brick, touch, true);
        if(isLegal(m)) lst.add(m);
    }
    private List<MoveI> generateValidMoves(List<Brick> bricks){
        List<MoveI> ret = new ArrayList<>();
        for(Brick brick : bricks){
            add(ret, brick.getA(), brick);
            if(!brick.isPair())
                add(ret, brick.getB(), brick);
        }

        return ret;
    }

    public boolean isLegal(MoveI moveX){
        if(!(moveX instanceof Move)) return false;
        Move move = (Move) moveX;

        if(move == null) return false;

        if(isFirstMove){
            if(move.touchsPair()) return false;
        }else{
            if(move.touchsPair()){
                if(outerDoubles[move.getTouch()] == 0) return false;
            }else{
                if(outersSingles[move.getTouch()] == 0) return false;
            }
        }

        return true;
    }

    private void checkConsistency(){
        /*
        for(int i = 0; i <= TransHelper.MAX_NUM; i++){
            if(outerDoubles[i] < 0)
                LOG.error("outerdoubles[{}] = {}",i,outerDoubles[i]);

            if(outersSingles[i] < 0)
                LOG.error("outersingles[{}] = {}",i,outersSingles[i]);

        }
        if(firstPair < -1 || firstPair > TransHelper.MAX_NUM)
            LOG.error("firstPair = {}",firstPair);

        if(boardScore < 0 || boardScore > 168){
            LOG.error("BoardScore = {}", boardScore);
        }*/
    }

    /**
     * if returns false move is not possible and nothing is changed
     * @param moveX
     * @return
     */
    public void play(MoveI moveX){
        this.checkConsistency();

        if(!isLegal(moveX)){
            LOG.error("Illegal Move = {}", moveX.toString());
            return;
        }
        Move move = (Move) moveX;

        if(isFirstMove){ // first move
            isFirstMove = false;

            boardScore = move.getBrick().getSum();

            if(move.getBrick().isPair()){
                firstPair = move.getBrick().getA();
                outerDoubles[firstPair] = 4;
            }else{
                outersSingles[move.getBrick().getA()] = 1;
                outersSingles[move.getBrick().getB()] = 1;
            }

        }else{
            if(move.touchsPair()){
                outerDoubles[move.getTouch()]--;
                if(move.getTouch() == firstPair){
                    if(outerDoubles[move.getTouch()] == 2)
                        boardScore -= firstPair * 2;
                }else{
                    if(outerDoubles[move.getTouch()] == 0){
                        boardScore -= move.getTouch() * 2;
                    }
                }

            }else{
                outersSingles[move.getTouch()]--;
                boardScore -= move.getTouch();
            }

            if(move.getBrick().isPair()){
                if(firstPair == -1){
                    firstPair = move.getBrick().getA();
                    outerDoubles[move.getBrick().getA()] = 3;
                }else{
                    outerDoubles[move.getBrick().getA()] = 1;
                }
                boardScore += move.getBrick().getSum();
            }else{
                outersSingles[move.getBrick().getSum() - move.getTouch()] ++;
                boardScore += move.getBrick().getSum() - move.getTouch();
            }
        }

        this.checkConsistency();

    }

    @Override
    public int hashCode() {
        int ret = 0;
        ret += Arrays.hashCode(outersSingles);
        ret *= 7;
        ret += Arrays.hashCode(outerDoubles);
        ret *= 7;
        ret += firstPair;

        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Board){
            Board b = (Board)obj;

            return Arrays.equals(outerDoubles, b.outerDoubles) &&
                    Arrays.equals(outersSingles, b.outersSingles) &&
                    firstPair == b.firstPair;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Board{" +
                "boardScore=" + boardScore +
                ", firstPair=" + firstPair +
                ", outersSingles=" + Arrays.toString(outersSingles) +
                ", outerDoubles=" + Arrays.toString(outerDoubles) +
                '}';
    }

    @Override
    public Board clone() {
        return new Board(isFirstMove, boardScore, firstPair, outersSingles.clone(), outerDoubles.clone());
    }
}
