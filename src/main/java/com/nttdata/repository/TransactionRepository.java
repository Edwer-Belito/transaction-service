package com.nttdata.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.model.Transaction;

import reactor.core.publisher.Flux;



public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String>{


	Flux<Transaction> findByIdCustomer(String idCustomer);
}
