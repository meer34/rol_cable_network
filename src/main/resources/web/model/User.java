package com.hunter.web.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hunter.data.controller.DeleteEventListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(DeleteEventListener.class)
@Table(name="users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;
	
	private String username;
	@Column(unique=true)
	private String phone;
	private String pin;
	private Boolean enabled;
	
	@JsonFormat(pattern="yyyy-MM-dd")
    private Date pinGenerationTime;
    
    private static final long MIN_TO_LONG = 24L*60L*60L * 1000L;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name="users_roles", joinColumns=@JoinColumn(name ="user_id"), inverseJoinColumns=@JoinColumn(name="role_id"))
	private Set<Role> roles = new HashSet<>();
	
	public User(String username, String phone, Boolean enabled, String role){
		this.username = username;
		this.phone = phone;
		this.enabled = enabled;
		this.roles.add(new Role(role));
	}
	
    public boolean isOtpNonExpired(Long days) {
        if (this.pinGenerationTime == null) return true;
        
        if(System.currentTimeMillis() > this.pinGenerationTime.getTime() + days*MIN_TO_LONG) {
        	System.out.println("Entered PIN for " + this.phone + " has expired!");
        	return false;
        	
        } else return true;
        
    }
	
}
