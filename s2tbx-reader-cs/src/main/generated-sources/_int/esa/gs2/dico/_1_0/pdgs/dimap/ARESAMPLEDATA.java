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
import javax.xml.bind.annotation.XmlValue;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHUNITATTR;


/**
 * 
 * 
 * <p>Classe Java pour A_RESAMPLE_DATA complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_RESAMPLE_DATA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Reflectance_Conversion">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="U" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                   &lt;element name="Solar_Irradiance_List">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="SOLAR_IRRADIANCE" maxOccurs="13">
 *                               &lt;complexType>
 *                                 &lt;simpleContent>
 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
 *                                     &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
 *                                     &lt;attribute name="unit" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="W/m²/µm" />
 *                                   &lt;/extension>
 *                                 &lt;/simpleContent>
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
 *         &lt;element name="QUANTIFICATION_VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_UNIT_ATTR"/>
 *         &lt;element name="Noise_Model_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Noise_Model" maxOccurs="13">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ALPHA" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                             &lt;element name="BETA" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
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
@XmlType(name = "A_RESAMPLE_DATA", propOrder = {
    "reflectanceConversion",
    "quantificationvalue",
    "noiseModelList"
})
public class ARESAMPLEDATA {

    @XmlElement(name = "Reflectance_Conversion", required = true)
    protected ARESAMPLEDATA.ReflectanceConversion reflectanceConversion;
    @XmlElement(name = "QUANTIFICATION_VALUE", required = true)
    protected ADOUBLEWITHUNITATTR quantificationvalue;
    @XmlElement(name = "Noise_Model_List", required = true)
    protected ARESAMPLEDATA.NoiseModelList noiseModelList;

    /**
     * Obtient la valeur de la propriété reflectanceConversion.
     * 
     * @return
     *     possible object is
     *     {@link ARESAMPLEDATA.ReflectanceConversion }
     *     
     */
    public ARESAMPLEDATA.ReflectanceConversion getReflectanceConversion() {
        return reflectanceConversion;
    }

    /**
     * Définit la valeur de la propriété reflectanceConversion.
     * 
     * @param value
     *     allowed object is
     *     {@link ARESAMPLEDATA.ReflectanceConversion }
     *     
     */
    public void setReflectanceConversion(ARESAMPLEDATA.ReflectanceConversion value) {
        this.reflectanceConversion = value;
    }

    /**
     * Obtient la valeur de la propriété quantificationvalue.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public ADOUBLEWITHUNITATTR getQUANTIFICATIONVALUE() {
        return quantificationvalue;
    }

    /**
     * Définit la valeur de la propriété quantificationvalue.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public void setQUANTIFICATIONVALUE(ADOUBLEWITHUNITATTR value) {
        this.quantificationvalue = value;
    }

    /**
     * Obtient la valeur de la propriété noiseModelList.
     * 
     * @return
     *     possible object is
     *     {@link ARESAMPLEDATA.NoiseModelList }
     *     
     */
    public ARESAMPLEDATA.NoiseModelList getNoiseModelList() {
        return noiseModelList;
    }

    /**
     * Définit la valeur de la propriété noiseModelList.
     * 
     * @param value
     *     allowed object is
     *     {@link ARESAMPLEDATA.NoiseModelList }
     *     
     */
    public void setNoiseModelList(ARESAMPLEDATA.NoiseModelList value) {
        this.noiseModelList = value;
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
     *         &lt;element name="Noise_Model" maxOccurs="13">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ALPHA" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                   &lt;element name="BETA" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
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
        "noiseModel"
    })
    public static class NoiseModelList {

        @XmlElement(name = "Noise_Model", required = true)
        protected List<ARESAMPLEDATA.NoiseModelList.NoiseModel> noiseModel;

        /**
         * Gets the value of the noiseModel property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the noiseModel property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getNoiseModel().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ARESAMPLEDATA.NoiseModelList.NoiseModel }
         * 
         * 
         */
        public List<ARESAMPLEDATA.NoiseModelList.NoiseModel> getNoiseModel() {
            if (noiseModel == null) {
                noiseModel = new ArrayList<ARESAMPLEDATA.NoiseModelList.NoiseModel>();
            }
            return this.noiseModel;
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
         *         &lt;element name="ALPHA" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *         &lt;element name="BETA" type="{http://www.w3.org/2001/XMLSchema}double"/>
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
            "alpha",
            "beta"
        })
        public static class NoiseModel {

            @XmlElement(name = "ALPHA")
            protected double alpha;
            @XmlElement(name = "BETA")
            protected double beta;
            @XmlAttribute(name = "bandId", required = true)
            protected String bandId;

            /**
             * Obtient la valeur de la propriété alpha.
             * 
             */
            public double getALPHA() {
                return alpha;
            }

            /**
             * Définit la valeur de la propriété alpha.
             * 
             */
            public void setALPHA(double value) {
                this.alpha = value;
            }

            /**
             * Obtient la valeur de la propriété beta.
             * 
             */
            public double getBETA() {
                return beta;
            }

            /**
             * Définit la valeur de la propriété beta.
             * 
             */
            public void setBETA(double value) {
                this.beta = value;
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
     *         &lt;element name="U" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *         &lt;element name="Solar_Irradiance_List">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="SOLAR_IRRADIANCE" maxOccurs="13">
     *                     &lt;complexType>
     *                       &lt;simpleContent>
     *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
     *                           &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
     *                           &lt;attribute name="unit" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="W/m²/µm" />
     *                         &lt;/extension>
     *                       &lt;/simpleContent>
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
        "u",
        "solarIrradianceList"
    })
    public static class ReflectanceConversion {

        @XmlElement(name = "U")
        protected double u;
        @XmlElement(name = "Solar_Irradiance_List", required = true)
        protected ARESAMPLEDATA.ReflectanceConversion.SolarIrradianceList solarIrradianceList;

        /**
         * Obtient la valeur de la propriété u.
         * 
         */
        public double getU() {
            return u;
        }

        /**
         * Définit la valeur de la propriété u.
         * 
         */
        public void setU(double value) {
            this.u = value;
        }

        /**
         * Obtient la valeur de la propriété solarIrradianceList.
         * 
         * @return
         *     possible object is
         *     {@link ARESAMPLEDATA.ReflectanceConversion.SolarIrradianceList }
         *     
         */
        public ARESAMPLEDATA.ReflectanceConversion.SolarIrradianceList getSolarIrradianceList() {
            return solarIrradianceList;
        }

        /**
         * Définit la valeur de la propriété solarIrradianceList.
         * 
         * @param value
         *     allowed object is
         *     {@link ARESAMPLEDATA.ReflectanceConversion.SolarIrradianceList }
         *     
         */
        public void setSolarIrradianceList(ARESAMPLEDATA.ReflectanceConversion.SolarIrradianceList value) {
            this.solarIrradianceList = value;
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
         *         &lt;element name="SOLAR_IRRADIANCE" maxOccurs="13">
         *           &lt;complexType>
         *             &lt;simpleContent>
         *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
         *                 &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
         *                 &lt;attribute name="unit" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="W/m²/µm" />
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
        @XmlType(name = "", propOrder = {
            "solarirradiance"
        })
        public static class SolarIrradianceList {

            @XmlElement(name = "SOLAR_IRRADIANCE", required = true)
            protected List<ARESAMPLEDATA.ReflectanceConversion.SolarIrradianceList.SOLARIRRADIANCE> solarirradiance;

            /**
             * Gets the value of the solarirradiance property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the solarirradiance property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getSOLARIRRADIANCE().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ARESAMPLEDATA.ReflectanceConversion.SolarIrradianceList.SOLARIRRADIANCE }
             * 
             * 
             */
            public List<ARESAMPLEDATA.ReflectanceConversion.SolarIrradianceList.SOLARIRRADIANCE> getSOLARIRRADIANCE() {
                if (solarirradiance == null) {
                    solarirradiance = new ArrayList<ARESAMPLEDATA.ReflectanceConversion.SolarIrradianceList.SOLARIRRADIANCE>();
                }
                return this.solarirradiance;
            }


            /**
             * <p>Classe Java pour anonymous complex type.
             * 
             * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;simpleContent>
             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
             *       &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
             *       &lt;attribute name="unit" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="W/m²/µm" />
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
            public static class SOLARIRRADIANCE {

                @XmlValue
                protected double value;
                @XmlAttribute(name = "bandId", required = true)
                protected String bandId;
                @XmlAttribute(name = "unit", required = true)
                protected String unit;

                /**
                 * Obtient la valeur de la propriété value.
                 * 
                 */
                public double getValue() {
                    return value;
                }

                /**
                 * Définit la valeur de la propriété value.
                 * 
                 */
                public void setValue(double value) {
                    this.value = value;
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
                 * Obtient la valeur de la propriété unit.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getUnit() {
                    if (unit == null) {
                        return "W/m\u00b2/\u00b5m";
                    } else {
                        return unit;
                    }
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

    }

}
