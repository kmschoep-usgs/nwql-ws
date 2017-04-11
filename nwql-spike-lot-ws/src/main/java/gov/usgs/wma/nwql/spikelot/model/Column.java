package gov.usgs.wma.nwql.spikelot.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"nwisSpkLot", "parmCode", "amount", "certDate", "expireDate",
	"methodCode", "schedule", "origRecord", "manufacturer", "lotNumber", "analyte", "units", "testCode"})
@XmlRootElement
public class Column implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String nwisSpkLot;

	private String parmCode;

	private String amount;

	private String certDate;

	private String expireDate;

	private String methodCode;

	private String schedule;

	private String origRecord;

	private String manufacturer;

	private String lotNumber;

	private String analyte;

	private String units;

	private String testCode;

	// JAXB requires an empty constructor
	public Column() {
	}

	@XmlElement(name = "nwisSpkLot")
	public String getNwisSpkLot() {
		return nwisSpkLot;
	}

	public void setNwisSpkLot(String nwisSpkLot) {
		this.nwisSpkLot = nwisSpkLot;
	}

	@XmlElement(name = "parmCode")
	public String getParmCode() {
		return parmCode;
	}

	public void setParmCode(String parmCode) {
		this.parmCode = parmCode;
	}

	@XmlElement(name = "amount")
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	@XmlElement(name = "certDate")
	public String getCertDate() {
		return certDate;
	}

	public void setCertDate(String certDate) {
		this.certDate = certDate;
	}

	@XmlElement(name = "expireDate")
	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	@XmlElement(name = "methodCode")
	public String getMethodCode() {
		return methodCode;
	}

	public void setMethodCode(String methodCode) {
		this.methodCode = methodCode;
	}

	@XmlElement(name = "schedule")
	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	
	@XmlElement(name = "origRecord")
	public String getOrigRecord() {
		return origRecord;
	}

	public void setOrigRecord(String origRecord) {
		this.origRecord = origRecord;
	}
	
	@XmlElement(name = "manufacturer")
	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	@XmlElement(name = "lotNumber")
	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}
	
	@XmlElement(name = "analyte")
	public String getAnalyte() {
		return analyte;
	}

	public void setAnalyte(String analyte) {
		this.analyte = analyte;
	}
	
	@XmlElement(name = "units")
	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	@XmlElement(name = "testCode")
	public String getTestCode() {
		return testCode;
	}

	public void setTestCode(String testCode) {
		this.testCode = testCode;
	}


}
