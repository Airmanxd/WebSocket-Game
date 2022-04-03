package work.init.game.Messages;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import work.init.game.Entities.Game;

@Data
@AllArgsConstructor
public class MessageToClient {
    private String type;
    private String state;
    private int waitingCount;
    Game game;

    public MessageToClient(String type, int waitingCount) {
        this.type = type;
        this.waitingCount = waitingCount;
    }

    public MessageToClient(String type, String state, Game game) {
        this.type = type;
        this.state = state;
        this.game = game;
    }
    public String jsonMessage(){
        String message = new Gson().toJson(this);
        this.game.swapPov();
        return message;
    }
}
