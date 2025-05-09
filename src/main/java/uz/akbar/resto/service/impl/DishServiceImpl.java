package uz.akbar.resto.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.Attachment;
import uz.akbar.resto.entity.Dish;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.mapper.DishMapper;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.DishDto;
import uz.akbar.resto.repository.AttachmentRepository;
import uz.akbar.resto.repository.DishRepository;
import uz.akbar.resto.service.DishService;

@Service
@RequiredArgsConstructor
@Transactional
public class DishServiceImpl implements DishService {

	private final DishRepository repository;
	private final AttachmentRepository attachmentRepository;
	private final DishMapper mapper;

	@Override
	public AppResponse create(DishDto dto) {

		if (repository.existsByName(dto.getName()))
			throw new AppBadRequestException("Dish already exists with the name: " + dto.getName());

		Attachment photo = attachmentRepository.findById(dto.getPhotoId())
				.orElseThrow(
						() -> new AppBadRequestException("Attached photo is not found with id: " + dto.getPhotoId()));

		Dish dish = mapper.toEntity(dto);
		dish.setPhoto(photo);

		Dish saved = repository.save(dish);

		return AppResponse.builder()
				.success(true)
				.message("Dish created successfully")
				.data(mapper.toDto(saved))
				.build();
	}

}
