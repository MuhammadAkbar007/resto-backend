package uz.akbar.resto.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** AppResponse */
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY) // excludes null and empty collections/arrays
public class AppResponse {

    Boolean success;

    String message;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp = LocalDateTime.now();

    Object data;
}
