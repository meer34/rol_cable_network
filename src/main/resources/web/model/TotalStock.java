package com.hunter.web.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalStock {
	
	private String name;
	private String size;
	private String colour;
	private String brand;
	private long totalProductCount;
	private long totalQuantity;

	public TotalStock(String name, String size, String colour, String brand, long totalProductCount, long totalQuantity) {
		super();
		this.name = name;
		this.size = size;
		this.colour = colour;
		this.brand = brand;
		this.totalProductCount = totalProductCount;
		this.totalQuantity = totalQuantity;
	}
	
	public String getConcat() {
		return new StringBuilder(this.name)
				.append(this.size)
				.append(this.colour)
				.append(this.brand)
				.toString();
	}

	public static List<TotalStock> getListOfTotalStock(List<TotalStock> listOfTotalStock) {
		
		if(listOfTotalStock != null && !listOfTotalStock.isEmpty()) {
			Map<String, TotalStock> mapOfTotalStock = new HashMap<>();
			String cat = null;
			TotalStock temp = null;
			for (TotalStock totalStock : listOfTotalStock) {
				cat = totalStock.getConcat();
				if(mapOfTotalStock.containsKey(cat)) {
					temp = mapOfTotalStock.get(cat);
					temp.setTotalProductCount(temp.getTotalProductCount() + totalStock.getTotalProductCount());
					temp.setTotalQuantity(temp.getTotalQuantity() + totalStock.getTotalQuantity());
				} else {
					mapOfTotalStock.put(cat, totalStock);
				}
			}
			return new ArrayList<TotalStock>(mapOfTotalStock.values());
		}
		return null;
		
	}

}
