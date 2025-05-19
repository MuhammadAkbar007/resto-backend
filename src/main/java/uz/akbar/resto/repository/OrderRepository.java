package uz.akbar.resto.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import uz.akbar.resto.entity.Order;

/**
 * OrderRepo
 */
public interface OrderRepository extends JpaRepository<Order, UUID> {

	@Query("select coalesce(max(o.number), 0) + 1 from orders o")
	Long getNextOrderNumber();

	boolean existsByNumber(Long number);
}
