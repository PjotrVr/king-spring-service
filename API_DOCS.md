# API Documentation
## Endpoints

### 1. List all products

**URL:** `/products`  
**Method:** `GET`  
**Description:** Retrieves a list of all products.

#### Request and response example
Request: `/products`
```json
[
  {
    "id": 1,
    "name": "Essence Mascara Lash Princess",
    "category": "beatuy",
    "description": "The Essence Mascara Lash Princess is a popular mascara known for its volumizing and lengthening effects. Achieve dramatic lashes with this long-lasting and cruelty-free formula."",
    "price": 9.99,
    "imageUrl": "https://cdn.dummyjson.com/products/images/beauty/Essence%20Mascara%20Lash%20Princess/thumbnail.png"
  },
  ...
]
```

### 2. Get product by id

**URL:** `/products/{id}`  
**Method:** `GET`  
**Description:** Retrieves details of a single product specified by its id.

#### URL Parameters

-   `id` (integer): The id of the product.

#### Request and response example
Request: `/products/3`
```json
{
    "id": 3,
    "name": "Powder Canister",
    "category": "beauty",
    "description": "The Powder Canister is a finely milled setting powder designed to set makeup and control shine. With a lightweight and translucent formula, it provides a smooth and matte finish.",
    "price": 14.99,
    "imageUrl": "https://cdn.dummyjson.com/products/images/beauty/Powder%20Canister/thumbnail.png"
}
```

### 3. List all categories
**URL:** `/products/categories`  
**Method:** `GET`  
**Description:** Retrieves a list of all distinct product categories.

#### Request and response example
Request: `/products/categories`
```json
[
  "Beauty",
  "Fragrances",
  ...
]
```

### 4. Filter products

**URL:** `/products/filter`  
**Method:** `GET`  
**Description:** Retrieves products filtered by category and/or price range.

#### Query parameters

-   `category` (string, optional): The category to filter by.
-   `lower` (number, optional): The lower bound of the price range. Default is 0.
-   `upper` (number, optional): The upper bound of the price range. Default is the maximum value of Double.

#### Request and response example
Request: `/products/filter?category=beauty&lower=2&upper=100`

```json
[
  {
    "id": 1,
    "name": "Essence Mascara Lash Princess",
    "category": "Beauty",
    "description": 	"The Essence Mascara Lash Princess is a popular mascara known for its volumizing and lengthening effects. Achieve dramatic lashes with this long-lasting and cruelty-free formula.",
    "price": 9.99,
    "imageUrl": "https://cdn.dummyjson.com/products/images/beauty/Essence%20Mascara%20Lash%20Princess/thumbnail.png"
  },
  ...
]
```

### 5. Search products

**URL:** `/products/search`  
**Method:** `GET`  
**Description:** Searches products by matching given query and returning sorted list  of (from most matched to least matched) products.

#### Query parameters

-   `query` (string): The search query.

#### Request and response example
Request: `/products/search?query=red+finish`
```json
[
  {
    "id": 5,
    "name": "Red Nail Polish",
    "category": "beauty",
    "description": "The Red Nail Polish offers a rich and glossy red hue for vibrant and polished nails. With a quick-drying formula, it provides a salon-quality finish at home.",
    "price": 8.99,
    "imageUrl": "https://cdn.dummyjson.com/products/images/beauty/Red%20Nail%20Polish/thumbnail.png"
  },
  {
    "id": 4,
    "name": "Red Lipstick",
    "category": "beauty",
    "description": "The Red Lipstick is a classic and bold choice for adding a pop of color to your lips. With a creamy and pigmented formula, it provides a vibrant and long-lasting finish.",
    "price": 12.99,
    "imageUrl": "https://cdn.dummyjson.com/products/images/beauty/Red%20Lipstick/thumbnail.png"
  },
  {
    "id": 3,
    "name": "Powder Canister",
    "category": "beauty",
    "description": "The Powder Canister is a finely milled setting powder designed to set makeup and control shine. With a lightweight and translucent formula, it provides a smooth and matte finish.",
    "price": 14.99,
    "imageUrl": "https://cdn.dummyjson.com/products/images/beauty/Powder%20Canister/thumbnail.png"
  }
]
```

#### Here is an explanation behind results order:

Query text was `"red finish"`.

Tokenizer tokenizes query into `["red", "finish"]`.

Algorithm goes through all products and tokenizes product's name and description.

For better accuracy, I'm using a bidirectional matching approach which basically means "red" and "redhat" would be a match
because one is a subset of the other.
For this to work we have to do it both ways. First is checking if query token is subset of product parameter token and then vice versa.
That means if we have exact match, eg. "red" and "red", that token will be counted twice. That was accidental behavior, but it actually
works better. Exact matches should be more valuable.

For more test cases and exact calculation explanation, check [unit test for ProductScore class](src/test/java/com/kingict/spring/service/utils/ProductScoreUnitTest.java).
