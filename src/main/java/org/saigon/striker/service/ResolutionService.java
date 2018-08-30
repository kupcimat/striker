package org.saigon.striker.service;

import org.saigon.striker.model.Resolution;
import org.saigon.striker.model.ResolutionEntity;
import org.saigon.striker.model.ResolutionList;
import org.saigon.striker.model.ResolutionRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class ResolutionService {

    private final ResolutionRepository resolutionRepository;

    public ResolutionService(ResolutionRepository resolutionRepository) {
        // TODO null check
        this.resolutionRepository = resolutionRepository;
    }

    public Resolution createResolution(Resolution resolution) {
        // TODO validation
        return resolutionRepository.save(ResolutionEntity.fromResolution(resolution))
                .toResolution();
    }

    public ResolutionList getResolution(String name) {
        // TODO validation
        return resolutionRepository.findByName(name).stream()
                .map(ResolutionEntity::toResolution)
                .collect(toResolutionList());
    }

    public ResolutionList getAllResolutions() {
        return resolutionRepository.findAll().stream()
                .map(ResolutionEntity::toResolution)
                .collect(toResolutionList());
    }

    public void deleteResolution(String name) {
        // TODO validation
        resolutionRepository.deleteAll(
                resolutionRepository.findByName(name));
    }

    private Collector<Resolution, Object, ResolutionList> toResolutionList() {
        return Collectors.collectingAndThen(toList(), ResolutionList::new);
    }
}
