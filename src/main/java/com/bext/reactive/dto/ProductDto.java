package com.bext.reactive.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5360764695721848031L;

	@Id
	private String id;
	
	@NotEmpty(message = "ProductDto-name in ProductDto must not be empty")
	@NotNull(message = "ProductDto-name in ProductDto must not be null")
	private String name;
	
	@Min(value = 1, message = "ProductDto-Quantity must be at least One")
	@Max(value = 100, message = "ProductDto-Quantity more than 100 must be authorized")
	private int qty;
	
	private double price;
	
	private Instant updatetime;

}
