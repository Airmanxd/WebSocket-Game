package work.init.game.Services;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import work.init.game.Configs.GameConfig;
import work.init.game.Entities.Game;
import work.init.game.Entities.POVData;
import work.init.game.Messages.ThreadMessage;
import work.init.game.Messages.MessageToClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Async
@NoArgsConstructor
@ConfigurationProperties(prefix = "game.const")
public class GameService {

    @Autowired
    private SessionService sessionService;
    @Autowired
    private POVDataService povDataService;
    @Autowired
    private SharedResourcesService sharedResourcesService;
    @Autowired
    private GameConfig config;

    public void startGame(WebSocketSession p1, WebSocketSession p2, ThreadMessage threadMessage) throws IOException, InterruptedException {
        ConcurrentHashMap<String, Integer[]> results;
        System.out.printf("Running game in thread %s%n", Thread.currentThread().getName());
        String token1 = p1.getAttributes().get("token").toString();
        String token2 = p2.getAttributes().get("token").toString();
        POVData player1 = new POVData(p1.getAttributes().get("username").toString(), token1);
        POVData player2 = new POVData(p2.getAttributes().get("username").toString(), token2);

        povDataService.add(player1);
        povDataService.add(player2);

        Game currMatch = new Game(player1, player2);

        sessionService.updateSessionStateByToken(token1, "game");
        sessionService.setPOVDataByToken(token1, player1);

        sessionService.updateSessionStateByToken(token2, "game");
        sessionService.setPOVDataByToken(token2, player2);

        MessageToClient msg = new MessageToClient("state", "game", currMatch);
        p1.sendMessage(new TextMessage(msg.jsonMessage()));
        p2.sendMessage(new TextMessage(msg.jsonMessage()));
        long startTime;

        for(int i = 0; i < config.getMaxRounds(); i++){
            currMatch.incrRound();
            startTime = System.currentTimeMillis();
            for(int j = 0; j < 30*(1000/config.getUpdateRate()); j++){
                Thread.sleep(config.getUpdateRate());
                if(!p1.isOpen())
                    sharedResourcesService.returnToQueue(new WebSocketSession[]{p2});
                if(!p1.isOpen())
                    sharedResourcesService.returnToQueue(new WebSocketSession[]{p1});
                if(threadMessage.checkReady())
                    break;
                else
                    currMatch.setTimeoutPassed((float) (System.currentTimeMillis() - startTime) /1000);
                currMatch.setData(threadMessage.getMessages(), token1, token2);
                sendUpdate(p1, p2, currMatch);
            }
            results = threadMessage.getMessages();
            currMatch.setData(results, token1, token2);
            currMatch.setRoundCompleted(true);

            if(!(currMatch.enemy.getKick() == 0) &&
                    (currMatch.mine.getBlock() != currMatch.enemy.getKick()))
                currMatch.enemy.setHit(true);

            if(!(currMatch.mine.getKick() == 0) &&
                    (currMatch.enemy.getBlock() != currMatch.mine.getKick()))
                currMatch.mine.setHit(true);

            if(currMatch.enemy.isHit())
                currMatch.enemy.incrScore();
            if(currMatch.mine.isHit())
                currMatch.mine.incrScore();

            sendUpdate(p1, p2, currMatch);
            currMatch.resetRound();
            threadMessage.resetPositions();
            Thread.sleep(config.getRoundTimeout());
        }
        currMatch.setRoundCompleted(true);
        currMatch.setCompleted(true);
        if(currMatch.mine.getScore() > currMatch.enemy.getScore())
            currMatch.mine.setWinner(true);
        else if(currMatch.mine.getScore() < currMatch.enemy.getScore())
            currMatch.enemy.setWinner(true);

        sendUpdate(p1, p2, currMatch);
        Thread.sleep(5000);
        sharedResourcesService.returnToQueue(new WebSocketSession[]{p1, p2});
    }

    public void sendUpdate(WebSocketSession p1, WebSocketSession p2, Game currMatch) throws IOException {
        MessageToClient msg = new MessageToClient("state", "game", currMatch);
        TextMessage update = new TextMessage(msg.jsonMessage());
        p1.sendMessage(update);
        update = new TextMessage(msg.jsonMessage());
        p2.sendMessage(update);
    }

}
