package rcn.web.repo;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import rcn.web.model.Bill;
import rcn.web.model.Connection;

public interface BillRepo extends JpaRepository<Bill, Long>, JpaSpecificationExecutor<Bill> {
	
	@Query("FROM Bill bill WHERE bill.connection = (FROM Connection connection WHERE connection.id = :connectionId)")
	Page<Bill> findPageByConnection(Long connectionId, PageRequest of);

//	List<Bill> deleteByConnectionAndStartDateAndEndDate(Connection connection, Date dateOfConnStart, Date dateOfConnExpiry);
	
	@Query("SELECT b FROM Bill b LEFT JOIN FETCH b.billPayments WHERE b.connection = :connection AND b.startDate = :dateOfConnStart AND b.endDate = :dateOfConnExpiry")
	Bill findByConnectionAndStartDateAndEndDate(Connection connection, Date dateOfConnStart, Date dateOfConnExpiry);

}
