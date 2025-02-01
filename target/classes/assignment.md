# Functionality

## User
 - [ ] registration *with email verification*
 - [ ] log in
 - [ ] log out
 - [ ] change credentials *email, password etc*

# Entity

## User
 - id
 - firstName
 - lastName
 - email
 - phoneNumber
 - password
 - photo
 - status *active, block*
 - refreshTokens `bidirectional`
 - roles `bidirectional`

## Role
 - id
 - roleType *customer, employee, manager, admin*
 - users `bidirectional`

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
 - image
 - isAvailable
 - quantityAvailable
 - category *hot, cold, soup, grill, appetizer, dessert*
 - orders `bidirectional`

## Order
 - id
 - discount
 - totalPrice
 - customer
 - createdAt
 - status *pending, preparing, completed, canceled*
 - orderItems `bidirectional`

## OrderItem
 - id
 - order
 - dish
 - quantity
 - price
 - note
 - type *dineIn, toGo, Delivery*

