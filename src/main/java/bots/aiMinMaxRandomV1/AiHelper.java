package bots.aiMinMaxRandomV1;

import controller.BotI;
import helper.TransHelper;
import moves.Move;
import moves.MoveGrab;
import moves.MoveI;
import moves.MovePass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.Brick;
import state.EnhancedBoard;
import util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gguliash on 3/26/16.
 */
public class AiHelper implements BotI{
    private static final Logger LOG = LoggerFactory.getLogger(AiHelper.class);
    private EnhancedBoard enhancedBoard;
    private List<Brick> possible = new ArrayList<>(TransHelper.getBricks());
    private int MEPLAYER = -1;
    private int ENEMY;

    public AiHelper(EnhancedBoard b, Integer player) {
        enhancedBoard = b;
        MEPLAYER = player;
        ENEMY = 1 - player;
    }
    @Override
    public void doMove(MoveI move){
        if(enhancedBoard.getCurPlayer() == ENEMY){
            if(move instanceof Move){
                enhancedBoard.addBrick(((Move) move).getBrick(),ENEMY);
            }else if(move instanceof MovePass || move instanceof MoveGrab){
                if(move instanceof MoveGrab) possible = new ArrayList<>(TransHelper.getBricks());

                for(Brick brick : TransHelper.getBricks()){
                    if(enhancedBoard.getOuterDouble(brick.getA()) > 0 || enhancedBoard.getOuterDouble(brick.getB()) > 0 ||
                            enhancedBoard.getOutersSingle(brick.getA()) > 0 || enhancedBoard.getOutersSingle(brick.getB()) > 0){
                        possible.remove(brick);
                    }
                }

            }


        }

        enhancedBoard.play(move);

        possible.removeAll(enhancedBoard.getBricks()[MEPLAYER]);
        possible.removeAll(enhancedBoard.getUsedBricks());
    }

    public List<Brick> getPossible() {
        return possible;
    }

    public EnhancedBoard getBoard() {
        return enhancedBoard;
    }
    @Override
    public MoveI getMove(){

        if(MEPLAYER != enhancedBoard.getCurPlayer()){
            LOG.error("Should not shuffle during the game");
        }


        AI ai = new AI(this);
        Pair<Double,MoveI> p = ai.getAns();
        return p.getB();
    }

}
