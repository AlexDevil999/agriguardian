package com.agriguardian.config;

import io.grpc.netty.shaded.io.netty.handler.codec.http2.DefaultHttp2HeadersDecoder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.TcpCodecs;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.IOException;
import java.util.Arrays;

@EnableIntegration
@IntegrationComponentScan
@Configuration
@Log4j2
public class TcpConfig {

    private int port=8088;
    @Value("${tcp.ip}")
    private String ip;

    @Bean
    public TcpInboundGateway tcpInGate(AbstractServerConnectionFactory connectionFactory)  {
        try {
            TcpInboundGateway inGate = new TcpInboundGateway();
            inGate.setConnectionFactory(connectionFactory);
            inGate.setRequestChannel(fromTcp());

            return inGate;
        }
        catch (Throwable e){
            log.error("tcp error catch " + e.getMessage());

            TcpInboundGateway inGate = new TcpInboundGateway();
            inGate.setConnectionFactory(connectionFactory);
            inGate.setRequestChannel(fromTcp());

            return inGate;
        }
    }

    @Bean
    public MessageChannel fromTcp() {
        return new DirectChannel();
    }

    @MessageEndpoint
    public static class Echo {

        @Transformer(inputChannel="fromTcp", outputChannel="toEcho")
        public String convert(byte[] bytes) {
            try {
                log.debug("tcp [convert] (bytes):" + Arrays.toString(bytes));
                log.debug("tcp [convert]:" + new String(bytes));
                return new String(bytes);
            }
            catch (Throwable e){
                log.error("[convert] tcp error catch " + e.getMessage());
                return new String(bytes);
            }
        }

        @ServiceActivator(inputChannel="toEcho")
        public String upCase(String in) {
            try {
                log.debug("tcp : " + in + " connected");
                return in.toUpperCase();
            }
            catch (Throwable e){
                log.error("[upCase] tcp error catch " + e.getMessage());
                return in.toUpperCase();
            }
        }

        @Transformer(inputChannel="resultToString")
        public String convertResult(byte[] bytes) {
            try {

                log.debug("tcp [convertResult]:" + new String(bytes));
                return new String(bytes);
            }
            catch (Throwable e){
                log.error("[convertResult] tcp error catch " + e.getMessage());
                return new String(bytes);
            }
        }

    }

//    @Bean
//    public AbstractClientConnectionFactory clientCF() {
//        return new TcpNetClientConnectionFactory("localhost", this.port);
//    }

    @Bean
    public AbstractServerConnectionFactory serverCF() {
        return new TcpNetServerConnectionFactory(this.port);
    }

}
