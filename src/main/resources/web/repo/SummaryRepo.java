package com.hunter.web.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hunter.web.model.AccountReport;
import com.hunter.web.model.BillRecord;
import com.hunter.web.model.DashBoard;
import com.hunter.web.model.StockIn;
import com.hunter.web.model.StockOut;
import com.hunter.web.model.TotalSale;
import com.hunter.web.model.TotalStock;

public interface SummaryRepo extends JpaRepository<StockIn, Long>{
	
	//Total Stock
	@Query("SELECT new com.hunter.web.model.TotalStock(p.name as name, p.size as size, p.colour as colour, p.brand as brand, COUNT(DISTINCT p.id) as totalProductCount, "
			+ "COALESCE(p.quantity, 0) - COALESCE(SUM(sop.quantity), 0) as totalQuantity) "
			+ "FROM Product p LEFT JOIN StockOutProduct sop ON p.id = sop.product "
			+ "GROUP BY p.id, p.name , p.size , p.colour , p.brand")
	List<TotalStock> findAllTotalStocksByProductDetails();
	
	//Total Sale
	@Query("SELECT p.name AS productName, p.size AS size, p.colour AS colour, p.brand AS brand, COALESCE(SUM(sop.quantity), 0) AS quantity "
			+ "FROM StockOutProduct sop LEFT JOIN Product p on p.id = sop.product "
			+ "GROUP BY p.name, p.size, p.colour, p.brand")
	List<TotalSale> findTotalSales();
	
	@Query("FROM StockOut so WHERE EXISTS (SELECT 1 FROM StockOutProduct sop LEFT JOIN Product p ON p.id = sop.product "
			+ "WHERE so.id = sop.stockOut AND p.name = :productName AND p.size = :size AND p.colour = :colour AND p.brand = :brand)")
	List<StockOut> findStockOutsByProductDetails(String productName, String size, String colour, String brand);
	
	
	//Dash board
	@Query("SELECT p.name AS productName, p.brand AS brand, COALESCE(SUM(p.quantity), 0) AS stockInQuantity, COALESCE(SUM(sop.quantity), 0) AS stockOutQuantity "
			+ "FROM Product p LEFT JOIN StockOutProduct sop ON sop.product = p.id GROUP BY p.name, p.brand")
	List<DashBoard> findAllDashBoardItems();
	
	@Query("SELECT p.name AS productName, p.brand AS brand, "
			+ "COALESCE(SUM(CASE WHEN si.id <> null THEN p.quantity ELSE 0 END), 0) AS stockInQuantity, "
			+ "COALESCE(SUM(CASE WHEN so.id <> null THEN sop.quantity ELSE 0 END), 0) AS stockOutQuantity "
			+ "FROM Product p LEFT JOIN StockOutProduct sop ON sop.product = p.id "
			+ "LEFT JOIN StockIn si on si.id = p.stockIn AND si.date >= :fromDate AND si.date <= :toDate "
			+ "LEFT JOIN StockOut so ON so.id = sop.stockOut AND so.date >= :fromDate AND so.date <= :toDate "
			+ "WHERE ( p.name LIKE %:keyword% OR p.brand LIKE %:keyword% ) GROUP BY p.name, p.brand")
	List<DashBoard> findAllDashBoardItemsByDateAndMahajan(Date fromDate, Date toDate, String keyword);
	
	
	//Income Expense
	@Query("SELECT COALESCE(SUM(income.totalAmount), 0) FROM Income income")
	Long findTotalIncome();
	
	@Query("SELECT COALESCE(SUM(expense.totalAmount), 0) FROM Expense expense")
	Long findTotalExpense();
	
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
	
	
	//Bill Record
	@Query("SELECT cust.id AS custId, cust.name AS billingName, cust.address AS address, COALESCE(COUNT(DISTINCT so.id), 0) AS totalBill, "
			+ "COALESCE(SUM(so.netAmount), 0) AS totalAmount, COALESCE(SUM(so.totalPaid), 0) AS totalPaid, COALESCE(SUM(so.totalDue), 0) AS totalDue "
			+ "FROM StockOut so LEFT JOIN Customer cust ON so.customer = cust.id GROUP BY cust.id, cust.name, cust.address")
	List<BillRecord> findBillRecord();


}
