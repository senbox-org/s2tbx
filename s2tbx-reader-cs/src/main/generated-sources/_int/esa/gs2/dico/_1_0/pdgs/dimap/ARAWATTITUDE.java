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
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHDEGREECELSIUSUNITATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHMVOLTUNITATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHSUNITATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ALISTOF3DOUBLEWITHDEGSATTR;
import _int.esa.gs2.dico._1_0.sy.platform.AQUATERNIONSTATUS;


/**
 * <p>Classe Java pour A_RAW_ATTITUDE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_RAW_ATTITUDE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="STR_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="STR" maxOccurs="3" minOccurs="2">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Attitude_Data_List">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="Attitude_Data" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="QUATERNION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION"/>
 *                                                 &lt;element name="QUATERNION_STATUS">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION_STATUS">
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="ANGULAR_RATE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE_WITH_DEGS_ATTR"/>
 *                                                 &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                                 &lt;element name="JULIAN_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_JULIAN_DAY_WITHOUT_SEC"/>
 *                                                 &lt;element name="ATTITUDE_QUALITY_INDICATOR">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                       &lt;enumeration value="SOL_INVALID"/>
 *                                                       &lt;enumeration value="SOL_PROPAG"/>
 *                                                       &lt;enumeration value="ONE_STRMEA_AVL"/>
 *                                                       &lt;enumeration value="TWO_STRMEA_AVL"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="RATE_QUALITY">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                       &lt;enumeration value="NO_RATE"/>
 *                                                       &lt;enumeration value="COARSE_RATE"/>
 *                                                       &lt;enumeration value="FINE_RATE"/>
 *                                                       &lt;enumeration value="FILTERED_RATE"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="VALIDITY_RATE">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                                       &lt;minInclusive value="0"/>
 *                                                       &lt;maxInclusive value="1"/>
 *                                                       &lt;enumeration value="1"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="Status_And_Health_Data_List">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="Status_And_Health_Data" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="OP_MODE">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                       &lt;enumeration value="BOOT"/>
 *                                                       &lt;enumeration value="STANDBY"/>
 *                                                       &lt;enumeration value="PHOTO"/>
 *                                                       &lt;enumeration value="AADF"/>
 *                                                       &lt;enumeration value="AADW"/>
 *                                                       &lt;enumeration value="NAT"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="TEC_MODE">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                       &lt;enumeration value="COOLER_OFF"/>
 *                                                       &lt;enumeration value="COOLER_CONTROLLED"/>
 *                                                       &lt;enumeration value="COOLER_MAXIMUM"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="TARGET" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
 *                                                 &lt;element name="DETECTOR" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
 *                                                 &lt;element name="OPTICS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
 *                                                 &lt;element name="HOUSING" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
 *                                                 &lt;element name="SYNC_SOURCE">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                       &lt;enumeration value="NONE"/>
 *                                                       &lt;enumeration value="PRIMARY"/>
 *                                                       &lt;enumeration value="SECONDARY"/>
 *                                                       &lt;enumeration value="ANY"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="SECONDS_SINCE_TIME_SYNC">
 *                                                   &lt;complexType>
 *                                                     &lt;simpleContent>
 *                                                       &lt;restriction base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_S_UNIT_ATTR">
 *                                                       &lt;/restriction>
 *                                                     &lt;/simpleContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="TRACKABLE_STARS">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                                       &lt;minInclusive value="1"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="TRACKED_STARS">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                                       &lt;minInclusive value="1"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="IDENTIFIED_STARS">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                                       &lt;minInclusive value="0"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="USED_STARS">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                                       &lt;minInclusive value="1"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="ATT_RESULT">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                       &lt;enumeration value="NOT_ENOUGH_STARS"/>
 *                                                       &lt;enumeration value="SUCCESS"/>
 *                                                       &lt;enumeration value="REFINED"/>
 *                                                       &lt;enumeration value="NA"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="ID_RESULT">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                       &lt;enumeration value="LDLE"/>
 *                                                       &lt;enumeration value="SUCCESS"/>
 *                                                       &lt;enumeration value="RUNNING"/>
 *                                                       &lt;enumeration value="NOT_ENOUGH_STARS"/>
 *                                                       &lt;enumeration value="NA"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="strId" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="STR1"/>
 *                                 &lt;enumeration value="STR2"/>
 *                                 &lt;enumeration value="STR3"/>
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
 *         &lt;element name="IMU_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="IMU" maxOccurs="4">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Value" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="FILTERED_ANGLE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                                       &lt;element name="RAW_ANGLE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                                       &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                       &lt;element name="Temperatures">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="ORGANISER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
 *                                                 &lt;element name="SIA" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
 *                                                 &lt;element name="OPTICAL_SOURCE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
 *                                                 &lt;element name="BOARD" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
 *                                                 &lt;element name="VOLTAGE_OFFSET" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
 *                                                 &lt;element name="VOLTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
 *                                                 &lt;element name="ACQUISITION">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT">
 *                                                       &lt;enumeration value="0"/>
 *                                                       &lt;enumeration value="1"/>
 *                                                       &lt;enumeration value="2"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
 *                                                 &lt;element name="TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_MILLISEC_DATE_TIME"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                       &lt;element name="ACQUISITION">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT">
 *                                             &lt;enumeration value="0"/>
 *                                             &lt;enumeration value="1"/>
 *                                             &lt;enumeration value="2"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
 *                                       &lt;element name="HEALTH_STATUS_BITS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="HEALTH_STATUS_BITS_VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="imuId" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="IMU1"/>
 *                                 &lt;enumeration value="IMU2"/>
 *                                 &lt;enumeration value="IMU3"/>
 *                                 &lt;enumeration value="IMU4"/>
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
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_RAW_ATTITUDE", propOrder = {
    "strList",
    "imuList"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.ANATTITUDEDATAINV.RawAttitudes.class
})
public class ARAWATTITUDE {

    @XmlElement(name = "STR_List", required = true)
    protected ARAWATTITUDE.STRList strList;
    @XmlElement(name = "IMU_List", required = true)
    protected ARAWATTITUDE.IMUList imuList;

    /**
     * Obtient la valeur de la propriété strList.
     * 
     * @return
     *     possible object is
     *     {@link ARAWATTITUDE.STRList }
     *     
     */
    public ARAWATTITUDE.STRList getSTRList() {
        return strList;
    }

    /**
     * Définit la valeur de la propriété strList.
     * 
     * @param value
     *     allowed object is
     *     {@link ARAWATTITUDE.STRList }
     *     
     */
    public void setSTRList(ARAWATTITUDE.STRList value) {
        this.strList = value;
    }

    /**
     * Obtient la valeur de la propriété imuList.
     * 
     * @return
     *     possible object is
     *     {@link ARAWATTITUDE.IMUList }
     *     
     */
    public ARAWATTITUDE.IMUList getIMUList() {
        return imuList;
    }

    /**
     * Définit la valeur de la propriété imuList.
     * 
     * @param value
     *     allowed object is
     *     {@link ARAWATTITUDE.IMUList }
     *     
     */
    public void setIMUList(ARAWATTITUDE.IMUList value) {
        this.imuList = value;
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
     *         &lt;element name="IMU" maxOccurs="4">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Value" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="FILTERED_ANGLE" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                             &lt;element name="RAW_ANGLE" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                             &lt;element name="Temperatures">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="ORGANISER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
     *                                       &lt;element name="SIA" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
     *                                       &lt;element name="OPTICAL_SOURCE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
     *                                       &lt;element name="BOARD" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
     *                                       &lt;element name="VOLTAGE_OFFSET" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
     *                                       &lt;element name="VOLTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
     *                                       &lt;element name="ACQUISITION">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT">
     *                                             &lt;enumeration value="0"/>
     *                                             &lt;enumeration value="1"/>
     *                                             &lt;enumeration value="2"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                       &lt;element name="VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
     *                                       &lt;element name="TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_MILLISEC_DATE_TIME"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                             &lt;element name="ACQUISITION">
     *                               &lt;simpleType>
     *                                 &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT">
     *                                   &lt;enumeration value="0"/>
     *                                   &lt;enumeration value="1"/>
     *                                   &lt;enumeration value="2"/>
     *                                 &lt;/restriction>
     *                               &lt;/simpleType>
     *                             &lt;/element>
     *                             &lt;element name="VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
     *                             &lt;element name="HEALTH_STATUS_BITS" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="HEALTH_STATUS_BITS_VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="imuId" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="IMU1"/>
     *                       &lt;enumeration value="IMU2"/>
     *                       &lt;enumeration value="IMU3"/>
     *                       &lt;enumeration value="IMU4"/>
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
        "imu"
    })
    public static class IMUList {

        @XmlElement(name = "IMU", required = true)
        protected List<ARAWATTITUDE.IMUList.IMU> imu;

        /**
         * Gets the value of the imu property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the imu property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getIMU().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ARAWATTITUDE.IMUList.IMU }
         * 
         * 
         */
        public List<ARAWATTITUDE.IMUList.IMU> getIMU() {
            if (imu == null) {
                imu = new ArrayList<ARAWATTITUDE.IMUList.IMU>();
            }
            return this.imu;
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
         *                   &lt;element name="FILTERED_ANGLE" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *                   &lt;element name="RAW_ANGLE" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                   &lt;element name="Temperatures">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="ORGANISER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
         *                             &lt;element name="SIA" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
         *                             &lt;element name="OPTICAL_SOURCE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
         *                             &lt;element name="BOARD" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
         *                             &lt;element name="VOLTAGE_OFFSET" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
         *                             &lt;element name="VOLTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
         *                             &lt;element name="ACQUISITION">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT">
         *                                   &lt;enumeration value="0"/>
         *                                   &lt;enumeration value="1"/>
         *                                   &lt;enumeration value="2"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
         *                             &lt;element name="VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
         *                             &lt;element name="TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_MILLISEC_DATE_TIME"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                   &lt;element name="ACQUISITION">
         *                     &lt;simpleType>
         *                       &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT">
         *                         &lt;enumeration value="0"/>
         *                         &lt;enumeration value="1"/>
         *                         &lt;enumeration value="2"/>
         *                       &lt;/restriction>
         *                     &lt;/simpleType>
         *                   &lt;/element>
         *                   &lt;element name="VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
         *                   &lt;element name="HEALTH_STATUS_BITS" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="HEALTH_STATUS_BITS_VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="imuId" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="IMU1"/>
         *             &lt;enumeration value="IMU2"/>
         *             &lt;enumeration value="IMU3"/>
         *             &lt;enumeration value="IMU4"/>
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
        public static class IMU {

            @XmlElement(name = "Value", required = true)
            protected List<ARAWATTITUDE.IMUList.IMU.Value> value;
            @XmlAttribute(name = "imuId", required = true)
            protected String imuId;

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
             * {@link ARAWATTITUDE.IMUList.IMU.Value }
             * 
             * 
             */
            public List<ARAWATTITUDE.IMUList.IMU.Value> getValue() {
                if (value == null) {
                    value = new ArrayList<ARAWATTITUDE.IMUList.IMU.Value>();
                }
                return this.value;
            }

            /**
             * Obtient la valeur de la propriété imuId.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getImuId() {
                return imuId;
            }

            /**
             * Définit la valeur de la propriété imuId.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setImuId(String value) {
                this.imuId = value;
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
             *         &lt;element name="FILTERED_ANGLE" type="{http://www.w3.org/2001/XMLSchema}int"/>
             *         &lt;element name="RAW_ANGLE" type="{http://www.w3.org/2001/XMLSchema}int"/>
             *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
             *         &lt;element name="Temperatures">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="ORGANISER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
             *                   &lt;element name="SIA" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
             *                   &lt;element name="OPTICAL_SOURCE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
             *                   &lt;element name="BOARD" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
             *                   &lt;element name="VOLTAGE_OFFSET" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
             *                   &lt;element name="VOLTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
             *                   &lt;element name="ACQUISITION">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT">
             *                         &lt;enumeration value="0"/>
             *                         &lt;enumeration value="1"/>
             *                         &lt;enumeration value="2"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
             *                   &lt;/element>
             *                   &lt;element name="VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
             *                   &lt;element name="TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_MILLISEC_DATE_TIME"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
             *         &lt;element name="ACQUISITION">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT">
             *               &lt;enumeration value="0"/>
             *               &lt;enumeration value="1"/>
             *               &lt;enumeration value="2"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
             *         &lt;/element>
             *         &lt;element name="VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
             *         &lt;element name="HEALTH_STATUS_BITS" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="HEALTH_STATUS_BITS_VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
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
                "filteredangle",
                "rawangle",
                "gpstime",
                "temperatures",
                "time",
                "acquisition",
                "validity",
                "healthstatusbits",
                "healthstatusbitsvalidity"
            })
            public static class Value {

                @XmlElement(name = "FILTERED_ANGLE")
                protected int filteredangle;
                @XmlElement(name = "RAW_ANGLE")
                protected int rawangle;
                @XmlElement(name = "GPS_TIME", required = true)
                protected XMLGregorianCalendar gpstime;
                @XmlElement(name = "Temperatures", required = true)
                protected ARAWATTITUDE.IMUList.IMU.Value.Temperatures temperatures;
                @XmlElement(name = "TIME", required = true)
                protected XMLGregorianCalendar time;
                @XmlElement(name = "ACQUISITION")
                protected int acquisition;
                @XmlElement(name = "VALIDITY")
                protected boolean validity;
                @XmlElement(name = "HEALTH_STATUS_BITS", required = true)
                protected String healthstatusbits;
                @XmlElement(name = "HEALTH_STATUS_BITS_VALIDITY")
                protected boolean healthstatusbitsvalidity;

                /**
                 * Obtient la valeur de la propriété filteredangle.
                 * 
                 */
                public int getFILTEREDANGLE() {
                    return filteredangle;
                }

                /**
                 * Définit la valeur de la propriété filteredangle.
                 * 
                 */
                public void setFILTEREDANGLE(int value) {
                    this.filteredangle = value;
                }

                /**
                 * Obtient la valeur de la propriété rawangle.
                 * 
                 */
                public int getRAWANGLE() {
                    return rawangle;
                }

                /**
                 * Définit la valeur de la propriété rawangle.
                 * 
                 */
                public void setRAWANGLE(int value) {
                    this.rawangle = value;
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

                /**
                 * Obtient la valeur de la propriété temperatures.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ARAWATTITUDE.IMUList.IMU.Value.Temperatures }
                 *     
                 */
                public ARAWATTITUDE.IMUList.IMU.Value.Temperatures getTemperatures() {
                    return temperatures;
                }

                /**
                 * Définit la valeur de la propriété temperatures.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ARAWATTITUDE.IMUList.IMU.Value.Temperatures }
                 *     
                 */
                public void setTemperatures(ARAWATTITUDE.IMUList.IMU.Value.Temperatures value) {
                    this.temperatures = value;
                }

                /**
                 * Obtient la valeur de la propriété time.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getTIME() {
                    return time;
                }

                /**
                 * Définit la valeur de la propriété time.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setTIME(XMLGregorianCalendar value) {
                    this.time = value;
                }

                /**
                 * Obtient la valeur de la propriété acquisition.
                 * 
                 */
                public int getACQUISITION() {
                    return acquisition;
                }

                /**
                 * Définit la valeur de la propriété acquisition.
                 * 
                 */
                public void setACQUISITION(int value) {
                    this.acquisition = value;
                }

                /**
                 * Obtient la valeur de la propriété validity.
                 * 
                 */
                public boolean isVALIDITY() {
                    return validity;
                }

                /**
                 * Définit la valeur de la propriété validity.
                 * 
                 */
                public void setVALIDITY(boolean value) {
                    this.validity = value;
                }

                /**
                 * Obtient la valeur de la propriété healthstatusbits.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getHEALTHSTATUSBITS() {
                    return healthstatusbits;
                }

                /**
                 * Définit la valeur de la propriété healthstatusbits.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setHEALTHSTATUSBITS(String value) {
                    this.healthstatusbits = value;
                }

                /**
                 * Obtient la valeur de la propriété healthstatusbitsvalidity.
                 * 
                 */
                public boolean isHEALTHSTATUSBITSVALIDITY() {
                    return healthstatusbitsvalidity;
                }

                /**
                 * Définit la valeur de la propriété healthstatusbitsvalidity.
                 * 
                 */
                public void setHEALTHSTATUSBITSVALIDITY(boolean value) {
                    this.healthstatusbitsvalidity = value;
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
                 *         &lt;element name="ORGANISER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
                 *         &lt;element name="SIA" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
                 *         &lt;element name="OPTICAL_SOURCE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
                 *         &lt;element name="BOARD" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
                 *         &lt;element name="VOLTAGE_OFFSET" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
                 *         &lt;element name="VOLTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MVOLT_UNIT_ATTR"/>
                 *         &lt;element name="ACQUISITION">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT">
                 *               &lt;enumeration value="0"/>
                 *               &lt;enumeration value="1"/>
                 *               &lt;enumeration value="2"/>
                 *             &lt;/restriction>
                 *           &lt;/simpleType>
                 *         &lt;/element>
                 *         &lt;element name="VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
                 *         &lt;element name="TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_MILLISEC_DATE_TIME"/>
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
                    "organiser",
                    "sia",
                    "opticalsource",
                    "board",
                    "voltageoffset",
                    "voltage",
                    "acquisition",
                    "validity",
                    "time"
                })
                public static class Temperatures {

                    @XmlElement(name = "ORGANISER", required = true)
                    protected ADOUBLEWITHMVOLTUNITATTR organiser;
                    @XmlElement(name = "SIA", required = true)
                    protected ADOUBLEWITHMVOLTUNITATTR sia;
                    @XmlElement(name = "OPTICAL_SOURCE", required = true)
                    protected ADOUBLEWITHMVOLTUNITATTR opticalsource;
                    @XmlElement(name = "BOARD", required = true)
                    protected ADOUBLEWITHMVOLTUNITATTR board;
                    @XmlElement(name = "VOLTAGE_OFFSET", required = true)
                    protected ADOUBLEWITHMVOLTUNITATTR voltageoffset;
                    @XmlElement(name = "VOLTAGE", required = true)
                    protected ADOUBLEWITHMVOLTUNITATTR voltage;
                    @XmlElement(name = "ACQUISITION")
                    protected int acquisition;
                    @XmlElement(name = "VALIDITY")
                    protected boolean validity;
                    @XmlElement(name = "TIME", required = true)
                    protected XMLGregorianCalendar time;

                    /**
                     * Obtient la valeur de la propriété organiser.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHMVOLTUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHMVOLTUNITATTR getORGANISER() {
                        return organiser;
                    }

                    /**
                     * Définit la valeur de la propriété organiser.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHMVOLTUNITATTR }
                     *     
                     */
                    public void setORGANISER(ADOUBLEWITHMVOLTUNITATTR value) {
                        this.organiser = value;
                    }

                    /**
                     * Obtient la valeur de la propriété sia.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHMVOLTUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHMVOLTUNITATTR getSIA() {
                        return sia;
                    }

                    /**
                     * Définit la valeur de la propriété sia.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHMVOLTUNITATTR }
                     *     
                     */
                    public void setSIA(ADOUBLEWITHMVOLTUNITATTR value) {
                        this.sia = value;
                    }

                    /**
                     * Obtient la valeur de la propriété opticalsource.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHMVOLTUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHMVOLTUNITATTR getOPTICALSOURCE() {
                        return opticalsource;
                    }

                    /**
                     * Définit la valeur de la propriété opticalsource.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHMVOLTUNITATTR }
                     *     
                     */
                    public void setOPTICALSOURCE(ADOUBLEWITHMVOLTUNITATTR value) {
                        this.opticalsource = value;
                    }

                    /**
                     * Obtient la valeur de la propriété board.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHMVOLTUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHMVOLTUNITATTR getBOARD() {
                        return board;
                    }

                    /**
                     * Définit la valeur de la propriété board.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHMVOLTUNITATTR }
                     *     
                     */
                    public void setBOARD(ADOUBLEWITHMVOLTUNITATTR value) {
                        this.board = value;
                    }

                    /**
                     * Obtient la valeur de la propriété voltageoffset.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHMVOLTUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHMVOLTUNITATTR getVOLTAGEOFFSET() {
                        return voltageoffset;
                    }

                    /**
                     * Définit la valeur de la propriété voltageoffset.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHMVOLTUNITATTR }
                     *     
                     */
                    public void setVOLTAGEOFFSET(ADOUBLEWITHMVOLTUNITATTR value) {
                        this.voltageoffset = value;
                    }

                    /**
                     * Obtient la valeur de la propriété voltage.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHMVOLTUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHMVOLTUNITATTR getVOLTAGE() {
                        return voltage;
                    }

                    /**
                     * Définit la valeur de la propriété voltage.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHMVOLTUNITATTR }
                     *     
                     */
                    public void setVOLTAGE(ADOUBLEWITHMVOLTUNITATTR value) {
                        this.voltage = value;
                    }

                    /**
                     * Obtient la valeur de la propriété acquisition.
                     * 
                     */
                    public int getACQUISITION() {
                        return acquisition;
                    }

                    /**
                     * Définit la valeur de la propriété acquisition.
                     * 
                     */
                    public void setACQUISITION(int value) {
                        this.acquisition = value;
                    }

                    /**
                     * Obtient la valeur de la propriété validity.
                     * 
                     */
                    public boolean isVALIDITY() {
                        return validity;
                    }

                    /**
                     * Définit la valeur de la propriété validity.
                     * 
                     */
                    public void setVALIDITY(boolean value) {
                        this.validity = value;
                    }

                    /**
                     * Obtient la valeur de la propriété time.
                     * 
                     * @return
                     *     possible object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public XMLGregorianCalendar getTIME() {
                        return time;
                    }

                    /**
                     * Définit la valeur de la propriété time.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public void setTIME(XMLGregorianCalendar value) {
                        this.time = value;
                    }

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
     *         &lt;element name="STR" maxOccurs="3" minOccurs="2">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Attitude_Data_List">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="Attitude_Data" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="QUATERNION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION"/>
     *                                       &lt;element name="QUATERNION_STATUS">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION_STATUS">
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                       &lt;element name="ANGULAR_RATE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE_WITH_DEGS_ATTR"/>
     *                                       &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                                       &lt;element name="JULIAN_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_JULIAN_DAY_WITHOUT_SEC"/>
     *                                       &lt;element name="ATTITUDE_QUALITY_INDICATOR">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                             &lt;enumeration value="SOL_INVALID"/>
     *                                             &lt;enumeration value="SOL_PROPAG"/>
     *                                             &lt;enumeration value="ONE_STRMEA_AVL"/>
     *                                             &lt;enumeration value="TWO_STRMEA_AVL"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                       &lt;element name="RATE_QUALITY">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                             &lt;enumeration value="NO_RATE"/>
     *                                             &lt;enumeration value="COARSE_RATE"/>
     *                                             &lt;enumeration value="FINE_RATE"/>
     *                                             &lt;enumeration value="FILTERED_RATE"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                       &lt;element name="VALIDITY_RATE">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                                             &lt;minInclusive value="0"/>
     *                                             &lt;maxInclusive value="1"/>
     *                                             &lt;enumeration value="1"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="Status_And_Health_Data_List">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="Status_And_Health_Data" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="OP_MODE">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                             &lt;enumeration value="BOOT"/>
     *                                             &lt;enumeration value="STANDBY"/>
     *                                             &lt;enumeration value="PHOTO"/>
     *                                             &lt;enumeration value="AADF"/>
     *                                             &lt;enumeration value="AADW"/>
     *                                             &lt;enumeration value="NAT"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                       &lt;element name="TEC_MODE">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                             &lt;enumeration value="COOLER_OFF"/>
     *                                             &lt;enumeration value="COOLER_CONTROLLED"/>
     *                                             &lt;enumeration value="COOLER_MAXIMUM"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                       &lt;element name="TARGET" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
     *                                       &lt;element name="DETECTOR" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
     *                                       &lt;element name="OPTICS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
     *                                       &lt;element name="HOUSING" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
     *                                       &lt;element name="SYNC_SOURCE">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                             &lt;enumeration value="NONE"/>
     *                                             &lt;enumeration value="PRIMARY"/>
     *                                             &lt;enumeration value="SECONDARY"/>
     *                                             &lt;enumeration value="ANY"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                       &lt;element name="SECONDS_SINCE_TIME_SYNC">
     *                                         &lt;complexType>
     *                                           &lt;simpleContent>
     *                                             &lt;restriction base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_S_UNIT_ATTR">
     *                                             &lt;/restriction>
     *                                           &lt;/simpleContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                       &lt;element name="TRACKABLE_STARS">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                                             &lt;minInclusive value="1"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                       &lt;element name="TRACKED_STARS">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                                             &lt;minInclusive value="1"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                       &lt;element name="IDENTIFIED_STARS">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                                             &lt;minInclusive value="0"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                       &lt;element name="USED_STARS">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                                             &lt;minInclusive value="1"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                       &lt;element name="ATT_RESULT">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                             &lt;enumeration value="NOT_ENOUGH_STARS"/>
     *                                             &lt;enumeration value="SUCCESS"/>
     *                                             &lt;enumeration value="REFINED"/>
     *                                             &lt;enumeration value="NA"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                       &lt;element name="ID_RESULT">
     *                                         &lt;simpleType>
     *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                             &lt;enumeration value="LDLE"/>
     *                                             &lt;enumeration value="SUCCESS"/>
     *                                             &lt;enumeration value="RUNNING"/>
     *                                             &lt;enumeration value="NOT_ENOUGH_STARS"/>
     *                                             &lt;enumeration value="NA"/>
     *                                           &lt;/restriction>
     *                                         &lt;/simpleType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="strId" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="STR1"/>
     *                       &lt;enumeration value="STR2"/>
     *                       &lt;enumeration value="STR3"/>
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
        "str"
    })
    public static class STRList {

        @XmlElement(name = "STR", required = true)
        protected List<ARAWATTITUDE.STRList.STR> str;

        /**
         * Gets the value of the str property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the str property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSTR().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ARAWATTITUDE.STRList.STR }
         * 
         * 
         */
        public List<ARAWATTITUDE.STRList.STR> getSTR() {
            if (str == null) {
                str = new ArrayList<ARAWATTITUDE.STRList.STR>();
            }
            return this.str;
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
         *         &lt;element name="Attitude_Data_List">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="Attitude_Data" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="QUATERNION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION"/>
         *                             &lt;element name="QUATERNION_STATUS">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION_STATUS">
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
         *                             &lt;element name="ANGULAR_RATE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE_WITH_DEGS_ATTR"/>
         *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                             &lt;element name="JULIAN_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_JULIAN_DAY_WITHOUT_SEC"/>
         *                             &lt;element name="ATTITUDE_QUALITY_INDICATOR">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                                   &lt;enumeration value="SOL_INVALID"/>
         *                                   &lt;enumeration value="SOL_PROPAG"/>
         *                                   &lt;enumeration value="ONE_STRMEA_AVL"/>
         *                                   &lt;enumeration value="TWO_STRMEA_AVL"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
         *                             &lt;element name="RATE_QUALITY">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                                   &lt;enumeration value="NO_RATE"/>
         *                                   &lt;enumeration value="COARSE_RATE"/>
         *                                   &lt;enumeration value="FINE_RATE"/>
         *                                   &lt;enumeration value="FILTERED_RATE"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
         *                             &lt;element name="VALIDITY_RATE">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *                                   &lt;minInclusive value="0"/>
         *                                   &lt;maxInclusive value="1"/>
         *                                   &lt;enumeration value="1"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
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
         *         &lt;element name="Status_And_Health_Data_List">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="Status_And_Health_Data" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="OP_MODE">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                                   &lt;enumeration value="BOOT"/>
         *                                   &lt;enumeration value="STANDBY"/>
         *                                   &lt;enumeration value="PHOTO"/>
         *                                   &lt;enumeration value="AADF"/>
         *                                   &lt;enumeration value="AADW"/>
         *                                   &lt;enumeration value="NAT"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
         *                             &lt;element name="TEC_MODE">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                                   &lt;enumeration value="COOLER_OFF"/>
         *                                   &lt;enumeration value="COOLER_CONTROLLED"/>
         *                                   &lt;enumeration value="COOLER_MAXIMUM"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
         *                             &lt;element name="TARGET" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
         *                             &lt;element name="DETECTOR" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
         *                             &lt;element name="OPTICS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
         *                             &lt;element name="HOUSING" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
         *                             &lt;element name="SYNC_SOURCE">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                                   &lt;enumeration value="NONE"/>
         *                                   &lt;enumeration value="PRIMARY"/>
         *                                   &lt;enumeration value="SECONDARY"/>
         *                                   &lt;enumeration value="ANY"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
         *                             &lt;element name="SECONDS_SINCE_TIME_SYNC">
         *                               &lt;complexType>
         *                                 &lt;simpleContent>
         *                                   &lt;restriction base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_S_UNIT_ATTR">
         *                                   &lt;/restriction>
         *                                 &lt;/simpleContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                             &lt;element name="TRACKABLE_STARS">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *                                   &lt;minInclusive value="1"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
         *                             &lt;element name="TRACKED_STARS">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *                                   &lt;minInclusive value="1"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
         *                             &lt;element name="IDENTIFIED_STARS">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *                                   &lt;minInclusive value="0"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
         *                             &lt;element name="USED_STARS">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *                                   &lt;minInclusive value="1"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
         *                             &lt;element name="ATT_RESULT">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                                   &lt;enumeration value="NOT_ENOUGH_STARS"/>
         *                                   &lt;enumeration value="SUCCESS"/>
         *                                   &lt;enumeration value="REFINED"/>
         *                                   &lt;enumeration value="NA"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
         *                             &lt;element name="ID_RESULT">
         *                               &lt;simpleType>
         *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                                   &lt;enumeration value="LDLE"/>
         *                                   &lt;enumeration value="SUCCESS"/>
         *                                   &lt;enumeration value="RUNNING"/>
         *                                   &lt;enumeration value="NOT_ENOUGH_STARS"/>
         *                                   &lt;enumeration value="NA"/>
         *                                 &lt;/restriction>
         *                               &lt;/simpleType>
         *                             &lt;/element>
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
         *       &lt;attribute name="strId" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="STR1"/>
         *             &lt;enumeration value="STR2"/>
         *             &lt;enumeration value="STR3"/>
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
            "attitudeDataList",
            "statusAndHealthDataList"
        })
        public static class STR {

            @XmlElement(name = "Attitude_Data_List", required = true)
            protected ARAWATTITUDE.STRList.STR.AttitudeDataList attitudeDataList;
            @XmlElement(name = "Status_And_Health_Data_List", required = true)
            protected ARAWATTITUDE.STRList.STR.StatusAndHealthDataList statusAndHealthDataList;
            @XmlAttribute(name = "strId", required = true)
            protected String strId;

            /**
             * Obtient la valeur de la propriété attitudeDataList.
             * 
             * @return
             *     possible object is
             *     {@link ARAWATTITUDE.STRList.STR.AttitudeDataList }
             *     
             */
            public ARAWATTITUDE.STRList.STR.AttitudeDataList getAttitudeDataList() {
                return attitudeDataList;
            }

            /**
             * Définit la valeur de la propriété attitudeDataList.
             * 
             * @param value
             *     allowed object is
             *     {@link ARAWATTITUDE.STRList.STR.AttitudeDataList }
             *     
             */
            public void setAttitudeDataList(ARAWATTITUDE.STRList.STR.AttitudeDataList value) {
                this.attitudeDataList = value;
            }

            /**
             * Obtient la valeur de la propriété statusAndHealthDataList.
             * 
             * @return
             *     possible object is
             *     {@link ARAWATTITUDE.STRList.STR.StatusAndHealthDataList }
             *     
             */
            public ARAWATTITUDE.STRList.STR.StatusAndHealthDataList getStatusAndHealthDataList() {
                return statusAndHealthDataList;
            }

            /**
             * Définit la valeur de la propriété statusAndHealthDataList.
             * 
             * @param value
             *     allowed object is
             *     {@link ARAWATTITUDE.STRList.STR.StatusAndHealthDataList }
             *     
             */
            public void setStatusAndHealthDataList(ARAWATTITUDE.STRList.STR.StatusAndHealthDataList value) {
                this.statusAndHealthDataList = value;
            }

            /**
             * Obtient la valeur de la propriété strId.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getStrId() {
                return strId;
            }

            /**
             * Définit la valeur de la propriété strId.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setStrId(String value) {
                this.strId = value;
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
             *         &lt;element name="Attitude_Data" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="QUATERNION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION"/>
             *                   &lt;element name="QUATERNION_STATUS">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION_STATUS">
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
             *                   &lt;/element>
             *                   &lt;element name="ANGULAR_RATE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE_WITH_DEGS_ATTR"/>
             *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
             *                   &lt;element name="JULIAN_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_JULIAN_DAY_WITHOUT_SEC"/>
             *                   &lt;element name="ATTITUDE_QUALITY_INDICATOR">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *                         &lt;enumeration value="SOL_INVALID"/>
             *                         &lt;enumeration value="SOL_PROPAG"/>
             *                         &lt;enumeration value="ONE_STRMEA_AVL"/>
             *                         &lt;enumeration value="TWO_STRMEA_AVL"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
             *                   &lt;/element>
             *                   &lt;element name="RATE_QUALITY">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *                         &lt;enumeration value="NO_RATE"/>
             *                         &lt;enumeration value="COARSE_RATE"/>
             *                         &lt;enumeration value="FINE_RATE"/>
             *                         &lt;enumeration value="FILTERED_RATE"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
             *                   &lt;/element>
             *                   &lt;element name="VALIDITY_RATE">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
             *                         &lt;minInclusive value="0"/>
             *                         &lt;maxInclusive value="1"/>
             *                         &lt;enumeration value="1"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
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
            @XmlType(name = "", propOrder = {
                "attitudeData"
            })
            public static class AttitudeDataList {

                @XmlElement(name = "Attitude_Data", required = true)
                protected List<ARAWATTITUDE.STRList.STR.AttitudeDataList.AttitudeData> attitudeData;

                /**
                 * Gets the value of the attitudeData property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the attitudeData property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getAttitudeData().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link ARAWATTITUDE.STRList.STR.AttitudeDataList.AttitudeData }
                 * 
                 * 
                 */
                public List<ARAWATTITUDE.STRList.STR.AttitudeDataList.AttitudeData> getAttitudeData() {
                    if (attitudeData == null) {
                        attitudeData = new ArrayList<ARAWATTITUDE.STRList.STR.AttitudeDataList.AttitudeData>();
                    }
                    return this.attitudeData;
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
                 *         &lt;element name="QUATERNION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION"/>
                 *         &lt;element name="QUATERNION_STATUS">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION_STATUS">
                 *             &lt;/restriction>
                 *           &lt;/simpleType>
                 *         &lt;/element>
                 *         &lt;element name="ANGULAR_RATE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE_WITH_DEGS_ATTR"/>
                 *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
                 *         &lt;element name="JULIAN_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_JULIAN_DAY_WITHOUT_SEC"/>
                 *         &lt;element name="ATTITUDE_QUALITY_INDICATOR">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
                 *               &lt;enumeration value="SOL_INVALID"/>
                 *               &lt;enumeration value="SOL_PROPAG"/>
                 *               &lt;enumeration value="ONE_STRMEA_AVL"/>
                 *               &lt;enumeration value="TWO_STRMEA_AVL"/>
                 *             &lt;/restriction>
                 *           &lt;/simpleType>
                 *         &lt;/element>
                 *         &lt;element name="RATE_QUALITY">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
                 *               &lt;enumeration value="NO_RATE"/>
                 *               &lt;enumeration value="COARSE_RATE"/>
                 *               &lt;enumeration value="FINE_RATE"/>
                 *               &lt;enumeration value="FILTERED_RATE"/>
                 *             &lt;/restriction>
                 *           &lt;/simpleType>
                 *         &lt;/element>
                 *         &lt;element name="VALIDITY_RATE">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
                 *               &lt;minInclusive value="0"/>
                 *               &lt;maxInclusive value="1"/>
                 *               &lt;enumeration value="1"/>
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
                    "quaternionvalues",
                    "quaternionstatus",
                    "angularrate",
                    "gpstime",
                    "juliandate",
                    "attitudequalityindicator",
                    "ratequality",
                    "validityrate"
                })
                public static class AttitudeData {

                    @XmlList
                    @XmlElement(name = "QUATERNION_VALUES", type = Double.class)
                    protected List<Double> quaternionvalues;
                    @XmlElement(name = "QUATERNION_STATUS", required = true)
                    protected AQUATERNIONSTATUS quaternionstatus;
                    @XmlElement(name = "ANGULAR_RATE", required = true)
                    protected ALISTOF3DOUBLEWITHDEGSATTR angularrate;
                    @XmlElement(name = "GPS_TIME", required = true)
                    protected XMLGregorianCalendar gpstime;
                    @XmlElement(name = "JULIAN_DATE")
                    protected int juliandate;
                    @XmlElement(name = "ATTITUDE_QUALITY_INDICATOR", required = true)
                    protected String attitudequalityindicator;
                    @XmlElement(name = "RATE_QUALITY", required = true)
                    protected String ratequality;
                    @XmlElement(name = "VALIDITY_RATE")
                    protected int validityrate;

                    /**
                     * Gets the value of the quaternionvalues property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the quaternionvalues property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getQUATERNIONVALUES().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link Double }
                     * 
                     * 
                     */
                    public List<Double> getQUATERNIONVALUES() {
                        if (quaternionvalues == null) {
                            quaternionvalues = new ArrayList<Double>();
                        }
                        return this.quaternionvalues;
                    }

                    /**
                     * Obtient la valeur de la propriété quaternionstatus.
                     * 
                     * @return
                     *     possible object is
                     *     {@link AQUATERNIONSTATUS }
                     *     
                     */
                    public AQUATERNIONSTATUS getQUATERNIONSTATUS() {
                        return quaternionstatus;
                    }

                    /**
                     * Définit la valeur de la propriété quaternionstatus.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link AQUATERNIONSTATUS }
                     *     
                     */
                    public void setQUATERNIONSTATUS(AQUATERNIONSTATUS value) {
                        this.quaternionstatus = value;
                    }

                    /**
                     * Obtient la valeur de la propriété angularrate.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ALISTOF3DOUBLEWITHDEGSATTR }
                     *     
                     */
                    public ALISTOF3DOUBLEWITHDEGSATTR getANGULARRATE() {
                        return angularrate;
                    }

                    /**
                     * Définit la valeur de la propriété angularrate.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ALISTOF3DOUBLEWITHDEGSATTR }
                     *     
                     */
                    public void setANGULARRATE(ALISTOF3DOUBLEWITHDEGSATTR value) {
                        this.angularrate = value;
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

                    /**
                     * Obtient la valeur de la propriété juliandate.
                     * 
                     */
                    public int getJULIANDATE() {
                        return juliandate;
                    }

                    /**
                     * Définit la valeur de la propriété juliandate.
                     * 
                     */
                    public void setJULIANDATE(int value) {
                        this.juliandate = value;
                    }

                    /**
                     * Obtient la valeur de la propriété attitudequalityindicator.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getATTITUDEQUALITYINDICATOR() {
                        return attitudequalityindicator;
                    }

                    /**
                     * Définit la valeur de la propriété attitudequalityindicator.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setATTITUDEQUALITYINDICATOR(String value) {
                        this.attitudequalityindicator = value;
                    }

                    /**
                     * Obtient la valeur de la propriété ratequality.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getRATEQUALITY() {
                        return ratequality;
                    }

                    /**
                     * Définit la valeur de la propriété ratequality.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setRATEQUALITY(String value) {
                        this.ratequality = value;
                    }

                    /**
                     * Obtient la valeur de la propriété validityrate.
                     * 
                     */
                    public int getVALIDITYRATE() {
                        return validityrate;
                    }

                    /**
                     * Définit la valeur de la propriété validityrate.
                     * 
                     */
                    public void setVALIDITYRATE(int value) {
                        this.validityrate = value;
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
             *         &lt;element name="Status_And_Health_Data" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="OP_MODE">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *                         &lt;enumeration value="BOOT"/>
             *                         &lt;enumeration value="STANDBY"/>
             *                         &lt;enumeration value="PHOTO"/>
             *                         &lt;enumeration value="AADF"/>
             *                         &lt;enumeration value="AADW"/>
             *                         &lt;enumeration value="NAT"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
             *                   &lt;/element>
             *                   &lt;element name="TEC_MODE">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *                         &lt;enumeration value="COOLER_OFF"/>
             *                         &lt;enumeration value="COOLER_CONTROLLED"/>
             *                         &lt;enumeration value="COOLER_MAXIMUM"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
             *                   &lt;/element>
             *                   &lt;element name="TARGET" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
             *                   &lt;element name="DETECTOR" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
             *                   &lt;element name="OPTICS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
             *                   &lt;element name="HOUSING" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
             *                   &lt;element name="SYNC_SOURCE">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *                         &lt;enumeration value="NONE"/>
             *                         &lt;enumeration value="PRIMARY"/>
             *                         &lt;enumeration value="SECONDARY"/>
             *                         &lt;enumeration value="ANY"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
             *                   &lt;/element>
             *                   &lt;element name="SECONDS_SINCE_TIME_SYNC">
             *                     &lt;complexType>
             *                       &lt;simpleContent>
             *                         &lt;restriction base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_S_UNIT_ATTR">
             *                         &lt;/restriction>
             *                       &lt;/simpleContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                   &lt;element name="TRACKABLE_STARS">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
             *                         &lt;minInclusive value="1"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
             *                   &lt;/element>
             *                   &lt;element name="TRACKED_STARS">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
             *                         &lt;minInclusive value="1"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
             *                   &lt;/element>
             *                   &lt;element name="IDENTIFIED_STARS">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
             *                         &lt;minInclusive value="0"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
             *                   &lt;/element>
             *                   &lt;element name="USED_STARS">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
             *                         &lt;minInclusive value="1"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
             *                   &lt;/element>
             *                   &lt;element name="ATT_RESULT">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *                         &lt;enumeration value="NOT_ENOUGH_STARS"/>
             *                         &lt;enumeration value="SUCCESS"/>
             *                         &lt;enumeration value="REFINED"/>
             *                         &lt;enumeration value="NA"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
             *                   &lt;/element>
             *                   &lt;element name="ID_RESULT">
             *                     &lt;simpleType>
             *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *                         &lt;enumeration value="LDLE"/>
             *                         &lt;enumeration value="SUCCESS"/>
             *                         &lt;enumeration value="RUNNING"/>
             *                         &lt;enumeration value="NOT_ENOUGH_STARS"/>
             *                         &lt;enumeration value="NA"/>
             *                       &lt;/restriction>
             *                     &lt;/simpleType>
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
            @XmlType(name = "", propOrder = {
                "statusAndHealthData"
            })
            public static class StatusAndHealthDataList {

                @XmlElement(name = "Status_And_Health_Data", required = true)
                protected List<ARAWATTITUDE.STRList.STR.StatusAndHealthDataList.StatusAndHealthData> statusAndHealthData;

                /**
                 * Gets the value of the statusAndHealthData property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the statusAndHealthData property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getStatusAndHealthData().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link ARAWATTITUDE.STRList.STR.StatusAndHealthDataList.StatusAndHealthData }
                 * 
                 * 
                 */
                public List<ARAWATTITUDE.STRList.STR.StatusAndHealthDataList.StatusAndHealthData> getStatusAndHealthData() {
                    if (statusAndHealthData == null) {
                        statusAndHealthData = new ArrayList<ARAWATTITUDE.STRList.STR.StatusAndHealthDataList.StatusAndHealthData>();
                    }
                    return this.statusAndHealthData;
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
                 *         &lt;element name="OP_MODE">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
                 *               &lt;enumeration value="BOOT"/>
                 *               &lt;enumeration value="STANDBY"/>
                 *               &lt;enumeration value="PHOTO"/>
                 *               &lt;enumeration value="AADF"/>
                 *               &lt;enumeration value="AADW"/>
                 *               &lt;enumeration value="NAT"/>
                 *             &lt;/restriction>
                 *           &lt;/simpleType>
                 *         &lt;/element>
                 *         &lt;element name="TEC_MODE">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
                 *               &lt;enumeration value="COOLER_OFF"/>
                 *               &lt;enumeration value="COOLER_CONTROLLED"/>
                 *               &lt;enumeration value="COOLER_MAXIMUM"/>
                 *             &lt;/restriction>
                 *           &lt;/simpleType>
                 *         &lt;/element>
                 *         &lt;element name="TARGET" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
                 *         &lt;element name="DETECTOR" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
                 *         &lt;element name="OPTICS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
                 *         &lt;element name="HOUSING" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEGREE_CELSIUS_UNIT_ATTR"/>
                 *         &lt;element name="SYNC_SOURCE">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
                 *               &lt;enumeration value="NONE"/>
                 *               &lt;enumeration value="PRIMARY"/>
                 *               &lt;enumeration value="SECONDARY"/>
                 *               &lt;enumeration value="ANY"/>
                 *             &lt;/restriction>
                 *           &lt;/simpleType>
                 *         &lt;/element>
                 *         &lt;element name="SECONDS_SINCE_TIME_SYNC">
                 *           &lt;complexType>
                 *             &lt;simpleContent>
                 *               &lt;restriction base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_S_UNIT_ATTR">
                 *               &lt;/restriction>
                 *             &lt;/simpleContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *         &lt;element name="TRACKABLE_STARS">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
                 *               &lt;minInclusive value="1"/>
                 *             &lt;/restriction>
                 *           &lt;/simpleType>
                 *         &lt;/element>
                 *         &lt;element name="TRACKED_STARS">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
                 *               &lt;minInclusive value="1"/>
                 *             &lt;/restriction>
                 *           &lt;/simpleType>
                 *         &lt;/element>
                 *         &lt;element name="IDENTIFIED_STARS">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
                 *               &lt;minInclusive value="0"/>
                 *             &lt;/restriction>
                 *           &lt;/simpleType>
                 *         &lt;/element>
                 *         &lt;element name="USED_STARS">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
                 *               &lt;minInclusive value="1"/>
                 *             &lt;/restriction>
                 *           &lt;/simpleType>
                 *         &lt;/element>
                 *         &lt;element name="ATT_RESULT">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
                 *               &lt;enumeration value="NOT_ENOUGH_STARS"/>
                 *               &lt;enumeration value="SUCCESS"/>
                 *               &lt;enumeration value="REFINED"/>
                 *               &lt;enumeration value="NA"/>
                 *             &lt;/restriction>
                 *           &lt;/simpleType>
                 *         &lt;/element>
                 *         &lt;element name="ID_RESULT">
                 *           &lt;simpleType>
                 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
                 *               &lt;enumeration value="LDLE"/>
                 *               &lt;enumeration value="SUCCESS"/>
                 *               &lt;enumeration value="RUNNING"/>
                 *               &lt;enumeration value="NOT_ENOUGH_STARS"/>
                 *               &lt;enumeration value="NA"/>
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
                    "opmode",
                    "tecmode",
                    "target",
                    "detector",
                    "optics",
                    "housing",
                    "syncsource",
                    "secondssincetimesync",
                    "trackablestars",
                    "trackedstars",
                    "identifiedstars",
                    "usedstars",
                    "attresult",
                    "idresult"
                })
                public static class StatusAndHealthData {

                    @XmlElement(name = "OP_MODE", required = true)
                    protected String opmode;
                    @XmlElement(name = "TEC_MODE", required = true)
                    protected String tecmode;
                    @XmlElement(name = "TARGET", required = true)
                    protected ADOUBLEWITHDEGREECELSIUSUNITATTR target;
                    @XmlElement(name = "DETECTOR", required = true)
                    protected ADOUBLEWITHDEGREECELSIUSUNITATTR detector;
                    @XmlElement(name = "OPTICS", required = true)
                    protected ADOUBLEWITHDEGREECELSIUSUNITATTR optics;
                    @XmlElement(name = "HOUSING", required = true)
                    protected ADOUBLEWITHDEGREECELSIUSUNITATTR housing;
                    @XmlElement(name = "SYNC_SOURCE", required = true)
                    protected String syncsource;
                    @XmlElement(name = "SECONDS_SINCE_TIME_SYNC", required = true)
                    protected ARAWATTITUDE.STRList.STR.StatusAndHealthDataList.StatusAndHealthData.SECONDSSINCETIMESYNC secondssincetimesync;
                    @XmlElement(name = "TRACKABLE_STARS")
                    protected int trackablestars;
                    @XmlElement(name = "TRACKED_STARS")
                    protected int trackedstars;
                    @XmlElement(name = "IDENTIFIED_STARS")
                    protected int identifiedstars;
                    @XmlElement(name = "USED_STARS")
                    protected int usedstars;
                    @XmlElement(name = "ATT_RESULT", required = true)
                    protected String attresult;
                    @XmlElement(name = "ID_RESULT", required = true)
                    protected String idresult;

                    /**
                     * Obtient la valeur de la propriété opmode.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getOPMODE() {
                        return opmode;
                    }

                    /**
                     * Définit la valeur de la propriété opmode.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setOPMODE(String value) {
                        this.opmode = value;
                    }

                    /**
                     * Obtient la valeur de la propriété tecmode.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getTECMODE() {
                        return tecmode;
                    }

                    /**
                     * Définit la valeur de la propriété tecmode.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setTECMODE(String value) {
                        this.tecmode = value;
                    }

                    /**
                     * Obtient la valeur de la propriété target.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHDEGREECELSIUSUNITATTR getTARGET() {
                        return target;
                    }

                    /**
                     * Définit la valeur de la propriété target.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                     *     
                     */
                    public void setTARGET(ADOUBLEWITHDEGREECELSIUSUNITATTR value) {
                        this.target = value;
                    }

                    /**
                     * Obtient la valeur de la propriété detector.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHDEGREECELSIUSUNITATTR getDETECTOR() {
                        return detector;
                    }

                    /**
                     * Définit la valeur de la propriété detector.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                     *     
                     */
                    public void setDETECTOR(ADOUBLEWITHDEGREECELSIUSUNITATTR value) {
                        this.detector = value;
                    }

                    /**
                     * Obtient la valeur de la propriété optics.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHDEGREECELSIUSUNITATTR getOPTICS() {
                        return optics;
                    }

                    /**
                     * Définit la valeur de la propriété optics.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                     *     
                     */
                    public void setOPTICS(ADOUBLEWITHDEGREECELSIUSUNITATTR value) {
                        this.optics = value;
                    }

                    /**
                     * Obtient la valeur de la propriété housing.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHDEGREECELSIUSUNITATTR getHOUSING() {
                        return housing;
                    }

                    /**
                     * Définit la valeur de la propriété housing.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHDEGREECELSIUSUNITATTR }
                     *     
                     */
                    public void setHOUSING(ADOUBLEWITHDEGREECELSIUSUNITATTR value) {
                        this.housing = value;
                    }

                    /**
                     * Obtient la valeur de la propriété syncsource.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getSYNCSOURCE() {
                        return syncsource;
                    }

                    /**
                     * Définit la valeur de la propriété syncsource.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setSYNCSOURCE(String value) {
                        this.syncsource = value;
                    }

                    /**
                     * Obtient la valeur de la propriété secondssincetimesync.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ARAWATTITUDE.STRList.STR.StatusAndHealthDataList.StatusAndHealthData.SECONDSSINCETIMESYNC }
                     *     
                     */
                    public ARAWATTITUDE.STRList.STR.StatusAndHealthDataList.StatusAndHealthData.SECONDSSINCETIMESYNC getSECONDSSINCETIMESYNC() {
                        return secondssincetimesync;
                    }

                    /**
                     * Définit la valeur de la propriété secondssincetimesync.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ARAWATTITUDE.STRList.STR.StatusAndHealthDataList.StatusAndHealthData.SECONDSSINCETIMESYNC }
                     *     
                     */
                    public void setSECONDSSINCETIMESYNC(ARAWATTITUDE.STRList.STR.StatusAndHealthDataList.StatusAndHealthData.SECONDSSINCETIMESYNC value) {
                        this.secondssincetimesync = value;
                    }

                    /**
                     * Obtient la valeur de la propriété trackablestars.
                     * 
                     */
                    public int getTRACKABLESTARS() {
                        return trackablestars;
                    }

                    /**
                     * Définit la valeur de la propriété trackablestars.
                     * 
                     */
                    public void setTRACKABLESTARS(int value) {
                        this.trackablestars = value;
                    }

                    /**
                     * Obtient la valeur de la propriété trackedstars.
                     * 
                     */
                    public int getTRACKEDSTARS() {
                        return trackedstars;
                    }

                    /**
                     * Définit la valeur de la propriété trackedstars.
                     * 
                     */
                    public void setTRACKEDSTARS(int value) {
                        this.trackedstars = value;
                    }

                    /**
                     * Obtient la valeur de la propriété identifiedstars.
                     * 
                     */
                    public int getIDENTIFIEDSTARS() {
                        return identifiedstars;
                    }

                    /**
                     * Définit la valeur de la propriété identifiedstars.
                     * 
                     */
                    public void setIDENTIFIEDSTARS(int value) {
                        this.identifiedstars = value;
                    }

                    /**
                     * Obtient la valeur de la propriété usedstars.
                     * 
                     */
                    public int getUSEDSTARS() {
                        return usedstars;
                    }

                    /**
                     * Définit la valeur de la propriété usedstars.
                     * 
                     */
                    public void setUSEDSTARS(int value) {
                        this.usedstars = value;
                    }

                    /**
                     * Obtient la valeur de la propriété attresult.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getATTRESULT() {
                        return attresult;
                    }

                    /**
                     * Définit la valeur de la propriété attresult.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setATTRESULT(String value) {
                        this.attresult = value;
                    }

                    /**
                     * Obtient la valeur de la propriété idresult.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getIDRESULT() {
                        return idresult;
                    }

                    /**
                     * Définit la valeur de la propriété idresult.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setIDRESULT(String value) {
                        this.idresult = value;
                    }


                    /**
                     * <p>Classe Java pour anonymous complex type.
                     * 
                     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;simpleContent>
                     *     &lt;restriction base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_S_UNIT_ATTR">
                     *     &lt;/restriction>
                     *   &lt;/simpleContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "")
                    public static class SECONDSSINCETIMESYNC
                        extends ADOUBLEWITHSUNITATTR
                    {


                    }

                }

            }

        }

    }

}
