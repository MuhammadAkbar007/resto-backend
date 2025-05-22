package uz.akbar.resto.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.enums.OrderType;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.service.OrderItemService;
import uz.akbar.resto.utils.Utils;

@RequiredArgsConstructor
@RestController
@RequestMapping(Utils.BASE_URL + "/order-items")
public class OrderItemController {

	private final OrderItemService service;

	public ResponseEntity<AppResponse> getAll(
			@RequestParam(required = false) String searchTerm, // for general search
			@RequestParam(required = false) Long number, // for filter
			@RequestParam(required = false) Integer quantity, // for filter
			@RequestParam(required = false) Double price, // for filter
			@RequestParam(required = false) OrderType orderType, // for filter
			@RequestParam(required = false) LocalDateTime fromDateTime, // for filter
			@RequestParam(required = false) LocalDateTime toDateTime, // for filter
			// TODO: add filter by dish & order
			@RequestParam(defaultValue = "0") int page, // for pagination
			@RequestParam(defaultValue = "10") int size, // for pagination
			@RequestParam(defaultValue = "createdAt,desc") String[] sort) {

		AppResponse response = service.getAll(searchTerm, number, quantity, price, orderType, fromDateTime, toDateTime,
				page, size, sort);

		return ResponseEntity.ok(response);
	}
}
