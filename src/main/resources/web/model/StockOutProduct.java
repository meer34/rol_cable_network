package com.hunter.web.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.hunter.data.controller.DeleteEventListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(DeleteEventListener.class)
public class StockOutProduct {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(updatable=false)
	private Long remoteId;
	private boolean synced;

	private long stockInProduct;
	private int quantity;
	private double rate;
	private double amount;

	@ManyToOne
	@JoinColumn(name="product")
	private Product product;

	@ManyToOne
	@JoinColumn(name="stockOut")
	private StockOut stockOut;

	public StockOutProduct(String[] arr, StockOut stockOut, Product product) {
		if(arr[0] != null && !"".equals(arr[0])) this.id = Long.parseLong(arr[0]);
		this.stockInProduct = Long.parseLong(arr[1]);
		this.product = product;
		this.stockOut = stockOut;
		
		this.quantity = Integer.valueOf(arr[2]!=""? arr[2]: "0");
		this.rate = Double.valueOf(arr[3]!=""? arr[3]: "0");
		this.amount = Double.valueOf(arr[4]!=""? arr[4]: "0");
	}

}
