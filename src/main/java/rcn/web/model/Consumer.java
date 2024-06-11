package rcn.web.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Consumer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String fullName;
	private String nickName;
	private String fatherName;
	private String phoneNo;
	private String landmark;
	private double securityDeposit;
	private String remarks;
	private String fullAddress;
	@Transient
	private double subscriptionBill;
	@Transient
	private double otherDueBill;
	@Transient
	private double totalPaid;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="area")
	private Area area;
	
	@OneToMany(mappedBy="consumer")
	private List<Connection> connections;
	
	@OneToMany(mappedBy="consumer")
	private List<Due> dues;
	
	@OneToMany(mappedBy="consumer")
	private List<Collection> collections;
	
	public void calculateTotalSubscriptionBill() {
		subscriptionBill = 0;
		for (Connection connection : connections) {
			for (Bill bill : connection.getBills()) {
				subscriptionBill += bill.getBillAmount() - bill.getPaidAmount();
			}
		}
	}
	
	public void calculateTotalOtherDueBill() {
		otherDueBill = 0;
		for (Due due : dues) {
			otherDueBill += due.getDueAmount() - due.getPaidAmount();
		}
	}
	
	public void calculateTotalPaid() {
		totalPaid= 0;
		for (Collection collection : collections) {
			totalPaid += collection.getAmount();
		}
	}

	public void calculateTotalSubscriptionBillForYear(Date yearStartDate, Date yearEndDate) {
		subscriptionBill = 0;
		for (Connection connection : connections) {
			for (Bill bill : connection.getBills()) {
				if((bill.getEndDate().after(yearStartDate) || bill.getEndDate().equals(yearStartDate))
						& (bill.getEndDate().before(yearEndDate) || bill.getEndDate().equals(yearEndDate))) {
					subscriptionBill += bill.getBillAmount();
				}
			}
		}
	}
	
	public void calculateTotalOtherDueBillForYear(Date yearStartDate, Date yearEndDate) {
		otherDueBill = 0;
		for (Due due : dues) {
			if((due.getDateOfDueEntry().after(yearStartDate) || due.getDateOfDueEntry().equals(yearStartDate))
					& (due.getDateOfDueEntry().before(yearEndDate) || due.getDateOfDueEntry().equals(yearEndDate))) {
				otherDueBill += due.getDueAmount();
			}
		}
	}
	
	public void calculateTotalPaidForYear(Date yearStartDate, Date yearEndDate) {
		totalPaid = 0;
		for (Collection collection : collections) {
			if((collection.getDate().after(yearStartDate) || collection.getDate().equals(yearStartDate))
					& (collection.getDate().before(yearEndDate) || collection.getDate().equals(yearEndDate))) {
				totalPaid += collection.getAmount();
			}
		}
	}
	
}
