## Overview
1. ### Design

   The AccountBook APP includes four functions: 

- **Login** module concludes three parts: login module, forget Password module and register module, and use login module, user can login our system and save their data in database, then if they forgot their password, they can use forget Password module to find back and for the first users, they must use register module to start a new account for use it,which protects the safety of the information record.

- **Income and cost** module concludes four parts,“add one bill” can jump to “add one record” module and the main interface of it can show every bill in this month,there has a button can choose month and date,it also can show the pie chart about the cost and income which shows us clearly.
- **Add new record** can jump to an interface that has different kind of category such as eating,traveling and so on,and has choose the date of happening bill and the exact number of the bill,also,users can type the node for record the special information. 
- **Currency option** is the module that we can change the exchange of HKD and CNY, which is really helpful for students in HK from mainland.

<img src="img\overview.jpg" alt="overview" style="zoom:67%;" />

2. ### Interface

|   <img src="img\Main.png" height=300/>   | <img src="img\add.png" height=300/>  |  <img src="img\InOut.png" height=300/>  |
| :--------------------------------------: | :----------------------------------: | :-------------------------------------: |
| <img src="img\MultiAcc.png" height=300/> | <img src="img\rate.png" height=300/> | <img src="img\account.png" height=300/> |



## Run

- The folder "accountBook" is an Android project, which you can open using Android Studio to compile and run.
- The folder "PHP + SQL" holds the database-related code. The database is configured on the web server so that the accountBook application can be run without additional configuration.