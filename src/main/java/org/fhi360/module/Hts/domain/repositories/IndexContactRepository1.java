package org.fhi360.module.Hts.domain.repositories;

import org.lamisplus.modules.lamis.legacy.domain.repositories.IndexContactRepository;
import org.springframework.data.jpa.repository.Query;

public interface IndexContactRepository1 extends IndexContactRepository {

    @Query(value = "SELECT max(indexcontact_id) FROM indexcontact", nativeQuery = true)
    Long masIndexContactId();
}
