package com.bext.reactive.service;

import org.springframework.http.ResponseEntity;

import com.bext.reactive.dto.ProductDto;
import com.bext.reactive.entity.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {
	public Flux<ProductDto> getProducts();
	public Mono<ProductDto> getProduct(String id);
	public Mono<ResponseEntity<ProductDto>> getWithHttpResponse(String id);
	public Flux<ProductDto> getProductInRange(double min, double max);
	public Mono<ProductDto> save(Mono<ProductDto> productDtoMono);
	public Mono<Product> saveProduct(Product product);
	public Mono<ProductDto> update(Mono<ProductDto> productDtoMono, String id);
	public Mono<Void> delete(String id);
	public Mono<Product> deleteExist(String id);
	public Mono<ResponseEntity<Void>> deleteWithHttpResponse(String id);
	public Mono<ResponseEntity<ProductDto>> deleteWithHttpResponseProductDto(String id);
	
}
