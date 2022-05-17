package com.bext.reactive.advice;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class GlobalErrorController {

	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<?> handleException(WebExchangeBindException e){
		LinkedHashMap<Object,Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", e.getStatus());
		
		List<String> errors = new ArrayList<String>();
	    errors = e.getAllErrors().stream().map(x -> x.getDefaultMessage())
	    		.collect(Collectors.toList());
		body.put("errors", errors);
		
		return new ResponseEntity<>(body, e.getStatus());
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> handleException(ConstraintViolationException e){
		LinkedHashMap<Object,Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", HttpStatus.BAD_REQUEST);
		body.put("errors", e.getMessage());
		
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
}
