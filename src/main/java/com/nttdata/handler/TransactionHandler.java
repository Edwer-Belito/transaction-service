package com.nttdata.handler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.nttdata.model.Customer;
import com.nttdata.model.Transaction;
import com.nttdata.service.TransactionService;

import reactor.core.publisher.Mono;

@Component
public class TransactionHandler {

	private final TransactionService transactionService;
	
	Mono<ServerResponse> notFound = ServerResponse.notFound().build();
	
	@Autowired
	public TransactionHandler(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	
	
	public Mono<ServerResponse> getAllTransaction(ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(transactionService.findAllTransaction().log("Func: "), Customer.class);
    }
    
    public Mono<ServerResponse> getTransactionByCustomerId(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        //Flux<Transaction> itemMono = transactionService.findByIdCustomer(id);
        return 
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(transactionService.findByIdCustomer(id),Transaction.class)
                        .switchIfEmpty(notFound);

    }
    
    public Mono<ServerResponse> createTransaction(ServerRequest serverRequest) {
        Mono<Transaction> transactionMono = serverRequest.bodyToMono(Transaction.class);

        return transactionMono.flatMap(customer ->
           ServerResponse.status(HttpStatus.CREATED)
                   .contentType(MediaType.APPLICATION_JSON)
                   .body(transactionService.createTransaction(transactionMono.block()), Transaction.class));

    }
    
}
