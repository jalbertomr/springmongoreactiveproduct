package com.bext.reactive.initialize;

import java.util.function.Consumer;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bext.reactive.entity.Product;
import com.bext.reactive.repository.IProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleDataInitializer {

	private final IProductRepository repository;
	
	@EventListener(ApplicationReadyEvent.class)
	public void initialize() {
		var productFluxSaved = Flux.just("ProductA","ProductB","ProductC","ProductD","ProductE")
	    .map(prodName -> new Product(null, prodName, 2, 111, null))
	    .flatMap(this.repository::save);
	    
	    repository.deleteAll()
	    .thenMany(productFluxSaved)
	    .thenMany( this.repository.findAll())
	    .subscribe(product -> log.info(product.toString()));
	}
}
