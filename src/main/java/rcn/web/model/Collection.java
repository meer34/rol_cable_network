package rcn.web.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
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
	@Transient
	private Double advanceAmount;
	private Double netAmount;
	private String paymentMode;
	private String remarks;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="collectedBy")
	private AppUser collectedBy;
	
	@ManyToMany
	@JoinTable(
	  name = "collection_bill", 
	  joinColumns = @JoinColumn(name = "collection_id"), 
	  inverseJoinColumns = @JoinColumn(name = "bill_id"))
	@OrderBy("startDate DESC")
	private List<Bill> bills;
	
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(
	  name = "collection_due", 
	  joinColumns = @JoinColumn(name = "collection_id"), 
	  inverseJoinColumns = @JoinColumn(name = "due_id"))
	@OrderBy("dateOfDueEntry DESC")
	private List<Due> dues;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date date;
	
}
