package tests;

import helper.TransHelper;
import moves.Move;
import moves.MoveGrab;
import moves.MoveI;
import org.junit.Assert;
import org.junit.Test;
import state.Brick;
import state.EnhancedBoard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gguliash on 3/26/16.
 */
public class EnhancedBoardTest {

    private List<Brick>[] generate(int[] a,int[] b){
        List<Brick>[] ls = new List[3];
        for(int i = 0; i < 3; i++) ls[i] = new ArrayList<>();
        if(a.length % 2 == 1 || b.length % 2 == 1){
            throw new RuntimeException("ax");
        }

        for(int i = 0; i < TransHelper.BRICK_COUNT; i++){
            ls[2].add(TransHelper.transform(i));
        }

        for(int i = 0; i < a.length; i += 2){
            Brick x = TransHelper.transforb(a[i],a[i+1]);
            ls[2].remove(x);
            ls[0].add(x);
        }

        for(int i = 0; i < b.length; i += 2){
            Brick x = TransHelper.transforb(b[i],b[i+1]);
            ls[2].remove(x);
            ls[1].add(x);
        }

        return ls;
    }

    @Test
    public void testClear(){
        EnhancedBoard enhancedBoard = new EnhancedBoard(0,
                generate(new int[]{},new int[]{}));
        BoardTest.testClear(enhancedBoard);
    }

    @Test
    public void testIllegal1(){
        EnhancedBoard enhancedBoard = new EnhancedBoard(0,
                generate(new int[]{5,0},new int[]{}));
        BoardTest.testIllegal1(enhancedBoard);
    }

    @Test
    public void testIllegal2(){
        EnhancedBoard enhancedBoard = new EnhancedBoard(0,
                generate(new int[]{2,2,1,2,4,2},new int[]{0,2,3,2,5,2}));
        BoardTest.testIllegal2(enhancedBoard);
    }

    @Test
    public void testIllegal3(){
        EnhancedBoard enhancedBoard = new EnhancedBoard(0,
                generate(new int[]{0,0,2,2,5,2},new int[]{0,2,6,2}));
        BoardTest.testIllegal3(enhancedBoard);
    }

    @Test
    public void testRealGame1(){
        EnhancedBoard enhancedBoard = new EnhancedBoard(0,
                generate(new int[]{6,6,2,2,3,3,6,5,5,1,3,6},new int[]{6,2,3,2,6,4,5,5,2,4}));
        BoardTest.testRealGame1(enhancedBoard);
    }
    @Test
    public void testRealGame2(){
        EnhancedBoard enhancedBoard = new EnhancedBoard(0,
                generate(new int[]{1,2,0,0,5,5},new int[]{2,0,1,1,4,4}));
        BoardTest.testRealGame2(enhancedBoard);
    }


    @Test
    public void test1(){
        EnhancedBoard b = new EnhancedBoard(0,
                generate(new int[]{6,6, 4,4, 1,5, 1,2, 1,3, 4,0, 3,4 },
                         new int[]{0,2, 2,2, 5,0, 5,2, 1,4, 0,3, 4,6}));
        Set<MoveI> p = new HashSet<>();
        b.play(new Move(6,6,6,false));
        p.clear();
        p.add(new Move(6,4,6,true));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());

        b.play(new Move(6,4,6,true));
        p.clear();
        p.add(new Move(4,4,4,false));
        p.add(new Move(4,0,4,false));
        p.add(new Move(4,3,4,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());


        b.play(new Move(4,3,4,false));
        p.clear();
        p.add(new Move(0,3,3,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());

        Assert.assertEquals("My Score should be 15", 15, b.getScore()[0]);
        Assert.assertEquals("his Score should be 0", 0, b.getScore()[1]);
        b.play(new Move(0,3,3,false));

        p.clear();
        p.add(new Move(0,4,0,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());


        b.play(new Move(0,4,0,false));

        p.clear();
        p.add(new Move(1,4,4,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());

        b.play(new Move(1,4,4,false));

        p.clear();
        p.add(new Move(5,1,1,false));
        p.add(new Move(2,1,1,false));
        p.add(new Move(3,1,1,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());

        b.play(new Move(1,3,1,false));

        Assert.assertEquals("My Score should be 30", 30, b.getScore()[0]);
        Assert.assertEquals("his Score should be 0", 0, b.getScore()[1]);

        Assert.assertEquals("go to bank",14, b.generateValidMoves().size());
        for(MoveI moveI : b.generateValidMoves()){
            Assert.assertTrue(moveI instanceof MoveGrab);
        }

        b.play(new MoveGrab(TransHelper.transforb(3,3)));
        p.clear();
        p.add(new Move(3,3,3,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());

        b.play(new Move(3,3,3,false));

        Assert.assertEquals("go to bank",13, b.generateValidMoves().size());
        for(MoveI moveI : b.generateValidMoves()){
            Assert.assertTrue(moveI instanceof MoveGrab);
        }
        b.play(new MoveGrab(TransHelper.transforb(3,5)));


        p.clear();
        p.add(new Move(3,5,3,true));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());
        b.play(new Move(5,3,3,true));


        p.clear();
        p.add(new Move(0,5,5,false));
        p.add(new Move(2,5,5,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());
        b.play(new Move(5,2,5,false));


        p.clear();
        p.add(new Move(2,1,2,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());
        b.play(new Move(1,2,2,false));

        Assert.assertEquals("go to bank",12, b.generateValidMoves().size());
        for(MoveI moveI : b.generateValidMoves()){
            Assert.assertTrue(moveI instanceof MoveGrab);
        }
        b.play(new MoveGrab(TransHelper.transforb(4,5)));

        Assert.assertEquals("go to bank",11, b.generateValidMoves().size());
        for(MoveI moveI : b.generateValidMoves()){
            Assert.assertTrue(moveI instanceof MoveGrab);
        }
        b.play(new MoveGrab(TransHelper.transforb(0,6)));

        p.clear();
        p.add(new Move(6,0,6,true));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());
        b.play(new Move(6,0,6,true));


        p.clear();
        p.add(new Move(1,5,1,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());
        b.play(new Move(1,5,1,false));
        Assert.assertEquals("My Score should be 35", 35, b.getScore()[0]);
        Assert.assertEquals("his Score should be 0", 0, b.getScore()[1]);

        p.clear();
        p.add(new Move(5,0,0,false));
        p.add(new Move(2,0,0,false));
        p.add(new Move(5,4,5,false));
        p.add(new Move(0,5,5,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());
        b.play(new Move(0,5,0,false));

        Assert.assertEquals("go to bank",10, b.generateValidMoves().size());
        for(MoveI moveI : b.generateValidMoves()){
            Assert.assertTrue(moveI instanceof MoveGrab);
        }
        b.play(new MoveGrab(TransHelper.transforb(2,3)));

        Assert.assertEquals("go to bank",9, b.generateValidMoves().size());
        for(MoveI moveI : b.generateValidMoves()){
            Assert.assertTrue(moveI instanceof MoveGrab);
        }
        b.play(new MoveGrab(TransHelper.transforb(1,0)));

        Assert.assertEquals("go to bank",8, b.generateValidMoves().size());
        for(MoveI moveI : b.generateValidMoves()){
            Assert.assertTrue(moveI instanceof MoveGrab);
        }
        b.play(new MoveGrab(TransHelper.transforb(2,4)));

        Assert.assertEquals("go to bank",7, b.generateValidMoves().size());
        for(MoveI moveI : b.generateValidMoves()){
            Assert.assertTrue(moveI instanceof MoveGrab);
        }
        b.play(new MoveGrab(TransHelper.transforb(2,6)));

        p.clear();
        p.add(new Move(6,2,6,true));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());
        b.play(new Move(6,2,6,true));


        p.clear();
        p.add(new Move(2,2,2,false));
        p.add(new Move(0,2,2,false));
        p.add(new Move(4,5,5,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());
        b.play(new Move(2,2,2,false));


        p.clear();
        p.add(new Move(3,2,2,true));
        p.add(new Move(4,2,2,true));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());
        b.play(new Move(3,2,2,true));


        p.clear();
        p.add(new Move(5,4,5,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());
        b.play(new Move(4,5,5,false));


        p.clear();
        p.add(new Move(4,2,4,false));
        p.add(new Move(4,4,4,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());
        b.play(new Move(4,2,4,false));
        Assert.assertFalse("Is not Finished", b.isGameFinished());


        p.clear();
        p.add(new Move(0,2,2,false));
        Assert.assertArrayEquals("Move", p.toArray(), new HashSet(b.generateValidMoves()).toArray());
        b.play(new Move(2,0,2,false));

        Assert.assertEquals("My Score should be 45", 45, b.getScore()[0]);
        Assert.assertEquals("his Score should be 20", 20, b.getScore()[1]);
        Assert.assertTrue("Is Finished", b.isGameFinished());
        Assert.assertEquals("Game is finished should not have more moves", 0, b.generateValidMoves().size());
    }
}
