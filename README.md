# Share It

**Description**

The Java Backend part of the Share It project is responsible for handling the server-side logic that enables users to share various devices and tools through a web interface. The backend is built using the Java programming language and is designed to provide a reliable and scalable system for handling user requests and data storage.

System is divided to gateway service and main service.

Gateway is responsible for handling user authorization, allowing only authorized users to access the sharing functionality. 

Main service manages the device and tool data, including user profiles, available devices and tools, and current device/tool reservations. The backend implements a scheduling system to ensure that users can reserve devices and tools on a free basis without conflicts.

To enable fast and efficient data retrieval, the backend uses a database to store and manage all device and tool data. It also includes an API for connecting to the frontend and for enabling user interaction with the available devices and tools.

Overall, the Java Backend of Share It is a robust and scalable system that provides the necessary functionality to allow users to share different devices and tools on a free basis via a user-friendly web interface.

**Technology stack**

Language - Java 11
Frameworks - Spring Boot, Hibernate
Libraries - Lombock, Query DSL, Mapstruct
Platform - Docker container based microservice solution
RDBMS - Postgres SQL

**Database schema** 

![shareItDatabaseSchema](https://user-images.githubusercontent.com/103366918/231750903-7b381f7d-fcc4-400e-8d6b-03c7879a292f.png)



