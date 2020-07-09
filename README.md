# SIMPLE ECOMMERCE APPLICATION
This page aims to provide an **overall technical design** for a basic E-COMMERCE web application by using Java and Microservices.
Also, provide a sample code of **inter-services communication**

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
  - ...
- SinglePageApp with React/Redux/NextJS
  - Mature, large community
  - Fast loading
  - Support both of types of rendering: client side (CSR) + server side (SSR)
  - Component oriented
  - Page pre-rendering with cache (thanks for Nextjs)

## Overall Architecture
![Overall Architecture](/assets/Overall-Architecture.png "Overall Architecture")

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

### A few big challenges
Using microservices principle means that we would have more than 1 backend app. This raises some tackles as below
#### Data 
To fllow de-coupling pattern in Microservices, we should apply Database-Per-Service pattern, it means: each service has it own database.
This allow us to use the best kind of database for each kind of data that the service handles. For example: for complex data as UserBehavior or ProductCatalog with different product dataset, we should use NoSQL.

**Solution**: apply [Database per Service] pattern
![Database per Service](/assets/Services-With-DB.png "Database per Service")

#### Inter-services communication
In monolothith app, interacting between modules/services is easy via functions calling.
However, in microservices architect, that's imposible because the services are deployed as independent apps. Actually, a service can invoke another service by directly call via Restful client, but what if destination service is unreachable? or long run? or the service's address was changed? 
This comes up some patterns to deal with this in microservices

- Use a **RestClient** like Feign/RestTemplate come with a service discovery and a load blancer 
  - A load-balancer (Ribbon): ensure call another alive instance for durable 
  - A service discovery (Eureka): ensure to use logical service name, instead of physical name (e.g. ip/port) when a new service instance is launched

This kind of communication is ok for **simple calls or synchronus call** when the thread wait for the response to handle next steps.

But what if **Asynchronus calls** (e.g. send "new order confirmed" when orders successlly placed that we don't need to wait for)

- Use **Message broker** as a middle man to store all messages produced by source service and then let a subcribed service consumes the messsage. Even in case of no subcribed services to consume, the message is still the in the queue, wait for until consuming, avoid data lost.

Sample of 2 kind of inter-services communication
![inter-services communication](/assets/Inter_Service-Communication.png)


#### Distributed transaction management
Some actions need to span across multiple services that data stored in separated database to complete. How to rollback data if one of those service are failure to ensure all data is consistence
**Solution**: apply Command/SAGA pattern to handle
![Command/SAGA](/assets/CQRS-Saga_Orchestration.png)

#### Fault tolenrant
If a service, somehow, cannot proceed its logic in right way e.g. get stuck. How to pypass to release waiting?

#### Monitoring to troubleshoot problems
When an incident comes, how to check logs or trace the request when the services are on multiple instances? 

### Frontend
### Data Persistence
#### ERD
A typical ERD for this eCommerce site
![ERD](/assets/Entities_Diagram.png "ERD")
  
#### In-used DB types
- *PostgreSQL*: for Commerce/Inventory/... services to be able use ACID when data is sensity and need to be run in BI system later
- *MongoDB*: for Product Catalog service or UserBehaviors analytics when its data mostly is un-structured, complex data.
- *ElasticSeach*: for Search service that support full-text search and many more interesting featured like auto-suggestion, facted search, advance details search (colors, category...)
## Deployment plan
# Demo code for services inter-communication








