package rcn.web.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import rcn.web.model.AccountReport;
import rcn.web.model.BillRecord;
import rcn.web.model.DashBoard;
import rcn.web.model.StockIn;
import rcn.web.model.StockOut;
import rcn.web.model.TotalSale;
import rcn.web.model.TotalStock;

public interface SummaryRepo extends JpaRepository<StockIn, Long>{
	
	//Total Stock
	@Query("SELECT si.id AS id, si.sortNo AS sortNo, p.name AS mahajanName, "
			+ "COUNT(DISTINCT r.rollNo) AS totalRollCount, COALESCE(SUM(r.quantity), 0) AS totalQuantity "
			+ "FROM Roll r LEFT JOIN StockIn si on r.stockIn = si.id LEFT JOIN Party p on si.mahajan = p.id "
			+ "WHERE r.stockOutIndicator = FALSE GROUP BY si.id, si.sortNo, p.name")
	Page<TotalStock> findAllTotalStocksBySortNo(Pageable pageable);
	
	@Query("SELECT p.name AS mahajanName, "
			+ "COUNT(DISTINCT r.rollNo) AS totalRollCount, COALESCE(SUM(r.quantity), 0) AS totalQuantity "
			+ "FROM Party p LEFT JOIN StockIn si on p.id = si.mahajan LEFT JOIN Roll r on si.id = r.stockIn AND r.stockOutIndicator = FALSE  GROUP BY p.name")
//	@Query("SELECT p.name AS mahajanName, COALESCE(SUM(si.totalQuantity), 0) AS stockInCount, COALESCE(SUM(r.quantity), 0) AS stockOutCount "
//			+ "FROM Party p LEFT JOIN StockIn si on p.id = si.mahajan LEFT JOIN Roll r on si.id = r.stockIn AND r.stockOutIndicator = FALSE  GROUP BY p.name")
	Page<TotalStock> findAllTotalStocksByMahajanName(Pageable pageable);
	
	
	//Total Sale
	@Query("SELECT roll.sortNo AS sortNo, roll.rollNo AS rollNo, COALESCE(SUM(roll.quantity), 0) AS quantity FROM Roll roll "
			+ "WHERE roll.stockOutIndicator = TRUE GROUP BY roll.sortNo, roll.rollNo")
	List<TotalSale> findAllByTotalSales();
	
	@Query("SELECT roll.sortNo AS sortNo, roll.rollNo AS rollNo, COALESCE(SUM(roll.quantity), 0) AS quantity FROM Roll roll "
			+ "WHERE roll.stockOutIndicator = TRUE AND (roll.sortNo LIKE %:keyword% OR roll.rollNo LIKE %:keyword% ) GROUP BY roll.sortNo, roll.rollNo")
	List<TotalSale> findTotalSalesBySortNoOrRollNo(String keyword);
	
	@Query("FROM StockOut so WHERE EXISTS (SELECT 1 FROM Roll roll WHERE so.id = roll.stockOut AND roll.stockOutIndicator = TRUE)")
	List<StockOut> findStockOutsBySortNoAndRollNo(String sortNo, String rollNo);
	
	
	//Dash board
	@Query("SELECT p.name AS mahajanName, COALESCE(SUM(si.totalQuantity), 0) AS stockInCount, COALESCE(SUM(r.quantity), 0) AS stockOutCount "
			+ "FROM Party p LEFT JOIN StockIn si on p.id = si.mahajan LEFT JOIN Roll r on si.id = r.stockIn AND r.stockOutIndicator = TRUE  GROUP BY p.name")
	List<DashBoard> findAllDashBoardItems();
	
	@Query("SELECT p.name AS mahajanName, COALESCE(SUM(si.totalQuantity), 0) AS stockInCount, COALESCE(SUM(r.quantity), 0) AS stockOutCount "
			+ "FROM Party p LEFT JOIN StockIn si on p.id = si.mahajan LEFT JOIN Roll r on si.id = r.stockIn AND r.stockOutIndicator = TRUE "
			+ "WHERE (si.date > :fromDate AND si.date < :toDate ) AND p.name LIKE %:mahajanName% GROUP BY p.name")
	List<DashBoard> findAllDashBoardItemsByDateAndMahajan(Date fromDate, Date toDate, String mahajanName);
	
	
	//Income Expense
	@Query("SELECT COALESCE(SUM(income.totalAmount), 0) FROM Income income")
	Long findTotalIncome();
	
	@Query("SELECT COALESCE(SUM(expense.totalAmount), 0) FROM Expense expense")
	Long findTotalExpense();
	
	@Query("SELECT type.name AS category, COALESCE(SUM(income.totalAmount), 0) AS totalAmount "
			+ "FROM IncomeType type LEFT JOIN Income income ON type.id = income.incomeType GROUP BY type.name")
	List<AccountReport> findIncomeReport();
	
	@Query("SELECT type.name AS category, COALESCE(SUM(expense.totalAmount), 0) AS totalAmount "
			+ "FROM ExpenseType type LEFT JOIN Expense expense ON type.id = expense.expenseType GROUP BY type.name")
	List<AccountReport> findExpenseReport();
	
	
	//Bill Record
	@Query("SELECT cust.id AS custId, cust.name AS billingName, cust.address AS address, COALESCE(COUNT(DISTINCT so.id), 0) AS totalBill, "
			+ "COALESCE(SUM(so.netAmount), 0) AS totalAmount, COALESCE(SUM(so.totalPaid), 0) AS totalPaid, COALESCE(SUM(so.totalDue), 0) AS totalDue "
			+ "FROM StockOut so LEFT JOIN Customer cust ON so.customer = cust.id GROUP BY cust.id, cust.name, cust.address")
	List<BillRecord> findBillRecord();

}
