package bots.aiMinMaxRandomV1;

import moves.MoveGrab;
import moves.MoveI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.Brick;
import state.EnhancedBoard;
import util.Pair;
import util.Tree;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by gguliash on 3/26/16.
 */
public class AI {
    private static final Logger LOG = LoggerFactory.getLogger(AI.class);
    private static final Random random = new Random(666);
    private EnhancedBoard start;
    private List<Brick> possible;
    private static final int NUM_ITERATION_CHECK = 100;
    private static final int RANDOM_SHUFFLE = 600;
    private static final int NODE_LIMIT = 100;
    private int CURPLAYER;
    private int ENEMY;

    public AI(AiHelper ai){
        possible = ai.getPossible();
        start = ai.getBoard();
    }

    private int simulate(EnhancedBoard enhancedBoard){
        int step = 0;
        while (!enhancedBoard.isGameFinished()){
            step++;
            if(step == 100){
                LOG.error(enhancedBoard.toString());
                return 0;
            }
            List<MoveI> moves = enhancedBoard.generateValidMoves();
            enhancedBoard.play(moves.get(Math.abs(random.nextInt()) % moves.size()));
        }

        return enhancedBoard.getScore()[CURPLAYER] - enhancedBoard.getScore()[ENEMY];
    }


    private double score(EnhancedBoard enhancedBoard){
        int sul = NUM_ITERATION_CHECK;
        if(enhancedBoard.isGameFinished()) sul = 1;
        double ret = 0;
        for(int i = 0; i < sul; i++){
            int score = simulate(new EnhancedBoard(enhancedBoard));
            if(score < -20) ret -= 2;
            else if(score < 0) ret --;
            else if(score > 20) ret+=2;
            else ret ++;
        }
        ret /= NUM_ITERATION_CHECK;

        return ret;
    }

    private double minMax(Tree<EnhancedBoard> tree){
        EnhancedBoard enhancedBoard = tree.getValue();
        if(tree.getChildren().size() == 0 || enhancedBoard.isGameFinished()){
            return score(enhancedBoard);
        }

        Double bscore = null;

        for(Tree<EnhancedBoard> child : tree.getChildren()){

            double p = minMax(child);
            if(enhancedBoard.getCurPlayer() == CURPLAYER){ // maximizer
                if(bscore == null || bscore < p){
                    bscore = p;
                }
            }else{ // minimizer
                if(bscore == null || bscore > p){
                    bscore = p;
                }

            }
        }
        return bscore;
    }


    public List<List<Brick>> getPossibleShuffles(){
        List<List<Brick>> ret = new ArrayList<>(RANDOM_SHUFFLE);
        for(int i = 0; i < RANDOM_SHUFFLE; i++){
            Collections.shuffle(possible,random);
            ret.add(new ArrayList<>(possible.subList(0, start.getCount()[ENEMY])));
        }

        return ret;
    }

    private Tree<EnhancedBoard> getAdaptiveDepth(EnhancedBoard e){
        Tree<EnhancedBoard> rot = new Tree<>(e);
        Queue<Tree<EnhancedBoard> > q = new LinkedList<>();
        int limit = NODE_LIMIT;
        int dep = -1;
        q.add(rot);
        q.add(null);


        while(q.size() > 1 && limit > 0){

            Tree<EnhancedBoard> cur = q.poll();
            if(cur == null) {
                q.add(null);
                dep++;
                continue;
            }
            for(MoveI move : cur.getValue().generateValidMoves()){
                Tree<EnhancedBoard> b = new Tree<>(new EnhancedBoard(cur.getValue()));
                b.getValue().play(move);
                cur.addChild(b);
                q.add(b);
                limit--;
            }

        }

        return rot;
    }

    public Pair<Double, MoveI> getAns(){
        CURPLAYER = start.getCurPlayer();
        ENEMY = 1 - CURPLAYER;

        if(start.getCurPlayer() != 0 && start.getCurPlayer() != 1) {
            LOG.error("Predicts only for first player");
            return null;
        }
        if(start.getBricks()[ENEMY].size() != 0){
            LOG.error("How do you know brick?");
            return null;
        }
        if(start.getBricks()[CURPLAYER].size() != start.getCount()[CURPLAYER]){
            LOG.error("should know everything");
            return null;
        }




        List<MoveI> moves = start.generateValidMoves();
        if(moves.size() == 0) return null;
        else if(moves.size() == 1) return new Pair<>(-666.0,moves.get(0));
        else if(moves.get(0) instanceof MoveGrab) return new Pair<>(-666.0,moves.get(0));

        List<List<Brick>> shuffles = getPossibleShuffles();
        Pair<Double, MoveI> bst = new Pair<>(-9999999.0,null);


        ExecutorService executorService = Executors.newFixedThreadPool(shuffles.size());
        double[] allS = new double[moves.size()];
        Future<double[]>[] tasks = new Future[shuffles.size()];

        for(int i = 0; i < shuffles.size(); i++){
            final List<Brick> shuff = shuffles.get(i);
            tasks[i] = executorService.submit(new Callable<double[]>(){
                @Override
                public double[] call() throws Exception {
                    double[] movesS = new double[moves.size()];
                    EnhancedBoard e = new EnhancedBoard(start);
                    e.getBricks()[ENEMY] = new ArrayList<>(shuff);
                    e.getBricks()[2].removeAll(shuff);

                    Tree<EnhancedBoard> tree = getAdaptiveDepth(e);

                    for(int i = 0; i < moves.size(); i++) {
                        movesS[i] += minMax(tree);
                    }
                    return movesS;
                }
            });
        }

        for(Future<double[]> future : tasks){
            try{
                double[] movesS = future.get();
                for(int i = 0; i < movesS.length; i++) allS[i] += movesS[i];
            }catch (Exception e){
              LOG.error("Error");
            }
        }

        for(int i = 0; i < allS.length; i++) {
            double sum = allS[i] / RANDOM_SHUFFLE;
            if(bst.getA() < sum)
                bst = new Pair<>(sum, moves.get(i));
        }
        executorService.shutdown();


        return bst;
    }
}
