package rcn.web.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import rcn.security.User;

@Getter
@Setter
@Entity
@ToString
public class Moderator {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String phone;
	private String address;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="user_id", unique=true)
	@JsonIgnore
	private User user;

	@Transient
	@ElementCollection
	private List<String> roles;
	
	@OneToMany(mappedBy="receivedBy")
	@JsonIgnore
	private List<Income> incomeList;

	@OneToMany(mappedBy="spentBy")
	@JsonIgnore
	private List<Expense> expenseList;

	

}
