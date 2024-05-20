package rcn.web.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import rcn.web.model.Bill;

public interface BillRepo extends JpaRepository<Bill, Long>, JpaSpecificationExecutor<Bill> {
	
	@Query("FROM Bill bill WHERE bill.connection = (FROM Connection connection WHERE connection.id = :connectionId)")
	Page<Bill> findPageByConnection(Long connectionId, PageRequest of);

}
