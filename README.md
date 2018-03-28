#Reactive Rest APIs with Spring WebFlux and DynamoDB Async


## Requirements

Run DynamoDB at local

```
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
```

In the DynamoDB console at : `localhost:8000/shell/`

Execute:
```
AWS.config.update({
  region: "eu-west-1",
  endpoint: 'http://localhost:8000',
  accessKeyId: "jsa",
  secretAccessKey: "javasampleapproach"
});

var dynamodb = new AWS.DynamoDB();
    var params = {
        TableName : "Tweet",
        KeySchema: [
            { AttributeName: "Id", KeyType: "HASH"}
        ],
        AttributeDefinitions: [
            { AttributeName: "Id", AttributeType: "S" },
            { AttributeName: "Text", AttributeType: "S" },
            { AttributeName: "CreatedAt", AttributeType: "S" }
        ],
        ProvisionedThroughput: {
            ReadCapacityUnits: 5,
            WriteCapacityUnits: 5
        },
        GlobalSecondaryIndexes: [{
          IndexName: "TweetIndex",
          KeySchema: [
              {
                  AttributeName: "Text",
                  KeyType: "HASH"
                  },
              {
                  AttributeName: "CreatedAt",
                  KeyType: "RANGE"
              }
          ],
          Projection: {
              ProjectionType: "ALL"
              },
          ProvisionedThroughput: {
              ReadCapacityUnits: 5,
              WriteCapacityUnits: 5
              }
          }]
    };

    dynamodb.createTable(params, function(err, data) {
        if (err) {
            ppJson(err);
        } else {
            ppJson(data);
        }
    });
```


Run the app `mvn spring-boot:run`


The server will start at <http://localhost:8080>.

## Exploring the Rest APIs

The application defines following REST APIs

```
1. GET /tweets - Get All Tweets

2. POST /tweets - Create a new Tweet

3. GET /tweets/{id} - Retrieve a Tweet by Id

4. DELETE /tweets/{id} - Delete a Tweet

```

## Running integration tests

The project also contains integration tests for all the Rest APIs. For running the integration tests, go to the root directory of the project and type `mvn test` in your terminal.