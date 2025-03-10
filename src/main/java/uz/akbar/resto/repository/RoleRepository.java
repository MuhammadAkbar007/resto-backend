package uz.akbar.resto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import uz.akbar.resto.entity.Role;
import uz.akbar.resto.enums.RoleType;

/**
 * RoleRepository
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByRoleType(RoleType roleType);
}
