package com.redhat.camel.router;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.support.builder.Namespaces;

public class CustomersRouter extends RouteBuilder{

    @Override
    public void configure() throws Exception {

		ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Namespaces ns = new Namespaces("ns2", "http://server.produtos.redhat.com/");

        restConfiguration().bindingMode(RestBindingMode.auto)
        	.component("platform-http")
			.dataFormatProperty("prettyPrint", "true")
			.contextPath("/").port(8080)
			.apiContextPath("/openapi")
			.apiProperty("api.title", "Camel Quarkus Demo API")
			.apiProperty("api.version", "1.0.0-SNAPSHOT")
            .apiProperty("cors", "true");

        rest()
        .tag("Service API Demo using quarkus and Camel")
        .produces("application/json")
        .get("/customers")
        .description("List all Customers")
        .route()
        .routeId("restclientall")
        .to("direct:customersall")
        .choice()
        .when(body().isNull()).setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404)).endChoice()
        .otherwise().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
        .endRest()

        .post("/customers/{cpf}/{nome}/{email}/{endereco}")
        .description("Save Customer")
        .route().routeId("restclientesave")
        .to("direct:customerinsert")
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
        .endRest()

        .put("/customers/{cpf}/{nome}/{email}/{endereco}")
        .description("Update Customer")
        .route().routeId("restclienteupdate")
        .to("direct:customerupdate")
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
        .endRest()

        .delete("/customers/{cpf}/{nome}/{email}/{endereco}")
        .description("Delete Customer")
        .route().routeId("restclientedelete")
        .to("direct:customerdelete")
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
        .endRest();

        from("direct:customersall")
        .routeId("customersall")
        .to("sql:classpath:sql/queryallcustomers.sql");

        from("direct:querybycpf")
        .routeId("querybycpf")
        .to("sql:classpath:sql/querybycpf.sql");

        from("direct:customerinsert")
        .routeId("customerinsert")
        .to("sql:classpath:sql/insert.sql");

        from("direct:customerupdate")
        .routeId("customerupdate")
        .to("sql:classpath:sql/updatecustomers.sql");

        from("direct:customerdelete")
        .routeId("customerdelete")
        .to("sql:classpath:sql/delete.sql");


        //Working with files
        from("file:{{customers.in.folder}}?antInclude=**/*.xml&move={{customers.out.folder}}")
        .routeId("customersfile")
        .log("Catched the file ${body}");

    }
    
}

