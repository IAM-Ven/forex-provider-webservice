# forex-provider-webservice
A Spring boot project for Forex Trading system provider

### Prerequisites
1. Maven 3.5
2. Java 8
3. MySQL 


### Setting up webservice
1. Clone the application
```
git clone https://github.com/bhaskargogs/forex-provider-webservice.git
cd forex-provider-webservice
```

2. Create SQL Database
```sql
create database forex_app
```

3. Change MySQL username and password as per your MySQL installation
   * open `src/main/resources/application.properties` file.
   * change `spring.datasource.username` and `spring.datasource.password` properties as per your mysql installation

4. Run the app
  
  You can run the spring boot app by typing the following command -
  ```markdown
mvn spring:boot run
```

The server wil start at port 8081

You can also package the application in the form of a jar file and then run it like so -
```markdown
mvn package
java -jar target/forex-0.0.1-SNAPSHOT.jar
```

5. Role and Constant

The spring boot app uses role based authorization powered by spring security. To add the default role and to change account number dynamically in the database, I have added the following sql queries in `src/main/resources/data.sql` file. Spring boot will automatically execute this script on startup -
```sql
insert ignore into forex_db.role(name) values ('ROLE_USER');
insert ignore into forex_db.constant(name, val) values ('ACCOUNT_NUM', '1211345120');
```
Any new user who signs up to the app is assigned the `ROLE_USER` by default.
