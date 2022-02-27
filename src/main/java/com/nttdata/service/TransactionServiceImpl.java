package com.nttdata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.model.Customer;
import com.nttdata.model.Product;
import com.nttdata.model.Transaction;
import com.nttdata.repository.TransactionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	/*
	 * Metodo para registrar la transaccion de deposito o retiro de la cuenta del
	 * cliente
	 */
	@Override
	public void createTransaction(Transaction transaction) {

		try {

			transactionRepository.save(transaction).log().subscribe();

			if (transaction.transactionType.equals("deposito"))
				transaction.product.saldo = transaction.product.saldo.abs();
			else
				transaction.product.saldo = transaction.product.saldo.negate();

			WebClient webClient = WebClient.builder().baseUrl("http://localhost:8081")
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

			webClient.put().uri("/customer/updateSaldo/" + transaction.idCustomer)
					.body(Mono.just(transaction.product), Product.class).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(Customer.class).subscribe();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Flux<Transaction> findAllTransaction() {
		return transactionRepository.findAll();
	}

}
