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
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHDEGUNITATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHMUNITATTR;


/**
 * <p>Classe Java pour A_GEOMETRIC_HEADER_LIST_EXPERTISE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GEOMETRIC_HEADER_LIST_EXPERTISE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Geometric_Header" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                   &lt;element name="LINE_INDEX">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                         &lt;minInclusive value="0"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="Pointing_Angles">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Satellite_Reference" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_YAW_PITCH_ROLL_ANGLES"/>
 *                             &lt;element name="Image_Reference" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_IMAGE_POINTING_ANGLES" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Located_Geometric_Header" maxOccurs="3" minOccurs="3">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ORIENTATION" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEG_UNIT_ATTR"/>
 *                             &lt;element name="Incidence_Angles" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
 *                             &lt;element name="Solar_Angles" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
 *                             &lt;element name="Pixel_Size">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="ALONG_TRACK" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
 *                                       &lt;element name="ACROSS_TRACK" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="pos" use="required" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SWATH_POSITION" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="geometry" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="FULL_RESOLUTION"/>
 *                       &lt;enumeration value="QL"/>
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
@XmlType(name = "A_GEOMETRIC_HEADER_LIST_EXPERTISE", propOrder = {
    "geometricHeader"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.ANIMAGEDATAINFODSL0 .GeometricHeaderList.class,
    _int.esa.gs2.dico._1_0.pdgs.dimap.ANIMAGEDATAINFODSL1A.GeometricHeaderList.class
})
public class AGEOMETRICHEADERLISTEXPERTISE {

    @XmlElement(name = "Geometric_Header", required = true)
    protected List<AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader> geometricHeader;

    /**
     * Gets the value of the geometricHeader property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the geometricHeader property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGeometricHeader().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader }
     * 
     * 
     */
    public List<AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader> getGeometricHeader() {
        if (geometricHeader == null) {
            geometricHeader = new ArrayList<AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader>();
        }
        return this.geometricHeader;
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
     *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *         &lt;element name="LINE_INDEX">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *               &lt;minInclusive value="0"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="Pointing_Angles">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Satellite_Reference" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_YAW_PITCH_ROLL_ANGLES"/>
     *                   &lt;element name="Image_Reference" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_IMAGE_POINTING_ANGLES" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Located_Geometric_Header" maxOccurs="3" minOccurs="3">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ORIENTATION" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEG_UNIT_ATTR"/>
     *                   &lt;element name="Incidence_Angles" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
     *                   &lt;element name="Solar_Angles" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
     *                   &lt;element name="Pixel_Size">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="ALONG_TRACK" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
     *                             &lt;element name="ACROSS_TRACK" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="pos" use="required" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SWATH_POSITION" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="geometry" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="FULL_RESOLUTION"/>
     *             &lt;enumeration value="QL"/>
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
        "gpstime",
        "lineindex",
        "pointingAngles",
        "locatedGeometricHeader"
    })
    public static class GeometricHeader {

        @XmlElement(name = "GPS_TIME", required = true)
        protected XMLGregorianCalendar gpstime;
        @XmlElement(name = "LINE_INDEX")
        protected int lineindex;
        @XmlElement(name = "Pointing_Angles", required = true)
        protected AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.PointingAngles pointingAngles;
        @XmlElement(name = "Located_Geometric_Header", required = true)
        protected List<AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.LocatedGeometricHeader> locatedGeometricHeader;
        @XmlAttribute(name = "geometry", required = true)
        protected String geometry;

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
         * Obtient la valeur de la propriété lineindex.
         * 
         */
        public int getLINEINDEX() {
            return lineindex;
        }

        /**
         * Définit la valeur de la propriété lineindex.
         * 
         */
        public void setLINEINDEX(int value) {
            this.lineindex = value;
        }

        /**
         * Obtient la valeur de la propriété pointingAngles.
         * 
         * @return
         *     possible object is
         *     {@link AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.PointingAngles }
         *     
         */
        public AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.PointingAngles getPointingAngles() {
            return pointingAngles;
        }

        /**
         * Définit la valeur de la propriété pointingAngles.
         * 
         * @param value
         *     allowed object is
         *     {@link AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.PointingAngles }
         *     
         */
        public void setPointingAngles(AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.PointingAngles value) {
            this.pointingAngles = value;
        }

        /**
         * Gets the value of the locatedGeometricHeader property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the locatedGeometricHeader property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLocatedGeometricHeader().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.LocatedGeometricHeader }
         * 
         * 
         */
        public List<AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.LocatedGeometricHeader> getLocatedGeometricHeader() {
            if (locatedGeometricHeader == null) {
                locatedGeometricHeader = new ArrayList<AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.LocatedGeometricHeader>();
            }
            return this.locatedGeometricHeader;
        }

        /**
         * Obtient la valeur de la propriété geometry.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGeometry() {
            return geometry;
        }

        /**
         * Définit la valeur de la propriété geometry.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGeometry(String value) {
            this.geometry = value;
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
         *         &lt;element name="ORIENTATION" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEG_UNIT_ATTR"/>
         *         &lt;element name="Incidence_Angles" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
         *         &lt;element name="Solar_Angles" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
         *         &lt;element name="Pixel_Size">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="ALONG_TRACK" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
         *                   &lt;element name="ACROSS_TRACK" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="pos" use="required" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SWATH_POSITION" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "orientation",
            "incidenceAngles",
            "solarAngles",
            "pixelSize"
        })
        public static class LocatedGeometricHeader {

            @XmlElement(name = "ORIENTATION", required = true)
            protected ADOUBLEWITHDEGUNITATTR orientation;
            @XmlElement(name = "Incidence_Angles", required = true)
            protected AZENITHANDAZIMUTHANGLES incidenceAngles;
            @XmlElement(name = "Solar_Angles", required = true)
            protected AZENITHANDAZIMUTHANGLES solarAngles;
            @XmlElement(name = "Pixel_Size", required = true)
            protected AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.LocatedGeometricHeader.PixelSize pixelSize;
            @XmlAttribute(name = "pos", required = true)
            protected ASWATHPOSITION pos;

            /**
             * Obtient la valeur de la propriété orientation.
             * 
             * @return
             *     possible object is
             *     {@link ADOUBLEWITHDEGUNITATTR }
             *     
             */
            public ADOUBLEWITHDEGUNITATTR getORIENTATION() {
                return orientation;
            }

            /**
             * Définit la valeur de la propriété orientation.
             * 
             * @param value
             *     allowed object is
             *     {@link ADOUBLEWITHDEGUNITATTR }
             *     
             */
            public void setORIENTATION(ADOUBLEWITHDEGUNITATTR value) {
                this.orientation = value;
            }

            /**
             * Obtient la valeur de la propriété incidenceAngles.
             * 
             * @return
             *     possible object is
             *     {@link AZENITHANDAZIMUTHANGLES }
             *     
             */
            public AZENITHANDAZIMUTHANGLES getIncidenceAngles() {
                return incidenceAngles;
            }

            /**
             * Définit la valeur de la propriété incidenceAngles.
             * 
             * @param value
             *     allowed object is
             *     {@link AZENITHANDAZIMUTHANGLES }
             *     
             */
            public void setIncidenceAngles(AZENITHANDAZIMUTHANGLES value) {
                this.incidenceAngles = value;
            }

            /**
             * Obtient la valeur de la propriété solarAngles.
             * 
             * @return
             *     possible object is
             *     {@link AZENITHANDAZIMUTHANGLES }
             *     
             */
            public AZENITHANDAZIMUTHANGLES getSolarAngles() {
                return solarAngles;
            }

            /**
             * Définit la valeur de la propriété solarAngles.
             * 
             * @param value
             *     allowed object is
             *     {@link AZENITHANDAZIMUTHANGLES }
             *     
             */
            public void setSolarAngles(AZENITHANDAZIMUTHANGLES value) {
                this.solarAngles = value;
            }

            /**
             * Obtient la valeur de la propriété pixelSize.
             * 
             * @return
             *     possible object is
             *     {@link AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.LocatedGeometricHeader.PixelSize }
             *     
             */
            public AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.LocatedGeometricHeader.PixelSize getPixelSize() {
                return pixelSize;
            }

            /**
             * Définit la valeur de la propriété pixelSize.
             * 
             * @param value
             *     allowed object is
             *     {@link AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.LocatedGeometricHeader.PixelSize }
             *     
             */
            public void setPixelSize(AGEOMETRICHEADERLISTEXPERTISE.GeometricHeader.LocatedGeometricHeader.PixelSize value) {
                this.pixelSize = value;
            }

            /**
             * Obtient la valeur de la propriété pos.
             * 
             * @return
             *     possible object is
             *     {@link ASWATHPOSITION }
             *     
             */
            public ASWATHPOSITION getPos() {
                return pos;
            }

            /**
             * Définit la valeur de la propriété pos.
             * 
             * @param value
             *     allowed object is
             *     {@link ASWATHPOSITION }
             *     
             */
            public void setPos(ASWATHPOSITION value) {
                this.pos = value;
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
             *         &lt;element name="ALONG_TRACK" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
             *         &lt;element name="ACROSS_TRACK" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_M_UNIT_ATTR"/>
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
                "alongtrack",
                "acrosstrack"
            })
            public static class PixelSize {

                @XmlElement(name = "ALONG_TRACK", required = true)
                protected ADOUBLEWITHMUNITATTR alongtrack;
                @XmlElement(name = "ACROSS_TRACK", required = true)
                protected ADOUBLEWITHMUNITATTR acrosstrack;

                /**
                 * Obtient la valeur de la propriété alongtrack.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ADOUBLEWITHMUNITATTR }
                 *     
                 */
                public ADOUBLEWITHMUNITATTR getALONGTRACK() {
                    return alongtrack;
                }

                /**
                 * Définit la valeur de la propriété alongtrack.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ADOUBLEWITHMUNITATTR }
                 *     
                 */
                public void setALONGTRACK(ADOUBLEWITHMUNITATTR value) {
                    this.alongtrack = value;
                }

                /**
                 * Obtient la valeur de la propriété acrosstrack.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ADOUBLEWITHMUNITATTR }
                 *     
                 */
                public ADOUBLEWITHMUNITATTR getACROSSTRACK() {
                    return acrosstrack;
                }

                /**
                 * Définit la valeur de la propriété acrosstrack.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ADOUBLEWITHMUNITATTR }
                 *     
                 */
                public void setACROSSTRACK(ADOUBLEWITHMUNITATTR value) {
                    this.acrosstrack = value;
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
         *         &lt;element name="Satellite_Reference" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_YAW_PITCH_ROLL_ANGLES"/>
         *         &lt;element name="Image_Reference" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_IMAGE_POINTING_ANGLES" minOccurs="0"/>
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
            "satelliteReference",
            "imageReference"
        })
        public static class PointingAngles {

            @XmlElement(name = "Satellite_Reference", required = true)
            protected AYAWPITCHROLLANGLES satelliteReference;
            @XmlElement(name = "Image_Reference")
            protected ANIMAGEPOINTINGANGLES imageReference;

            /**
             * Obtient la valeur de la propriété satelliteReference.
             * 
             * @return
             *     possible object is
             *     {@link AYAWPITCHROLLANGLES }
             *     
             */
            public AYAWPITCHROLLANGLES getSatelliteReference() {
                return satelliteReference;
            }

            /**
             * Définit la valeur de la propriété satelliteReference.
             * 
             * @param value
             *     allowed object is
             *     {@link AYAWPITCHROLLANGLES }
             *     
             */
            public void setSatelliteReference(AYAWPITCHROLLANGLES value) {
                this.satelliteReference = value;
            }

            /**
             * Obtient la valeur de la propriété imageReference.
             * 
             * @return
             *     possible object is
             *     {@link ANIMAGEPOINTINGANGLES }
             *     
             */
            public ANIMAGEPOINTINGANGLES getImageReference() {
                return imageReference;
            }

            /**
             * Définit la valeur de la propriété imageReference.
             * 
             * @param value
             *     allowed object is
             *     {@link ANIMAGEPOINTINGANGLES }
             *     
             */
            public void setImageReference(ANIMAGEPOINTINGANGLES value) {
                this.imageReference = value;
            }

        }

    }

}
