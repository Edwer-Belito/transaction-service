package com.nttdata.service;


import com.nttdata.model.Transaction;

import reactor.core.publisher.Flux;

public interface TransactionService {

    String createTransaction(Transaction e);
    Flux<Transaction> findAllTransaction ();
    Flux<Transaction> findByIdCustomer(String idCustomer);
}
