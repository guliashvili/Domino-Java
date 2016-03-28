package bots.randomPlayer;

import controller.BotI;
import moves.Move;
import moves.MoveGrab;
import moves.MoveI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.EnhancedBoard;

import java.util.List;
import java.util.Random;

/**
 * Created by gguliash on 3/27/16.
 */
public class RandomAI implements BotI {
    private static final Logger LOG = LoggerFactory.getLogger(RandomAI.class);

    private EnhancedBoard b;
    private Random random;
    private int player;

    public RandomAI(EnhancedBoard board, Integer player){
        this.player = player;
        b = board;
        long seed = System.currentTimeMillis();
        LOG.info("RandomAI seed = {}",seed);
        random = new Random(seed);
    }

    @Override
    public MoveI getMove() {
        List<MoveI> moves = b.generateValidMoves();
        return moves.get(random.nextInt(moves.size()));
    }

    @Override
    public void doMove(MoveI move) {
        if(b.getCurPlayer() != player){
            if(move instanceof MoveGrab){

            }
            else if(move instanceof Move){
                b.addBrick(((Move) move).getBrick(),1 - player);
            }
        }
        b.play(move);

    }
}
