package io.eddie.demo.domain.settlements.batch.config;

import io.eddie.demo.domain.orders.model.entity.OrderItem;
import io.eddie.demo.domain.settlements.batch.processor.SettlementConvertProcessor;
import io.eddie.demo.domain.settlements.batch.processor.SettlementProcessor;
import io.eddie.demo.domain.settlements.batch.reader.EligibleOrderItemReader;
import io.eddie.demo.domain.settlements.batch.reader.SettlementDataReader;
import io.eddie.demo.domain.settlements.batch.writer.SettlementDataWriter;
import io.eddie.demo.domain.settlements.batch.writer.SettlementStatusWriter;
import io.eddie.demo.domain.settlements.model.entity.Settlement;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.UUID;

@Configuration
public class SettlementBatchConfiguration {

    @Bean
    @Qualifier("settlementJob")
    public Job settlementJob(
            JobRepository jobRepository,
            @Qualifier("prepareSettlementDataStep") Step prepareSettlementDataStep,
            @Qualifier("processSettlementStep") Step processSettlementStep
    ) {
        return new JobBuilder("settlementJob_" + UUID.randomUUID(), jobRepository)
                .start(prepareSettlementDataStep)
                .next(processSettlementStep)
                .build();
    }

    @Value("${custom.batch.chunk.size}")
    private Integer batchSize;

    @Bean
    @Qualifier("prepareSettlementDataStep")
    public Step prepareSettlementDataStep(
            JobRepository jobRepository
            , PlatformTransactionManager txManager
            , EligibleOrderItemReader eligibleOrderItemReader
            , SettlementConvertProcessor settlementConvertProcessor
            , SettlementDataWriter settlementDataWriter
    ) {
        return new StepBuilder("prepareSettlementDataStep", jobRepository)
                .<OrderItem, Settlement>chunk(batchSize, txManager)
                .reader(eligibleOrderItemReader)
                .processor(settlementConvertProcessor)
                .writer(settlementDataWriter)
                .build();
    }

    @Bean
    @Qualifier("processSettlementStep")
    public Step processSettlementStep(
            JobRepository jobRepository
            , PlatformTransactionManager txManager
            , SettlementDataReader settlementDataReader
            , SettlementProcessor settlementProcessor
            , SettlementStatusWriter settlementStatusWriter
    ) {
        return new StepBuilder("processSettlementStep", jobRepository)
                .<Settlement, Settlement>chunk(batchSize, txManager)
                .reader(settlementDataReader)
                .processor(settlementProcessor)
                .writer(settlementStatusWriter)
                // 예외 발생시 건너뛰고 계속 진행 (최대 1000개까지 허용)
                .faultTolerant()
                .skipLimit(1000)
                .skip(Exception.class)
                // 재시도 설정 (최대 3번 재시도)
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

}
