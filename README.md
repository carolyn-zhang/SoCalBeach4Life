# SoCalBeach4Life - TEAM WAC
Wesley Tong (wytong@usc.edu), Aidan Chueh (achueh@usc.edu), Carolyn Zhang (zhangcy@usc.edu)

**Demo**: https://drive.google.com/file/d/13U5zWyXJAGSkFRlnOPpMj4hL8KLeAv6q/view?usp=sharing   

**Instructions**:  

Set the emulator location to coordinates 34.0522, -118.2437 as seen below.
<img width="823" alt="Screen Shot 2022-11-07 at 7 07 26 PM" src="https://user-images.githubusercontent.com/46872874/200465603-795cc76c-392e-4786-bf2d-19c7e90e37e2.png">

In Android Studio, build the project and click the run app button.

Accept location requests.

Register a profile or login with an existing one:  
Email: zhangcy@usc.edu  
Password: 123

**Improvements since project 2.4**:

-The beaches now shown near the user will be filtered by string to ensure that it is only beaches that are being shown to the user and not location associated with beached. 

-The radius for finding a restaurant around the user has been converted to feet as opposed to meters. 

-An extra unit test was added to test the Result.toString() method.

-The beach overall score is now calculated correctly.

-The user is able to include an optional comment to a beach review as well as delete it. 

-There is also a new feature that allows users to invite their friends to download the app through email notification, which is an important functionality to increase the user base of our app.

-Code cleanup and refactoring for modulization.


