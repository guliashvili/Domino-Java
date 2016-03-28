package moves;

import state.Brick;

/**
 * Created by gguliash on 3/26/16.
 */
public class MoveGrab implements MoveI {
    private Brick brick;

    public MoveGrab(Brick brick) {
        this.brick = brick;
    }

    public Brick getBrick() {
        return brick;
    }

    @Override
    public int hashCode() {
        return brick.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MoveGrab){
            if(brick == null) return ((MoveGrab) obj).brick == null;
            else return brick.equals(((MoveGrab) obj).brick);
        }
        return false;
    }

    @Override
    public String toString() {
        return "MoveGrab{" +
                "brick=" + brick +
                '}';
    }
}
