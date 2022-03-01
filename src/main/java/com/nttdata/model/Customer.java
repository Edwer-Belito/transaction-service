package com.nttdata.model;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

	
	public String idCustomer = UUID.randomUUID().toString().substring(0, 10);
	public String name;
	public String customerType;
	public List<Product> product;
	
}
