package tests;

import org.junit.Assert;
import org.junit.Test;
import state.Board;
import moves.Move;
import helper.TransHelper;

/**
 * Created by gguliash on 3/26/16.
 */
public class BoardTest {

    public static void testClear(Board b){
        Assert.assertEquals( "Score is not 0 at the beginning", 0, b.getBoardScore());
        for(int i = 0; i <= TransHelper.MAX_NUM; i++){
            Assert.assertEquals("double count " + i + " should be zero but is " + b.getOuterDouble(i), 0, b.getOuterDouble(i));
            Assert.assertEquals("single count " + i + " should be zero but is " + b.getOutersSingle(i), 0, b.getOutersSingle(i));
        }
        Assert.assertEquals("First move is not played", true, b.isFirstMove());
        Assert.assertEquals("First pair is not played", -1, b.getFirstPair());
        Assert.assertEquals("Score should be 0", 0, b.getBoardScore());
    }

    @Test
    public void testClear(){
        BoardTest.testClear(new Board());
    }

    public static void testFirstDouble1(Board b){
        b.play(new Move(TransHelper.transforb(0,0), 0, false ));
        b.play(new Move(TransHelper.transforb(5,0), 0, true ));

        Assert.assertEquals("Single 5 should be 1", 1, b.getOutersSingle(5));

        b.play(new Move(TransHelper.transforb(5,5), 5, false ));

        Assert.assertEquals("Score should be 10", 10, b.getBoardScore());
        Assert.assertEquals("First pair should be 0", 0, b.getFirstPair());
        for(int i = 0; i <= TransHelper.MAX_NUM; i++){
            Assert.assertEquals("Singles should be 0", 0, b.getOutersSingle(i));
            if(i == 0){
                Assert.assertEquals("Double 0 should be 3", 3, b.getOuterDouble(i));
            }else if(i == 5){
                Assert.assertEquals("Double 5 should be 3", 1, b.getOuterDouble(i));
            }else{
                Assert.assertEquals("Other Doubles should be 0", 0, b.getOuterDouble(i));
            }
        }
    }
    @Test
    public void testFirstDouble1(){
        BoardTest.testFirstDouble1(new Board());
    }


    public static void testIllegal1(Board b){
        Assert.assertFalse("first move touch pair is illegal", b.isLegal(new Move(TransHelper.transforb(5,0),0,true)));
    }

    @Test
    public void testIllegal1(){
        BoardTest.testIllegal1(new Board());
    }

    public static void testIllegal2(Board b){
        b.play(new Move(TransHelper.transforb(2,2),2,false));

        Move m;

        m = new Move(TransHelper.transforb(0,2),2,true);
        Assert.assertTrue("should be legal",b.isLegal(m));
        b.play(m);

        m = new Move(TransHelper.transforb(1,2),2,true);
        Assert.assertTrue("should be legal",b.isLegal(m));
        b.play(m);

        m = new Move(TransHelper.transforb(3,2),2,true);
        Assert.assertTrue("should be legal",b.isLegal(m));
        b.play(m);

        m = new Move(TransHelper.transforb(4,2),2,true);
        Assert.assertTrue("should be legal",b.isLegal(m));
        b.play(m);

        Assert.assertFalse("Over used first double", b.isLegal(new Move(TransHelper.transforb(5,2),2,true)));
    }

    @Test
    public void testIllegal2(){
        BoardTest.testIllegal2(new Board());
    }

    public static void testIllegal3(Board b){
        Move m = new Move(TransHelper.transforb(0,0),0,false);
        Assert.assertTrue("should be legal",b.isLegal(m));
        b.play(m);

        m = new Move(TransHelper.transforb(0,2),0,true);
        Assert.assertTrue("should be legal",b.isLegal(m));
        b.play(m);

        m = new Move(TransHelper.transforb(2,2),2,false);
        Assert.assertTrue("should be legal",b.isLegal(m));
        b.play(m);

        m = new Move(TransHelper.transforb(6,2),2,true);
        Assert.assertTrue("should be legal",b.isLegal(m));
        b.play(m);

        Assert.assertFalse("Over used next double", b.isLegal(new Move(TransHelper.transforb(5,2),2,true)));
    }

    @Test
    public void testIllegal3(){
        BoardTest.testIllegal3(new Board());
    }

    public static void testIllegal4(Board b){
        Move m = new Move(TransHelper.transforb(1,5),1,false);
        Assert.assertTrue("should be legal",b.isLegal(m));
        b.play(m);

        Assert.assertFalse("there is no double", b.isLegal(new Move(TransHelper.transforb(5,2),5,true)));

        Assert.assertFalse("there is no double", b.isLegal(new Move(TransHelper.transforb(5,1),1,true)));

        Assert.assertFalse("not used this number", b.isLegal(new Move(TransHelper.transforb(5,2),2,false)));

    }

    @Test
    public void testIllegal4(){
        BoardTest.testIllegal4(new Board());
    }

    public static void testRealGame1(Board b){
        b.play(new Move(6,6,6,false));
        Assert.assertEquals(12, b.getBoardScore());
        Assert.assertEquals(6, b.getFirstPair());

        b.play(new Move(6,2,6,true));
        Assert.assertEquals(14, b.getBoardScore());


        b.play(new Move(2,2,2,false));
        Assert.assertEquals(16, b.getBoardScore());

        b.play(new Move(3,2,2,true));
        Assert.assertEquals(15, b.getBoardScore());

        b.play(new Move(3,3,3,false));
        Assert.assertEquals(18, b.getBoardScore());

        b.play(new Move(6,4,6,true));
        Assert.assertEquals(10, b.getBoardScore());

        b.play(new Move(6,5,6,true));
        Assert.assertEquals(15 , b.getBoardScore());

        b.play(new Move(5,5,5,false));
        Assert.assertEquals(20 , b.getBoardScore());

        b.play(new Move(5,1,5,true));
        Assert.assertEquals(11 , b.getBoardScore());

        int[] doub = new int[]{0,0,0,1,0,0,1};
        int[] singl = new int[]{0,1,0,0,1,0,0};

        for(int i = 0; i <= TransHelper.MAX_NUM; i++){
            Assert.assertEquals(doub[i], b.getOuterDouble(i));
        }
        for(int i = 0; i <= TransHelper.MAX_NUM; i++){
            Assert.assertEquals(singl[i], b.getOutersSingle(i));
        }

        Assert.assertEquals(6, b.getFirstPair());
    }

    @Test
    public void testRealGame1(){
        BoardTest.testRealGame1(new Board());
    }


    public static void testRealGame2(Board b){

        b.play(new Move(1,2,1,false));
        Assert.assertEquals(3, b.getBoardScore());

        b.play(new Move(2,0,2,false));
        Assert.assertEquals(1, b.getBoardScore());

        b.play(new Move(0,0,0,false));
        Assert.assertEquals(1, b.getBoardScore());

        b.play(new Move(1,1,1,false));
        Assert.assertEquals(2, b.getBoardScore());


        int[] doub = new int[]{3,1,0,0,0,0,0};
        int[] singl = new int[]{0,0,0,0,0,0,0};

        for(int i = 0; i <= TransHelper.MAX_NUM; i++){
            Assert.assertEquals(doub[i], b.getOuterDouble(i));
        }
        for(int i = 0; i <= TransHelper.MAX_NUM; i++){
            Assert.assertEquals(singl[i], b.getOutersSingle(i));
        }

        Assert.assertEquals(0, b.getFirstPair());
    }

    @Test
    public void testRealGame2(){
        BoardTest.testRealGame2(new Board());
    }



}
