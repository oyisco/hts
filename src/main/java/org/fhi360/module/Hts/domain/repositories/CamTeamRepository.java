package org.fhi360.module.Hts.domain.repositories;

import org.fhi360.module.Hts.domain.entities.CamTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CamTeamRepository extends JpaRepository<CamTeam, Long> {
    List<CamTeam> findByFacilityId(Long facilityId);
}
