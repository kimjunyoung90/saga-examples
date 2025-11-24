package com.example.controller;

import com.example.dto.request.OrderRequest;
import com.example.service.OrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Saga Orchestration", description = "분산 트랜잭션 조정 API")
@RestController
@RequestMapping("/saga")
@RequiredArgsConstructor
public class OrchestrationController {

    private final OrchestrationService orchestrationService;

    @Operation(
            summary = "주문 Saga 트랜잭션 실행",
            description = "주문 생성 → 결제 처리 → 재고 예약 순서로 분산 트랜잭션을 실행합니다. " +
                    "실패 시 자동으로 보상 트랜잭션을 수행하여 롤백합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "트랜잭션 처리 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(
                                            name = "성공",
                                            value = "\"SUCCESS\"",
                                            description = "모든 단계가 성공적으로 완료됨"
                                    ),
                                    @ExampleObject(
                                            name = "결제 실패",
                                            value = "\"FAILED_PAYMENT\"",
                                            description = "결제 단계 실패, 주문이 취소됨"
                                    ),
                                    @ExampleObject(
                                            name = "재고 실패",
                                            value = "\"FAILED_INVENTORY\"",
                                            description = "재고 예약 실패, 결제 및 주문이 취소됨"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류"
            )
    })
    @PostMapping("/order")
    public ResponseEntity<String> createOrder(
            @Parameter(
                    description = "주문 요청 정보",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = OrderRequest.class),
                            examples = @ExampleObject(
                                    name = "주문 예제",
                                    value = """
                                            {
                                              "userId": 1,
                                              "productId": 1,
                                              "quantity": 2,
                                              "price": 10000
                                            }
                                            """
                            )
                    )
            )
            @RequestBody OrderRequest request
    ) {
        String result = orchestrationService.orderSagaTransaction(request);
        return ResponseEntity.ok(result);
    }
}
