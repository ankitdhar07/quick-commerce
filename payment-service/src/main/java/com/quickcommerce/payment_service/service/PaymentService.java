package com.quickcommerce.payment_service.service;

import com.quickcommerce.payment_service.dto.PaymentDTO;
import com.quickcommerce.payment_service.entity.Payment;
import com.quickcommerce.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PaymentDTO initiatePayment(PaymentDTO paymentDTO) {
        log.info("Initiating payment for order: {}", paymentDTO.getOrderId());

        Payment payment = Payment.builder()
                .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .orderId(paymentDTO.getOrderId())
                .amount(paymentDTO.getAmount())
                .paymentMethod(Payment.PaymentMethod.valueOf(paymentDTO.getPaymentMethod()))
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment initiated with transaction ID: {}", saved.getTransactionId());

        publishPaymentEvent("PaymentInitiated", saved);
        return PaymentDTO.from(saved);
    }

    public PaymentDTO getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
        return PaymentDTO.from(payment);
    }

    public PaymentDTO getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + transactionId));
        return PaymentDTO.from(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getOrderPayments(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(PaymentDTO::from)
                .collect(Collectors.toList());
    }

    public PaymentDTO processPayment(Long id) {
        log.info("Processing payment: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));

        // Simulate payment processing
        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        payment.setReferenceNumber("REF-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());

        Payment updated = paymentRepository.save(payment);
        publishPaymentEvent("PaymentProcessing", updated);
        return PaymentDTO.from(updated);
    }

    public PaymentDTO completePayment(Long id) {
        log.info("Completing payment: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));

        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        Payment updated = paymentRepository.save(payment);
        publishPaymentEvent("PaymentCompleted", updated);
        return PaymentDTO.from(updated);
    }

    public PaymentDTO failPayment(Long id, String errorMessage) {
        log.info("Failing payment: {} with error: {}", id, errorMessage);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));

        payment.setStatus(Payment.PaymentStatus.FAILED);
        payment.setErrorMessage(errorMessage);
        Payment updated = paymentRepository.save(payment);
        publishPaymentEvent("PaymentFailed", updated);
        return PaymentDTO.from(updated);
    }

    public PaymentDTO refundPayment(Long id) {
        log.info("Refunding payment: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        Payment updated = paymentRepository.save(payment);
        publishPaymentEvent("PaymentRefunded", updated);
        return PaymentDTO.from(updated);
    }

    private void publishPaymentEvent(String eventType, Payment payment) {
        String message = String.format(
                "{\"eventType\":\"%s\",\"paymentId\":%d,\"transactionId\":\"%s\",\"orderId\":%d,\"amount\":%s,\"status\":\"%s\"}",
                eventType, payment.getId(), payment.getTransactionId(), payment.getOrderId(),
                payment.getAmount(), payment.getStatus());
        try {
            kafkaTemplate.send("payment-events", payment.getId().toString(), message);
            log.info("Published {} event for payment: {}", eventType, payment.getId());
        } catch (Exception e) {
            log.error("Failed to publish event", e);
        }
    }
}