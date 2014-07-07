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
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.geographical.AGMLPOLYGON2D;
import _int.esa.gs2.dico._1_0.sy.image.ANIMAGESIZE;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHMSUNITATTR;
import _int.esa.gs2.dico._1_0.sy.misc.AROTATIONAROUNDTHREEAXISANDSCALE;
import _int.esa.gs2.dico._1_0.sy.orbital.AVIEWINGDIRECTIONSUPPERCASE;


/**
 * Description of the quicklook
 * 
 * <p>Classe Java pour A_QUICKLOOK_DESCRIPTOR complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_QUICKLOOK_DESCRIPTOR">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Image_Size" type="{http://gs2.esa.int/DICO/1.0/SY/image/}AN_IMAGE_SIZE"/>
 *         &lt;element name="Footprint" type="{http://gs2.esa.int/DICO/1.0/SY/geographical/}A_GML_POLYGON_2D"/>
 *         &lt;element name="Display_Geometric_Model">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Datation_Model">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="L0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                   &lt;minInclusive value="0"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="T0" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                             &lt;element name="TE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MS_UNIT_ATTR"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Viewing_Directions" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}A_VIEWING_DIRECTIONS_UPPER_CASE"/>
 *                   &lt;element name="Connect_Col_List">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="CONNECT_COL" maxOccurs="11" minOccurs="11">
 *                               &lt;complexType>
 *                                 &lt;simpleContent>
 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>int">
 *                                     &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
 *                                   &lt;/extension>
 *                                 &lt;/simpleContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Piloting_To_Msi_Frame" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_ROTATION_AROUND_THREE_AXIS_AND_SCALE"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="REF_QL_IMAGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_QUICKLOOK_DESCRIPTOR", propOrder = {
    "imageSize",
    "footprint",
    "displayGeometricModel",
    "refqlimage"
})
public class AQUICKLOOKDESCRIPTOR {

    @XmlElement(name = "Image_Size", required = true)
    protected ANIMAGESIZE imageSize;
    @XmlElement(name = "Footprint", required = true)
    protected AGMLPOLYGON2D footprint;
    @XmlElement(name = "Display_Geometric_Model", required = true)
    protected AQUICKLOOKDESCRIPTOR.DisplayGeometricModel displayGeometricModel;
    @XmlElement(name = "REF_QL_IMAGE", required = true)
    protected String refqlimage;

    /**
     * Obtient la valeur de la propriété imageSize.
     * 
     * @return
     *     possible object is
     *     {@link ANIMAGESIZE }
     *     
     */
    public ANIMAGESIZE getImageSize() {
        return imageSize;
    }

    /**
     * Définit la valeur de la propriété imageSize.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIMAGESIZE }
     *     
     */
    public void setImageSize(ANIMAGESIZE value) {
        this.imageSize = value;
    }

    /**
     * Obtient la valeur de la propriété footprint.
     * 
     * @return
     *     possible object is
     *     {@link AGMLPOLYGON2D }
     *     
     */
    public AGMLPOLYGON2D getFootprint() {
        return footprint;
    }

    /**
     * Définit la valeur de la propriété footprint.
     * 
     * @param value
     *     allowed object is
     *     {@link AGMLPOLYGON2D }
     *     
     */
    public void setFootprint(AGMLPOLYGON2D value) {
        this.footprint = value;
    }

    /**
     * Obtient la valeur de la propriété displayGeometricModel.
     * 
     * @return
     *     possible object is
     *     {@link AQUICKLOOKDESCRIPTOR.DisplayGeometricModel }
     *     
     */
    public AQUICKLOOKDESCRIPTOR.DisplayGeometricModel getDisplayGeometricModel() {
        return displayGeometricModel;
    }

    /**
     * Définit la valeur de la propriété displayGeometricModel.
     * 
     * @param value
     *     allowed object is
     *     {@link AQUICKLOOKDESCRIPTOR.DisplayGeometricModel }
     *     
     */
    public void setDisplayGeometricModel(AQUICKLOOKDESCRIPTOR.DisplayGeometricModel value) {
        this.displayGeometricModel = value;
    }

    /**
     * Obtient la valeur de la propriété refqlimage.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREFQLIMAGE() {
        return refqlimage;
    }

    /**
     * Définit la valeur de la propriété refqlimage.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREFQLIMAGE(String value) {
        this.refqlimage = value;
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
     *         &lt;element name="Datation_Model">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="L0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                         &lt;minInclusive value="0"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="T0" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                   &lt;element name="TE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MS_UNIT_ATTR"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Viewing_Directions" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}A_VIEWING_DIRECTIONS_UPPER_CASE"/>
     *         &lt;element name="Connect_Col_List">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="CONNECT_COL" maxOccurs="11" minOccurs="11">
     *                     &lt;complexType>
     *                       &lt;simpleContent>
     *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>int">
     *                           &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
     *                         &lt;/extension>
     *                       &lt;/simpleContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Piloting_To_Msi_Frame" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_ROTATION_AROUND_THREE_AXIS_AND_SCALE"/>
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
        "datationModel",
        "viewingDirections",
        "connectColList",
        "pilotingToMsiFrame"
    })
    public static class DisplayGeometricModel {

        @XmlElement(name = "Datation_Model", required = true)
        protected AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.DatationModel datationModel;
        @XmlElement(name = "Viewing_Directions", required = true)
        protected AVIEWINGDIRECTIONSUPPERCASE viewingDirections;
        @XmlElement(name = "Connect_Col_List", required = true)
        protected AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.ConnectColList connectColList;
        @XmlElement(name = "Piloting_To_Msi_Frame", required = true)
        protected AROTATIONAROUNDTHREEAXISANDSCALE pilotingToMsiFrame;

        /**
         * Obtient la valeur de la propriété datationModel.
         * 
         * @return
         *     possible object is
         *     {@link AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.DatationModel }
         *     
         */
        public AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.DatationModel getDatationModel() {
            return datationModel;
        }

        /**
         * Définit la valeur de la propriété datationModel.
         * 
         * @param value
         *     allowed object is
         *     {@link AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.DatationModel }
         *     
         */
        public void setDatationModel(AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.DatationModel value) {
            this.datationModel = value;
        }

        /**
         * Obtient la valeur de la propriété viewingDirections.
         * 
         * @return
         *     possible object is
         *     {@link AVIEWINGDIRECTIONSUPPERCASE }
         *     
         */
        public AVIEWINGDIRECTIONSUPPERCASE getViewingDirections() {
            return viewingDirections;
        }

        /**
         * Définit la valeur de la propriété viewingDirections.
         * 
         * @param value
         *     allowed object is
         *     {@link AVIEWINGDIRECTIONSUPPERCASE }
         *     
         */
        public void setViewingDirections(AVIEWINGDIRECTIONSUPPERCASE value) {
            this.viewingDirections = value;
        }

        /**
         * Obtient la valeur de la propriété connectColList.
         * 
         * @return
         *     possible object is
         *     {@link AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.ConnectColList }
         *     
         */
        public AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.ConnectColList getConnectColList() {
            return connectColList;
        }

        /**
         * Définit la valeur de la propriété connectColList.
         * 
         * @param value
         *     allowed object is
         *     {@link AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.ConnectColList }
         *     
         */
        public void setConnectColList(AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.ConnectColList value) {
            this.connectColList = value;
        }

        /**
         * Obtient la valeur de la propriété pilotingToMsiFrame.
         * 
         * @return
         *     possible object is
         *     {@link AROTATIONAROUNDTHREEAXISANDSCALE }
         *     
         */
        public AROTATIONAROUNDTHREEAXISANDSCALE getPilotingToMsiFrame() {
            return pilotingToMsiFrame;
        }

        /**
         * Définit la valeur de la propriété pilotingToMsiFrame.
         * 
         * @param value
         *     allowed object is
         *     {@link AROTATIONAROUNDTHREEAXISANDSCALE }
         *     
         */
        public void setPilotingToMsiFrame(AROTATIONAROUNDTHREEAXISANDSCALE value) {
            this.pilotingToMsiFrame = value;
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
         *         &lt;element name="CONNECT_COL" maxOccurs="11" minOccurs="11">
         *           &lt;complexType>
         *             &lt;simpleContent>
         *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>int">
         *                 &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
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
            "connectcol"
        })
        public static class ConnectColList {

            @XmlElement(name = "CONNECT_COL", required = true)
            protected List<AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.ConnectColList.CONNECTCOL> connectcol;

            /**
             * Gets the value of the connectcol property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the connectcol property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getCONNECTCOL().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.ConnectColList.CONNECTCOL }
             * 
             * 
             */
            public List<AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.ConnectColList.CONNECTCOL> getCONNECTCOL() {
                if (connectcol == null) {
                    connectcol = new ArrayList<AQUICKLOOKDESCRIPTOR.DisplayGeometricModel.ConnectColList.CONNECTCOL>();
                }
                return this.connectcol;
            }


            /**
             * <p>Classe Java pour anonymous complex type.
             * 
             * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;simpleContent>
             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>int">
             *       &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
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
            public static class CONNECTCOL {

                @XmlValue
                protected int value;
                @XmlAttribute(name = "detectorId", required = true)
                protected String detectorId;

                /**
                 * Obtient la valeur de la propriété value.
                 * 
                 */
                public int getValue() {
                    return value;
                }

                /**
                 * Définit la valeur de la propriété value.
                 * 
                 */
                public void setValue(int value) {
                    this.value = value;
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
         *         &lt;element name="L0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *               &lt;minInclusive value="0"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="T0" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
         *         &lt;element name="TE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MS_UNIT_ATTR"/>
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
            "l0",
            "t0",
            "te"
        })
        public static class DatationModel {

            @XmlElement(name = "L0")
            protected int l0;
            @XmlElement(name = "T0", required = true)
            protected XMLGregorianCalendar t0;
            @XmlElement(name = "TE", required = true)
            protected ADOUBLEWITHMSUNITATTR te;

            /**
             * Obtient la valeur de la propriété l0.
             * 
             */
            public int getL0() {
                return l0;
            }

            /**
             * Définit la valeur de la propriété l0.
             * 
             */
            public void setL0(int value) {
                this.l0 = value;
            }

            /**
             * Obtient la valeur de la propriété t0.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getT0() {
                return t0;
            }

            /**
             * Définit la valeur de la propriété t0.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setT0(XMLGregorianCalendar value) {
                this.t0 = value;
            }

            /**
             * Obtient la valeur de la propriété te.
             * 
             * @return
             *     possible object is
             *     {@link ADOUBLEWITHMSUNITATTR }
             *     
             */
            public ADOUBLEWITHMSUNITATTR getTE() {
                return te;
            }

            /**
             * Définit la valeur de la propriété te.
             * 
             * @param value
             *     allowed object is
             *     {@link ADOUBLEWITHMSUNITATTR }
             *     
             */
            public void setTE(ADOUBLEWITHMSUNITATTR value) {
                this.te = value;
            }

        }

    }

}
