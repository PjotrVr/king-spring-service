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
- **GET /products/categories**: fetches all categories
- **GET /products/{id}**: fetches specific product by its id, if it doesn't exist then returns 404 NOT FOUND status
- **GET /products/filter**: fetches all products and then filters them by category and price range

## Configuration
Everything that is configurable is inside .env file.

Parameters that you can change:
- **port**: port that server will run on, **DEFAULT: 8080**

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
- [ ] Add caching for similar product retrieval
- [x] Add docker
