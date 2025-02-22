package com.hunter.web.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hunter.data.controller.DeleteEventListener;
import com.hunter.web.repo.ProductRepo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(DeleteEventListener.class)
public class StockIn {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(updatable=false)
	private Long remoteId;
	private boolean synced;
	
	private String billNo;
	private Integer totalQuantity;
	private Double totalPrice;
	private String remarks;

	@ManyToOne
	@JoinColumn(name ="mahajan")
	private Party mahajan;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date date;

	@Transient
	@JsonIgnore
	private List<String> stockInParts;

	@OneToMany(mappedBy="stockIn", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Product> productList;
	
	public void processParts(ProductRepo productRepo) {
		
		this.totalQuantity = 0;
		this.totalPrice = 0.0;
		
		if(this.stockInParts != null) {
			this.productList = new ArrayList<>();
			Product newProduct = null;

			List<Long> currentChildIds = new ArrayList<>();

			for (String stockInPartString : stockInParts) {
				String[] arr = stockInPartString.split("\\|\\~\\|", -1);
				newProduct = new Product(arr, this);
				
				this.productList.add(newProduct);
				currentChildIds.add(newProduct.getId());
				
				totalQuantity += Integer.valueOf(arr[5]!=""? arr[5]: "0");
				totalPrice += Double.valueOf(arr[7]!=""? arr[7]: "0");
			}

			if(this.id != null) {
				List<Product> listOfOrphanProducts =  productRepo.getStockInOrphanChilds(this.id, currentChildIds);
				if(listOfOrphanProducts == null) return;
				for (Product orphanProduct : listOfOrphanProducts) {
					productRepo.delete(orphanProduct);
				}
			}

		} else {
			List<Product> listOfOrphanProducts =  productRepo.getStockInOrphanChilds(this.id, Arrays.asList(new Long[] {0L}));
			if(listOfOrphanProducts == null) return;
			for (Product orphanProduct : listOfOrphanProducts) {
				productRepo.delete(orphanProduct);
			}
			
		}
	}

}
