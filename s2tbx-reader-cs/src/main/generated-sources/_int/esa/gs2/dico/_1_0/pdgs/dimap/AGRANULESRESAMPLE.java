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
import _int.esa.gs2.dico._1_0.sy.tile.ATILEDESCRIPTIONDIMAP;


/**
 * <p>Classe Java pour A_GRANULES_RESAMPLE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GRANULES_RESAMPLE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Tile" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Tile_Description" type="{http://gs2.esa.int/DICO/1.0/SY/tile/}A_TILE_DESCRIPTION_DIMAP"/>
 *                   &lt;element name="Mean_Sun_Angle" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
 *                   &lt;element name="Sun_Angles_Grid" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SUN_INCIDENCE_ANGLE_GRID"/>
 *                   &lt;element name="Mask_List">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="MASK_FILENAME" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;simpleContent>
 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                     &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/extension>
 *                                 &lt;/simpleContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Quality_Assessment" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="DEGRADED_ANC_DATA_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
 *                             &lt;element name="DEGRADED_MSI_DATA_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
 *                             &lt;element name="CLOUDY_PIXEL_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_CLOUDY_PIXEL_PERCENTAGE" minOccurs="0"/>
 *                             &lt;element name="CIRRUS_CLOUD_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Mean_Viewing_Incidence_Angle_List">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Mean_Viewing_Incidence_Angle" maxOccurs="13" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES">
 *                                     &lt;attribute name="bandId" use="required">
 *                                       &lt;simpleType>
 *                                         &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER">
 *                                           &lt;enumeration value="0"/>
 *                                           &lt;enumeration value="1"/>
 *                                           &lt;enumeration value="2"/>
 *                                           &lt;enumeration value="3"/>
 *                                           &lt;enumeration value="4"/>
 *                                           &lt;enumeration value="5"/>
 *                                           &lt;enumeration value="6"/>
 *                                           &lt;enumeration value="7"/>
 *                                           &lt;enumeration value="8"/>
 *                                           &lt;enumeration value="9"/>
 *                                           &lt;enumeration value="10"/>
 *                                           &lt;enumeration value="11"/>
 *                                           &lt;enumeration value="12"/>
 *                                         &lt;/restriction>
 *                                       &lt;/simpleType>
 *                                     &lt;/attribute>
 *                                   &lt;/extension>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Viewing_Incidence_Angles_Grids" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_INCIDENCE_ANGLE_GRID" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="tileId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/tile/}A_TILE_IDENTIFIER" />
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
@XmlType(name = "A_GRANULES_RESAMPLE", propOrder = {
    "tile"
})
public class AGRANULESRESAMPLE {

    @XmlElement(name = "Tile", required = true)
    protected List<AGRANULESRESAMPLE.Tile> tile;

    /**
     * Gets the value of the tile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AGRANULESRESAMPLE.Tile }
     * 
     * 
     */
    public List<AGRANULESRESAMPLE.Tile> getTile() {
        if (tile == null) {
            tile = new ArrayList<AGRANULESRESAMPLE.Tile>();
        }
        return this.tile;
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
     *         &lt;element name="Tile_Description" type="{http://gs2.esa.int/DICO/1.0/SY/tile/}A_TILE_DESCRIPTION_DIMAP"/>
     *         &lt;element name="Mean_Sun_Angle" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
     *         &lt;element name="Sun_Angles_Grid" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SUN_INCIDENCE_ANGLE_GRID"/>
     *         &lt;element name="Mask_List">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="MASK_FILENAME" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;simpleContent>
     *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                           &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/extension>
     *                       &lt;/simpleContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Quality_Assessment" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="DEGRADED_ANC_DATA_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
     *                   &lt;element name="DEGRADED_MSI_DATA_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
     *                   &lt;element name="CLOUDY_PIXEL_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_CLOUDY_PIXEL_PERCENTAGE" minOccurs="0"/>
     *                   &lt;element name="CIRRUS_CLOUD_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Mean_Viewing_Incidence_Angle_List">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Mean_Viewing_Incidence_Angle" maxOccurs="13" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES">
     *                           &lt;attribute name="bandId" use="required">
     *                             &lt;simpleType>
     *                               &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER">
     *                                 &lt;enumeration value="0"/>
     *                                 &lt;enumeration value="1"/>
     *                                 &lt;enumeration value="2"/>
     *                                 &lt;enumeration value="3"/>
     *                                 &lt;enumeration value="4"/>
     *                                 &lt;enumeration value="5"/>
     *                                 &lt;enumeration value="6"/>
     *                                 &lt;enumeration value="7"/>
     *                                 &lt;enumeration value="8"/>
     *                                 &lt;enumeration value="9"/>
     *                                 &lt;enumeration value="10"/>
     *                                 &lt;enumeration value="11"/>
     *                                 &lt;enumeration value="12"/>
     *                               &lt;/restriction>
     *                             &lt;/simpleType>
     *                           &lt;/attribute>
     *                         &lt;/extension>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Viewing_Incidence_Angles_Grids" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_INCIDENCE_ANGLE_GRID" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *       &lt;attribute name="tileId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/tile/}A_TILE_IDENTIFIER" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "tileDescription",
        "meanSunAngle",
        "sunAnglesGrid",
        "maskList",
        "qualityAssessment",
        "meanViewingIncidenceAngleList",
        "viewingIncidenceAnglesGrids"
    })
    public static class Tile {

        @XmlElement(name = "Tile_Description", required = true)
        protected ATILEDESCRIPTIONDIMAP tileDescription;
        @XmlElement(name = "Mean_Sun_Angle", required = true)
        protected AZENITHANDAZIMUTHANGLES meanSunAngle;
        @XmlElement(name = "Sun_Angles_Grid", required = true)
        protected ASUNINCIDENCEANGLEGRID sunAnglesGrid;
        @XmlElement(name = "Mask_List", required = true)
        protected AGRANULESRESAMPLE.Tile.MaskList maskList;
        @XmlElement(name = "Quality_Assessment")
        protected AGRANULESRESAMPLE.Tile.QualityAssessment qualityAssessment;
        @XmlElement(name = "Mean_Viewing_Incidence_Angle_List", required = true)
        protected AGRANULESRESAMPLE.Tile.MeanViewingIncidenceAngleList meanViewingIncidenceAngleList;
        @XmlElement(name = "Viewing_Incidence_Angles_Grids", required = true)
        protected List<ANINCIDENCEANGLEGRID> viewingIncidenceAnglesGrids;
        @XmlAttribute(name = "tileId", required = true)
        protected String tileId;

        /**
         * Obtient la valeur de la propriété tileDescription.
         * 
         * @return
         *     possible object is
         *     {@link ATILEDESCRIPTIONDIMAP }
         *     
         */
        public ATILEDESCRIPTIONDIMAP getTileDescription() {
            return tileDescription;
        }

        /**
         * Définit la valeur de la propriété tileDescription.
         * 
         * @param value
         *     allowed object is
         *     {@link ATILEDESCRIPTIONDIMAP }
         *     
         */
        public void setTileDescription(ATILEDESCRIPTIONDIMAP value) {
            this.tileDescription = value;
        }

        /**
         * Obtient la valeur de la propriété meanSunAngle.
         * 
         * @return
         *     possible object is
         *     {@link AZENITHANDAZIMUTHANGLES }
         *     
         */
        public AZENITHANDAZIMUTHANGLES getMeanSunAngle() {
            return meanSunAngle;
        }

        /**
         * Définit la valeur de la propriété meanSunAngle.
         * 
         * @param value
         *     allowed object is
         *     {@link AZENITHANDAZIMUTHANGLES }
         *     
         */
        public void setMeanSunAngle(AZENITHANDAZIMUTHANGLES value) {
            this.meanSunAngle = value;
        }

        /**
         * Obtient la valeur de la propriété sunAnglesGrid.
         * 
         * @return
         *     possible object is
         *     {@link ASUNINCIDENCEANGLEGRID }
         *     
         */
        public ASUNINCIDENCEANGLEGRID getSunAnglesGrid() {
            return sunAnglesGrid;
        }

        /**
         * Définit la valeur de la propriété sunAnglesGrid.
         * 
         * @param value
         *     allowed object is
         *     {@link ASUNINCIDENCEANGLEGRID }
         *     
         */
        public void setSunAnglesGrid(ASUNINCIDENCEANGLEGRID value) {
            this.sunAnglesGrid = value;
        }

        /**
         * Obtient la valeur de la propriété maskList.
         * 
         * @return
         *     possible object is
         *     {@link AGRANULESRESAMPLE.Tile.MaskList }
         *     
         */
        public AGRANULESRESAMPLE.Tile.MaskList getMaskList() {
            return maskList;
        }

        /**
         * Définit la valeur de la propriété maskList.
         * 
         * @param value
         *     allowed object is
         *     {@link AGRANULESRESAMPLE.Tile.MaskList }
         *     
         */
        public void setMaskList(AGRANULESRESAMPLE.Tile.MaskList value) {
            this.maskList = value;
        }

        /**
         * Obtient la valeur de la propriété qualityAssessment.
         * 
         * @return
         *     possible object is
         *     {@link AGRANULESRESAMPLE.Tile.QualityAssessment }
         *     
         */
        public AGRANULESRESAMPLE.Tile.QualityAssessment getQualityAssessment() {
            return qualityAssessment;
        }

        /**
         * Définit la valeur de la propriété qualityAssessment.
         * 
         * @param value
         *     allowed object is
         *     {@link AGRANULESRESAMPLE.Tile.QualityAssessment }
         *     
         */
        public void setQualityAssessment(AGRANULESRESAMPLE.Tile.QualityAssessment value) {
            this.qualityAssessment = value;
        }

        /**
         * Obtient la valeur de la propriété meanViewingIncidenceAngleList.
         * 
         * @return
         *     possible object is
         *     {@link AGRANULESRESAMPLE.Tile.MeanViewingIncidenceAngleList }
         *     
         */
        public AGRANULESRESAMPLE.Tile.MeanViewingIncidenceAngleList getMeanViewingIncidenceAngleList() {
            return meanViewingIncidenceAngleList;
        }

        /**
         * Définit la valeur de la propriété meanViewingIncidenceAngleList.
         * 
         * @param value
         *     allowed object is
         *     {@link AGRANULESRESAMPLE.Tile.MeanViewingIncidenceAngleList }
         *     
         */
        public void setMeanViewingIncidenceAngleList(AGRANULESRESAMPLE.Tile.MeanViewingIncidenceAngleList value) {
            this.meanViewingIncidenceAngleList = value;
        }

        /**
         * Gets the value of the viewingIncidenceAnglesGrids property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the viewingIncidenceAnglesGrids property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getViewingIncidenceAnglesGrids().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ANINCIDENCEANGLEGRID }
         * 
         * 
         */
        public List<ANINCIDENCEANGLEGRID> getViewingIncidenceAnglesGrids() {
            if (viewingIncidenceAnglesGrids == null) {
                viewingIncidenceAnglesGrids = new ArrayList<ANINCIDENCEANGLEGRID>();
            }
            return this.viewingIncidenceAnglesGrids;
        }

        /**
         * Obtient la valeur de la propriété tileId.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTileId() {
            return tileId;
        }

        /**
         * Définit la valeur de la propriété tileId.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTileId(String value) {
            this.tileId = value;
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
         *         &lt;element name="MASK_FILENAME" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;simpleContent>
         *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *                 &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
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
            "maskfilename"
        })
        public static class MaskList {

            @XmlElement(name = "MASK_FILENAME", required = true)
            protected List<AGRANULESRESAMPLE.Tile.MaskList.MASKFILENAME> maskfilename;

            /**
             * Gets the value of the maskfilename property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the maskfilename property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getMASKFILENAME().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link AGRANULESRESAMPLE.Tile.MaskList.MASKFILENAME }
             * 
             * 
             */
            public List<AGRANULESRESAMPLE.Tile.MaskList.MASKFILENAME> getMASKFILENAME() {
                if (maskfilename == null) {
                    maskfilename = new ArrayList<AGRANULESRESAMPLE.Tile.MaskList.MASKFILENAME>();
                }
                return this.maskfilename;
            }


            /**
             * <p>Classe Java pour anonymous complex type.
             * 
             * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;simpleContent>
             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
             *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
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
            public static class MASKFILENAME {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "type", required = true)
                protected String type;

                /**
                 * Obtient la valeur de la propriété value.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getValue() {
                    return value;
                }

                /**
                 * Définit la valeur de la propriété value.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setValue(String value) {
                    this.value = value;
                }

                /**
                 * Obtient la valeur de la propriété type.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getType() {
                    return type;
                }

                /**
                 * Définit la valeur de la propriété type.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setType(String value) {
                    this.type = value;
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
         *         &lt;element name="Mean_Viewing_Incidence_Angle" maxOccurs="13" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES">
         *                 &lt;attribute name="bandId" use="required">
         *                   &lt;simpleType>
         *                     &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER">
         *                       &lt;enumeration value="0"/>
         *                       &lt;enumeration value="1"/>
         *                       &lt;enumeration value="2"/>
         *                       &lt;enumeration value="3"/>
         *                       &lt;enumeration value="4"/>
         *                       &lt;enumeration value="5"/>
         *                       &lt;enumeration value="6"/>
         *                       &lt;enumeration value="7"/>
         *                       &lt;enumeration value="8"/>
         *                       &lt;enumeration value="9"/>
         *                       &lt;enumeration value="10"/>
         *                       &lt;enumeration value="11"/>
         *                       &lt;enumeration value="12"/>
         *                     &lt;/restriction>
         *                   &lt;/simpleType>
         *                 &lt;/attribute>
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
        @XmlType(name = "", propOrder = {
            "meanViewingIncidenceAngle"
        })
        public static class MeanViewingIncidenceAngleList {

            @XmlElement(name = "Mean_Viewing_Incidence_Angle")
            protected List<AGRANULESRESAMPLE.Tile.MeanViewingIncidenceAngleList.MeanViewingIncidenceAngle> meanViewingIncidenceAngle;

            /**
             * Gets the value of the meanViewingIncidenceAngle property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the meanViewingIncidenceAngle property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getMeanViewingIncidenceAngle().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link AGRANULESRESAMPLE.Tile.MeanViewingIncidenceAngleList.MeanViewingIncidenceAngle }
             * 
             * 
             */
            public List<AGRANULESRESAMPLE.Tile.MeanViewingIncidenceAngleList.MeanViewingIncidenceAngle> getMeanViewingIncidenceAngle() {
                if (meanViewingIncidenceAngle == null) {
                    meanViewingIncidenceAngle = new ArrayList<AGRANULESRESAMPLE.Tile.MeanViewingIncidenceAngleList.MeanViewingIncidenceAngle>();
                }
                return this.meanViewingIncidenceAngle;
            }


            /**
             * <p>Classe Java pour anonymous complex type.
             * 
             * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES">
             *       &lt;attribute name="bandId" use="required">
             *         &lt;simpleType>
             *           &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER">
             *             &lt;enumeration value="0"/>
             *             &lt;enumeration value="1"/>
             *             &lt;enumeration value="2"/>
             *             &lt;enumeration value="3"/>
             *             &lt;enumeration value="4"/>
             *             &lt;enumeration value="5"/>
             *             &lt;enumeration value="6"/>
             *             &lt;enumeration value="7"/>
             *             &lt;enumeration value="8"/>
             *             &lt;enumeration value="9"/>
             *             &lt;enumeration value="10"/>
             *             &lt;enumeration value="11"/>
             *             &lt;enumeration value="12"/>
             *           &lt;/restriction>
             *         &lt;/simpleType>
             *       &lt;/attribute>
             *     &lt;/extension>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class MeanViewingIncidenceAngle
                extends AZENITHANDAZIMUTHANGLES
            {

                @XmlAttribute(name = "bandId", required = true)
                protected String bandId;

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
         *         &lt;element name="DEGRADED_ANC_DATA_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
         *         &lt;element name="DEGRADED_MSI_DATA_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
         *         &lt;element name="CLOUDY_PIXEL_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_CLOUDY_PIXEL_PERCENTAGE" minOccurs="0"/>
         *         &lt;element name="CIRRUS_CLOUD_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE" minOccurs="0"/>
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
            "degradedmsidatapercentage",
            "cloudypixelpercentage",
            "cirruscloudpercentage"
        })
        public static class QualityAssessment {

            @XmlElement(name = "DEGRADED_ANC_DATA_PERCENTAGE")
            protected double degradedancdatapercentage;
            @XmlElement(name = "DEGRADED_MSI_DATA_PERCENTAGE")
            protected double degradedmsidatapercentage;
            @XmlElement(name = "CLOUDY_PIXEL_PERCENTAGE")
            protected Double cloudypixelpercentage;
            @XmlElement(name = "CIRRUS_CLOUD_PERCENTAGE")
            protected Double cirruscloudpercentage;

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
             * Obtient la valeur de la propriété cirruscloudpercentage.
             * 
             * @return
             *     possible object is
             *     {@link Double }
             *     
             */
            public Double getCIRRUSCLOUDPERCENTAGE() {
                return cirruscloudpercentage;
            }

            /**
             * Définit la valeur de la propriété cirruscloudpercentage.
             * 
             * @param value
             *     allowed object is
             *     {@link Double }
             *     
             */
            public void setCIRRUSCLOUDPERCENTAGE(Double value) {
                this.cirruscloudpercentage = value;
            }

        }

    }

}
