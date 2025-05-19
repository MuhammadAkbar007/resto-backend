package uz.akbar.resto.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import uz.akbar.resto.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

}
