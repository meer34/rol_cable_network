package rcn.web.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Collection {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="consumer")
	private Consumer consumer;
	
	private String billType;
	private Double amount;
	private Double discount;
	private Double advanceAmount;
	private Double netAmount;
	private String paymentMode;
	private String remarks;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="collectedBy")
	private AppUser collectedBy;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date date;
	
	@Transient
	private List<Bill> bills;
	
	@Transient
	private List<Due> dues;
	
	@OneToMany(mappedBy="collection", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BillPayment> billPayments;
	
	@OneToMany(mappedBy="collection", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DuePayment> duePayments;
	
	

	public Collection processForEdit() {
		if(this.billPayments != null) {
			this.bills = new ArrayList<>();
			for (BillPayment billPayment : billPayments) {
				Bill bill = billPayment.getBill();
				bill.getBillPayments().remove(billPayment);
				this.bills.add(bill);
			}
		}
		if(this.duePayments != null) {
			this.dues = new ArrayList<>();
			for (DuePayment duePayment : duePayments) {
				Due due = duePayment.getDue();
				due.getDuePayments().remove(duePayment);
				this.dues.add(due);
			}
		}
		return this;
	}
	
	public Collection processForEdit1() {
		if(this.bills != null) {
			this.bills = this.bills
					.stream()
					.map(bill -> {
						bill.setPaidAmount(bill.getPaidAmount() - bill.getCollectedAmount());
						bill.setCollectedAmount(0);
						return bill;
					})
					.collect(Collectors.toList());
		}
		if(this.dues != null) {
			this.dues = this.dues
					.stream()
					.map(due -> {
						due.setPaidAmount(due.getPaidAmount() - due.getCollectedAmount());
						due.setCollectedAmount(0);
						return due;
					})
					.collect(Collectors.toList());
		}
		return this;
	}

	public void addBillPayment(BillPayment billPayment) {
		if(billPayments == null) billPayments = new ArrayList<>();
		billPayments.add(billPayment);
	}
	
	public void addDuePayment(DuePayment duePayment) {
		if(duePayments == null) duePayments = new ArrayList<>();
		duePayments.add(duePayment);
	}
	
}
