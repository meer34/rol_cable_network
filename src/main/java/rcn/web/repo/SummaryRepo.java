package rcn.web.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import rcn.web.model.AccountReport;
import rcn.web.model.Income;

public interface SummaryRepo extends JpaRepository<Income, Long>{
	
//	//Dash board
//	@Query("SELECT p.name AS productName, p.brand AS brand, COALESCE(SUM(p.quantity), 0) AS stockInQuantity, COALESCE(SUM(sop.quantity), 0) AS stockOutQuantity "
//			+ "FROM Product p LEFT JOIN StockOutProduct sop ON sop.product = p.id GROUP BY p.name, p.brand")
//	List<DashBoard> findAllDashBoardItems();
//	
//	@Query("SELECT p.name AS productName, p.brand AS brand, "
//			+ "COALESCE(SUM(CASE WHEN si.id <> null THEN p.quantity ELSE 0 END), 0) AS stockInQuantity, "
//			+ "COALESCE(SUM(CASE WHEN so.id <> null THEN sop.quantity ELSE 0 END), 0) AS stockOutQuantity "
//			+ "FROM Product p LEFT JOIN StockOutProduct sop ON sop.product = p.id "
//			+ "LEFT JOIN StockIn si on si.id = p.stockIn AND si.date >= :fromDate AND si.date <= :toDate "
//			+ "LEFT JOIN StockOut so ON so.id = sop.stockOut AND so.date >= :fromDate AND so.date <= :toDate "
//			+ "WHERE ( p.name LIKE %:keyword% OR p.brand LIKE %:keyword% ) GROUP BY p.name, p.brand")
//	List<DashBoard> findAllDashBoardItemsByDateAndMahajan(Date fromDate, Date toDate, String keyword);
//	
	
	//Income Expense
	@Query("SELECT COALESCE(SUM(income.totalAmount), 0) FROM Income income")
	Long findTotalIncome();
	
	@Query("SELECT COALESCE(SUM(collection.netAmount), 0) FROM Collection collection")
	Long findTotalCollection();
	
	@Query("SELECT COALESCE(SUM(expense.totalAmount), 0) FROM Expense expense")
	Long findTotalExpense();
	
	@Query("SELECT appUser.name AS category, COALESCE(SUM(collection.netAmount), 0) AS totalAmount "
			+ "FROM AppUser appUser LEFT JOIN Collection collection ON appUser.id = collection.collectedBy "
			+ "GROUP BY appUser.name")
	List<AccountReport> findCollectionReport();
	
	@Query("SELECT appUser.name AS category, COALESCE(SUM(collection.netAmount), 0) AS totalAmount "
			+ "FROM AppUser appUser LEFT JOIN Collection collection ON appUser.id = collection.collectedBy "
			+ "WHERE appUser.name LIKE %:appUserName% AND collection.date >= :fromDate "
			+ "AND collection.date <= :toDate GROUP BY appUser.name")
	List<AccountReport> findCollectionReport(Date fromDate, Date toDate, String appUserName);
	
	@Query("SELECT type.name AS category, COALESCE(SUM(income.totalAmount), 0) AS totalAmount "
			+ "FROM IncomeType type LEFT JOIN Income income ON type.id = income.incomeType GROUP BY type.name")
	List<AccountReport> findIncomeReport();
	
	@Query("SELECT type.name AS category, COALESCE(SUM(income.totalAmount), 0) AS totalAmount "
			+ "FROM IncomeType type LEFT JOIN Income income ON type.id = income.incomeType "
			+ "WHERE type.name LIKE %:incomeType% AND income.date >= :fromDate AND income.date <= :toDate GROUP BY type.name")
	List<AccountReport> findIncomeReport(Date fromDate, Date toDate, String incomeType);
	
	@Query("SELECT type.name AS category, COALESCE(SUM(expense.totalAmount), 0) AS totalAmount "
			+ "FROM ExpenseType type LEFT JOIN Expense expense ON type.id = expense.expenseType GROUP BY type.name")
	List<AccountReport> findExpenseReport();
	
	@Query("SELECT type.name AS category, COALESCE(SUM(expense.totalAmount), 0) AS totalAmount "
			+ "FROM ExpenseType type LEFT JOIN Expense expense ON type.id = expense.expenseType "
			+ "WHERE type.name like %:expenseType% AND expense.date >= :fromDate AND expense.date <= :toDate GROUP BY type.name")
	List<AccountReport> findExpenseReport(Date fromDate, Date toDate, String expenseType);


}
