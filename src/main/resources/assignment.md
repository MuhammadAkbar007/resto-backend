# Functionality

## Additional Considerations

 - [ ] *Security Enhancements*
Consider rate limiting OTP verification attempts to prevent brute force attacks
You might want to implement a maximum attempt count before temporarily locking verification

 - [ ] *User Experience*
You could add a resend OTP functionality for cases where the email is delayed or the OTP 
expires. Consider adding alternative delivery methods (SMS) for the OTP

 - [ ] *Recovery Process*
Implement a mechanism to handle cases where users lose access to their email 
during registration

 - [-] *Cleaning db*
Delete expired OTPs



> [!NOTE]
> - default image for user and dish -> metadata should be saved in db
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

## System
 - [ ] implement logging

## User
 - [ ] registration *Two-step email verification with OTP*
 - [ ] admin seeder in dataInitializer
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

