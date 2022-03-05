package com.nttdata.common;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Operation {

	private String deposit;
	private String retirement;
	private String payment;
	private String consumption;
	private Integer numberOperationAllowed;
	private BigDecimal commision;
}
