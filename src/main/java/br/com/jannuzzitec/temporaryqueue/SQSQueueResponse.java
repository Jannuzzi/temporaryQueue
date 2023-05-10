package br.com.jannuzzitec.temporaryqueue;

import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.MessageContent;
import com.amazonaws.services.sqs.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.HashMap;
import java.util.Map;

@Service
public class SQSQueueResponse {

    @Autowired
    private AmazonSQSResponder amazonSQSResponder;

    @SqsListener(value = "${cloud.aws.requester-queue}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void getMessageAndSendResponse(@Payload String message, @Headers Map<String, String> headers){
        System.out.println("Receive message: " + message);

        MessageAttributeValue messageAttributeValue = MessageAttributeValue
                .builder()
                .stringValue(headers.get(Constants.RESPONSE_QUEUE_URL_ATTRIBUTE_NAME))
                .build();

        Map<String, MessageAttributeValue> headersCoppied = new HashMap<>();
        headersCoppied.put(Constants.RESPONSE_QUEUE_URL_ATTRIBUTE_NAME, messageAttributeValue);

        Message messageResponse = Message.builder().messageAttributes(headersCoppied).body("aloha").build();
        amazonSQSResponder.sendResponseMessage(MessageContent.fromMessage(messageResponse), new MessageContent(messageResponse.body()));
    }

}
