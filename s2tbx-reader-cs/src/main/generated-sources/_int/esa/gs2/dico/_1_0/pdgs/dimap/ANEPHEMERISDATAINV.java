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
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHNSUNITATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHRADATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ALISTOF3DOUBLEWITHMATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ALISTOF3DOUBLEWITHMSATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ALISTOF3LONGWITHMMATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ALISTOF3LONGWITHMMSATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ANSM;


/**
 * <p>Classe Java pour AN_EPHEMERIS_DATA_INV complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_EPHEMERIS_DATA_INV">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GPS_Number_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Gps_Number" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="GPS_TIME_START" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                             &lt;element name="GPS_TIME_END" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="id" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="GPS-A"/>
 *                                 &lt;enumeration value="GPS-B"/>
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
 *         &lt;element name="GPS_Points_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="GPS_Point" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="POSITION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_LONG_WITH_MM_ATTR"/>
 *                             &lt;element name="POSITION_ERRORS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_LONG_WITH_MM_ATTR"/>
 *                             &lt;element name="VELOCITY_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_LONG_WITH_MMS_ATTR"/>
 *                             &lt;element name="VELOCITY_ERRORS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_LONG_WITH_MMS_ATTR"/>
 *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                             &lt;element name="NSM" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_NSM"/>
 *                             &lt;element name="QUALITY_INDEX" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                             &lt;element name="GDOP" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                             &lt;element name="PDOP" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                             &lt;element name="TDOP" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                             &lt;element name="NOF_SV">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                   &lt;minInclusive value="0"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="TIME_ERROR" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NS_UNIT_ATTR"/>
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
 *         &lt;element name="AOCS_Ephemeris_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AOCS_Ephemeris" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="VALID_FLAG" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
 *                             &lt;element name="OPSOL_QUALITY">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT">
 *                                   &lt;enumeration value="0"/>
 *                                   &lt;enumeration value="1"/>
 *                                   &lt;enumeration value="2"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="POSITION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE_WITH_M_ATTR"/>
 *                             &lt;element name="VELOCITY_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE_WITH_M_S_ATTR"/>
 *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                             &lt;element name="ORBIT_ANGLE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_RAD_ATTR"/>
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
@XmlType(name = "AN_EPHEMERIS_DATA_INV", propOrder = {
    "gpsNumberList",
    "gpsPointsList",
    "aocsEphemerisList"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.ANANCILLARYDATADSL1AL1B.Ephemeris.class,
    ANEPHEMERISDATAPROD.class,
    _int.esa.gs2.dico._1_0.pdgs.dimap.ANANCILLARYDATADSL0 .Ephemeris.class
})
public class ANEPHEMERISDATAINV {

    @XmlElement(name = "GPS_Number_List", required = true)
    protected ANEPHEMERISDATAINV.GPSNumberList gpsNumberList;
    @XmlElement(name = "GPS_Points_List", required = true)
    protected ANEPHEMERISDATAINV.GPSPointsList gpsPointsList;
    @XmlElement(name = "AOCS_Ephemeris_List", required = true)
    protected ANEPHEMERISDATAINV.AOCSEphemerisList aocsEphemerisList;

    /**
     * Obtient la valeur de la propriété gpsNumberList.
     * 
     * @return
     *     possible object is
     *     {@link ANEPHEMERISDATAINV.GPSNumberList }
     *     
     */
    public ANEPHEMERISDATAINV.GPSNumberList getGPSNumberList() {
        return gpsNumberList;
    }

    /**
     * Définit la valeur de la propriété gpsNumberList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANEPHEMERISDATAINV.GPSNumberList }
     *     
     */
    public void setGPSNumberList(ANEPHEMERISDATAINV.GPSNumberList value) {
        this.gpsNumberList = value;
    }

    /**
     * Obtient la valeur de la propriété gpsPointsList.
     * 
     * @return
     *     possible object is
     *     {@link ANEPHEMERISDATAINV.GPSPointsList }
     *     
     */
    public ANEPHEMERISDATAINV.GPSPointsList getGPSPointsList() {
        return gpsPointsList;
    }

    /**
     * Définit la valeur de la propriété gpsPointsList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANEPHEMERISDATAINV.GPSPointsList }
     *     
     */
    public void setGPSPointsList(ANEPHEMERISDATAINV.GPSPointsList value) {
        this.gpsPointsList = value;
    }

    /**
     * Obtient la valeur de la propriété aocsEphemerisList.
     * 
     * @return
     *     possible object is
     *     {@link ANEPHEMERISDATAINV.AOCSEphemerisList }
     *     
     */
    public ANEPHEMERISDATAINV.AOCSEphemerisList getAOCSEphemerisList() {
        return aocsEphemerisList;
    }

    /**
     * Définit la valeur de la propriété aocsEphemerisList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANEPHEMERISDATAINV.AOCSEphemerisList }
     *     
     */
    public void setAOCSEphemerisList(ANEPHEMERISDATAINV.AOCSEphemerisList value) {
        this.aocsEphemerisList = value;
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
     *         &lt;element name="AOCS_Ephemeris" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="VALID_FLAG" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
     *                   &lt;element name="OPSOL_QUALITY">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT">
     *                         &lt;enumeration value="0"/>
     *                         &lt;enumeration value="1"/>
     *                         &lt;enumeration value="2"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="POSITION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE_WITH_M_ATTR"/>
     *                   &lt;element name="VELOCITY_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE_WITH_M_S_ATTR"/>
     *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                   &lt;element name="ORBIT_ANGLE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_RAD_ATTR"/>
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
        "aocsEphemeris"
    })
    public static class AOCSEphemerisList {

        @XmlElement(name = "AOCS_Ephemeris", required = true)
        protected List<ANEPHEMERISDATAINV.AOCSEphemerisList.AOCSEphemeris> aocsEphemeris;

        /**
         * Gets the value of the aocsEphemeris property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the aocsEphemeris property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAOCSEphemeris().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ANEPHEMERISDATAINV.AOCSEphemerisList.AOCSEphemeris }
         * 
         * 
         */
        public List<ANEPHEMERISDATAINV.AOCSEphemerisList.AOCSEphemeris> getAOCSEphemeris() {
            if (aocsEphemeris == null) {
                aocsEphemeris = new ArrayList<ANEPHEMERISDATAINV.AOCSEphemerisList.AOCSEphemeris>();
            }
            return this.aocsEphemeris;
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
         *         &lt;element name="VALID_FLAG" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
         *         &lt;element name="OPSOL_QUALITY">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT">
         *               &lt;enumeration value="0"/>
         *               &lt;enumeration value="1"/>
         *               &lt;enumeration value="2"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="POSITION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE_WITH_M_ATTR"/>
         *         &lt;element name="VELOCITY_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE_WITH_M_S_ATTR"/>
         *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *         &lt;element name="ORBIT_ANGLE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_RAD_ATTR"/>
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
            "validflag",
            "opsolquality",
            "positionvalues",
            "velocityvalues",
            "gpstime",
            "orbitangle"
        })
        public static class AOCSEphemeris {

            @XmlElement(name = "VALID_FLAG")
            protected boolean validflag;
            @XmlElement(name = "OPSOL_QUALITY")
            protected int opsolquality;
            @XmlElement(name = "POSITION_VALUES", required = true)
            protected ALISTOF3DOUBLEWITHMATTR positionvalues;
            @XmlElement(name = "VELOCITY_VALUES", required = true)
            protected ALISTOF3DOUBLEWITHMSATTR velocityvalues;
            @XmlElement(name = "GPS_TIME", required = true)
            protected XMLGregorianCalendar gpstime;
            @XmlElement(name = "ORBIT_ANGLE", required = true)
            protected ADOUBLEWITHRADATTR orbitangle;

            /**
             * Obtient la valeur de la propriété validflag.
             * 
             */
            public boolean isVALIDFLAG() {
                return validflag;
            }

            /**
             * Définit la valeur de la propriété validflag.
             * 
             */
            public void setVALIDFLAG(boolean value) {
                this.validflag = value;
            }

            /**
             * Obtient la valeur de la propriété opsolquality.
             * 
             */
            public int getOPSOLQUALITY() {
                return opsolquality;
            }

            /**
             * Définit la valeur de la propriété opsolquality.
             * 
             */
            public void setOPSOLQUALITY(int value) {
                this.opsolquality = value;
            }

            /**
             * Obtient la valeur de la propriété positionvalues.
             * 
             * @return
             *     possible object is
             *     {@link ALISTOF3DOUBLEWITHMATTR }
             *     
             */
            public ALISTOF3DOUBLEWITHMATTR getPOSITIONVALUES() {
                return positionvalues;
            }

            /**
             * Définit la valeur de la propriété positionvalues.
             * 
             * @param value
             *     allowed object is
             *     {@link ALISTOF3DOUBLEWITHMATTR }
             *     
             */
            public void setPOSITIONVALUES(ALISTOF3DOUBLEWITHMATTR value) {
                this.positionvalues = value;
            }

            /**
             * Obtient la valeur de la propriété velocityvalues.
             * 
             * @return
             *     possible object is
             *     {@link ALISTOF3DOUBLEWITHMSATTR }
             *     
             */
            public ALISTOF3DOUBLEWITHMSATTR getVELOCITYVALUES() {
                return velocityvalues;
            }

            /**
             * Définit la valeur de la propriété velocityvalues.
             * 
             * @param value
             *     allowed object is
             *     {@link ALISTOF3DOUBLEWITHMSATTR }
             *     
             */
            public void setVELOCITYVALUES(ALISTOF3DOUBLEWITHMSATTR value) {
                this.velocityvalues = value;
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
             * Obtient la valeur de la propriété orbitangle.
             * 
             * @return
             *     possible object is
             *     {@link ADOUBLEWITHRADATTR }
             *     
             */
            public ADOUBLEWITHRADATTR getORBITANGLE() {
                return orbitangle;
            }

            /**
             * Définit la valeur de la propriété orbitangle.
             * 
             * @param value
             *     allowed object is
             *     {@link ADOUBLEWITHRADATTR }
             *     
             */
            public void setORBITANGLE(ADOUBLEWITHRADATTR value) {
                this.orbitangle = value;
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
     *         &lt;element name="Gps_Number" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="GPS_TIME_START" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                   &lt;element name="GPS_TIME_END" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="id" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="GPS-A"/>
     *                       &lt;enumeration value="GPS-B"/>
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
        "gpsNumber"
    })
    public static class GPSNumberList {

        @XmlElement(name = "Gps_Number", required = true)
        protected List<ANEPHEMERISDATAINV.GPSNumberList.GpsNumber> gpsNumber;

        /**
         * Gets the value of the gpsNumber property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the gpsNumber property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGpsNumber().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ANEPHEMERISDATAINV.GPSNumberList.GpsNumber }
         * 
         * 
         */
        public List<ANEPHEMERISDATAINV.GPSNumberList.GpsNumber> getGpsNumber() {
            if (gpsNumber == null) {
                gpsNumber = new ArrayList<ANEPHEMERISDATAINV.GPSNumberList.GpsNumber>();
            }
            return this.gpsNumber;
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
         *         &lt;element name="GPS_TIME_START" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *         &lt;element name="GPS_TIME_END" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *       &lt;/sequence>
         *       &lt;attribute name="id" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="GPS-A"/>
         *             &lt;enumeration value="GPS-B"/>
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
            "gpstimestart",
            "gpstimeend"
        })
        public static class GpsNumber {

            @XmlElement(name = "GPS_TIME_START", required = true)
            protected XMLGregorianCalendar gpstimestart;
            @XmlElement(name = "GPS_TIME_END", required = true)
            protected XMLGregorianCalendar gpstimeend;
            @XmlAttribute(name = "id", required = true)
            protected String id;

            /**
             * Obtient la valeur de la propriété gpstimestart.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getGPSTIMESTART() {
                return gpstimestart;
            }

            /**
             * Définit la valeur de la propriété gpstimestart.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setGPSTIMESTART(XMLGregorianCalendar value) {
                this.gpstimestart = value;
            }

            /**
             * Obtient la valeur de la propriété gpstimeend.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getGPSTIMEEND() {
                return gpstimeend;
            }

            /**
             * Définit la valeur de la propriété gpstimeend.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setGPSTIMEEND(XMLGregorianCalendar value) {
                this.gpstimeend = value;
            }

            /**
             * Obtient la valeur de la propriété id.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getId() {
                return id;
            }

            /**
             * Définit la valeur de la propriété id.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setId(String value) {
                this.id = value;
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
     *         &lt;element name="GPS_Point" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="POSITION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_LONG_WITH_MM_ATTR"/>
     *                   &lt;element name="POSITION_ERRORS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_LONG_WITH_MM_ATTR"/>
     *                   &lt;element name="VELOCITY_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_LONG_WITH_MMS_ATTR"/>
     *                   &lt;element name="VELOCITY_ERRORS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_LONG_WITH_MMS_ATTR"/>
     *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                   &lt;element name="NSM" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_NSM"/>
     *                   &lt;element name="QUALITY_INDEX" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                   &lt;element name="GDOP" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                   &lt;element name="PDOP" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                   &lt;element name="TDOP" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                   &lt;element name="NOF_SV">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                         &lt;minInclusive value="0"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="TIME_ERROR" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NS_UNIT_ATTR"/>
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
        "gpsPoint"
    })
    public static class GPSPointsList {

        @XmlElement(name = "GPS_Point", required = true)
        protected List<ANEPHEMERISDATAINV.GPSPointsList.GPSPoint> gpsPoint;

        /**
         * Gets the value of the gpsPoint property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the gpsPoint property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGPSPoint().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ANEPHEMERISDATAINV.GPSPointsList.GPSPoint }
         * 
         * 
         */
        public List<ANEPHEMERISDATAINV.GPSPointsList.GPSPoint> getGPSPoint() {
            if (gpsPoint == null) {
                gpsPoint = new ArrayList<ANEPHEMERISDATAINV.GPSPointsList.GPSPoint>();
            }
            return this.gpsPoint;
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
         *         &lt;element name="POSITION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_LONG_WITH_MM_ATTR"/>
         *         &lt;element name="POSITION_ERRORS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_LONG_WITH_MM_ATTR"/>
         *         &lt;element name="VELOCITY_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_LONG_WITH_MMS_ATTR"/>
         *         &lt;element name="VELOCITY_ERRORS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_LONG_WITH_MMS_ATTR"/>
         *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *         &lt;element name="NSM" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_NSM"/>
         *         &lt;element name="QUALITY_INDEX" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *         &lt;element name="GDOP" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *         &lt;element name="PDOP" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *         &lt;element name="TDOP" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *         &lt;element name="NOF_SV">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *               &lt;minInclusive value="0"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="TIME_ERROR" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NS_UNIT_ATTR"/>
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
            "positionvalues",
            "positionerrors",
            "velocityvalues",
            "velocityerrors",
            "gpstime",
            "nsm",
            "qualityindex",
            "gdop",
            "pdop",
            "tdop",
            "nofsv",
            "timeerror"
        })
        public static class GPSPoint {

            @XmlElement(name = "POSITION_VALUES", required = true)
            protected ALISTOF3LONGWITHMMATTR positionvalues;
            @XmlElement(name = "POSITION_ERRORS", required = true)
            protected ALISTOF3LONGWITHMMATTR positionerrors;
            @XmlElement(name = "VELOCITY_VALUES", required = true)
            protected ALISTOF3LONGWITHMMSATTR velocityvalues;
            @XmlElement(name = "VELOCITY_ERRORS", required = true)
            protected ALISTOF3LONGWITHMMSATTR velocityerrors;
            @XmlElement(name = "GPS_TIME", required = true)
            protected XMLGregorianCalendar gpstime;
            @XmlElement(name = "NSM", required = true)
            protected ANSM nsm;
            @XmlElement(name = "QUALITY_INDEX")
            protected double qualityindex;
            @XmlElement(name = "GDOP")
            protected double gdop;
            @XmlElement(name = "PDOP")
            protected double pdop;
            @XmlElement(name = "TDOP")
            protected double tdop;
            @XmlElement(name = "NOF_SV")
            protected int nofsv;
            @XmlElement(name = "TIME_ERROR", required = true)
            protected ADOUBLEWITHNSUNITATTR timeerror;

            /**
             * Obtient la valeur de la propriété positionvalues.
             * 
             * @return
             *     possible object is
             *     {@link ALISTOF3LONGWITHMMATTR }
             *     
             */
            public ALISTOF3LONGWITHMMATTR getPOSITIONVALUES() {
                return positionvalues;
            }

            /**
             * Définit la valeur de la propriété positionvalues.
             * 
             * @param value
             *     allowed object is
             *     {@link ALISTOF3LONGWITHMMATTR }
             *     
             */
            public void setPOSITIONVALUES(ALISTOF3LONGWITHMMATTR value) {
                this.positionvalues = value;
            }

            /**
             * Obtient la valeur de la propriété positionerrors.
             * 
             * @return
             *     possible object is
             *     {@link ALISTOF3LONGWITHMMATTR }
             *     
             */
            public ALISTOF3LONGWITHMMATTR getPOSITIONERRORS() {
                return positionerrors;
            }

            /**
             * Définit la valeur de la propriété positionerrors.
             * 
             * @param value
             *     allowed object is
             *     {@link ALISTOF3LONGWITHMMATTR }
             *     
             */
            public void setPOSITIONERRORS(ALISTOF3LONGWITHMMATTR value) {
                this.positionerrors = value;
            }

            /**
             * Obtient la valeur de la propriété velocityvalues.
             * 
             * @return
             *     possible object is
             *     {@link ALISTOF3LONGWITHMMSATTR }
             *     
             */
            public ALISTOF3LONGWITHMMSATTR getVELOCITYVALUES() {
                return velocityvalues;
            }

            /**
             * Définit la valeur de la propriété velocityvalues.
             * 
             * @param value
             *     allowed object is
             *     {@link ALISTOF3LONGWITHMMSATTR }
             *     
             */
            public void setVELOCITYVALUES(ALISTOF3LONGWITHMMSATTR value) {
                this.velocityvalues = value;
            }

            /**
             * Obtient la valeur de la propriété velocityerrors.
             * 
             * @return
             *     possible object is
             *     {@link ALISTOF3LONGWITHMMSATTR }
             *     
             */
            public ALISTOF3LONGWITHMMSATTR getVELOCITYERRORS() {
                return velocityerrors;
            }

            /**
             * Définit la valeur de la propriété velocityerrors.
             * 
             * @param value
             *     allowed object is
             *     {@link ALISTOF3LONGWITHMMSATTR }
             *     
             */
            public void setVELOCITYERRORS(ALISTOF3LONGWITHMMSATTR value) {
                this.velocityerrors = value;
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
             * Obtient la valeur de la propriété nsm.
             * 
             * @return
             *     possible object is
             *     {@link ANSM }
             *     
             */
            public ANSM getNSM() {
                return nsm;
            }

            /**
             * Définit la valeur de la propriété nsm.
             * 
             * @param value
             *     allowed object is
             *     {@link ANSM }
             *     
             */
            public void setNSM(ANSM value) {
                this.nsm = value;
            }

            /**
             * Obtient la valeur de la propriété qualityindex.
             * 
             */
            public double getQUALITYINDEX() {
                return qualityindex;
            }

            /**
             * Définit la valeur de la propriété qualityindex.
             * 
             */
            public void setQUALITYINDEX(double value) {
                this.qualityindex = value;
            }

            /**
             * Obtient la valeur de la propriété gdop.
             * 
             */
            public double getGDOP() {
                return gdop;
            }

            /**
             * Définit la valeur de la propriété gdop.
             * 
             */
            public void setGDOP(double value) {
                this.gdop = value;
            }

            /**
             * Obtient la valeur de la propriété pdop.
             * 
             */
            public double getPDOP() {
                return pdop;
            }

            /**
             * Définit la valeur de la propriété pdop.
             * 
             */
            public void setPDOP(double value) {
                this.pdop = value;
            }

            /**
             * Obtient la valeur de la propriété tdop.
             * 
             */
            public double getTDOP() {
                return tdop;
            }

            /**
             * Définit la valeur de la propriété tdop.
             * 
             */
            public void setTDOP(double value) {
                this.tdop = value;
            }

            /**
             * Obtient la valeur de la propriété nofsv.
             * 
             */
            public int getNOFSV() {
                return nofsv;
            }

            /**
             * Définit la valeur de la propriété nofsv.
             * 
             */
            public void setNOFSV(int value) {
                this.nofsv = value;
            }

            /**
             * Obtient la valeur de la propriété timeerror.
             * 
             * @return
             *     possible object is
             *     {@link ADOUBLEWITHNSUNITATTR }
             *     
             */
            public ADOUBLEWITHNSUNITATTR getTIMEERROR() {
                return timeerror;
            }

            /**
             * Définit la valeur de la propriété timeerror.
             * 
             * @param value
             *     allowed object is
             *     {@link ADOUBLEWITHNSUNITATTR }
             *     
             */
            public void setTIMEERROR(ADOUBLEWITHNSUNITATTR value) {
                this.timeerror = value;
            }

        }

    }

}
