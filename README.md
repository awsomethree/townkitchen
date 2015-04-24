# The Town Kitchen #

## About Town Kitchen ##
The Town Kitchen is a community driven food business where they source,craft, 
and deliver locally-sourced lunch through a community of growers, food markers,
chefs and youth. 

The Town Kitchen vision is to create community through local food; a community
where low-income Oakland youth can shine; a community where it will introduce
under-served youth to talented chefs and artisans & start-up entrepreneurs so 
they have the skills and network to pursue their future. 

The Town Kitchen can be found in [Instagram](http://instagram.com/thetownkitchen) - [Twitter](https://twitter.com/TheTownKitchen)
or [www.thetownkitchen.com](http://www.thetownkitchen.com/)

## Requirements ##
The Town Kitchen need an __mobile__ app to create a better user/eater experience
with main functions of (in order of priority):

1. Take and process meal orders (currently available via website only), including
verification within pre-defined geographic area and disseminating orders to other
individuals
2. Estimate and track meal deliveries based on driver's location
3. Review meals (give feedback) and contact support.

## User audience ##
1. Office Managers and meeting coordinators who order lunch to their team
2. Individuals who are ordering lunch to themselves.

## User Story ##

_Required user stories_ : 

- [x] User can login with Twitter to access the application
- [x] __(Menu View)__ User can see the `current day` menu options in grid 
    - The menu options include the image of the menu, small description, 
        price and order quantity.
- [x] User can see the menu options of a `particular day`.
    - By clicking on calendar icon to choose specific dates 
- [x] __(Detail Menu View)__ User can see in detail specific menu options of the day
    - User can see the image of the menu, description of the menu, price, 
        additional serving information (e.g served with)
- [x ] User can choose and add particular menu options into the shopping cart
    - The shopping cart icon will also shows number of orders. 
- [x] __(Shopping Cart View)__ User can see the details of their shopping cart contents by clicking on the
        shopping cart icon
    - User can edit (either adding or removing their order), total order, 
        total price
- [x] __(Checkout View)__ User can see the shipping and billing info by clicking on the checkout icon from the shopping cart
    - By default billing address should be the same as shipping. User can have an option to change the billing address if it different from the shipping address
    - User should able to select the delivery location
    - User must be able to enter their credit cart info like CC, expiration date. This should also perform basic validation like date not expired. And the normal credit card number validation
    - When user click 'Order', the app should display a progress of the submission and display order confirmation page on success
- [x] __(Confirmation View)__ user should see the created order number with summary of the order. User should have an option to 'Share/Review' and this will link to TownKitchen facebook or Twitter account
- [x] __(My Order View)__ From home page, user should be able to click on the 'My Order' and see the current order status 
    - User can also see the current location of the Driver on the map


_Optional user stories_ : 

- [ ] User can login with Facebook or other Social media integration.
- [ ] On __Menu View__ user can also swipe left and right to navigate to the next
        or previous day
- [ ] On __Detail Menu View__ user can also swipe left and right to navigate to
        the other menu option for the same day
- [ ] on __Checkout View__ user can also enter the promo code
- [ ] on __Checkout View__ user can pick their delivery location using maps, by default the GPS will give the
        default location

First Draft Mockup can be found [Mockup](docs/TownKitchenApp.pdf)

![Application-demo](TheTownKitchenApp_demo1.gif)