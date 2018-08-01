## ReadMe

*Facebook Integration Bot*

### About:
A java servlet which communicates with facebook graph api to register complaints automatically

### Requirements:

-JDBC Driver Library

-mysql server

Instructions to setup project on netbeans:

-Create new project

-Add ComplaintBot.java ComplaintDetails.java
 ComplaintDetailsDao.java to your project

-Import JDBC library

-Get PageAccess token for bot facebook account from "developers.facebook.com/tools/explorer" set it equal to the accesstoken string

-Set the "<group id>/feed" to "bot managed page id/feed"

-Run file ComplaintBot.java 
