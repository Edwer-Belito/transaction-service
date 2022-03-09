package com.nttdata.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.nttdata.dto.TransferDto;
import com.nttdata.model.Transaction;
import com.nttdata.service.TransactionService;

import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
@WebFluxTest(TransactionController.class)
class TransactionControllerTest {

	@MockBean
	TransactionService transactionService;

	@Autowired
	private WebTestClient webClient;

	@Test
	void createTransaction() throws Exception {
		Transaction t = new Transaction();
		t.setMovementType("OPERATION");
		t.setTransactionType("PAGO");

		Mockito.when(transactionService.createTransaction(t)).thenReturn("ok");

		webClient.post().uri("/transaction/create").body(BodyInserters.fromValue(t)).exchange().expectStatus()
				.isCreated();

		// verify
		Mockito.verify(transactionService, times(1)).createTransaction(t);

	}

	@Test
	void getAllTransaction() throws Exception {
		Transaction t = new Transaction();
		t.setMovementType("OPERATION");
		t.setTransactionType("PAGO");

		Mockito.when(transactionService.findAllTransaction()).thenReturn(Flux.just(t));

		webClient.get().uri("/transaction/getAll").exchange().expectStatus().isOk();

		// verify
		Mockito.verify(transactionService, times(1)).findAllTransaction();

	}

	@Test
	void findByCustomerId() throws Exception {
		Transaction t = new Transaction();
		t.setMovementType("OPERATION");
		t.setTransactionType("PAGO");
		t.setIdCustomer("id");

		Mockito.when(transactionService.findByIdCustomer(anyString())).thenReturn(Flux.just(t));

		webClient.get()
				.uri(uri -> uri.path("/transaction/getByCustomerId/{customerId}").build(t.getIdCustomer()))
				.exchange().expectStatus().isOk();

		// verify
		Mockito.verify(transactionService, times(1)).findByIdCustomer(anyString());

	}
	
	@Test
	void transferTransaction() throws Exception {
		Transaction t = new Transaction();
		t.setMovementType("OPERATION");
		t.setTransactionType("PAGO");

		Mockito.when(transactionService.createTransfer(any())).thenReturn("ok");

		webClient.post().uri("/transaction/transfer").body(BodyInserters.fromValue(t)).exchange().expectStatus()
				.isCreated();

		// verify
		Mockito.verify(transactionService, times(1)).createTransfer(any());

	}
	
	
	@Test
	void getCommision() throws Exception {
		
		TransferDto t = new TransferDto();
		t.setAmount(BigDecimal.TEN);
		
		Transaction tra = new Transaction();
		tra.setMovementType("OPERATION");
		tra.setTransactionType("PAGO");

		Mockito.when(transactionService.findByProductCodeAndPeriod(any(),any())).thenReturn(Flux.just(tra));

		webClient.post().uri("/transaction/getCommision")
					.body(BodyInserters.fromValue(t))
					.exchange()
					.expectStatus().isOk();

		// verify

		Mockito.verify(transactionService, times(1)).findByProductCodeAndPeriod(any(),any());

	}
	
	
	

}
