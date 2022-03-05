package com.nttdata.dto;

import java.math.BigDecimal;

import com.nttdata.model.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferDto {
	
	private Customer customerOrigin;
	private Customer customerDestination;
	private BigDecimal amount;
	
	

}
