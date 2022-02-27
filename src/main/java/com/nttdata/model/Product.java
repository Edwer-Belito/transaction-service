package com.nttdata.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	public String code;
	public String name;
	public String type;
	public BigDecimal saldo;
	
}