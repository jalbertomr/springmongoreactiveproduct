package com.bext.reactive.entity;

import java.time.Instant;

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
	private String name;
	private int qty;
	private double price;
	private Instant updatetime = Instant.now();
}
