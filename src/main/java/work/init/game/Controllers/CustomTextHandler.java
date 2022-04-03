package work.init.game.Controllers;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import work.init.game.Services.MatcherService;
import work.init.game.Services.SharedResourcesService;
import work.init.game.Messages.MessageToClient;
import work.init.game.Services.SessionService;

import java.io.IOException;
import java.util.Map;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class CustomTextHandler extends TextWebSocketHandler{
    private SessionService sessionService;
    private SharedResourcesService sharedResourcesService;
    private MatcherService matcherService;
    @Autowired
    public CustomTextHandler(SessionService sessionService, MatcherService matcherService, SharedResourcesService sharedResourcesService) throws IOException, InterruptedException {
        this.sessionService = sessionService;
        this.sharedResourcesService = sharedResourcesService;
        this.matcherService = matcherService;
        matcherService.match();
        System.out.println("Maing thread: " + Thread.currentThread().getName());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String token = session.getUri().toString().split("=")[1];
        WebSocketSession temp = sharedResourcesService.getByToken(token);
        if(temp!=null) {
            sharedResourcesService.removeFromQueue(temp);
            temp.close();
        }
        else{
            session.getAttributes().put("token", token);
            session.getAttributes().put("username", sessionService.getUsernameByToken(token));
            session.getAttributes().put("state", "init");
        }
        MessageToClient msg = new MessageToClient("state", sharedResourcesService.getSize());
        msg.setState("init");
        session.sendMessage(new TextMessage(new Gson().toJson(msg)));
        sharedResourcesService.addConnection(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sharedResourcesService.removeConnection(session);
        sessionService.remove(session);
        matcherService.removeFromGames(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException, InterruptedException {
        Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);
        String token = session.getAttributes().get("token").toString();
        switch(value.get("action")){
            case "game_set_block":
                matcherService.getGames()
                        .get(token)
                        .setAction(token, Integer.parseInt(value.get("block")), 0);
                break;
            case "game_set_kick":
                matcherService.getGames()
                        .get(token)
                        .setAction(token,  Integer.parseInt(value.get("kick")), 1);
                break;
            case "join":
                handleJoin(session);
                break;
            case "undo_join":
                handleExitQueue(session);
                break;
        }
    }

    public void handleJoin(WebSocketSession session) throws IOException, InterruptedException {
        sessionService.updateSessionStateByToken(session.getAttributes().get("token").toString(), "wait");
        sharedResourcesService.addToQueue(session);
    }

    public void handleExitQueue(WebSocketSession session) throws IOException {
        sessionService.updateSessionStateByToken(session.getAttributes().get("token").toString(), "init");
        sharedResourcesService.removeFromQueue(session);
    }

}
