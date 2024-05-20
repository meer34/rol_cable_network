package com.hunter.web.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hunter.data.controller.DeleteEventListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(DeleteEventListener.class)
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(updatable=false)
	private Long remoteId;
	private boolean synced;
	
	private String name;
	private String scanCode;
	private String size;
	private String colour;
	private String brand;
	private int quantity;
	private double rate;
	private double amount;
	
	@OneToMany(mappedBy="product")
	@JsonIgnore
	private List<StockOutProduct> stockOutProductList;
	
	@ManyToOne
	@JoinColumn(name="stockIn")
	private StockIn stockIn;
	
	public Product(String[] arr, StockIn stockIn) {
		if(arr[0] != null && !"".equals(arr[0])) this.id = Long.parseLong(arr[0]);
		this.name = arr[1];
		this.size = arr[2];
		this.colour = arr[3];
		this.brand = arr[4];
		this.quantity = Integer.valueOf(arr[5]!=""? arr[5]: "0");
		this.rate = Double.valueOf(arr[6]!=""? arr[6]: "0");
		this.amount = Double.valueOf(arr[7]!=""? arr[7]: "0");
		this.scanCode = arr[8];
		this.stockIn = stockIn;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(String.valueOf(id))
				.append("~").append(name)
				.append("~").append(scanCode)
				.append("~").append(size)
				.append("~").append(colour)
				.append("~").append(brand)
				.append("~").append(quantity)
				.append("~").append(rate)
				.append("~").append(amount)
				.toString();
	}

}
