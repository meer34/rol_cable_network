package com.hunter.web.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hunter.data.controller.DeleteEventListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(DeleteEventListener.class)
public class Moderator {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(updatable=false)
	private Long remoteId;
	private boolean synced;
	
	private String name;
	private String phone;
	private String address;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="user_id", unique=true)
	@JsonIgnore
	private User user;
	
	@OneToMany(mappedBy="receivedBy")
	@JsonIgnore
	private List<Income> incomeList;
	
	@OneToMany(mappedBy="spentBy")
	@JsonIgnore
	private List<Expense> expenseList;
	
	@Override
	public String toString() {
		return name + "~" + phone + "~" + address;
	}
	
}
