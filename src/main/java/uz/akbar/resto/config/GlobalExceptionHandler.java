package uz.akbar.resto.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.exception.FileDeletionException;
import uz.akbar.resto.exception.FileUploadException;
import uz.akbar.resto.exception.RefreshTokenException;
import uz.akbar.resto.exception.ResourceNotFoundException;
import uz.akbar.resto.utils.Utils;

/** GlobalExceptionHandler */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Creates a standardized error response
	 * 
	 * @param status  HTTP status code for the response
	 * @param message Detailed error message
	 * @return ResponseEntity with Spring's standardized ProblemDetail error body
	 */
	private ResponseEntity<ProblemDetail> createErrorResponse(HttpStatus status, String message) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(status);
		problemDetail.setProperty("timestamp", LocalDateTime.now().format(Utils.FORMATTER));
		problemDetail.setStatus(status.value());
		problemDetail.setTitle(status.getReasonPhrase());
		problemDetail.setDetail(message);

		return ResponseEntity.status(status).body(problemDetail);
	}

	@ExceptionHandler({ RuntimeException.class, FileDeletionException.class })
	public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException e) {
		System.err.println("*** INTERNAL SERVER ERROR ***");
		e.getMessage();
		e.printStackTrace();

		return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
	}

	@ExceptionHandler({ AppBadRequestException.class, ResourceNotFoundException.class })
	public ResponseEntity<ProblemDetail> handle(AppBadRequestException e) {
		// e.printStackTrace();
		return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	// org.springframework.security.authentication.DisabledException
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<ProblemDetail> handle(DisabledException e) {
		return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler({ ResponseStatusException.class, FileUploadException.class })
	public ResponseEntity<ProblemDetail> handleResponseStatusException(ResponseStatusException e) {
		HttpStatus status = (HttpStatus) e.getStatusCode();

		if (status.equals(HttpStatus.INTERNAL_SERVER_ERROR))
			e.printStackTrace();

		return createErrorResponse(status, e.getReason());
	}

	@ExceptionHandler(RefreshTokenException.class)
	public ResponseEntity<ProblemDetail> handle(RefreshTokenException e) {
		return createErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ProblemDetail> handle(ExpiredJwtException e) {
		return createErrorResponse(HttpStatus.UNAUTHORIZED, "Token has expired");
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ProblemDetail> handle(AuthorizationDeniedException e) {
		return createErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request) {

		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now().format(Utils.FORMATTER));
		body.put("status", status.value());

		if (status instanceof HttpStatus) {
			body.put("error", ((HttpStatus) status).getReasonPhrase());
		} else {
			body.put("error", "Validation error");
		}

		List<String> errors = new LinkedList<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getDefaultMessage());
		}
		body.put("errors", errors);

		return new ResponseEntity<>(body, headers, status);
	}
}
