package org.revo.livefeed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.ip.udp.MulticastReceivingChannelAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class LivefeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(LivefeedApplication.class, args);
    }

    @Bean
    public IntegrationFlow processUniCastUdpMessage() {
        return IntegrationFlows
                .from(new MulticastReceivingChannelAdapter("225.4.5.6", 4444))
                .handle(x -> log.info(new String(((byte[]) x.getPayload()))))
                .get();
    }
}
