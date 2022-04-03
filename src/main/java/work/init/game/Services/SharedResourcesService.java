package work.init.game.Services;

import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import work.init.game.Messages.MessageToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@NoArgsConstructor
public class SharedResourcesService {

    private final ArrayList<WebSocketSession> queue = new ArrayList<>();

    private final List<WebSocketSession> connected = Collections.synchronizedList(new ArrayList<>());

    private final List<WebSocketSession> broadcastList = Collections.synchronizedList(new ArrayList<>());

    public int getSize(){
        return this.queue.size();
    }

    public WebSocketSession[] takePair() throws IOException {
        if(this.queue.size()>1) {
            WebSocketSession[] res = new WebSocketSession[2];

            synchronized (queue) {
                int id = ThreadLocalRandom.current().nextInt(this.queue.size());
                res[0] = queue.get(id);
                res[0].getAttributes().replace("state", "game");
                queue.remove(res[0]);
                id = ThreadLocalRandom.current().nextInt(this.queue.size());
                res[1] = queue.get(id);
                res[1].getAttributes().replace("state", "game");
                queue.remove(res[1]);
            }

            broadcastList.removeAll(Arrays.asList(res));
            broadcastQueueUpdate();
            return res;
        }
        else
            return null;
    }

    public WebSocketSession getByToken(String token){
        synchronized (connected) {
            for (WebSocketSession temp :
                    connected) {
                if (temp.getAttributes().get("token").toString().equals(token))
                    return temp;
            }
            return null;
        }
    }

    public void removeConnection(WebSocketSession session) throws IOException {
        this.connected.remove(session);
        removeFromQueue(session);
        this.broadcastList.remove(session);
    }

    public void removeFromQueue(WebSocketSession session) throws IOException {
        boolean removed = false;
        synchronized (queue){
            removed = this.queue.remove(session);
        }
        if(removed) {
            session.getAttributes().replace("state", "init");
            broadcastQueueUpdate();
        }
    }

    public void addConnection(WebSocketSession session){
        this.connected.add(session);
        this.broadcastList.add(session);
    }

    public void addToQueue(WebSocketSession session) throws IOException {
        synchronized (queue){
            this.queue.add(session);
        }
        session.getAttributes().replace("state","wait");
        broadcastQueueUpdate();
    }

    public void returnToQueue(WebSocketSession[] players) throws IOException {
        for (WebSocketSession temp :
                players) {
            synchronized (queue){
                this.queue.add(temp);
            }
            this.broadcastList.add(temp);
            temp.getAttributes().replace("state", "wait");
        }
        broadcastQueueUpdate();
    }

    public void broadcastQueueUpdate() throws IOException {
        synchronized (broadcastList){
            for (WebSocketSession temp :
                    broadcastList) {
                temp.sendMessage(createMessage(temp));
            }
        }
    }
    private TextMessage createMessage(WebSocketSession session){
        MessageToClient msg = new MessageToClient("state", queue.size());
        msg.setState(session.getAttributes().get("state").toString());
        return new TextMessage(new Gson().toJson(msg));
    }
}
