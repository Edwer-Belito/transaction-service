package com.nttdata.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.nttdata.utility.RestUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImpl implements TransactionService {

	private static Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	@Qualifier("wcLoadBalanced")
	private WebClient.Builder webClientBuilder;

	/*
	 * Metodo para registrar la transaccion de deposito, pago o retiro, consumo,
	 * tambien Consume servicio de actualizacion de saldo del ms customer-service
	 */
	@Override
	public String createTransaction(Transaction transaction) {

		try {

			// VALID TRANSACTION TYPE
			if (!transaction.getMovementType().equals("TRANSFER")) {

				if (transaction.getTransactionType().equals(appConfig.getApplication().getOperation().getDeposit())
						|| transaction.getTransactionType()
								.equals(appConfig.getApplication().getOperation().getPayment()))
					transaction.getProduct().setSaldo(transaction.getAmount().abs());
				else if (transaction.getTransactionType()
						.equals(appConfig.getApplication().getOperation().getRetirement())
						|| transaction.getTransactionType()
								.equals(appConfig.getApplication().getOperation().getConsumption()))
					transaction.getProduct().setSaldo(transaction.getAmount().negate());
				else
					return "Transaction unsupported";

			}

			// VALID PRODUCT
			if (!validProductCode(transaction.getProduct().getCode())) {
				return "Product is not valid";
			}

			// VALID COUNT TRANSACTION BY CUSTOMER FOR COMMISION
			Mono<Long> countTransactionsCustomer = countTransactionByCustomerId(transaction.getIdCustomer());

			if (countTransactionsCustomer.toFuture().get() >= appConfig.getApplication().getOperation()
					.getNumberOperationAllowed()) {
				transaction.setCommission(appConfig.getApplication().getOperation().getCommision());
				transaction.getProduct().setSaldo(transaction.getProduct().getSaldo()
						.subtract(appConfig.getApplication().getOperation().getCommision()));

			}

			// INSERT TRANSACTION
			logger.info("REGISTRO DE LA TRANSACCION - INICIO");
			transaction.setEnviromentInsert(appConfig.getApplication().getConfig().getDescription());
			LocalDate localDate = LocalDate.now();
			transaction.setDateRegistry(localDate.toString());
			transaction.setPeriod("" + localDate.getYear() + localDate.getMonth().getValue());
			transactionRepository.save(transaction).log().subscribe();

			logger.info("REGISTRO DE LA TRANSACCION - FIN");

			// UPDATE SALDO CUSTOMER
			logger.info("CONSUMO DEL SERVICIO DE ACTUALIZAR SALDO - INICIO");

			//con eureka
			
			webClientBuilder.clientConnector(RestUtils.getDefaultClientConnector())
					.build().put()
					.uri("http://customer-service/customer/updateSaldo/" + transaction.getIdCustomer())
					.body(Mono.just(transaction.getProduct()), Product.class).accept(MediaType.APPLICATION_JSON)
					.retrieve().bodyToMono(Customer.class).subscribe();
			;
			/*
			 * sin eureka
			WebClient webClient = WebClient
					.builder().baseUrl("http://localhost:8081")
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

			webClient.put().uri("/customer/updateSaldo/" + transaction.getIdCustomer())
					.body(Mono.just(transaction.getProduct()), Product.class).accept(MediaType.APPLICATION_JSON)
					.retrieve().bodyToMono(Customer.class).subscribe();
			
			*/

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

		// VALID MOVEMENT
		if (!dtoTransferDto.getMovementType().equals("TRANSFER"))
			return "TYPE MOVEMENT INVALID";

		// VALID AMOUNT
		if (dtoTransferDto.getAmount().signum() == -1)
			return "AMOUNT IS NEGATIVE - INVALID";

		try {

			// ADD
			Transaction transactionOne = new Transaction();
			transactionOne.setMovementType(dtoTransferDto.getMovementType());
			transactionOne.setTransactionType(appConfig.getApplication().getOperation().getDeposit());
			transactionOne.setIdCustomer(dtoTransferDto.getCustomerDestination().getIdCustomer());
			transactionOne.setProduct(new Product(dtoTransferDto.getCustomerDestination().getProduct().getCode(),
					dtoTransferDto.getAmount()));
			transactionOne.setAmount(dtoTransferDto.getAmount());
			this.createTransaction(transactionOne);

			// SUBSTRACT
			Transaction transactionTwo = new Transaction();
			transactionTwo.setMovementType(dtoTransferDto.getMovementType());
			transactionTwo.setTransactionType(appConfig.getApplication().getOperation().getRetirement());
			transactionTwo.setIdCustomer(dtoTransferDto.getCustomerOrigin().getIdCustomer());
			transactionTwo.setProduct(new Product(dtoTransferDto.getCustomerOrigin().getProduct().getCode(),
					dtoTransferDto.getAmount().negate()));
			transactionTwo.setAmount(dtoTransferDto.getAmount());
			this.createTransaction(transactionTwo);

			return "Transfer succesfull";

		} catch (Exception e) {
			return "Transfer failure";
		}

	}

	@Override
	public Flux<Transaction> findByProductCodeAndPeriod(String codeProduct,String period) {
		return transactionRepository.findByProductCodeAndPeriodAndCommissionGreaterThan(codeProduct,period,BigDecimal.ZERO);
	}

}
