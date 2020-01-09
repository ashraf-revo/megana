package org.revo.livesender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.dsl.context.IntegrationFlowContext.IntegrationFlowRegistration;
import org.springframework.integration.ip.dsl.Udp;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class LivesenderApplication {
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) {
        SpringApplication.run(LivesenderApplication.class, args);
    }


    @Bean
    public PublishSubscribeChannel publishSubscribeChannel() {
        return new PublishSubscribeChannel();
    }

    @Autowired
    @Qualifier("publishSubscribeChannel")
    private PublishSubscribeChannel publishSubscribeChannel;

    @Scheduled(fixedDelay = 1000)
    public void schedule() {
        publishSubscribeChannel.send(MessageBuilder.withPayload("send "+atomicInteger.incrementAndGet()+" at " + new Date()).build());
    }
//livefeed.default.pod.cluster.local
    @Bean
    public IntegrationFlowRegistration integrationFlow(IntegrationFlowContext flowContext) {
        System.out.println("Creating an adapter to send to port ");
        IntegrationFlow flow = IntegrationFlows.from(publishSubscribeChannel)
                .handle(Udp.outboundAdapter("224.0.0.1", 4444))
                .get();
        return flowContext.registration(flow).register();
    }

}
