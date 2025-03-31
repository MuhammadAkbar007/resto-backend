package uz.akbar.resto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uz.akbar.resto.entity.template.AbsLongEntity;
import uz.akbar.resto.enums.RoleType;

import java.util.HashSet;
import java.util.Set;

/** Role */
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "roles")
public class Role extends AbsLongEntity {

	@Column(nullable = false, unique = true)
	@Enumerated(EnumType.STRING)
	private RoleType roleType;

	@Column(nullable = false, columnDefinition = "text")
	private String description;

	@Builder.Default
	@ManyToMany(mappedBy = "roles")
	private Set<User> users = new HashSet<>();
}
