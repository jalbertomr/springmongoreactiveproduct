package com.bext.reactive.repository;

import org.springframework.data.domain.Range;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bext.reactive.dto.ProductDto;
import com.bext.reactive.entity.Product;

import reactor.core.publisher.Flux;

@Repository
public interface IProductRepository extends ReactiveMongoRepository<Product, String>{

	Flux<ProductDto> findByPriceBetween(Range<Double> closed);

}
