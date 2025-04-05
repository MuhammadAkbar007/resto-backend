package uz.akbar.resto.exception;

public class FileDeletionException extends RuntimeException {

	public FileDeletionException(String message) {
		super(message);
	}

	public FileDeletionException(String message, Throwable cause) {
		super(message, cause);
	}
}
