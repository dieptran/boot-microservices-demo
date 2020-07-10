# SIMPLE E-COMMERCE APPLICATION
This page aims to provide an **Overall Technical Design** for a basic E-COMMERCE web application by using Java and Microservices.
Also, provide a sample code of **Inter-Service Communication**

# Software Requirements
This eCommerce web app should be used by **2 actors** (Customers & Administrator) and have some significant requirements

### Functional Requirements
- For Customers
  - A web page that shows all products on which customers can filter, short and search for products based on different criteria
  - Able to add a product to the shoping cart and place order
  - Able to login in by Facebook account
  - No online payment, support COD only at this moment

- For Administrator
  - Keep track price history
  - Audit customer's behaviors such as searching, filtering, product viewing, etc... to analytics for boosting sale
  
### Non-functional Requirements
- Scalability: asy to scale up / down based on the needs
- Stability: 99.99% uptime
- Resilience: quickly recovery in case of disater
- Reliability: ensure all data is consisted w/o losing
- Performance: all page load should be finished within 3s, ideally 1s
- Extensibility: easy to integrate with other external services

# Technical Design
## Overview
This application would consists 2 main part:
- Frontend (storefront web ui)
- Backend (Resful APIs)

The main flow looks like this:
FRONTEND ---- *async send request* --->> BACKEND --->> *handle logic & send response* --->> FRONTEND (update state to refresh ui part)

Basically, a combine of Java Spring-based + Netflix OSS + Microservices parttern are chosen for Backend development. And SinglePageApp with React + Redux + NextJS are chosen for Frontend.

**The reasons**
- Java Spring
  - Very mature, proven framework
  - A lot of components, easy to integrate for quickly development
- Netflix OSS
  - Seamless work with Spring since it's a part of Spring 
  - Developed by Netflix team that experienced in large-scale and highly avaibility system development
- Microservices
  - Easy to test, scaling per service
  - Easy to maintain and develop
  - ...
- SinglePageApp with React/Redux/NextJS
  - Mature, large community
  - Fast loading
  - Support both of types of rendering: client side (CSR) + server side (SSR)
  - Component oriented
  - Page pre-rendering with cache (thanks for Nextjs)

## Overall Architecture
![Overall Architecture](/assets/Overall-Architecture.png "Overall Architecture")

EXPLANATIONS

- Microservices
  - Heart of the backend app, handle all logics and process data
  - It consists all micro services, each service handle logic/data for its own domain
  - One service has one or multiple instances (or containers)
  - Inter-service communication would be used via:
    - Rest client (e.g. Feign, RestTemplate, HttpClient) for syn calls
    - Message broker (e.g. RabbitMQ, Kafka,...) for acyns calls 
  - Services know each other via logical name (not physical name like IP:port) by using a Service Discovery (Eureka, Consul,...) that auto track and manage service statuse
- API Gateway (Zull, Kong...)
  - Create single point for client's access
  - Manage things like
    - authentication, authority via Auth server that can connect or communicate with other external systems (e.g. Facebook)
    - ratelimit
    - routing...
- Config server (Spring)
  - Store all configuration data of the system in one place
  - Allow to refresh the system w/ new config data w/o downtime
- Crendential (Vault)
  - Store all credential data (password, secrect key...)
- Monitor (Zipkin & ELK)
  - Collects all logs or request tracing data for monitoring as well as debuging via UI for troubleshootings
- KV Cache cluster (Redis)
  - Store data for fast accessing like most search, even user carts...
- Fulltext search (Elastic Search, Algolia service...)
  - Used by search service
  - Data would be indexed from all DBs whenever updating happens
- Data warehouse
  - Collection of slave DBs would be auto replicated in a period or realtime 
  - This would be used for BI system
- Clickstream
  - Collects and push all user's activities on the app like viewing products/pages, search ... as messages to a message broker, then be stored in NoSQL database like Aurora that supports big query for analytics
- Notification service
  - Sending notifications like email, push message
  - This subcribes some message queues then handle messages and might call a serverless service (e.g. Lamdba) to send/push notification
- Asset service
  - Handles asset actions like resize, store... that use Lamdba function, then all assets would be stored in cloud storage (e.g. S3)

## Technical Stack
- Backend: Java, Spring Cloud, Netflix OSS
- Frontend: ReactJS, Redux, NextJS
- Data persistence: Postgres, Mongo, Elastic Search
- Code storage: Github
- Unittest/Code quality: JUnit, Sonarqube
- CI/CD: Jenkin, Spinnaker
- Hosting: public clouds (e.g. AWS for backend, Vercel for web frontend)
## Details Design
### Backend
### Microservices
Splitting into micro services to manage their own logic on separated databases

### A few challenges
Using microservices principle means that we would have more than 1 backend app. This raises some tackles as below
#### Privately data
To ensure the service is independant and its data should be privately and accessible only via its API, we apply [Database Per Service] pattern, it means: each service has it own database.
This allow us to use the best kind of database for each kind of data that the service handles. For example: for complex data as UserBehavior or ProductCatalog with different product dataset, we should use NoSQL.

**Solution**: apply [Database Per Service] pattern

![Database per Service](/assets/Services-With-DB.png "Database per Service")

#### Inter-service communication
In monolothith app, interacting between modules/services is easy via functions calling.
However, in microservices architect, that's imposible because the services are deployed as independent apps. Actually, a service can invoke another service by directly call via Restful client, but what if destination service is unreachable? or long run? or the service's address was changed? 
This comes up some patterns to deal with this in microservices

- Use a **RestClient** like Feign/RestTemplate comes with a service discovery and a load blancer 
  - A load-balancer (Ribbon): ensure call another alive instance for durability
  - A service discovery (Eureka): ensure to use logical service name, instead of physical name (e.g. ip/port) when a new service instance is launched

This kind of communication is ok for **simple calls or synchronus call** when the thread wait for the response to handle next steps.

But what if **Asynchronus calls** (e.g. send "new order confirmed" when orders successlly placed that we don't need to wait for)

- Use **Message broker** as a middle man to store all messages produced by source service and then let a subcribed service consumes the messsage. Even in case of no subcribed services to consume, the message is still the in the queue, wait for until consuming --> avoid data lost.

Sample of 2 kinds of inter-services communication

![inter-services communication](/assets/Inter_Service-Communication.png)


#### Distributed transaction management
Some actions need to span across multiple services that data stored in separated database to complete. How to rollback data if one of those services are failure?
*Solution*: apply [Command/SAGA] pattern to handle

![Command/SAGA](/assets/CQRS-Saga_Orchestration.png)

#### Fault tolerance
If a service somehow takes long time to finish due to network latency or service failure even. How to pypass to release long wait-queue?
*Solution*: apply [Circuit Breaker](https://martinfowler.com/bliki/CircuitBreaker.html "Circuit Breaker pattern") by using Netflix Hystrix (as a part of Spring framework) 

#### Monitoring to troubleshoot problems
When an incident comes, how to check the logs or trace the requests when the services are on multiple instances?

*Solution*: apply [Log Aggregation] pattern that all logs in every service instance would be sent to a centralize database, then use an query UI tool for tracking. We can use ELK or EFK: ElasticSearch for storage, Logtash/Fluentd agent for sending log, Kibana for query UI tool

### Frontend
Uses mordern technologies:
- React: to build independent UI component that instantly reflect UI part with latest data
- Redux: to manage state of React app
- NextJS: a React framework that supports routing, pre-rendering, caching, etc...
- JS SDK: generated from our Microservices APIs to reduce the complexity when calling APIs from client side
- [Bit](https://bit.dev "Bit") (*Optional*): to build, manage, reuse and test React components 

The app should be **auto-responsive** and **progressive** if possible

### Data Storage
We use Spring JPA/Hibernate as an ORM abstraction peristence layer to communicate with database.
The below ERD is used to define entity-mapping in JPA/Hibernate

#### ERD
A typical ERD for this eCommerce site

![ERD](/assets/Entities_Diagram.png "ERD")
  
- **ProductCatalog DB** (MongboDB)
  - ProductInfo 
    - Metadata only, such as name, desc, category, properties, etc...

- **Inventory DB** (PostgreSQL)
  - Products
    - Physical product, real variants of product with business info like: quantity in inventory, price, etc...
    - These data would be combined with info from ProductCatalog.ProductInfo (composition pattern) to make official product displayed in the site
  - ProductPriceHistory
    - Price history would be stored here for audit
- **Commerce DB** (PostgreSQL)
  - Orders
  - OrderLineItems
  - Shipments
- **User DB** (PostgreSQL)
  - Users
    - Store all users includes Admins or Customers that distinguishes by Role field
  - Addresses
    - Store all addresses of all users
  - UserAbandonCarts
    - Store list of products in customer's cart, this info would be used for marketing (abandoned cart email) or auto load when the customer logs in
- **Audit DB** (MongoDB)
  - AuditLog
    - Store all user activities that s/he made on data object for audit

#### In-used DB types
- *PostgreSQL*: for Commerce/Inventory/... services to be able use ACID when data is sensity and need to be run in BI system later
- *MongoDB*: for Product Catalog service or UserBehaviors analytics when its data mostly is un-structured, complex data.
- *ElasticSeach*: for Search service that support full-text search and many more interesting featured like auto-suggestion, facted search, advance details search (colors, category...)

# Demo code for Inter-Service Communication
### Description
This sample code demontrate a inter-service communication between: **OrderService and ProductService by using Feign** (sync call)

### Typical package structure
![Package diagram](/assets/Packages-Diagram.png "Package diagram")

### APIs

#### Product Service
`Host: localhost:8000`

1. GET PRODUCTS

        GET /products
        
        Params: NO
        
2. GET PRODUCT BY ID

        GET /products/{id}

        Params:
            - id (Long): identifier of product

3. CHECK PRODUCT AVAIBILITY

        GET /products/{id}/avaibility/{orderQuantity}

        Params:
            - id (Long): identifier of product
            - orderQuantity (Int): product quantity for checking

4. DESCREASE PRODUCT QUANTITY

        PUT /products/{id}/decreaseQuantity/{orderedQuantity}

        Params:
            - id (Long): identifier of product
            - orderQuantity (Int): quantity of product placed order and need to be subtract in inventory

#### Order Service
`Host: localhost:9000`

1. GET ORDER BY ID*

        GET /orders/{id}

        Params:
            - id (Long): identify of order

2. PLACE NEW ORDER

        POST /orders
    
        Request body (JSON):
        {
            "productId": 1,
            "customerEmail": "michel@mail.com",
            "quantity": 1,
            "price": 10000
        }

### How to run
Clone the code to local directory (`DEMO_HOME`)

#### Start applications
1. Start Eureuka service discovery

In terminal, open a new tab

        # cd DEMO_HOME/eureka-naming-server
        # mvn spring-boot:run

Wait util see `Tomcat started on port(s): 8761 (http) ...` --> Eureka is ready on `localhost:8761`, access locahost:8761 on browser to see Eureka web UI

2. Start Product service

In terminal, open another new tab

        # cd DEMO_HOME/product-service
        # mvn spring-boot:run
        
Wait util see `Tomcat started on port(s): 8000 (http)...` --> Product service is ready on `localhost:8000`

3. Start Order service

In terminal, open another new tab

        # cd DEMO_HOME/order-service
        # mvn spring-boot:run

Wait util see `Tomcat started on port(s): 9000 (http)...` --> Order service is ready on `localhost:9000`

#### API test
Note that seed data was set during the startup.
Seed data includes 3 products for testing purpose

1. Get all products

        # curl -v http://localhost:8000/products
        
   Returned payload
   
        [
            {
                "id": 1,
                "name": "MacBook Pro 2020",
                "description": "The brand new",
                "quantity": 5
            },
            {
                "id": 2,
                "name": "MacBook Air 2020",
                "description": "Refurbished one",
                "quantity": 10
            },
            {
                "id": 3,
                "name": "Surface 2019",
                "description": "Max options",
                "quantity": 15
            }
        ]
        
2. Get a product by `id = 1`

        # curl -v http://localhost:8000/products/1
        
   Returned payload
   
       {"id":1,"name":"MacBook Pro 2020","description":"The brand new","quantity":5}
   
3. Create order

`Create order` would do a *inter-service communication* by calling to `product-service` via Feign proxy for checking inventory quantity against ordered quantity. 

3.1 Order quantity is ready to place

        curl -v -X POST localhost:9000/orders -H 'Content-type:application/json' -d '{"customerEmail": "customer@mail.com", "productId": 1, "price": 1000, "quantity": 1}'
        
   Returned payload
   
      < HTTP/1.1 201 
      < Location: http://localhost:9000/orders/1
      < Content-Type: text/plain;charset=UTF-8
      < Content-Length: 30
      < Date: Thu, 09 Jul 2020 16:13:33 GMT
      < 
      * Connection #0 to host localhost left intact
      Order was successfully created
        
   As you see, in response header, it returns the location of newly created order `Location: http://localhost:9000/orders/1`
   
   Let check it out
   
      # curl -v http://localhost:9000/orders/1
   
   Returned payload
   
      {
        "id":1,
        "code":"O-239558",
        "orderDate":"2020-07-09T16:13:33.250Z",
        "subTotal":1000.0,
        "tax":null,
        "discount":null,
        "total":1000.0,
        "status":"CONFIRMED",
        "customerEmail":"customer@mail.com"
     }
      
3.2 Order quantity is greater than quantity in inventory  --> invalid

        curl -v -X POST localhost:9000/orders -H 'Content-type:application/json' -d '{"customerEmail": "customer@mail.com", "productId": 1, "price": 1000, "quantity": 1000}'
       
  Returned payload
  
    < HTTP/1.1 400 
    < Content-Type: text/plain;charset=UTF-8
    < Content-Length: 34
    < Date: Thu, 09 Jul 2020 16:26:44 GMT
    < Connection: close
    < 
    * Closing connection 0
    Not enough quantity to place order

  As you see, the quantity to order exceeds the quantity in inventory, so it returns a response with `statusCode=400` (bad request) and a message `Not enough quantity to place order`
  
  
