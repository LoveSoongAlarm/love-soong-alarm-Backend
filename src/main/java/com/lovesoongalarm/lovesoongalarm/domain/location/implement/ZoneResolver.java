package com.lovesoongalarm.lovesoongalarm.domain.location.implement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.locationtech.jts.geom.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ZoneResolver {
    private static final Map<String, List<String>> adjacency = Map.of(
            "1", List.of("2", "3", "7"),
            "2", List.of("1", "3", "5", "7"),
            "3", List.of("1", "2", "4", "5"),
            "4", List.of("3", "5", "6"),
            "5", List.of("2", "3", "4", "6", "7"),
            "6", List.of("4", "5", "7"),
            "7", List.of("1", "2", "5", "6")
    );
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final Map<String, Geometry> zones = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        try (InputStream is = new ClassPathResource("zones/campus-zones.geojson").getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(is);

            for (JsonNode feature : root.path("features")) {
                String zoneId = feature.path("properties").path("id").asText();
                JsonNode geom = feature.path("geometry");
                if ("Polygon".equalsIgnoreCase(geom.get("type").asText())) {
                    zones.put(zoneId, polygonFrom(geom));
                }
            }

            System.out.println("[ZoneResolver] Loaded zones : " + zones.keySet());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load zones GeoJSON", e);
        }
    }

    public String resolve(double lat, double lon) {
        Point p = geometryFactory.createPoint(new Coordinate(lon, lat));
        return zones.entrySet().stream()
                .filter(e -> e.getValue().covers(p))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("8");   // 우선은 구역을 벗어난 경우 zone 8 반환
    }

    public List<String> getNeighborZones(String zoneId) {
        return adjacency.getOrDefault(zoneId, List.of());
    }

    private Polygon polygonFrom(JsonNode geom) {
        JsonNode coordinates = geom.path("coordinates").get(0);
        Coordinate[] c = new Coordinate[coordinates.size() + 1];
        for (int i = 0; i < coordinates.size(); i++) {
            double lon = coordinates.get(i).get(0).asDouble();
            double lat = coordinates.get(i).get(1).asDouble();
            c[i] = new Coordinate(lon, lat);
        }
        c[coordinates.size()] = c[0];
        LinearRing linearRing = geometryFactory.createLinearRing(c);
        return geometryFactory.createPolygon(linearRing);
    }
}
