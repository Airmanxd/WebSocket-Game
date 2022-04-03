package work.init.game.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import work.init.game.Entities.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    Session findByToken(String token);
}
