package pl.usterkimiejskie.usterkimiejskie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.usterkimiejskie.usterkimiejskie.entity.Komentarz;

import java.util.List;


public interface KomentarzRepository extends JpaRepository<Komentarz, Long> {
    List<Komentarz> findByUsterkaId(Long usterkaId);
}
