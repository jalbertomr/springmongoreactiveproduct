package com.bext.reactive.entity;

import java.time.Instant;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product")
@ToString
public class Product {
	@Id
	private String id;
	
	@NotNull(message="Product-name must be present")
	private String name;
	
	@Min(value = 1, message = "Product-Quantity must be at least One")
	@Max(value = 100, message = "Product-Quantity more than 100 must be authorized")
	private int qty;
	
	@Min(value = 0, message = "Product-price must be positive")
	private double price;
	
	private Instant updatetime = Instant.now();
}
