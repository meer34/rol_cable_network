package rcn.web.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import rcn.web.model.Connection;

public interface ConnectionRepo extends JpaRepository<Connection, Long>, JpaSpecificationExecutor<Connection> {
	
	@Query("FROM Connection conn where conn.consumer = (FROM Consumer cons where cons.id = :consumerId)")
	Connection findByConsumerId(Long consumerId);
	
	@Query("FROM Connection conn where conn.consumer = (FROM Consumer cons where cons.id = :consumerId)")
	Page<Connection> findPageByConsumerId(Long consumerId, PageRequest pageRequest);

}
