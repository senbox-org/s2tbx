//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHMUNITATTR;
import _int.esa.gs2.dico._1_0.sy.spatio.ASPATIORESULT;


/**
 * Quality assessement created by GEO_S2
 * 
 * <p>Classe Java pour A_GEOMETRIC_REFINING_QUALITY_L1B_L1C complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GEOMETRIC_REFINING_QUALITY_L1B_L1C">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Image_Refining" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Correlation_Quality" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_CORRELATION_QUALITY"/>
 *                   &lt;element name="Performance_Indicators">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Multi_Temporal_Registration">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
 *                                       &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="Multi_Spectral_Registration">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="Band_10m">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
 *                                                 &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="Band_20m">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
 *                                                 &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="Band_60m">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
 *                                                 &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
 *                             &lt;element name="Reference_Geolocation">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
 *                                       &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
 *         &lt;element name="VNIR_SWIR_Registration" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Correlation_Quality" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_CORRELATION_QUALITY"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Spatiotriangulation_Residual_Histogram" type="{http://gs2.esa.int/DICO/1.0/SY/spatio/}A_SPATIO_RESULT" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_GEOMETRIC_REFINING_QUALITY_L1B_L1C", propOrder = {
    "imageRefining",
    "vnirswirRegistration",
    "spatiotriangulationResidualHistogram"
})
public class AGEOMETRICREFININGQUALITYL1BL1C {

    @XmlElement(name = "Image_Refining")
    protected AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining imageRefining;
    @XmlElement(name = "VNIR_SWIR_Registration")
    protected AGEOMETRICREFININGQUALITYL1BL1C.VNIRSWIRRegistration vnirswirRegistration;
    @XmlElement(name = "Spatiotriangulation_Residual_Histogram")
    protected ASPATIORESULT spatiotriangulationResidualHistogram;

    /**
     * Obtient la valeur de la propriété imageRefining.
     * 
     * @return
     *     possible object is
     *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining }
     *     
     */
    public AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining getImageRefining() {
        return imageRefining;
    }

    /**
     * Définit la valeur de la propriété imageRefining.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining }
     *     
     */
    public void setImageRefining(AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining value) {
        this.imageRefining = value;
    }

    /**
     * Obtient la valeur de la propriété vnirswirRegistration.
     * 
     * @return
     *     possible object is
     *     {@link AGEOMETRICREFININGQUALITYL1BL1C.VNIRSWIRRegistration }
     *     
     */
    public AGEOMETRICREFININGQUALITYL1BL1C.VNIRSWIRRegistration getVNIRSWIRRegistration() {
        return vnirswirRegistration;
    }

    /**
     * Définit la valeur de la propriété vnirswirRegistration.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOMETRICREFININGQUALITYL1BL1C.VNIRSWIRRegistration }
     *     
     */
    public void setVNIRSWIRRegistration(AGEOMETRICREFININGQUALITYL1BL1C.VNIRSWIRRegistration value) {
        this.vnirswirRegistration = value;
    }

    /**
     * Obtient la valeur de la propriété spatiotriangulationResidualHistogram.
     * 
     * @return
     *     possible object is
     *     {@link ASPATIORESULT }
     *     
     */
    public ASPATIORESULT getSpatiotriangulationResidualHistogram() {
        return spatiotriangulationResidualHistogram;
    }

    /**
     * Définit la valeur de la propriété spatiotriangulationResidualHistogram.
     * 
     * @param value
     *     allowed object is
     *     {@link ASPATIORESULT }
     *     
     */
    public void setSpatiotriangulationResidualHistogram(ASPATIORESULT value) {
        this.spatiotriangulationResidualHistogram = value;
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
     *         &lt;element name="Correlation_Quality" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_CORRELATION_QUALITY"/>
     *         &lt;element name="Performance_Indicators">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Multi_Temporal_Registration">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
     *                             &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="Multi_Spectral_Registration">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="Band_10m">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
     *                                       &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="Band_20m">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
     *                                       &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="Band_60m">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
     *                                       &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
     *                   &lt;element name="Reference_Geolocation">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
     *                             &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
        "correlationQuality",
        "performanceIndicators"
    })
    public static class ImageRefining {

        @XmlElement(name = "Correlation_Quality", required = true)
        protected ACORRELATIONQUALITY correlationQuality;
        @XmlElement(name = "Performance_Indicators", required = true)
        protected AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators performanceIndicators;

        /**
         * Obtient la valeur de la propriété correlationQuality.
         * 
         * @return
         *     possible object is
         *     {@link ACORRELATIONQUALITY }
         *     
         */
        public ACORRELATIONQUALITY getCorrelationQuality() {
            return correlationQuality;
        }

        /**
         * Définit la valeur de la propriété correlationQuality.
         * 
         * @param value
         *     allowed object is
         *     {@link ACORRELATIONQUALITY }
         *     
         */
        public void setCorrelationQuality(ACORRELATIONQUALITY value) {
            this.correlationQuality = value;
        }

        /**
         * Obtient la valeur de la propriété performanceIndicators.
         * 
         * @return
         *     possible object is
         *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators }
         *     
         */
        public AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators getPerformanceIndicators() {
            return performanceIndicators;
        }

        /**
         * Définit la valeur de la propriété performanceIndicators.
         * 
         * @param value
         *     allowed object is
         *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators }
         *     
         */
        public void setPerformanceIndicators(AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators value) {
            this.performanceIndicators = value;
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
         *         &lt;element name="Multi_Temporal_Registration">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
         *                   &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="Multi_Spectral_Registration">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="Band_10m">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
         *                             &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="Band_20m">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
         *                             &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="Band_60m">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
         *                             &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
         *         &lt;element name="Reference_Geolocation">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
         *                   &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
            "multiTemporalRegistration",
            "multiSpectralRegistration",
            "referenceGeolocation"
        })
        public static class PerformanceIndicators {

            @XmlElement(name = "Multi_Temporal_Registration", required = true)
            protected AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiTemporalRegistration multiTemporalRegistration;
            @XmlElement(name = "Multi_Spectral_Registration", required = true)
            protected AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration multiSpectralRegistration;
            @XmlElement(name = "Reference_Geolocation", required = true)
            protected AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.ReferenceGeolocation referenceGeolocation;

            /**
             * Obtient la valeur de la propriété multiTemporalRegistration.
             * 
             * @return
             *     possible object is
             *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiTemporalRegistration }
             *     
             */
            public AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiTemporalRegistration getMultiTemporalRegistration() {
                return multiTemporalRegistration;
            }

            /**
             * Définit la valeur de la propriété multiTemporalRegistration.
             * 
             * @param value
             *     allowed object is
             *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiTemporalRegistration }
             *     
             */
            public void setMultiTemporalRegistration(AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiTemporalRegistration value) {
                this.multiTemporalRegistration = value;
            }

            /**
             * Obtient la valeur de la propriété multiSpectralRegistration.
             * 
             * @return
             *     possible object is
             *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration }
             *     
             */
            public AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration getMultiSpectralRegistration() {
                return multiSpectralRegistration;
            }

            /**
             * Définit la valeur de la propriété multiSpectralRegistration.
             * 
             * @param value
             *     allowed object is
             *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration }
             *     
             */
            public void setMultiSpectralRegistration(AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration value) {
                this.multiSpectralRegistration = value;
            }

            /**
             * Obtient la valeur de la propriété referenceGeolocation.
             * 
             * @return
             *     possible object is
             *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.ReferenceGeolocation }
             *     
             */
            public AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.ReferenceGeolocation getReferenceGeolocation() {
                return referenceGeolocation;
            }

            /**
             * Définit la valeur de la propriété referenceGeolocation.
             * 
             * @param value
             *     allowed object is
             *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.ReferenceGeolocation }
             *     
             */
            public void setReferenceGeolocation(AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.ReferenceGeolocation value) {
                this.referenceGeolocation = value;
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
             *         &lt;element name="Band_10m">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
             *                   &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="Band_20m">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
             *                   &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="Band_60m">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
             *                   &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                "band10M",
                "band20M",
                "band60M"
            })
            public static class MultiSpectralRegistration {

                @XmlElement(name = "Band_10m", required = true)
                protected AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band10M band10M;
                @XmlElement(name = "Band_20m", required = true)
                protected AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band20M band20M;
                @XmlElement(name = "Band_60m", required = true)
                protected AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band60M band60M;

                /**
                 * Obtient la valeur de la propriété band10M.
                 * 
                 * @return
                 *     possible object is
                 *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band10M }
                 *     
                 */
                public AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band10M getBand10M() {
                    return band10M;
                }

                /**
                 * Définit la valeur de la propriété band10M.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band10M }
                 *     
                 */
                public void setBand10M(AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band10M value) {
                    this.band10M = value;
                }

                /**
                 * Obtient la valeur de la propriété band20M.
                 * 
                 * @return
                 *     possible object is
                 *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band20M }
                 *     
                 */
                public AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band20M getBand20M() {
                    return band20M;
                }

                /**
                 * Définit la valeur de la propriété band20M.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band20M }
                 *     
                 */
                public void setBand20M(AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band20M value) {
                    this.band20M = value;
                }

                /**
                 * Obtient la valeur de la propriété band60M.
                 * 
                 * @return
                 *     possible object is
                 *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band60M }
                 *     
                 */
                public AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band60M getBand60M() {
                    return band60M;
                }

                /**
                 * Définit la valeur de la propriété band60M.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band60M }
                 *     
                 */
                public void setBand60M(AGEOMETRICREFININGQUALITYL1BL1C.ImageRefining.PerformanceIndicators.MultiSpectralRegistration.Band60M value) {
                    this.band60M = value;
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
                 *         &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
                 *         &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                    "value",
                    "measurementdate"
                })
                public static class Band10M {

                    @XmlElement(name = "VALUE", required = true)
                    protected ADOUBLEWITHMUNITATTR value;
                    @XmlElement(name = "MEASUREMENT_DATE", required = true)
                    protected XMLGregorianCalendar measurementdate;

                    /**
                     * Obtient la valeur de la propriété value.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHMUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHMUNITATTR getVALUE() {
                        return value;
                    }

                    /**
                     * Définit la valeur de la propriété value.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHMUNITATTR }
                     *     
                     */
                    public void setVALUE(ADOUBLEWITHMUNITATTR value) {
                        this.value = value;
                    }

                    /**
                     * Obtient la valeur de la propriété measurementdate.
                     * 
                     * @return
                     *     possible object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public XMLGregorianCalendar getMEASUREMENTDATE() {
                        return measurementdate;
                    }

                    /**
                     * Définit la valeur de la propriété measurementdate.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public void setMEASUREMENTDATE(XMLGregorianCalendar value) {
                        this.measurementdate = value;
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
                 *         &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
                 *         &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                    "value",
                    "measurementdate"
                })
                public static class Band20M {

                    @XmlElement(name = "VALUE", required = true)
                    protected ADOUBLEWITHMUNITATTR value;
                    @XmlElement(name = "MEASUREMENT_DATE", required = true)
                    protected XMLGregorianCalendar measurementdate;

                    /**
                     * Obtient la valeur de la propriété value.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHMUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHMUNITATTR getVALUE() {
                        return value;
                    }

                    /**
                     * Définit la valeur de la propriété value.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHMUNITATTR }
                     *     
                     */
                    public void setVALUE(ADOUBLEWITHMUNITATTR value) {
                        this.value = value;
                    }

                    /**
                     * Obtient la valeur de la propriété measurementdate.
                     * 
                     * @return
                     *     possible object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public XMLGregorianCalendar getMEASUREMENTDATE() {
                        return measurementdate;
                    }

                    /**
                     * Définit la valeur de la propriété measurementdate.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public void setMEASUREMENTDATE(XMLGregorianCalendar value) {
                        this.measurementdate = value;
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
                 *         &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
                 *         &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                    "value",
                    "measurementdate"
                })
                public static class Band60M {

                    @XmlElement(name = "VALUE", required = true)
                    protected ADOUBLEWITHMUNITATTR value;
                    @XmlElement(name = "MEASUREMENT_DATE", required = true)
                    protected XMLGregorianCalendar measurementdate;

                    /**
                     * Obtient la valeur de la propriété value.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHMUNITATTR }
                     *     
                     */
                    public ADOUBLEWITHMUNITATTR getVALUE() {
                        return value;
                    }

                    /**
                     * Définit la valeur de la propriété value.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHMUNITATTR }
                     *     
                     */
                    public void setVALUE(ADOUBLEWITHMUNITATTR value) {
                        this.value = value;
                    }

                    /**
                     * Obtient la valeur de la propriété measurementdate.
                     * 
                     * @return
                     *     possible object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public XMLGregorianCalendar getMEASUREMENTDATE() {
                        return measurementdate;
                    }

                    /**
                     * Définit la valeur de la propriété measurementdate.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public void setMEASUREMENTDATE(XMLGregorianCalendar value) {
                        this.measurementdate = value;
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
             *         &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
             *         &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                "value",
                "measurementdate"
            })
            public static class MultiTemporalRegistration {

                @XmlElement(name = "VALUE", required = true)
                protected ADOUBLEWITHMUNITATTR value;
                @XmlElement(name = "MEASUREMENT_DATE", required = true)
                protected XMLGregorianCalendar measurementdate;

                /**
                 * Obtient la valeur de la propriété value.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ADOUBLEWITHMUNITATTR }
                 *     
                 */
                public ADOUBLEWITHMUNITATTR getVALUE() {
                    return value;
                }

                /**
                 * Définit la valeur de la propriété value.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ADOUBLEWITHMUNITATTR }
                 *     
                 */
                public void setVALUE(ADOUBLEWITHMUNITATTR value) {
                    this.value = value;
                }

                /**
                 * Obtient la valeur de la propriété measurementdate.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getMEASUREMENTDATE() {
                    return measurementdate;
                }

                /**
                 * Définit la valeur de la propriété measurementdate.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setMEASUREMENTDATE(XMLGregorianCalendar value) {
                    this.measurementdate = value;
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
             *         &lt;element name="VALUE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
             *         &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
                "value",
                "measurementdate"
            })
            public static class ReferenceGeolocation {

                @XmlElement(name = "VALUE", required = true)
                protected ADOUBLEWITHMUNITATTR value;
                @XmlElement(name = "MEASUREMENT_DATE", required = true)
                protected XMLGregorianCalendar measurementdate;

                /**
                 * Obtient la valeur de la propriété value.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ADOUBLEWITHMUNITATTR }
                 *     
                 */
                public ADOUBLEWITHMUNITATTR getVALUE() {
                    return value;
                }

                /**
                 * Définit la valeur de la propriété value.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ADOUBLEWITHMUNITATTR }
                 *     
                 */
                public void setVALUE(ADOUBLEWITHMUNITATTR value) {
                    this.value = value;
                }

                /**
                 * Obtient la valeur de la propriété measurementdate.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getMEASUREMENTDATE() {
                    return measurementdate;
                }

                /**
                 * Définit la valeur de la propriété measurementdate.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setMEASUREMENTDATE(XMLGregorianCalendar value) {
                    this.measurementdate = value;
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
     *         &lt;element name="Correlation_Quality" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_CORRELATION_QUALITY"/>
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
        "correlationQuality"
    })
    public static class VNIRSWIRRegistration {

        @XmlElement(name = "Correlation_Quality", required = true)
        protected ACORRELATIONQUALITY correlationQuality;

        /**
         * Obtient la valeur de la propriété correlationQuality.
         * 
         * @return
         *     possible object is
         *     {@link ACORRELATIONQUALITY }
         *     
         */
        public ACORRELATIONQUALITY getCorrelationQuality() {
            return correlationQuality;
        }

        /**
         * Définit la valeur de la propriété correlationQuality.
         * 
         * @param value
         *     allowed object is
         *     {@link ACORRELATIONQUALITY }
         *     
         */
        public void setCorrelationQuality(ACORRELATIONQUALITY value) {
            this.correlationQuality = value;
        }

    }

}
