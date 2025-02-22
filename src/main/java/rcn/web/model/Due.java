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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Due {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String dueType;
	private double dueAmount;
	private String remarks;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateOfDueEntry;
	
	@ManyToOne
	@JoinColumn(name="consumer")
	private Consumer consumer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="createdBy")
	private AppUser createdBy;
	
	@Transient
	private double paidAmount;
	@Transient
	private double collectedAmount;
	
	@OneToMany(mappedBy="due")
	private List<DuePayment> duePayments;
	
	public double getPaidAmount() {
		paidAmount = 0;
		if(duePayments == null) return paidAmount;
		for (DuePayment duePayment : duePayments) {
			paidAmount += duePayment.getAmount();
		}
		return paidAmount;
	}
	
}
