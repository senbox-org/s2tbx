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
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.image.ASOURCEPACKETDEGRADATIONTYPE;


/**
 * <p>Classe Java pour AN_ANCILLARY_DATA_DSL1AL1B complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_ANCILLARY_DATA_DSL1AL1B">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Time_Correlation_Data_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_TIME_CORRELATION_DATA_LIST" minOccurs="0"/>
 *         &lt;element name="Ephemeris">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_EPHEMERIS_DATA_INV">
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Attitudes" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ATTITUDE_DATA_INV"/>
 *         &lt;element name="Thermal_Data" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_THERMAL_DATA_INV"/>
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
 *                           &lt;attribute name="structIdentifier" use="required">
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
 *                           &lt;attribute name="service" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
 *                                 &lt;minInclusive value="0"/>
 *                                 &lt;maxInclusive value="255"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="packetCat" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
 *                                 &lt;minInclusive value="0"/>
 *                                 &lt;maxInclusive value="15"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="proIdentifier" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
 *                                 &lt;minInclusive value="0"/>
 *                                 &lt;maxInclusive value="127"/>
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
 *         &lt;element name="ANC_DATA_REF" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_ANCILLARY_DATA_DSL1AL1B", propOrder = {
    "timeCorrelationDataList",
    "ephemeris",
    "attitudes",
    "thermalData",
    "lostSourcePacketList",
    "ancdataref"
})
public class ANANCILLARYDATADSL1AL1B {

    @XmlElement(name = "Time_Correlation_Data_List")
    protected ATIMECORRELATIONDATALIST timeCorrelationDataList;
    @XmlElement(name = "Ephemeris", required = true)
    protected ANANCILLARYDATADSL1AL1B.Ephemeris ephemeris;
    @XmlElement(name = "Attitudes", required = true)
    protected ANATTITUDEDATAINV attitudes;
    @XmlElement(name = "Thermal_Data", required = true)
    protected ATHERMALDATAINV thermalData;
    @XmlElement(name = "Lost_Source_Packet_List")
    protected ANANCILLARYDATADSL1AL1B.LostSourcePacketList lostSourcePacketList;
    @XmlElement(name = "ANC_DATA_REF", required = true)
    protected Object ancdataref;

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
     *     {@link ANANCILLARYDATADSL1AL1B.Ephemeris }
     *     
     */
    public ANANCILLARYDATADSL1AL1B.Ephemeris getEphemeris() {
        return ephemeris;
    }

    /**
     * Définit la valeur de la propriété ephemeris.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATADSL1AL1B.Ephemeris }
     *     
     */
    public void setEphemeris(ANANCILLARYDATADSL1AL1B.Ephemeris value) {
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
     *     {@link ANANCILLARYDATADSL1AL1B.LostSourcePacketList }
     *     
     */
    public ANANCILLARYDATADSL1AL1B.LostSourcePacketList getLostSourcePacketList() {
        return lostSourcePacketList;
    }

    /**
     * Définit la valeur de la propriété lostSourcePacketList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATADSL1AL1B.LostSourcePacketList }
     *     
     */
    public void setLostSourcePacketList(ANANCILLARYDATADSL1AL1B.LostSourcePacketList value) {
        this.lostSourcePacketList = value;
    }

    /**
     * Obtient la valeur de la propriété ancdataref.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getANCDATAREF() {
        return ancdataref;
    }

    /**
     * Définit la valeur de la propriété ancdataref.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setANCDATAREF(Object value) {
        this.ancdataref = value;
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
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Ephemeris
        extends ANEPHEMERISDATAINV
    {


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
     *                 &lt;attribute name="structIdentifier" use="required">
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
     *                 &lt;attribute name="service" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
     *                       &lt;minInclusive value="0"/>
     *                       &lt;maxInclusive value="255"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="packetCat" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
     *                       &lt;minInclusive value="0"/>
     *                       &lt;maxInclusive value="15"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="proIdentifier" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
     *                       &lt;minInclusive value="0"/>
     *                       &lt;maxInclusive value="127"/>
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
        protected List<ANANCILLARYDATADSL1AL1B.LostSourcePacketList.LostSourcePacket> lostSourcePacket;

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
         * {@link ANANCILLARYDATADSL1AL1B.LostSourcePacketList.LostSourcePacket }
         * 
         * 
         */
        public List<ANANCILLARYDATADSL1AL1B.LostSourcePacketList.LostSourcePacket> getLostSourcePacket() {
            if (lostSourcePacket == null) {
                lostSourcePacket = new ArrayList<ANANCILLARYDATADSL1AL1B.LostSourcePacketList.LostSourcePacket>();
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
         *       &lt;attribute name="structIdentifier" use="required">
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
         *       &lt;attribute name="service" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
         *             &lt;minInclusive value="0"/>
         *             &lt;maxInclusive value="255"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="packetCat" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
         *             &lt;minInclusive value="0"/>
         *             &lt;maxInclusive value="15"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="proIdentifier" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedShort">
         *             &lt;minInclusive value="0"/>
         *             &lt;maxInclusive value="127"/>
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
            @XmlAttribute(name = "structIdentifier", required = true)
            protected int structIdentifier;
            @XmlAttribute(name = "subService", required = true)
            protected int subService;
            @XmlAttribute(name = "service", required = true)
            protected int service;
            @XmlAttribute(name = "packetCat", required = true)
            protected int packetCat;
            @XmlAttribute(name = "proIdentifier", required = true)
            protected int proIdentifier;

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
             * Obtient la valeur de la propriété structIdentifier.
             * 
             */
            public int getStructIdentifier() {
                return structIdentifier;
            }

            /**
             * Définit la valeur de la propriété structIdentifier.
             * 
             */
            public void setStructIdentifier(int value) {
                this.structIdentifier = value;
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
             * Obtient la valeur de la propriété packetCat.
             * 
             */
            public int getPacketCat() {
                return packetCat;
            }

            /**
             * Définit la valeur de la propriété packetCat.
             * 
             */
            public void setPacketCat(int value) {
                this.packetCat = value;
            }

            /**
             * Obtient la valeur de la propriété proIdentifier.
             * 
             */
            public int getProIdentifier() {
                return proIdentifier;
            }

            /**
             * Définit la valeur de la propriété proIdentifier.
             * 
             */
            public void setProIdentifier(int value) {
                this.proIdentifier = value;
            }

        }

    }

}
