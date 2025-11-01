package com.quickcommerce.service;

import com.quickcommerce.dto.OrderDTO;
import com.quickcommerce.entity.Order;
import com.quickcommerce.repository.OrderRepository;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderDTO createOrder(OrderDTO orderDTO) {
        log.info("Creating order for customer: {}", orderDTO.getCustomerId());

        Order order = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customerId(orderDTO.getCustomerId())
                .totalAmount(orderDTO.getTotalAmount())
                .shippingAddress(orderDTO.getShippingAddress())
                .billingAddress(orderDTO.getBillingAddress())
                .build();

        Order saved = orderRepository.save(order);
        log.info("Order created with ID: {} and order number: {}", saved.getId(), saved.getOrderNumber());

        publishOrderEvent("OrderCreated", saved);
        return OrderDTO.from(saved);
    }

    public OrderDTO getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return OrderDTO.from(order);
    }

    public OrderDTO getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));
        return OrderDTO.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getCustomerOrders(String customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(OrderDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(OrderDTO::from)
                .collect(Collectors.toList());
    }

    public OrderDTO updateOrderStatus(Long id, Order.OrderStatus newStatus) {
        log.info("Updating order status - ID: {}, new status: {}", id, newStatus);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);

        String eventType = "Order" + newStatus.toString();
        publishOrderEvent(eventType, updated);
        return OrderDTO.from(updated);
    }

    public void cancelOrder(Long id) {
        log.info("Cancelling order: {}", id);
        updateOrderStatus(id, Order.OrderStatus.CANCELLED);
    }

    private void publishOrderEvent(String eventType, Order order) {
        String message = String.format(
                "{\"eventType\":\"%s\",\"orderId\":%d,\"orderNumber\":\"%s\",\"customerId\":\"%s\",\"totalAmount\":%s,\"status\":\"%s\"}",
                eventType, order.getId(), order.getOrderNumber(), order.getCustomerId(),
                order.getTotalAmount(), order.getStatus());
        try {
            kafkaTemplate.send("order-events", order.getId().toString(), message);
            log.info("Published {} event for order: {}", eventType, order.getId());
        } catch (Exception e) {
            log.error("Failed to publish event", e);
        }
    }
}