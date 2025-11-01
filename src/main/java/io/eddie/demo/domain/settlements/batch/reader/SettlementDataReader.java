package io.eddie.demo.domain.settlements.batch.reader;

import io.eddie.demo.domain.settlements.model.entity.Settlement;
import io.eddie.demo.domain.settlements.util.SettlementTimeUtils;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@StepScope
@Component
public class SettlementDataReader extends JpaCursorItemReader<Settlement> {

    public SettlementDataReader(
            EntityManagerFactory entityManagerFactory
            , @Value("#{jobParameters['dateStr']") String dateStr

    ) {

        setEntityManagerFactory(entityManagerFactory);

        setQueryString("""
            select
                s
            from
                Settlement s
            where
                s.createdAt between :startDate and :endDate
            and
                s.settlementStatus = 'SETTLEMENT_CREATED'
            and
                s.settlementDate is null
        """);

        Map<String, Object> params = new HashMap<>();
        params.put("startDate", SettlementTimeUtils.getStartDay(dateStr));
        params.put("endDate", SettlementTimeUtils.getEndDay(dateStr));
        setParameterValues(params);

    }

}
