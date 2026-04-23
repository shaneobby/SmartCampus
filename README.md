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
