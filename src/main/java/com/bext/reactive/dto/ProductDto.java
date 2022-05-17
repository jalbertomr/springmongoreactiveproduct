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
//@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto implements Serializable{
	@Id
	private String id;
	
	@NotEmpty(message = "name in ProductDto must not be empty")
	@NotNull(message = "name in ProductDto must not be null")
	private String name;
	
	@Min(value = 1, message = "Quantity must be more than one")
	@Max(value = 100, message = "Quantity more than 100 not allowed")
	private int qty;
	
	private double price;
	
	private Instant updatetime;

	public ProductDto() {
		
	}
	
	
}
