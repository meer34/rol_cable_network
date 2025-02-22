package rcn.web.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name;
	private String designation;
	private String phone;
	@Temporal(TemporalType.DATE) @DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date joiningDate;
	private String address;
	private Double salary;
	private String idProof;
	private String qualification;
	private String remarks;
	
}
