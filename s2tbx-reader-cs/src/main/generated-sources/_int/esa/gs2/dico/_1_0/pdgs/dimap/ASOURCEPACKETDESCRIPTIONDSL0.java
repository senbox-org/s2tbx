//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_SOURCE_PACKET_DESCRIPTION_DSL0 complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_SOURCE_PACKET_DESCRIPTION_DSL0">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Source_Packet_Counters_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Detector_List">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Detector" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="Band_List">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="Band" maxOccurs="unbounded">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;sequence>
 *                                                           &lt;element name="DATA_STRIP_START" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *                                                           &lt;element name="SCENE_POSITION">
 *                                                             &lt;simpleType>
 *                                                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                                                 &lt;minInclusive value="1"/>
 *                                                                 &lt;maxInclusive value="2304"/>
 *                                                               &lt;/restriction>
 *                                                             &lt;/simpleType>
 *                                                           &lt;/element>
 *                                                           &lt;element name="NB_OF_SOURCE_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *                                                         &lt;/sequence>
 *                                                         &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
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
 *                                     &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
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
 *         &lt;element name="Degradation_Summary">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="NUMBER_OF_LOST_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *                   &lt;element name="NUMBER_OF_TOO_DEGRADED_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *                   &lt;element name="NUMBER_OF_KEPT_DEGRADED_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="degradationPercentage">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
 *                       &lt;minInclusive value="0"/>
 *                       &lt;maxInclusive value="100"/>
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
@XmlType(name = "A_SOURCE_PACKET_DESCRIPTION_DSL0", propOrder = {
    "sourcePacketCountersList",
    "degradationSummary"
})
public class ASOURCEPACKETDESCRIPTIONDSL0 {

    @XmlElement(name = "Source_Packet_Counters_List", required = true)
    protected ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList sourcePacketCountersList;
    @XmlElement(name = "Degradation_Summary", required = true)
    protected ASOURCEPACKETDESCRIPTIONDSL0 .DegradationSummary degradationSummary;

    /**
     * Obtient la valeur de la propriété sourcePacketCountersList.
     * 
     * @return
     *     possible object is
     *     {@link ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList }
     *     
     */
    public ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList getSourcePacketCountersList() {
        return sourcePacketCountersList;
    }

    /**
     * Définit la valeur de la propriété sourcePacketCountersList.
     * 
     * @param value
     *     allowed object is
     *     {@link ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList }
     *     
     */
    public void setSourcePacketCountersList(ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList value) {
        this.sourcePacketCountersList = value;
    }

    /**
     * Obtient la valeur de la propriété degradationSummary.
     * 
     * @return
     *     possible object is
     *     {@link ASOURCEPACKETDESCRIPTIONDSL0 .DegradationSummary }
     *     
     */
    public ASOURCEPACKETDESCRIPTIONDSL0 .DegradationSummary getDegradationSummary() {
        return degradationSummary;
    }

    /**
     * Définit la valeur de la propriété degradationSummary.
     * 
     * @param value
     *     allowed object is
     *     {@link ASOURCEPACKETDESCRIPTIONDSL0 .DegradationSummary }
     *     
     */
    public void setDegradationSummary(ASOURCEPACKETDESCRIPTIONDSL0 .DegradationSummary value) {
        this.degradationSummary = value;
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
     *         &lt;element name="NUMBER_OF_LOST_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
     *         &lt;element name="NUMBER_OF_TOO_DEGRADED_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
     *         &lt;element name="NUMBER_OF_KEPT_DEGRADED_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
     *       &lt;/sequence>
     *       &lt;attribute name="degradationPercentage">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}float">
     *             &lt;minInclusive value="0"/>
     *             &lt;maxInclusive value="100"/>
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
        "numberoflostpackets",
        "numberoftoodegradedpackets",
        "numberofkeptdegradedpackets"
    })
    public static class DegradationSummary {

        @XmlElement(name = "NUMBER_OF_LOST_PACKETS", required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger numberoflostpackets;
        @XmlElement(name = "NUMBER_OF_TOO_DEGRADED_PACKETS", required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger numberoftoodegradedpackets;
        @XmlElement(name = "NUMBER_OF_KEPT_DEGRADED_PACKETS", required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger numberofkeptdegradedpackets;
        @XmlAttribute(name = "degradationPercentage")
        protected Float degradationPercentage;

        /**
         * Obtient la valeur de la propriété numberoflostpackets.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getNUMBEROFLOSTPACKETS() {
            return numberoflostpackets;
        }

        /**
         * Définit la valeur de la propriété numberoflostpackets.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setNUMBEROFLOSTPACKETS(BigInteger value) {
            this.numberoflostpackets = value;
        }

        /**
         * Obtient la valeur de la propriété numberoftoodegradedpackets.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getNUMBEROFTOODEGRADEDPACKETS() {
            return numberoftoodegradedpackets;
        }

        /**
         * Définit la valeur de la propriété numberoftoodegradedpackets.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setNUMBEROFTOODEGRADEDPACKETS(BigInteger value) {
            this.numberoftoodegradedpackets = value;
        }

        /**
         * Obtient la valeur de la propriété numberofkeptdegradedpackets.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getNUMBEROFKEPTDEGRADEDPACKETS() {
            return numberofkeptdegradedpackets;
        }

        /**
         * Définit la valeur de la propriété numberofkeptdegradedpackets.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setNUMBEROFKEPTDEGRADEDPACKETS(BigInteger value) {
            this.numberofkeptdegradedpackets = value;
        }

        /**
         * Obtient la valeur de la propriété degradationPercentage.
         * 
         * @return
         *     possible object is
         *     {@link Float }
         *     
         */
        public Float getDegradationPercentage() {
            return degradationPercentage;
        }

        /**
         * Définit la valeur de la propriété degradationPercentage.
         * 
         * @param value
         *     allowed object is
         *     {@link Float }
         *     
         */
        public void setDegradationPercentage(Float value) {
            this.degradationPercentage = value;
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
     *                   &lt;element name="Detector" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="Band_List">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="Band" maxOccurs="unbounded">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;sequence>
     *                                                 &lt;element name="DATA_STRIP_START" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
     *                                                 &lt;element name="SCENE_POSITION">
     *                                                   &lt;simpleType>
     *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                                                       &lt;minInclusive value="1"/>
     *                                                       &lt;maxInclusive value="2304"/>
     *                                                     &lt;/restriction>
     *                                                   &lt;/simpleType>
     *                                                 &lt;/element>
     *                                                 &lt;element name="NB_OF_SOURCE_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
     *                                               &lt;/sequence>
     *                                               &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
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
     *                           &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
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
        "detectorList"
    })
    public static class SourcePacketCountersList {

        @XmlElement(name = "Detector_List", required = true)
        protected ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList detectorList;

        /**
         * Obtient la valeur de la propriété detectorList.
         * 
         * @return
         *     possible object is
         *     {@link ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList }
         *     
         */
        public ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList getDetectorList() {
            return detectorList;
        }

        /**
         * Définit la valeur de la propriété detectorList.
         * 
         * @param value
         *     allowed object is
         *     {@link ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList }
         *     
         */
        public void setDetectorList(ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList value) {
            this.detectorList = value;
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
         *         &lt;element name="Detector" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="Band_List">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="Band" maxOccurs="unbounded">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;sequence>
         *                                       &lt;element name="DATA_STRIP_START" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
         *                                       &lt;element name="SCENE_POSITION">
         *                                         &lt;simpleType>
         *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *                                             &lt;minInclusive value="1"/>
         *                                             &lt;maxInclusive value="2304"/>
         *                                           &lt;/restriction>
         *                                         &lt;/simpleType>
         *                                       &lt;/element>
         *                                       &lt;element name="NB_OF_SOURCE_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
         *                                     &lt;/sequence>
         *                                     &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
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
         *                 &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
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
            protected List<ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector> detector;

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
             * {@link ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector }
             * 
             * 
             */
            public List<ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector> getDetector() {
                if (detector == null) {
                    detector = new ArrayList<ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector>();
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
             *         &lt;element name="Band_List">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="Band" maxOccurs="unbounded">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;sequence>
             *                             &lt;element name="DATA_STRIP_START" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
             *                             &lt;element name="SCENE_POSITION">
             *                               &lt;simpleType>
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
             *                                   &lt;minInclusive value="1"/>
             *                                   &lt;maxInclusive value="2304"/>
             *                                 &lt;/restriction>
             *                               &lt;/simpleType>
             *                             &lt;/element>
             *                             &lt;element name="NB_OF_SOURCE_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
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
                "bandList"
            })
            public static class Detector {

                @XmlElement(name = "Band_List", required = true)
                protected ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector.BandList bandList;
                @XmlAttribute(name = "detectorId", required = true)
                protected String detectorId;

                /**
                 * Obtient la valeur de la propriété bandList.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector.BandList }
                 *     
                 */
                public ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector.BandList getBandList() {
                    return bandList;
                }

                /**
                 * Définit la valeur de la propriété bandList.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector.BandList }
                 *     
                 */
                public void setBandList(ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector.BandList value) {
                    this.bandList = value;
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
                 *         &lt;element name="Band" maxOccurs="unbounded">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;sequence>
                 *                   &lt;element name="DATA_STRIP_START" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
                 *                   &lt;element name="SCENE_POSITION">
                 *                     &lt;simpleType>
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
                 *                         &lt;minInclusive value="1"/>
                 *                         &lt;maxInclusive value="2304"/>
                 *                       &lt;/restriction>
                 *                     &lt;/simpleType>
                 *                   &lt;/element>
                 *                   &lt;element name="NB_OF_SOURCE_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
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
                    "band"
                })
                public static class BandList {

                    @XmlElement(name = "Band", required = true)
                    protected List<ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector.BandList.Band> band;

                    /**
                     * Gets the value of the band property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the band property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getBand().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector.BandList.Band }
                     * 
                     * 
                     */
                    public List<ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector.BandList.Band> getBand() {
                        if (band == null) {
                            band = new ArrayList<ASOURCEPACKETDESCRIPTIONDSL0 .SourcePacketCountersList.DetectorList.Detector.BandList.Band>();
                        }
                        return this.band;
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
                     *         &lt;element name="DATA_STRIP_START" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
                     *         &lt;element name="SCENE_POSITION">
                     *           &lt;simpleType>
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
                     *               &lt;minInclusive value="1"/>
                     *               &lt;maxInclusive value="2304"/>
                     *             &lt;/restriction>
                     *           &lt;/simpleType>
                     *         &lt;/element>
                     *         &lt;element name="NB_OF_SOURCE_PACKETS" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
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
                        "datastripstart",
                        "sceneposition",
                        "nbofsourcepackets"
                    })
                    public static class Band {

                        @XmlElement(name = "DATA_STRIP_START", required = true)
                        @XmlSchemaType(name = "nonNegativeInteger")
                        protected BigInteger datastripstart;
                        @XmlElement(name = "SCENE_POSITION")
                        protected int sceneposition;
                        @XmlElement(name = "NB_OF_SOURCE_PACKETS", required = true)
                        @XmlSchemaType(name = "nonNegativeInteger")
                        protected BigInteger nbofsourcepackets;
                        @XmlAttribute(name = "bandId", required = true)
                        protected String bandId;

                        /**
                         * Obtient la valeur de la propriété datastripstart.
                         * 
                         * @return
                         *     possible object is
                         *     {@link BigInteger }
                         *     
                         */
                        public BigInteger getDATASTRIPSTART() {
                            return datastripstart;
                        }

                        /**
                         * Définit la valeur de la propriété datastripstart.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link BigInteger }
                         *     
                         */
                        public void setDATASTRIPSTART(BigInteger value) {
                            this.datastripstart = value;
                        }

                        /**
                         * Obtient la valeur de la propriété sceneposition.
                         * 
                         */
                        public int getSCENEPOSITION() {
                            return sceneposition;
                        }

                        /**
                         * Définit la valeur de la propriété sceneposition.
                         * 
                         */
                        public void setSCENEPOSITION(int value) {
                            this.sceneposition = value;
                        }

                        /**
                         * Obtient la valeur de la propriété nbofsourcepackets.
                         * 
                         * @return
                         *     possible object is
                         *     {@link BigInteger }
                         *     
                         */
                        public BigInteger getNBOFSOURCEPACKETS() {
                            return nbofsourcepackets;
                        }

                        /**
                         * Définit la valeur de la propriété nbofsourcepackets.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link BigInteger }
                         *     
                         */
                        public void setNBOFSOURCEPACKETS(BigInteger value) {
                            this.nbofsourcepackets = value;
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

            }

        }

    }

}
