# Simple Spring SSE Demo

SSE is for Server Sent Events.

## Requirements

* jdk8 or Latest
* Maven3

## Run

```
mvn spring-boot:run
```

## Test

- #### Run on terminal. 
`curl -R http://localhost:8080/stream/<id>`

- #### Post Request. 
`http://localhost:8080/chat/<id>`

```json
{
    "from": "Name",
    "message":"Then they could not carry on partisan investigations."
}
```

## Takeaways

* the timeout for the SSE - Emitters needs to be raised.
* emitters need to be manually removed when the listener dies or on completion
* SSE with Spring Boot is practically a no-brainer
