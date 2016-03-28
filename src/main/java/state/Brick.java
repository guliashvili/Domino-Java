package state;

/**
 * Created by gguliash on 3/25/16.
 */
public class Brick {
    private int a,b,id;
    public Brick(int a,int b, int id){
        if( a < 0 || b < 0 || a > 6 || b > 6){
            throw new RuntimeException("brick out of order");
        }
        this.a = Math.min(a, b);
        this.b = Math.max(a, b);
        this.id = id;
    }

    public int getSum(){
        return a + b;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getId(){
        return id;
    }

    public boolean isPair(){
        return  a == b;
    }

    @Override
    public String toString() {
        return "Brick{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
