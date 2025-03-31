package uz.akbar.resto.payload;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationData {
	private int page;
	private int size;
	private int numberOfElements;
	private int totalPages;
	private long totalElements;
	private boolean isFirst;
	private boolean isLast;
	private boolean hasNext;
	private boolean hasPrevious;

	public static PaginationData of(Page<?> pagination) {
		return PaginationData.builder()
				.page(pagination.getNumber())
				.size(pagination.getSize())
				.totalElements(pagination.getTotalElements())
				.totalPages(pagination.getTotalPages())
				.numberOfElements(pagination.getNumberOfElements())
				.isFirst(pagination.isFirst())
				.isLast(pagination.isLast())
				.hasNext(pagination.hasNext())
				.hasPrevious(pagination.hasPrevious())
				.build();
	}
}
