package br.com.jannuzzitec.temporaryqueue;

import com.amazonaws.services.sqs.AmazonSQSRequester;
import com.amazonaws.services.sqs.AmazonSQSRequesterClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(value = "/sqs")
public class SQSRequesterController {

    @Autowired
    private AmazonSQSRequester amazonSQSRequester;
    @Value("${cloud.aws.requester-queue}")
    private String queueURL;

    @GetMapping
    public String requesterMessage(@RequestParam(name="message") String message) {
        try {
            SendMessageRequest sendRequest =
                SendMessageRequest.builder()
                .queueUrl(queueURL)
                .messageBody(message)
            .build();

            Message response = amazonSQSRequester.sendMessageAndGetResponse(sendRequest, 30, TimeUnit.SECONDS);
            String result = response.body();
            System.out.println("Mensagem de retorno recebida " + result);

            return result;
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
