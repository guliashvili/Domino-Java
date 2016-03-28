package controller;

import helper.TransHelper;
import moves.Move;
import moves.MoveGrab;
import moves.MoveI;
import moves.MovePass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.Brick;
import state.EnhancedBoard;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gguliash on 3/27/16.
 */
public class ControlRead implements ControllerI {
    private static final Logger LOG = LoggerFactory.getLogger(ControlRead.class);
    private BotI bot = null;
    private String className;

    public ControlRead(String className) {
        this.className = className;
    }

    private void readPlayMove(BufferedReader bf, EnhancedBoard board, BotI bot) throws Exception{
        MoveI cur;
        while ((cur = play(bf.readLine())) == null);

        bot.doMove(cur);
        if(board.getCurPlayer() == 1) {
            if (cur instanceof Move)
                board.addBrick(((Move) cur).getBrick(), 1);
        }
        board.play(cur);
        LOG.info(cur.toString());

    }
    @Override
    public int[] start() {

        try(BufferedReader bf = new BufferedReader(new InputStreamReader(System.in))) {
            EnhancedBoard board = null;
            while((board = create(bf.readLine())) == null);
            bot = (BotI) Class.forName(className).getConstructor(EnhancedBoard.class,Integer.class).newInstance(new EnhancedBoard(board),0);


            while (!board.isGameFinished()){
                if(board.getCurPlayer() == 0){
                    MoveI move = bot.getMove();
                    if(move instanceof MoveGrab) {
                        LOG.info("Grabbing");
                        readPlayMove(bf,board,bot);
                    }
                    else if(board.generateValidMoves().contains(move)){
                        board.play(move);
                        bot.doMove(move);
                        LOG.info(move.toString());
                    }else{
                        LOG.error("Illegal move returned");
                    }
                }else{
                    readPlayMove(bf,board,bot);
                }



            }

            return board.getScore();



        }catch (Exception e){
            LOG.error(e.getMessage() + " error");
            return null;
        }
    }

    public EnhancedBoard create(String s){

        s = s.replaceAll("\\s+","");
        s = s.replaceAll("[^0-9]","");
        LOG.info(s);
        if(s.length() != 15){
            LOG.error("there is no 15 number");
            return null;
        }
        boolean[] tab = new boolean[40];
        List<Brick> bricks = new ArrayList<>();
        int first = s.charAt(0) - '0';
        if(first != 0 && first !=1){
            LOG.error("Wrong player = {}",first);
            return null;
        }

        for(int i = 1; i < s.length(); i+=2){
            Brick brick = TransHelper.transforb(s.charAt(i) - '0', s.charAt(i+1) - '0');
            if(brick == null){
                LOG.error("Wrong brick numbers");
                return null;
            }
            if(tab[brick.getId()]){
                LOG.error("repeat in sequence");
            }else{
                tab[brick.getId()] = true;
            }
            bricks.add(brick);
        }

        List<Brick>[] ls = new List[3];
        ls[0] = bricks;
        ls[1] = new ArrayList<>();
        ls[2] = new ArrayList<>(TransHelper.getBricks());
        ls[2].removeAll(bricks);
        EnhancedBoard enhancedBoard = new EnhancedBoard(first,ls);

        return enhancedBoard;
    }


    public MoveI play(String s){
        if(s.length() == 0){
            LOG.error("Very small");
            return null;
        }

        if(Character.isAlphabetic(s.charAt(0))){
            if(s.charAt(0) == 'p') return new MovePass();
            else if(s.charAt(0) == 'b'){
                s = s.replaceAll("[^0-9]","");
                Brick brick;
                if(s.length() == 0){
                    brick = null;
                }else if(s.length() == 2){
                    brick = TransHelper.transforb(s.charAt(0)-'0',s.charAt(1)-'0');
                    if(brick == null){
                        LOG.error("Wrong brick numbers");
                        return null;
                    }
                }else{
                    LOG.error("bank wrong length s = {}",s);
                    return null;
                }
                return new MoveGrab(brick);
            }
        }else{
            s = s.replaceAll("[^0-9]","");
            if(s.length() != 3){
                LOG.error("Wrong length s = {}",s);
                return null;
            }else{
                Brick brick;
                brick = TransHelper.transforb(s.charAt(0)-'0',s.charAt(1)-'0');
                if(brick == null){
                    LOG.error("Wrong brick numbers");
                    return null;
                }

                Move move = new Move(brick,s.charAt(0) - '0',s.charAt(2) == '1');
                return move;
            }

        }

        return null;
    }

}
