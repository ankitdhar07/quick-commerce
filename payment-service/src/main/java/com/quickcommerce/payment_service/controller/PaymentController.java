package com.quickcommerce.payment_service.controller;

import com.quickcommerce.payment_service.dto.PaymentDTO;
import com.quickcommerce.payment_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Processing", description = "APIs for payment processing")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Initiate a new payment")
    public ResponseEntity<PaymentDTO> initiatePayment(@RequestBody PaymentDTO paymentDTO) {
        log.info("POST /payments - Initiating payment");
        PaymentDTO created = paymentService.initiatePayment(paymentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable Long id) {
        log.info("GET /payments/{} - Fetching payment", id);
        PaymentDTO payment = paymentService.getPayment(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID")
    public ResponseEntity<PaymentDTO> getPaymentByTransactionId(@PathVariable String transactionId) {
        log.info("GET /payments/transaction/{} - Fetching payment", transactionId);
        PaymentDTO payment = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get all payments for an order")
    public ResponseEntity<List<PaymentDTO>> getOrderPayments(@PathVariable Long orderId) {
        log.info("GET /payments/order/{} - Fetching order payments", orderId);
        List<PaymentDTO> payments = paymentService.getOrderPayments(orderId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{id}/process")
    @Operation(summary = "Process a payment")
    public ResponseEntity<PaymentDTO> processPayment(@PathVariable Long id) {
        log.info("POST /payments/{}/process - Processing payment", id);
        PaymentDTO processed = paymentService.processPayment(id);
        return ResponseEntity.ok(processed);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete a payment")
    public ResponseEntity<PaymentDTO> completePayment(@PathVariable Long id) {
        log.info("POST /payments/{}/complete - Completing payment", id);
        PaymentDTO completed = paymentService.completePayment(id);
        return ResponseEntity.ok(completed);
    }

    @PostMapping("/{id}/fail")
    @Operation(summary = "Fail a payment")
    public ResponseEntity<PaymentDTO> failPayment(
            @PathVariable Long id,
            @RequestParam String errorMessage) {
        log.info("POST /payments/{}/fail - Failing payment", id);
        PaymentDTO failed = paymentService.failPayment(id, errorMessage);
        return ResponseEntity.ok(failed);
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Refund a payment")
    public ResponseEntity<PaymentDTO> refundPayment(@PathVariable Long id) {
        log.info("POST /payments/{}/refund - Refunding payment", id);
        PaymentDTO refunded = paymentService.refundPayment(id);
        return ResponseEntity.ok(refunded);
    }
}