## Spring Boot Reactive Web MongoDB CRUD for Product

A simple implemention of reactive web of a product entity with CRUD operations, based on javaTeachi channel.

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