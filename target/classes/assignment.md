# Functionality

> [!NOTE]
> - default image for user and dish -> metadata should be saved in db
> - BASE_URL
```java
 @RequestMapping(Utils.BASE_URL+"/group")
```
> - Pagination data
```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.domain.Page;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationData {
    private int page;
    private int numberOfElements;
    private int totalPages;
    private long totalElements;

    public static PaginationData of(Page<?> pagination) {
        return PaginationData.builder()
                .page(pagination.getNumber())
                .totalElements(pagination.getTotalElements())
                .totalPages(pagination.getTotalPages())
                .numberOfElements(pagination.getNumberOfElements())
                .build();
    }
}
```
> - in orderService
before saving, getNextOrderNumber from repo

## User
 - [ ] registration *Two-step email verification with OTP*
 - [ ] log in
 - [ ] log out
 - [ ] change credentials *email, password etc*

## System
 - [ ] implement logging

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

