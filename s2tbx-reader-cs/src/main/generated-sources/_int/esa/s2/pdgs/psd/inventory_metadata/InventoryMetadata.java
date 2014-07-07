//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.inventory_metadata;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.pdgs.center.AS2ACQUISITIONCENTER;
import _int.esa.gs2.dico._1_0.pdgs.center.AS2PROCESSINGCENTRE;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="File_ID">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="S2(A|B)_OPER_[\w{Lu}_]{10}_[\w{Lu}_]{4}_\d{8}T\d{6}_S\d{8}T\d{6}_D\d{2}_N\d{2}\.\d{2}"/>
 *               &lt;pattern value="S2(A|B)_OPER_[\w{Lu}_]{10}_[\w{Lu}_]{4}_\d{8}T\d{6}_A\d{6}_T[\w{Lu}_]{5}_N\d{2}\.\d{2}"/>
 *               &lt;pattern value="S2(A|B)_OPER_[\w{Lu}_]{10}_[\w{Lu}_]{4}_\d{8}T\d{6}_S\d{8}T\d{6}_N\d{2}\.\d{2}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Parent_ID" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}DATASTRIP_ID">
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Group_ID">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}DATATAKE_ID">
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="File_Name">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="S2(A|B)_OPER_[\w{Lu}_]{10}_[\w{Lu}_]{4}_\d{8}T\d{6}_S\d{8}T\d{6}_D\d{2}_N\d{2}\.\d{2}"/>
 *               &lt;pattern value="S2(A|B)_OPER_[\w{Lu}_]{10}_[\w{Lu}_]{4}_\d{8}T\d{6}_A\d{6}_T[\w{Lu}_]{5}_N\d{2}\.\d{2}"/>
 *               &lt;pattern value="S2(A|B)_OPER_[\w{Lu}_]{10}_[\w{Lu}_]{4}_\d{8}T\d{6}_S\d{8}T\d{6}_N\d{2}\.\d{2}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="File_Version">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[0-9]{1,2}\.[0-9]{1,2}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="System">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="S2PDGS-DPC"/>
 *               &lt;enumeration value="S2PDGS-MCC"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Source">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="L(0|1A|1B|1C)_Processor"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Source_Sw_Version">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{1,2}\.\d{1,2}(\.\d{1,2})*"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Generation_Time">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="UTC=(\d{4}-(((01|03|05|07|08|10|12)-(0[1-9]|[1,2][0-9]|3[0,1]))|((04|06|09|11)-(0[1-9]|[1,2][0-9]|30))|(02-(0[1-9]|[1,2][0-9]))))T(([0,1][0-9]|2[0-3])(:[0-5][0-9]){2})(\.\d{6})?"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Validity_Start">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="UTC=(\d{4}-(((01|03|05|07|08|10|12)-(0[1-9]|[1,2][0-9]|3[0,1]))|((04|06|09|11)-(0[1-9]|[1,2][0-9]|30))|(02-(0[1-9]|[1,2][0-9]))))T(([0,1][0-9]|2[0-3])(:[0-5][0-9]){2})(\.\d{6})?"/>
 *               &lt;pattern value="UTC=(\d{4}-(((01|03|05|07|08|10|12)-(0[1-9]|[1,2][0-9]|3[0,1]))|((04|06|09|11)-(0[1-9]|[1,2][0-9]|30))|(02-(0[1-9]|[1,2][0-9]))))T(([0,1][0-9]|2[0-3])(:[0-5][0-9]){2})(\.\d{6})? UTC=(\d{4}-(((01|03|05|07|08|10|12)-(0[1-9]|[1,2][0-9]|3[0,1]))|((04|06|09|11)-(0[1-9]|[1,2][0-9]|30))|(02-(0[1-9]|[1,2][0-9]))))T(([0,1][0-9]|2[0-3])(:[0-5][0-9]){2})(\.\d{6})?"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Validity_Stop">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="UTC=(\d{4}-(((01|03|05|07|08|10|12)-(0[1-9]|[1,2][0-9]|3[0,1]))|((04|06|09|11)-(0[1-9]|[1,2][0-9]|30))|(02-(0[1-9]|[1,2][0-9]))))T(([0,1][0-9]|2[0-3])(:[0-5][0-9]){2})(\.\d{6})?"/>
 *               &lt;pattern value="UTC=(\d{4}-(((01|03|05|07|08|10|12)-(0[1-9]|[1,2][0-9]|3[0,1]))|((04|06|09|11)-(0[1-9]|[1,2][0-9]|30))|(02-(0[1-9]|[1,2][0-9]))))T(([0,1][0-9]|2[0-3])(:[0-5][0-9]){2})(\.\d{6})? UTC=(\d{4}-(((01|03|05|07|08|10|12)-(0[1-9]|[1,2][0-9]|3[0,1]))|((04|06|09|11)-(0[1-9]|[1,2][0-9]|30))|(02-(0[1-9]|[1,2][0-9]))))T(([0,1][0-9]|2[0-3])(:[0-5][0-9]){2})(\.\d{6})?"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Start_Orbit_Number">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{6}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Stop_Orbit_Number">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{6}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Geographic_Localization">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="GEO_TYPE">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;enumeration value="Polygon"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="List_Of_Geo_Pnt">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Geo_Pnt" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="LATITUDE">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
 *                                             &lt;minInclusive value="-90"/>
 *                                             &lt;maxInclusive value="+90"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="LONGITUDE">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
 *                                             &lt;minInclusive value="-180"/>
 *                                             &lt;maxExclusive value="+180"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="count" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Quality_Info">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="100"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Data_Size" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="File_Type">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Detector" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="(0[1-9])"/>
 *               &lt;pattern value="1[012]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="File_Class">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="RT"/>
 *               &lt;enumeration value="NRT"/>
 *               &lt;enumeration value="NOM"/>
 *               &lt;enumeration value="TEST-RT"/>
 *               &lt;enumeration value="TEST-NRT"/>
 *               &lt;enumeration value="TEST-NOM"/>
 *               &lt;enumeration value="NA"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Sensor_Code">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Sensor_Mode">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="3"/>
 *               &lt;enumeration value="NOM"/>
 *               &lt;enumeration value="DSC"/>
 *               &lt;enumeration value="ABC"/>
 *               &lt;enumeration value="VIC"/>
 *               &lt;enumeration value="RAW"/>
 *               &lt;enumeration value="TDI"/>
 *               &lt;enumeration value="NA_"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Acquisition_Station">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://gs2.esa.int/DICO/1.0/PDGS/center/}A_S2_ACQUISITION_CENTER">
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Processing_Station">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://gs2.esa.int/DICO/1.0/PDGS/center/}A_S2_PROCESSING_CENTRE">
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Satellite_Code">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="S2A"/>
 *               &lt;enumeration value="S2B"/>
 *               &lt;enumeration value="NIL"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Ascending_Flag">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="true"/>
 *               &lt;enumeration value="false"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CloudPercentage">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="100"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fileID",
    "parentID",
    "groupID",
    "fileName",
    "fileVersion",
    "system",
    "source",
    "sourceSwVersion",
    "generationTime",
    "validityStart",
    "validityStop",
    "startOrbitNumber",
    "stopOrbitNumber",
    "geographicLocalization",
    "qualityInfo",
    "dataSize",
    "fileType",
    "detector",
    "fileClass",
    "sensorCode",
    "sensorMode",
    "acquisitionStation",
    "processingStation",
    "satelliteCode",
    "ascendingFlag",
    "cloudPercentage"
})
@XmlRootElement(name = "Inventory_Metadata")
public class InventoryMetadata {

    @XmlElement(name = "File_ID", required = true)
    protected String fileID;
    @XmlElement(name = "Parent_ID")
    protected String parentID;
    @XmlElement(name = "Group_ID", required = true)
    protected String groupID;
    @XmlElement(name = "File_Name", required = true)
    protected String fileName;
    @XmlElement(name = "File_Version", required = true)
    protected String fileVersion;
    @XmlElement(name = "System", required = true)
    protected String system;
    @XmlElement(name = "Source", required = true)
    protected String source;
    @XmlElement(name = "Source_Sw_Version", required = true)
    protected String sourceSwVersion;
    @XmlElement(name = "Generation_Time", required = true)
    protected String generationTime;
    @XmlElement(name = "Validity_Start", required = true)
    protected String validityStart;
    @XmlElement(name = "Validity_Stop", required = true)
    protected String validityStop;
    @XmlElement(name = "Start_Orbit_Number", required = true)
    protected String startOrbitNumber;
    @XmlElement(name = "Stop_Orbit_Number", required = true)
    protected String stopOrbitNumber;
    @XmlElement(name = "Geographic_Localization", required = true)
    protected InventoryMetadata.GeographicLocalization geographicLocalization;
    @XmlElement(name = "Quality_Info")
    protected float qualityInfo;
    @XmlElement(name = "Data_Size", required = true)
    protected String dataSize;
    @XmlElement(name = "File_Type", required = true)
    protected String fileType;
    @XmlElementRef(name = "Detector", namespace = "http://pdgs.s2.esa.int/PSD/Inventory_Metadata.xsd", type = JAXBElement.class)
    protected JAXBElement<String> detector;
    @XmlElement(name = "File_Class", required = true)
    protected String fileClass;
    @XmlElement(name = "Sensor_Code", required = true)
    protected String sensorCode;
    @XmlElement(name = "Sensor_Mode", required = true)
    protected String sensorMode;
    @XmlElement(name = "Acquisition_Station", required = true)
    protected AS2ACQUISITIONCENTER acquisitionStation;
    @XmlElement(name = "Processing_Station", required = true)
    protected AS2PROCESSINGCENTRE processingStation;
    @XmlElement(name = "Satellite_Code", required = true, nillable = true)
    protected String satelliteCode;
    @XmlElement(name = "Ascending_Flag", required = true)
    protected String ascendingFlag;
    @XmlElement(name = "CloudPercentage")
    protected float cloudPercentage;

    /**
     * Obtient la valeur de la propriété fileID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileID() {
        return fileID;
    }

    /**
     * Définit la valeur de la propriété fileID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileID(String value) {
        this.fileID = value;
    }

    /**
     * Obtient la valeur de la propriété parentID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentID() {
        return parentID;
    }

    /**
     * Définit la valeur de la propriété parentID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentID(String value) {
        this.parentID = value;
    }

    /**
     * Obtient la valeur de la propriété groupID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupID() {
        return groupID;
    }

    /**
     * Définit la valeur de la propriété groupID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupID(String value) {
        this.groupID = value;
    }

    /**
     * Obtient la valeur de la propriété fileName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Définit la valeur de la propriété fileName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Obtient la valeur de la propriété fileVersion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileVersion() {
        return fileVersion;
    }

    /**
     * Définit la valeur de la propriété fileVersion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileVersion(String value) {
        this.fileVersion = value;
    }

    /**
     * Obtient la valeur de la propriété system.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystem() {
        return system;
    }

    /**
     * Définit la valeur de la propriété system.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystem(String value) {
        this.system = value;
    }

    /**
     * Obtient la valeur de la propriété source.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Définit la valeur de la propriété source.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Obtient la valeur de la propriété sourceSwVersion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceSwVersion() {
        return sourceSwVersion;
    }

    /**
     * Définit la valeur de la propriété sourceSwVersion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceSwVersion(String value) {
        this.sourceSwVersion = value;
    }

    /**
     * Obtient la valeur de la propriété generationTime.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenerationTime() {
        return generationTime;
    }

    /**
     * Définit la valeur de la propriété generationTime.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenerationTime(String value) {
        this.generationTime = value;
    }

    /**
     * Obtient la valeur de la propriété validityStart.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidityStart() {
        return validityStart;
    }

    /**
     * Définit la valeur de la propriété validityStart.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidityStart(String value) {
        this.validityStart = value;
    }

    /**
     * Obtient la valeur de la propriété validityStop.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidityStop() {
        return validityStop;
    }

    /**
     * Définit la valeur de la propriété validityStop.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidityStop(String value) {
        this.validityStop = value;
    }

    /**
     * Obtient la valeur de la propriété startOrbitNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartOrbitNumber() {
        return startOrbitNumber;
    }

    /**
     * Définit la valeur de la propriété startOrbitNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartOrbitNumber(String value) {
        this.startOrbitNumber = value;
    }

    /**
     * Obtient la valeur de la propriété stopOrbitNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStopOrbitNumber() {
        return stopOrbitNumber;
    }

    /**
     * Définit la valeur de la propriété stopOrbitNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStopOrbitNumber(String value) {
        this.stopOrbitNumber = value;
    }

    /**
     * Obtient la valeur de la propriété geographicLocalization.
     * 
     * @return
     *     possible object is
     *     {@link InventoryMetadata.GeographicLocalization }
     *     
     */
    public InventoryMetadata.GeographicLocalization getGeographicLocalization() {
        return geographicLocalization;
    }

    /**
     * Définit la valeur de la propriété geographicLocalization.
     * 
     * @param value
     *     allowed object is
     *     {@link InventoryMetadata.GeographicLocalization }
     *     
     */
    public void setGeographicLocalization(InventoryMetadata.GeographicLocalization value) {
        this.geographicLocalization = value;
    }

    /**
     * Obtient la valeur de la propriété qualityInfo.
     * 
     */
    public float getQualityInfo() {
        return qualityInfo;
    }

    /**
     * Définit la valeur de la propriété qualityInfo.
     * 
     */
    public void setQualityInfo(float value) {
        this.qualityInfo = value;
    }

    /**
     * Obtient la valeur de la propriété dataSize.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataSize() {
        return dataSize;
    }

    /**
     * Définit la valeur de la propriété dataSize.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataSize(String value) {
        this.dataSize = value;
    }

    /**
     * Obtient la valeur de la propriété fileType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Définit la valeur de la propriété fileType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileType(String value) {
        this.fileType = value;
    }

    /**
     * Obtient la valeur de la propriété detector.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDetector() {
        return detector;
    }

    /**
     * Définit la valeur de la propriété detector.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDetector(JAXBElement<String> value) {
        this.detector = value;
    }

    /**
     * Obtient la valeur de la propriété fileClass.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileClass() {
        return fileClass;
    }

    /**
     * Définit la valeur de la propriété fileClass.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileClass(String value) {
        this.fileClass = value;
    }

    /**
     * Obtient la valeur de la propriété sensorCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSensorCode() {
        return sensorCode;
    }

    /**
     * Définit la valeur de la propriété sensorCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSensorCode(String value) {
        this.sensorCode = value;
    }

    /**
     * Obtient la valeur de la propriété sensorMode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSensorMode() {
        return sensorMode;
    }

    /**
     * Définit la valeur de la propriété sensorMode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSensorMode(String value) {
        this.sensorMode = value;
    }

    /**
     * Obtient la valeur de la propriété acquisitionStation.
     * 
     * @return
     *     possible object is
     *     {@link AS2ACQUISITIONCENTER }
     *     
     */
    public AS2ACQUISITIONCENTER getAcquisitionStation() {
        return acquisitionStation;
    }

    /**
     * Définit la valeur de la propriété acquisitionStation.
     * 
     * @param value
     *     allowed object is
     *     {@link AS2ACQUISITIONCENTER }
     *     
     */
    public void setAcquisitionStation(AS2ACQUISITIONCENTER value) {
        this.acquisitionStation = value;
    }

    /**
     * Obtient la valeur de la propriété processingStation.
     * 
     * @return
     *     possible object is
     *     {@link AS2PROCESSINGCENTRE }
     *     
     */
    public AS2PROCESSINGCENTRE getProcessingStation() {
        return processingStation;
    }

    /**
     * Définit la valeur de la propriété processingStation.
     * 
     * @param value
     *     allowed object is
     *     {@link AS2PROCESSINGCENTRE }
     *     
     */
    public void setProcessingStation(AS2PROCESSINGCENTRE value) {
        this.processingStation = value;
    }

    /**
     * Obtient la valeur de la propriété satelliteCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSatelliteCode() {
        return satelliteCode;
    }

    /**
     * Définit la valeur de la propriété satelliteCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSatelliteCode(String value) {
        this.satelliteCode = value;
    }

    /**
     * Obtient la valeur de la propriété ascendingFlag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAscendingFlag() {
        return ascendingFlag;
    }

    /**
     * Définit la valeur de la propriété ascendingFlag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAscendingFlag(String value) {
        this.ascendingFlag = value;
    }

    /**
     * Obtient la valeur de la propriété cloudPercentage.
     * 
     */
    public float getCloudPercentage() {
        return cloudPercentage;
    }

    /**
     * Définit la valeur de la propriété cloudPercentage.
     * 
     */
    public void setCloudPercentage(float value) {
        this.cloudPercentage = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="GEO_TYPE">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;enumeration value="Polygon"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="List_Of_Geo_Pnt">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Geo_Pnt" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="LATITUDE">
     *                               &lt;simpleType>
     *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
     *                                   &lt;minInclusive value="-90"/>
     *                                   &lt;maxInclusive value="+90"/>
     *                                 &lt;/restriction>
     *                               &lt;/simpleType>
     *                             &lt;/element>
     *                             &lt;element name="LONGITUDE">
     *                               &lt;simpleType>
     *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
     *                                   &lt;minInclusive value="-180"/>
     *                                   &lt;maxExclusive value="+180"/>
     *                                 &lt;/restriction>
     *                               &lt;/simpleType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="count" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "geotype",
        "listOfGeoPnt"
    })
    public static class GeographicLocalization {

        @XmlElement(name = "GEO_TYPE", required = true)
        protected String geotype;
        @XmlElement(name = "List_Of_Geo_Pnt", required = true)
        protected InventoryMetadata.GeographicLocalization.ListOfGeoPnt listOfGeoPnt;

        /**
         * Obtient la valeur de la propriété geotype.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGEOTYPE() {
            return geotype;
        }

        /**
         * Définit la valeur de la propriété geotype.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGEOTYPE(String value) {
            this.geotype = value;
        }

        /**
         * Obtient la valeur de la propriété listOfGeoPnt.
         * 
         * @return
         *     possible object is
         *     {@link InventoryMetadata.GeographicLocalization.ListOfGeoPnt }
         *     
         */
        public InventoryMetadata.GeographicLocalization.ListOfGeoPnt getListOfGeoPnt() {
            return listOfGeoPnt;
        }

        /**
         * Définit la valeur de la propriété listOfGeoPnt.
         * 
         * @param value
         *     allowed object is
         *     {@link InventoryMetadata.GeographicLocalization.ListOfGeoPnt }
         *     
         */
        public void setListOfGeoPnt(InventoryMetadata.GeographicLocalization.ListOfGeoPnt value) {
            this.listOfGeoPnt = value;
        }


        /**
         * <p>Classe Java pour anonymous complex type.
         * 
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="Geo_Pnt" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="LATITUDE">
         *                     &lt;simpleType>
         *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
         *                         &lt;minInclusive value="-90"/>
         *                         &lt;maxInclusive value="+90"/>
         *                       &lt;/restriction>
         *                     &lt;/simpleType>
         *                   &lt;/element>
         *                   &lt;element name="LONGITUDE">
         *                     &lt;simpleType>
         *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
         *                         &lt;minInclusive value="-180"/>
         *                         &lt;maxExclusive value="+180"/>
         *                       &lt;/restriction>
         *                     &lt;/simpleType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="count" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "geoPnt"
        })
        public static class ListOfGeoPnt {

            @XmlElement(name = "Geo_Pnt", required = true)
            protected List<InventoryMetadata.GeographicLocalization.ListOfGeoPnt.GeoPnt> geoPnt;
            @XmlAttribute(name = "count", required = true)
            @XmlSchemaType(name = "positiveInteger")
            protected BigInteger count;

            /**
             * Gets the value of the geoPnt property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the geoPnt property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getGeoPnt().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link InventoryMetadata.GeographicLocalization.ListOfGeoPnt.GeoPnt }
             * 
             * 
             */
            public List<InventoryMetadata.GeographicLocalization.ListOfGeoPnt.GeoPnt> getGeoPnt() {
                if (geoPnt == null) {
                    geoPnt = new ArrayList<InventoryMetadata.GeographicLocalization.ListOfGeoPnt.GeoPnt>();
                }
                return this.geoPnt;
            }

            /**
             * Obtient la valeur de la propriété count.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getCount() {
                return count;
            }

            /**
             * Définit la valeur de la propriété count.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setCount(BigInteger value) {
                this.count = value;
            }


            /**
             * <p>Classe Java pour anonymous complex type.
             * 
             * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="LATITUDE">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
             *               &lt;minInclusive value="-90"/>
             *               &lt;maxInclusive value="+90"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
             *         &lt;/element>
             *         &lt;element name="LONGITUDE">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
             *               &lt;minInclusive value="-180"/>
             *               &lt;maxExclusive value="+180"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "latitude",
                "longitude"
            })
            public static class GeoPnt {

                @XmlElement(name = "LATITUDE")
                protected float latitude;
                @XmlElement(name = "LONGITUDE")
                protected float longitude;

                /**
                 * Obtient la valeur de la propriété latitude.
                 * 
                 */
                public float getLATITUDE() {
                    return latitude;
                }

                /**
                 * Définit la valeur de la propriété latitude.
                 * 
                 */
                public void setLATITUDE(float value) {
                    this.latitude = value;
                }

                /**
                 * Obtient la valeur de la propriété longitude.
                 * 
                 */
                public float getLONGITUDE() {
                    return longitude;
                }

                /**
                 * Définit la valeur de la propriété longitude.
                 * 
                 */
                public void setLONGITUDE(float value) {
                    this.longitude = value;
                }

            }

        }

    }

}
