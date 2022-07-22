package ru.shtanko.camel_processes.file_processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class OtherwiseFileProcessor implements Processor {
    public static long countOtherwiseFile = 0;
    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println("exchange = " + exchange.getIn().getBody(String.class));
        ++OtherwiseFileProcessor.countOtherwiseFile;
    }
}
