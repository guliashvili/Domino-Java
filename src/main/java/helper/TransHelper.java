package helper;

import state.Brick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gguliash on 3/25/16.
 */
public class TransHelper {
    public static final int BRICK_COUNT = 7 * (7 + 1) / 2;
    public static final int MAX_NUM = 6;

    private static final int[][] to = new int[7][7];
    private static final Brick[] from = new Brick[BRICK_COUNT];
    private static final List<Brick> bricks = new ArrayList<>();
    static {
        int k = 0;
        for(int i = 0; i < 7; i++)
            for(int j = i; j < 7; j++) {
                to[i][j] = to[j][i] = k;
                from[k] = new Brick(i, j, k);
                bricks.add(from[k]);
                k++;
            }
    }

    public static int transform(int a, int b){

        if(a < 0 || b < 0 || a > 6 || b > 6) return -1;
        return to[a][b];
    }

    public static List<Brick> getBricks() {
        return Collections.unmodifiableList(bricks);
    }

    public static Brick transforb(int a, int b){
        if(a < 0 || b < 0 || a > 6 || b > 6) return null;
        return from[to[a][b]];
    }
    public static int transform(Brick a){
        if(a == null )return  -1;
        return to[a.getA()][a.getB()];
    }
    public static Brick transform(int a){
        if(a < 0 || a > from.length) return null;
        return from[a];
    }
}
