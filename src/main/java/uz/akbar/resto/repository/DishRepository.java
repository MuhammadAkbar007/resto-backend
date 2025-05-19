package uz.akbar.resto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import uz.akbar.resto.entity.Dish;

public interface DishRepository extends JpaRepository<Dish, Long>, JpaSpecificationExecutor<Dish> {

	boolean existsByName(String name);

	Optional<Dish> findByIdAndVisibleTrue(Long id);
}
