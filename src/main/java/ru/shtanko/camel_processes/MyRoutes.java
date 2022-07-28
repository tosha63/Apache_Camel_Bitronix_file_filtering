package ru.shtanko.camel_processes;

import org.apache.camel.builder.RouteBuilder;
import ru.shtanko.camel_processes.exception.BadExpansion;
import ru.shtanko.camel_processes.file_processor.ExtensionProcessor;

public class MyRoutes extends RouteBuilder {

    private ExtensionProcessor extensionProcessor = new ExtensionProcessor();

    @Override
    public void configure() {

        onException(BadExpansion.class)
                .to("activemq:invalid-queue");

        from("file:data?noop=true").transacted()
                .choice()
                    .when().simple("${file:name} endsWith 'xml'")
                        .process(extensionProcessor)
                        .to("activemq:test")
                    .when().simple("${file:name} endsWith 'txt'")
                        .process(extensionProcessor)
                .to("activemq:test")
                .endChoice()
                    .otherwise()
                        .process(extensionProcessor)
                        .to("activemq:invalid-queue")
                .end();
    }
}

