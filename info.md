# Car Rental System - Features

## 1. Customer Features

- User registration
- Login / Logout
- Forgot password / Reset password
- Manage profile
- Upload profile picture
- Search vehicles
- Filter vehicles by:
  - Vehicle type
  - Brand
  - Price
  - Transmission
  - Fuel type
  - Seats
  - Availability
- View vehicle details
- View vehicle images
- View rental price
- Check vehicle availability
- Select rental date/time
- Select pickup location
- Select return location
- Make a reservation
- Cancel reservation
- View rental history
- View current rental
- View upcoming rentals
- View completed rentals
- View payment history
- View invoice
- Rate and review vehicle
- Favorite vehicles
- Notifications
- Change language
- Light/Dark mode

## 2. Vehicle Management

Admin/Manager can:

- Add vehicle
- Update vehicle
- Delete vehicle
- View vehicle list
- Upload vehicle images
- Set vehicle status
- Set rental price
- Assign vehicle category
- Manage vehicle specifications

### Vehicle Status

```text
Available
Reserved
Rented
Maintenance
Unavailable
```

### Vehicle Information

```text
Vehicle
├── ID
├── Brand
├── Model
├── Year
├── License Plate
├── Color
├── Type
├── Transmission
├── Fuel Type
├── Seats
├── Price Per Day
├── Mileage
├── Description
├── Images
└── Status
```

## 3. Rental / Booking Management

- Create rental
- Check availability
- Select pickup date
- Select return date
- Calculate rental duration
- Calculate rental cost
- Apply discount
- Add additional services
- Confirm booking
- Cancel booking
- Extend rental
- Return vehicle
- Record late return
- Calculate late fee
- View rental status

### Rental Lifecycle

```text
Pending
   ↓
Confirmed
   ↓
Picked Up
   ↓
Active Rental
   ↓
Returned
   ↓
Completed
```

## 4. Payment Features

- Calculate total rental price
- Deposit management
- Payment
- Payment confirmation
- Payment history
- Refund
- Discount / Coupon
- Invoice generation
- Payment status

### Example

```text
Rental Price       $50 × 3 days = $150
Insurance          $20
Additional Service $10
Discount            -$10
--------------------------
Total              $170
Deposit             $50
Remaining           $120
```

## 5. Admin Features

### Admin Dashboard

- Dashboard statistics
- Manage users
- Manage employees
- Manage vehicles
- Manage categories
- Manage locations
- Manage rentals
- Manage reservations
- Manage payments
- Manage discounts
- Manage reviews
- Manage notifications
- View reports

### Dashboard Example

```text
Total Vehicles       120
Available             65
Currently Rented      40
Maintenance            15

Today's Rentals        18
Today's Revenue    $1,250

Total Customers     2,450
```

## 6. Staff / Employee Features

### Manager

- Manage vehicles
- Manage employees
- Manage rentals
- Manage customers
- View reports

### Rental Staff

- Create reservation
- Confirm reservation
- Check customer documents
- Vehicle pickup
- Vehicle return
- Calculate additional charges

### Admin

- Full system access
- User/role management
- System configuration

## 7. Vehicle Maintenance

- Maintenance records
- Maintenance schedule
- Service history
- Oil change tracking
- Tire replacement
- Repair records
- Maintenance cost
- Set vehicle to `Maintenance`
- Automatically make vehicle unavailable during maintenance

### Maintenance Flow

```text
Toyota Camry
      ↓
Maintenance
      ↓
Oil Change
      ↓
$80
      ↓
Completed
      ↓
Available
```

## 8. Location Management

For rental companies with multiple branches:

- Add rental location
- Pickup location
- Return location
- Branch management
- One-way rental
- Location availability

### Example Locations

```text
Phnom Penh Branch
Siem Reap Branch
Battambang Branch
Sihanoukville Branch
```

## 9. Review & Rating

### Customer

- Rate vehicle
- Write review
- Edit review
- Delete review

### Example

```text
Toyota Camry
★★★★★ 4.8

"Very clean and comfortable car."
```

### Admin

- View reviews
- Hide inappropriate reviews
- Delete reviews

## 10. Notification System

Notifications for:

- Booking confirmed
- Booking cancelled
- Rental starting soon
- Rental ending soon
- Payment successful
- Payment failed
- Vehicle return reminder
- Late return
- Promotion/discount

Possible technologies:

```text
Firebase Cloud Messaging
        +
Flutter Local Notifications
```

## 11. Reports & Analytics

Admin/Manager can view:

- Revenue report
- Rental report
- Vehicle utilization
- Most rented vehicles
- Most popular vehicle categories
- Customer report
- Payment report
- Cancellation report
- Maintenance cost report
- Monthly/yearly revenue

### Example

```text
Revenue
Jan  $8,500
Feb  $9,200
Mar  $11,400
Apr  $10,800
```

## 12. Security & Authorization

- JWT authentication
- Refresh token
- Role-based authorization
- Password hashing
- Permission management
- Secure API
- Input validation
- Account activation/deactivation
- Login history
- Audit logs

### Roles

```text
ADMIN
MANAGER
STAFF
CUSTOMER
```

# Recommended System Modules

For a Flutter + Spring Boot + Oracle/JPA architecture:

```text
Car Rental System
│
├── Authentication
│   ├── Login
│   ├── Register
│   ├── JWT
│   └── Password Reset
│
├── Customer
│   ├── Profile
│   ├── Favorites
│   └── Reviews
│
├── Vehicle
│   ├── Vehicle
│   ├── Category
│   ├── Images
│   └── Availability
│
├── Reservation
│   ├── Booking
│   ├── Availability
│   └── Cancellation
│
├── Rental
│   ├── Pickup
│   ├── Active Rental
│   ├── Return
│   └── Late Fee
│
├── Payment
│   ├── Payment
│   ├── Deposit
│   ├── Refund
│   └── Invoice
│
├── Maintenance
│   ├── Service
│   ├── Repair
│   └── Maintenance History
│
├── Location
│   └── Branches
│
├── Notification
│
└── Reports
    ├── Revenue
    ├── Rentals
    ├── Vehicles
    └── Customers
```

# MVP Features

If this is a student or portfolio project, start with:

1. Authentication
2. Customer management
3. Vehicle management
4. Vehicle search/filter
5. Availability checking
6. Reservation
7. Rental
8. Payment
9. Rental history
10. Admin dashboard

# Advanced Features

After the MVP, add:

11. Vehicle maintenance
12. Multiple branches
13. Reviews and ratings
14. Coupons
15. Notifications
16. Reports and analytics
17. Online payment
18. Vehicle pickup/return inspection
19. Digital rental contracts
20. GPS/location tracking

# Core Business Flow

The main rental process should be:

```text
Search
  ↓
Check Availability
  ↓
Reserve
  ↓
Payment / Deposit
  ↓
Pickup
  ↓
Active Rental
  ↓
Return
  ↓
Final Payment
  ↓
Review
```