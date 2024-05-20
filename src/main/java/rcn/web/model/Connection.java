package rcn.web.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
public class Connection {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
//	@OneToOne
//	@JoinColumn(name="consumer")
//	private Consumer consumer;
	
	@ManyToOne
	@JoinColumn(name="consumer")
	private Consumer consumer;
	
	private String state;
	private String connectionType;
	private String connectionFor;
	private double connectionChange;
	private double securityDeposit;
	
	private double subscriptionAmount;
	private double previousDue;
	private double advanceAmount;
	
	private String remarks;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateOfConnStart;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateOfConnExpiry;
	
	@OneToOne @JoinColumn(name="channelPackage") private ChannelPackage channelPackage;
	
	@ManyToMany
	@JoinTable(
	  name = "connection_bucket", 
	  joinColumns = @JoinColumn(name = "connection_id"), 
	  inverseJoinColumns = @JoinColumn(name = "bucket_id"))
	private List<Bucket> buckets;
	
	@ManyToMany
	@JoinTable(
	  name = "connection_channel", 
	  joinColumns = @JoinColumn(name = "connection_id"), 
	  inverseJoinColumns = @JoinColumn(name = "channel_id"))
	private List<Channel> channels;
	
	@OneToMany(mappedBy="connection")
	private List<Bill> bills;
	
}
