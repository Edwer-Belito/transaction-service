package com.nttdata.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transaction")
public class Transaction {
	
	@Id
	public String idTransaccion = UUID.randomUUID().toString().substring(0, 10);
	public String transactionType;
	public String idCustomer;
	public Product product;

}
