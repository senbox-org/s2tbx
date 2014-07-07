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


/**
 * <p>Classe Java pour A_QUALITY_INDICATORS_INFO_USER_PROD_L0_L1A complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_QUALITY_INDICATORS_INFO_USER_PROD_L0_L1A">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cloud_Coverage_Assessment">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="100"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Technical_Quality_Assessment">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DEGRADED_ANC_DATA_PERCENTAGE">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *                         &lt;minInclusive value="0"/>
 *                         &lt;maxInclusive value="100"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="DEGRADED_MSI_DATA_PERCENTAGE">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *                         &lt;minInclusive value="0"/>
 *                         &lt;maxInclusive value="100"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Quality_Control_Checks">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Quality_Inspections" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_QUALITY_SUMMARY_L0_L1A_USER"/>
 *                   &lt;element name="Failed_Inspections">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Datastrip_Report" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATASTRIP_REPORT_LIST"/>
 *                             &lt;element name="Granule_Report" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GRANULE_REPORT_LIST"/>
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
@XmlType(name = "A_QUALITY_INDICATORS_INFO_USER_PROD_L0_L1A", propOrder = {
    "cloudCoverageAssessment",
    "technicalQualityAssessment",
    "qualityControlChecks"
})
public class AQUALITYINDICATORSINFOUSERPRODL0L1A {

    @XmlElement(name = "Cloud_Coverage_Assessment")
    protected double cloudCoverageAssessment;
    @XmlElement(name = "Technical_Quality_Assessment", required = true)
    protected AQUALITYINDICATORSINFOUSERPRODL0L1A.TechnicalQualityAssessment technicalQualityAssessment;
    @XmlElement(name = "Quality_Control_Checks", required = true)
    protected AQUALITYINDICATORSINFOUSERPRODL0L1A.QualityControlChecks qualityControlChecks;

    /**
     * Obtient la valeur de la propriété cloudCoverageAssessment.
     * 
     */
    public double getCloudCoverageAssessment() {
        return cloudCoverageAssessment;
    }

    /**
     * Définit la valeur de la propriété cloudCoverageAssessment.
     * 
     */
    public void setCloudCoverageAssessment(double value) {
        this.cloudCoverageAssessment = value;
    }

    /**
     * Obtient la valeur de la propriété technicalQualityAssessment.
     * 
     * @return
     *     possible object is
     *     {@link AQUALITYINDICATORSINFOUSERPRODL0L1A.TechnicalQualityAssessment }
     *     
     */
    public AQUALITYINDICATORSINFOUSERPRODL0L1A.TechnicalQualityAssessment getTechnicalQualityAssessment() {
        return technicalQualityAssessment;
    }

    /**
     * Définit la valeur de la propriété technicalQualityAssessment.
     * 
     * @param value
     *     allowed object is
     *     {@link AQUALITYINDICATORSINFOUSERPRODL0L1A.TechnicalQualityAssessment }
     *     
     */
    public void setTechnicalQualityAssessment(AQUALITYINDICATORSINFOUSERPRODL0L1A.TechnicalQualityAssessment value) {
        this.technicalQualityAssessment = value;
    }

    /**
     * Obtient la valeur de la propriété qualityControlChecks.
     * 
     * @return
     *     possible object is
     *     {@link AQUALITYINDICATORSINFOUSERPRODL0L1A.QualityControlChecks }
     *     
     */
    public AQUALITYINDICATORSINFOUSERPRODL0L1A.QualityControlChecks getQualityControlChecks() {
        return qualityControlChecks;
    }

    /**
     * Définit la valeur de la propriété qualityControlChecks.
     * 
     * @param value
     *     allowed object is
     *     {@link AQUALITYINDICATORSINFOUSERPRODL0L1A.QualityControlChecks }
     *     
     */
    public void setQualityControlChecks(AQUALITYINDICATORSINFOUSERPRODL0L1A.QualityControlChecks value) {
        this.qualityControlChecks = value;
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
     *         &lt;element name="Quality_Inspections" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_QUALITY_SUMMARY_L0_L1A_USER"/>
     *         &lt;element name="Failed_Inspections">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Datastrip_Report" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATASTRIP_REPORT_LIST"/>
     *                   &lt;element name="Granule_Report" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GRANULE_REPORT_LIST"/>
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
        "qualityInspections",
        "failedInspections"
    })
    public static class QualityControlChecks {

        @XmlElement(name = "Quality_Inspections", required = true)
        protected AQUALITYSUMMARYL0L1AUSER qualityInspections;
        @XmlElement(name = "Failed_Inspections", required = true)
        protected AQUALITYINDICATORSINFOUSERPRODL0L1A.QualityControlChecks.FailedInspections failedInspections;

        /**
         * Obtient la valeur de la propriété qualityInspections.
         * 
         * @return
         *     possible object is
         *     {@link AQUALITYSUMMARYL0L1AUSER }
         *     
         */
        public AQUALITYSUMMARYL0L1AUSER getQualityInspections() {
            return qualityInspections;
        }

        /**
         * Définit la valeur de la propriété qualityInspections.
         * 
         * @param value
         *     allowed object is
         *     {@link AQUALITYSUMMARYL0L1AUSER }
         *     
         */
        public void setQualityInspections(AQUALITYSUMMARYL0L1AUSER value) {
            this.qualityInspections = value;
        }

        /**
         * Obtient la valeur de la propriété failedInspections.
         * 
         * @return
         *     possible object is
         *     {@link AQUALITYINDICATORSINFOUSERPRODL0L1A.QualityControlChecks.FailedInspections }
         *     
         */
        public AQUALITYINDICATORSINFOUSERPRODL0L1A.QualityControlChecks.FailedInspections getFailedInspections() {
            return failedInspections;
        }

        /**
         * Définit la valeur de la propriété failedInspections.
         * 
         * @param value
         *     allowed object is
         *     {@link AQUALITYINDICATORSINFOUSERPRODL0L1A.QualityControlChecks.FailedInspections }
         *     
         */
        public void setFailedInspections(AQUALITYINDICATORSINFOUSERPRODL0L1A.QualityControlChecks.FailedInspections value) {
            this.failedInspections = value;
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
         *         &lt;element name="Datastrip_Report" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATASTRIP_REPORT_LIST"/>
         *         &lt;element name="Granule_Report" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GRANULE_REPORT_LIST"/>
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
            "datastripReport",
            "granuleReport"
        })
        public static class FailedInspections {

            @XmlElement(name = "Datastrip_Report", required = true)
            protected ADATASTRIPREPORTLIST datastripReport;
            @XmlElement(name = "Granule_Report", required = true)
            protected AGRANULEREPORTLIST granuleReport;

            /**
             * Obtient la valeur de la propriété datastripReport.
             * 
             * @return
             *     possible object is
             *     {@link ADATASTRIPREPORTLIST }
             *     
             */
            public ADATASTRIPREPORTLIST getDatastripReport() {
                return datastripReport;
            }

            /**
             * Définit la valeur de la propriété datastripReport.
             * 
             * @param value
             *     allowed object is
             *     {@link ADATASTRIPREPORTLIST }
             *     
             */
            public void setDatastripReport(ADATASTRIPREPORTLIST value) {
                this.datastripReport = value;
            }

            /**
             * Obtient la valeur de la propriété granuleReport.
             * 
             * @return
             *     possible object is
             *     {@link AGRANULEREPORTLIST }
             *     
             */
            public AGRANULEREPORTLIST getGranuleReport() {
                return granuleReport;
            }

            /**
             * Définit la valeur de la propriété granuleReport.
             * 
             * @param value
             *     allowed object is
             *     {@link AGRANULEREPORTLIST }
             *     
             */
            public void setGranuleReport(AGRANULEREPORTLIST value) {
                this.granuleReport = value;
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
     *         &lt;element name="DEGRADED_ANC_DATA_PERCENTAGE">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
     *               &lt;minInclusive value="0"/>
     *               &lt;maxInclusive value="100"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="DEGRADED_MSI_DATA_PERCENTAGE">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
     *               &lt;minInclusive value="0"/>
     *               &lt;maxInclusive value="100"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
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
        "degradedancdatapercentage",
        "degradedmsidatapercentage"
    })
    public static class TechnicalQualityAssessment {

        @XmlElement(name = "DEGRADED_ANC_DATA_PERCENTAGE")
        protected double degradedancdatapercentage;
        @XmlElement(name = "DEGRADED_MSI_DATA_PERCENTAGE")
        protected double degradedmsidatapercentage;

        /**
         * Obtient la valeur de la propriété degradedancdatapercentage.
         * 
         */
        public double getDEGRADEDANCDATAPERCENTAGE() {
            return degradedancdatapercentage;
        }

        /**
         * Définit la valeur de la propriété degradedancdatapercentage.
         * 
         */
        public void setDEGRADEDANCDATAPERCENTAGE(double value) {
            this.degradedancdatapercentage = value;
        }

        /**
         * Obtient la valeur de la propriété degradedmsidatapercentage.
         * 
         */
        public double getDEGRADEDMSIDATAPERCENTAGE() {
            return degradedmsidatapercentage;
        }

        /**
         * Définit la valeur de la propriété degradedmsidatapercentage.
         * 
         */
        public void setDEGRADEDMSIDATAPERCENTAGE(double value) {
            this.degradedmsidatapercentage = value;
        }

    }

}
