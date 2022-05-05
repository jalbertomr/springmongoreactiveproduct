package com.bext.reactive.utils;

import org.springframework.beans.BeanUtils;

import com.bext.reactive.dto.ProductDto;
import com.bext.reactive.entity.Product;

public class AppUtils {

	public static Product dtoToEntity(ProductDto productDto) {
		Product product = new Product();
		BeanUtils.copyProperties(productDto, product);
		return product;
	}
	
	public static ProductDto entityToDto(Product product) {
		ProductDto productDto = new ProductDto();
		BeanUtils.copyProperties(product, productDto);
		return productDto;
	}
}
