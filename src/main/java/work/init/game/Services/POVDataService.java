package work.init.game.Services;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.init.game.Entities.POVData;
import work.init.game.Repositories.POVDataRepository;

@Service
@NoArgsConstructor
public class POVDataService {
    @Autowired
    private POVDataRepository repository;

    public void add(POVData data){
        repository.save(data);
    }
}
