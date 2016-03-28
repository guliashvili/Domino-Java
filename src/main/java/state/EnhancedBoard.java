package state;

import helper.TransHelper;
import moves.Move;
import moves.MoveGrab;
import moves.MoveI;
import moves.MovePass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by gguliash on 3/25/16.
 */
public class EnhancedBoard extends Board {
    private static final Logger LOG = LoggerFactory.getLogger(EnhancedBoard.class);

    private int curPlayer; // 0 1
    private List<Brick> usedBricks;
    private List<Brick>[] bricks;
    private int[] score;
    private boolean gameFinished;
    private MoveI lastMove;
    private int[] count;

    public EnhancedBoard(int curPlayer, List<Brick>[] bricks) {
        super();

        this.curPlayer = curPlayer;
        this.bricks = bricks;
        this.usedBricks = new ArrayList<>();
        this.score = new int[2];
        this.gameFinished = false;
        this.lastMove = null;
        this.count = new int[]{7,7};

        checkConsistency();
    }
    public EnhancedBoard reverse(){
        EnhancedBoard ret = new EnhancedBoard(this);
        ret.curPlayer = 1 - ret.curPlayer;

        List<Brick> tmp = ret.bricks[0];
        ret.bricks[0] = ret.bricks[1];
        ret.bricks[1] = tmp;

        int tmps = ret.score[0];
        ret.score[0] = ret.score[1];
        ret.score[1] = tmps;

        tmps = ret.count[0];
        ret.count[0] = ret.count[1];
        ret.count[1] = tmps;

        return ret;
    }

    public MoveI getLastMove() {
        return lastMove;
    }

    public int getCurPlayer() {
        return curPlayer;
    }

    public List<Brick> getUsedBricks() {
        return usedBricks;
    }

    public List<Brick>[] getBricks() {
        return bricks;
    }

    public int[] getScore() {
        return score;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public EnhancedBoard(Board board, int curPlayer, List<Brick> usedBricks, List<Brick>[] bricks, int[] score, boolean gameFinished, MoveI lastMove, int[] count) {
        super(board);
        this.curPlayer = curPlayer;
        this.usedBricks = usedBricks;
        this.bricks = bricks;
        this.score = score;
        this.gameFinished = gameFinished;
        this.lastMove = lastMove;
        this.count = count;

        checkConsistency();
    }
    public EnhancedBoard(EnhancedBoard e){
        this(e, e.curPlayer, new ArrayList<>(e.usedBricks),
                new ArrayList[]{new ArrayList(e.bricks[0]),new ArrayList(e.bricks[1]),new ArrayList(e.bricks[2])},
                e.score.clone(), e.gameFinished, e.lastMove, e.count.clone());
    }

    public int[] getCount() {
        return count;
    }

    public List<MoveI> generateValidMoves() {
        if(isGameFinished()) return Collections.emptyList();

        List<MoveI> ret = new ArrayList<>();
        for(Brick brick : bricks[curPlayer])
            for(Move move : Move.getMoves(brick))
                if(isLegal(move)) ret.add(move);

        if(ret.size() == 0 && TransHelper.BRICK_COUNT - usedBricks.size() - count[0] - count[1] > 0)
            for(Brick brick : this.bricks[2])
                ret.add(new MoveGrab(brick));

        if(ret.size() == 0)
            ret.add(new MovePass());

        return ret;
    }

    @Override
    public boolean isLegal(MoveI moveX) {
        checkConsistency();
        if(isGameFinished()) return false;

        if(moveX instanceof MoveGrab){
            if(isFirstMove()) return false;
            if(((MoveGrab) moveX).getBrick() != null && !bricks[2].contains(((MoveGrab) moveX).getBrick())) return false;
            for(Brick brick : bricks[curPlayer]){
                if(getOutersSingle(brick.getA()) > 0 || getOuterDouble(brick.getA()) > 0
                        || getOutersSingle(brick.getB()) > 0 || getOuterDouble(brick.getB()) > 0) return false;
            }

            return true;
        }else if(moveX instanceof Move){
            Move move = (Move) moveX;
            if(!super.isLegal(move)) return false;
            if(!bricks[curPlayer].contains(move.getBrick())) return false;

            return true;
        }else if(moveX instanceof MovePass){
            List<MoveI> l = generateValidMoves();
            return l.size() == 1 && (l.get(0) instanceof MovePass);
        }else{
            LOG.error("Unknown type of move");
            return false;
        }


    }
    public void addBrick(Brick brick, int player){
        if(bricks[player].contains(brick)) return;
        if(bricks[2].contains(brick)){
            bricks[2].remove(brick);
            bricks[player].add(brick);
        }else{
            LOG.error("Brick = {} is already used",brick.toString());
        }
    }

    @Override
    public void play(MoveI moveX) {
        if(isGameFinished()){
            LOG.error("Incorrect move = {}",moveX.toString());
            return;
        }

        if(!isLegal(moveX)){
            LOG.error("Not legal move {}",moveX.toString());
            return;
        }

        if(moveX instanceof MoveGrab){
            MoveGrab move = (MoveGrab) moveX;
            if(move.getBrick() != null) {
                bricks[2].remove((move.getBrick()));
                bricks[curPlayer].add((move.getBrick()));
            }
            count[curPlayer]++;

        }else if(moveX instanceof Move){
            Move move = (Move) moveX;

            super.play(move);
            bricks[curPlayer].remove(move.getBrick());
            count[curPlayer]--;
            usedBricks.add(move.getBrick());

            if(getBoardScore() % 5 == 0)
                score[curPlayer] += getBoardScore();

            if(count[curPlayer] == 0){
                gameFinished = true;
                int sum = 0;
                for(Brick op : bricks[1 - curPlayer]) sum += op.getSum();
                sum = (sum + 4) / 5 * 5;
                score[curPlayer] += sum;
            }

            curPlayer = 1 - curPlayer;
        }else if(moveX instanceof MovePass){
            curPlayer = 1 - curPlayer;
            if(lastMove instanceof MovePass){
                gameFinished = true;
                int[] sum = new int[2];
                for(int i = 0; i < 2; i++)
                    for(Brick brick : bricks[i]) sum[i] += brick.getSum();

                if(sum[0] < sum[1]){
                    score[1] += (sum[0] + 4) / 5  * 5;
                }else if(sum[1] > sum[0]){
                    score[0] += (sum[1] + 4) / 5 * 5;
                }else{
                    //noscore
                }
            }


        }else{
            LOG.error("Unknown type of move");
        }

        lastMove = moveX;

        checkConsistency();
    }

    private void checkConsistency() {
/*
        if(curPlayer != 0 && curPlayer != 1) LOG.error("Last player is not correct = {}",curPlayer);
        if(usedBricks == null) LOG.error("Used bricks is null");
        if(bricks == null) LOG.error("bricks is null");
        for(int i = 0; i < 3; i++) if(bricks[i] == null) LOG.error("bricks[{}] is null", i);
        if(bricks[0].size() + bricks[1].size() + bricks[2].size() + usedBricks.size() != TransHelper.BRICK_COUNT){
            LOG.error("Bricks are fucked [0] = {} [1] = {} [2] = {} used = {}",
                    Arrays.toString(bricks[0].toArray()),
                    Arrays.toString(bricks[1].toArray()),
                    Arrays.toString(bricks[2].toArray()),
                    Arrays.toString(usedBricks.toArray()));
        }
        List<List<Brick>> lsts = new ArrayList<>();
        lsts.addAll(Arrays.asList(bricks));
        lsts.add(usedBricks);

        for(int i = 0; i < lsts.size(); i++){
            List<Brick> others = new ArrayList<>();
            for(int j = 0; j < lsts.size(); j++) if(i != j) others.addAll(lsts.get(j));

            for(int j = 0; j < lsts.get(i).size(); j++){
                if(others.indexOf(lsts.get(i).get(j)) != -1){
                    LOG.error("Found duplication elem = {} i = {}", lsts.get(i).get(j).toString(), i);
                }
            }
        }

        if(count[0] < 0 || count[1] < 0 || count[0] > 21 || count[1] > 21){
            LOG.error("count error {} curplayer = {}",Arrays.toString(count),getCurPlayer());
        }
*/
    }

    @Override
    public String toString() {
        return "EnhancedBoard{" +
                "count=" +  Arrays.toString(count) +
                ", usedBricks=" + usedBricks +
                ", bricks=" + Arrays.toString(bricks) +
                ", score=" + Arrays.toString(score) +
                ", gameFinished=" + gameFinished +
                ", lastMove=" + lastMove +
                ", curPlayer=" +curPlayer +
                ", super = " + super.toString() +
                '}';
    }
}
