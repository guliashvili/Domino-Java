package moves;

import state.Brick;
import helper.TransHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gguliash on 3/25/16.
 */
public class Move implements MoveI{
    private Brick brick;
    private int touch;
    private boolean touchPair;

    public Move(Brick brick, int touch, boolean touchPair) {
        if(touch != brick.getA() && touch != brick.getB()){
            throw new RuntimeException("error Move");
        }
        this.brick = brick;
        this.touch = touch;
        this.touchPair = touchPair;
    }
    public Move(int a,int b, int touch, boolean touchPair) {
        this(TransHelper.transforb(a,b),touch,touchPair);
    }

    public static List<Move> getMoves(Brick brick){
        List<Move> ret = new ArrayList<>(2 + (brick.isPair()?0:2));

        ret.add(new Move(brick, brick.getA(), false));
        ret.add(new Move(brick, brick.getA(), true));

        if(!brick.isPair()){
            ret.add(new Move(brick, brick.getB(), false));
            ret.add(new Move(brick, brick.getB(), true));
        }

        return ret;
    }

    public Brick getBrick() {
        return brick;
    }

    public boolean touchsPair() {
        return touchPair;
    }

    public int getTouch() {
        return touch;
    }

    @Override
    public int hashCode() {
        return TransHelper.transform(brick) + 100 * touch + (touchPair?1010103:0);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  Move){
            Move m = (Move)obj;
            return brick == m.brick && touch == m.touch && touchPair == m.touchPair;
        }
        return false;
    }

    @Override
    public String toString() {
        return (touch) + " " + ((getBrick().getA() == touch)?getBrick().getB():getBrick().getA()) + " " + (touchPair?1:0) + " Move";
    }
}
