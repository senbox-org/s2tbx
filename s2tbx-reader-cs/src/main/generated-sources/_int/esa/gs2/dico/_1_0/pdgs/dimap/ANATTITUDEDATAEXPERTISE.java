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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.platform.AQUATERNIONSTATUS;


/**
 * <p>Classe Java pour AN_ATTITUDE_DATA_EXPERTISE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_ATTITUDE_DATA_EXPERTISE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Corrected_Attitudes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Values" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="QUATERNION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION"/>
 *                             &lt;element name="QUATERNION_STATUS" type="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION_STATUS"/>
 *                             &lt;element name="QUATERNION_VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
 *                             &lt;element name="VELOCITY_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
 *                             &lt;element name="VELOCITY_ERRORS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
 *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                             &lt;element name="INUSE_FLAGS" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_11_BOOLEAN">
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="AOCS_MODE" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                   &lt;enumeration value="0"/>
 *                                   &lt;enumeration value="1"/>
 *                                   &lt;enumeration value="2"/>
 *                                   &lt;enumeration value="3"/>
 *                                   &lt;enumeration value="4"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="AOCS_SUBMODE" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                   &lt;enumeration value="0"/>
 *                                   &lt;enumeration value="1"/>
 *                                   &lt;enumeration value="2"/>
 *                                   &lt;enumeration value="3"/>
 *                                   &lt;enumeration value="4"/>
 *                                   &lt;enumeration value="5"/>
 *                                   &lt;enumeration value="6"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="INNOVATION_STR1" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE" minOccurs="0"/>
 *                             &lt;element name="INNOVATION_STR2" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE" minOccurs="0"/>
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
 *         &lt;element name="Raw_Attitudes" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_RAW_ATTITUDE" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_ATTITUDE_DATA_EXPERTISE", propOrder = {
    "correctedAttitudes",
    "rawAttitudes"
})
public class ANATTITUDEDATAEXPERTISE {

    @XmlElement(name = "Corrected_Attitudes", required = true)
    protected ANATTITUDEDATAEXPERTISE.CorrectedAttitudes correctedAttitudes;
    @XmlElement(name = "Raw_Attitudes")
    protected ARAWATTITUDE rawAttitudes;

    /**
     * Obtient la valeur de la propriété correctedAttitudes.
     * 
     * @return
     *     possible object is
     *     {@link ANATTITUDEDATAEXPERTISE.CorrectedAttitudes }
     *     
     */
    public ANATTITUDEDATAEXPERTISE.CorrectedAttitudes getCorrectedAttitudes() {
        return correctedAttitudes;
    }

    /**
     * Définit la valeur de la propriété correctedAttitudes.
     * 
     * @param value
     *     allowed object is
     *     {@link ANATTITUDEDATAEXPERTISE.CorrectedAttitudes }
     *     
     */
    public void setCorrectedAttitudes(ANATTITUDEDATAEXPERTISE.CorrectedAttitudes value) {
        this.correctedAttitudes = value;
    }

    /**
     * Obtient la valeur de la propriété rawAttitudes.
     * 
     * @return
     *     possible object is
     *     {@link ARAWATTITUDE }
     *     
     */
    public ARAWATTITUDE getRawAttitudes() {
        return rawAttitudes;
    }

    /**
     * Définit la valeur de la propriété rawAttitudes.
     * 
     * @param value
     *     allowed object is
     *     {@link ARAWATTITUDE }
     *     
     */
    public void setRawAttitudes(ARAWATTITUDE value) {
        this.rawAttitudes = value;
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
     *         &lt;element name="Values" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="QUATERNION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION"/>
     *                   &lt;element name="QUATERNION_STATUS" type="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION_STATUS"/>
     *                   &lt;element name="QUATERNION_VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
     *                   &lt;element name="VELOCITY_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
     *                   &lt;element name="VELOCITY_ERRORS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
     *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                   &lt;element name="INUSE_FLAGS" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_11_BOOLEAN">
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="AOCS_MODE" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                         &lt;enumeration value="0"/>
     *                         &lt;enumeration value="1"/>
     *                         &lt;enumeration value="2"/>
     *                         &lt;enumeration value="3"/>
     *                         &lt;enumeration value="4"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="AOCS_SUBMODE" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                         &lt;enumeration value="0"/>
     *                         &lt;enumeration value="1"/>
     *                         &lt;enumeration value="2"/>
     *                         &lt;enumeration value="3"/>
     *                         &lt;enumeration value="4"/>
     *                         &lt;enumeration value="5"/>
     *                         &lt;enumeration value="6"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="INNOVATION_STR1" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE" minOccurs="0"/>
     *                   &lt;element name="INNOVATION_STR2" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE" minOccurs="0"/>
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
        "values"
    })
    public static class CorrectedAttitudes {

        @XmlElement(name = "Values", required = true)
        protected List<ANATTITUDEDATAEXPERTISE.CorrectedAttitudes.Values> values;

        /**
         * Gets the value of the values property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the values property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getValues().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ANATTITUDEDATAEXPERTISE.CorrectedAttitudes.Values }
         * 
         * 
         */
        public List<ANATTITUDEDATAEXPERTISE.CorrectedAttitudes.Values> getValues() {
            if (values == null) {
                values = new ArrayList<ANATTITUDEDATAEXPERTISE.CorrectedAttitudes.Values>();
            }
            return this.values;
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
         *         &lt;element name="QUATERNION_STATUS" type="{http://gs2.esa.int/DICO/1.0/SY/platform/}A_QUATERNION_STATUS"/>
         *         &lt;element name="QUATERNION_VALIDITY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
         *         &lt;element name="VELOCITY_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
         *         &lt;element name="VELOCITY_ERRORS" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
         *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *         &lt;element name="INUSE_FLAGS" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_11_BOOLEAN">
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="AOCS_MODE" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *               &lt;enumeration value="0"/>
         *               &lt;enumeration value="1"/>
         *               &lt;enumeration value="2"/>
         *               &lt;enumeration value="3"/>
         *               &lt;enumeration value="4"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="AOCS_SUBMODE" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *               &lt;enumeration value="0"/>
         *               &lt;enumeration value="1"/>
         *               &lt;enumeration value="2"/>
         *               &lt;enumeration value="3"/>
         *               &lt;enumeration value="4"/>
         *               &lt;enumeration value="5"/>
         *               &lt;enumeration value="6"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="INNOVATION_STR1" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE" minOccurs="0"/>
         *         &lt;element name="INNOVATION_STR2" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE" minOccurs="0"/>
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
            "quaternionvalidity",
            "velocityvalues",
            "velocityerrors",
            "gpstime",
            "inuseflags",
            "aocsmode",
            "aocssubmode",
            "innovationstr1",
            "innovationstr2"
        })
        public static class Values {

            @XmlList
            @XmlElement(name = "QUATERNION_VALUES", type = Double.class)
            protected List<Double> quaternionvalues;
            @XmlElement(name = "QUATERNION_STATUS", required = true)
            protected AQUATERNIONSTATUS quaternionstatus;
            @XmlElement(name = "QUATERNION_VALIDITY")
            protected boolean quaternionvalidity;
            @XmlList
            @XmlElement(name = "VELOCITY_VALUES", type = Double.class)
            protected List<Double> velocityvalues;
            @XmlList
            @XmlElement(name = "VELOCITY_ERRORS", type = Double.class)
            protected List<Double> velocityerrors;
            @XmlElement(name = "GPS_TIME", required = true)
            protected XMLGregorianCalendar gpstime;
            @XmlList
            @XmlElement(name = "INUSE_FLAGS", type = Boolean.class)
            protected List<Boolean> inuseflags;
            @XmlElement(name = "AOCS_MODE")
            protected Integer aocsmode;
            @XmlElement(name = "AOCS_SUBMODE")
            protected Integer aocssubmode;
            @XmlList
            @XmlElement(name = "INNOVATION_STR1", type = Double.class)
            protected List<Double> innovationstr1;
            @XmlList
            @XmlElement(name = "INNOVATION_STR2", type = Double.class)
            protected List<Double> innovationstr2;

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
             * Obtient la valeur de la propriété quaternionvalidity.
             * 
             */
            public boolean isQUATERNIONVALIDITY() {
                return quaternionvalidity;
            }

            /**
             * Définit la valeur de la propriété quaternionvalidity.
             * 
             */
            public void setQUATERNIONVALIDITY(boolean value) {
                this.quaternionvalidity = value;
            }

            /**
             * Gets the value of the velocityvalues property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the velocityvalues property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getVELOCITYVALUES().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Double }
             * 
             * 
             */
            public List<Double> getVELOCITYVALUES() {
                if (velocityvalues == null) {
                    velocityvalues = new ArrayList<Double>();
                }
                return this.velocityvalues;
            }

            /**
             * Gets the value of the velocityerrors property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the velocityerrors property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getVELOCITYERRORS().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Double }
             * 
             * 
             */
            public List<Double> getVELOCITYERRORS() {
                if (velocityerrors == null) {
                    velocityerrors = new ArrayList<Double>();
                }
                return this.velocityerrors;
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
             * Gets the value of the inuseflags property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the inuseflags property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getINUSEFLAGS().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Boolean }
             * 
             * 
             */
            public List<Boolean> getINUSEFLAGS() {
                if (inuseflags == null) {
                    inuseflags = new ArrayList<Boolean>();
                }
                return this.inuseflags;
            }

            /**
             * Obtient la valeur de la propriété aocsmode.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getAOCSMODE() {
                return aocsmode;
            }

            /**
             * Définit la valeur de la propriété aocsmode.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setAOCSMODE(Integer value) {
                this.aocsmode = value;
            }

            /**
             * Obtient la valeur de la propriété aocssubmode.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getAOCSSUBMODE() {
                return aocssubmode;
            }

            /**
             * Définit la valeur de la propriété aocssubmode.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setAOCSSUBMODE(Integer value) {
                this.aocssubmode = value;
            }

            /**
             * Gets the value of the innovationstr1 property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the innovationstr1 property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getINNOVATIONSTR1().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Double }
             * 
             * 
             */
            public List<Double> getINNOVATIONSTR1() {
                if (innovationstr1 == null) {
                    innovationstr1 = new ArrayList<Double>();
                }
                return this.innovationstr1;
            }

            /**
             * Gets the value of the innovationstr2 property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the innovationstr2 property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getINNOVATIONSTR2().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Double }
             * 
             * 
             */
            public List<Double> getINNOVATIONSTR2() {
                if (innovationstr2 == null) {
                    innovationstr2 = new ArrayList<Double>();
                }
                return this.innovationstr2;
            }

        }

    }

}
