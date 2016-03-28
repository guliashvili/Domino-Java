package main;

import controller.ControlRead;
import controller.ControllerCompetition;
import controller.ControllerI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gguliash on 3/27/16.
 */
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String... args){
        //ControlRead controlRead = new ControlRead("bots.aiMinMaxRandomV1.AiHelper");
        //controlRead.start();

        int score1=0,score2=0;
        for(int i = 0; i < 10; i++){
            ControllerI controllerCompetition =  new ControllerCompetition("bots.aiMinMaxRandomV1.AiHelper","bots.randomPlayer.RandomAI", i);
            int[]s = controllerCompetition.start();
            if(s == null){
                LOG.error("ox");
                break;
            }
            if(s[0] > s[1]) score1++;
            else if(s[1] > s[0]) score2++;
        }
        LOG.info("Score Table {} {}",score1,score2);

    }
}
