package work.init.game.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import work.init.game.Messages.ThreadMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MatcherService {
    @Autowired
    private GameService gameService;
    @Autowired
    private SharedResourcesService sharedQueue;
    private final ConcurrentHashMap<String, ThreadMessage> games = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 10000)
    public void match() throws IOException, InterruptedException {
        System.out.println(Thread.currentThread().getName());
        WebSocketSession[] players = sharedQueue.takePair();
        if(players!=null){
            System.out.println("Matched 2 players");

            players[0].getAttributes().replace("state", "game");
            players[1].getAttributes().replace("state", "game");


            String token1 = players[0].getAttributes().get("token").toString();
            String token2 = players[1].getAttributes().get("token").toString();

            ThreadMessage threadMessage = new ThreadMessage(token1, token2);

            games.put(token1, threadMessage);
            games.put(token2, threadMessage);

            System.out.printf("Match made in thread %s%n",Thread.currentThread().getName());
            gameService.startGame(players[0], players[1], threadMessage);
        }
        else
            System.out.println("Too few players in the queue to match");
    }
    public ConcurrentHashMap<String, ThreadMessage> getGames(){
        return this.games;
    }
    public void removeFromGames(WebSocketSession session){
        games.remove(session.getAttributes().get("token").toString());
    }
}
