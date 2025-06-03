package uz.akbar.resto.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import uz.akbar.resto.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID>, JpaSpecificationExecutor<OrderItem> {

	Optional<OrderItem> findByIdAndVisibleTrue(UUID id);

	@Modifying
	@Query("DELETE FROM OrderItem o WHERE o.id = :id")
	void deleteByIdCustom(@Param("id") UUID id);
}
