package org.fhi360.module.Hts.util;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fhi360.module.Hts.domain.entities.CamTeam;
import org.fhi360.module.Hts.domain.repositories.CamTeamRepository;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
@Component
public class Import {

    private  final CamTeamRepository camTeamRepository;

    public Import(CamTeamRepository camTeamRepository) {
        this.camTeamRepository = camTeamRepository;
    }

    public  void importData() {
        try {
          FileInputStream input = new FileInputStream(new File("camteams.xlsx"));
            XSSFWorkbook wb = new XSSFWorkbook(input);
            XSSFSheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                DataFormatter formatter = new DataFormatter();
                CamTeam camTeam = new CamTeam();
                camTeam.setId(Long.parseLong(formatter.formatCellValue(sheet.getRow(i).getCell(0))));
                camTeam.setFacilityId(Long.parseLong(formatter.formatCellValue(sheet.getRow(i).getCell(1))));
                camTeam.setCamteam(formatter.formatCellValue(sheet.getRow(i).getCell(2)));
                camTeam.setCamCode(formatter.formatCellValue(sheet.getRow(i).getCell(3)));
                camTeamRepository.save(camTeam);
            }

            input.close();
            System.out.println("Success import excel to mysql table");
        } catch ( IOException e) {
            System.out.println(e);
        }
    }

}
