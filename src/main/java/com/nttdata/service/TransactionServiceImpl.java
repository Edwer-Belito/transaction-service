package com.nttdata.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.dto.TransferDto;
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

	private static Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AppConfig appConfig;

	/*
	 * Metodo para registrar la transaccion de deposito, pago o retiro, consumo,
	 * tambien Consume servicio de actualizacion de saldo del ms customer-service
	 */
	@Override
	public String createTransaction(Transaction transaction) {

		try {

			// VALID OPERATION
			if (transaction.getTransactionType().equals(appConfig.getApplication().getOperation().getDeposit())
					|| transaction.getTransactionType().equals(appConfig.getApplication().getOperation().getPayment()))
				transaction.getProduct().setSaldo(transaction.getAmount().abs());
			else if (transaction.getTransactionType().equals(appConfig.getApplication().getOperation().getRetirement())
					|| transaction.getTransactionType()
							.equals(appConfig.getApplication().getOperation().getConsumption()))
				transaction.getProduct().setSaldo(transaction.getAmount().negate());
			else
				return "Transaction unsupported";

			// VALID PRODUCT
			if (!validProductCode(transaction.getProduct().getCode())) {
				return "Product is not valid";
			}

			// VALID COUNT TRANSACTION BY CUSTOMER FOR COMMISION
			Mono<Long> countTransactionsCustomer = countTransactionByCustomerId(transaction.getIdCustomer());

			if (countTransactionsCustomer.toFuture().get() > appConfig.getApplication().getOperation()
					.getNumberOperationAllowed()) {
			//if(transactionRepository.findByIdCustomer(transaction.getIdCustomer()).count(). >4) {
				transaction.setCommission(appConfig.getApplication().getOperation().getCommision());
				transaction.getProduct().setSaldo(transaction.getProduct().getSaldo()
						.subtract(appConfig.getApplication().getOperation().getCommision()));

			}

			// INSERT TRANSACTION
			logger.info("REGISTRO DE LA TRANSACCION - INICIO");
			transaction.setEnviromentInsert(appConfig.getApplication().getConfig().getDescription());
			LocalDate localDate = LocalDate.now();
			transaction.setDateRegistry(localDate.toString());
			transaction.setPeriod(""+localDate.getYear()+localDate.getMonth());
			transactionRepository.save(transaction).log().subscribe();

			logger.info("REGISTRO DE LA TRANSACCION - FIN");

			// UPDATE SALDO CUSTOMER
			logger.info("CONSUMO DEL SERVICIO DE ACTUALIZAR SALDO - INICIO");

			WebClient webClient = WebClient.builder().baseUrl("http://localhost:8081")
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

			webClient.put().uri("/customer/updateSaldo/" + transaction.getIdCustomer())
					.body(Mono.just(transaction.getProduct()), Product.class).accept(MediaType.APPLICATION_JSON)
					.retrieve().bodyToMono(Customer.class).subscribe();

			logger.info("CONSUMO DEL SERVICIO DE ACTUALIZAR SALDO - FIN");

			return "Transaction succesfull";

		} catch (Exception e) {
			logger.error("ERROR EN createTransaction: " + e.getMessage(), e);
			Thread.currentThread().interrupt();
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
		logger.info("TransactionServiceImpl - findByIdCustomer: {}", idCustomer);
		return transactionRepository.findByIdCustomer(idCustomer);
	}

	@Override
	public Mono<Long> countTransactionByCustomerId(String idCustomer) {
		logger.info("TransactionServiceImpl - countTransactionByCustomerId: {}", idCustomer);
		return transactionRepository.findByIdCustomer(idCustomer).count();
	}

	private boolean validProductCode(String productCode) {
		return Arrays.stream(Constantes.Products.values()).anyMatch(e -> e.key.equals(productCode));

	}

	@Override
	public String createTransfer(TransferDto dtoTransferDto) {
		
		return null;
	}

}
