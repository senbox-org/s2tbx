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
 * <p>Classe Java pour A_RADIOMETRIC_L1PRODUCT_QUALITY_ASSESSMENT complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_RADIOMETRIC_L1PRODUCT_QUALITY_ASSESSMENT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Radiometric_Quality_List">
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
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_RADIOMETRIC_L1PRODUCT_QUALITY_ASSESSMENT", propOrder = {
    "radiometricQualityList"
})
public class ARADIOMETRICL1PRODUCTQUALITYASSESSMENT {

    @XmlElement(name = "Radiometric_Quality_List", required = true)
    protected ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList radiometricQualityList;

    /**
     * Obtient la valeur de la propriété radiometricQualityList.
     * 
     * @return
     *     possible object is
     *     {@link ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList }
     *     
     */
    public ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList getRadiometricQualityList() {
        return radiometricQualityList;
    }

    /**
     * Définit la valeur de la propriété radiometricQualityList.
     * 
     * @param value
     *     allowed object is
     *     {@link ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList }
     *     
     */
    public void setRadiometricQualityList(ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList value) {
        this.radiometricQualityList = value;
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
        protected List<ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality> radiometricQuality;

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
         * {@link ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality }
         * 
         * 
         */
        public List<ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality> getRadiometricQuality() {
            if (radiometricQuality == null) {
                radiometricQuality = new ArrayList<ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality>();
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
            protected ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality.NoiseModel noiseModel;
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
             *     {@link ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality.NoiseModel }
             *     
             */
            public ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality.NoiseModel getNoiseModel() {
                return noiseModel;
            }

            /**
             * Définit la valeur de la propriété noiseModel.
             * 
             * @param value
             *     allowed object is
             *     {@link ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality.NoiseModel }
             *     
             */
            public void setNoiseModel(ARADIOMETRICL1PRODUCTQUALITYASSESSMENT.RadiometricQualityList.RadiometricQuality.NoiseModel value) {
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
