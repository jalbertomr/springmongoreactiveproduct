## Spring Boot Reactive Web MongoDB CRUD for ProductDto

A simple implemention of reactive web of a product entity with CRUD operations with many options of process responses from Flux to Https. The API interface (Controller input and output) uses ProductDto to transfer data validating some fields using JSR-303 at two levels, At Controller ProductDto level and Repository Level. The error message returned when invalid data is handled by GlobalErrorController class annotated with @RestControllerAdvice.

__Sample of options of processing at controller level__

Simple and insensible. At Controller level, on this processing option the delete simply delete with any result of the operation delete.

    @DeleteMapping("/deletenohttpresponse/{id}")
	 public Mono<Void> deleteProduct(@PathVariable("id") String id){
		return service.delete(id);
	 }

Sensible and simple code. On this processing option the call to the service does the http response and return the result of the operation, the responsablility of the controller layer of construct the http response is done by the service layer, not exactly responsability of the service layer
but the code is simple.
	
	 @DeleteMapping("/layerservicedeletehttp/{id}")
	 public Mono<ResponseEntity<ProductDto>> deleteProductResponse(@PathVariable("id") String id){
		return service.deleteWithHttpResponseProductDto(id);
	 }

Sensible and responsability of layers in order. In this case the controller is responsible of construct the http response, the service layer is responsible to add sensibility of the operation (existence of element to delete) and execute de operation calling the repository layer corresponding operation.
	
	 @DeleteMapping("/{id}")
	 public Mono<ResponseEntity<ProductDto>> deleteExist(@PathVariable String id){
		return service.deleteExist(id)
				.map(AppUtils::entityToDto)
				.map(productDto -> ResponseEntity.ok(productDto))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	 }
    

the product entity has a string ID key, name, qty, price, updateTime, uses a ProductDto as unit of data transfer on controller
level and Product at repository level. On service level the conversion is made using BeanUtils.copyProperties(source, target).

The controller functions returns Mono<ProductDto> or Flux<ProductDto>.


####The Repository

The interface IProductRepository extends from ReactiveMongoRepository<Product, String> which internally extends ReactiveSortingRepository<T, ID>, ReactiveQueryByExampleExecutor<T> classes

and uses repository.findByPriceBetween(Range.closed(min, max));

####The Service

On the save is important to observe the way the data is transformed and used using flux stream, the map transform the ProductDto to Product then inserted on the database and finally converted to productDto to be returned.

    public Mono<ProductDto> save(Mono<ProductDto> productDtoMono) {
	 return productDtoMono.map(AppUtils::dtoToEntity)
				.flatMap(repository::insert)
				.map(AppUtils::entityToDto);
	 }
The update is a little more complex, It has to look for any existence of the Id, to be updated only his id on the doOnNext() then be saved and finally converted to Dto and be returned.
	  
    public Mono<ProductDto> update(Mono<ProductDto> productDtoMono, String id) {
		return repository.findById(id)
				.flatMap(p -> productDtoMono.map(AppUtils::dtoToEntity)
						.doOnNext(e -> e.setId(id)))
				.flatMap(repository::save).map(AppUtils::entityToDto);
	 }
#### The Controller

There are two versions of the saveProduct. One receives Mono<ProductDto> and return Mono<ProductDto>, the other one receives Product
and return Mono<Product>, the @RequestBody do the job of work with Mono<T> or just <T>

    @PostMapping
	  public Mono<ProductDto> saveProduct(@RequestBody Mono<ProductDto> productDtoMono) {
		return service.save(productDtoMono);
	 }
	
	@PostMapping("/saveproduct")
	public Mono<Product> saveProductProduct(@RequestBody Product product){
		return service.saveProduct(product);
	}	 
	
Are shown many progresive versions of the update method with many variant till the final version, just for learning purpouses.
	
#### Validating the ProductDto (Controller level)

The validation of ProductDto is made with JSR-303 validation, the ProductDto class is annotated in their fields that requieres the constraints. The Controller is annotated in their parameter with @Valid, and the related errors of the validations are handled by GlobalErrorController class annotated with @RestControllerAdvice.

ProductDto

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class ProductDto implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5360764695721848031L;
	
		@Id
		private String id;
		
		@NotEmpty(message = "name in ProductDto must not be empty")
		@NotNull(message = "name in ProductDto must not be null")
		private String name;
		
		@Min(value = 1, message = "Quantity must be more than one")
		@Max(value = 100, message = "Quantity more than 100 not allowed")
		private int qty;
		
		private double price;
		
		private Instant updatetime;
	
	}
	
The Controller

	@PostMapping
	public Mono<ResponseEntity<ProductDto>> saveProduct(@Valid @RequestBody Mono<ProductDto> productDto, ServerHttpRequest req){
		return productDto.map(AppUtils::dtoToEntity)
		.flatMap(service::saveProduct)		
		.map(AppUtils::entityToDto)
		.map(prodDtoSaved -> ResponseEntity.created(URI.create(req.getPath() + "/" + prodDtoSaved.getId())).body(prodDtoSaved));
		//.switchIfEmpty(Mono.error((new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR))));  //Not necesary
	}

The GlobalErrorController

	@RestControllerAdvice
	public class GlobalErrorController {
	
		@ExceptionHandler(WebExchangeBindException.class)
		public ResponseEntity<?> handleException(WebExchangeBindException e){
			LinkedHashMap<Object,Object> body = new LinkedHashMap<>();
			body.put("timestamp", new Date());
			body.put("status", e.getStatus());
			List<String> errors = new ArrayList<String>();
		    errors = e.getAllErrors().stream().map(x -> x.getDefaultMessage())
		    		.collect(Collectors.toList());
			body.put("errors", errors);
			return new ResponseEntity<>(body, e.getStatus());
		}
	}

#### Validating the Product Entity (Service Level)

Validating the Product Entity at Service level uses Validator and the JSR-303 annotations in the entity model.

	public Mono<Product> saveProduct( Product product){
		Set<ConstraintViolation<Product>> violations = validator.validate(product);
		if (!violations.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for(ConstraintViolation<Product> constraintViolation : violations) {
				sb.append(constraintViolation.getMessage());
			}
			throw new ConstraintViolationException("saveProduct Error constraint: " + sb.toString(), violations);
		}
		product.setUpdatetime(Instant.now());
		return repository.save(product);
	}

The Product Model

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Document(collection = "product")
	@ToString
	public class Product {
		@Id
		private String id;
		
		@NotNull(message="name must be present")
		private String name;
		
		@Min(value = 1, message = "Quantity must be at least One")
		@Max(value = 100, message = "Quantity more than 100 must be authorized")
		private int qty;
		
		@Min(value = 0, message = "price must be positive")
		private double price;
		
		private Instant updatetime = Instant.now();
	}
	
To Catch the error of Validator is used the GlobalErrorController.class handling the ConstriantViolationException

    ...
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> handleException(ConstraintViolationException e){
		LinkedHashMap<Object,Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", HttpStatus.BAD_REQUEST);
		body.put("errors", e.getMessage());
		
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	...

	
#### Data Initializer	
	
To initialize de data on the database create a bean when the application startup, this bean create a flux of primary data that will be completed with random values and then saved on the database. this flux will not be executed until subscribe is called.

So first from repository.deleteAll() the data and then run the saved pipeline, and then run in the pipeline a findall() that return all the data to finally execute al the pipeline with subscribe with a consumer logger to show all the data created in the initialization.

    @Slf4j
    @Component
    @RequiredArgsConstructor
    public class SampleDataInitializer {
    private final IProductRepository repository;
	
	 @EventListener(ApplicationReadyEvent.class)
	 public void initialize() {
		var productFluxSaved = Flux.just("ProductA","ProductB","ProductC","ProductD","ProductE")
	    .map(prodName -> new Product(null, prodName,(int)(Math.random() * 20), Math.random() * 1000, null))
	    .flatMap(this.repository::save);
	    
	    repository.deleteAll()
	    .thenMany(productFluxSaved)
	    .thenMany( this.repository.findAll())
	    //.subscribeOn(Schedulers.fromExecutor(Executors.newSingleThreadExecutor()))
	    .subscribe(product -> log.info(product.toString()));
	  }
    }
    
	
The Schedulers have only one event loop per core per machine