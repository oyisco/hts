package org.fhi360.module.Hts.domain.repositories;

import org.lamisplus.modules.lamis.legacy.domain.repositories.AssessmentRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssessmentRepository1 extends AssessmentRepository {
    @Query(value = "SELECT count(*) FROM assessment", nativeQuery = true)
    int maxAssessmentId();

}
