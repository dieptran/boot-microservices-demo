# Boot-microservices-demo
This page aims to provide an **overal technical design** for a basic E-COMMERCE web application by using Java and Microservices.
Also, provide a sample code of **services inter-communication**

# Software Specification
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
To adapt the software specs, Java Spring-based techologies, Netflix OSS and Microservices parttern are choosen for development

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

## Overal Architecture

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
#### Inter-communication

#### Distributed transaction management
Some actions need to span across multiple services that data stored in separated database to complete. How to rollback data if one of those service are failure to ensure all data is consistence

#### Fault tolenrant
If a service, somehow, cannot proceed its logic in right way e.g. get stuck. How to pypass to release waiting?

#### Monitoring to troubleshoot problems
When an incident comes, how to check logs or trace the request when the services are on multiple instances? 

### Frontend
### Data Persistence
#### ERD
A typical ERD for this eCommerce site

HINH....
  
#### In-used DB types
- *PostgreSQL*: for Commerce/Inventory/... services to be able use ACID when data is sensity and need to be run in BI system later
- *MongoDB*: for Product Catalog service or UserBehaviors analytics when its data mostly is un-structured, complex data.
- *ElasticSeach*: for Search service that support full-text search and many more interesting featured like auto-suggestion, facted search, advance details search (colors, category...)
## Deployment plan
# Demo code for services inter-communication








