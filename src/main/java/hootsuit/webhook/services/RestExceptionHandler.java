package hootsuit.webhook.services;

import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);
	
	@ResponseStatus(value=HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoSuchElementException.class)
	public String handleNoSuchElementException(HttpServletRequest request, Exception ex){
		logger.info("Exception {} on Request {}", ex.getMessage(), request.getRequestURL());
		return ex.getMessage();
	}
	
	
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public String handleIllegalArgumentExceptionn(HttpServletRequest request, Exception ex){
		logger.info("Exception {} on Request {}", ex.getMessage(), request.getRequestURL());
		return ex.getMessage();
	}
	
}
