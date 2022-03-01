package com.nttdata.service;

import java.time.DayOfWeek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.controller.TransactionController;
import com.nttdata.model.Customer;
import com.nttdata.model.Product;
import com.nttdata.model.Transaction;
import com.nttdata.repository.TransactionRepository;
import com.nttdata.utility.AppConfig;
import com.nttdata.utility.Constantes;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImpl implements TransactionService {

	private static Logger logger = LoggerFactory.getLogger(TransactionController.class);
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private AppConfig appConfig;

	/*
	 * Metodo para registrar la transaccion de deposito o retiro
	 * Consume servicio de actualizacion de saldo
	 */
	@Override
	public String createTransaction(Transaction transaction) {

		try {
			
			//if (transaction.transactionType.equals(appConfig.application.users.operations.split(",")[0]))
			if (transaction.transactionType.equals(Constantes.DEPOSITO))
				transaction.product.saldo = transaction.amount.abs();
			//else if (transaction.transactionType.equals(appConfig.application.users.operations.split(",")[1]))
			else if (transaction.transactionType.equals(Constantes.RETIRO))
				transaction.product.saldo = transaction.amount.negate();
			else
				return "Transaction unsupported";

			logger.info("REGISTRO DE LA TRANSACCION - INICIO");
			//transaction.userInsert = appConfig.application.users.transactionId;
			//transaction.enviromentInsert = appConfig.application.config.description;
			transaction.enviromentInsert = "dev";
			transactionRepository.save(transaction).log().subscribe();

			logger.info("REGISTRO DE LA TRANSACCION - FIN");
			
			logger.info("CONSUMO DEL SERVICIO DE ACTUALIZAR SALDO - INICIO");
			
			WebClient webClient = WebClient.builder().baseUrl("http://localhost:8081")
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

			webClient.put().uri("/customer/updateSaldo/" + transaction.idCustomer)
					.body(Mono.just(transaction.product), Product.class).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(Customer.class).subscribe();

			logger.info("CONSUMO DEL SERVICIO DE ACTUALIZAR SALDO - FIN");
			
			return "Transaction succesfull";
			
		} catch (Exception e) {
			logger.error("ERROR EN createTransaction: " + e.getMessage(),e);
			return "Transaction failure";
		}

	}

	@Override
	public Flux<Transaction> findAllTransaction() {
		
		logger.info("TransactionServiceImpl - findAllTransaction");
		return transactionRepository.findAll();
	}

	@Override
	public Flux<Transaction> findByIdCustomer(String idCustomer) {
		logger.info("TransactionServiceImpl - findByIdCustomer: "+idCustomer);
		return transactionRepository.findByIdCustomer(idCustomer);
	}

}
