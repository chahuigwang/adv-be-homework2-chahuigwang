package io.eddie.demo.domain.settlements.batch.processor;

import io.eddie.demo.domain.orders.model.entity.OrderItem;
import io.eddie.demo.domain.settlements.model.entity.Settlement;
import io.eddie.demo.domain.settlements.model.vo.SettlementStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
@RequiredArgsConstructor
public class SettlementConvertProcessor implements ItemProcessor<OrderItem, Settlement> {

    @Value("${custom.settlement.rate}")
    private Double settlementRate;

    @Override
    public Settlement process(OrderItem item) throws Exception {

        Long productPrice = item.getProductPrice();

        Settlement settlement = Settlement.builder()
                .buyerCode(item.getOrder().getAccountCode())
                .sellerCode(item.getSellerCode())
                .orderItemCode(item.getCode())
                .settlementRate(settlementRate)
                .totalAmount(productPrice)
                .build();

        settlement.applySettlementPolicy();

        return settlement;
    }
}
