package util;

/**
 * Created by gguliash on 3/26/16.
 */
public class Pair<A,B> {
    A a;
    B b;
    public Pair(A a, B b){
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
