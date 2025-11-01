package io.eddie.demo.domain.settlements.batch.writer;

import io.eddie.demo.domain.settlements.model.entity.Settlement;
import io.eddie.demo.domain.settlements.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettlementDataWriter implements ItemWriter<Settlement> {

    private final SettlementRepository repository;

    @Override
    public void write(Chunk<? extends Settlement> chunk) throws Exception {
        repository.saveAll(chunk.getItems());
    }
}
