package com.nttdata.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.nttdata.dto.ReporteComissionsDto;
import com.nttdata.dto.TransferDto;
import com.nttdata.model.Transaction;
import com.nttdata.service.TransactionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/transaction")
public class TransactionController {
	
	private static Logger logger = LoggerFactory.getLogger(TransactionController.class);

	@Autowired
	private TransactionService transactionService;
	
	@PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public String createTransaction (@RequestBody Transaction transaction){
		logger.info("TransactionController - createTransaction - TRANSACTION DATA: {}" , transaction);
		transaction.setMovementType("OPERATION");
		return transactionService.createTransaction(transaction);
    }

    @GetMapping(value = "/getAll",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<Transaction> findAll(){
    	logger.info("TransactionController - findAllTransaction");
        return transactionService.findAllTransaction();
    }

    
    @GetMapping(value = "/getByCustomerId/{customerId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Flux<Transaction> findByIdCustomer(@PathVariable(name = "customerId") String customerId){
    	logger.info("TransactionController - findByIdCustomer");
        return transactionService.findByIdCustomer(customerId);
    }
    
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public String createTransaction (@RequestBody TransferDto transferDto){
		logger.info("TransactionController - createTransaction - TRANSFER DATA: {}" , transferDto);
		return transactionService.createTransfer(transferDto);
    }
    
    @GetMapping(value = "/getCommision",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Flux<Transaction> getComissionByProductAndPeriod(@RequestBody ReporteComissionsDto dto){
    	logger.info("TransactionController - getComissionByProductAndPeriod -  DATA: {}" , dto);
        return transactionService.findByProductCodeAndPeriod(dto.getCodeProduct(), dto.getPeriod());
    }

}
