package rcn.web.repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import rcn.web.model.Consumer;
import rcn.web.model.ConsumerDTO;

public interface ConsumerRepo extends JpaRepository<Consumer, Long>, JpaSpecificationExecutor<Consumer> {
	
	Consumer findByStbAccountNo(String stbAccountNo);
	
	@Query("FROM Consumer consumer where consumer.area = (FROM Area area where area.name = :area)")
	Page<Consumer> findConsumerByArea(String area, PageRequest pageRequest);
	
	@Transactional
	@Modifying
	@Query("update Consumer u set u.advanceAmount = u.advanceAmount - :addAmount where u.id = :id")
	void deductFromAdvanceAmountForId(Long id, double addAmount);
	
//		       "(SELECT COALESCE(SUM(d2.dueAmount - d2.paidAmount), 0) FROM Due d2 WHERE d2.consumer = c) >= :filterAmount)")
	@Query("SELECT c " +
		       "FROM Consumer c " +
		       "LEFT JOIN c.dues d " +
		       "LEFT JOIN d.duePayments dp " +
		       "GROUP BY c " +
		       "HAVING COALESCE(SUM(d.dueAmount), 0) - COALESCE(SUM(dp.amount), 0) >= :filterAmount " +
		       "ORDER BY COALESCE(SUM(d.dueAmount), 0) - COALESCE(SUM(dp.amount), 0) DESC")
		List<Consumer> findFilteredConsumers1(@Param("filterAmount") Double filterAmount);
	
	@Query("SELECT c " +
		       "FROM Consumer c " +
		       "LEFT JOIN c.dues d " +
		       "LEFT JOIN d.duePayments dp " +
		       "LEFT JOIN c.connections conn " +
		       "LEFT JOIN conn.bills b " +
		       "LEFT JOIN b.billPayments bp " +
		       "GROUP BY c " +
		       "HAVING ((COALESCE(SUM(d.dueAmount), 0) - COALESCE(SUM(dp.amount), 0)) + " +
		       "(COALESCE(SUM(b.billAmount), 0) - COALESCE(SUM(bp.amount), 0))) >= :filterAmount " +
		       "ORDER BY (COALESCE(SUM(d.dueAmount), 0) - COALESCE(SUM(dp.amount), 0)) + " +
		       "(COALESCE(SUM(b.billAmount), 0) - COALESCE(SUM(bp.amount), 0)) DESC")
		List<Consumer> findFilteredConsumers2(@Param("filterAmount") Double filterAmount);
	
	@Query(value="SELECT c AS consumer, " +
		       "((SELECT COALESCE(SUM(d.dueAmount), 0) FROM Due d WHERE d.consumer = c) - " +
		       "(SELECT COALESCE(SUM(dp.amount), 0) FROM DuePayment dp WHERE dp.due.consumer = c) + " +
		       "(SELECT COALESCE(SUM(b.billAmount), 0) FROM Bill b WHERE b.connection.consumer = c) - " +
		       "(SELECT COALESCE(SUM(bp.amount), 0) FROM BillPayment bp WHERE bp.bill.connection.consumer = c)) AS remainingBalance " +
		       "FROM Consumer c " +
		       "WHERE ((SELECT COALESCE(SUM(d.dueAmount), 0) FROM Due d WHERE d.consumer = c) - " +
		       "(SELECT COALESCE(SUM(dp.amount), 0) FROM DuePayment dp WHERE dp.due.consumer = c) + " +
		       "(SELECT COALESCE(SUM(b.billAmount), 0) FROM Bill b WHERE b.connection.consumer = c) - " +
		       "(SELECT COALESCE(SUM(bp.amount), 0) FROM BillPayment bp WHERE bp.bill.connection.consumer = c)) >= :filterAmount " +
		       "ORDER BY remainingBalance DESC",
		       countQuery="SELECT COUNT(DISTINCT c) FROM Consumer c " +
				       "WHERE ((SELECT COALESCE(SUM(d.dueAmount), 0) FROM Due d WHERE d.consumer = c) - " +
				       "(SELECT COALESCE(SUM(dp.amount), 0) FROM DuePayment dp WHERE dp.due.consumer = c) + " +
				       "(SELECT COALESCE(SUM(b.billAmount), 0) FROM Bill b WHERE b.connection.consumer = c) - " +
				       "(SELECT COALESCE(SUM(bp.amount), 0) FROM BillPayment bp WHERE bp.bill.connection.consumer = c)) >= :filterAmount ")
		Page<ConsumerDTO> findFilteredConsumers(@Param("filterAmount") Double filterAmount, PageRequest pageRequest);


}
