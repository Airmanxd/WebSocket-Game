package work.init.game.Services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import work.init.game.Entities.POVData;
import work.init.game.Repositories.SessionRepository;
import work.init.game.Entities.Session;

import java.io.IOException;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class SessionService {
    @Autowired
    private SessionRepository repository;

    public void updateSessionStateByToken(String token, String state){
        Session sesh = repository.findByToken(token);
        sesh.setState(state);
        repository.save(sesh);
    }
    public void addSession(String token, String username) throws IOException {
        Session temp = repository.findByToken(token);
        if(temp==null){
            Session sesh = new Session(token, username);
            repository.save(sesh);
        }
    }
    public String getUsernameByToken(String token){
        return repository.findByToken(token).getUsername();
    }

    public void setPOVDataByToken(String token, POVData data){
        Session temp = repository.findByToken(token);
        temp.setData(data);
        repository.save(temp);
    }
    public void remove(WebSocketSession session){
        repository.deleteById(session.getAttributes().get("token").toString());
    }

}
