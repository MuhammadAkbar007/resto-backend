package uz.akbar.resto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import uz.akbar.resto.entity.Dish;

public interface DishRepository extends JpaRepository<Dish, Long> {

	boolean existsByName(String name);
}
