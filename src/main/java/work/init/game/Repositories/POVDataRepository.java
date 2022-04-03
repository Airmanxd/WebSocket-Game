package work.init.game.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import work.init.game.Entities.POVData;

@Repository
public interface POVDataRepository extends JpaRepository<POVData, String> {
    POVData findByUsername(String username);
}
