package br.com.jannuzzitec.temporaryqueue.configuration;

import com.amazonaws.ApacheHttpClientConfig;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.apache.client.impl.ApacheHttpClientFactory;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.*;
import org.springframework.cloud.aws.core.SpringCloudClientConfiguration;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.rsocket.service.PayloadArgumentResolver;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableSqs
@Configuration
public class SqsConfiguration {

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClient.asyncBuilder().withClientConfiguration(clienteConfiguration())
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("localstack", "localstack")))
                .withRegion(Regions.SA_EAST_1).build();
    }

    public ClientConfiguration clienteConfiguration() {
        return SpringCloudClientConfiguration.getClientConfiguration();
    }

    @Bean
    public QueueMessageHandler queueMessageHandler(AmazonSQSAsync amazonSQSAsync) {
        QueueMessageHandlerFactory queueMsgHandlerFactory = new QueueMessageHandlerFactory();
        queueMsgHandlerFactory.setArgumentResolvers(Arrays.asList(new PayloadMethodArgumentResolver(new MappingJackson2MessageConverter())));
        queueMsgHandlerFactory.setAmazonSqs(amazonSQSAsync);
        return queueMsgHandlerFactory.createQueueMessageHandler();
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSQSAsync) {
        SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory = new SimpleMessageListenerContainerFactory();
        simpleMessageListenerContainerFactory.setAmazonSqs(amazonSQSAsync);
        simpleMessageListenerContainerFactory.setAutoStartup(true);
        simpleMessageListenerContainerFactory.setMaxNumberOfMessages(1);
        return simpleMessageListenerContainerFactory;
    }

    @Bean
    public AmazonSQSRequester sqsRequester() {
        return AmazonSQSRequesterClientBuilder.standard().withAmazonSQS(getSqsClient()).build();
    }

    @Bean
    public AmazonSQSResponder sqsResponder() {
        return AmazonSQSResponderClientBuilder.standard().withAmazonSQS(getSqsClient()).build();
    }

    public SqsClient getSqsClient() {
        return SqsClient.builder().region(Region.SA_EAST_1).endpointOverride(URI.create("http://localhost:4566")).build();
    }

}
