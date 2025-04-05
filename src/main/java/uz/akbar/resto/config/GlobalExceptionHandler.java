package uz.akbar.resto.config;

import io.jsonwebtoken.ExpiredJwtException;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.exception.FileDeletionException;
import uz.akbar.resto.exception.RefreshTokenException;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** GlobalExceptionHandler */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
		System.err.println("*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ");
		e.getMessage();
		System.err.println("*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ");
		e.printStackTrace();
		return ResponseEntity.internalServerError().body(e.getMessage());
		// return ResponseEntity.internalServerError().body("internal server error: " +
		// e.getMessage());
	}

	@ExceptionHandler(AppBadRequestException.class)
	public ResponseEntity<?> handle(AppBadRequestException e) {
		// e.printStackTrace();
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(FileDeletionException.class)
	public ResponseEntity<?> handle(FileDeletionException e) {
		return ResponseEntity.internalServerError().body(e.getMessage());
	}

	@ExceptionHandler(FileUploadException.class)
	public ResponseEntity<?> handle(FileUploadException e) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
		// TODO: reconsider status here: internalServerError or badRequest
		body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		body.put("error", "File upload failed");
		body.put("message", e.getMessage());

		return ResponseEntity.internalServerError().body(body);
	}

	// org.springframework.security.authentication.DisabledException
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<?> handle(DisabledException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(RefreshTokenException.class)
	public ResponseEntity<?> handle(RefreshTokenException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<?> handle(ExpiredJwtException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has expired");
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<?> handle(AuthorizationDeniedException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", status.value());

		List<String> errors = new LinkedList<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getDefaultMessage());
		}
		body.put("errors", errors);

		return new ResponseEntity<>(body, headers, status);
	}
}
