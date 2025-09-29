package com.aurionpro.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<APIError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
		APIError err = new APIError();
		err.setStatus(HttpStatus.NOT_FOUND.value());
		err.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
		err.setMessage(ex.getMessage());
		err.setPath(req.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);

	}

//	You throw a custom NotFoundException when a resource (like Employee or Passport) isn’t found.
//	{
//	    "status": 404,
//	    "error": "Not Found",
//	    "message": "Employee with id 100 not found",
//	    "path": "/api/employees/100"
//	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<APIError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		APIError err = new APIError();
		err.setStatus(HttpStatus.BAD_REQUEST.value());
		err.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
		err.setMessage("Validation failed");
		err.setPath(req.getRequestURI());
		List<String> details = ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).toList();
		err.setValidationErrors(details);
		return ResponseEntity.badRequest().body(err);
	}

//	Validation fails on DTO fields like @NotBlank, @Email, or @Pattern.
//	{
//	    "status": 400,
//	    "error": "Bad Request",
//	    "message": "Validation failed",
//	    "path": "/api/employees",
//	    "validationErrors": [
//	        "firstName: size must be at least 2",
//	        "lastName: must not be blank",
//	        "email: must be a well-formed email address",
//	        "passport.passportNumber: must match \"^[A-Z]{2}[0-9]{7}$\"",
//	        "passport.expiryDate: must be a future date"
//	    ]
//	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<APIError> handleState(IllegalStateException ex, HttpServletRequest req) {
		APIError err = new APIError();
		err.setStatus(HttpStatus.CONFLICT.value());
		err.setError(HttpStatus.CONFLICT.getReasonPhrase());
		err.setMessage(ex.getMessage());
		err.setPath(req.getRequestURI());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
	}

//You manually throw an IllegalStateException when the state of the resource doesn’t allow the requested operation.
//{
//    "status": 409,
//    "error": "Conflict",
//    "message": "Passport already exists for employee 1",
//    "path": "/api/employees/1/passport"
//}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<APIError> handleGeneric(Exception ex, HttpServletRequest req) {
		APIError err = new APIError();
		err.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		err.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		err.setMessage(ex.getMessage());
		err.setPath(req.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
	}

//	This is a fallback handler for any unhandled exception — runtime exceptions, programming errors, etc.
//	
//	{
//	    "status": 500,
//	    "error": "Internal Server Error",
//	    "message": "Unexpected error occurred: something went wrong!",
//	    "path": "/api/employees"
//	}


	 @ExceptionHandler(CustomConcurrentUpdateException.class)
	    public ResponseEntity<String> handleConcurrentUpdateException(CustomConcurrentUpdateException ex) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	    }
	 

@ExceptionHandler(AuthorizationDeniedException.class)
public ResponseEntity<String> handleAccessDenied(AuthorizationDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
}
}


