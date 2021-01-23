package org.fhi360.module.Hts.domain.repositories;

import org.lamisplus.modules.lamis.legacy.domain.repositories.ChronicCareRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChronicCareRepository1 extends ChronicCareRepository {

    @Query(value = "SELECT count(*) FROM Chronic_care", nativeQuery = true)
    int maxChronicCare();
}
