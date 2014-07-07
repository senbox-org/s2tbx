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
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHMSUNITATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHSUNITATTR;


/**
 * <p>Classe Java pour A_TIME_STAMP complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_TIME_STAMP">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LINE_PERIOD" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MS_UNIT_ATTR"/>
 *         &lt;element name="Band_Time_Stamp" maxOccurs="13">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Detector" maxOccurs="12">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="REFERENCE_LINE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GPS_SYNC" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
 *         &lt;element name="THEORETICAL_LINE_PERIOD" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MS_UNIT_ATTR" minOccurs="0"/>
 *         &lt;element name="Quality_Indicators" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Global">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="RMOY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_S_UNIT_ATTR"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="GSP_List">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="GSP" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="RMOY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_S_UNIT_ATTR"/>
 *                                     &lt;/sequence>
 *                                     &lt;attribute name="gspId" use="required">
 *                                       &lt;simpleType>
 *                                         &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/image/}A_GSP_ID">
 *                                         &lt;/restriction>
 *                                       &lt;/simpleType>
 *                                     &lt;/attribute>
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
 *       &lt;attribute name="usedModel" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="INITIAL"/>
 *             &lt;enumeration value="EXOGENEOUS"/>
 *             &lt;enumeration value="CORRECTED"/>
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
@XmlType(name = "A_TIME_STAMP", propOrder = {
    "lineperiod",
    "bandTimeStamp",
    "gpssync",
    "theoreticallineperiod",
    "qualityIndicators"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.ASENSORCONFIGURATION.TimeStamp.class
})
public class ATIMESTAMP {

    @XmlElement(name = "LINE_PERIOD", required = true)
    protected ADOUBLEWITHMSUNITATTR lineperiod;
    @XmlElement(name = "Band_Time_Stamp", required = true)
    protected List<ATIMESTAMP.BandTimeStamp> bandTimeStamp;
    @XmlElement(name = "GPS_SYNC")
    protected boolean gpssync;
    @XmlElement(name = "THEORETICAL_LINE_PERIOD")
    protected ADOUBLEWITHMSUNITATTR theoreticallineperiod;
    @XmlElement(name = "Quality_Indicators")
    protected ATIMESTAMP.QualityIndicators qualityIndicators;
    @XmlAttribute(name = "usedModel", required = true)
    protected String usedModel;

    /**
     * Obtient la valeur de la propriété lineperiod.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHMSUNITATTR }
     *     
     */
    public ADOUBLEWITHMSUNITATTR getLINEPERIOD() {
        return lineperiod;
    }

    /**
     * Définit la valeur de la propriété lineperiod.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHMSUNITATTR }
     *     
     */
    public void setLINEPERIOD(ADOUBLEWITHMSUNITATTR value) {
        this.lineperiod = value;
    }

    /**
     * Gets the value of the bandTimeStamp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bandTimeStamp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBandTimeStamp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ATIMESTAMP.BandTimeStamp }
     * 
     * 
     */
    public List<ATIMESTAMP.BandTimeStamp> getBandTimeStamp() {
        if (bandTimeStamp == null) {
            bandTimeStamp = new ArrayList<ATIMESTAMP.BandTimeStamp>();
        }
        return this.bandTimeStamp;
    }

    /**
     * Obtient la valeur de la propriété gpssync.
     * 
     */
    public boolean isGPSSYNC() {
        return gpssync;
    }

    /**
     * Définit la valeur de la propriété gpssync.
     * 
     */
    public void setGPSSYNC(boolean value) {
        this.gpssync = value;
    }

    /**
     * Obtient la valeur de la propriété theoreticallineperiod.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHMSUNITATTR }
     *     
     */
    public ADOUBLEWITHMSUNITATTR getTHEORETICALLINEPERIOD() {
        return theoreticallineperiod;
    }

    /**
     * Définit la valeur de la propriété theoreticallineperiod.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHMSUNITATTR }
     *     
     */
    public void setTHEORETICALLINEPERIOD(ADOUBLEWITHMSUNITATTR value) {
        this.theoreticallineperiod = value;
    }

    /**
     * Obtient la valeur de la propriété qualityIndicators.
     * 
     * @return
     *     possible object is
     *     {@link ATIMESTAMP.QualityIndicators }
     *     
     */
    public ATIMESTAMP.QualityIndicators getQualityIndicators() {
        return qualityIndicators;
    }

    /**
     * Définit la valeur de la propriété qualityIndicators.
     * 
     * @param value
     *     allowed object is
     *     {@link ATIMESTAMP.QualityIndicators }
     *     
     */
    public void setQualityIndicators(ATIMESTAMP.QualityIndicators value) {
        this.qualityIndicators = value;
    }

    /**
     * Obtient la valeur de la propriété usedModel.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsedModel() {
        return usedModel;
    }

    /**
     * Définit la valeur de la propriété usedModel.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsedModel(String value) {
        this.usedModel = value;
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
     *         &lt;element name="Detector" maxOccurs="12">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="REFERENCE_LINE" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "detector"
    })
    public static class BandTimeStamp {

        @XmlElement(name = "Detector", required = true)
        protected List<ATIMESTAMP.BandTimeStamp.Detector> detector;
        @XmlAttribute(name = "bandId", required = true)
        protected String bandId;

        /**
         * Gets the value of the detector property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the detector property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDetector().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ATIMESTAMP.BandTimeStamp.Detector }
         * 
         * 
         */
        public List<ATIMESTAMP.BandTimeStamp.Detector> getDetector() {
            if (detector == null) {
                detector = new ArrayList<ATIMESTAMP.BandTimeStamp.Detector>();
            }
            return this.detector;
        }

        /**
         * Obtient la valeur de la propriété bandId.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBandId() {
            return bandId;
        }

        /**
         * Définit la valeur de la propriété bandId.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBandId(String value) {
            this.bandId = value;
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
         *         &lt;element name="REFERENCE_LINE" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *       &lt;/sequence>
         *       &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "referenceline",
            "gpstime"
        })
        public static class Detector {

            @XmlElement(name = "REFERENCE_LINE")
            protected int referenceline;
            @XmlElement(name = "GPS_TIME", required = true)
            protected XMLGregorianCalendar gpstime;
            @XmlAttribute(name = "detectorId", required = true)
            protected String detectorId;

            /**
             * Obtient la valeur de la propriété referenceline.
             * 
             */
            public int getREFERENCELINE() {
                return referenceline;
            }

            /**
             * Définit la valeur de la propriété referenceline.
             * 
             */
            public void setREFERENCELINE(int value) {
                this.referenceline = value;
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
             * Obtient la valeur de la propriété detectorId.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDetectorId() {
                return detectorId;
            }

            /**
             * Définit la valeur de la propriété detectorId.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDetectorId(String value) {
                this.detectorId = value;
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
     *         &lt;element name="Global">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="RMOY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_S_UNIT_ATTR"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="GSP_List">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="GSP" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="RMOY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_S_UNIT_ATTR"/>
     *                           &lt;/sequence>
     *                           &lt;attribute name="gspId" use="required">
     *                             &lt;simpleType>
     *                               &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/image/}A_GSP_ID">
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
    @XmlType(name = "", propOrder = {
        "global",
        "gspList"
    })
    public static class QualityIndicators {

        @XmlElement(name = "Global", required = true)
        protected ATIMESTAMP.QualityIndicators.Global global;
        @XmlElement(name = "GSP_List", required = true)
        protected ATIMESTAMP.QualityIndicators.GSPList gspList;

        /**
         * Obtient la valeur de la propriété global.
         * 
         * @return
         *     possible object is
         *     {@link ATIMESTAMP.QualityIndicators.Global }
         *     
         */
        public ATIMESTAMP.QualityIndicators.Global getGlobal() {
            return global;
        }

        /**
         * Définit la valeur de la propriété global.
         * 
         * @param value
         *     allowed object is
         *     {@link ATIMESTAMP.QualityIndicators.Global }
         *     
         */
        public void setGlobal(ATIMESTAMP.QualityIndicators.Global value) {
            this.global = value;
        }

        /**
         * Obtient la valeur de la propriété gspList.
         * 
         * @return
         *     possible object is
         *     {@link ATIMESTAMP.QualityIndicators.GSPList }
         *     
         */
        public ATIMESTAMP.QualityIndicators.GSPList getGSPList() {
            return gspList;
        }

        /**
         * Définit la valeur de la propriété gspList.
         * 
         * @param value
         *     allowed object is
         *     {@link ATIMESTAMP.QualityIndicators.GSPList }
         *     
         */
        public void setGSPList(ATIMESTAMP.QualityIndicators.GSPList value) {
            this.gspList = value;
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
         *         &lt;element name="GSP" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="RMOY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_S_UNIT_ATTR"/>
         *                 &lt;/sequence>
         *                 &lt;attribute name="gspId" use="required">
         *                   &lt;simpleType>
         *                     &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/image/}A_GSP_ID">
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
            "gsp"
        })
        public static class GSPList {

            @XmlElement(name = "GSP", required = true)
            protected List<ATIMESTAMP.QualityIndicators.GSPList.GSP> gsp;

            /**
             * Gets the value of the gsp property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the gsp property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getGSP().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ATIMESTAMP.QualityIndicators.GSPList.GSP }
             * 
             * 
             */
            public List<ATIMESTAMP.QualityIndicators.GSPList.GSP> getGSP() {
                if (gsp == null) {
                    gsp = new ArrayList<ATIMESTAMP.QualityIndicators.GSPList.GSP>();
                }
                return this.gsp;
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
             *         &lt;element name="RMOY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_S_UNIT_ATTR"/>
             *       &lt;/sequence>
             *       &lt;attribute name="gspId" use="required">
             *         &lt;simpleType>
             *           &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/image/}A_GSP_ID">
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
                "rmoy"
            })
            public static class GSP {

                @XmlElement(name = "RMOY", required = true)
                protected ADOUBLEWITHSUNITATTR rmoy;
                @XmlAttribute(name = "gspId", required = true)
                protected String gspId;

                /**
                 * Obtient la valeur de la propriété rmoy.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ADOUBLEWITHSUNITATTR }
                 *     
                 */
                public ADOUBLEWITHSUNITATTR getRMOY() {
                    return rmoy;
                }

                /**
                 * Définit la valeur de la propriété rmoy.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ADOUBLEWITHSUNITATTR }
                 *     
                 */
                public void setRMOY(ADOUBLEWITHSUNITATTR value) {
                    this.rmoy = value;
                }

                /**
                 * Obtient la valeur de la propriété gspId.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getGspId() {
                    return gspId;
                }

                /**
                 * Définit la valeur de la propriété gspId.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setGspId(String value) {
                    this.gspId = value;
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
         *         &lt;element name="RMOY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_S_UNIT_ATTR"/>
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
            "rmoy"
        })
        public static class Global {

            @XmlElement(name = "RMOY", required = true)
            protected ADOUBLEWITHSUNITATTR rmoy;

            /**
             * Obtient la valeur de la propriété rmoy.
             * 
             * @return
             *     possible object is
             *     {@link ADOUBLEWITHSUNITATTR }
             *     
             */
            public ADOUBLEWITHSUNITATTR getRMOY() {
                return rmoy;
            }

            /**
             * Définit la valeur de la propriété rmoy.
             * 
             * @param value
             *     allowed object is
             *     {@link ADOUBLEWITHSUNITATTR }
             *     
             */
            public void setRMOY(ADOUBLEWITHSUNITATTR value) {
                this.rmoy = value;
            }

        }

    }

}
