package rcn.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import rcn.web.model.Bouquet;

public interface BouquetRepo extends JpaRepository<Bouquet, Long>, JpaSpecificationExecutor<Bouquet>{

}
