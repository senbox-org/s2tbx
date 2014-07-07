//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHDEGREECELSIUSUNITATTR;
import _int.esa.gs2.dico._1_0.sy.misc.AINTWITHOHMUNITATTR;


/**
 * <p>Classe Java pour A_THERMAL_DATA_INV complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_THERMAL_DATA_INV">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FPA_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="FPA" maxOccurs="2">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Value" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
 *                                       &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="fpaId" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="VNIR"/>
 *                                 &lt;enumeration value="SWIR"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="FEE" maxOccurs="2">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Value" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
 *                                       &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="feeId" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="VNIR"/>
 *                                 &lt;enumeration value="SWIR"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Mirror_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Mirror" maxOccurs="3">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Value" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
 *                                       &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="mirrorId" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                 &lt;enumeration value="1"/>
 *                                 &lt;enumeration value="2"/>
 *                                 &lt;enumeration value="3"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ThSensor_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ThSensor" maxOccurs="5">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Value" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
 *                                       &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="sensorId" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="+X-Z"/>
 *                                 &lt;enumeration value="-X-Z"/>
 *                                 &lt;enumeration value="+Y"/>
 *                                 &lt;enumeration value="-Y"/>
 *                                 &lt;enumeration value="+Z"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Splitter_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Splitter" maxOccurs="2">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Value" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
 *                                       &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="splitterId" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="bot+X"/>
 *                                 &lt;enumeration value="top-X"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="CSM_Diffuser_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CSM_Diffuser" maxOccurs="3">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Value" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
 *                                       &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="diffuserId" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                 &lt;enumeration value="0"/>
 *                                 &lt;enumeration value="1"/>
 *                                 &lt;enumeration value="2"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="IMU_Sensorplate_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="IMU_Sensorplate" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
 *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="STR_Sensorplate_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="STR_Sensorplate" maxOccurs="3">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Value" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
 *                                       &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="sensorplateId" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                 &lt;enumeration value="0"/>
 *                                 &lt;enumeration value="1"/>
 *                                 &lt;enumeration value="2"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="STR_Baseplate_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="STR_Baseplate" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
 *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="STR_Backplate_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="STR_Backplate" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
 *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
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
@XmlType(name = "A_THERMAL_DATA_INV", propOrder = {
    "fpaList",
    "mirrorList",
    "thSensorList",
    "splitterList",
    "csmDiffuserList",
    "imuSensorplateList",
    "strSensorplateList",
    "strBaseplateList",
    "strBackplateList"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.ANANCILLARYDATADSL0 .ThermalData.class
})
public class ATHERMALDATAINV {

    @XmlElement(name = "FPA_List", required = true)
    protected ATHERMALDATAINV.FPAList fpaList;
    @XmlElement(name = "Mirror_List", required = true)
    protected ATHERMALDATAINV.MirrorList mirrorList;
    @XmlElement(name = "ThSensor_List", required = true)
    protected ATHERMALDATAINV.ThSensorList thSensorList;
    @XmlElement(name = "Splitter_List", required = true)
    protected ATHERMALDATAINV.SplitterList splitterList;
    @XmlElement(name = "CSM_Diffuser_List", required = true)
    protected ATHERMALDATAINV.CSMDiffuserList csmDiffuserList;
    @XmlElement(name = "IMU_Sensorplate_List", required = true)
    protected ATHERMALDATAINV.IMUSensorplateList imuSensorplateList;
    @XmlElement(name = "STR_Sensorplate_List", required = true)
    protected ATHERMALDATAINV.STRSensorplateList strSensorplateList;
    @XmlElement(name = "STR_Baseplate_List", required = true)
    protected ATHERMALDATAINV.STRBaseplateList strBaseplateList;
    @XmlElement(name = "STR_Backplate_List", required = true)
    protected ATHERMALDATAINV.STRBackplateList strBackplateList;

    /**
     * Obtient la valeur de la propriété fpaList.
     * 
     * @return
     *     possible object is
     *     {@link ATHERMALDATAINV.FPAList }
     *     
     */
    public ATHERMALDATAINV.FPAList getFPAList() {
        return fpaList;
    }

    /**
     * Définit la valeur de la propriété fpaList.
     * 
     * @param value
     *     allowed object is
     *     {@link ATHERMALDATAINV.FPAList }
     *     
     */
    public void setFPAList(ATHERMALDATAINV.FPAList value) {
        this.fpaList = value;
    }

    /**
     * Obtient la valeur de la propriété mirrorList.
     * 
     * @return
     *     possible object is
     *     {@link ATHERMALDATAINV.MirrorList }
     *     
     */
    public ATHERMALDATAINV.MirrorList getMirrorList() {
        return mirrorList;
    }

    /**
     * Définit la valeur de la propriété mirrorList.
     * 
     * @param value
     *     allowed object is
     *     {@link ATHERMALDATAINV.MirrorList }
     *     
     */
    public void setMirrorList(ATHERMALDATAINV.MirrorList value) {
        this.mirrorList = value;
    }

    /**
     * Obtient la valeur de la propriété thSensorList.
     * 
     * @return
     *     possible object is
     *     {@link ATHERMALDATAINV.ThSensorList }
     *     
     */
    public ATHERMALDATAINV.ThSensorList getThSensorList() {
        return thSensorList;
    }

    /**
     * Définit la valeur de la propriété thSensorList.
     * 
     * @param value
     *     allowed object is
     *     {@link ATHERMALDATAINV.ThSensorList }
     *     
     */
    public void setThSensorList(ATHERMALDATAINV.ThSensorList value) {
        this.thSensorList = value;
    }

    /**
     * Obtient la valeur de la propriété splitterList.
     * 
     * @return
     *     possible object is
     *     {@link ATHERMALDATAINV.SplitterList }
     *     
     */
    public ATHERMALDATAINV.SplitterList getSplitterList() {
        return splitterList;
    }

    /**
     * Définit la valeur de la propriété splitterList.
     * 
     * @param value
     *     allowed object is
     *     {@link ATHERMALDATAINV.SplitterList }
     *     
     */
    public void setSplitterList(ATHERMALDATAINV.SplitterList value) {
        this.splitterList = value;
    }

    /**
     * Obtient la valeur de la propriété csmDiffuserList.
     * 
     * @return
     *     possible object is
     *     {@link ATHERMALDATAINV.CSMDiffuserList }
     *     
     */
    public ATHERMALDATAINV.CSMDiffuserList getCSMDiffuserList() {
        return csmDiffuserList;
    }

    /**
     * Définit la valeur de la propriété csmDiffuserList.
     * 
     * @param value
     *     allowed object is
     *     {@link ATHERMALDATAINV.CSMDiffuserList }
     *     
     */
    public void setCSMDiffuserList(ATHERMALDATAINV.CSMDiffuserList value) {
        this.csmDiffuserList = value;
    }

    /**
     * Obtient la valeur de la propriété imuSensorplateList.
     * 
     * @return
     *     possible object is
     *     {@link ATHERMALDATAINV.IMUSensorplateList }
     *     
     */
    public ATHERMALDATAINV.IMUSensorplateList getIMUSensorplateList() {
        return imuSensorplateList;
    }

    /**
     * Définit la valeur de la propriété imuSensorplateList.
     * 
     * @param value
     *     allowed object is
     *     {@link ATHERMALDATAINV.IMUSensorplateList }
     *     
     */
    public void setIMUSensorplateList(ATHERMALDATAINV.IMUSensorplateList value) {
        this.imuSensorplateList = value;
    }

    /**
     * Obtient la valeur de la propriété strSensorplateList.
     * 
     * @return
     *     possible object is
     *     {@link ATHERMALDATAINV.STRSensorplateList }
     *     
     */
    public ATHERMALDATAINV.STRSensorplateList getSTRSensorplateList() {
        return strSensorplateList;
    }

    /**
     * Définit la valeur de la propriété strSensorplateList.
     * 
     * @param value
     *     allowed object is
     *     {@link ATHERMALDATAINV.STRSensorplateList }
     *     
     */
    public void setSTRSensorplateList(ATHERMALDATAINV.STRSensorplateList value) {
        this.strSensorplateList = value;
    }

    /**
     * Obtient la valeur de la propriété strBaseplateList.
     * 
     * @return
     *     possible object is
     *     {@link ATHERMALDATAINV.STRBaseplateList }
     *     
     */
    public ATHERMALDATAINV.STRBaseplateList getSTRBaseplateList() {
        return strBaseplateList;
    }

    /**
     * Définit la valeur de la propriété strBaseplateList.
     * 
     * @param value
     *     allowed object is
     *     {@link ATHERMALDATAINV.STRBaseplateList }
     *     
     */
    public void setSTRBaseplateList(ATHERMALDATAINV.STRBaseplateList value) {
        this.strBaseplateList = value;
    }

    /**
     * Obtient la valeur de la propriété strBackplateList.
     * 
     * @return
     *     possible object is
     *     {@link ATHERMALDATAINV.STRBackplateList }
     *     
     */
    public ATHERMALDATAINV.STRBackplateList getSTRBackplateList() {
        return strBackplateList;
    }

    /**
     * Définit la valeur de la propriété strBackplateList.
     * 
     * @param value
     *     allowed object is
     *     {@link ATHERMALDATAINV.STRBackplateList }
     *     
     */
    public void setSTRBackplateList(ATHERMALDATAINV.STRBackplateList value) {
        this.strBackplateList = value;
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
     *         &lt;element name="CSM_Diffuser" maxOccurs="3">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Value" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
     *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="diffuserId" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                       &lt;enumeration value="0"/>
     *                       &lt;enumeration value="1"/>
     *                       &lt;enumeration value="2"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
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
        "csmDiffuser"
    })
    public static class CSMDiffuserList {

        @XmlElement(name = "CSM_Diffuser", required = true)
        protected List<ATHERMALDATAINV.CSMDiffuserList.CSMDiffuser> csmDiffuser;

        /**
         * Gets the value of the csmDiffuser property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the csmDiffuser property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCSMDiffuser().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ATHERMALDATAINV.CSMDiffuserList.CSMDiffuser }
         * 
         * 
         */
        public List<ATHERMALDATAINV.CSMDiffuserList.CSMDiffuser> getCSMDiffuser() {
            if (csmDiffuser == null) {
                csmDiffuser = new ArrayList<ATHERMALDATAINV.CSMDiffuserList.CSMDiffuser>();
            }
            return this.csmDiffuser;
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
         *         &lt;element name="Value" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
         *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="diffuserId" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *             &lt;enumeration value="0"/>
         *             &lt;enumeration value="1"/>
         *             &lt;enumeration value="2"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class CSMDiffuser {

            @XmlElement(name = "Value", required = true)
            protected List<ATHERMALDATAINV.CSMDiffuserList.CSMDiffuser.Value> value;
            @XmlAttribute(name = "diffuserId", required = true)
            protected int diffuserId;

            /**
             * Gets the value of the value property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the value property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getValue().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ATHERMALDATAINV.CSMDiffuserList.CSMDiffuser.Value }
             * 
             * 
             */
            public List<ATHERMALDATAINV.CSMDiffuserList.CSMDiffuser.Value> getValue() {
                if (value == null) {
                    value = new ArrayList<ATHERMALDATAINV.CSMDiffuserList.CSMDiffuser.Value>();
                }
                return this.value;
            }

            /**
             * Obtient la valeur de la propriété diffuserId.
             * 
             */
            public int getDiffuserId() {
                return diffuserId;
            }

            /**
             * Définit la valeur de la propriété diffuserId.
             * 
             */
            public void setDiffuserId(int value) {
                this.diffuserId = value;
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
             *         &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
             *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                "t",
                "gpstime"
            })
            public static class Value {

                @XmlElement(name = "T", required = true)
                protected AINTWITHOHMUNITATTR t;
                @XmlElement(name = "GPS_TIME", required = true)
                protected XMLGregorianCalendar gpstime;

                /**
                 * Obtient la valeur de la propriété t.
                 * 
                 * @return
                 *     possible object is
                 *     {@link AINTWITHOHMUNITATTR }
                 *     
                 */
                public AINTWITHOHMUNITATTR getT() {
                    return t;
                }

                /**
                 * Définit la valeur de la propriété t.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link AINTWITHOHMUNITATTR }
                 *     
                 */
                public void setT(AINTWITHOHMUNITATTR value) {
                    this.t = value;
                }

                /**
                 * Obtient la valeur de la propriété gpstime.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getGPSTIME() {
                    return gpstime;
                }

                /**
                 * Définit la valeur de la propriété gpstime.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setGPSTIME(XMLGregorianCalendar value) {
                    this.gpstime = value;
                }

            }

        }

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
     *         &lt;element name="FPA" maxOccurs="2">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Value" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
     *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="fpaId" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="VNIR"/>
     *                       &lt;enumeration value="SWIR"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="FEE" maxOccurs="2">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Value" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
     *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="feeId" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="VNIR"/>
     *                       &lt;enumeration value="SWIR"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
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
        "fpa",
        "fee"
    })
    public static class FPAList {

        @XmlElement(name = "FPA", required = true)
        protected List<ATHERMALDATAINV.FPAList.FPA> fpa;
        @XmlElement(name = "FEE", required = true)
        protected List<ATHERMALDATAINV.FPAList.FEE> fee;

        /**
         * Gets the value of the fpa property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the fpa property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFPA().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ATHERMALDATAINV.FPAList.FPA }
         * 
         * 
         */
        public List<ATHERMALDATAINV.FPAList.FPA> getFPA() {
            if (fpa == null) {
                fpa = new ArrayList<ATHERMALDATAINV.FPAList.FPA>();
            }
            return this.fpa;
        }

        /**
         * Gets the value of the fee property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the fee property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFEE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ATHERMALDATAINV.FPAList.FEE }
         * 
         * 
         */
        public List<ATHERMALDATAINV.FPAList.FEE> getFEE() {
            if (fee == null) {
                fee = new ArrayList<ATHERMALDATAINV.FPAList.FEE>();
            }
            return this.fee;
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
         *         &lt;element name="Value" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
         *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="feeId" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="VNIR"/>
         *             &lt;enumeration value="SWIR"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class FEE {

            @XmlElement(name = "Value", required = true)
            protected List<ATHERMALDATAINV.FPAList.FEE.Value> value;
            @XmlAttribute(name = "feeId", required = true)
            protected String feeId;

            /**
             * Gets the value of the value property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the value property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getValue().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ATHERMALDATAINV.FPAList.FEE.Value }
             * 
             * 
             */
            public List<ATHERMALDATAINV.FPAList.FEE.Value> getValue() {
                if (value == null) {
                    value = new ArrayList<ATHERMALDATAINV.FPAList.FEE.Value>();
                }
                return this.value;
            }

            /**
             * Obtient la valeur de la propriété feeId.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFeeId() {
                return feeId;
            }

            /**
             * Définit la valeur de la propriété feeId.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFeeId(String value) {
                this.feeId = value;
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
             *         &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
             *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                "t",
                "gpstime"
            })
            public static class Value {

                @XmlElement(name = "T", required = true)
                protected ADOUBLEWITHDEGREECELSIUSUNITATTR t;
                @XmlElement(name = "GPS_TIME", required = true)
                protected XMLGregorianCalendar gpstime;

                /**
                 * Obtient la valeur de la propriété t.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                 *     
                 */
                public ADOUBLEWITHDEGREECELSIUSUNITATTR getT() {
                    return t;
                }

                /**
                 * Définit la valeur de la propriété t.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                 *     
                 */
                public void setT(ADOUBLEWITHDEGREECELSIUSUNITATTR value) {
                    this.t = value;
                }

                /**
                 * Obtient la valeur de la propriété gpstime.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getGPSTIME() {
                    return gpstime;
                }

                /**
                 * Définit la valeur de la propriété gpstime.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setGPSTIME(XMLGregorianCalendar value) {
                    this.gpstime = value;
                }

            }

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
         *         &lt;element name="Value" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
         *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="fpaId" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="VNIR"/>
         *             &lt;enumeration value="SWIR"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class FPA {

            @XmlElement(name = "Value", required = true)
            protected List<ATHERMALDATAINV.FPAList.FPA.Value> value;
            @XmlAttribute(name = "fpaId", required = true)
            protected String fpaId;

            /**
             * Gets the value of the value property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the value property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getValue().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ATHERMALDATAINV.FPAList.FPA.Value }
             * 
             * 
             */
            public List<ATHERMALDATAINV.FPAList.FPA.Value> getValue() {
                if (value == null) {
                    value = new ArrayList<ATHERMALDATAINV.FPAList.FPA.Value>();
                }
                return this.value;
            }

            /**
             * Obtient la valeur de la propriété fpaId.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFpaId() {
                return fpaId;
            }

            /**
             * Définit la valeur de la propriété fpaId.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFpaId(String value) {
                this.fpaId = value;
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
             *         &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
             *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                "t",
                "gpstime"
            })
            public static class Value {

                @XmlElement(name = "T", required = true)
                protected ADOUBLEWITHDEGREECELSIUSUNITATTR t;
                @XmlElement(name = "GPS_TIME", required = true)
                protected XMLGregorianCalendar gpstime;

                /**
                 * Obtient la valeur de la propriété t.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                 *     
                 */
                public ADOUBLEWITHDEGREECELSIUSUNITATTR getT() {
                    return t;
                }

                /**
                 * Définit la valeur de la propriété t.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                 *     
                 */
                public void setT(ADOUBLEWITHDEGREECELSIUSUNITATTR value) {
                    this.t = value;
                }

                /**
                 * Obtient la valeur de la propriété gpstime.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getGPSTIME() {
                    return gpstime;
                }

                /**
                 * Définit la valeur de la propriété gpstime.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setGPSTIME(XMLGregorianCalendar value) {
                    this.gpstime = value;
                }

            }

        }

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
     *         &lt;element name="IMU_Sensorplate" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
     *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                 &lt;/sequence>
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
        "imuSensorplate"
    })
    public static class IMUSensorplateList {

        @XmlElement(name = "IMU_Sensorplate", required = true)
        protected List<ATHERMALDATAINV.IMUSensorplateList.IMUSensorplate> imuSensorplate;

        /**
         * Gets the value of the imuSensorplate property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the imuSensorplate property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getIMUSensorplate().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ATHERMALDATAINV.IMUSensorplateList.IMUSensorplate }
         * 
         * 
         */
        public List<ATHERMALDATAINV.IMUSensorplateList.IMUSensorplate> getIMUSensorplate() {
            if (imuSensorplate == null) {
                imuSensorplate = new ArrayList<ATHERMALDATAINV.IMUSensorplateList.IMUSensorplate>();
            }
            return this.imuSensorplate;
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
         *         &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
         *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
            "t",
            "gpstime"
        })
        public static class IMUSensorplate {

            @XmlElement(name = "T", required = true)
            protected AINTWITHOHMUNITATTR t;
            @XmlElement(name = "GPS_TIME", required = true)
            protected XMLGregorianCalendar gpstime;

            /**
             * Obtient la valeur de la propriété t.
             * 
             * @return
             *     possible object is
             *     {@link AINTWITHOHMUNITATTR }
             *     
             */
            public AINTWITHOHMUNITATTR getT() {
                return t;
            }

            /**
             * Définit la valeur de la propriété t.
             * 
             * @param value
             *     allowed object is
             *     {@link AINTWITHOHMUNITATTR }
             *     
             */
            public void setT(AINTWITHOHMUNITATTR value) {
                this.t = value;
            }

            /**
             * Obtient la valeur de la propriété gpstime.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getGPSTIME() {
                return gpstime;
            }

            /**
             * Définit la valeur de la propriété gpstime.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setGPSTIME(XMLGregorianCalendar value) {
                this.gpstime = value;
            }

        }

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
     *         &lt;element name="Mirror" maxOccurs="3">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Value" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
     *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="mirrorId" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                       &lt;enumeration value="1"/>
     *                       &lt;enumeration value="2"/>
     *                       &lt;enumeration value="3"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
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
        "mirror"
    })
    public static class MirrorList {

        @XmlElement(name = "Mirror", required = true)
        protected List<ATHERMALDATAINV.MirrorList.Mirror> mirror;

        /**
         * Gets the value of the mirror property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the mirror property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMirror().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ATHERMALDATAINV.MirrorList.Mirror }
         * 
         * 
         */
        public List<ATHERMALDATAINV.MirrorList.Mirror> getMirror() {
            if (mirror == null) {
                mirror = new ArrayList<ATHERMALDATAINV.MirrorList.Mirror>();
            }
            return this.mirror;
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
         *         &lt;element name="Value" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
         *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="mirrorId" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *             &lt;enumeration value="1"/>
         *             &lt;enumeration value="2"/>
         *             &lt;enumeration value="3"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Mirror {

            @XmlElement(name = "Value", required = true)
            protected List<ATHERMALDATAINV.MirrorList.Mirror.Value> value;
            @XmlAttribute(name = "mirrorId", required = true)
            protected int mirrorId;

            /**
             * Gets the value of the value property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the value property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getValue().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ATHERMALDATAINV.MirrorList.Mirror.Value }
             * 
             * 
             */
            public List<ATHERMALDATAINV.MirrorList.Mirror.Value> getValue() {
                if (value == null) {
                    value = new ArrayList<ATHERMALDATAINV.MirrorList.Mirror.Value>();
                }
                return this.value;
            }

            /**
             * Obtient la valeur de la propriété mirrorId.
             * 
             */
            public int getMirrorId() {
                return mirrorId;
            }

            /**
             * Définit la valeur de la propriété mirrorId.
             * 
             */
            public void setMirrorId(int value) {
                this.mirrorId = value;
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
             *         &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
             *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                "t",
                "gpstime"
            })
            public static class Value {

                @XmlElement(name = "T", required = true)
                protected ADOUBLEWITHDEGREECELSIUSUNITATTR t;
                @XmlElement(name = "GPS_TIME", required = true)
                protected XMLGregorianCalendar gpstime;

                /**
                 * Obtient la valeur de la propriété t.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                 *     
                 */
                public ADOUBLEWITHDEGREECELSIUSUNITATTR getT() {
                    return t;
                }

                /**
                 * Définit la valeur de la propriété t.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                 *     
                 */
                public void setT(ADOUBLEWITHDEGREECELSIUSUNITATTR value) {
                    this.t = value;
                }

                /**
                 * Obtient la valeur de la propriété gpstime.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getGPSTIME() {
                    return gpstime;
                }

                /**
                 * Définit la valeur de la propriété gpstime.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setGPSTIME(XMLGregorianCalendar value) {
                    this.gpstime = value;
                }

            }

        }

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
     *         &lt;element name="STR_Backplate" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
     *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                 &lt;/sequence>
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
        "strBackplate"
    })
    public static class STRBackplateList {

        @XmlElement(name = "STR_Backplate", required = true)
        protected List<ATHERMALDATAINV.STRBackplateList.STRBackplate> strBackplate;

        /**
         * Gets the value of the strBackplate property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the strBackplate property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSTRBackplate().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ATHERMALDATAINV.STRBackplateList.STRBackplate }
         * 
         * 
         */
        public List<ATHERMALDATAINV.STRBackplateList.STRBackplate> getSTRBackplate() {
            if (strBackplate == null) {
                strBackplate = new ArrayList<ATHERMALDATAINV.STRBackplateList.STRBackplate>();
            }
            return this.strBackplate;
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
         *         &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
         *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
            "t",
            "gpstime"
        })
        public static class STRBackplate {

            @XmlElement(name = "T", required = true)
            protected AINTWITHOHMUNITATTR t;
            @XmlElement(name = "GPS_TIME", required = true)
            protected XMLGregorianCalendar gpstime;

            /**
             * Obtient la valeur de la propriété t.
             * 
             * @return
             *     possible object is
             *     {@link AINTWITHOHMUNITATTR }
             *     
             */
            public AINTWITHOHMUNITATTR getT() {
                return t;
            }

            /**
             * Définit la valeur de la propriété t.
             * 
             * @param value
             *     allowed object is
             *     {@link AINTWITHOHMUNITATTR }
             *     
             */
            public void setT(AINTWITHOHMUNITATTR value) {
                this.t = value;
            }

            /**
             * Obtient la valeur de la propriété gpstime.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getGPSTIME() {
                return gpstime;
            }

            /**
             * Définit la valeur de la propriété gpstime.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setGPSTIME(XMLGregorianCalendar value) {
                this.gpstime = value;
            }

        }

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
     *         &lt;element name="STR_Baseplate" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
     *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                 &lt;/sequence>
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
        "strBaseplate"
    })
    public static class STRBaseplateList {

        @XmlElement(name = "STR_Baseplate", required = true)
        protected List<ATHERMALDATAINV.STRBaseplateList.STRBaseplate> strBaseplate;

        /**
         * Gets the value of the strBaseplate property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the strBaseplate property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSTRBaseplate().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ATHERMALDATAINV.STRBaseplateList.STRBaseplate }
         * 
         * 
         */
        public List<ATHERMALDATAINV.STRBaseplateList.STRBaseplate> getSTRBaseplate() {
            if (strBaseplate == null) {
                strBaseplate = new ArrayList<ATHERMALDATAINV.STRBaseplateList.STRBaseplate>();
            }
            return this.strBaseplate;
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
         *         &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
         *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
            "t",
            "gpstime"
        })
        public static class STRBaseplate {

            @XmlElement(name = "T", required = true)
            protected AINTWITHOHMUNITATTR t;
            @XmlElement(name = "GPS_TIME", required = true)
            protected XMLGregorianCalendar gpstime;

            /**
             * Obtient la valeur de la propriété t.
             * 
             * @return
             *     possible object is
             *     {@link AINTWITHOHMUNITATTR }
             *     
             */
            public AINTWITHOHMUNITATTR getT() {
                return t;
            }

            /**
             * Définit la valeur de la propriété t.
             * 
             * @param value
             *     allowed object is
             *     {@link AINTWITHOHMUNITATTR }
             *     
             */
            public void setT(AINTWITHOHMUNITATTR value) {
                this.t = value;
            }

            /**
             * Obtient la valeur de la propriété gpstime.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getGPSTIME() {
                return gpstime;
            }

            /**
             * Définit la valeur de la propriété gpstime.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setGPSTIME(XMLGregorianCalendar value) {
                this.gpstime = value;
            }

        }

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
     *         &lt;element name="STR_Sensorplate" maxOccurs="3">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Value" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
     *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="sensorplateId" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                       &lt;enumeration value="0"/>
     *                       &lt;enumeration value="1"/>
     *                       &lt;enumeration value="2"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
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
        "strSensorplate"
    })
    public static class STRSensorplateList {

        @XmlElement(name = "STR_Sensorplate", required = true)
        protected List<ATHERMALDATAINV.STRSensorplateList.STRSensorplate> strSensorplate;

        /**
         * Gets the value of the strSensorplate property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the strSensorplate property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSTRSensorplate().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ATHERMALDATAINV.STRSensorplateList.STRSensorplate }
         * 
         * 
         */
        public List<ATHERMALDATAINV.STRSensorplateList.STRSensorplate> getSTRSensorplate() {
            if (strSensorplate == null) {
                strSensorplate = new ArrayList<ATHERMALDATAINV.STRSensorplateList.STRSensorplate>();
            }
            return this.strSensorplate;
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
         *         &lt;element name="Value" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
         *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="sensorplateId" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *             &lt;enumeration value="0"/>
         *             &lt;enumeration value="1"/>
         *             &lt;enumeration value="2"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class STRSensorplate {

            @XmlElement(name = "Value", required = true)
            protected List<ATHERMALDATAINV.STRSensorplateList.STRSensorplate.Value> value;
            @XmlAttribute(name = "sensorplateId", required = true)
            protected int sensorplateId;

            /**
             * Gets the value of the value property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the value property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getValue().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ATHERMALDATAINV.STRSensorplateList.STRSensorplate.Value }
             * 
             * 
             */
            public List<ATHERMALDATAINV.STRSensorplateList.STRSensorplate.Value> getValue() {
                if (value == null) {
                    value = new ArrayList<ATHERMALDATAINV.STRSensorplateList.STRSensorplate.Value>();
                }
                return this.value;
            }

            /**
             * Obtient la valeur de la propriété sensorplateId.
             * 
             */
            public int getSensorplateId() {
                return sensorplateId;
            }

            /**
             * Définit la valeur de la propriété sensorplateId.
             * 
             */
            public void setSensorplateId(int value) {
                this.sensorplateId = value;
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
             *         &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_OHM_UNIT_ATTR"/>
             *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                "t",
                "gpstime"
            })
            public static class Value {

                @XmlElement(name = "T", required = true)
                protected AINTWITHOHMUNITATTR t;
                @XmlElement(name = "GPS_TIME", required = true)
                protected XMLGregorianCalendar gpstime;

                /**
                 * Obtient la valeur de la propriété t.
                 * 
                 * @return
                 *     possible object is
                 *     {@link AINTWITHOHMUNITATTR }
                 *     
                 */
                public AINTWITHOHMUNITATTR getT() {
                    return t;
                }

                /**
                 * Définit la valeur de la propriété t.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link AINTWITHOHMUNITATTR }
                 *     
                 */
                public void setT(AINTWITHOHMUNITATTR value) {
                    this.t = value;
                }

                /**
                 * Obtient la valeur de la propriété gpstime.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getGPSTIME() {
                    return gpstime;
                }

                /**
                 * Définit la valeur de la propriété gpstime.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setGPSTIME(XMLGregorianCalendar value) {
                    this.gpstime = value;
                }

            }

        }

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
     *         &lt;element name="Splitter" maxOccurs="2">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Value" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
     *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="splitterId" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="bot+X"/>
     *                       &lt;enumeration value="top-X"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
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
        "splitter"
    })
    public static class SplitterList {

        @XmlElement(name = "Splitter", required = true)
        protected List<ATHERMALDATAINV.SplitterList.Splitter> splitter;

        /**
         * Gets the value of the splitter property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the splitter property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSplitter().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ATHERMALDATAINV.SplitterList.Splitter }
         * 
         * 
         */
        public List<ATHERMALDATAINV.SplitterList.Splitter> getSplitter() {
            if (splitter == null) {
                splitter = new ArrayList<ATHERMALDATAINV.SplitterList.Splitter>();
            }
            return this.splitter;
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
         *         &lt;element name="Value" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
         *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="splitterId" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="bot+X"/>
         *             &lt;enumeration value="top-X"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Splitter {

            @XmlElement(name = "Value", required = true)
            protected List<ATHERMALDATAINV.SplitterList.Splitter.Value> value;
            @XmlAttribute(name = "splitterId", required = true)
            protected String splitterId;

            /**
             * Gets the value of the value property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the value property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getValue().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ATHERMALDATAINV.SplitterList.Splitter.Value }
             * 
             * 
             */
            public List<ATHERMALDATAINV.SplitterList.Splitter.Value> getValue() {
                if (value == null) {
                    value = new ArrayList<ATHERMALDATAINV.SplitterList.Splitter.Value>();
                }
                return this.value;
            }

            /**
             * Obtient la valeur de la propriété splitterId.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSplitterId() {
                return splitterId;
            }

            /**
             * Définit la valeur de la propriété splitterId.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSplitterId(String value) {
                this.splitterId = value;
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
             *         &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
             *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                "t",
                "gpstime"
            })
            public static class Value {

                @XmlElement(name = "T", required = true)
                protected ADOUBLEWITHDEGREECELSIUSUNITATTR t;
                @XmlElement(name = "GPS_TIME", required = true)
                protected XMLGregorianCalendar gpstime;

                /**
                 * Obtient la valeur de la propriété t.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                 *     
                 */
                public ADOUBLEWITHDEGREECELSIUSUNITATTR getT() {
                    return t;
                }

                /**
                 * Définit la valeur de la propriété t.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                 *     
                 */
                public void setT(ADOUBLEWITHDEGREECELSIUSUNITATTR value) {
                    this.t = value;
                }

                /**
                 * Obtient la valeur de la propriété gpstime.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getGPSTIME() {
                    return gpstime;
                }

                /**
                 * Définit la valeur de la propriété gpstime.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setGPSTIME(XMLGregorianCalendar value) {
                    this.gpstime = value;
                }

            }

        }

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
     *         &lt;element name="ThSensor" maxOccurs="5">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Value" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
     *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="sensorId" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="+X-Z"/>
     *                       &lt;enumeration value="-X-Z"/>
     *                       &lt;enumeration value="+Y"/>
     *                       &lt;enumeration value="-Y"/>
     *                       &lt;enumeration value="+Z"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
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
        "thSensor"
    })
    public static class ThSensorList {

        @XmlElement(name = "ThSensor", required = true)
        protected List<ATHERMALDATAINV.ThSensorList.ThSensor> thSensor;

        /**
         * Gets the value of the thSensor property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the thSensor property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getThSensor().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ATHERMALDATAINV.ThSensorList.ThSensor }
         * 
         * 
         */
        public List<ATHERMALDATAINV.ThSensorList.ThSensor> getThSensor() {
            if (thSensor == null) {
                thSensor = new ArrayList<ATHERMALDATAINV.ThSensorList.ThSensor>();
            }
            return this.thSensor;
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
         *         &lt;element name="Value" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
         *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="sensorId" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="+X-Z"/>
         *             &lt;enumeration value="-X-Z"/>
         *             &lt;enumeration value="+Y"/>
         *             &lt;enumeration value="-Y"/>
         *             &lt;enumeration value="+Z"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class ThSensor {

            @XmlElement(name = "Value", required = true)
            protected List<ATHERMALDATAINV.ThSensorList.ThSensor.Value> value;
            @XmlAttribute(name = "sensorId", required = true)
            protected String sensorId;

            /**
             * Gets the value of the value property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the value property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getValue().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ATHERMALDATAINV.ThSensorList.ThSensor.Value }
             * 
             * 
             */
            public List<ATHERMALDATAINV.ThSensorList.ThSensor.Value> getValue() {
                if (value == null) {
                    value = new ArrayList<ATHERMALDATAINV.ThSensorList.ThSensor.Value>();
                }
                return this.value;
            }

            /**
             * Obtient la valeur de la propriété sensorId.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSensorId() {
                return sensorId;
            }

            /**
             * Définit la valeur de la propriété sensorId.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSensorId(String value) {
                this.sensorId = value;
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
             *         &lt;element name="T" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
             *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                "t",
                "gpstime"
            })
            public static class Value {

                @XmlElement(name = "T", required = true)
                protected ADOUBLEWITHDEGREECELSIUSUNITATTR t;
                @XmlElement(name = "GPS_TIME", required = true)
                protected XMLGregorianCalendar gpstime;

                /**
                 * Obtient la valeur de la propriété t.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                 *     
                 */
                public ADOUBLEWITHDEGREECELSIUSUNITATTR getT() {
                    return t;
                }

                /**
                 * Définit la valeur de la propriété t.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                 *     
                 */
                public void setT(ADOUBLEWITHDEGREECELSIUSUNITATTR value) {
                    this.t = value;
                }

                /**
                 * Obtient la valeur de la propriété gpstime.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getGPSTIME() {
                    return gpstime;
                }

                /**
                 * Définit la valeur de la propriété gpstime.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setGPSTIME(XMLGregorianCalendar value) {
                    this.gpstime = value;
                }

            }

        }

    }

}
