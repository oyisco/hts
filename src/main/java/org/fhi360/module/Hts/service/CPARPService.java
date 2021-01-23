package org.fhi360.module.Hts.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fhi360.module.Hts.domain.entities.DeviceConfig;
import org.fhi360.module.Hts.domain.repositories.CommunityPharmacyRepository1;
import org.fhi360.module.Hts.domain.repositories.DeviceConfigRepository1;
import org.fhi360.module.Hts.dto.EncounterDTO;
import org.lamisplus.modules.lamis.legacy.domain.entities.CommunityPharmacy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CPARPService {
    private final DeviceConfigRepository1 deviceConfigRepository;
    private final CommunityPharmacyRepository1 communityPharmacyRepository;
    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> getActivationData(String ip, String pin, String deviceId, String username, String password) {
        Map<String, Object> obj = new HashMap<>();
        List<Map<String, Object>> facilities = new ArrayList<>();
        if (ip.equalsIgnoreCase("fhi360")) {
            List<CommunityPharmacy> communityPharmacy = this.communityPharmacyRepository.findByPin(pin);
            DeviceConfig deviceConfigId = this.deviceConfigRepository.findByDeviceId(deviceId);
            DeviceConfig deviceconfig = new DeviceConfig();
            List<Map<String, Object>> patients = new ArrayList<>();
            if (!communityPharmacy.isEmpty()) {
                deviceconfig.setDeviceId(deviceId);
                deviceconfig.setUsername(username);
                deviceconfig.setPassword(password);
                this.deviceConfigRepository.save(deviceconfig);
                communityPharmacy.forEach(cp -> {
                    try {
                        String query = "" +
                                "select * from ( " +
                                "   with active_devolves(facility_name, hospital_num, uuid, id, facility_id, unique_id,  " +
                                "       surname, other_names, date_birth, marital_status, status_at_registration, education, " +
                                "       occupation, date_confirmed_hiv, entry_point, address, phone, gender, date_started, " +
                                "       date_registration, age, lga, state, status, date_devolved) as ( " +
                                "           select f.name, hospital_num, p.uuid, p.id, p.facility_id, unique_id, surname,  " +
                                "           other_names, date_birth, marital_status, status_at_registration, education, occupation," +
                                "           date_confirmed_hiv, entry_point, address, phone, (case gender when 'MALE' then 'Male' else 'Female' end) gender,  " +
                                "           date_started, date_registration, datediff('year', date_birth, current_date) age, l.name," +
                                "           s.name,(select status from status_history where patient_id = d.patient_id and archived = false " +
                                "           order by date_status desc limit 1), date_devolved from devolve d join patient p on p.id = patient_id  join " +
                                "           facility f on f.id = p.facility_id left join lga l on l.id = p.lga_id left join state s on s.id = l.state_id " +
                                "           where d.community_pharmacy_id = ? and d.archived = false and p.archived = false and date_devolved <= current_date  " +
                                "           and date_returned_to_facility is null " +
                                "   )  " +
                                "   select *, rank()  over(partition by id order by date_devolved desc) _rank,  " +
                                "       max(date_devolved) over(partition by id order by date_devolved desc) date_devolved from active_devolves " +
                                ") devolves where _rank = 1 and status not in ('KNOWN_DEATH', 'ART_TRANSFER_OUT') order by other_names, surname";
                        List<Map<String, Object>> patient1 = jdbcTemplate.queryForList(query, cp.getId());
                        patient1 = patient1.stream()
                                .map(p -> {
                                    int patientId = (int) p.get("id");
                                    try {
                                        p.putAll(jdbcTemplate.queryForMap("" +
                                                "SELECT clinic_stage, date_visit date_last_clinic, next_appointment " +
                                                "   date_next_clinic FROM clinic WHERE patient_id = ? AND archived = false " +
                                                "   AND date_visit <= CURRENT_DATE ORDER BY date_visit DESC LIMIT 1", patientId));
                                    } catch (Exception ignored) {
                                    }
                                    try {
                                        p.putAll(jdbcTemplate.queryForMap("" +
                                                "SELECT jsonb_extract_path_text(l,'result') last_cd4, date_result_received date_last_cd4 FROM laboratory, " +
                                                "   jsonb_array_elements(lines) with ordinality a(l) WHERE patient_id = ? AND " +
                                                "   archived = false AND date_result_received <= CURRENT_DATE and " +
                                                "   (jsonb_extract_path_text(l,'lab_test_id'))::int = 1 ORDER BY date_result_received DESC LIMIT 1", patientId));
                                    } catch (Exception ignored) {
                                    }
                                    try {
                                        p.putAll(jdbcTemplate.queryForMap("" +
                                                "SELECT jsonb_extract_path_text(l,'result') last_viral_load, date_result_received date_last_viral_load, " +
                                                "   jsonb_extract_path_text(l,'indication') viral_load_type FROM laboratory, jsonb_array_elements(lines) " +
                                                "   with ordinality a(l) WHERE patient_id = ? AND archived = false AND date_result_received <= CURRENT_DATE and " +
                                                "   (jsonb_extract_path_text(l,'lab_test_id'))::int = 16 ORDER BY date_result_received DESC LIMIT 1", patientId));
                                    } catch (Exception ignored) {
                                    }
                                    try {
                                        p.putAll(jdbcTemplate.queryForMap("" +
                                                "SELECT date_visit date_last_refill, next_appointment date_next_refill, " +
                                                "   (jsonb_extract_path_text(l,'duration'))::int last_refill_duration, r.description regimen, " +
                                                "   t.description regimen_type FROM pharmacy p, jsonb_array_elements(lines) with ordinality a(l) " +
                                                "   join regimen r on r.id = (jsonb_extract_path_text(l,'regimen_id'))::int join regimen_type t " +
                                                "   on t.id = r.regimen_type_id WHERE t.id in (1,2,3,4,14) AND p.patient_id = ? AND date_visit <= CURRENT_DATE " +
                                                "   AND p.archived = false ORDER BY date_visit DESC, (jsonb_extract_path_text(l,'duration'))::int DESC LIMIT 1", patientId));
                                    } catch (Exception ignored) {
                                    }
                                    return p;
                                }).collect(Collectors.toList());
                        patients.addAll(patient1);


                        query = "with d as (" +
                                "   with active_devolves(id, facility_id, patient_id, date_devolved, dmoc_type, related_viral_load_id, " +
                                "       related_clinic_id, related_pharmacy_id, related_cd4_id, status) as (" +
                                "           select id, facility_id, patient_id, date_devolved, dmoc_type,related_viral_load_id, related_clinic_id," +
                                "               related_pharmacy_id, related_cd4_id, (select status from status_history where patient_id = d.patient_id " +
                                "               order by date_status desc limit 1) from devolve d where community_pharmacy_id = ? and archived = false " +
                                "               and date_returned_to_facility is null" +
                                "   ) " +
                                "   select *, rank()  over(partition by patient_id order by date_devolved desc) _rank,  " +
                                "       max(date_devolved) over(partition by patient_id order by date_devolved desc) date_devolved from active_devolves" +
                                ") " +
                                "SELECT distinct *, " +
                                "   (select jsonb_extract_path_text(l,'result') result from laboratory, jsonb_array_elements(lines) with ordinality a(l) where id = " +
                                "   related_viral_load_id and (jsonb_extract_path_text(l,'lab_test_id'))::int = 16 limit 1) last_viral_load," +
                                "   (select date_result_received from laboratory, jsonb_array_elements(lines) with ordinality a(l) where id = " +
                                "   related_viral_load_id and (jsonb_extract_path_text(l,'lab_test_id'))::int = 16 limit 1) date_last_viral_load," +
                                "   (select jsonb_extract_path_text(l,'result') result from laboratory, jsonb_array_elements(lines) with ordinality a(l) " +
                                "   where id = related_cd4_id and (jsonb_extract_path_text(l,'lab_test_id'))::int = 1 limit 1) last_cd4," +
                                "   (select date_result_received from laboratory, jsonb_array_elements(lines) with ordinality a(l) where " +
                                "   id = related_cd4_id and (jsonb_extract_path_text(l,'lab_test_id'))::int = 1 limit 1) date_last_cd4," +
                                "   (select clinic_stage from clinic where id = related_clinic_id) last_clinic_stage," +
                                "   (select date_visit from clinic where id = related_clinic_id) date_last_clinic_stage," +
                                "   (select r.description from pharmacy p, jsonb_array_elements(lines) with ordinality a(l) join regimen r " +
                                "   on r.id = (jsonb_extract_path_text(l,'regimen_id'))::int where p.id = related_pharmacy_id and r.regimen_type_id in (1, 2, 3, 4, 14) limit 1) regimen," +
                                "   (select r.description from pharmacy p, jsonb_array_elements(lines) with ordinality a(l) join regimen_type r " +
                                "   on r.id = (jsonb_extract_path_text(l,'regimen_type_id'))::int where p.id = related_pharmacy_id and " +
                                "   (jsonb_extract_path_text(l,'regimen_type_id'))::int in (1, 2, 3, 4, 14)  limit 1) regimen_type," +
                                "   (select date_visit from pharmacy where id = related_pharmacy_id limit 1) date_refill,facility_id " +
                                "from d where _rank = 1 and status not in ('KNOWN_DEATH', 'ART_TRANSFER_OUT')";
                        List<Map<String, Object>> devolves = jdbcTemplate.queryForList(query, cp.getId());

                        devolves.stream()
                                .map(d -> d.get("facility_id"))
                                .distinct()
                                .forEach(id -> {
                                    Long facilityId = (Long) id;
                                    facilities.add(jdbcTemplate.queryForMap("SELECT f.id, f.name, l.name AS lga, s.name AS " +
                                            "state FROM facility f JOIN lga l ON f.lga_id = l.id JOIN state s ON f.state_id = s.id WHERE f.id = ?", facilityId));
                                });

                        query = "SELECT r.id , r.description regimen, t.id regimen_type_id, t.description regimen_type FROM regimen r JOIN " +
                                "regimen_type t ON regimen_type_id = t.id";
                        List<Map<String, Object>> regimens = jdbcTemplate.queryForList(query);

                        query = "SELECT id, name, address, phone, email, pin FROM community_pharmacy WHERE id = ?";
                        List<Map<String, Object>> accounts = jdbcTemplate.queryForList(query, cp.getId());
                        obj.put("accounts", accounts);
                        obj.put("patients", patients);
                        obj.put("devolves", devolves);
                        obj.put("facilities", facilities);
                        obj.put("regimens", regimens);
                        updateLastSync(cp.getId());
                        LOG.debug("CPARP ACTIVATED: {}", cp.getId());

                    } catch (Exception exception) {

                    }
                });
            }
        }

        return obj;
    }


    private void updateLastSync(Long id) {
        try {
            String query = "UPDATE community_pharmacy SET last_modified = ? WHERE id = ?";
            jdbcTemplate.update(query, new Date(), id);
        } catch (Exception ignored) {
        }
    }


    public Map<String, Object> getPatient(String pin) {
        Map<String, Object> obj = new HashMap<>();
        List<Map<String, Object>> facilities = new ArrayList<>();
        List<CommunityPharmacy> communityPharmacy = this.communityPharmacyRepository.findByPin(pin);
        List<Map<String, Object>> patients = new ArrayList<>();
        if (!communityPharmacy.isEmpty()) {
            communityPharmacy.forEach(cp -> {
                try {
                    String query = "" +
                            "select * from ( " +
                            "   with active_devolves(facility_name, hospital_num, uuid, id, facility_id, unique_id,  " +
                            "       surname, other_names, date_birth, marital_status, status_at_registration, education, " +
                            "       occupation, date_confirmed_hiv, entry_point, address, phone, gender, date_started, " +
                            "       date_registration, age, lga, state, status, date_devolved) as ( " +
                            "           select f.name, hospital_num, p.uuid, p.id, p.facility_id, unique_id, surname,  " +
                            "           other_names, date_birth, marital_status, status_at_registration, education, occupation," +
                            "           date_confirmed_hiv, entry_point, address, phone, (case gender when 'MALE' then 'Male' else 'Female' end) gender,  " +
                            "           date_started, date_registration, datediff('year', date_birth, current_date) age, l.name," +
                            "           s.name,(select status from status_history where patient_id = d.patient_id and archived = false " +
                            "           order by date_status desc limit 1), date_devolved from devolve d join patient p on p.id = patient_id  join " +
                            "           facility f on f.id = p.facility_id left join lga l on l.id = p.lga_id left join state s on s.id = l.state_id " +
                            "           where d.community_pharmacy_id = ? and d.archived = false and p.archived = false and date_devolved <= current_date  " +
                            "           and date_returned_to_facility is null " +
                            "   )  " +
                            "   select *, rank()  over(partition by id order by date_devolved desc) _rank,  " +
                            "       max(date_devolved) over(partition by id order by date_devolved desc) date_devolved from active_devolves " +
                            ") devolves where _rank = 1 and status not in ('KNOWN_DEATH', 'ART_TRANSFER_OUT') order by other_names, surname";
                    List<Map<String, Object>> patient1 = jdbcTemplate.queryForList(query, cp.getId());
                    patient1 = patient1.stream()
                            .map(p -> {
                                int patientId = (int) p.get("id");
                                try {
                                    p.putAll(jdbcTemplate.queryForMap("" +
                                            "SELECT clinic_stage, date_visit date_last_clinic, next_appointment " +
                                            "   date_next_clinic FROM clinic WHERE patient_id = ? AND archived = false " +
                                            "   AND date_visit <= CURRENT_DATE ORDER BY date_visit DESC LIMIT 1", patientId));
                                } catch (Exception ignored) {
                                }
                                try {
                                    p.putAll(jdbcTemplate.queryForMap("" +
                                            "SELECT jsonb_extract_path_text(l,'result') last_cd4, date_result_received date_last_cd4 FROM laboratory, " +
                                            "   jsonb_array_elements(lines) with ordinality a(l) WHERE patient_id = ? AND " +
                                            "   archived = false AND date_result_received <= CURRENT_DATE and " +
                                            "   (jsonb_extract_path_text(l,'lab_test_id'))::int = 1 ORDER BY date_result_received DESC LIMIT 1", patientId));
                                } catch (Exception ignored) {
                                }
                                try {
                                    p.putAll(jdbcTemplate.queryForMap("" +
                                            "SELECT jsonb_extract_path_text(l,'result') last_viral_load, date_result_received date_last_viral_load, " +
                                            "   jsonb_extract_path_text(l,'indication') viral_load_type FROM laboratory, jsonb_array_elements(lines) " +
                                            "   with ordinality a(l) WHERE patient_id = ? AND archived = false AND date_result_received <= CURRENT_DATE and " +
                                            "   (jsonb_extract_path_text(l,'lab_test_id'))::int = 16 ORDER BY date_result_received DESC LIMIT 1", patientId));
                                } catch (Exception ignored) {
                                }
                                try {
                                    p.putAll(jdbcTemplate.queryForMap("" +
                                            "SELECT date_visit date_last_refill, next_appointment date_next_refill, " +
                                            "   (jsonb_extract_path_text(l,'duration'))::int last_refill_duration, r.description regimen, " +
                                            "   t.description regimen_type FROM pharmacy p, jsonb_array_elements(lines) with ordinality a(l) " +
                                            "   join regimen r on r.id = (jsonb_extract_path_text(l,'regimen_id'))::int join regimen_type t " +
                                            "   on t.id = r.regimen_type_id WHERE t.id in (1,2,3,4,14) AND p.patient_id = ? AND date_visit <= CURRENT_DATE " +
                                            "   AND p.archived = false ORDER BY date_visit DESC, (jsonb_extract_path_text(l,'duration'))::int DESC LIMIT 1", patientId));
                                } catch (Exception ignored) {
                                }
                                return p;
                            }).collect(Collectors.toList());
                    patients.addAll(patient1);


                    query = "with d as (" +
                            "   with active_devolves(id, facility_id, patient_id, date_devolved, dmoc_type, related_viral_load_id, " +
                            "       related_clinic_id, related_pharmacy_id, related_cd4_id, status) as (" +
                            "           select id, facility_id, patient_id, date_devolved, dmoc_type,related_viral_load_id, related_clinic_id," +
                            "               related_pharmacy_id, related_cd4_id, (select status from status_history where patient_id = d.patient_id " +
                            "               order by date_status desc limit 1) from devolve d where community_pharmacy_id = ? and archived = false " +
                            "               and date_returned_to_facility is null" +
                            "   ) " +
                            "   select *, rank()  over(partition by patient_id order by date_devolved desc) _rank,  " +
                            "       max(date_devolved) over(partition by patient_id order by date_devolved desc) date_devolved from active_devolves" +
                            ") " +
                            "SELECT distinct *, " +
                            "   (select jsonb_extract_path_text(l,'result') result from laboratory, jsonb_array_elements(lines) with ordinality a(l) where id = " +
                            "   related_viral_load_id and (jsonb_extract_path_text(l,'lab_test_id'))::int = 16 limit 1) last_viral_load," +
                            "   (select date_result_received from laboratory, jsonb_array_elements(lines) with ordinality a(l) where id = " +
                            "   related_viral_load_id and (jsonb_extract_path_text(l,'lab_test_id'))::int = 16 limit 1) date_last_viral_load," +
                            "   (select jsonb_extract_path_text(l,'result') result from laboratory, jsonb_array_elements(lines) with ordinality a(l) " +
                            "   where id = related_cd4_id and (jsonb_extract_path_text(l,'lab_test_id'))::int = 1 limit 1) last_cd4," +
                            "   (select date_result_received from laboratory, jsonb_array_elements(lines) with ordinality a(l) where " +
                            "   id = related_cd4_id and (jsonb_extract_path_text(l,'lab_test_id'))::int = 1 limit 1) date_last_cd4," +
                            "   (select clinic_stage from clinic where id = related_clinic_id) last_clinic_stage," +
                            "   (select date_visit from clinic where id = related_clinic_id) date_last_clinic_stage," +
                            "   (select r.description from pharmacy p, jsonb_array_elements(lines) with ordinality a(l) join regimen r " +
                            "   on r.id = (jsonb_extract_path_text(l,'regimen_id'))::int where p.id = related_pharmacy_id and r.regimen_type_id in (1, 2, 3, 4, 14) limit 1) regimen," +
                            "   (select r.description from pharmacy p, jsonb_array_elements(lines) with ordinality a(l) join regimen_type r " +
                            "   on r.id = (jsonb_extract_path_text(l,'regimen_type_id'))::int where p.id = related_pharmacy_id and " +
                            "   (jsonb_extract_path_text(l,'regimen_type_id'))::int in (1, 2, 3, 4, 14)  limit 1) regimen_type," +
                            "   (select date_visit from pharmacy where id = related_pharmacy_id limit 1) date_refill,facility_id " +
                            "from d where _rank = 1 and status not in ('KNOWN_DEATH', 'ART_TRANSFER_OUT')";
                    List<Map<String, Object>> devolves = jdbcTemplate.queryForList(query, cp.getId());

                    obj.put("patients", patients);
                    obj.put("devolves", devolves);

                    updateLastSync(cp.getId());

                } catch (Exception exception) {

                }
            });
        }
        return obj;
    }


    public void sync(List<EncounterDTO> encounterDTOList, String table) {
        delegate(table, encounterDTOList);
    }


    public void delegate(String table, List<EncounterDTO> encounterDTOList) {
        try {
            switch (table) {
                case "encounter":
                    List<EncounterDTO> encounters = encounterDTOList;
                    encounters.forEach(encounter -> {
                        Long id = checkIfEncounterExist(encounter.getPatient_id(), encounter.getFacility_id());
                        if (id != null) {
                            updateEncounter(encounter.getFacility_id(), encounter.getPatient_id(), LocalDate.parse(encounter.getDate_visit()), encounter.getRegimen1(), encounter.getRegimen2(), encounter.getRegimen3(), encounter.getRegimen4(), encounter.getDuration1(), encounter.getDuration2(), encounter.getDuration3(), encounter.getDuration4(), LocalDate.parse(encounter.getNext_refill()), getPuuId(encounter.getPatient_id(), encounter.getFacility_id()));
                            System.out.println("SUCCESS UPDATED");
                        } else {
                            System.out.println("facility" + encounter.getFacility_id());
                            saveEncounter(encounter.getFacility_id(), encounter.getPatient_id(), LocalDate.parse(encounter.getDate_visit()), encounter.getRegimen1(), encounter.getRegimen2(), encounter.getRegimen3(), encounter.getRegimen4(), encounter.getDuration1(), encounter.getDuration2(), encounter.getDuration3(), encounter.getDuration4(), LocalDate.parse(encounter.getNext_refill()), getPuuId(encounter.getPatient_id(), encounter.getFacility_id()));
                            System.out.println("SUCCESS SAVED");
                        }
                    });
                    break;
                default:
                    break;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

    public String getPuuId(Long patientId,Long facilityId)
    {
        String sql = "SELECT uuid FROM patient WHERE ID=? and FACILITY_ID = ?";
        List<String> puuid = jdbcTemplate.queryForList(sql, String.class, patientId, facilityId);
        if (puuid.size() > 0)
        {
            return puuid.get(0);
        }
        return null;
    }

    private Long checkIfEncounterExist(Long patientId, Long facilityId)
    {
        String sql = "SELECT id FROM encounter WHERE PATIENT_ID = ? and FACILITY_ID =? ";
        List<Long> ids = jdbcTemplate.queryForList(sql, Long.class, patientId, facilityId);
        if (ids.size() > 0) {
            return ids.get(0);
        }
        return null;
    }

    public void saveEncounter(Long facilityId, Long patientId, LocalDate dateVisit, String regimen1, String regimen2, String regimen3, String regimen4, int duration1, int duration2, int duration3, int duration4, LocalDate nextAppoint, String puuid) {
        jdbcTemplate.update("insert into encounter (facility_id, patient_id, date_visit, regimen1, regimen2, regimen3, regimen4, duration1, duration2, duration3, duration4, next_refill,last_modified,uuid,puuid) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                facilityId, patientId, dateVisit, regimen1, regimen2, regimen3, regimen4, duration1, duration2, duration3, duration4, nextAppoint, LocalDateTime.now(), UUID.randomUUID().toString(), puuid);
    }

    public void updateEncounter(Long facilityId, Long patientId, LocalDate dateVisit, String regimen1, String regimen2, String regimen3, String regimen4, int duration1, int duration2, int duration3, int duration4, LocalDate nextAppoint, String puuid) {
        jdbcTemplate.update("update encounter set facility_id = ?, patient_id = ? , date_visit = ?, regimen1 = ?, regimen2 = ?, regimen3 = ?, regimen4 = ?, duration1 = ?, duration2 = ?, duration3 = ?, duration4 = ?, next_refill = ?,time_stamp = ?,puuid = ? where puuid = ?",
                facilityId, patientId, dateVisit, regimen1, regimen2, regimen3, regimen4, duration1, duration2, duration3, duration4, nextAppoint, LocalDateTime.now(), puuid, puuid);
    }

}
