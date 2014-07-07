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
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.image.ANIMAGEGEOMETRY;


/**
 * <p>Classe Java pour A_GRANULES_DATATION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GRANULES_DATATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GEOMETRY" type="{http://gs2.esa.int/DICO/1.0/SY/image/}AN_IMAGE_GEOMETRY"/>
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
 *                                                 &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_NANOSECOND_DATE_TIME"/>
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
@XmlType(name = "A_GRANULES_DATATION", propOrder = {
    "geometry",
    "detectorList"
})
public class AGRANULESDATATION {

    @XmlElement(name = "GEOMETRY", required = true)
    protected ANIMAGEGEOMETRY geometry;
    @XmlElement(name = "Detector_List", required = true)
    protected AGRANULESDATATION.DetectorList detectorList;

    /**
     * Obtient la valeur de la propriété geometry.
     * 
     * @return
     *     possible object is
     *     {@link ANIMAGEGEOMETRY }
     *     
     */
    public ANIMAGEGEOMETRY getGEOMETRY() {
        return geometry;
    }

    /**
     * Définit la valeur de la propriété geometry.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIMAGEGEOMETRY }
     *     
     */
    public void setGEOMETRY(ANIMAGEGEOMETRY value) {
        this.geometry = value;
    }

    /**
     * Obtient la valeur de la propriété detectorList.
     * 
     * @return
     *     possible object is
     *     {@link AGRANULESDATATION.DetectorList }
     *     
     */
    public AGRANULESDATATION.DetectorList getDetectorList() {
        return detectorList;
    }

    /**
     * Définit la valeur de la propriété detectorList.
     * 
     * @param value
     *     allowed object is
     *     {@link AGRANULESDATATION.DetectorList }
     *     
     */
    public void setDetectorList(AGRANULESDATATION.DetectorList value) {
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
     *                                       &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_NANOSECOND_DATE_TIME"/>
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
        protected List<AGRANULESDATATION.DetectorList.Detector> detector;

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
         * {@link AGRANULESDATATION.DetectorList.Detector }
         * 
         * 
         */
        public List<AGRANULESDATATION.DetectorList.Detector> getDetector() {
            if (detector == null) {
                detector = new ArrayList<AGRANULESDATATION.DetectorList.Detector>();
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
         *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_NANOSECOND_DATE_TIME"/>
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
            "granuleList"
        })
        public static class Detector {

            @XmlElement(name = "Granule_List", required = true)
            protected AGRANULESDATATION.DetectorList.Detector.GranuleList granuleList;
            @XmlAttribute(name = "detectorId", required = true)
            protected String detectorId;

            /**
             * Obtient la valeur de la propriété granuleList.
             * 
             * @return
             *     possible object is
             *     {@link AGRANULESDATATION.DetectorList.Detector.GranuleList }
             *     
             */
            public AGRANULESDATATION.DetectorList.Detector.GranuleList getGranuleList() {
                return granuleList;
            }

            /**
             * Définit la valeur de la propriété granuleList.
             * 
             * @param value
             *     allowed object is
             *     {@link AGRANULESDATATION.DetectorList.Detector.GranuleList }
             *     
             */
            public void setGranuleList(AGRANULESDATATION.DetectorList.Detector.GranuleList value) {
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
             *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_NANOSECOND_DATE_TIME"/>
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
                "granule"
            })
            public static class GranuleList {

                @XmlElement(name = "Granule", required = true)
                protected List<AGRANULESDATATION.DetectorList.Detector.GranuleList.Granule> granule;

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
                 * {@link AGRANULESDATATION.DetectorList.Detector.GranuleList.Granule }
                 * 
                 * 
                 */
                public List<AGRANULESDATATION.DetectorList.Detector.GranuleList.Granule> getGranule() {
                    if (granule == null) {
                        granule = new ArrayList<AGRANULESDATATION.DetectorList.Detector.GranuleList.Granule>();
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
                 *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_NANOSECOND_DATE_TIME"/>
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
                    "position",
                    "gpstime"
                })
                public static class Granule {

                    @XmlElement(name = "POSITION")
                    protected int position;
                    @XmlElement(name = "GPS_TIME", required = true)
                    protected XMLGregorianCalendar gpstime;

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

                }

            }

        }

    }

}
