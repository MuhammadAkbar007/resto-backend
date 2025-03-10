package uz.akbar.resto.payload;

import lombok.Builder;

/** AppResponse */
@Builder
public class AppResponse {

	Boolean success;

	String message;

	Object data;
}
