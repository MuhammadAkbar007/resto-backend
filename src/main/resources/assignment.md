# Functionality
## User
 - [x] registration *Two-step email verification with OTP*
 - [x] log in
 - [x] log out
 - [x] refresh token
 - [x] admin seeder in dataInitializer
 - [x] `read` (CRUD) *Pagination & Filter*
 - [x] `update` (CRUD)
 - [x] `delete` (CRUD)
 - [x] admin changes role *admin, manager, employee etc*
 - [x] admin changes user status *active, block, deleted etc*
 - [x] user changes his/her profile picture

## System
 - [ ] change `RuntimeException` handler in *GlobalExceptionHandler*

> [!NOTE]
> - default image for user and dish -> metadata should be saved in db
> - in orderService
before saving, getNextOrderNumber from repo

## System Functionality
 - [x] *PaginationData* is implemented

## OTP for email verification
 - [x] *Security Enhancements*
Consider rate limiting OTP verification attempts to prevent brute force attacks
You might want to implement a maximum attempt count before temporarily locking verification
 - [x] *Cleaning db*
Delete expired OTPs

# Entity
## User
 - id
 - firstName
 - lastName
 - email
 - phoneNumber
 - password
 - visible
 - createdAt
 - status *active, block*
 - photo
 - refreshTokens `bidirectional`
 - roles `bidirectional`
 - orders `bidirectional`

## Role
 - id
 - visible
 - roleType *customer, employee, manager, admin*
 - createdAt
 - users `bidirectional`

## Attachment
 - id
 - originalName
 - size
 - contentType
 - extension
 - visible
 - filePath
 - content
 - compressedAttachment
 - createdAt
 - storageType *database, filesystem*

## RefreshToken
 - id
 - token
 - expiryDate
 - createdAt
 - user

## Dish
 - id
 - name
 - price
 - quantityAvailable
 - visible
 - createdAt
 - dishCategory *hot, cold, soup, grill, appetizer, dessert*
 - image
 - orders `bidirectional`
 
## Order
 - id
 - number
 - discount
 - totalPrice
 - visible
 - createdAt
 - orderStatus *pending, preparing, completed, canceled*
 - customer
 - orderItems `bidirectional`

## OrderItem
 - id
 - quantity
 - price
 - note
 - createdAt
 - orederType *dineIn, toGo, Delivery*
 - dish
 - order

