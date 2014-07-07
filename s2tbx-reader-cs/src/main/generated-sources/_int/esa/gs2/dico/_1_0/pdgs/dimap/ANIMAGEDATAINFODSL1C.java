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
 * <p>Classe Java pour AN_IMAGE_DATA_INFO_DSL1C complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_IMAGE_DATA_INFO_DSL1C">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Tiles_Information">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Tile_List">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Tile" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="tileId" use="required" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}TILE_ID" />
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
 *         &lt;element name="Radiometric_Info">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_RADIOMETRIC_DATA_L1C">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Geometric_Info">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_DATA_DSL1C">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Product_Compression" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="COMPRESSION">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;enumeration value="NONE"/>
 *                         &lt;enumeration value="LOSSY"/>
 *                         &lt;enumeration value="LOSSLESS"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
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
@XmlType(name = "AN_IMAGE_DATA_INFO_DSL1C", propOrder = {
    "tilesInformation",
    "sensorConfiguration",
    "radiometricInfo",
    "geometricInfo",
    "productCompression"
})
public class ANIMAGEDATAINFODSL1C {

    @XmlElement(name = "Tiles_Information", required = true)
    protected ANIMAGEDATAINFODSL1C.TilesInformation tilesInformation;
    @XmlElement(name = "Sensor_Configuration", required = true)
    protected ASENSORCONFIGURATION sensorConfiguration;
    @XmlElement(name = "Radiometric_Info", required = true)
    protected ANIMAGEDATAINFODSL1C.RadiometricInfo radiometricInfo;
    @XmlElement(name = "Geometric_Info", required = true)
    protected ANIMAGEDATAINFODSL1C.GeometricInfo geometricInfo;
    @XmlElement(name = "Product_Compression")
    protected ANIMAGEDATAINFODSL1C.ProductCompression productCompression;

    /**
     * Obtient la valeur de la propriété tilesInformation.
     * 
     * @return
     *     possible object is
     *     {@link ANIMAGEDATAINFODSL1C.TilesInformation }
     *     
     */
    public ANIMAGEDATAINFODSL1C.TilesInformation getTilesInformation() {
        return tilesInformation;
    }

    /**
     * Définit la valeur de la propriété tilesInformation.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIMAGEDATAINFODSL1C.TilesInformation }
     *     
     */
    public void setTilesInformation(ANIMAGEDATAINFODSL1C.TilesInformation value) {
        this.tilesInformation = value;
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
     * Obtient la valeur de la propriété radiometricInfo.
     * 
     * @return
     *     possible object is
     *     {@link ANIMAGEDATAINFODSL1C.RadiometricInfo }
     *     
     */
    public ANIMAGEDATAINFODSL1C.RadiometricInfo getRadiometricInfo() {
        return radiometricInfo;
    }

    /**
     * Définit la valeur de la propriété radiometricInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIMAGEDATAINFODSL1C.RadiometricInfo }
     *     
     */
    public void setRadiometricInfo(ANIMAGEDATAINFODSL1C.RadiometricInfo value) {
        this.radiometricInfo = value;
    }

    /**
     * Obtient la valeur de la propriété geometricInfo.
     * 
     * @return
     *     possible object is
     *     {@link ANIMAGEDATAINFODSL1C.GeometricInfo }
     *     
     */
    public ANIMAGEDATAINFODSL1C.GeometricInfo getGeometricInfo() {
        return geometricInfo;
    }

    /**
     * Définit la valeur de la propriété geometricInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIMAGEDATAINFODSL1C.GeometricInfo }
     *     
     */
    public void setGeometricInfo(ANIMAGEDATAINFODSL1C.GeometricInfo value) {
        this.geometricInfo = value;
    }

    /**
     * Obtient la valeur de la propriété productCompression.
     * 
     * @return
     *     possible object is
     *     {@link ANIMAGEDATAINFODSL1C.ProductCompression }
     *     
     */
    public ANIMAGEDATAINFODSL1C.ProductCompression getProductCompression() {
        return productCompression;
    }

    /**
     * Définit la valeur de la propriété productCompression.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIMAGEDATAINFODSL1C.ProductCompression }
     *     
     */
    public void setProductCompression(ANIMAGEDATAINFODSL1C.ProductCompression value) {
        this.productCompression = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_DATA_DSL1C">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GeometricInfo
        extends AGEOMETRICDATADSL1C
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
     *         &lt;element name="COMPRESSION">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;enumeration value="NONE"/>
     *               &lt;enumeration value="LOSSY"/>
     *               &lt;enumeration value="LOSSLESS"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
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
        "compression"
    })
    public static class ProductCompression {

        @XmlElement(name = "COMPRESSION", required = true)
        protected String compression;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Obtient la valeur de la propriété compression.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCOMPRESSION() {
            return compression;
        }

        /**
         * Définit la valeur de la propriété compression.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCOMPRESSION(String value) {
            this.compression = value;
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

    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_RADIOMETRIC_DATA_L1C">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class RadiometricInfo
        extends ARADIOMETRICDATAL1C
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
     *         &lt;element name="Tile_List">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Tile" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="tileId" use="required" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}TILE_ID" />
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
        "tileList"
    })
    public static class TilesInformation {

        @XmlElement(name = "Tile_List", required = true)
        protected ANIMAGEDATAINFODSL1C.TilesInformation.TileList tileList;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Obtient la valeur de la propriété tileList.
         * 
         * @return
         *     possible object is
         *     {@link ANIMAGEDATAINFODSL1C.TilesInformation.TileList }
         *     
         */
        public ANIMAGEDATAINFODSL1C.TilesInformation.TileList getTileList() {
            return tileList;
        }

        /**
         * Définit la valeur de la propriété tileList.
         * 
         * @param value
         *     allowed object is
         *     {@link ANIMAGEDATAINFODSL1C.TilesInformation.TileList }
         *     
         */
        public void setTileList(ANIMAGEDATAINFODSL1C.TilesInformation.TileList value) {
            this.tileList = value;
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
         *         &lt;element name="Tile" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="tileId" use="required" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}TILE_ID" />
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
            "tile"
        })
        public static class TileList {

            @XmlElement(name = "Tile", required = true)
            protected List<ANIMAGEDATAINFODSL1C.TilesInformation.TileList.Tile> tile;

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
             * {@link ANIMAGEDATAINFODSL1C.TilesInformation.TileList.Tile }
             * 
             * 
             */
            public List<ANIMAGEDATAINFODSL1C.TilesInformation.TileList.Tile> getTile() {
                if (tile == null) {
                    tile = new ArrayList<ANIMAGEDATAINFODSL1C.TilesInformation.TileList.Tile>();
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
             *       &lt;attribute name="tileId" use="required" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}TILE_ID" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Tile {

                @XmlAttribute(name = "tileId", required = true)
                protected String tileId;

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

            }

        }

    }

}
