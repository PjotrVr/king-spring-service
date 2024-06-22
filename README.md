# King Spring Service
Basic middleware REST API made in Spring that fetches online product data and creates
endpoints from which we can:
- search all products (product attributes are: name, price, short description and image)
- search specific products (specified by the product's id)
- search all categories
- search products by keywords
- filter products by price and/or category

## Prerequisites
Only things you will need are Java 17 and Maven. <br>
Everything else will be installed automatically by following the steps in the [Installation](#installation) section.

## Installation
1. Clone the repository:
    ```sh
    git clone https://github.com/PjotrVr/king-spring-service
    cd king-spring-service
    ```

2. Build the project 
    ```sh
    mvn clean install
    ```

3. Run the application
    ```sh
    mvn spring-boot:run
    ```
## Usage
Since this is just a REST API and not an actual app, I'd recommend using tools like [Postman](https://www.postman.com/) or [Insomnia](https://insomnia.rest/) for easier usage.

If you don't have any of such tools, you can still make requests by using your browser and devtools extension.

By default I'm using port 8080.

Available endpoints:
- **GET /products**: fetches all products
- **GET /products/categories**: fetches all categories
- **GET /products/{id}**: fetches specific product by its id, if it doesn't exist then returns 404 NOT FOUND status
- **GET /products/filter**: fetches all products, but filters them by two criteria that are specified inside request body in JSON format
    - **categories**: list of categories that will be included 
    - **price**:
      - **lower**: lower bound for price, by default it is 0
      - **upper**: upper bound for a price, by default it is infinity

## TODO
Basic:
- [ ] Implement `/products` endpoint
- [ ] Implement `/products/categories` endpoint
- [ ] Implement `/products/{id}` endpoint
- [ ] Implement `/products/filter` endpoint
- [ ] Implement `/products/search` endpoint

Nice to have:
- [ ] Add tests for all endpoints
- [ ] Add basic authentication and authorization
- [ ] Add logging 
- [ ] Add caching for similar product retrieval
