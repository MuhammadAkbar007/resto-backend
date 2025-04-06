package uz.akbar.resto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FileUploadException extends ResponseStatusException {

	public FileUploadException(HttpStatus status, String message) {
		super(status, message);
	}

	public FileUploadException(HttpStatus status, String message, Throwable cause) {
		super(status, message, cause);
	}
}
