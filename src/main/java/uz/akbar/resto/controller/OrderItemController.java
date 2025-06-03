package uz.akbar.resto.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.enums.DeleteType;
import uz.akbar.resto.enums.OrderType;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.UpdateOrderItemDto;
import uz.akbar.resto.security.CustomUserDetails;
import uz.akbar.resto.service.OrderItemService;
import uz.akbar.resto.utils.Utils;

@RequiredArgsConstructor
@RestController
@RequestMapping(Utils.BASE_URL + "/order-items")
public class OrderItemController {

	private final OrderItemService service;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AppResponse> getAll(
			@RequestParam(required = false) String searchTerm, // for general search
			@RequestParam(required = false) Integer quantity, // for filter
			@RequestParam(required = false) Double price, // for filter
			@RequestParam(required = false) OrderType orderType, // for filter
			@RequestParam(required = false) LocalDateTime fromDateTime, // for filter
			@RequestParam(required = false) LocalDateTime toDateTime, // for filter
			@RequestParam(required = false) String dishName, // for filter by Dish
			@RequestParam(required = false) Long orderNumber, // for filter by Order
			@RequestParam(defaultValue = "0") int page, // for pagination
			@RequestParam(defaultValue = "10") int size, // for pagination
			@RequestParam(defaultValue = "createdAt,desc") String[] sort) {

		AppResponse response = service.getAll(searchTerm, quantity, price, orderType, fromDateTime, toDateTime,
				dishName, orderNumber, page, size, sort);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AppResponse> getById(@PathVariable UUID id) {
		AppResponse response = service.getById(id);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AppResponse> update(@PathVariable UUID id, @RequestBody UpdateOrderItemDto dto,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		AppResponse response = service.update(id, dto, customUserDetails.getUser());
		return ResponseEntity.ok(response);
	}

	@PutMapping("/order-type/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AppResponse> updateOrderType(@PathVariable UUID id,
			@RequestParam(required = true) OrderType orderType,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		AppResponse response = service.updateOrderType(id, orderType, customUserDetails.getUser());
		return ResponseEntity.ok(response);
	}

	// TODO: delete
	@DeleteMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AppResponse> delete(@PathVariable UUID id,
			@RequestParam(required = true) DeleteType deleteType,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		service.delete(id, deleteType, customUserDetails.getUser());
		return ResponseEntity.noContent().build();
	}
}
