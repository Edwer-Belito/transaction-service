package com.nttdata.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.common.Application;
import com.nttdata.common.Config;
import com.nttdata.common.Operation;
import com.nttdata.model.Product;
import com.nttdata.model.Transaction;
import com.nttdata.repository.TransactionRepository;
import com.nttdata.utility.AppConfig;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import  static org.mockito.Mockito.when;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
@WebFluxTest(TransactionServiceImpl.class)
public class TransactionServiceImplTest {
	
	@Autowired
	private TransactionService transactionService;
	
	@MockBean
	@SpyBean
	private TransactionRepository transactionRepository;
	
	@MockBean
	private AppConfig appConfig;
	
	@MockBean
	@Qualifier("wcLoadBalanced")
	private WebClient.Builder webClientBuilder;
	
	@MockBean
    private WebClient mockedWebClient;
	
	/*
	@BeforeTestMethod("createTransaction_DepositTest")
    public void setup() {
        when(webClientBuilder.exchangeStrategies(any(ExchangeStrategies.class))).thenReturn(webClientBuilder);
        when(webClientBuilder.clientConnector(any(ReactorClientHttpConnector.class))).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(mockedWebClient);
    }
	*/
	
	@Test
	void createTransaction_TransactionUnsupportedTest() {
		
		String msg = "Transaction unsupported";
		
		Transaction transaction = new Transaction();
		transaction.setMovementType("OPERATION");
		transaction.setTransactionType("TRASLADO");
		
		AppConfig appConfig = new AppConfig();
		appConfig.setApplication(new Application());
		appConfig.getApplication().setOperation(new Operation());
		appConfig.getApplication().getOperation().setDeposit("DEPOSITO");
		appConfig.getApplication().getOperation().setRetirement("RETIRO");
		appConfig.getApplication().getOperation().setPayment("PAGO");
		appConfig.getApplication().getOperation().setConsumption("CONSUMO");
		
		when(this.appConfig.getApplication()).thenReturn(appConfig.getApplication());
		
		String msg2 = transactionService.createTransaction(transaction);
		assertEquals(msg, msg2,"Test fail");
		
	}
	
	@Test
	void createTransaction_ProductInvalidTest() {
		
		String msg = "Product is not valid";
		
		Transaction transaction = new Transaction();
		transaction.setMovementType("TRANSFER");
		transaction.setProduct(new Product());
		transaction.getProduct().setCode("010");
		
		AppConfig appConfig = new AppConfig();
		appConfig.setApplication(new Application());
		appConfig.getApplication().setOperation(new Operation());
		appConfig.getApplication().getOperation().setDeposit("DEPOSITO");
		appConfig.getApplication().getOperation().setRetirement("RETIRO");
		appConfig.getApplication().getOperation().setPayment("PAGO");
		appConfig.getApplication().getOperation().setConsumption("CONSUMO");
		
		when(this.appConfig.getApplication()).thenReturn(appConfig.getApplication());
		
		String msg2 = transactionService.createTransaction(transaction);
		assertEquals(msg, msg2,"Test fail");
		
	}
	
	
	@Test
	void createTransaction_DepositTest() {
		
		String msg = "Transaction succesfull";
		
		Transaction transaction = new Transaction();
		transaction.setMovementType("OPERATION");
		transaction.setTransactionType("DEPOSITO");
		transaction.setProduct(new Product());
		transaction.getProduct().setCode("001");
		transaction.setAmount(BigDecimal.TEN);
		
		AppConfig appConfig = new AppConfig();
		appConfig.setApplication(new Application());
		appConfig.getApplication().setOperation(new Operation());
		appConfig.getApplication().getOperation().setDeposit("DEPOSITO");
		appConfig.getApplication().getOperation().setRetirement("RETIRO");
		appConfig.getApplication().getOperation().setPayment("PAGO");
		appConfig.getApplication().getOperation().setConsumption("CONSUMO");
		appConfig.getApplication().getOperation().setNumberOperationAllowed(3);
		appConfig.getApplication().setConfig(new Config());
		appConfig.getApplication().getConfig().setDescription("Decripcion");
		
		when(this.appConfig.getApplication()).thenReturn(appConfig.getApplication());
		when(transactionRepository.findByIdCustomer(any())).thenReturn(Flux.empty());
		when(transactionRepository.save(any())).thenReturn(Mono.empty());
		
		//TODO: falta mockear webclient
		//when(webClientBuilder.clientConnector(any())).thenReturn(any());
		
		
		String msg2 = transactionService.createTransaction(transaction);
		assertEquals(msg, msg2,"Test fail");
		
	}

}
