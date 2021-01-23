package org.fhi360.module.Hts.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fhi360.module.Hts.domain.entities.DeviceConfig;
import org.fhi360.module.Hts.domain.repositories.AssessmentRepository1;
import org.fhi360.module.Hts.domain.repositories.CamTeamRepository;
import org.fhi360.module.Hts.domain.repositories.DeviceConfigRepository1;
import org.fhi360.module.Hts.dto.*;
import org.lamisplus.modules.base.domain.entities.State;
import org.lamisplus.modules.base.domain.repositories.ProvinceRepository;
import org.lamisplus.modules.base.domain.repositories.StateRepository;
import org.lamisplus.modules.lamis.legacy.domain.entities.Assessment;
import org.lamisplus.modules.lamis.legacy.domain.entities.IndexContact;
import org.lamisplus.modules.lamis.legacy.domain.repositories.HtsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class HtsService {
    private final HtsRepository htsRepository;
    private final StateRepository stateRepository;
    private final AssessmentRepository1 assessmentRepository;
    private final DeviceConfigRepository1 deviceConfigRepository;
    private final ProvinceRepository provinceRepository;
    private final JdbcTemplate jdbcTemplate;
    private final CamTeamRepository camTeamRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private XSSFSheet sheet;
    private final XSSFWorkbook workbook = new XSSFWorkbook();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    Logger logger = LoggerFactory.getLogger(HtsService.class);

    public ResponseDTO saveAssessment(List<Assessment> assessmentList) {
        ResponseDTO response = new ResponseDTO();
        try {
            assessmentList.forEach(assessment -> {
                Assessment assessmentId = this.assessmentRepository.findByClientCodeAndFacility(assessment.getClientCode(), assessment.getFacility());
                if (assessment.getDateVisit() != null) {
                    assessment.setDateVisit(assessment.getDateVisit());
                }
                if (assessmentId != null) {
                    assessment.setId(assessmentId.getId());
                    this.assessmentRepository.save(assessment);
                } else {
                    int count = this.assessmentRepository.maxAssessmentId();
                    count++;
                    assessment.setId((long) count);
                    assessment.setUuid(UUID.randomUUID().toString());
                    this.assessmentRepository.save(assessment);
                }
                logger.info("ASSESSMENT SAVING...");
            });
            response.setMessage("Assessment success");
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        return response;

    }

    public ResponseDTO saveHts(List<HTSInsertDTO> htsList) throws RuntimeException {
        ResponseDTO response = new ResponseDTO();
        try {
            htsList.forEach(htsList1 -> {
                Long htsId1 = this.getHtsId(htsList1.getFacilityId(), htsList1.getClientCode());
                if (!StringUtils.isBlank(htsList1.getDateVisit()) || !StringUtils.isBlank(htsList1.getDateBirth())) {
                    if (htsId1 != null) {
                        updateHts(htsId1,
                                htsList1.getAssessmentId(),
                                htsList1.getStateId(),
                                htsList1.getCamCode(),
                                htsList1.getLgaId(),
                                htsList1.getFacilityId(),
                                htsList1.getFacilityName(),
                                htsList1.getDateVisit() == null ? "" : htsList1.getDateVisit(),
                                htsList1.getClientCode(),
                                htsList1.getHospitalNum(),
                                htsList1.getReferredFrom(),
                                htsList1.getTestingSetting(),
                                htsList1.getSurname(),
                                htsList1.getOtherNames(),
                                htsList1.getDateBirth(),
                                htsList1.getPhone(),
                                htsList1.getAddress(),
                                htsList1.getGender(),
                                htsList1.getFirstTimeVisit(),
                                htsList1.getMaritalStatus(),
                                htsList1.getNumChildren(),
                                htsList1.getNumWives(),
                                htsList1.getTypeCounseling(),
                                htsList1.getIndexClient(),
                                htsList1.getTypeIndex(),
                                htsList1.getIndexClientCode(),
                                htsList1.getKnowledgeAssessment1(),
                                htsList1.getKnowledgeAssessment2(),
                                htsList1.getKnowledgeAssessment3(),
                                htsList1.getKnowledgeAssessment4(),
                                htsList1.getKnowledgeAssessment5(),
                                htsList1.getKnowledgeAssessment6(),
                                htsList1.getKnowledgeAssessment7(),
                                htsList1.getRiskAssessment1(),
                                htsList1.getRiskAssessment2(),
                                htsList1.getRiskAssessment3(),
                                htsList1.getRiskAssessment4(),
                                htsList1.getRiskAssessment5(),
                                htsList1.getRiskAssessment6(),
                                htsList1.getTbScreening1(),
                                htsList1.getTbScreening2(),
                                htsList1.getTbScreening3(),
                                htsList1.getTbScreening4(),
                                htsList1.getStiScreening1(),
                                htsList1.getStiScreening2(),
                                htsList1.getStiScreening3(),
                                htsList1.getStiScreening4(),
                                htsList1.getStiScreening5(),
                                htsList1.getHivTestResult(),
                                htsList1.getTestedHiv(),
                                htsList1.getPostTest1(),
                                htsList1.getPostTest2(),
                                htsList1.getPostTest3(),
                                htsList1.getPostTest4(),
                                htsList1.getPostTest5(),
                                htsList1.getPostTest6(),
                                htsList1.getPostTest7(),
                                htsList1.getPostTest8(),
                                htsList1.getPostTest9(),
                                htsList1.getPostTest10(),
                                htsList1.getPostTest11(),
                                htsList1.getPostTest12(),
                                htsList1.getPostTest13(),
                                htsList1.getPostTest14(),
                                htsList1.getSyphilisTestResult(),
                                htsList1.getHepatitisbTestResult(),
                                htsList1.getHepatitiscTestResult(),
                                htsList1.getArtReferred(),
                                htsList1.getTbReferred(),
                                htsList1.getStiReferred(),
                                htsList1.getLatitude(),
                                htsList1.getLongitude()
                                );
                    } else {
                        saveHts1(
                                htsList1.getAssessmentId(),
                                htsList1.getStateId(),
                                htsList1.getCamCode(),
                                htsList1.getLgaId(),
                                htsList1.getFacilityId(),
                                htsList1.getFacilityName(),
                                htsList1.getDateVisit(),
                                htsList1.getClientCode(),
                                htsList1.getHospitalNum(),
                                htsList1.getReferredFrom(),
                                htsList1.getTestingSetting(),
                                htsList1.getSurname(),
                                htsList1.getOtherNames(),
                                htsList1.getDateBirth(),
                                htsList1.getAgeUnit(),
                                htsList1.getPhone(),
                                htsList1.getAddress(),
                                htsList1.getGender(),
                                htsList1.getFirstTimeVisit(),
                                htsList1.getMaritalStatus(),
                                htsList1.getNumChildren(),
                                htsList1.getNumWives(),
                                htsList1.getTypeCounseling(),
                                htsList1.getIndexClient(),
                                htsList1.getTypeIndex(),
                                htsList1.getIndexClientCode(),
                                htsList1.getKnowledgeAssessment1(),
                                htsList1.getKnowledgeAssessment2(),
                                htsList1.getKnowledgeAssessment3(),
                                htsList1.getKnowledgeAssessment4(),
                                htsList1.getKnowledgeAssessment5(),
                                htsList1.getKnowledgeAssessment6(),
                                htsList1.getKnowledgeAssessment7(),
                                htsList1.getRiskAssessment1(),
                                htsList1.getRiskAssessment2(),
                                htsList1.getRiskAssessment3(),
                                htsList1.getRiskAssessment4(),
                                htsList1.getRiskAssessment5(),
                                htsList1.getRiskAssessment6(),
                                htsList1.getTbScreening1(),
                                htsList1.getTbScreening2(),
                                htsList1.getTbScreening3(),
                                htsList1.getTbScreening4(),
                                htsList1.getStiScreening1(),
                                htsList1.getStiScreening2(),
                                htsList1.getStiScreening3(),
                                htsList1.getStiScreening4(),
                                htsList1.getStiScreening5(),
                                htsList1.getHivTestResult(),
                                htsList1.getTestedHiv(),
                                htsList1.getPostTest1(),
                                htsList1.getPostTest2(),
                                htsList1.getPostTest3(),
                                htsList1.getPostTest4(),
                                htsList1.getPostTest5(),
                                htsList1.getPostTest6(),
                                htsList1.getPostTest7(),
                                htsList1.getPostTest8(),
                                htsList1.getPostTest9(),
                                htsList1.getPostTest10(),
                                htsList1.getPostTest11(),
                                htsList1.getPostTest12(),
                                htsList1.getPostTest13(),
                                htsList1.getPostTest14(),
                                htsList1.getSyphilisTestResult(),
                                htsList1.getHepatitisbTestResult(),
                                htsList1.getHepatitiscTestResult(),
                                htsList1.getArtReferred(),
                                htsList1.getTbReferred(),
                                htsList1.getStiReferred(),
                                htsList1.getLatitude(),
                                htsList1.getLongitude());}
                }
            });
            response.setMessage("Hts success");
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
            throw new RuntimeException(e.getLocalizedMessage());
        }
        return response;
    }

    public ResponseDTO saveRecency(List<Recencies> recencies) {
        ResponseDTO response = new ResponseDTO();
        recencies.forEach(recencies1 -> {
            Long exist = checkIfRecency(recencies1.getRecencyNumber(), recencies1.getHtsId());
            if (exist != null) {
                updateRecency(exist, recencies1.getRecencyNumber(), recencies1.getHtsId(), recencies1.getTestName(), recencies1.getTestDate(), recencies1.getControlLine(), recencies1.getVerificationLine(), recencies1.getLongTimeLine(), recencies1.getRecencyInterpretation());
            } else {
                saveRecency(recencies1.getRecencyNumber(), recencies1.getHtsId(), recencies1.getTestName(), recencies1.getTestDate(), recencies1.getControlLine(), recencies1.getVerificationLine(), recencies1.getLongTimeLine(), recencies1.getRecencyInterpretation());

            }
        });
        response.setMessage("RECENCY success");
        return response;
    }

    public ResponseDTO saveIndexContact(List<IndexContact> indexcontactList) {
        ResponseDTO response = new ResponseDTO();
        try {
//            for (IndexContact indexcontactDto : indexcontactList) {
//                IndexContact indexContact = new IndexContact();
//                Hts htsId = this.htsRepository.findByClientCodeAndFacilityId(indexcontactDto.getClientCode(), indexcontactDto.getFacilityId());
//                Hts hts = new Hts();
//                hts.setHtsId(htsId.getHtsId());
//                indexContact.setHts(hts);
//                indexContact.setFacilityId(indexcontactDto.getFacilityId());
//                indexContact.setContactType(indexcontactDto.getContactType());
//                indexContact.setDeviceconfigId(indexcontactDto.getDeviceconfigId());
//                if (indexcontactDto.getIndexContactCode() != null) {
//                    indexContact.setIndexContactCode(indexcontactDto.getIndexContactCode());
//                }
//                indexContact.setClientCode(indexcontactDto.getClientCode());
//                indexContact.setSurname(indexcontactDto.getSurname());
//                indexContact.setOtherNames(indexcontactDto.getOtherNames());
//                indexContact.setAge(indexcontactDto.getAge());
//                indexContact.setGender(indexcontactDto.getGender());
//                indexContact.setAddress(indexcontactDto.getAddress());
//                indexContact.setPhone(indexcontactDto.getPhone());
//                indexContact.setRelationship(indexcontactDto.getRelationship());
//                indexContact.setGbv(indexcontactDto.getGbv());
//                indexContact.setDurationPartner(indexcontactDto.getDurationPartner());
//                indexContact.setPhoneTracking(indexcontactDto.getPhoneTracking());
//                indexContact.setHomeTracking(indexcontactDto.getHomeTracking());
//                indexContact.setOutcome(indexcontactDto.getOutcome());
//                indexContact.setHivStatus(indexcontactDto.getHivStatus());
//                indexContact.setLinkCare(indexcontactDto.getLinkCare());
//                indexContact.setPartnerNotification(indexcontactDto.getPartnerNotification());
//                indexContact.setModeNotification(indexcontactDto.getModeNotification());
//                indexContact.setServiceProvided(indexcontactDto.getServiceProvided());
//                if (!indexcontactDto.getDateHivTest().equals("")) {
//                    indexContact.setDateHivTest1(LocalDate.parse(indexcontactDto.getDateHivTest(), formatter));
//                }
//
//                IndexContact indexContactId = this.indexContactRepository.findByIndexContactCodeAndFacilityId(indexcontactDto.getIndexContactCode(), indexcontactDto.getFacilityId());
//                if (indexContactId != null) {
//                    indexContact.setIndexcontactId(indexContactId.getIndexcontactId());
//                    this.indexContactRepository.save(indexContact);
//                } else {
//                    this.indexContactRepository.save(indexContact);
//                }
//                logger.info("INDEXCONTACT SAVING...");
//            }
            response.setMessage("INDEXCONTACT success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    //select setval('indexcontact_indexcontact_id_seq'::regclass, 1000);
    public InitializeDTO initializationLAMISLite(String ip, Long facilityId, String deviceId, String username, String password) {
        List<Map<String, Object>> facilities = new ArrayList<>();
        Map<String, Object> obj = new HashMap<>();
        InitializeDTO response = new InitializeDTO();
        if (ip.equalsIgnoreCase("fhi360") || ip.equalsIgnoreCase("gf") || ip.equalsIgnoreCase("jhpiego")) {
            DeviceConfig deviceconfigId = this.deviceConfigRepository.findByDeviceId(deviceId);

            if (!isDeviceExistExists(deviceId)) {
                saveDeviceConfig(facilityId, deviceId, username, password);
            } else {
                updateDeviceConfig(deviceconfigId.getId(), facilityId, deviceId, username, password);
            }

            try {
                response.setCamTeams(camTeamRepository.findByFacilityId(facilityId));
                response.setStates(this.stateRepository.findAll());
                response.setLgas(this.provinceRepository.findAll());

                try {
                    facilities.add(jdbcTemplate.queryForMap("SELECT f.id AS facilityId , f.name, l.id AS lgaId, s.id AS stateId," +
                            " (select deviceconfig_id AS deviceconfigId from deviceconfig where device_id = '" + deviceId + "' ) " +
                            "FROM facility f JOIN lga l ON f.lga_id = l.id JOIN state s ON f.state_id = s.id  WHERE f.id = ?", facilityId));
                    response.setFacilities(facilities);

                } catch (Exception e) {

                }


                String query = "SELECT r.id , r.description regimen, t.id regimen_type_id, t.description regimen_type FROM regimen r JOIN " +
                        "regimen_type t ON regimen_type_id = t.id";
                List<RegimensDTO> regimensList = new ArrayList<>();
                jdbcTemplate.query(query, resultSet -> {
                    while (resultSet.next()) {
                        RegimensDTO regimens = new RegimensDTO();
                        regimens.setRegimenId(resultSet.getString("id"));
                        regimens.setRegimen(resultSet.getString("regimen"));
                        regimens.setRegimentypeId(resultSet.getString("regimen_type_id"));
                        regimens.setRegimentype(resultSet.getString("regimen_type"));
                        regimensList.add(regimens);
                    }
                    logger.info("INITIALIZING COMPLETED...");
                    response.setRegimens(regimensList);
                    return null;
                });
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }

        }
        return response;
    }

    public State getHtsByStateId(long stateId) {
        return this.stateRepository.getOne(stateId);
    }

    public void saveRecency(String recencyNumber, Long htsId, String testName, String testDate, Long controlLine, Long verificationLine, Long longTimeLine, String recencyInterpretation) {
        jdbcTemplate.update("insert into recency " +
                "(participant_id ,hts_id, test_name,test_date,control_line,verification_line,long_term_line,recency_interpretation,uuid)" +
                " values (?, ?,?,?,?,?,?,?,?)", recencyNumber, htsId, testName, testDate, controlLine, verificationLine, longTimeLine, recencyInterpretation, UUID.randomUUID().toString());
    }


    public void updateRecency(Long id, String recencyNumber, Long htsId, String testName, String testDate, Long controlLine, Long verificationLine, Long longTimeLine, String recencyInterpretation) {
        jdbcTemplate.update("update recency set participant_id = ? ,hts_id =? , test_name =?,test_date = ? ,control_line = ? ,verification_line = ? ,long_term_line = ? ,recency_interpretation =? where id = ?", recencyNumber, htsId, testName, testDate, controlLine, verificationLine, longTimeLine, recencyInterpretation, id);
    }

    public void saveDeviceConfig(Long facilityId, String deviceId, String username, String password) {
        jdbcTemplate.update("insert into deviceconfig (facility_id ,device_id, username, password) values (?, ?,?,?)", facilityId, deviceId, username, password);
    }

    public void updateDeviceConfig(Long deviceConfigId, Long facilityId, String deviceId, String username, String password) {
        jdbcTemplate.update("update deviceconfig set facility_id = ? , device_id = ?, username = ?, password = ? where deviceconfig_id = ? ", facilityId, deviceId, username, password, deviceConfigId);
    }


    private boolean isDeviceExistExists(String deviceId) {
        String sql = "SELECT count(*) FROM deviceconfig WHERE device_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[]{deviceId}, Integer.class);
        return count > 0;
    }


    private Long checkIfRecency(String recencyNumber, Long htsId) {
        String sql = "SELECT id FROM recency WHERE participant_id = ? and hts_id =? ";
        List<Long> ids = jdbcTemplate.queryForList(sql, Long.class, recencyNumber, htsId);
        if (ids.size() > 0) {
            return ids.get(0);
        }
        return null;
    }

    private Long getHtsId(Long facilityId, String clientCode) {
        String sql = "SELECT id FROM hts WHERE facility_id = ? and client_code =? ";
        List<Long> ids = jdbcTemplate.queryForList(sql, Long.class, facilityId, clientCode);
        if (ids.size() > 0) {
            return ids.get(0);
        }
        return null;
    }

    public void saveHts1(long assessmentId,
                         Long stateId,
                         String camCode,
                         Long lgaId,
                         Long facilityId,
                         String facilityName,
                         String dateVisit,
                         String clientCode,
                         String hospitalNum,
                         String referredFrom,
                         String testingSetting,
                         String surname,
                         String otherNames,
                         String dateBirth,
                         String ageUnit,
                         String phone,
                         String address,
                         String gender,
                         String firstTimeVisit,
                         String maritalStatus,
                         Integer numChildren,
                         Integer numWives,
                         String typeCounseling,
                         String indexClient,
                         String typeIndex,
                         String indexClientCode,
                         Integer knowledgeAssessment1,
                         Integer knowledgeAssessment2,
                         Integer knowledgeAssessment3,
                         Integer knowledgeAssessment4,
                         Integer knowledgeAssessment5,
                         Integer knowledgeAssessment6,
                         Integer knowledgeAssessment7,
                         Integer riskAssessment1,
                         Integer riskAssessment2,
                         Integer riskAssessment3,
                         Integer riskAssessment4,
                         Integer riskAssessment5,
                         Integer riskAssessment6,
                         Integer tbScreening1,
                         Integer tbScreening2,
                         Integer tbScreening3,
                         Integer tbScreening4,
                         Integer stiScreening1,
                         Integer stiScreening2,
                         Integer stiScreening3,
                         Integer stiScreening4,
                         Integer stiScreening5,
                         String hivTestResult,
                         String testedHiv,
                         Integer postTest1,
                         Integer postTest2,
                         Integer postTest3,
                         Integer postTest4,
                         Integer postTest5,
                         Integer postTest6,
                         Integer postTest7,
                         Integer postTest8,
                         Integer postTest9,
                         Integer postTest10,
                         Integer postTest11,
                         Integer postTest12,
                         Integer postTest13,
                         Integer postTest14,
                         String syphilisTestResult,
                         String hepatitisbTestResult,
                         String hepatitiscTestResult,
                         String artReferred,
                         String tbReferred,
                         String stiReferred,
                         float latitude,
                         float longitude) {
        jdbcTemplate.update("insert into hts " +
                        "(ASSESSMENT_ID,STATE_ID," +
                        "LGA_ID,FACILITY_ID,FACILITY_NAME," +
                        "CAM_CODE,CLIENT_CODE ,HOSPITAL_NUM," +
                        " DATE_VISIT, REFERRED_FROM,TESTING_SETTING," +
                        "SURNAME,OTHER_NAMES,DATE_BIRTH,PHONE,ADDRESS," +
                        "GENDER,FIRST_TIME_VISIT,MARITAL_STATUS," +
                        "NUM_CHILDREN,NUM_WIVES," +
                        "TYPE_COUNSELING,INDEX_CLIENT,TYPE_INDEX," +
                        "INDEX_CLIENT_CODE," +
                        "KNOWLEDGE_ASSESSMENT1," +
                        "KNOWLEDGE_ASSESSMENT2,KNOWLEDGE_ASSESSMENT3," +
                        "KNOWLEDGE_ASSESSMENT4," +
                        "KNOWLEDGE_ASSESSMENT5,KNOWLEDGE_ASSESSMENT6," +
                        "KNOWLEDGE_ASSESSMENT7," +
                        "RISK_ASSESSMENT1,RISK_ASSESSMENT2,RISK_ASSESSMENT3," +
                        "RISK_ASSESSMENT4,RISK_ASSESSMENT5,RISK_ASSESSMENT6," +
                        "TB_SCREENING1,TB_SCREENING2,TB_SCREENING3,TB_SCREENING4," +
                        "STI_SCREENING1,STI_SCREENING2,STI_SCREENING3," +
                        "STI_SCREENING4,STI_SCREENING5,HIV_TEST_RESULT,TESTED_HIV,POST_TEST1," +
                        "POST_TEST2,POST_TEST3,POST_TEST4,POST_TEST5,POST_TEST6," +
                        "POST_TEST7,POST_TEST8,POST_TEST9,POST_TEST10,POST_TEST11," +
                        "POST_TEST12,POST_TEST13,POST_TEST14,SYPHILIS_TEST_RESULT," +
                        "HEPATITISB_TEST_RESULT,HEPATITISC_TEST_RESULT,STI_REFERRED," +
                        "TB_REFERRED,ART_REFERRED,LONGITUDE,LATITUDE,uuid) " +
                        "values" +
                        " (?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
                        ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
                        ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",

                assessmentId, stateId, lgaId, facilityId, facilityName, camCode,
                clientCode, hospitalNum, dateVisit, referredFrom,
                testingSetting, surname, otherNames, dateBirth, phone, address, gender,
                firstTimeVisit, maritalStatus, numChildren, numWives, typeCounseling, indexClient, typeIndex, indexClientCode,
                knowledgeAssessment1, knowledgeAssessment2, knowledgeAssessment3, knowledgeAssessment4, knowledgeAssessment5,
                knowledgeAssessment6, knowledgeAssessment7,
                riskAssessment1, riskAssessment2, riskAssessment3, riskAssessment4, riskAssessment5, riskAssessment6, tbScreening1,
                tbScreening2, tbScreening3, tbScreening4,
                stiScreening1, stiScreening2, stiScreening3, stiScreening4, stiScreening5, hivTestResult, testedHiv, postTest1,
                postTest2, postTest3, postTest4, postTest5, postTest6, postTest7, postTest8, postTest9,
                postTest10, postTest11, postTest12, postTest13, postTest14, syphilisTestResult,
                hepatitisbTestResult, hepatitiscTestResult, stiReferred, tbReferred, artReferred, longitude, latitude,
                UUID.randomUUID().toString());
    }


    public void updateHts(long htsId,
                          long assessmentId,
                          Long stateId,
                          String camCode,
                          Long lgaId,
                          Long facilityId,
                          String facilityName,
                          String dateVisit,
                          String clientCode,
                          String hospitalNum,
                          String referredFrom,
                          String testingSetting,
                          String surname,
                          String otherNames,
                          String dateBirth,
                          String phone,
                          String address,
                          String gender,
                          String firstTimeVisit,
                          String maritalStatus,
                          Integer numChildren,
                          Integer numWives,
                          String typeCounseling,
                          String indexClient,
                          String typeIndex,
                          String indexClientCode,
                          Integer knowledgeAssessment1,
                          Integer knowledgeAssessment2,
                          Integer knowledgeAssessment3,
                          Integer knowledgeAssessment4,
                          Integer knowledgeAssessment5,
                          Integer knowledgeAssessment6,
                          Integer knowledgeAssessment7,
                          Integer riskAssessment1,
                          Integer riskAssessment2,
                          Integer riskAssessment3,
                          Integer riskAssessment4,
                          Integer riskAssessment5,
                          Integer riskAssessment6,
                          Integer tbScreening1,
                          Integer tbScreening2,
                          Integer tbScreening3,
                          Integer tbScreening4,
                          Integer stiScreening1,
                          Integer stiScreening2,
                          Integer stiScreening3,
                          Integer stiScreening4,
                          Integer stiScreening5,
                          String hivTestResult,
                          String testedHiv,
                          Integer postTest1,
                          Integer postTest2,
                          Integer postTest3,
                          Integer postTest4,
                          Integer postTest5,
                          Integer postTest6,
                          Integer postTest7,
                          Integer postTest8,
                          Integer postTest9,
                          Integer postTest10,
                          Integer postTest11,
                          Integer postTest12,
                          Integer postTest13,
                          Integer postTest14,
                          String syphilisTestResult,
                          String hepatitisbTestResult,
                          String hepatitiscTestResult,
                          String artReferred,
                          String tbReferred,
                          String stiReferred,
                          float latitude,
                          float longitude) {
        jdbcTemplate.update("update hts set ASSESSMENT_ID =?,STATE_ID =? ,LGA_ID =? ,FACILITY_ID =?," +
                        "FACILITY_NAME =?,CAM_CODE =?,CLIENT_CODE =? ,HOSPITAL_NUM =?," +
                        " DATE_VISIT =?, REFERRED_FROM =?,TESTING_SETTING =?," +
                        "SURNAME =?,OTHER_NAMES =?,DATE_BIRTH =? ,PHONE =? ,ADDRESS =? ," +
                        "GENDER =?,FIRST_TIME_VISIT =?,MARITAL_STATUS =?,NUM_CHILDREN =?,NUM_WIVES =?," +
                        "TYPE_COUNSELING =?,INDEX_CLIENT =?,TYPE_INDEX =?,INDEX_CLIENT_CODE =?," +
                        "KNOWLEDGE_ASSESSMENT1 =?," +
                        "KNOWLEDGE_ASSESSMENT2 =?,KNOWLEDGE_ASSESSMENT3 =?," +
                        "KNOWLEDGE_ASSESSMENT4 =?," +
                        "KNOWLEDGE_ASSESSMENT5 =? ,KNOWLEDGE_ASSESSMENT6 =? ," +
                        "KNOWLEDGE_ASSESSMENT7 =? ," +
                        "RISK_ASSESSMENT1 =? ,RISK_ASSESSMENT2 =? ,RISK_ASSESSMENT3 =?," +
                        "RISK_ASSESSMENT4 =? ,RISK_ASSESSMENT5 =? ,RISK_ASSESSMENT6 =? ," +
                        "TB_SCREENING1 =? ,TB_SCREENING2 =? ,TB_SCREENING3 =? ,TB_SCREENING4 =?," +
                        "STI_SCREENING1 =? ,STI_SCREENING2 =? ,STI_SCREENING3 =? ," +
                        "STI_SCREENING4 =? ,STI_SCREENING5 =? ,HIV_TEST_RESULT =? ,TESTED_HIV =? ,POST_TEST1 =? ," +
                        "POST_TEST2 =? ,POST_TEST3 =? ,POST_TEST4 =? ,POST_TEST5 =? ,POST_TEST6 =? ," +
                        "POST_TEST7 =? ,POST_TEST8 =? ,POST_TEST9 =? ,POST_TEST10 =? ,POST_TEST11 =? ," +
                        "POST_TEST12 =? ,POST_TEST13 =? ,POST_TEST14 =? ,SYPHILIS_TEST_RESULT =? ," +
                        "HEPATITISB_TEST_RESULT =? ,HEPATITISC_TEST_RESULT =? ,STI_REFERRED =? ," +
                        "TB_REFERRED =? ,ART_REFERRED =? ,LONGITUDE =? ,LATITUDE =?  where id = ? ",

                assessmentId, stateId, lgaId, facilityId, facilityName, camCode,
                clientCode, hospitalNum, dateVisit, referredFrom,
                testingSetting, surname, otherNames, dateBirth, phone, address, gender,
                firstTimeVisit, maritalStatus, numChildren, numWives, typeCounseling, indexClient, typeIndex, indexClientCode,
                knowledgeAssessment1, knowledgeAssessment2, knowledgeAssessment3, knowledgeAssessment4, knowledgeAssessment5,
                knowledgeAssessment6, knowledgeAssessment7,
                riskAssessment1, riskAssessment2, riskAssessment3, riskAssessment4, riskAssessment5, riskAssessment6, tbScreening1,
                tbScreening2, tbScreening3, tbScreening4,
                stiScreening1, stiScreening2, stiScreening3, stiScreening4, stiScreening5, hivTestResult, testedHiv, postTest1,
                postTest2, postTest3, postTest4, postTest5, postTest6, postTest7, postTest8, postTest9,
                postTest10, postTest11, postTest12, postTest13, postTest14, syphilisTestResult,
                hepatitisbTestResult, hepatitiscTestResult, stiReferred, tbReferred, artReferred, longitude, latitude, htsId);

    }

    public ByteArrayOutputStream htsReport(Long stateId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Workbook workbook = new SXSSFWorkbook(100);  // turn off auto-flushing and accumulate all rows in memory
            Sheet sheet = workbook.createSheet();
            int[] rowNum = {0};
            int[] cellNum = {0};
            Row[] row = {sheet.createRow(rowNum[0]++)};
            Cell[] cell = {row[0].createCell(cellNum[0]++)};
            cell[0].setCellValue("UUID");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("CAM_location");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("CAM_Team_Codes");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Date_of_Survey");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("State");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("LGA");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Facility_Name");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Latitude_Testing_Point");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Longitude_Testing_Point");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Client_ID");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Client_Age");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Client_Sex");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Client_Marital_Status");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Client_ICT_Type");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("TestMod");
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("HIV_Test_Result");
            String query = "select h.uuid,h.cam_code, (select camteam from camteam c where c.cam_code = h.cam_code) , h.date_visit, (select name as state from state s where s.id = h.state_id) , (select name as lga from lga l where l.id = h.lga_id),h.facility_name,h.longitude,h.latitude,h.id,h.date_birth,h.gender,h.marital_status,h.type_index,h.testing_setting,h.hiv_test_result from hts h where h.state_id = ? and h.cam_code is not null";
            jdbcTemplate.query(query, entry -> {
                while (entry.next()) {
                    cellNum[0] = 0;
                    row[0] = sheet.createRow(rowNum[0]++);
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getString("uuid"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getString("camteam"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getString("cam_code"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getString("date_visit"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getString("state"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getString("lga"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getString("facility_name"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getFloat("latitude"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getFloat("longitude"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue("CAM_CLIENT_" + entry.getLong("id"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(getAge(entry.getString("date_birth")));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getString("gender"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getString("marital_status"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getString("type_index"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getString("testing_setting"));
                    cell[0] = row[0].createCell(cellNum[0]++);
                    cell[0].setCellValue(entry.getString("hiv_test_result"));
                }
                return null;
            }, stateId);
            try {
                workbook.write(baos);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos;
    }

    public int getAge(String dateBirth) {
        LocalDate currentDate = LocalDate.parse(dateBirth);
        LocalDate pdate = LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth());
        LocalDate now = LocalDate.now();
        Period diff = Period.between(pdate, now);
        return diff.getYears();
    }
//0640.0567
    //1297.0187

}
