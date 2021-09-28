# Job Management System

## Description
A simple Job Management Service to handle the execution of multiple types of Jobs.

### Getting Started
To build and run this project, you need java 11 and maven installed

Run the  following command to run test

`mvn test`

To build and run the project, run the following command in the terminal

``
    mvn package && java -jar ./target/*.jar
``

### Environment variables
`NO_OF_J0B_POOL_EXECUTOR_THREADS` The no of threads used in executing the job pool (ie jobs in queue), defaults to 1.

`QUEUE_INITIAL_CAPACITY` The initial capacity for the JobManagementService's priority blocking queue. The default is 10.

### Assumptions
- Comments have been added throughout the application to highlight assumptions made.

### Notes
- Sample job classes have been structured to allow some rudimentary tests.
- A total of about 24 Hours, spread over four days was spent on this case study.

### Further Improvements
- The project can be re implemented with framework like spring boot
- More encompassing tests could be written to cover scheduler behavior.
- A time-out can be set for job execution after which the job is failed