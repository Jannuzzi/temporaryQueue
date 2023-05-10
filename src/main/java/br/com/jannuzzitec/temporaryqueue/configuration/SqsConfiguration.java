package br.com.jannuzzitec.temporaryqueue.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
@EnableSqs
public class SqsConfiguration {
    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKeyId;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretAccessKey;

    @Value("${cloud.aws.end-point.uri}")
    private String sqsUrl;

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(sqsUrl, region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("localstack", "localstack")))
                .build();
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

    //@Bean
    //public QueueMessagingTemplate queueMessagingTemplate() {
     //   return new QueueMessagingTemplate(amazonSQSAsync());
    //}

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
