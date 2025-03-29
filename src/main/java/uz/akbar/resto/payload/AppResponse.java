package uz.akbar.resto.payload;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** AppResponse */
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY) // excludes null and empty collections/arrays
public class AppResponse {

	Boolean success;

	String message;

	Object data;
}
