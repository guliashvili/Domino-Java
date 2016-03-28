package controller;

import helper.TransHelper;
import moves.MoveGrab;
import moves.MoveI;
import moves.MovePass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.Brick;
import state.EnhancedBoard;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by gguliash on 3/27/16.
 */
public class ControllerCompetition implements ControllerI{
    private static final Logger LOG = LoggerFactory.getLogger(ControllerCompetition.class);
    private String className1;
    private String className2;
    private Iterator<Brick> bricks;
    private EnhancedBoard board;
    private int player;

    private Iterator<Brick> randomBricks(Random random){
        List<Brick> bricks = new ArrayList<>(TransHelper.getBricks());
        Collections.shuffle(bricks,random);
        return new ArrayList<Brick>(bricks).iterator();
    }
    private Iterator<Brick> randomBricks(){
        return randomBricks(new Random());
    }
    private Iterator<Brick> randomBricks(int seed){
        return randomBricks(new Random(seed));
    }

    private int randomPlayer(Random random){
        return random.nextInt(2);
    }
    private int randomPlayer(){
        return randomPlayer(new Random());
    }

    private int randomPlayer(int seed){
        return randomPlayer(new Random(seed));

    }
    private EnhancedBoard getBoard(int player){
        List<Brick> first = new ArrayList<>();
        for(int i = 0; i < 7; i++) first.add(bricks.next());

        List<Brick> second = new ArrayList<>();
        for(int i = 0; i < 7; i++) second.add(bricks.next());

        List<Brick> unused = new ArrayList<>(TransHelper.getBricks());
        unused.removeAll(first);
        unused.removeAll(second);

        EnhancedBoard enhancedBoard = new EnhancedBoard(player, new List[]{first, second, unused});

        return enhancedBoard;
    }

    public ControllerCompetition(String className1, String className2, List<Brick> bricks, int player) {
        this.className1 = className1;
        this.className2 = className2;
        if(bricks.size() != TransHelper.BRICK_COUNT){
            LOG.error("incomplete bricks");
            return;
        }
        this.bricks = bricks.iterator();
        this.board = getBoard(player);
        this.player = player;
    }
    public ControllerCompetition(String className1, String className2, int seed) {
        this.className1 = className1;
        this.className2 = className2;
        this.player = randomPlayer(seed);
        this.bricks = randomBricks(seed);
        this.board = getBoard(player);
    }



    @Override
    public int[] start() {
        try{
            EnhancedBoard f = new EnhancedBoard(board);
            EnhancedBoard s = new EnhancedBoard(board);
            f.getBricks()[2].addAll(f.getBricks()[1]);
            f.getBricks()[1] = new ArrayList<>();

            s.getBricks()[2].addAll(s.getBricks()[0]);
            s.getBricks()[0] = new ArrayList<>();



            BotI[] bots = new BotI[]{
                    (BotI) Class.forName(className1).getConstructor(EnhancedBoard.class, Integer.class).newInstance(f,0),
                    (BotI) Class.forName(className2).getConstructor(EnhancedBoard.class, Integer.class).newInstance(s,1)
            };


            while (!board.isGameFinished()){
                BotI curBot = bots[board.getCurPlayer()];
                BotI otherBot = bots[1 - board.getCurPlayer()];
                MoveI move = curBot.getMove();
                List<MoveI> validMoves = board.generateValidMoves();
                if(!validMoves.contains(move) && !(move instanceof MoveGrab && (validMoves.get(0) instanceof MoveGrab || validMoves.get(0) instanceof MovePass))){
                    LOG.error("Incorrect move");
                    LOG.error(board.toString());
                    LOG.error("Player = {}",board.getCurPlayer());
                    LOG.error("Move = {}",move.toString());
                    return null;
                }

                if(move instanceof MoveGrab){
                    if(bricks.hasNext())
                        move = new MoveGrab(bricks.next());
                    else{
                        LOG.error(board.toString());
                        move = curBot.getMove();
                        return null;
                    }


                    curBot.doMove(move);
                    otherBot.doMove(new MoveGrab(null));
                }else{
                    curBot.doMove(move);
                    otherBot.doMove(move);
                }

                LOG.info("Turn {} : {}", board.getCurPlayer(), move.toString());
                board.play(move);


            }

            LOG.info("Score = {} =  {} {} = {}", className1, board.getScore()[0], className2,board.getScore()[1]);

            return board.getScore();


        }catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e){
            LOG.error("{} error",e.getMessage());
            return null;
        }
    }
}
