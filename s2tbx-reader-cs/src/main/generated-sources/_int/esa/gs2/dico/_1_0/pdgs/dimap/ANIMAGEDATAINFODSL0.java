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
 * <p>Classe Java pour AN_IMAGE_DATA_INFO_DSL0 complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_IMAGE_DATA_INFO_DSL0">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Granules_Information">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Detector_List">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Detector" maxOccurs="12">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="Granule_List">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="Granule" maxOccurs="unbounded">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;sequence>
 *                                                           &lt;element name="POSITION">
 *                                                             &lt;simpleType>
 *                                                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                                                 &lt;minInclusive value="1"/>
 *                                                               &lt;/restriction>
 *                                                             &lt;/simpleType>
 *                                                           &lt;/element>
 *                                                         &lt;/sequence>
 *                                                         &lt;attribute name="granuleId" use="required" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}GRANULE_ID" />
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                     &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DETECTOR_NUMBER" />
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
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Sensor_Configuration" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SENSOR_CONFIGURATION"/>
 *         &lt;element name="Geometric_Header_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_HEADER_LIST_EXPERTISE">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
 *               &lt;/extension>
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
@XmlType(name = "AN_IMAGE_DATA_INFO_DSL0", propOrder = {
    "granulesInformation",
    "sensorConfiguration",
    "geometricHeaderList"
})
public class ANIMAGEDATAINFODSL0 {

    @XmlElement(name = "Granules_Information", required = true)
    protected ANIMAGEDATAINFODSL0 .GranulesInformation granulesInformation;
    @XmlElement(name = "Sensor_Configuration", required = true)
    protected ASENSORCONFIGURATION sensorConfiguration;
    @XmlElement(name = "Geometric_Header_List", required = true)
    protected ANIMAGEDATAINFODSL0 .GeometricHeaderList geometricHeaderList;

    /**
     * Obtient la valeur de la propriété granulesInformation.
     * 
     * @return
     *     possible object is
     *     {@link ANIMAGEDATAINFODSL0 .GranulesInformation }
     *     
     */
    public ANIMAGEDATAINFODSL0 .GranulesInformation getGranulesInformation() {
        return granulesInformation;
    }

    /**
     * Définit la valeur de la propriété granulesInformation.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIMAGEDATAINFODSL0 .GranulesInformation }
     *     
     */
    public void setGranulesInformation(ANIMAGEDATAINFODSL0 .GranulesInformation value) {
        this.granulesInformation = value;
    }

    /**
     * Obtient la valeur de la propriété sensorConfiguration.
     * 
     * @return
     *     possible object is
     *     {@link ASENSORCONFIGURATION }
     *     
     */
    public ASENSORCONFIGURATION getSensorConfiguration() {
        return sensorConfiguration;
    }

    /**
     * Définit la valeur de la propriété sensorConfiguration.
     * 
     * @param value
     *     allowed object is
     *     {@link ASENSORCONFIGURATION }
     *     
     */
    public void setSensorConfiguration(ASENSORCONFIGURATION value) {
        this.sensorConfiguration = value;
    }

    /**
     * Obtient la valeur de la propriété geometricHeaderList.
     * 
     * @return
     *     possible object is
     *     {@link ANIMAGEDATAINFODSL0 .GeometricHeaderList }
     *     
     */
    public ANIMAGEDATAINFODSL0 .GeometricHeaderList getGeometricHeaderList() {
        return geometricHeaderList;
    }

    /**
     * Définit la valeur de la propriété geometricHeaderList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIMAGEDATAINFODSL0 .GeometricHeaderList }
     *     
     */
    public void setGeometricHeaderList(ANIMAGEDATAINFODSL0 .GeometricHeaderList value) {
        this.geometricHeaderList = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_HEADER_LIST_EXPERTISE">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GeometricHeaderList
        extends AGEOMETRICHEADERLISTEXPERTISE
    {

        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Obtient la valeur de la propriété metadataLevel.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMetadataLevel() {
            if (metadataLevel == null) {
                return "Expertise";
            } else {
                return metadataLevel;
            }
        }

        /**
         * Définit la valeur de la propriété metadataLevel.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMetadataLevel(String value) {
            this.metadataLevel = value;
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
     *         &lt;element name="Detector_List">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Detector" maxOccurs="12">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="Granule_List">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="Granule" maxOccurs="unbounded">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;sequence>
     *                                                 &lt;element name="POSITION">
     *                                                   &lt;simpleType>
     *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                                                       &lt;minInclusive value="1"/>
     *                                                     &lt;/restriction>
     *                                                   &lt;/simpleType>
     *                                                 &lt;/element>
     *                                               &lt;/sequence>
     *                                               &lt;attribute name="granuleId" use="required" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}GRANULE_ID" />
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                           &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DETECTOR_NUMBER" />
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
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "detectorList"
    })
    public static class GranulesInformation {

        @XmlElement(name = "Detector_List", required = true)
        protected ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList detectorList;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Obtient la valeur de la propriété detectorList.
         * 
         * @return
         *     possible object is
         *     {@link ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList }
         *     
         */
        public ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList getDetectorList() {
            return detectorList;
        }

        /**
         * Définit la valeur de la propriété detectorList.
         * 
         * @param value
         *     allowed object is
         *     {@link ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList }
         *     
         */
        public void setDetectorList(ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList value) {
            this.detectorList = value;
        }

        /**
         * Obtient la valeur de la propriété metadataLevel.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMetadataLevel() {
            if (metadataLevel == null) {
                return "Standard";
            } else {
                return metadataLevel;
            }
        }

        /**
         * Définit la valeur de la propriété metadataLevel.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMetadataLevel(String value) {
            this.metadataLevel = value;
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
         *                   &lt;element name="Granule_List">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="Granule" maxOccurs="unbounded">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;sequence>
         *                                       &lt;element name="POSITION">
         *                                         &lt;simpleType>
         *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *                                             &lt;minInclusive value="1"/>
         *                                           &lt;/restriction>
         *                                         &lt;/simpleType>
         *                                       &lt;/element>
         *                                     &lt;/sequence>
         *                                     &lt;attribute name="granuleId" use="required" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}GRANULE_ID" />
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
         *                 &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DETECTOR_NUMBER" />
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
            "detector"
        })
        public static class DetectorList {

            @XmlElement(name = "Detector", required = true)
            protected List<ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector> detector;

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
             * {@link ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector }
             * 
             * 
             */
            public List<ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector> getDetector() {
                if (detector == null) {
                    detector = new ArrayList<ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector>();
                }
                return this.detector;
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
             *         &lt;element name="Granule_List">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="Granule" maxOccurs="unbounded">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;sequence>
             *                             &lt;element name="POSITION">
             *                               &lt;simpleType>
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
             *                                   &lt;minInclusive value="1"/>
             *                                 &lt;/restriction>
             *                               &lt;/simpleType>
             *                             &lt;/element>
             *                           &lt;/sequence>
             *                           &lt;attribute name="granuleId" use="required" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}GRANULE_ID" />
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
             *       &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DETECTOR_NUMBER" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "granuleList"
            })
            public static class Detector {

                @XmlElement(name = "Granule_List", required = true)
                protected ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector.GranuleList granuleList;
                @XmlAttribute(name = "detectorId", required = true)
                protected String detectorId;

                /**
                 * Obtient la valeur de la propriété granuleList.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector.GranuleList }
                 *     
                 */
                public ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector.GranuleList getGranuleList() {
                    return granuleList;
                }

                /**
                 * Définit la valeur de la propriété granuleList.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector.GranuleList }
                 *     
                 */
                public void setGranuleList(ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector.GranuleList value) {
                    this.granuleList = value;
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
                 *         &lt;element name="Granule" maxOccurs="unbounded">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;sequence>
                 *                   &lt;element name="POSITION">
                 *                     &lt;simpleType>
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
                 *                         &lt;minInclusive value="1"/>
                 *                       &lt;/restriction>
                 *                     &lt;/simpleType>
                 *                   &lt;/element>
                 *                 &lt;/sequence>
                 *                 &lt;attribute name="granuleId" use="required" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}GRANULE_ID" />
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
                    "granule"
                })
                public static class GranuleList {

                    @XmlElement(name = "Granule", required = true)
                    protected List<ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector.GranuleList.Granule> granule;

                    /**
                     * Gets the value of the granule property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the granule property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getGranule().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector.GranuleList.Granule }
                     * 
                     * 
                     */
                    public List<ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector.GranuleList.Granule> getGranule() {
                        if (granule == null) {
                            granule = new ArrayList<ANIMAGEDATAINFODSL0 .GranulesInformation.DetectorList.Detector.GranuleList.Granule>();
                        }
                        return this.granule;
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
                     *         &lt;element name="POSITION">
                     *           &lt;simpleType>
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
                     *               &lt;minInclusive value="1"/>
                     *             &lt;/restriction>
                     *           &lt;/simpleType>
                     *         &lt;/element>
                     *       &lt;/sequence>
                     *       &lt;attribute name="granuleId" use="required" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}GRANULE_ID" />
                     *     &lt;/restriction>
                     *   &lt;/complexContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "position"
                    })
                    public static class Granule {

                        @XmlElement(name = "POSITION")
                        protected int position;
                        @XmlAttribute(name = "granuleId", required = true)
                        protected String granuleId;

                        /**
                         * Obtient la valeur de la propriété position.
                         * 
                         */
                        public int getPOSITION() {
                            return position;
                        }

                        /**
                         * Définit la valeur de la propriété position.
                         * 
                         */
                        public void setPOSITION(int value) {
                            this.position = value;
                        }

                        /**
                         * Obtient la valeur de la propriété granuleId.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getGranuleId() {
                            return granuleId;
                        }

                        /**
                         * Définit la valeur de la propriété granuleId.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setGranuleId(String value) {
                            this.granuleId = value;
                        }

                    }

                }

            }

        }

    }

}
