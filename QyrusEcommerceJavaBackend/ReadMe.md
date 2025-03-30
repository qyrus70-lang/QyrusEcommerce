This document shows How to setup the java backend for the Qart app!!!

# How to setup


### Run MySQL
```
cd docs/dockerfiles/mysql
docker-compose up
```

**Note**: Configure mysql port in properties file
### Populate the Database
```
cd docs
python populate_db.py
```

### Run the app
```
mvn clean package
mvn spring-boot:run
```
