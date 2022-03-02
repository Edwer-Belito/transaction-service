package com.nttdata.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	public String code;
	
	@Transient
	public BigDecimal saldo;
	
}
