package com.nttdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.model.Transaction;
import com.nttdata.service.TransactionService;

import reactor.core.publisher.Flux;

@RestController()
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createEmp (@RequestBody Transaction transaction){
		transactionService.createTransaction(transaction);
    }

    @GetMapping(value = "/getAll",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<Transaction> findAll(){
        return transactionService.findAllTransaction();
    }


    /*
    @GetMapping("/get/{customerId}")
    @ResponseBody
    public ResponseEntity<Flux<Transaction>> findById(@PathVariable("customerId") Integer customerId){
       Flux<Transaction> employeeMono= transactionService.findByTransactionByCustomerId(customerId);
        return new ResponseEntity<Flux<Transaction>>(employeeMono,employeeMono != null? HttpStatus.OK:HttpStatus.NOT_FOUND);
    }
    
    */
}
