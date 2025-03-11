package uz.akbar.resto.payload;

import lombok.Builder;
import lombok.Getter;

/** AppResponse */
@Getter
@Builder
public class AppResponse {

	Boolean success;

	String message;

	Object data;
}
