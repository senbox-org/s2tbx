//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.user_product_level_1c;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour Level-1C_User_Product_Structure complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Level-1C_User_Product_Structure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Product_Metadata_File">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GRANULE">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Tiles" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Level-1C_Tile_Metadata_File" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                             &lt;element name="IMG_DATA" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                             &lt;element name="QI_DATA" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                             &lt;element name="AUX_DATA">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
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
 *         &lt;element name="DATASTRIP">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="AUX_DATA">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Browse_Image" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="manifest.safe" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="rep_info" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="INSPIRE" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="HTML" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Level-1C_User_Product_Structure", propOrder = {
    "productMetadataFile",
    "granule",
    "datastrip",
    "auxdata",
    "browseImage",
    "manifestSafe",
    "repInfo",
    "inspire",
    "html"
})
public class Level1CUserProductStructure {

    @XmlElement(name = "Product_Metadata_File", required = true)
    protected Level1CUserProductStructure.ProductMetadataFile productMetadataFile;
    @XmlElement(name = "GRANULE", required = true)
    protected Level1CUserProductStructure.GRANULE granule;
    @XmlElement(name = "DATASTRIP", required = true)
    protected Level1CUserProductStructure.DATASTRIP datastrip;
    @XmlElement(name = "AUX_DATA", required = true)
    protected Level1CUserProductStructure.AUXDATA auxdata;
    @XmlElement(name = "Browse_Image")
    protected Object browseImage;
    @XmlElement(name = "manifest.safe")
    protected Object manifestSafe;
    @XmlElement(name = "rep_info")
    protected Object repInfo;
    @XmlElement(name = "INSPIRE", required = true)
    protected Object inspire;
    @XmlElement(name = "HTML", required = true)
    protected Object html;

    /**
     * Obtient la valeur de la propriété productMetadataFile.
     * 
     * @return
     *     possible object is
     *     {@link Level1CUserProductStructure.ProductMetadataFile }
     *     
     */
    public Level1CUserProductStructure.ProductMetadataFile getProductMetadataFile() {
        return productMetadataFile;
    }

    /**
     * Définit la valeur de la propriété productMetadataFile.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1CUserProductStructure.ProductMetadataFile }
     *     
     */
    public void setProductMetadataFile(Level1CUserProductStructure.ProductMetadataFile value) {
        this.productMetadataFile = value;
    }

    /**
     * Obtient la valeur de la propriété granule.
     * 
     * @return
     *     possible object is
     *     {@link Level1CUserProductStructure.GRANULE }
     *     
     */
    public Level1CUserProductStructure.GRANULE getGRANULE() {
        return granule;
    }

    /**
     * Définit la valeur de la propriété granule.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1CUserProductStructure.GRANULE }
     *     
     */
    public void setGRANULE(Level1CUserProductStructure.GRANULE value) {
        this.granule = value;
    }

    /**
     * Obtient la valeur de la propriété datastrip.
     * 
     * @return
     *     possible object is
     *     {@link Level1CUserProductStructure.DATASTRIP }
     *     
     */
    public Level1CUserProductStructure.DATASTRIP getDATASTRIP() {
        return datastrip;
    }

    /**
     * Définit la valeur de la propriété datastrip.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1CUserProductStructure.DATASTRIP }
     *     
     */
    public void setDATASTRIP(Level1CUserProductStructure.DATASTRIP value) {
        this.datastrip = value;
    }

    /**
     * Obtient la valeur de la propriété auxdata.
     * 
     * @return
     *     possible object is
     *     {@link Level1CUserProductStructure.AUXDATA }
     *     
     */
    public Level1CUserProductStructure.AUXDATA getAUXDATA() {
        return auxdata;
    }

    /**
     * Définit la valeur de la propriété auxdata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1CUserProductStructure.AUXDATA }
     *     
     */
    public void setAUXDATA(Level1CUserProductStructure.AUXDATA value) {
        this.auxdata = value;
    }

    /**
     * Obtient la valeur de la propriété browseImage.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getBrowseImage() {
        return browseImage;
    }

    /**
     * Définit la valeur de la propriété browseImage.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setBrowseImage(Object value) {
        this.browseImage = value;
    }

    /**
     * Obtient la valeur de la propriété manifestSafe.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getManifestSafe() {
        return manifestSafe;
    }

    /**
     * Définit la valeur de la propriété manifestSafe.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setManifestSafe(Object value) {
        this.manifestSafe = value;
    }

    /**
     * Obtient la valeur de la propriété repInfo.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getRepInfo() {
        return repInfo;
    }

    /**
     * Définit la valeur de la propriété repInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setRepInfo(Object value) {
        this.repInfo = value;
    }

    /**
     * Obtient la valeur de la propriété inspire.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getINSPIRE() {
        return inspire;
    }

    /**
     * Définit la valeur de la propriété inspire.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setINSPIRE(Object value) {
        this.inspire = value;
    }

    /**
     * Obtient la valeur de la propriété html.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getHTML() {
        return html;
    }

    /**
     * Définit la valeur de la propriété html.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setHTML(Object value) {
        this.html = value;
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
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class AUXDATA {


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
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DATASTRIP {


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
     *         &lt;element name="Tiles" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Level-1C_Tile_Metadata_File" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *                   &lt;element name="IMG_DATA" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *                   &lt;element name="QI_DATA" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *                   &lt;element name="AUX_DATA">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
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
        "tiles"
    })
    public static class GRANULE {

        @XmlElement(name = "Tiles", required = true)
        protected List<Level1CUserProductStructure.GRANULE.Tiles> tiles;

        /**
         * Gets the value of the tiles property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the tiles property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTiles().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Level1CUserProductStructure.GRANULE.Tiles }
         * 
         * 
         */
        public List<Level1CUserProductStructure.GRANULE.Tiles> getTiles() {
            if (tiles == null) {
                tiles = new ArrayList<Level1CUserProductStructure.GRANULE.Tiles>();
            }
            return this.tiles;
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
         *         &lt;element name="Level-1C_Tile_Metadata_File" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
         *         &lt;element name="IMG_DATA" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
         *         &lt;element name="QI_DATA" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
         *         &lt;element name="AUX_DATA">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
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
            "level1CTileMetadataFile",
            "imgdata",
            "qidata",
            "auxdata"
        })
        public static class Tiles {

            @XmlElement(name = "Level-1C_Tile_Metadata_File", required = true)
            protected Object level1CTileMetadataFile;
            @XmlElement(name = "IMG_DATA", required = true)
            protected Object imgdata;
            @XmlElement(name = "QI_DATA", required = true)
            protected Object qidata;
            @XmlElement(name = "AUX_DATA", required = true)
            protected Level1CUserProductStructure.GRANULE.Tiles.AUXDATA auxdata;

            /**
             * Obtient la valeur de la propriété level1CTileMetadataFile.
             * 
             * @return
             *     possible object is
             *     {@link Object }
             *     
             */
            public Object getLevel1CTileMetadataFile() {
                return level1CTileMetadataFile;
            }

            /**
             * Définit la valeur de la propriété level1CTileMetadataFile.
             * 
             * @param value
             *     allowed object is
             *     {@link Object }
             *     
             */
            public void setLevel1CTileMetadataFile(Object value) {
                this.level1CTileMetadataFile = value;
            }

            /**
             * Obtient la valeur de la propriété imgdata.
             * 
             * @return
             *     possible object is
             *     {@link Object }
             *     
             */
            public Object getIMGDATA() {
                return imgdata;
            }

            /**
             * Définit la valeur de la propriété imgdata.
             * 
             * @param value
             *     allowed object is
             *     {@link Object }
             *     
             */
            public void setIMGDATA(Object value) {
                this.imgdata = value;
            }

            /**
             * Obtient la valeur de la propriété qidata.
             * 
             * @return
             *     possible object is
             *     {@link Object }
             *     
             */
            public Object getQIDATA() {
                return qidata;
            }

            /**
             * Définit la valeur de la propriété qidata.
             * 
             * @param value
             *     allowed object is
             *     {@link Object }
             *     
             */
            public void setQIDATA(Object value) {
                this.qidata = value;
            }

            /**
             * Obtient la valeur de la propriété auxdata.
             * 
             * @return
             *     possible object is
             *     {@link Level1CUserProductStructure.GRANULE.Tiles.AUXDATA }
             *     
             */
            public Level1CUserProductStructure.GRANULE.Tiles.AUXDATA getAUXDATA() {
                return auxdata;
            }

            /**
             * Définit la valeur de la propriété auxdata.
             * 
             * @param value
             *     allowed object is
             *     {@link Level1CUserProductStructure.GRANULE.Tiles.AUXDATA }
             *     
             */
            public void setAUXDATA(Level1CUserProductStructure.GRANULE.Tiles.AUXDATA value) {
                this.auxdata = value;
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
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class AUXDATA {


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
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ProductMetadataFile {


    }

}
