package rcn.web.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import rcn.web.model.Consumer;

public interface ConsumerRepo extends JpaRepository<Consumer, Long>, JpaSpecificationExecutor<Consumer> {
	
	@Query("FROM Consumer conn where conn.area = (FROM Area area where area.name = :area)")
	Page<Consumer> findConsumerByArea(String area, PageRequest pageRequest);
	
}
