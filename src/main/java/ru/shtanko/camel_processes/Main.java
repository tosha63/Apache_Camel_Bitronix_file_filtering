package ru.shtanko.camel_processes;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class Main {
    public static void main(String[] args) {
        CamelContext context = new DefaultCamelContext();
        try {
            context.addRoutes(new MyRoutes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.start();
        try {
            Thread.sleep(6500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        context.stop();
    }
}
