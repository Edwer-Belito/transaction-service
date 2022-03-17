package com.nttdata.route;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.nttdata.handler.TransactionHandler;


@Configuration
public class TransactionRoute {

	@Bean
    public RouterFunction<ServerResponse> bookRouterFunc(TransactionHandler transactionHandler){
        return RouterFunctions.route(GET("/transaction/func/getAll").and(accept(MediaType.TEXT_EVENT_STREAM))
                ,transactionHandler::getAllTransaction)
                .andRoute(GET("/transaction/func/getByIdCustomer/"+"{idCustomer}").and(accept(MediaType.APPLICATION_JSON))
                        ,transactionHandler::getTransactionByCustomerId)
                .andRoute(POST("/transaction/func/create").and(accept(MediaType.APPLICATION_JSON))
                        ,transactionHandler::createTransaction);


    }
}
