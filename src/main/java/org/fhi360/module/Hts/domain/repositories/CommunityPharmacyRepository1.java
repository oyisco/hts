package org.fhi360.module.Hts.domain.repositories;


import org.lamisplus.modules.lamis.legacy.domain.entities.CommunityPharmacy;
import org.lamisplus.modules.lamis.legacy.domain.repositories.CommunityPharmacyRepository;

import java.util.List;

public interface CommunityPharmacyRepository1 extends CommunityPharmacyRepository {
    List<CommunityPharmacy> findByPin(String pin);
}
