package dev.vk.iam.config;
import java.time.Instant;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class ApiExceptionHandler {
 record ErrorBody(Instant timestamp,int status,String error,String message){}
 @ExceptionHandler(NoSuchElementException.class) ResponseEntity<ErrorBody> notFound(NoSuchElementException e){return ResponseEntity.status(404).body(new ErrorBody(Instant.now(),404,"Not Found",e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ErrorBody> invalid(MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(new ErrorBody(Instant.now(),400,"Bad Request","Request validation failed"));}
}
