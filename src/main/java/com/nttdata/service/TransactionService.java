package com.nttdata.service;


import com.nttdata.dto.TransferDto;
import com.nttdata.model.Transaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {

    String createTransaction(Transaction e);
    Flux<Transaction> findAllTransaction ();
    Flux<Transaction> findByIdCustomer(String idCustomer);
    Mono<Long> countTransactionByCustomerId(String idCustomer);
    String createTransfer(TransferDto dtoTransferDto);
}
