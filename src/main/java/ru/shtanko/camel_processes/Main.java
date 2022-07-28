package ru.shtanko.camel_processes;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.Configuration;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jms.PoolingConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        Configuration btmConfig = TransactionManagerServices.getConfiguration();
        btmConfig.setServerId("BTM-server");
        btmConfig.setLogPart1Filename("./tx-logs/part1.btm");
        btmConfig.setLogPart2Filename("./tx-logs/part2.btm");
        BitronixTransactionManager bitronixTM =  TransactionManagerServices.getTransactionManager();


        JtaTransactionManager jtaTM = new JtaTransactionManager();
        jtaTM.setUserTransaction(bitronixTM);
        jtaTM.setTransactionManager(bitronixTM);

        Properties propertiesQueue = new Properties();
        propertiesQueue.put("brokerURL","vm://localhost");
        propertiesQueue.put("userName","admin");
        propertiesQueue.put("password","admin");

        PoolingConnectionFactory poolingConnectionFactory = new PoolingConnectionFactory();

        poolingConnectionFactory.setClassName("org.apache.activemq.ActiveMQXAConnectionFactory");
        poolingConnectionFactory.setUniqueName("activemq");
        poolingConnectionFactory.setMaxPoolSize(8);
        poolingConnectionFactory.setDriverProperties(propertiesQueue);
        poolingConnectionFactory.setAllowLocalTransactions(true);

        poolingConnectionFactory.init();

        SimpleRegistry registry = new SimpleRegistry();
        registry.bind("transactionManager", jtaTM);
        registry.bind("test", poolingConnectionFactory);

        CamelContext context = new DefaultCamelContext(registry);
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
