package uz.akbar.resto.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(alphabetic = true, value = { "content" })
public class PaginationData<T> {
	private int pageNumber;
	private int pageSize;
	private int numberOfElements;
	private int totalPages;
	private long totalElements;
	private boolean hasNext;
	private boolean hasPrevious;
	private boolean isFirst;
	private boolean isLast;
	private List<T> content;

	public static <T> PaginationData<T> of(Page<T> page) {
		return PaginationData.<T>builder()
				.pageNumber(page.getNumber())
				.pageSize(page.getSize())
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.numberOfElements(page.getNumberOfElements())
				.isFirst(page.isFirst())
				.isLast(page.isLast())
				.hasNext(page.hasNext())
				.hasPrevious(page.hasPrevious())
				.content(page.getContent())
				.build();
	}

	public static <T> PaginationData<T> of(List<T> content, Page<?> page) {
		return PaginationData.<T>builder()
				.totalPages(page.getTotalPages())
				.totalElements(page.getTotalElements())
				.pageNumber(page.getNumber())
				.pageSize(page.getSize())
				.isFirst(page.isFirst())
				.isLast(page.isLast())
				.hasNext(page.hasNext())
				.hasPrevious(page.hasPrevious())
				.content(content)
				.build();
	}

}
