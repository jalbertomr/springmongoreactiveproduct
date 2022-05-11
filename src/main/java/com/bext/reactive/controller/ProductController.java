package com.bext.reactive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bext.reactive.dto.ProductDto;
import com.bext.reactive.entity.Product;
import com.bext.reactive.repository.IProductRepository;
import com.bext.reactive.serviceImpl.ProductServiceImpl;
import com.bext.reactive.utils.AppUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product")
public class ProductController {
   
	@Autowired
	ProductServiceImpl service;
	
	@Autowired
	IProductRepository repository;
	
	@GetMapping
	public Flux<ProductDto> getProducts(){
		return service.getProducts();
	}
	
//	@GetMapping("/{id}")
//	public Mono<ResponseEntity<ProductDto>> getProduct(@PathVariable("id") String id){
//		return service.getWithResponse(id);
//	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<ProductDto>> getProduct(@PathVariable("id") String id) {
		 return service.getProduct(id).map(ResponseEntity::ok)
		 .switchIfEmpty(Mono.error( new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}
	
	@GetMapping("/price-range")
	public Flux<ProductDto> getProductInRange(@RequestParam("min") double min, @RequestParam("max") double max) {
	return service.getProductInRange(min, max)
			.switchIfEmpty(Mono.error( new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }
	
//	@PostMapping
//	public Mono<ProductDto> saveProduct(@RequestBody Mono<ProductDto> productDtoMono) {
//		return service.save(productDtoMono);
//	}
	
	@PostMapping
	public Mono<ResponseEntity<Product>> saveProduct(@RequestBody Product product){
		return repository.save(product)
				.map(productSaved -> ResponseEntity.ok(productSaved))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping("/saveproduct")
	public Mono<Product> saveProductProduct(@RequestBody Product product){
		return service.saveProduct(product);
	}
	
	@PostMapping("/update/{id}")
	public Mono<ProductDto> updateProduct(@RequestBody Mono<ProductDto> productDtoMono, @PathVariable("id") String id){
		return service.update(productDtoMono, id);
	}
	
	@DeleteMapping("/deletenohttpresponse/{id}")
	public Mono<Void> deleteProduct(@PathVariable("id") String id){
		return service.delete(id);
	}
	
	@DeleteMapping("/layerservicedeletehttp/{id}")
	public Mono<ResponseEntity<ProductDto>> deleteProductResponse(@PathVariable("id") String id){
		return service.deleteWithHttpResponseProductDto(id);
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<ProductDto>> deleteExist(@PathVariable String id){
		return service.deleteExist(id)
				.map(AppUtils::entityToDto)
				.map(productDto -> ResponseEntity.ok(productDto))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
