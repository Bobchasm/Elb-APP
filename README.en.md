# Eleme Frontend-Backend Separation Project

This is an implementation of the Eleme food delivery platform based on a Spring Boot backend and a Vue frontend. The project includes complete core functionalities such as user management, merchant management, food management, shopping cart, and order processing.

## Project Structure

- **elm_bk**: Spring Boot backend project
  - Controllers handle HTTP requests
  - Services implement business logic
  - Mappers interact with the database
  - Entities correspond to database tables
  - DTOs are used for data transfer
  - VO objects are used for view presentation
  - Security module uses JWT authentication

- **elmclient**: Vue frontend project
  - Built with Vue 2 framework
  - Includes page components and common components
  - Routing configuration and global state management
  - Static resources and API request utilities

## Key Features

### User Management
- User registration and login
- Personal information management
- Delivery address management
- Password modification

### Merchant Management
- Merchant information maintenance
- Merchant search by category
- View merchant details

### Food Management
- Query merchant food list
- Food information display

### Shopping Cart Management
- Add items to the shopping cart
- Modify item quantity in the cart
- Remove items from the cart
- View shopping cart details

### Order Management
- Create new orders
- View order details
- Query user order list
- Search orders by merchant and status
- Set order status

## Technology Stack

**Backend:**
- Spring Boot 2.x
- MyBatis Plus
- JWT authentication
- Swagger API documentation
- MySQL database

**Frontend:**
- Vue.js 2.x
- Vue Router
- Axios
- Element UI component library

## Installation and Running

**Backend:**
1. Install JDK 1.8+ and Maven
2. Import the database file `elm_bk.sql`
3. Modify the database connection information in `application.yml`
4. Build the project using Maven: `mvn clean package`
5. Run the Spring Boot application: `java -jar elm_bk.jar`

**Frontend:**
1. Install Node.js and npm
2. Navigate to the elmclient directory: `cd elmclient`
3. Install dependencies: `npm install`
4. Run the development server: `npm run serve`
5. Build the production version: `npm run build`

## API Documentation

Complete API documentation is accessible via Swagger: `http://localhost:8080/swagger-ui.html`

## Database Design

Includes the following main tables:
- Users table (users)
- Merchants table (businesses)
- Foods table (foods)
- Shopping carts table (carts)
- Orders table (orders)
- Delivery addresses table (addresses)

## License

This project is licensed under the MIT License. See the LICENSE file for details.