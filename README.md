# Bank Application - Spring Boot

This project is a **Banking Application** built using **Spring Boot** with a focus on CRUD operations for users, customers, accounts, transactions, and passbooks. It implements **JWT-based authentication** and provides role-based access control for **ADMIN** and **CUSTOMER** roles.

## Features

- ✅ **User Registration**: Register as a **Customer** or **Admin**  
- ✅ **Login & JWT Authentication**: Secure login with JWT tokens  
- ✅ **Role-based Access**: Different access rights for **Admin** and **Customer**  
- ✅ **CRUD Operations**: Perform CRUD on Users, Customers, Accounts, Transactions, and more  
- ✅ **Passbook**: View all transactions and the current balance for a specific account  
- ✅ **Global Exception Handling**: Handles errors gracefully with meaningful messages  
- ✅ **Emailer**: Send notifications via email for important events  
- ✅ **Secure and Role-based Access**: **Admin** can view all customer accounts and transactions; **Customer** can only view their own accounts and transactions  
- ✅ **Transaction Management**: Perform debit, credit, and transfer operations on accounts

## Entities

- **User** (userid, username, password)  
- **Role** (roleid, roleName - Admin/Customer)  
- **Customer** (emailid, contactno., dob)  
- **Address** (city, state, pincode)  
- **Account** (accountid, accountnumber, accounttype, balance)  
- **Transaction** (transid, transtype [debit, credit, transfer], amount, date)  

## Database Structure

- **users**: Stores user credentials (username, password) and their roles  
- **roles**: Stores the role name (Admin, Customer)  
- **customers**: Stores customer details (email, contact number, date of birth)  
- **accounts**: Stores account details and the relationship to customers  
- **transactions**: Stores details of transactions (debit, credit, transfer)  
- **addresses**: Stores customer address (one-to-one relationship)  

## Endpoints

Here is the list of API endpoints available for testing:

### 1. **User Operations**

- **POST /register**: Register a new user  
  - Request Body: User details including **role (Admin/Customer)**, **username**, **password**  
  - Role: **Customer** or **Admin**

- **POST /login**: Login and get a JWT token  
  - Request Body: **username**, **password**  
  - Response: JWT token for authenticated user  

### 2. **Customer Operations**

- **GET /customer/{id}/accounts**: Get all accounts of a particular customer  
  - Request Params: **customerId**  
  - Response: List of accounts associated with the customer

- **GET /customer/{id}/transactions**: Get all transactions of a customer  
  - Request Params: **customerId**  
  - Response: List of transactions made by the customer

- **POST /customer/{id}/update**: Update a customer’s details (Only accessible by customer or Admin)  
  - Request Body: Customer details (email, contact, etc.)  
  - Response: Updated customer details

### 3. **Account Operations**

- **POST /account/create**: Create an account and assign it to a customer  
  - Request Body: Customer **id**, **account number**, **account type**, **balance**  
  - Response: Created account details

- **GET /account/{id}**: Get details of a particular account  
  - Request Params: **accountId**  
  - Response: Account details, including balance and type

- **GET /accounts**: Get all accounts (Admin only)  
  - Response: List of all customer accounts

- **POST /account/{id}/transaction**: Perform a transaction (debit, credit, or transfer) on an account  
  - Request Body: **transaction type**, **amount**, **target account** (for transfer), **balance update**  
  - Response: Success message or error details

### 4. **Transaction Operations**

- **GET /transaction/{id}**: Get details of a particular transaction  
  - Request Params: **transactionId**  
  - Response: Transaction details including type (debit, credit, transfer), amount, and date

- **GET /transactions/account/{accountId}**: Get all transactions for a particular account  
  - Request Params: **accountId**  
  - Response: List of transactions for the given account

- **GET /transactions/customer/{customerId}**: Get all transactions of a customer  
  - Request Params: **customerId**  
  - Response: List of transactions made by the customer

### 5. **Passbook Operations**

- **GET /passbook/{accountId}**: View the passbook for a specific account (All transactions and balance)  
  - Request Params: **accountId**  
  - Response: List of transactions, current balance, and summary of account activity

## Technologies Used

- **Spring Boot**  
- **Spring Security**  
- **JWT (JSON Web Token)**  
- **JPA/Hibernate**  
- **MySQL**  
- **Postman** (For API testing)  
- **Maven** (for dependency management)

## Postman Collection

You can test the API endpoints using the **Postman** collection. To import the collection, click the link below:

[**Postman Collection**](https://ashdagale-1846813.postman.co/workspace/Personal-Workspace~e3ea8d01-0fba-44f4-b310-62c30f32f000/collection/48102235-56b4be39-f09a-4ab6-ba63-1755fac5433c?action=share&creator=48102235)

