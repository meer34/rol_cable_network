package rcn.web.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
	private String fullAddress;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="area")
	private Area area;
	
	@OneToMany(mappedBy="consumer")
	private List<Connection> connections;
	
}
