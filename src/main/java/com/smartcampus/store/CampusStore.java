package com.smartcampus.store;

import com.smartcampus.exception.InvalidRequestException;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.exception.ResourceConflictException;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CampusStore {
    private static final CampusStore INSTANCE = new CampusStore();
    private static final List<String> ALLOWED_STATUSES = List.of("ACTIVE", "MAINTENANCE", "OFFLINE");

    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final Map<String, Sensor> sensors = new LinkedHashMap<>();
    private final Map<String, List<SensorReading>> readings = new LinkedHashMap<>();

    private CampusStore() {
    }

    public static CampusStore getInstance() {
        return INSTANCE;
    }

    public synchronized List<Room> getRooms() {
        return rooms.values().stream().map(this::copyRoom).collect(Collectors.toList());
    }

    public synchronized Room getRoom(String roomId) {
        String key = normalizeId(roomId, "roomId is required");
        Room room = rooms.get(key);
        if (room == null) {
            throw new ResourceNotFoundException("Room " + key + " was not found");
        }
        return copyRoom(room);
    }

    public synchronized Room createRoom(Room room) {
        if (room == null) {
            throw new InvalidRequestException("room payload is required");
        }
        String id = normalizeId(room.getId(), "room id is required");
        String name = normalizeText(room.getName(), "room name is required");
        int capacity = room.getCapacity();
        if (capacity <= 0) {
            throw new InvalidRequestException("room capacity must be greater than zero");
        }
        if (rooms.containsKey(id)) {
            throw new ResourceConflictException("room " + id + " already exists");
        }
        Room created = new Room(id, name, capacity, new ArrayList<>());
        rooms.put(id, created);
        return copyRoom(created);
    }

    public synchronized void deleteRoom(String roomId) {
        String key = normalizeId(roomId, "roomId is required");
        Room room = rooms.get(key);
        if (room == null) {
            throw new ResourceNotFoundException("Room " + key + " was not found");
        }
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("room " + key + " still has assigned sensors");
        }
        rooms.remove(key);
    }

    public synchronized List<Sensor> getSensors(String type) {
        List<Sensor> result = sensors.values().stream().map(this::copySensor).collect(Collectors.toList());
        if (type == null || type.isBlank()) {
            return result;
        }
        String filter = type.trim();
        return result.stream().filter(sensor -> sensor.getType() != null && sensor.getType().equalsIgnoreCase(filter)).collect(Collectors.toList());
    }

    public synchronized Sensor getSensor(String sensorId) {
        String key = normalizeId(sensorId, "sensorId is required");
        Sensor sensor = sensors.get(key);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor " + key + " was not found");
        }
        return copySensor(sensor);
    }

    public synchronized Sensor createSensor(Sensor sensor) {
        if (sensor == null) {
            throw new InvalidRequestException("sensor payload is required");
        }
        String id = normalizeId(sensor.getId(), "sensor id is required");
        String type = normalizeText(sensor.getType(), "sensor type is required");
        String roomId = normalizeId(sensor.getRoomId(), "roomId is required");
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new LinkedResourceNotFoundException("room " + roomId + " does not exist");
        }
        if (sensors.containsKey(id)) {
            throw new ResourceConflictException("sensor " + id + " already exists");
        }
        Sensor created = new Sensor(id, type, normalizeStatus(sensor.getStatus()), sensor.getCurrentValue(), roomId);
        sensors.put(id, created);
        room.getSensorIds().add(id);
        return copySensor(created);
    }

    public synchronized List<SensorReading> getReadings(String sensorId) {
        String key = normalizeId(sensorId, "sensorId is required");
        if (!sensors.containsKey(key)) {
            throw new ResourceNotFoundException("Sensor " + key + " was not found");
        }
        return readings.getOrDefault(key, List.of()).stream().map(this::copyReading).collect(Collectors.toList());
    }

    public synchronized SensorReading addReading(String sensorId, SensorReading reading) {
        String key = normalizeId(sensorId, "sensorId is required");
        Sensor sensor = sensors.get(key);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor " + key + " was not found");
        }
        if (!"ACTIVE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("sensor " + key + " is not accepting readings");
        }
        if (reading == null) {
            throw new InvalidRequestException("reading payload is required");
        }
        double value = reading.getValue();
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new InvalidRequestException("reading value must be a finite number");
        }
        String readingId = reading.getId() == null || reading.getId().isBlank() ? UUID.randomUUID().toString() : reading.getId().trim();
        long timestamp = reading.getTimestamp() <= 0 ? System.currentTimeMillis() : reading.getTimestamp();
        SensorReading created = new SensorReading(readingId, timestamp, value);
        readings.computeIfAbsent(key, ignored -> new ArrayList<>()).add(created);
        sensor.setCurrentValue(value);
        return copyReading(created);
    }

    private String normalizeId(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException(message);
        }
        return value.trim();
    }

    private String normalizeText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException(message);
        }
        return value.trim();
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "ACTIVE";
        }
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_STATUSES.contains(normalized)) {
            throw new InvalidRequestException("status must be ACTIVE, MAINTENANCE, or OFFLINE");
        }
        return normalized;
    }

    private Room copyRoom(Room room) {
        return new Room(room.getId(), room.getName(), room.getCapacity(), room.getSensorIds());
    }

    private Sensor copySensor(Sensor sensor) {
        return new Sensor(sensor.getId(), sensor.getType(), sensor.getStatus(), sensor.getCurrentValue(), sensor.getRoomId());
    }

    private SensorReading copyReading(SensorReading reading) {
        return new SensorReading(reading.getId(), reading.getTimestamp(), reading.getValue());
    }
}
