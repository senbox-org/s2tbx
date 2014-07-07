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


/**
 * <p>Classe Java pour A_L1A_PRODUCT_QUALITY_ASSESSMENT complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_L1A_PRODUCT_QUALITY_ASSESSMENT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Absolute_Location" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ABSOLUTE_LOCATION" minOccurs="0"/>
 *         &lt;element name="Planimetric_Stability" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PLANIMETRIC_STABILITY" minOccurs="0"/>
 *         &lt;element name="EPHEMERIS_QUALITY" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_EPHEMERIS_QUALITY" minOccurs="0"/>
 *         &lt;element name="Mask_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_MASK_LIST" minOccurs="0"/>
 *         &lt;element name="CLOUDY_PIXEL_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_CLOUDY_PIXEL_PERCENTAGE" minOccurs="0"/>
 *         &lt;element name="Radiometric_Quality_List" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Radiometric_Quality" maxOccurs="13">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Noise_Model">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="ALPHA" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                       &lt;element name="BETA" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="ABSOLUTE_CALIBRATION_ACCURACY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
 *                             &lt;element name="CROSS_BAND_CALIBRATION_ACCURACY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
 *                             &lt;element name="MULTI_TEMPORAL_CALIBRATION_ACCURACY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
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
 *         &lt;element name="Data_Content_Quality" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATA_CONTENT_QUALITY" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_L1A_PRODUCT_QUALITY_ASSESSMENT", propOrder = {
    "absoluteLocation",
    "planimetricStability",
    "ephemerisquality",
    "maskList",
    "cloudypixelpercentage",
    "radiometricQualityList",
    "dataContentQuality"
})
public class AL1APRODUCTQUALITYASSESSMENT {

    @XmlElement(name = "Absolute_Location")
    protected ANABSOLUTELOCATION absoluteLocation;
    @XmlElement(name = "Planimetric_Stability")
    protected APLANIMETRICSTABILITY planimetricStability;
    @XmlElement(name = "EPHEMERIS_QUALITY")
    protected Double ephemerisquality;
    @XmlElement(name = "Mask_List")
    protected AMASKLIST maskList;
    @XmlElement(name = "CLOUDY_PIXEL_PERCENTAGE")
    protected Double cloudypixelpercentage;
    @XmlElement(name = "Radiometric_Quality_List")
    protected AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList radiometricQualityList;
    @XmlElement(name = "Data_Content_Quality")
    protected ADATACONTENTQUALITY dataContentQuality;

    /**
     * Obtient la valeur de la propriété absoluteLocation.
     * 
     * @return
     *     possible object is
     *     {@link ANABSOLUTELOCATION }
     *     
     */
    public ANABSOLUTELOCATION getAbsoluteLocation() {
        return absoluteLocation;
    }

    /**
     * Définit la valeur de la propriété absoluteLocation.
     * 
     * @param value
     *     allowed object is
     *     {@link ANABSOLUTELOCATION }
     *     
     */
    public void setAbsoluteLocation(ANABSOLUTELOCATION value) {
        this.absoluteLocation = value;
    }

    /**
     * Obtient la valeur de la propriété planimetricStability.
     * 
     * @return
     *     possible object is
     *     {@link APLANIMETRICSTABILITY }
     *     
     */
    public APLANIMETRICSTABILITY getPlanimetricStability() {
        return planimetricStability;
    }

    /**
     * Définit la valeur de la propriété planimetricStability.
     * 
     * @param value
     *     allowed object is
     *     {@link APLANIMETRICSTABILITY }
     *     
     */
    public void setPlanimetricStability(APLANIMETRICSTABILITY value) {
        this.planimetricStability = value;
    }

    /**
     * Obtient la valeur de la propriété ephemerisquality.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getEPHEMERISQUALITY() {
        return ephemerisquality;
    }

    /**
     * Définit la valeur de la propriété ephemerisquality.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setEPHEMERISQUALITY(Double value) {
        this.ephemerisquality = value;
    }

    /**
     * Obtient la valeur de la propriété maskList.
     * 
     * @return
     *     possible object is
     *     {@link AMASKLIST }
     *     
     */
    public AMASKLIST getMaskList() {
        return maskList;
    }

    /**
     * Définit la valeur de la propriété maskList.
     * 
     * @param value
     *     allowed object is
     *     {@link AMASKLIST }
     *     
     */
    public void setMaskList(AMASKLIST value) {
        this.maskList = value;
    }

    /**
     * Obtient la valeur de la propriété cloudypixelpercentage.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCLOUDYPIXELPERCENTAGE() {
        return cloudypixelpercentage;
    }

    /**
     * Définit la valeur de la propriété cloudypixelpercentage.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCLOUDYPIXELPERCENTAGE(Double value) {
        this.cloudypixelpercentage = value;
    }

    /**
     * Obtient la valeur de la propriété radiometricQualityList.
     * 
     * @return
     *     possible object is
     *     {@link AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList }
     *     
     */
    public AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList getRadiometricQualityList() {
        return radiometricQualityList;
    }

    /**
     * Définit la valeur de la propriété radiometricQualityList.
     * 
     * @param value
     *     allowed object is
     *     {@link AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList }
     *     
     */
    public void setRadiometricQualityList(AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList value) {
        this.radiometricQualityList = value;
    }

    /**
     * Obtient la valeur de la propriété dataContentQuality.
     * 
     * @return
     *     possible object is
     *     {@link ADATACONTENTQUALITY }
     *     
     */
    public ADATACONTENTQUALITY getDataContentQuality() {
        return dataContentQuality;
    }

    /**
     * Définit la valeur de la propriété dataContentQuality.
     * 
     * @param value
     *     allowed object is
     *     {@link ADATACONTENTQUALITY }
     *     
     */
    public void setDataContentQuality(ADATACONTENTQUALITY value) {
        this.dataContentQuality = value;
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
     *         &lt;element name="Radiometric_Quality" maxOccurs="13">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Noise_Model">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="ALPHA" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                             &lt;element name="BETA" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="ABSOLUTE_CALIBRATION_ACCURACY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
     *                   &lt;element name="CROSS_BAND_CALIBRATION_ACCURACY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
     *                   &lt;element name="MULTI_TEMPORAL_CALIBRATION_ACCURACY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
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
        "radiometricQuality"
    })
    public static class RadiometricQualityList {

        @XmlElement(name = "Radiometric_Quality", required = true)
        protected List<AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality> radiometricQuality;

        /**
         * Gets the value of the radiometricQuality property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the radiometricQuality property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRadiometricQuality().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality }
         * 
         * 
         */
        public List<AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality> getRadiometricQuality() {
            if (radiometricQuality == null) {
                radiometricQuality = new ArrayList<AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality>();
            }
            return this.radiometricQuality;
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
         *         &lt;element name="Noise_Model">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="ALPHA" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                   &lt;element name="BETA" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="ABSOLUTE_CALIBRATION_ACCURACY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
         *         &lt;element name="CROSS_BAND_CALIBRATION_ACCURACY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
         *         &lt;element name="MULTI_TEMPORAL_CALIBRATION_ACCURACY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
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
            "noiseModel",
            "absolutecalibrationaccuracy",
            "crossbandcalibrationaccuracy",
            "multitemporalcalibrationaccuracy"
        })
        public static class RadiometricQuality {

            @XmlElement(name = "Noise_Model", required = true)
            protected AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality.NoiseModel noiseModel;
            @XmlElement(name = "ABSOLUTE_CALIBRATION_ACCURACY")
            protected double absolutecalibrationaccuracy;
            @XmlElement(name = "CROSS_BAND_CALIBRATION_ACCURACY")
            protected double crossbandcalibrationaccuracy;
            @XmlElement(name = "MULTI_TEMPORAL_CALIBRATION_ACCURACY")
            protected double multitemporalcalibrationaccuracy;
            @XmlAttribute(name = "bandId", required = true)
            protected String bandId;

            /**
             * Obtient la valeur de la propriété noiseModel.
             * 
             * @return
             *     possible object is
             *     {@link AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality.NoiseModel }
             *     
             */
            public AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality.NoiseModel getNoiseModel() {
                return noiseModel;
            }

            /**
             * Définit la valeur de la propriété noiseModel.
             * 
             * @param value
             *     allowed object is
             *     {@link AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality.NoiseModel }
             *     
             */
            public void setNoiseModel(AL1APRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality.NoiseModel value) {
                this.noiseModel = value;
            }

            /**
             * Obtient la valeur de la propriété absolutecalibrationaccuracy.
             * 
             */
            public double getABSOLUTECALIBRATIONACCURACY() {
                return absolutecalibrationaccuracy;
            }

            /**
             * Définit la valeur de la propriété absolutecalibrationaccuracy.
             * 
             */
            public void setABSOLUTECALIBRATIONACCURACY(double value) {
                this.absolutecalibrationaccuracy = value;
            }

            /**
             * Obtient la valeur de la propriété crossbandcalibrationaccuracy.
             * 
             */
            public double getCROSSBANDCALIBRATIONACCURACY() {
                return crossbandcalibrationaccuracy;
            }

            /**
             * Définit la valeur de la propriété crossbandcalibrationaccuracy.
             * 
             */
            public void setCROSSBANDCALIBRATIONACCURACY(double value) {
                this.crossbandcalibrationaccuracy = value;
            }

            /**
             * Obtient la valeur de la propriété multitemporalcalibrationaccuracy.
             * 
             */
            public double getMULTITEMPORALCALIBRATIONACCURACY() {
                return multitemporalcalibrationaccuracy;
            }

            /**
             * Définit la valeur de la propriété multitemporalcalibrationaccuracy.
             * 
             */
            public void setMULTITEMPORALCALIBRATIONACCURACY(double value) {
                this.multitemporalcalibrationaccuracy = value;
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
             *         &lt;element name="ALPHA" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *         &lt;element name="BETA" type="{http://www.w3.org/2001/XMLSchema}double"/>
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
                "alpha",
                "beta"
            })
            public static class NoiseModel {

                @XmlElement(name = "ALPHA")
                protected double alpha;
                @XmlElement(name = "BETA")
                protected double beta;

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

            }

        }

    }

}
