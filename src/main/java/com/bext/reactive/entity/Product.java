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
	
	@NotNull(message="name must be present")
	private String name;
	
	@Min(value = 1, message = "Quantity must be at least One")
	@Max(value = 100, message = "Quantity more than 100 must be authorized")
	private int qty;
	
	@NotNull(message = "price cannot be null")
	private double price;
	
	private Instant updatetime = Instant.now();
}
