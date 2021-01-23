package org.fhi360.module.Hts.web;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fhi360.module.Hts.dto.EncounterDTO;
import org.fhi360.module.Hts.service.CPARPService;
import org.lamisplus.modules.lamis.legacy.domain.entities.Devolve;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RequestMapping("/resources/webservice")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CPARPResource {
    private final CPARPService cparpService;

    //localhost:8080/resources/webservice/mobile/activate/fhi360/53/15155525525/1/1
    @GetMapping("/mobile/activate/{userName}/{pin}/{deviceId}/{accountUserName}/{accountPassword}")
    public Map<String, Object> getActivationData(@PathVariable("userName") String ip, @PathVariable("pin") String pin, @PathVariable("deviceId") String deviceId, @PathVariable("accountUserName") String username, @PathVariable("accountPassword") String password) {
        return cparpService.getActivationData(ip, pin, deviceId, username, password);
    }

    //Download newly devolved patients from server
    @GetMapping("/mobile/devolved/{pin}")
    public Map<String, Object> syncGetJsonMobile(@PathVariable("pin") String pin)
    {
        return cparpService.getPatient(pin);
    }

    @PostMapping("/mobile/syncs/{table}")
    public void syncJsonMobile(@RequestBody List<EncounterDTO> encounterDTOList, @PathVariable("table") String table)
    {
        cparpService.sync(encounterDTOList, table);
    }

    @PostMapping("/mobile/syncs/devolves")
    public void syncJsonMobile(@RequestBody List<Devolve> devolves) {

    }

}
