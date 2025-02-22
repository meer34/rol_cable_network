package rcn.web.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BillPayment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private double amount;
	
	@ManyToOne
	@JoinColumn(name="bill")
	private Bill bill;
	
	@ManyToOne
	@JoinColumn(name="collection")
	private Collection collection;

}
