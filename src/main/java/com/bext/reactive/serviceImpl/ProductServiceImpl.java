package com.bext.reactive.serviceImpl;

import java.net.URI;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.bext.reactive.dto.ProductDto;
import com.bext.reactive.entity.Product;
import com.bext.reactive.repository.IProductRepository;
import com.bext.reactive.service.IProductService;
import com.bext.reactive.utils.AppUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ProductServiceImpl implements IProductService {

	@Autowired
	private IProductRepository repository;

	@Override
	public Flux<ProductDto> getProducts() {
		return repository.findAll().map(AppUtils::entityToDto);
	}

	@Override
	public Mono<ProductDto> getProduct(String id) {
		return repository.findById(id).map(AppUtils::entityToDto);
	}

	@Override
	public Mono<ResponseEntity<ProductDto>> getWithHttpResponse(String id){
		return this.getProduct(id).map(ResponseEntity::ok)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}
	
	@Override
	public Flux<ProductDto> getProductInRange(double min, double max) {
		return repository.findByPriceBetween(Range.closed(min, max));
	}

	@Override
	public Mono<ProductDto> save(Mono<ProductDto> productDtoMono) {
		return productDtoMono.map(AppUtils::dtoToEntity)
				.flatMap(repository::insert)
				.map(AppUtils::entityToDto);
	}

	@Override
	public Mono<Product> saveProduct( Product product){
		product.setUpdatetime(Instant.now());
		return repository.save(product);
	}
	
	@Override
	public Mono<ResponseEntity<Product>> saveProductWithHttpResponse(Product product) {
		return repository.save(product)
				.map(prodSaved -> ResponseEntity.ok(prodSaved));
				//TODO Handle Exception
	}
	
	@Override
	public Mono<ResponseEntity<Product>> saveProductWithHttpResponseCreated(Product product, ServerHttpRequest req) {
		return repository.save(product)
				.map(prodSaved -> ResponseEntity.created(URI.create(req.getPath() + "/" + prodSaved.getId())).body(prodSaved));
				//TODO Exception 
	}
	
	@Override
	public Mono<ProductDto> update(Mono<ProductDto> productDtoMono, String id) {
		return repository.findById(id)
				.flatMap(p -> productDtoMono.map(AppUtils::dtoToEntity)
						.doOnNext(e -> {e.setId(id); e.setUpdatetime(Instant.now());}))
				.flatMap(repository::save).map(AppUtils::entityToDto);
	}
	
	public Mono<ResponseEntity<ProductDto>> updateWithHttpResponse(Mono<ProductDto> productDtoMono, String id, ServerHttpRequest req) {
		return repository.findById(id)
				.flatMap( prodFound -> productDtoMono.map(AppUtils::dtoToEntity)
						.doOnNext( productDtoToEntity -> {productDtoToEntity.setId( id);productDtoToEntity.setUpdatetime(Instant.now());}))
				.flatMap(repository::save).map(AppUtils::entityToDto)
				.map(prodSaved -> ResponseEntity.created(URI.create(req.getPath() + "/" + prodSaved.getId())).body(prodSaved))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@Override
	public Mono<Void> delete(String id) {
		return repository.deleteById(id);
	}
	
	@Override
	public Mono<ResponseEntity<Void>> deleteWithHttpResponse(String id){
		return this.getProduct(id)
		.flatMap( p -> repository.deleteById(id)
				                 .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))) )
		.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Deletes a given entity validate his existence.
	 *
	 * @param entity must not be {@literal null}.
	 * @return {@link Mono} Mono<T> if find and delete, Mono<Void> if not found, not delete.
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 */
	public Mono<Product> deleteExist(String id){
		return repository.findById(id)
				.map(productFound -> {repository.deleteById(id).subscribe(); return productFound;});
	}
	
	@Override
	public Mono<ResponseEntity<ProductDto>> deleteWithHttpResponseProductDto(String id){
				return repository.findById(id)
						         .map(AppUtils::entityToDto)
		                         .map( productFound -> {repository.deleteById(id).subscribe(); return productFound;})
		                         .map( product -> ResponseEntity.ok(product))
		                         .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
}
