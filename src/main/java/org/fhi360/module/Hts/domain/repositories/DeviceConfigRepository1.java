package org.fhi360.module.Hts.domain.repositories;

import org.fhi360.module.Hts.domain.entities.DeviceConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DeviceConfigRepository1 extends JpaRepository<DeviceConfig, Long> {
    DeviceConfig findByDeviceId(String deviceConfig);

    @Query(value = "SELECT count(*) FROM deviceconfig", nativeQuery = true)
    int masDeviceConfigId();

}
