# King Spring Service
Basic middleware REST API made in Spring that fetches online product data and creates
endpoints from which we can:
- search all products (product attributes are: name, price, short description and image)
- search specific products (specified by the product's id)
- search all categories
- search products by keywords
- filter products by price and/or category

## Prerequisites
You have to install [Docker](https://www.docker.com/products/docker-desktop/) and [Docker Compose](https://docs.docker.com/compose/install/).  <br>
Everything else will be installed automatically by following the steps in the [Installation](#installation) section.

## Installation
### 1. Clone the repository
    
With GitHub account:   
```sh
git clone https://github.com/PjotrVr/king-spring-service
cd king-spring-service
```

Or with SSH:
```sh
git clone git@github.com:PjotrVr/king-spring-service.git
cd king-spring-service
```

### 2. Start application in a docker container (detach mode)
```sh
docker-compose up --build -d
```

### 3. Exit application
```sh
docker-compose down
```

## Usage
Since this is just a REST API and not an actual app, I'd recommend using tools like [Postman](https://www.postman.com/) or [Insomnia](https://insomnia.rest/) for easier usage.

If you don't have any of such tools, you can still make requests by using your browser and devtools extension.

Available endpoints:
- **GET /products**: fetches all products
- **GET /products/categories**: fetches all products and makes a list of distinct categories
- **GET /products/{id}**: fetches specific product by its id if it exists
- **GET /products/filter&{filter}**: fetches all products and then filters them by category and price range
- **GET /products/search&{query}**: fetches all products and returns sorted list based on query-to-product matching algorithm

For more detailed explanation of endpoints, read [API documentation](API_DOCS.md).

## Configuration
Everything that is configurable is inside .env file.

Parameters that you can change:
- **PORT**: port that server will run on, **DEFAULT**: 8080
- **DB_NAME**: database name, **DEFAULT**: springdb 
- **DB_USERNAME**: database username, **DEFAULT**: admin
- **DB_PASSWORD**: database password, **DEFAULT**: admin

By default, you can access database through h2 console.

Start the application.

Go to `/h2-console` and login with .env information. Don't forget to set path to database.

## TODO
Basic:
- [x] Implement `/products` endpoint
- [x] Implement `/products/categories` endpoint
- [x] Implement `/products/{id}` endpoint
- [x] Implement `/products/filter` endpoint
- [x] Implement `/products/search` endpoint

Nice to have:
- [ ] Add tests for all endpoints
- [ ] Add basic authentication and authorization
- [ ] Add logging 
- [x] Add caching for similar product retrieval
- [x] Add docker
