package com.redhat.camel.router;

import org.apache.camel.builder.RouteBuilder;

public class CustomerRouterSample extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:camelrouter?period=60s")
        .to("direct:customersallsample");

        from("direct:customersallsample")
        .routeId("customersallsample")
        .to("sql:select * from customers order by nome")
        .log("${body}");
    }
    
}
