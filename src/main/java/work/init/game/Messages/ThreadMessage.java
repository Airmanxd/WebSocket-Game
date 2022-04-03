package work.init.game.Messages;

import java.util.concurrent.ConcurrentHashMap;

public class ThreadMessage {
    private final ConcurrentHashMap<String, Integer[]> messages = new ConcurrentHashMap<>();

    public ThreadMessage(String token1, String token2){
        messages.put(token1, new Integer[] {0, 0});
        messages.put(token2, new Integer[] {0, 0});
    }
    //act - 0 for block, 1 for kick
    public void setAction(String token, Integer message, int act){
        Integer[] temp = this.messages.get(token);
        temp[act] = message;
        messages.replace(token, temp);
    }
    public ConcurrentHashMap<String, Integer[]> getMessages(){
        return this.messages;
    }
    public void resetPositions(){
        for (Integer[] prev :
                messages.values()) {
            prev[0] = 0;
            prev[1] = 0;
        }
    }
    public boolean checkReady(){
        for (Integer[] temp:
             messages.values()) {
            if(temp[0] == 0 || temp[1] == 0)
                return false;
        }
        return true;
    }
}
