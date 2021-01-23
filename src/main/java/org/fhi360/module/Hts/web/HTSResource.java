package org.fhi360.module.Hts.web;
import lombok.RequiredArgsConstructor;
import org.fhi360.module.Hts.dto.*;
import org.fhi360.module.Hts.service.HtsService;
import org.lamisplus.modules.base.domain.entities.State;
import org.lamisplus.modules.base.domain.repositories.StateRepository;
import org.lamisplus.modules.lamis.legacy.domain.entities.Assessment;
import org.lamisplus.modules.lamis.legacy.domain.entities.IndexContact;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/resources/hts")
@RequiredArgsConstructor
public class HTSResource {
    private final StateRepository stateRepository;
    private final HtsService htsService;

    @GetMapping("/hts-report/{id}")
    public void htsReport(HttpServletResponse response, @PathVariable("id") Long id) throws IOException {
        ByteArrayOutputStream baos = htsService.htsReport(id);
        setStream(baos, response);
    }

    private void setStream(ByteArrayOutputStream baos, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename=CAM_Report.xlsx");
        response.setHeader("Content-Length", Integer.valueOf(baos.size()).toString());
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(baos.toByteArray());
        outputStream.close();
        response.flushBuffer();
    }

    @GetMapping("/state")
    public ResponseEntity<List<State>> getState() {
        return ResponseEntity.ok(this.stateRepository.findAll());
    }

    @GetMapping("/mobile/initialize/{userName}/{facilityId}/{deviceId}/{accountUserName}/{accountPassword}")
    public ResponseEntity<InitializeDTO> getInitializationData(@PathVariable("userName") String ip, @PathVariable("facilityId") Long facilityId, @PathVariable("deviceId") String deviceId, @PathVariable("accountUserName") String username, @PathVariable("accountPassword") String password) {
        return ResponseEntity.ok(htsService.initializationLAMISLite(ip, facilityId, deviceId, username, password));
    }

    @PostMapping("/mobile/sync/assessment")
    public ResponseEntity<ResponseDTO> syncAssessment(@RequestBody AssessmentDTO assessment) {
        List<Assessment> assessmentsDto = assessment.getAssessment();
        return ResponseEntity.ok(htsService.saveAssessment(assessmentsDto));
    }

    @PostMapping("/mobile/sync/hts")
    public ResponseEntity<ResponseDTO> syncHts(@RequestBody HtsDTO hts) {
        List<HTSInsertDTO> htsListDto = hts.getHts();
        return ResponseEntity.ok(htsService.saveHts(htsListDto));
    }

    @PostMapping("/mobile/sync/recency")
    public ResponseEntity<ResponseDTO> syncRecency(@RequestBody RecencyDTO recencies) {
        List<Recencies> recency1 = recencies.getRecencies();
        return ResponseEntity.ok(htsService.saveRecency(recency1));
    }

    @PostMapping("/mobile/sync/indexcontact")
    public ResponseEntity<ResponseDTO> syncIndexcontact(@RequestBody IndexContactDTO indexcontact) {
        List<IndexContact> indexcontactDto = indexcontact.getIndexcontact();
        return ResponseEntity.ok(htsService.saveIndexContact(indexcontactDto));
    }
}
