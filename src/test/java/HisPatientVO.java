import me.qping.utils.database.bean.DataBaseColumn;

/**
 * @ClassName HisPatientVO
 * @Description his
 * @Author qping
 * @Date 2020/1/13 15:02
 * @Version 1.0
 **/
public class HisPatientVO {

    @DataBaseColumn(value = "GUARDIAN_NAME")
    private String guardianName;

    @DataBaseColumn(value = "PATIENT_NAME")
    private String patientName;

    @DataBaseColumn(value = "ID_CARD_TYPE")
    private Integer idCardType;

    @DataBaseColumn(value = "ID_CARD_CODE")
    private String idCardCode;

    @DataBaseColumn(value = "IS_SELF")
    private Integer isSelf;

    @DataBaseColumn(value = "GENDE_CODE")
    private Integer genderCode;

    @DataBaseColumn(value = "BIRTH_DATE")
    private String birthDate;

    @DataBaseColumn(value = "EMPLOYER_ORG_NAME")
    private String employerOrgName;

    @DataBaseColumn(value = "TELE_COM")
    private String teleCom;

    @DataBaseColumn(value = "LIVING_ADDRESS")
    private String livingAddress;

    @DataBaseColumn(value = "ONSET_DATE")
    private String onsetDate;

    @DataBaseColumn(value = "DOC_ID")
    private String fillingDoctorCode;

    @DataBaseColumn(value = "DOC_NAME")
    private String fillingDoctorName;

    @DataBaseColumn(value = "PATIENT_NO")
    private String hisPatientNo;

    @DataBaseColumn(value = "DPT_NAME")
    private String hisDeptName;

    @DataBaseColumn(value = "DPT_CODE")

    private String hisDeptCode;
    @DataBaseColumn(value = "YLJGDM")
    private String hisYljgdm;

    private Integer hisPatientType;

    private Integer reportOrgCode;

    private Integer reportOrgCityCode;

    private Integer reportOrgCountyCode;

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Integer getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(Integer idCardType) {
        this.idCardType = idCardType;
    }

    public String getIdCardCode() {
        return idCardCode;
    }

    public void setIdCardCode(String idCardCode) {
        this.idCardCode = idCardCode;
    }

    public Integer getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(Integer isSelf) {
        this.isSelf = isSelf;
    }

    public Integer getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(Integer genderCode) {
        this.genderCode = genderCode;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmployerOrgName() {
        return employerOrgName;
    }

    public void setEmployerOrgName(String employerOrgName) {
        this.employerOrgName = employerOrgName;
    }

    public String getTeleCom() {
        return teleCom;
    }

    public void setTeleCom(String teleCom) {
        this.teleCom = teleCom;
    }

    public String getLivingAddress() {
        return livingAddress;
    }

    public void setLivingAddress(String livingAddress) {
        this.livingAddress = livingAddress;
    }

    public String getOnsetDate() {
        return onsetDate;
    }

    public void setOnsetDate(String onsetDate) {
        this.onsetDate = onsetDate;
    }

    public String getFillingDoctorCode() {
        return fillingDoctorCode;
    }

    public void setFillingDoctorCode(String fillingDoctorCode) {
        this.fillingDoctorCode = fillingDoctorCode;
    }

    public String getFillingDoctorName() {
        return fillingDoctorName;
    }

    public void setFillingDoctorName(String fillingDoctorName) {
        this.fillingDoctorName = fillingDoctorName;
    }

    public String getHisPatientNo() {
        return hisPatientNo;
    }

    public void setHisPatientNo(String hisPatientNo) {
        this.hisPatientNo = hisPatientNo;
    }

    public String getHisDeptName() {
        return hisDeptName;
    }

    public void setHisDeptName(String hisDeptName) {
        this.hisDeptName = hisDeptName;
    }

    public String getHisDeptCode() {
        return hisDeptCode;
    }

    public void setHisDeptCode(String hisDeptCode) {
        this.hisDeptCode = hisDeptCode;
    }

    public String getHisYljgdm() {
        return hisYljgdm;
    }

    public void setHisYljgdm(String hisYljgdm) {
        this.hisYljgdm = hisYljgdm;
    }

    public Integer getHisPatientType() {
        return hisPatientType;
    }

    public void setHisPatientType(Integer hisPatientType) {
        this.hisPatientType = hisPatientType;
    }

    public Integer getReportOrgCode() {
        return reportOrgCode;
    }

    public void setReportOrgCode(Integer reportOrgCode) {
        this.reportOrgCode = reportOrgCode;
    }

    public Integer getReportOrgCityCode() {
        return reportOrgCityCode;
    }

    public void setReportOrgCityCode(Integer reportOrgCityCode) {
        this.reportOrgCityCode = reportOrgCityCode;
    }

    public Integer getReportOrgCountyCode() {
        return reportOrgCountyCode;
    }

    public void setReportOrgCountyCode(Integer reportOrgCountyCode) {
        this.reportOrgCountyCode = reportOrgCountyCode;
    }
}
