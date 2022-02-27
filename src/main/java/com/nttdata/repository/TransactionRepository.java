package com.nttdata.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.model.Transaction;



public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String>{


}
