package com.bext.reactive.dto;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
	private String id;
	private String name;
	private int qty;
	private double price;
	private Instant updatetime;
}
