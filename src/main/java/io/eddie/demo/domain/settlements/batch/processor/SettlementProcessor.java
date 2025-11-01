package io.eddie.demo.domain.settlements.batch.processor;

import io.eddie.demo.domain.deposits.model.entity.DepositHistory;
import io.eddie.demo.domain.deposits.service.DepositService;
import io.eddie.demo.domain.settlements.model.entity.Settlement;
import io.eddie.demo.domain.settlements.model.vo.SettlementStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class SettlementProcessor implements ItemProcessor<Settlement, Settlement> {

    private final DepositService depositService;

    @Override
    public Settlement process(Settlement settlement) throws Exception {

        try {

            if ( settlement.getSettlementStatus() != SettlementStatus.SETTLEMENT_CREATED ) {
                throw new IllegalStateException("정산 요청을 처리할 수 없습니다.");
            }

            depositService.charge(settlement.getSellerCode()
                    , DepositHistory.DepositHistoryType.SETTLEMENT
                    , settlement.getSettlementBalance());

            settlement.done();

        } catch (Exception e) {
            log.error("Failed to process settlement for ID: {}", settlement.getId(), e);
            settlement.fail();
        }

        return settlement;

    }

}
