# Task Summary

It's a simple user management module with the following features

- Register
- Login
- List users
- Deactivate user
- Delete user

# Required Technologies

- Spring Boot
- Gradle
- MySql
- Docker
- Jenkins

# Notes

- All endpoints should be secured by JWT token (except for register and login).
- TDD should be used as a development approach
- Logging and Error Handling are required
- Your app should be containerized. We will only write `docker-compose up` to run the whole stack if it doesn’t work the task fails
- Data shouldn't be deleted even the containers are terminated/deleted
- A nightly build jenkins job should run every night making sure that the code is compiling, clean and test cases passes

# Stories

1. BE-UM-1 - Register a user using basic data (username, email and password) (open endpoint, no auth)
2. BE-UM-2 - Login with a username and a password (open endpoint, no auth)
3. BE-UM-3 - List Users by username and status "both are optional" (secure endpoint, requires auth)
4. BE-UM-4 - Deactivate user (secure endpoint, requires auth)
5. BE-UM-5 - Delete user (secure endpoint, requires auth)
6. BE-UM-6 - Auto Deactivate user who have not logged in over a month.(internal logic)

# Source Control Management

- Each one shall create a branch from main (i.e. username) and for each story take a branch from the task branch with the story name (username/BE-UM-1)
- After finishing a story a pull request shall be done to the task branch
