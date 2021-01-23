
package org.fhi360.module.Hts.dto;

import lombok.Data;
import org.lamisplus.modules.lamis.legacy.domain.entities.Assessment;

import java.util.List;

@Data
public class AssessmentDTO {
    private List<Assessment> assessment = null;
}
