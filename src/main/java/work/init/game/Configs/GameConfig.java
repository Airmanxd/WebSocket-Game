package work.init.game.Configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "game.const")
@Data
public class GameConfig {
    private int maxRounds;
    private int roundTimeout;
    private int updateRate;
}
