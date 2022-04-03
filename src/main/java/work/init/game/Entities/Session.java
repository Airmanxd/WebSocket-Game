package work.init.game.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
@Component
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="sessions")
public class Session {
    @Id
    private String token;
    @Column(nullable = false)
    private String state;
    @Column
    private String username;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name="gameData")
    private POVData data;

    public Session(String token, String username) {
        this.token = token;
        this.username = username;
        this.state = "init";
    }
}
