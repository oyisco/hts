package org.fhi360.module.Hts.domain.repositories;

import org.lamisplus.modules.lamis.legacy.domain.entities.Hts;
import org.lamisplus.modules.lamis.legacy.domain.repositories.HtsRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HtsRepository1 extends HtsRepository {
   List<Hts> findByState(Long stateId);
//    Optional<Hts> findByClientCodeAndFacilityId(String clientCode, Long facilityId);
    @Query(value = "SELECT count(*) FROM hts ", nativeQuery = true)
    int masHtsId();

}
