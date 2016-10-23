
Test Requests:

- Register a new destination (URL) returning its id

curl --header "Content-type: application/x-www-form-urlencoded" --request POST --data "url=http://posttestserver.com/post.php?dir=webhook"  http://localhost:8080/destinations


- List registered destinations [{id, URL},...]

curl --request GET http://localhost:8080/destinations


- Delete a destination by id

curl --request DELETE http://localhost:8080/destinations/1


- POST a message to this destination

curl --header "Content-type: text/pain" --request POST --data "WEBHOOK TEST" http://localhost:8080/destinations/1/message 
