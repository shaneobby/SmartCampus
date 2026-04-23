# SmartCampus API

| Name | E. S. R. Coorey |
|------|------------------------|
| UoW | w2120256 |
| IIT | 20231361 |
| Group | CS-20 |

A RESTful API built with JAX-RS (Jersey) for the 5COSC022W Client-Server Architectures coursework at the University of Westminster. This API manages rooms and sensors across a university campus. Users can create rooms, register sensors, log readings, and the error handling makes sure nothing crashes.

## Tech Stack

- Java 21
- JAX-RS / Jersey 2.32
- Jackson (JSON serialisation)
- Grizzly HTTP server
- Maven

## How to Build and Run

### Prerequisites

- JDK 21 or higher
- Maven
- Apache NetBeans is optional, but useful for opening and running the project

### Steps

1. Clone the repository:

```bash
git clone https://github.com/shaneobby/SmartCampus.git
```

2. Open the project in NetBeans, or use your terminal in the project folder
3. Build the project:

```bash
mvn clean package
```

4. Run the application:

```bash
mvn exec:java
```

5. The API will be available at:

`http://localhost:8080/api/v1/`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/` | Discovery - returns API info and resource links |
| GET | `/api/v1/rooms` | Get all rooms |
| POST | `/api/v1/rooms` | Create a new room |
| GET | `/api/v1/rooms/{roomId}` | Get a single room by ID |
| DELETE | `/api/v1/rooms/{roomId}` | Delete a room (blocked if sensors exist, 409) |
| GET | `/api/v1/sensors` | Get all sensors |
| GET | `/api/v1/sensors?type=CO2` | Filter sensors by type |
| GET | `/api/v1/sensors/{sensorId}` | Get a single sensor by ID |
| POST | `/api/v1/sensors` | Create a sensor (validates roomId, 422 if invalid) |
| GET | `/api/v1/sensors/{sensorId}/readings` | Get reading history for a sensor |
| POST | `/api/v1/sensors/{sensorId}/readings` | Add a reading (blocked if MAINTENANCE, 403) |

---

## Sample curl Commands

**1. API Discovery**

```bash
curl -X GET http://localhost:8080/api/v1/
```

**2. Get all rooms**

```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

**3. Create a new room**

```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "CS-201", "name": "CS Lab", "capacity": 40}'
```

**4. Delete a room that still has sensors (expect 409)**

```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-100
```

**5. Get sensors filtered by type**

```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
```

**6. Create a sensor linked to a room**

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-002", "type": "Temperature", "status": "ACTIVE", "currentValue": 21.0, "roomId": "LIB-100"}'
```

**7. Log a sensor reading**

```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 23.5}'
```

***REPORT***

**Q1) Default lifecycle of a JAX-RS resource class**  
Resource instances may be created and managed by the runtime in a thread-safe way. Shared data should be stored carefully and synchronized to remain thread safe.

**Q2) Benefits of hypermedia**  
Hyperlinks allow the server to provide distinct endpoints to the user without hardcoding every endpoint. This reduces the burden of scaling a system.

**Q3) The difference between returning only the ID versus the entire object list**  
Returning only the ID reduces the size of the payload and the bandwidth load, but it forces the client to send multiple fetch (`GET`) requests. Returning the entire list of objects increases the payload, but improves usability when the client receives the data.

**Q4) DELETE operation being idempotent**  
The DELETE operation is idempotent in REST. In the first iteration, an object is deleted. In the second iteration of the DELETE request, since the object being deleted is already gone, the API should return `404 Not Found`.

**Q5) Usage of the `@Consumes` annotation**  
`@Consumes` allows you to define an input parameter that will be expected with the query. If the stated input parameter is not provided, even if the hyperlink is accurate, the query will not go through and will return `415 Unsupported Media Type`.

**Q6) Difference between `@QueryParam` and using a direct URL path query**  
`@QueryParam` is the standard for filtering in REST. It is flexible and works well with multiple varying search conditions, while a URL path query is better suited for segmenting the collection to be searched.

**Q7) Architectural benefits of the sub-resource locator**  
The sub-resource architectural pattern provides a readable and testable structure for hyperlinks by creating a tree structure with similar information or methods that can be used in a branching pattern.

**Q8) HTTP 422 being considered more semantically accurate than 404 for a missing reference in valid JSON**  
`404 Not Found` implies that the URI was not provided, compared to `422 Unprocessable Entity`, which implies that the request is syntactically correct but semantically invalid and cannot be executed. In the case of a missing valid JSON reference, `422` is considered more accurate since the syntax is correct, but it cannot be executed because it does not exist.

**Q9) Risks of exposing Java stack traces to external API consumers**  
Exposing the Java stack trace to external API consumers runs the risk of allowing users to identify weaknesses in the API by revealing information about the system, such as file names, frameworks, line numbers, and versions.

**Q10) Advantages of using JAX-RS filters instead of manually inserting `Logger.info()`**  
Filters centralize the logging process by avoiding repetitive code in every endpoint and support maintaining consistent logging behavior across the API.

