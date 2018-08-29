package org.saigon.striker;

import org.saigon.striker.model.Resolution;
import org.saigon.striker.model.ResolutionEntity;
import org.saigon.striker.model.ResolutionList;
import org.saigon.striker.model.ResolutionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController("/")
public class MainController {

    private final ResolutionRepository resolutionRepository;

    public MainController(ResolutionRepository resolutionRepository) {
        // TODO null check
        this.resolutionRepository = resolutionRepository;
    }

    @GetMapping
    public String index() {
        return "Welcome to the Striker!";
    }

    @PostMapping("/resolution")
    @ResponseStatus(HttpStatus.CREATED)
    public Resolution createResolution(@RequestBody Resolution resolution) {
        return resolutionRepository.save(ResolutionEntity.fromResolution(resolution))
                .toResolution();
    }

    @GetMapping("/resolution")
    public ResolutionList getAllResolutions() {
        return resolutionRepository.findAll().stream()
                .map(ResolutionEntity::toResolution)
                .collect(toResolutionList());
    }

    @GetMapping("/resolution/{name}")
    public ResolutionList getResolution(@PathVariable String name) {
        return resolutionRepository.findByName(name).stream()
                .map(ResolutionEntity::toResolution)
                .collect(toResolutionList());
    }

    @DeleteMapping("/resolution/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResolution(@PathVariable String name) {
        final List<ResolutionEntity> resolutions = resolutionRepository.findByName(name);

        resolutionRepository.deleteAll(resolutions);
    }

    private Collector<Resolution, Object, ResolutionList> toResolutionList() {
        return Collectors.collectingAndThen(toList(), ResolutionList::new);
    }
}
