package work.init.game.Configs;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import work.init.game.Controllers.CustomTextHandler;
import work.init.game.Services.MatcherService;
import work.init.game.Services.SessionService;
import work.init.game.Services.SharedResourcesService;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private MatcherService matcherService;
    @Autowired
    private SharedResourcesService sharedResourcesService;
    @SneakyThrows
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new CustomTextHandler(sessionService, matcherService, sharedResourcesService), "/websocket");
    }
}
