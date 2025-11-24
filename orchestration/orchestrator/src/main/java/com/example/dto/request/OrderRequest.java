package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 요청")
public record OrderRequest(
        @Schema(description = "사용자 ID", example = "1", required = true)
        Long userId,

        @Schema(description = "상품 ID", example = "1", required = true)
        Long productId,

        @Schema(description = "주문 수량", example = "2", required = true)
        Integer quantity,

        @Schema(description = "상품 단가", example = "10000", required = true)
        Long price
) {
}
