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
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.image.ASOURCEPACKETDEGRADATIONTYPE;


/**
 * <p>Classe Java pour AN_ANCILLARY_DATA_ARCHIVE_EXPERTISE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_ANCILLARY_DATA_ARCHIVE_EXPERTISE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Time_Correlation_Data_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_TIME_CORRELATION_DATA_LIST" minOccurs="0"/>
 *         &lt;element name="Ephemeris" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_EPHEMERIS_DATA_INV"/>
 *         &lt;element name="Attitudes" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ATTITUDE_DATA_EXPERTISE"/>
 *         &lt;element name="Thermal_Data" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_THERMAL_DATA_INV" minOccurs="0"/>
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
 *                           &lt;attribute name="unit" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="TIME_CORRELATION"/>
 *                                 &lt;enumeration value="GPS"/>
 *                                 &lt;enumeration value="AOCS"/>
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
 *         &lt;element name="Other_Ancillary_Data" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CSM_Flags_List" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="CSM_Flags" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="FLAGS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_BOOLEAN"/>
 *                                       &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
@XmlType(name = "AN_ANCILLARY_DATA_ARCHIVE_EXPERTISE", propOrder = {
    "timeCorrelationDataList",
    "ephemeris",
    "attitudes",
    "thermalData",
    "lostSourcePacketList",
    "otherAncillaryData"
})
public class ANANCILLARYDATAARCHIVEEXPERTISE {

    @XmlElement(name = "Time_Correlation_Data_List")
    protected ATIMECORRELATIONDATALIST timeCorrelationDataList;
    @XmlElement(name = "Ephemeris", required = true)
    protected ANEPHEMERISDATAINV ephemeris;
    @XmlElement(name = "Attitudes", required = true)
    protected ANATTITUDEDATAEXPERTISE attitudes;
    @XmlElement(name = "Thermal_Data")
    protected ATHERMALDATAINV thermalData;
    @XmlElement(name = "Lost_Source_Packet_List")
    protected ANANCILLARYDATAARCHIVEEXPERTISE.LostSourcePacketList lostSourcePacketList;
    @XmlElement(name = "Other_Ancillary_Data")
    protected ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData otherAncillaryData;

    /**
     * Obtient la valeur de la propriété timeCorrelationDataList.
     * 
     * @return
     *     possible object is
     *     {@link ATIMECORRELATIONDATALIST }
     *     
     */
    public ATIMECORRELATIONDATALIST getTimeCorrelationDataList() {
        return timeCorrelationDataList;
    }

    /**
     * Définit la valeur de la propriété timeCorrelationDataList.
     * 
     * @param value
     *     allowed object is
     *     {@link ATIMECORRELATIONDATALIST }
     *     
     */
    public void setTimeCorrelationDataList(ATIMECORRELATIONDATALIST value) {
        this.timeCorrelationDataList = value;
    }

    /**
     * Obtient la valeur de la propriété ephemeris.
     * 
     * @return
     *     possible object is
     *     {@link ANEPHEMERISDATAINV }
     *     
     */
    public ANEPHEMERISDATAINV getEphemeris() {
        return ephemeris;
    }

    /**
     * Définit la valeur de la propriété ephemeris.
     * 
     * @param value
     *     allowed object is
     *     {@link ANEPHEMERISDATAINV }
     *     
     */
    public void setEphemeris(ANEPHEMERISDATAINV value) {
        this.ephemeris = value;
    }

    /**
     * Obtient la valeur de la propriété attitudes.
     * 
     * @return
     *     possible object is
     *     {@link ANATTITUDEDATAEXPERTISE }
     *     
     */
    public ANATTITUDEDATAEXPERTISE getAttitudes() {
        return attitudes;
    }

    /**
     * Définit la valeur de la propriété attitudes.
     * 
     * @param value
     *     allowed object is
     *     {@link ANATTITUDEDATAEXPERTISE }
     *     
     */
    public void setAttitudes(ANATTITUDEDATAEXPERTISE value) {
        this.attitudes = value;
    }

    /**
     * Obtient la valeur de la propriété thermalData.
     * 
     * @return
     *     possible object is
     *     {@link ATHERMALDATAINV }
     *     
     */
    public ATHERMALDATAINV getThermalData() {
        return thermalData;
    }

    /**
     * Définit la valeur de la propriété thermalData.
     * 
     * @param value
     *     allowed object is
     *     {@link ATHERMALDATAINV }
     *     
     */
    public void setThermalData(ATHERMALDATAINV value) {
        this.thermalData = value;
    }

    /**
     * Obtient la valeur de la propriété lostSourcePacketList.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATAARCHIVEEXPERTISE.LostSourcePacketList }
     *     
     */
    public ANANCILLARYDATAARCHIVEEXPERTISE.LostSourcePacketList getLostSourcePacketList() {
        return lostSourcePacketList;
    }

    /**
     * Définit la valeur de la propriété lostSourcePacketList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATAARCHIVEEXPERTISE.LostSourcePacketList }
     *     
     */
    public void setLostSourcePacketList(ANANCILLARYDATAARCHIVEEXPERTISE.LostSourcePacketList value) {
        this.lostSourcePacketList = value;
    }

    /**
     * Obtient la valeur de la propriété otherAncillaryData.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData }
     *     
     */
    public ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData getOtherAncillaryData() {
        return otherAncillaryData;
    }

    /**
     * Définit la valeur de la propriété otherAncillaryData.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData }
     *     
     */
    public void setOtherAncillaryData(ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData value) {
        this.otherAncillaryData = value;
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
     *                 &lt;attribute name="unit" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="TIME_CORRELATION"/>
     *                       &lt;enumeration value="GPS"/>
     *                       &lt;enumeration value="AOCS"/>
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
        "lostSourcePacket"
    })
    public static class LostSourcePacketList {

        @XmlElement(name = "Lost_Source_Packet", required = true)
        protected List<ANANCILLARYDATAARCHIVEEXPERTISE.LostSourcePacketList.LostSourcePacket> lostSourcePacket;

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
         * {@link ANANCILLARYDATAARCHIVEEXPERTISE.LostSourcePacketList.LostSourcePacket }
         * 
         * 
         */
        public List<ANANCILLARYDATAARCHIVEEXPERTISE.LostSourcePacketList.LostSourcePacket> getLostSourcePacket() {
            if (lostSourcePacket == null) {
                lostSourcePacket = new ArrayList<ANANCILLARYDATAARCHIVEEXPERTISE.LostSourcePacketList.LostSourcePacket>();
            }
            return this.lostSourcePacket;
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
         *       &lt;attribute name="unit" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="TIME_CORRELATION"/>
         *             &lt;enumeration value="GPS"/>
         *             &lt;enumeration value="AOCS"/>
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
            @XmlAttribute(name = "unit", required = true)
            protected String unit;

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
             * Obtient la valeur de la propriété unit.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getUnit() {
                return unit;
            }

            /**
             * Définit la valeur de la propriété unit.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setUnit(String value) {
                this.unit = value;
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
     *         &lt;element name="CSM_Flags_List" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="CSM_Flags" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="FLAGS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_BOOLEAN"/>
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
    @XmlType(name = "", propOrder = {
        "csmFlagsList"
    })
    public static class OtherAncillaryData {

        @XmlElement(name = "CSM_Flags_List")
        protected ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData.CSMFlagsList csmFlagsList;

        /**
         * Obtient la valeur de la propriété csmFlagsList.
         * 
         * @return
         *     possible object is
         *     {@link ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData.CSMFlagsList }
         *     
         */
        public ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData.CSMFlagsList getCSMFlagsList() {
            return csmFlagsList;
        }

        /**
         * Définit la valeur de la propriété csmFlagsList.
         * 
         * @param value
         *     allowed object is
         *     {@link ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData.CSMFlagsList }
         *     
         */
        public void setCSMFlagsList(ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData.CSMFlagsList value) {
            this.csmFlagsList = value;
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
         *         &lt;element name="CSM_Flags" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="FLAGS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_BOOLEAN"/>
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
            "csmFlags"
        })
        public static class CSMFlagsList {

            @XmlElement(name = "CSM_Flags", required = true)
            protected List<ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData.CSMFlagsList.CSMFlags> csmFlags;

            /**
             * Gets the value of the csmFlags property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the csmFlags property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getCSMFlags().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData.CSMFlagsList.CSMFlags }
             * 
             * 
             */
            public List<ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData.CSMFlagsList.CSMFlags> getCSMFlags() {
                if (csmFlags == null) {
                    csmFlags = new ArrayList<ANANCILLARYDATAARCHIVEEXPERTISE.OtherAncillaryData.CSMFlagsList.CSMFlags>();
                }
                return this.csmFlags;
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
             *         &lt;element name="FLAGS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_BOOLEAN"/>
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
                "flags",
                "gpstime"
            })
            public static class CSMFlags {

                @XmlList
                @XmlElement(name = "FLAGS", type = Boolean.class)
                protected List<Boolean> flags;
                @XmlElement(name = "GPS_TIME", required = true)
                protected XMLGregorianCalendar gpstime;

                /**
                 * Gets the value of the flags property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the flags property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getFLAGS().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Boolean }
                 * 
                 * 
                 */
                public List<Boolean> getFLAGS() {
                    if (flags == null) {
                        flags = new ArrayList<Boolean>();
                    }
                    return this.flags;
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
