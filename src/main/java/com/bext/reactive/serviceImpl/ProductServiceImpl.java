package com.bext.reactive.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;

import com.bext.reactive.dto.ProductDto;
import com.bext.reactive.entity.Product;
import com.bext.reactive.repository.IProductRepository;
import com.bext.reactive.service.IProductService;
import com.bext.reactive.utils.AppUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
		return Mono.from(repository.save(product));
	}
	
	@Override
	public Mono<ProductDto> update(Mono<ProductDto> productDtoMono, String id) {
		return repository.findById(id)
				.flatMap(p -> productDtoMono.map(AppUtils::dtoToEntity)
						.doOnNext(e -> e.setId(id)))
				.flatMap(repository::save).map(AppUtils::entityToDto);
	}

	@Override
	public Mono<Void> delete(String id) {
		return repository.deleteById(id);
	}

}
