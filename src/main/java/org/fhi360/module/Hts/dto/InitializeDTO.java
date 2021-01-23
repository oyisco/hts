package org.fhi360.module.Hts.dto;

import org.fhi360.module.Hts.domain.entities.CamTeam;
import org.lamisplus.modules.base.domain.entities.Province;
import org.lamisplus.modules.base.domain.entities.State;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@lombok.Data
public class InitializeDTO implements Serializable {
    private List<Map<String, Object>> facilities;
    private List<State> states;
    private List<Province> lgas;
    private List<RegimensDTO> regimens;
    private List<CamTeam> camTeams;
}
