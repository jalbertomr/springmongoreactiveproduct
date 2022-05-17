package com.bext.reactive.controller;

import java.net.URI;
import java.time.Instant;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

import lombok.extern.slf4j.Slf4j;
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
	
	@PostMapping("/saveproductwithok")
	public Mono<ResponseEntity<Product>> saveProductwithOk(@RequestBody Product product){
		return repository.save(product)
				.map(productSaved -> ResponseEntity.ok(productSaved))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping("/saveproducthttp")
	public Mono<ResponseEntity<Product>> saveProductCreated(@RequestBody Product product, ServerHttpRequest req){
		return repository.save(product)
				.map(productSaved -> ResponseEntity.created(URI.create(req.getPath() + "/" + productSaved.getId())).body(productSaved))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping("/saveproduct")
	public Mono<Product> saveProductProduct(@RequestBody Product product){
		return service.saveProduct(product);
	}
	
	@PostMapping("/saveproductservicewithhttpresponse")
	public Mono<ResponseEntity<Product>> saveProductService(@RequestBody Product product) {
		return service.saveProductWithHttpResponse(product);
	}
	
	@PostMapping("/saveproductservicewithhttpresponsecreated")
	public Mono<ResponseEntity<Product>> saveProductServiceCreated(@RequestBody Product product, ServerHttpRequest req){
		return service.saveProductWithHttpResponseCreated(product, req);
	}
	
	@PostMapping
	public Mono<ResponseEntity<ProductDto>> saveProduct(@Valid @RequestBody Mono<ProductDto> productDto, ServerHttpRequest req){
		return productDto.map(AppUtils::dtoToEntity)
		.flatMap(service::saveProduct)		
		.map(AppUtils::entityToDto)
		.map(prodDtoSaved -> ResponseEntity.created(URI.create(req.getPath() + "/" + prodDtoSaved.getId())).body(prodDtoSaved));
		//.switchIfEmpty(Mono.error((new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR))));  //Not necesary
	}
	
	@PutMapping("/updateNoHttp/{id}")
	public Mono<ProductDto> updateProduct(@RequestBody Mono<ProductDto> productDtoMono, @PathVariable("id") String id){
		return service.update(productDtoMono, id);
	}
	
	@PutMapping("/update/{id}")
	public Mono<ResponseEntity<Product>> updateProduct(@RequestBody Product product, @PathVariable("id") String id) {
		return repository.findById(id)
		.flatMap(productfound -> { productfound.setName(product.getName());
								   productfound.setPrice(product.getPrice());
								   productfound.setQty(product.getQty());
								   productfound.setUpdatetime(Instant.now());
		                           return repository.save(productfound);
		                         })
		.map(productSaved -> ResponseEntity.ok(productSaved))
		.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PutMapping("/updatev2product/{id}")
	public Mono<ResponseEntity<Product>> updateProductv2(@RequestBody Product product, @PathVariable("id") String id){
		return repository.findById(id)
		.doOnNext(productFound -> { productFound.setName(product.getName());
		                            productFound.setPrice(product.getPrice());
		                            productFound.setQty(product.getQty());
		                            productFound.setUpdatetime(Instant.now());
		                          })
		.flatMap(repository::save)
		.map(productSaved -> ResponseEntity.ok(productSaved))
		.defaultIfEmpty( new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PutMapping("/updatev3product/{id}")
    public Mono<ResponseEntity<ProductDto>> updateProductv3(@RequestBody Mono<ProductDto> productDtoMono, @PathVariable("id") String id) {
		return repository.findById(id)
				.flatMap( prodFound -> productDtoMono.map(AppUtils::dtoToEntity)
				                                     .doOnNext( prodDtoToEntity -> prodDtoToEntity.setId( id)))
		        .flatMap(repository::save).map(AppUtils::entityToDto)
		        .map(prodSaved -> ResponseEntity.ok(prodSaved))
		        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PutMapping("/updatev4product/{id}")
	public Mono<ResponseEntity<ProductDto>> updateProductv4(@RequestBody Mono<ProductDto> productDtoMono, @PathVariable("id") String id, ServerHttpRequest req){
		return repository.findById(id)
				.flatMap( prodFound -> productDtoMono.map(AppUtils::dtoToEntity)
				                                     .doOnNext( productDtoToEntity -> productDtoToEntity.setId( id)))
				.flatMap(repository::save).map(AppUtils::entityToDto)
				.map(prodSaved -> ResponseEntity.created(URI.create(req.getPath() + "/" + prodSaved.getId())).body(prodSaved))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PutMapping("/updatev5product/{id}")
	public Mono<ResponseEntity<ProductDto>> updateProductv5(@RequestBody Mono<ProductDto> productDtoMono, @PathVariable("id") String id, ServerHttpRequest req) {
		return service.updateWithHttpResponse(productDtoMono, id, req);
	}
	
	@PutMapping("/updatev6product/{id}")
	public Mono<ResponseEntity<ProductDto>> updateProductv6(@RequestBody Mono<ProductDto> productDtoMono, @PathVariable("id") String id, ServerHttpRequest req){
		return service.update(productDtoMono, id)
				.map(prodSaved -> ResponseEntity.created(URI.create(req.getPath() + "/" + prodSaved.getId())).body(prodSaved))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PutMapping("/updatev7product/{id}")
	public Mono<ResponseEntity<ProductDto>> updateProductv7(@RequestBody Mono<ProductDto> productDtoMono, @PathVariable("id") String id, ServerHttpRequest req){
		return service.update(productDtoMono, id)
				.map(prodSaved -> ResponseEntity.accepted().body(prodSaved))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<ProductDto>> update(@RequestBody Mono<ProductDto> productDtoMono, @PathVariable("id") String id){
		return service.update(productDtoMono, id)
				.map(prodSaved -> ResponseEntity.accepted().body(prodSaved))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@DeleteMapping("/deletenohttpresponse/{id}")
	public Mono<Void> deleteProduct(@PathVariable("id") String id){
		return service.delete(id);
	}
	
	@DeleteMapping("/deletehttplayerservice/{id}")
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
