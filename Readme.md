# QYRUS ECOMMERCE

----
Demo Ecommerce application which has most of the features of ecommerce other than integration with payment gateway. This is a mono repo consisting of python backend and react frontend.

## Local Setup:
Frontend:
clone the repo and go the directory
```
> npm i
> npm run dev
```

Create redis streams and store using docker compose

```
> cd redis
>sudo docker compose up
```

Backend:
```
cd CodeFluxProducer
python app.py

cd CodeFluxConsumer
python app.py
```



----

## RUN VIA DOCKER
