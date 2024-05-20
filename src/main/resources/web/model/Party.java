package com.hunter.web.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hunter.data.controller.DeleteEventListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(DeleteEventListener.class)
public class Party {
	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
//	@GenericGenerator(name = "seq_gen", strategy = "com.hunter.SeqGen")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(updatable=false)
	private Long remoteId;
	private boolean synced;
	
	private String name;
	private String phone;
	private String address;
	private String gst;
	
	@OneToMany(mappedBy="mahajan")
	@JsonIgnore
	private List<StockIn> stockInList;
	
	@Override
	public String toString() {
		return name + "~" + phone + "~" + address + "~" + gst;
	}
	
}
