package com.bext.reactive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bext.reactive.dto.ProductDto;
import com.bext.reactive.entity.Product;
import com.bext.reactive.serviceImpl.ProductServiceImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product")
public class ProductController {
   
	@Autowired
	ProductServiceImpl service;
	
	@GetMapping
	public Flux<ProductDto> getProducts(){
		return service.getProducts();
	}
	
	@GetMapping("/{id}")
	public Mono<ProductDto> getProduct(@PathVariable("id") String id){
		return service.getProduct(id);
	}
	
	@GetMapping("/price-range/")
	public Flux<ProductDto> getProductInRange(@RequestParam("min") double min, @RequestParam("max") double max) {
	return service.getProductInRange(min, max);
    }
	
	@PostMapping
	public Mono<ProductDto> saveProduct(@RequestBody Mono<ProductDto> productDtoMono) {
		return service.save(productDtoMono);
	}
	
	@PostMapping("/saveproduct")
	public Mono<Product> saveProductProduct(@RequestBody Product product){
		return service.saveProduct(product);
	}
	
	@PostMapping("/update/{id}")
	public Mono<ProductDto> updateProduct(@RequestBody Mono<ProductDto> productDtoMono, @PathVariable("id") String id){
		return service.update(productDtoMono, id);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> deleteProduct(@PathVariable("id") String id){
		
		return service.delete(id);
	}
}
