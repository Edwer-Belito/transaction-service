package com.nttdata.service;

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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImpl implements TransactionService {

	private static Logger logger = LoggerFactory.getLogger(TransactionController.class);
	
	@Autowired
	private TransactionRepository transactionRepository;

	/*
	 * Metodo para registrar la transaccion de deposito o retiro
	 * Consume servicio de actualizacion de saldo
	 */
	@Override
	public void createTransaction(Transaction transaction) {

		try {

			logger.info("REGISTRO DE LA TRANSACCION - INICIO");
			transactionRepository.save(transaction).log().subscribe();

			logger.info("REGISTRO DE LA TRANSACCION - FIN");
			
			if (transaction.transactionType.equals("deposito"))
				transaction.product.saldo = transaction.product.saldo.abs();
			else
				transaction.product.saldo = transaction.product.saldo.negate();

			
			logger.info("CONSUMO DEL SERVICIO DE ACTUALIZAR SALDO - INICIO");
			
			WebClient webClient = WebClient.builder().baseUrl("http://localhost:8081")
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

			webClient.put().uri("/customer/updateSaldo/" + transaction.idCustomer)
					.body(Mono.just(transaction.product), Product.class).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(Customer.class).subscribe();

			logger.info("CONSUMO DEL SERVICIO DE ACTUALIZAR SALDO - FIN");
			
		} catch (Exception e) {
			logger.error("ERROR EN createTransaction: " + e.getMessage(),e);
		}

	}

	@Override
	public Flux<Transaction> findAllTransaction() {
		
		logger.info("TransactionServiceImpl - findAllTransaction");
		return transactionRepository.findAll();
	}

}
