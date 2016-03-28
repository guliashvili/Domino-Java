package moves;

/**
 * Created by gguliash on 3/26/16.
 */
public class MovePass implements MoveI {
    @Override
    public int hashCode() {
        return 666;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MovePass) return true;
        else return false;
    }

    @Override
    public String toString() {
        return "Pass";
    }
}
