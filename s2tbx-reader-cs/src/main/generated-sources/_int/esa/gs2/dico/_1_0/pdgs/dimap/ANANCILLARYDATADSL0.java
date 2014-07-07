//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.image.ASOURCEPACKETDEGRADATIONTYPE;


/**
 * <p>Classe Java pour AN_ANCILLARY_DATA_DSL0 complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_ANCILLARY_DATA_DSL0">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Time_Correlation_Data_List" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_TIME_CORRELATION_DATA_LIST">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Ephemeris">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_EPHEMERIS_DATA_INV">
 *                 &lt;sequence>
 *                   &lt;element name="POD_Info" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="POD_FLAG" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                             &lt;element name="POD_FILENAME" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Attitudes" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ATTITUDE_DATA_INV"/>
 *         &lt;element name="Thermal_Data">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_THERMAL_DATA_INV">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Lost_Source_Packet_List" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Lost_Source_Packet" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="DEGRADATION_TYPE" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_SOURCE_PACKET_DEGRADATION_TYPE"/>
 *                             &lt;element name="ERROR_BEGINNING_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                             &lt;element name="ERROR_ENDING_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="processIdentifier" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
 *                                 &lt;minInclusive value="0"/>
 *                                 &lt;maxInclusive value="127"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="packetCategory" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
 *                                 &lt;minInclusive value="0"/>
 *                                 &lt;maxInclusive value="15"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="service" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
 *                                 &lt;minInclusive value="0"/>
 *                                 &lt;maxInclusive value="255"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="subService" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
 *                                 &lt;minInclusive value="0"/>
 *                                 &lt;maxInclusive value="255"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="structurIdentifier" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
 *                                 &lt;minInclusive value="0"/>
 *                                 &lt;maxInclusive value="255"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Degradation_Summary">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="NUMBER_OF_LOST_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *                             &lt;element name="NUMBER_OF_DEGRADED_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="degradationPercentage">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
 *                                 &lt;minInclusive value="0"/>
 *                                 &lt;maxInclusive value="100"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ANC_DATA_REF" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
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
@XmlType(name = "AN_ANCILLARY_DATA_DSL0", propOrder = {
    "timeCorrelationDataList",
    "ephemeris",
    "attitudes",
    "thermalData",
    "lostSourcePacketList",
    "ancdataref"
})
public class ANANCILLARYDATADSL0 {

    @XmlElement(name = "Time_Correlation_Data_List")
    protected ANANCILLARYDATADSL0 .TimeCorrelationDataList timeCorrelationDataList;
    @XmlElement(name = "Ephemeris", required = true)
    protected ANANCILLARYDATADSL0 .Ephemeris ephemeris;
    @XmlElement(name = "Attitudes", required = true)
    protected ANATTITUDEDATAINV attitudes;
    @XmlElement(name = "Thermal_Data", required = true)
    protected ANANCILLARYDATADSL0 .ThermalData thermalData;
    @XmlElement(name = "Lost_Source_Packet_List")
    protected ANANCILLARYDATADSL0 .LostSourcePacketList lostSourcePacketList;
    @XmlElement(name = "ANC_DATA_REF")
    protected ANANCILLARYDATADSL0 .ANCDATAREF ancdataref;

    /**
     * Obtient la valeur de la propriété timeCorrelationDataList.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATADSL0 .TimeCorrelationDataList }
     *     
     */
    public ANANCILLARYDATADSL0 .TimeCorrelationDataList getTimeCorrelationDataList() {
        return timeCorrelationDataList;
    }

    /**
     * Définit la valeur de la propriété timeCorrelationDataList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATADSL0 .TimeCorrelationDataList }
     *     
     */
    public void setTimeCorrelationDataList(ANANCILLARYDATADSL0 .TimeCorrelationDataList value) {
        this.timeCorrelationDataList = value;
    }

    /**
     * Obtient la valeur de la propriété ephemeris.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATADSL0 .Ephemeris }
     *     
     */
    public ANANCILLARYDATADSL0 .Ephemeris getEphemeris() {
        return ephemeris;
    }

    /**
     * Définit la valeur de la propriété ephemeris.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATADSL0 .Ephemeris }
     *     
     */
    public void setEphemeris(ANANCILLARYDATADSL0 .Ephemeris value) {
        this.ephemeris = value;
    }

    /**
     * Obtient la valeur de la propriété attitudes.
     * 
     * @return
     *     possible object is
     *     {@link ANATTITUDEDATAINV }
     *     
     */
    public ANATTITUDEDATAINV getAttitudes() {
        return attitudes;
    }

    /**
     * Définit la valeur de la propriété attitudes.
     * 
     * @param value
     *     allowed object is
     *     {@link ANATTITUDEDATAINV }
     *     
     */
    public void setAttitudes(ANATTITUDEDATAINV value) {
        this.attitudes = value;
    }

    /**
     * Obtient la valeur de la propriété thermalData.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATADSL0 .ThermalData }
     *     
     */
    public ANANCILLARYDATADSL0 .ThermalData getThermalData() {
        return thermalData;
    }

    /**
     * Définit la valeur de la propriété thermalData.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATADSL0 .ThermalData }
     *     
     */
    public void setThermalData(ANANCILLARYDATADSL0 .ThermalData value) {
        this.thermalData = value;
    }

    /**
     * Obtient la valeur de la propriété lostSourcePacketList.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATADSL0 .LostSourcePacketList }
     *     
     */
    public ANANCILLARYDATADSL0 .LostSourcePacketList getLostSourcePacketList() {
        return lostSourcePacketList;
    }

    /**
     * Définit la valeur de la propriété lostSourcePacketList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATADSL0 .LostSourcePacketList }
     *     
     */
    public void setLostSourcePacketList(ANANCILLARYDATADSL0 .LostSourcePacketList value) {
        this.lostSourcePacketList = value;
    }

    /**
     * Obtient la valeur de la propriété ancdataref.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATADSL0 .ANCDATAREF }
     *     
     */
    public ANANCILLARYDATADSL0 .ANCDATAREF getANCDATAREF() {
        return ancdataref;
    }

    /**
     * Définit la valeur de la propriété ancdataref.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATADSL0 .ANCDATAREF }
     *     
     */
    public void setANCDATAREF(ANANCILLARYDATADSL0 .ANCDATAREF value) {
        this.ancdataref = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class ANCDATAREF {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Obtient la valeur de la propriété value.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propriété metadataLevel.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMetadataLevel() {
            if (metadataLevel == null) {
                return "Standard";
            } else {
                return metadataLevel;
            }
        }

        /**
         * Définit la valeur de la propriété metadataLevel.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMetadataLevel(String value) {
            this.metadataLevel = value;
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
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_EPHEMERIS_DATA_INV">
     *       &lt;sequence>
     *         &lt;element name="POD_Info" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="POD_FLAG" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *                   &lt;element name="POD_FILENAME" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "podInfo"
    })
    public static class Ephemeris
        extends ANEPHEMERISDATAINV
    {

        @XmlElement(name = "POD_Info")
        protected ANANCILLARYDATADSL0 .Ephemeris.PODInfo podInfo;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Obtient la valeur de la propriété podInfo.
         * 
         * @return
         *     possible object is
         *     {@link ANANCILLARYDATADSL0 .Ephemeris.PODInfo }
         *     
         */
        public ANANCILLARYDATADSL0 .Ephemeris.PODInfo getPODInfo() {
            return podInfo;
        }

        /**
         * Définit la valeur de la propriété podInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link ANANCILLARYDATADSL0 .Ephemeris.PODInfo }
         *     
         */
        public void setPODInfo(ANANCILLARYDATADSL0 .Ephemeris.PODInfo value) {
            this.podInfo = value;
        }

        /**
         * Obtient la valeur de la propriété metadataLevel.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMetadataLevel() {
            if (metadataLevel == null) {
                return "Standard";
            } else {
                return metadataLevel;
            }
        }

        /**
         * Définit la valeur de la propriété metadataLevel.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMetadataLevel(String value) {
            this.metadataLevel = value;
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
         *         &lt;element name="POD_FLAG" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
         *         &lt;element name="POD_FILENAME" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
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
            "podflag",
            "podfilename"
        })
        public static class PODInfo {

            @XmlElement(name = "POD_FLAG", required = true)
            protected Object podflag;
            @XmlElement(name = "POD_FILENAME")
            protected Object podfilename;

            /**
             * Obtient la valeur de la propriété podflag.
             * 
             * @return
             *     possible object is
             *     {@link Object }
             *     
             */
            public Object getPODFLAG() {
                return podflag;
            }

            /**
             * Définit la valeur de la propriété podflag.
             * 
             * @param value
             *     allowed object is
             *     {@link Object }
             *     
             */
            public void setPODFLAG(Object value) {
                this.podflag = value;
            }

            /**
             * Obtient la valeur de la propriété podfilename.
             * 
             * @return
             *     possible object is
             *     {@link Object }
             *     
             */
            public Object getPODFILENAME() {
                return podfilename;
            }

            /**
             * Définit la valeur de la propriété podfilename.
             * 
             * @param value
             *     allowed object is
             *     {@link Object }
             *     
             */
            public void setPODFILENAME(Object value) {
                this.podfilename = value;
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
     *         &lt;element name="Lost_Source_Packet" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="DEGRADATION_TYPE" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_SOURCE_PACKET_DEGRADATION_TYPE"/>
     *                   &lt;element name="ERROR_BEGINNING_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                   &lt;element name="ERROR_ENDING_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="processIdentifier" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
     *                       &lt;minInclusive value="0"/>
     *                       &lt;maxInclusive value="127"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="packetCategory" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
     *                       &lt;minInclusive value="0"/>
     *                       &lt;maxInclusive value="15"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="service" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
     *                       &lt;minInclusive value="0"/>
     *                       &lt;maxInclusive value="255"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="subService" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
     *                       &lt;minInclusive value="0"/>
     *                       &lt;maxInclusive value="255"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="structurIdentifier" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
     *                       &lt;minInclusive value="0"/>
     *                       &lt;maxInclusive value="255"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Degradation_Summary">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="NUMBER_OF_LOST_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
     *                   &lt;element name="NUMBER_OF_DEGRADED_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="degradationPercentage">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
     *                       &lt;minInclusive value="0"/>
     *                       &lt;maxInclusive value="100"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "lostSourcePacket",
        "degradationSummary"
    })
    public static class LostSourcePacketList {

        @XmlElement(name = "Lost_Source_Packet", required = true)
        protected List<ANANCILLARYDATADSL0 .LostSourcePacketList.LostSourcePacket> lostSourcePacket;
        @XmlElement(name = "Degradation_Summary", required = true)
        protected ANANCILLARYDATADSL0 .LostSourcePacketList.DegradationSummary degradationSummary;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Gets the value of the lostSourcePacket property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the lostSourcePacket property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLostSourcePacket().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ANANCILLARYDATADSL0 .LostSourcePacketList.LostSourcePacket }
         * 
         * 
         */
        public List<ANANCILLARYDATADSL0 .LostSourcePacketList.LostSourcePacket> getLostSourcePacket() {
            if (lostSourcePacket == null) {
                lostSourcePacket = new ArrayList<ANANCILLARYDATADSL0 .LostSourcePacketList.LostSourcePacket>();
            }
            return this.lostSourcePacket;
        }

        /**
         * Obtient la valeur de la propriété degradationSummary.
         * 
         * @return
         *     possible object is
         *     {@link ANANCILLARYDATADSL0 .LostSourcePacketList.DegradationSummary }
         *     
         */
        public ANANCILLARYDATADSL0 .LostSourcePacketList.DegradationSummary getDegradationSummary() {
            return degradationSummary;
        }

        /**
         * Définit la valeur de la propriété degradationSummary.
         * 
         * @param value
         *     allowed object is
         *     {@link ANANCILLARYDATADSL0 .LostSourcePacketList.DegradationSummary }
         *     
         */
        public void setDegradationSummary(ANANCILLARYDATADSL0 .LostSourcePacketList.DegradationSummary value) {
            this.degradationSummary = value;
        }

        /**
         * Obtient la valeur de la propriété metadataLevel.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMetadataLevel() {
            if (metadataLevel == null) {
                return "Standard";
            } else {
                return metadataLevel;
            }
        }

        /**
         * Définit la valeur de la propriété metadataLevel.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMetadataLevel(String value) {
            this.metadataLevel = value;
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
         *         &lt;element name="NUMBER_OF_LOST_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
         *         &lt;element name="NUMBER_OF_DEGRADED_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
         *       &lt;/sequence>
         *       &lt;attribute name="degradationPercentage">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
         *             &lt;minInclusive value="0"/>
         *             &lt;maxInclusive value="100"/>
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
            "numberoflostpackets",
            "numberofdegradedpackets"
        })
        public static class DegradationSummary {

            @XmlElement(name = "NUMBER_OF_LOST_PACKETS", required = true)
            @XmlSchemaType(name = "nonNegativeInteger")
            protected BigInteger numberoflostpackets;
            @XmlElement(name = "NUMBER_OF_DEGRADED_PACKETS", required = true)
            @XmlSchemaType(name = "nonNegativeInteger")
            protected BigInteger numberofdegradedpackets;
            @XmlAttribute(name = "degradationPercentage")
            protected Float degradationPercentage;

            /**
             * Obtient la valeur de la propriété numberoflostpackets.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getNUMBEROFLOSTPACKETS() {
                return numberoflostpackets;
            }

            /**
             * Définit la valeur de la propriété numberoflostpackets.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setNUMBEROFLOSTPACKETS(BigInteger value) {
                this.numberoflostpackets = value;
            }

            /**
             * Obtient la valeur de la propriété numberofdegradedpackets.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getNUMBEROFDEGRADEDPACKETS() {
                return numberofdegradedpackets;
            }

            /**
             * Définit la valeur de la propriété numberofdegradedpackets.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setNUMBEROFDEGRADEDPACKETS(BigInteger value) {
                this.numberofdegradedpackets = value;
            }

            /**
             * Obtient la valeur de la propriété degradationPercentage.
             * 
             * @return
             *     possible object is
             *     {@link Float }
             *     
             */
            public Float getDegradationPercentage() {
                return degradationPercentage;
            }

            /**
             * Définit la valeur de la propriété degradationPercentage.
             * 
             * @param value
             *     allowed object is
             *     {@link Float }
             *     
             */
            public void setDegradationPercentage(Float value) {
                this.degradationPercentage = value;
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
         *         &lt;element name="DEGRADATION_TYPE" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_SOURCE_PACKET_DEGRADATION_TYPE"/>
         *         &lt;element name="ERROR_BEGINNING_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *         &lt;element name="ERROR_ENDING_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *       &lt;/sequence>
         *       &lt;attribute name="processIdentifier" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
         *             &lt;minInclusive value="0"/>
         *             &lt;maxInclusive value="127"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="packetCategory" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
         *             &lt;minInclusive value="0"/>
         *             &lt;maxInclusive value="15"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="service" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
         *             &lt;minInclusive value="0"/>
         *             &lt;maxInclusive value="255"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="subService" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
         *             &lt;minInclusive value="0"/>
         *             &lt;maxInclusive value="255"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="structurIdentifier" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
         *             &lt;minInclusive value="0"/>
         *             &lt;maxInclusive value="255"/>
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
            "degradationtype",
            "errorbeginningdate",
            "errorendingdate"
        })
        public static class LostSourcePacket {

            @XmlElement(name = "DEGRADATION_TYPE", required = true)
            protected ASOURCEPACKETDEGRADATIONTYPE degradationtype;
            @XmlElement(name = "ERROR_BEGINNING_DATE", required = true)
            protected XMLGregorianCalendar errorbeginningdate;
            @XmlElement(name = "ERROR_ENDING_DATE", required = true)
            protected XMLGregorianCalendar errorendingdate;
            @XmlAttribute(name = "processIdentifier", required = true)
            protected int processIdentifier;
            @XmlAttribute(name = "packetCategory", required = true)
            protected int packetCategory;
            @XmlAttribute(name = "service", required = true)
            protected int service;
            @XmlAttribute(name = "subService", required = true)
            protected int subService;
            @XmlAttribute(name = "structurIdentifier", required = true)
            protected int structurIdentifier;

            /**
             * Obtient la valeur de la propriété degradationtype.
             * 
             * @return
             *     possible object is
             *     {@link ASOURCEPACKETDEGRADATIONTYPE }
             *     
             */
            public ASOURCEPACKETDEGRADATIONTYPE getDEGRADATIONTYPE() {
                return degradationtype;
            }

            /**
             * Définit la valeur de la propriété degradationtype.
             * 
             * @param value
             *     allowed object is
             *     {@link ASOURCEPACKETDEGRADATIONTYPE }
             *     
             */
            public void setDEGRADATIONTYPE(ASOURCEPACKETDEGRADATIONTYPE value) {
                this.degradationtype = value;
            }

            /**
             * Obtient la valeur de la propriété errorbeginningdate.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getERRORBEGINNINGDATE() {
                return errorbeginningdate;
            }

            /**
             * Définit la valeur de la propriété errorbeginningdate.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setERRORBEGINNINGDATE(XMLGregorianCalendar value) {
                this.errorbeginningdate = value;
            }

            /**
             * Obtient la valeur de la propriété errorendingdate.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getERRORENDINGDATE() {
                return errorendingdate;
            }

            /**
             * Définit la valeur de la propriété errorendingdate.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setERRORENDINGDATE(XMLGregorianCalendar value) {
                this.errorendingdate = value;
            }

            /**
             * Obtient la valeur de la propriété processIdentifier.
             * 
             */
            public int getProcessIdentifier() {
                return processIdentifier;
            }

            /**
             * Définit la valeur de la propriété processIdentifier.
             * 
             */
            public void setProcessIdentifier(int value) {
                this.processIdentifier = value;
            }

            /**
             * Obtient la valeur de la propriété packetCategory.
             * 
             */
            public int getPacketCategory() {
                return packetCategory;
            }

            /**
             * Définit la valeur de la propriété packetCategory.
             * 
             */
            public void setPacketCategory(int value) {
                this.packetCategory = value;
            }

            /**
             * Obtient la valeur de la propriété service.
             * 
             */
            public int getService() {
                return service;
            }

            /**
             * Définit la valeur de la propriété service.
             * 
             */
            public void setService(int value) {
                this.service = value;
            }

            /**
             * Obtient la valeur de la propriété subService.
             * 
             */
            public int getSubService() {
                return subService;
            }

            /**
             * Définit la valeur de la propriété subService.
             * 
             */
            public void setSubService(int value) {
                this.subService = value;
            }

            /**
             * Obtient la valeur de la propriété structurIdentifier.
             * 
             */
            public int getStructurIdentifier() {
                return structurIdentifier;
            }

            /**
             * Définit la valeur de la propriété structurIdentifier.
             * 
             */
            public void setStructurIdentifier(int value) {
                this.structurIdentifier = value;
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
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_THERMAL_DATA_INV">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ThermalData
        extends ATHERMALDATAINV
    {

        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Obtient la valeur de la propriété metadataLevel.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMetadataLevel() {
            if (metadataLevel == null) {
                return "Expertise";
            } else {
                return metadataLevel;
            }
        }

        /**
         * Définit la valeur de la propriété metadataLevel.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMetadataLevel(String value) {
            this.metadataLevel = value;
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
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_TIME_CORRELATION_DATA_LIST">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class TimeCorrelationDataList
        extends ATIMECORRELATIONDATALIST
    {

        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Obtient la valeur de la propriété metadataLevel.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMetadataLevel() {
            if (metadataLevel == null) {
                return "Standard";
            } else {
                return metadataLevel;
            }
        }

        /**
         * Définit la valeur de la propriété metadataLevel.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMetadataLevel(String value) {
            this.metadataLevel = value;
        }

    }

}
