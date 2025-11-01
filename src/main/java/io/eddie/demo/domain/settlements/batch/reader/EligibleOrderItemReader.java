package io.eddie.demo.domain.settlements.batch.reader;

import io.eddie.demo.domain.orders.model.entity.OrderItem;
import io.eddie.demo.domain.settlements.util.SettlementTimeUtils;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@StepScope
@Component
public class EligibleOrderItemReader extends JpaPagingItemReader<OrderItem> {

    public EligibleOrderItemReader(
            EntityManagerFactory entityManagerFactory,
            @Value("#{jobParameters['dateStr'] ?: T(java.time.YearMonth).now().toString()}") String dateStr,
            @Value("${custom.batch.chunk.size}") Integer batchSize
    ) {

        setEntityManagerFactory(entityManagerFactory);

        setQueryString("""
        select
            oi
        from
            OrderItem oi
        join
            oi.order o
        where
            oi.createdAt between :startDate and :endDate
        and
            o.orderState = 'PAID'
        """);

        setPageSize(batchSize);

        Map<String, Object> params = new HashMap<>();
        params.put("startDate", SettlementTimeUtils.getStartDay(dateStr));
        params.put("endDate", SettlementTimeUtils.getEndDay(dateStr));

        setParameterValues(params);
    }
}
