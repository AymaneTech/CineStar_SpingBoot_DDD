package dev.codex.cinestar.Room.Application.Services.Impl;

import dev.codex.cinestar.Room.Application.DTOs.Requests.ZoneRequest;
import dev.codex.cinestar.Room.Application.Services.ZoneService;
import dev.codex.cinestar.Room.Domain.Entities.Room;
import dev.codex.cinestar.Room.Domain.Entities.Zone;
import dev.codex.cinestar.Room.Infrastructure.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository repository;

    public List<Zone> createAll(List<ZoneRequest> zones, Room room) {
        List<Zone> zoneList = zones.stream()
                .map(zone -> new Zone(
                                zone.name(),
                                zone.description(),
                                zone.capacity(),
                                zone.price(),
                                zone.position(),
                                room
                        )
                )
                .toList();
        return repository.saveAll(zoneList);
    }

    public List<Zone> sync(List<ZoneRequest> requestZones, Room room) {
        List<Zone> existingZones = repository.findAll();
        Map<String, Zone> existingZonesMap = existingZones.stream()
                .collect(Collectors.toMap(Zone::getName, zone -> zone));

        List<Zone> zonesToSave = requestZones.stream()
                .map(requestZone -> {
                    Zone existingZone = existingZonesMap.get(requestZone.name());
                    if (existingZone != null) {
                        existingZone.setName(requestZone.name());
                        return existingZone;
                    } else {
                        return new Zone(
                                requestZone.name(),
                                requestZone.description(),
                                requestZone.capacity(),
                                requestZone.price(),
                                requestZone.position(),
                                room
                        );
                    }
                })
                .collect(Collectors.toList());

        return repository.saveAll(zonesToSave);
    }
}
