package org.saigon.striker.controller;

import org.saigon.striker.model.Resolution;
import org.saigon.striker.model.ResolutionList;
import org.saigon.striker.service.ResolutionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class MainController {

    private final ResolutionService resolutionService;

    public MainController(ResolutionService resolutionService) {
        // TODO null check
        this.resolutionService = resolutionService;
    }

    @GetMapping
    // TODO check what is returned for random uri, e.g. /haha
    public String index() {
        return "Welcome to the Striker!";
    }

    @PostMapping("/resolution")
    // TODO replace with ResponseEntity.created()?
    @ResponseStatus(HttpStatus.CREATED)
    public Resolution createResolution(@RequestBody Resolution resolution) {
        return resolutionService.createResolution(resolution);
    }

    @GetMapping("/resolution")
    public ResolutionList getAllResolutions() {
        return resolutionService.getAllResolutions();
    }

    @GetMapping("/resolution/{name}")
    public ResolutionList getResolution(@PathVariable String name) {
        return resolutionService.getResolution(name);
    }

    @DeleteMapping("/resolution/{name}")
    // TODO replace with ResponseEntity.noContent()?
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResolution(@PathVariable String name) {
        resolutionService.deleteResolution(name);
    }
}
