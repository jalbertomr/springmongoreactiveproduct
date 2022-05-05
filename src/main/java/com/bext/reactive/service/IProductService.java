package com.bext.reactive.service;

import com.bext.reactive.dto.ProductDto;
import com.bext.reactive.entity.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {
	public Flux<ProductDto> getProducts();
	public Mono<ProductDto> getProduct(String id);
	public Flux<ProductDto> getProductInRange(double min, double max);
	public Mono<ProductDto> save(Mono<ProductDto> productDtoMono);
	public Mono<Product> saveProduct(Product product);
	public Mono<ProductDto> update(Mono<ProductDto> productDtoMono, String id);
	public Mono<Void> delete(String id);
}