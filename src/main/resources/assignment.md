# Functionality

> [!NOTE]
> default image for user and dish

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
 - createdAt
 - visible
 - generalStatus *active, block*
 - refreshTokens `bidirectional`
 - roles `bidirectional`

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
 - image
 - quantityAvailable
 - visible
 - createdAt
 - category *hot, cold, soup, grill, appetizer, dessert*
 - orders `bidirectional`

## Order
 - id
 - name
 - discount
 - totalPrice
 - customer
 - visible
 - createdAt
 - status *pending, preparing, completed, canceled*
 - orderItems `bidirectional`

## OrderItem
 - id
 - quantity
 - price
 - note
 - dish
 - order
 - createdAt
 - type *dineIn, toGo, Delivery*

