package me.exrates.riskmanagement.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
/*@Component*/
public class SqsServiceImpl implements SqsService {

    @Value("${sqs.url}")
    private String sqsURL;

    private final AmazonSQS sqs;

    public SqsServiceImpl(@Qualifier("sqs") AmazonSQS sqs) {
        this.sqs = sqs;
    }

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init() {
            scheduler.schedule(this::receive, 30, TimeUnit.SECONDS);
    }

    private void receive() {
        final ReceiveMessageRequest receiveMessageRequest =
                new ReceiveMessageRequest(sqsURL)
                        .withMaxNumberOfMessages(1)
                        .withWaitTimeSeconds(3);
        // Uncomment the following to provide the ReceiveRequestDeduplicationId
        //receiveMessageRequest.setReceiveRequestAttemptId("1");
        final List<Message> messages = sqs
                .receiveMessage(receiveMessageRequest)
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
            deleteMessage(message);
        }
    }

    private void deleteMessage(Message message) {
        System.out.println("Deleting a message.\n");
        final String messageReceiptHandle = message.getReceiptHandle();
        sqs.deleteMessage(new DeleteMessageRequest(sqsURL,
                messageReceiptHandle));
    }

    public static void main(String[] args) {
    }

    private static String createNewQueue(AmazonSQS sqs, String queueName) {
        // Create a FIFO queue.
        final Map<String, String> attributes = new HashMap<>();

        // A FIFO queue must have the FifoQueue attribute set to true.
        attributes.put("FifoQueue", "true");
        /*
         * If the user doesn't provide a MessageDeduplicationId, generate a
         * MessageDeduplicationId based on the content.
         */
        attributes.put("ContentBasedDeduplication", "true");

        // The FIFO queue name must end with the .fifo suffix.
        final CreateQueueRequest createQueueRequest =
                new CreateQueueRequest(queueName)
                        .withAttributes(attributes);
        final String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

        // List all queues.
        System.out.println("Listing all queues in your account.\n");
        for (final String queueUrl : sqs.listQueues().getQueueUrls()) {
            System.out.println("  QueueUrl: " + queueUrl);
        }
        return myQueueUrl;
    }


}
