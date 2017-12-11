package io.pivotal.practice.springintegration.guides.gettingstarted;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan
@Slf4j
public class GettingStartedApplication {

	public static void main(String[] args) {
        ConfigurableApplicationContext appCtx = new SpringApplicationBuilder(GettingStartedApplication.class)
                .web(false)
                .run(args);

        log.info("Starting");
        MessageChannel baristaInbox = (MessageChannel) appCtx.getBean("baristaInbox");
        PollableChannel countertop = (PollableChannel) appCtx.getBean("countertop");

        baristaInbox.send(new GenericMessage<>("medium mocha"));
        Message<?> completedOrder = countertop.receive(0);
        Object beverage = completedOrder.getPayload();

        log.info("Resulting beverage: {}", beverage);
	}

	@Bean
    MessageChannel baristaInbox() {
        return new DirectChannel();
    }

    @Bean
    MessageChannel countertop() {
        return new QueueChannel(10);
    }

    @ServiceActivator(inputChannel = "baristaInbox", outputChannel = "countertop")
    public String barista(String order) {
        log.info("Got the order.  It says \"{}\".", order);
        return "coffee!";
    }
}
