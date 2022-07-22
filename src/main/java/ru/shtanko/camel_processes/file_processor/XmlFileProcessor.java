package ru.shtanko.camel_processes.file_processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class XmlFileProcessor implements Processor {
    public static long countXmlFile = 0;
    @Override
    public void process(Exchange exchange) {
        System.out.println("exchange = " + exchange.getIn().getBody(String.class));
        ++XmlFileProcessor.countXmlFile;
    }
}
