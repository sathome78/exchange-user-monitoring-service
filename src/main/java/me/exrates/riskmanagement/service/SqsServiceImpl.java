package me.exrates.riskmanagement.service;


import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.riskmanagement.model.Event;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class SqsServiceImpl implements SqsService {

    private final ObjectMapper objectMapper;
    private final EventsService eventsService;

    public SqsServiceImpl(ObjectMapper objectMapper, EventsService eventsService) {
        this.objectMapper = objectMapper;
        this.eventsService = eventsService;
    }


    @SqsListener(value = "${sqs.url}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receive(String message, @Headers Map<String, Object> header) throws IOException {
        try {
            System.out.println("received message " + message);
            Event event = objectMapper.readValue(message, Event.class);
            eventsService.saveNewEvent(event);
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }


/*----------------------for testing ----------------------------------------------*/
    public static void main(String[] args) {
        receiveMessage();
    }

    private static void receiveMessage() {
        /*BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAILEI4FYC7BVUZDGQ", "IMJfSp9cfUJAj7Dem/7K4VBhD21G90yESnyvhuBB");*/
        String myQueueUrl = "https://sqs.eu-central-1.amazonaws.com/415915243029/new_queu.fifo";
        final AmazonSQS sqs = AmazonSQSClientBuilder
                .standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .build();

        System.out.println("Listing all queues in your account.\n");
        for (final String queueUrl : sqs.listQueues().getQueueUrls()) {
            System.out.println("  QueueUrl: " + queueUrl);
        }
        System.out.println();

        System.out.println("Receiving messages from MyQueue.\n");
        final ReceiveMessageRequest receiveMessageRequest =
                new ReceiveMessageRequest(myQueueUrl);
        final List<Message> messages = sqs.receiveMessage(receiveMessageRequest)
                .getMessages();
        for (final Message message : messages) {
            System.out.println("Message");
            System.out.println("  MessageId:     "
                    + message.getMessageId());
            System.out.println("  ReceiptHandle: "
                    + message.getReceiptHandle());
            System.out.println("  MD5OfBody:     "
                    + message.getMD5OfBody());
            System.out.println("  Body:          "
                    + message.getBody());
            for (final Map.Entry<String, String> entry : message.getAttributes()
                    .entrySet()) {
                System.out.println("Attribute");
                System.out.println("  Name:  " + entry
                        .getKey());
                System.out.println("  Value: " + entry
                        .getValue());
            }
        }
        System.out.println();
    }
}
