package org.fhi360.module.Hts.domain.repositories;

import org.fhi360.module.Hts.domain.entities.Recency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecencyRepository extends JpaRepository<Recency, Long> {
    Recency findByParticipantIdAndFacilityId(String participantId, Long facilityId);
}
