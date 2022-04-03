package work.init.game.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    public POVData mine;
    public POVData enemy;
    private boolean roundCompleted;
    private int round;
    private boolean completed;
    private int timeout = 30;
    private float timeoutPassed = 0;

    public Game(POVData mine, POVData enemy) {
        this.mine = mine;
        this.enemy = enemy;
        this.round = 0;
        this.roundCompleted = false;
        this.completed = false;
    }

    public void swapPov(){
        POVData temp = mine;
        mine = enemy;
        enemy = temp;
    }

    public void setData(ConcurrentHashMap<String, Integer[]> data, String player1, String player2){
        Integer[] temp = data.get(player1);
        mine.setBlock(temp[0]);
        mine.setKick(temp[1]);
        temp = data.get(player2);
        enemy.setBlock(temp[0]);
        enemy.setKick(temp[1]);
    }
    public void incrRound(){
        this.round++;
    }
    public void resetRound(){
        roundCompleted=false;
        mine.reset();
        enemy.reset();
    }
}
