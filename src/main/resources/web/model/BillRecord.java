package com.hunter.web.model;

public interface BillRecord {
	
	String getCustId();
	String getBillingName();
	String getAddress();
	Integer getTotalBill();
	Double getTotalAmount();
	Double getTotalPaid();
	Double getTotalDue();
	
	
}
