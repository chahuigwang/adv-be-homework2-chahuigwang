package io.eddie.demo.domain.orders.model.entity;

import io.eddie.demo.common.model.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Setter
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    /*
    주문 시점의 제품 상세정보
     */
    @Column(nullable = false)
    private String sellerCode;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Long productPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Builder
    public OrderItem(Orders order, String sellerCode, String productCode, String productName, Long productPrice, Integer quantity) {
        this.order = order;
        this.sellerCode = sellerCode;
        this.productCode = productCode;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

}
