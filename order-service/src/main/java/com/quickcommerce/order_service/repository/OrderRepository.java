package com.quickcommerce.order_service.repository;

import com.quickcommerce.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

        Optional<Order> findByOrderNumber(String orderNumber);

        List<Order> findByCustomerId(String customerId);

        List<Order> findByStatus(Order.OrderStatus status);

        @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND o.status = :status")
        List<Order> findByCustomerIdAndStatus(
                        @Param("customerId") String customerId,
                        @Param("status") Order.OrderStatus status);

        @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate")
        List<Order> findOrdersByDateRange(
                        @Param("startDate") java.time.LocalDateTime startDate,
                        @Param("endDate") java.time.LocalDateTime endDate);
}