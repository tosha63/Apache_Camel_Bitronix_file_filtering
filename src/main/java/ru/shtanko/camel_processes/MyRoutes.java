package ru.shtanko.camel_processes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import ru.shtanko.camel_processes.exception.BadExpansion;
import ru.shtanko.camel_processes.file_processor.OtherwiseFileProcessor;
import ru.shtanko.camel_processes.file_processor.TxtFileProcessor;
import ru.shtanko.camel_processes.file_processor.XmlFileProcessor;

public class MyRoutes extends RouteBuilder {
    private final XmlFileProcessor xmlFileProcessor = new XmlFileProcessor();
    private final TxtFileProcessor txtFileProcessor = new TxtFileProcessor();
    private final OtherwiseFileProcessor otherwiseFileProcessor = new OtherwiseFileProcessor();
    @Override
    public void configure() {
        from("file:data")
                .to("log:loggingToConsole")
                .choice()
                .when().simple("${file:name} endsWith 'xml'")
                .log("Xml файл обрабатывается процессором")
                .process(xmlFileProcessor)
                .log("Xml файл отправляется в очередь")
                .to("activemq:test")
                .log("Xml файл в очереди")
                .when().simple("${file:name} endsWith 'txt'")
                .log("Txt файл обрабатывается процессором")
                .process(txtFileProcessor)
                .log("Txt файл отправляется в очередь")
                .to("activemq:test")
                .log(LoggingLevel.INFO,"Txt файл в очереди")
                .otherwise()
                .log("Файл с каким-то расширением обрабатывается процессором")
                .process(otherwiseFileProcessor)
                .log("Выбрасывается исключение")
                .throwException(new BadExpansion("Неверное расширение"))
                .log("Обработка ошибки")
                .log("Файл с каким-то расширением отправляется в очередь invalide")
                .to("activemq:invalid-queue")
                .log("Файл с каким-то расширением в очереди invalide")
                .when(PredicateBuilder.constant((XmlFileProcessor.countXmlFile +
                        TxtFileProcessor.countTxtFile +
                        OtherwiseFileProcessor.countOtherwiseFile) % 100 == 0))
                .log("Количество файлов 100")
                .log("Количество файлов txt " + TxtFileProcessor.countTxtFile + "\n" +
                        "Количество файлов xml " + XmlFileProcessor.countXmlFile + "\n" +
                        "Количество остальных файлов " + OtherwiseFileProcessor.countOtherwiseFile + "\n" +
                        "Время обработки сообщений " )
                .end();


        /**
         * доделать время обработки файлов и менеджер распределенных транзакций
         */
    }
}
