package work.init.game.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="POVData")
public class POVData {
    @Id
    private String username;
    @Column
    private int block;
    @Column
    private int kick;
    @Column
    private boolean hit;
    @Column
    private boolean winner;
    @Column
    private int score;
    @Column
    private String token;

    public POVData(String username, String token) {
        this.username = username;
        this.block = 0;
        this.kick = 0;
        this.hit = false;
        this.winner = false;
        this.score = 0;
        this.token = token;
    }
    public void reset(){
        this.block = 0;
        this.kick = 0;
        this.hit = false;
    }
    public void incrScore(){
        this.score++;
    }

}
