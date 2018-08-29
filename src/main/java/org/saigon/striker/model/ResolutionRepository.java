package org.saigon.striker.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResolutionRepository extends JpaRepository<ResolutionEntity, Long> {

    // TODO should be unique
    List<ResolutionEntity> findByName(String name);
}
