package org.saigon.striker.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ResolutionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    // TODO unique?
    private String name;
    private Long days;

    protected ResolutionEntity() {
    }

    public ResolutionEntity(String name, Long days) {
        // TODO validation
        this.name = name;
        this.days = days;
    }

    public static ResolutionEntity fromResolution(Resolution resolution) {
        return new ResolutionEntity(resolution.getName(), resolution.getDays());
    }

    public Resolution toResolution() {
        return new Resolution(name, days);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
    }
}
