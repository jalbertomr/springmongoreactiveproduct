## Spring Boot Reactive Web MongoDB CRUD for Product

A simple implemention of reactive web of a product entity with CRUD operations, based on javaTeachi channel extended with many options of process responses from Flux to Https.

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