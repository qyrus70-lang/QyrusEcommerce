# How to setup


### Run MySQL
```
cd docs/dockerfiles/mysql
docker-compose up
```

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
