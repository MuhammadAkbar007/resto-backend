package uz.akbar.resto.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import uz.akbar.resto.enums.OrderStatus;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.CreateOrderDto;
import uz.akbar.resto.service.OrderService;
import uz.akbar.resto.utils.Utils;

@RequiredArgsConstructor
@RestController
@RequestMapping(Utils.BASE_URL + "/order")
public class OrderController {

	private final OrderService service;

	@PostMapping
	@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
	public ResponseEntity<AppResponse> create(@Valid @RequestBody CreateOrderDto dto) {
		AppResponse response = service.create(dto);
		return ResponseEntity.status(201).body(response);
	}

	@GetMapping("/all")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AppResponse> getAll(
			@RequestParam(required = false) String searchTerm, // for general search
			@RequestParam(required = false) Long number, // for filter
			@RequestParam(required = false) Double discount, // for filter
			@RequestParam(required = false) Double totalPrice, // for filter
			@RequestParam(required = false) OrderStatus orderStatus, // for filter
			@RequestParam(required = false) LocalDateTime fromDateTime, // for filter
			@RequestParam(required = false) LocalDateTime toDateTime, // for filter
			@RequestParam(defaultValue = "0") int page, // for pagination
			@RequestParam(defaultValue = "10") int size, // for pagination
			@RequestParam(defaultValue = "createdAt,desc") String[] sort) {

		AppResponse response = service.getAll(searchTerm, number, discount, totalPrice, orderStatus,
				fromDateTime, toDateTime, page, size, sort);

		return ResponseEntity.ok(response);
	}
}
