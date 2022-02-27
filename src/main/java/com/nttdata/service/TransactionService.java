package com.nttdata.service;


import com.nttdata.model.Transaction;

import reactor.core.publisher.Flux;

public interface TransactionService {

    void createTransaction(Transaction e);
    Flux<Transaction> findAllTransaction ();
}
