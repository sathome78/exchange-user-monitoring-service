package me.exrates.riskmanagement.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.exrates.riskmanagement.model.AwsSecret;
import me.exrates.riskmanagement.service.AWSSecretLookup;
import me.exrates.riskmanagement.service.AwsSecretLookupImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@EnableScheduling
@Configuration
@ComponentScan({"me.exrates.riskmanagement"})
public class AppConfig {

    @Value("${spring.datasource.hikari.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.hikari.jdbc-url}")
    private String jdbcUrl;
    @Value("${spring.datasource.hikari.username}")
    private String user;
    @Value("${spring.datasource.hikari.password}")
    private String password;

    @Value("${sqs.access_key_id}")
    private String accessKeyId;
    @Value("${sqs.secret_key_id}")
    private String secretKeyId;

    @Value("${secret_manager.region}")
    private String secretRegion;
    @Value("${secret_manager.sqs_creds_name}")
    private String secretNameForQueue;


    @Bean(name = "masterHikariDataSource")
    public DataSource masterHikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(30);
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "slaveHikariDataSource")
    public DataSource slaveHikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(40);
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }

    @Primary
    @DependsOn("masterHikariDataSource")
    @Bean(name = "masterTemplate")
    public NamedParameterJdbcTemplate masterNamedParameterJdbcTemplate(@Qualifier("masterHikariDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @DependsOn("slaveHikariDataSource")
    @Bean(name = "slaveTemplate")
    public NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate(@Qualifier("slaveHikariDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Primary
    @DependsOn("masterHikariDataSource")
    @Bean(name = "jMasterTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("masterHikariDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Primary
    @Bean(name = "masterTxManager")
    public PlatformTransactionManager masterPlatformTransactionManager() {
        return new DataSourceTransactionManager(masterHikariDataSource());
    }

    @Bean(name = "slaveTxManager")
    public PlatformTransactionManager slavePlatformTransactionManager() {
        return new DataSourceTransactionManager(slaveHikariDataSource());
    }

    @Bean
    public AWSSecretLookup getAwsSecretLookup() {
        return new AwsSecretLookupImpl();
    }

    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
        AwsSecret secret = getAwsSecretLookup().getSecretKeys(secretRegion, secretNameForQueue);
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(secret.getKeyId(), secret.getKeySecret());
        return AmazonSQSAsyncClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(AmazonSQSAsync amazonSQSAsync) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setAmazonSqs(amazonSQSAsync);
        simpleMessageListenerContainer.setMessageHandler(queueMessageHandler());
        simpleMessageListenerContainer.setMaxNumberOfMessages(10);
        simpleMessageListenerContainer.setTaskExecutor(threadPoolTaskExecutor());
        simpleMessageListenerContainer.setWaitTimeOut(20);
        return simpleMessageListenerContainer;
    }


    @Bean
    public QueueMessageHandler queueMessageHandler() {
        QueueMessageHandlerFactory queueMessageHandlerFactory = new QueueMessageHandlerFactory();
        queueMessageHandlerFactory.setAmazonSqs(amazonSQSAsync());
        return queueMessageHandlerFactory.createQueueMessageHandler();
    }


    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("oaoQueueExecutor");
        executor.initialize();
        return executor;
    }


    /*@Bean(name = "sqs")
    public QueueMessagingTemplate getSqs() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretKeyId);
        AmazonSQSAsync sqs = AmazonSQSAsyncClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        return new QueueMessagingTemplate(sqs);
    }*/
}
