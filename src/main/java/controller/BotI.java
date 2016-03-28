package controller;

import moves.MoveI;

/**
 * Created by gguliash on 3/27/16.
 */
public interface BotI {
    MoveI getMove();
    void doMove(MoveI move);
}
