package com.bext.reactive.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bext.reactive.dto.ProductDto;
import com.bext.reactive.entity.Product;

public class AppUtils {
	
	@Autowired
	private static Validator validator;

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
