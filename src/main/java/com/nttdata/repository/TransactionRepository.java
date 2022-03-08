package com.nttdata.repository;

import java.math.BigDecimal;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.model.Transaction;

import reactor.core.publisher.Flux;



public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String>{


	Flux<Transaction> findByIdCustomer(String idCustomer);
	Flux<Transaction> findByProductCodeAndPeriodAndCommissionGreaterThan(String codeProduct,String period,BigDecimal commision);
	
}
