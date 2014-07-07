//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_1a_granule_structure;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Folder
 * 
 * <p>Classe Java pour Level-1A_Granule complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Level-1A_Granule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Inventory_Metadata">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Level-1A_Granule_Metadata_File">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="General_Info" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="Geometric_Info">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Quality_Indicators_Info">
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
 *         &lt;element name="IMG_DATA">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Image_Files">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Bands_10m" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="4"/>
 *                             &lt;element name="Bands_20m" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="6"/>
 *                             &lt;element name="Bands_60m" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="3"/>
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
 *         &lt;element name="QI_DATA">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="OLQC_Report" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="Quality_Masks" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="manifest.safe">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="rep_info">
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
@XmlType(name = "Level-1A_Granule", propOrder = {
    "inventoryMetadata",
    "level1AGranuleMetadataFile",
    "imgdata",
    "qidata",
    "manifestSafe",
    "repInfo"
})
public class Level1AGranule {

    @XmlElement(name = "Inventory_Metadata", required = true)
    protected Level1AGranule.InventoryMetadata inventoryMetadata;
    @XmlElement(name = "Level-1A_Granule_Metadata_File", required = true)
    protected Level1AGranule.Level1AGranuleMetadataFile level1AGranuleMetadataFile;
    @XmlElement(name = "IMG_DATA", required = true)
    protected Level1AGranule.IMGDATA imgdata;
    @XmlElement(name = "QI_DATA", required = true)
    protected Level1AGranule.QIDATA qidata;
    @XmlElement(name = "manifest.safe", required = true)
    protected Level1AGranule.ManifestSafe manifestSafe;
    @XmlElement(name = "rep_info", required = true)
    protected Level1AGranule.RepInfo repInfo;

    /**
     * Obtient la valeur de la propriété inventoryMetadata.
     * 
     * @return
     *     possible object is
     *     {@link Level1AGranule.InventoryMetadata }
     *     
     */
    public Level1AGranule.InventoryMetadata getInventoryMetadata() {
        return inventoryMetadata;
    }

    /**
     * Définit la valeur de la propriété inventoryMetadata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1AGranule.InventoryMetadata }
     *     
     */
    public void setInventoryMetadata(Level1AGranule.InventoryMetadata value) {
        this.inventoryMetadata = value;
    }

    /**
     * Obtient la valeur de la propriété level1AGranuleMetadataFile.
     * 
     * @return
     *     possible object is
     *     {@link Level1AGranule.Level1AGranuleMetadataFile }
     *     
     */
    public Level1AGranule.Level1AGranuleMetadataFile getLevel1AGranuleMetadataFile() {
        return level1AGranuleMetadataFile;
    }

    /**
     * Définit la valeur de la propriété level1AGranuleMetadataFile.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1AGranule.Level1AGranuleMetadataFile }
     *     
     */
    public void setLevel1AGranuleMetadataFile(Level1AGranule.Level1AGranuleMetadataFile value) {
        this.level1AGranuleMetadataFile = value;
    }

    /**
     * Obtient la valeur de la propriété imgdata.
     * 
     * @return
     *     possible object is
     *     {@link Level1AGranule.IMGDATA }
     *     
     */
    public Level1AGranule.IMGDATA getIMGDATA() {
        return imgdata;
    }

    /**
     * Définit la valeur de la propriété imgdata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1AGranule.IMGDATA }
     *     
     */
    public void setIMGDATA(Level1AGranule.IMGDATA value) {
        this.imgdata = value;
    }

    /**
     * Obtient la valeur de la propriété qidata.
     * 
     * @return
     *     possible object is
     *     {@link Level1AGranule.QIDATA }
     *     
     */
    public Level1AGranule.QIDATA getQIDATA() {
        return qidata;
    }

    /**
     * Définit la valeur de la propriété qidata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1AGranule.QIDATA }
     *     
     */
    public void setQIDATA(Level1AGranule.QIDATA value) {
        this.qidata = value;
    }

    /**
     * Obtient la valeur de la propriété manifestSafe.
     * 
     * @return
     *     possible object is
     *     {@link Level1AGranule.ManifestSafe }
     *     
     */
    public Level1AGranule.ManifestSafe getManifestSafe() {
        return manifestSafe;
    }

    /**
     * Définit la valeur de la propriété manifestSafe.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1AGranule.ManifestSafe }
     *     
     */
    public void setManifestSafe(Level1AGranule.ManifestSafe value) {
        this.manifestSafe = value;
    }

    /**
     * Obtient la valeur de la propriété repInfo.
     * 
     * @return
     *     possible object is
     *     {@link Level1AGranule.RepInfo }
     *     
     */
    public Level1AGranule.RepInfo getRepInfo() {
        return repInfo;
    }

    /**
     * Définit la valeur de la propriété repInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1AGranule.RepInfo }
     *     
     */
    public void setRepInfo(Level1AGranule.RepInfo value) {
        this.repInfo = value;
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
     *         &lt;element name="Image_Files">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Bands_10m" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="4"/>
     *                   &lt;element name="Bands_20m" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="6"/>
     *                   &lt;element name="Bands_60m" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="3"/>
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
        "imageFiles"
    })
    public static class IMGDATA {

        @XmlElement(name = "Image_Files", required = true)
        protected Level1AGranule.IMGDATA.ImageFiles imageFiles;

        /**
         * Obtient la valeur de la propriété imageFiles.
         * 
         * @return
         *     possible object is
         *     {@link Level1AGranule.IMGDATA.ImageFiles }
         *     
         */
        public Level1AGranule.IMGDATA.ImageFiles getImageFiles() {
            return imageFiles;
        }

        /**
         * Définit la valeur de la propriété imageFiles.
         * 
         * @param value
         *     allowed object is
         *     {@link Level1AGranule.IMGDATA.ImageFiles }
         *     
         */
        public void setImageFiles(Level1AGranule.IMGDATA.ImageFiles value) {
            this.imageFiles = value;
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
         *         &lt;element name="Bands_10m" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="4"/>
         *         &lt;element name="Bands_20m" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="6"/>
         *         &lt;element name="Bands_60m" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="3"/>
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
            "bands10M",
            "bands20M",
            "bands60M"
        })
        public static class ImageFiles {

            @XmlElement(name = "Bands_10m", required = true)
            protected List<Object> bands10M;
            @XmlElement(name = "Bands_20m", required = true)
            protected List<Object> bands20M;
            @XmlElement(name = "Bands_60m", required = true)
            protected List<Object> bands60M;

            /**
             * Gets the value of the bands10M property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the bands10M property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getBands10M().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Object }
             * 
             * 
             */
            public List<Object> getBands10M() {
                if (bands10M == null) {
                    bands10M = new ArrayList<Object>();
                }
                return this.bands10M;
            }

            /**
             * Gets the value of the bands20M property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the bands20M property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getBands20M().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Object }
             * 
             * 
             */
            public List<Object> getBands20M() {
                if (bands20M == null) {
                    bands20M = new ArrayList<Object>();
                }
                return this.bands20M;
            }

            /**
             * Gets the value of the bands60M property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the bands60M property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getBands60M().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Object }
             * 
             * 
             */
            public List<Object> getBands60M() {
                if (bands60M == null) {
                    bands60M = new ArrayList<Object>();
                }
                return this.bands60M;
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
    public static class InventoryMetadata {


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
     *         &lt;element name="General_Info" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="Geometric_Info">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Quality_Indicators_Info">
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
        "generalInfo",
        "geometricInfo",
        "qualityIndicatorsInfo"
    })
    public static class Level1AGranuleMetadataFile {

        @XmlElement(name = "General_Info", required = true)
        protected Object generalInfo;
        @XmlElement(name = "Geometric_Info", required = true)
        protected Level1AGranule.Level1AGranuleMetadataFile.GeometricInfo geometricInfo;
        @XmlElement(name = "Quality_Indicators_Info", required = true)
        protected Level1AGranule.Level1AGranuleMetadataFile.QualityIndicatorsInfo qualityIndicatorsInfo;

        /**
         * Obtient la valeur de la propriété generalInfo.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getGeneralInfo() {
            return generalInfo;
        }

        /**
         * Définit la valeur de la propriété generalInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setGeneralInfo(Object value) {
            this.generalInfo = value;
        }

        /**
         * Obtient la valeur de la propriété geometricInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level1AGranule.Level1AGranuleMetadataFile.GeometricInfo }
         *     
         */
        public Level1AGranule.Level1AGranuleMetadataFile.GeometricInfo getGeometricInfo() {
            return geometricInfo;
        }

        /**
         * Définit la valeur de la propriété geometricInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level1AGranule.Level1AGranuleMetadataFile.GeometricInfo }
         *     
         */
        public void setGeometricInfo(Level1AGranule.Level1AGranuleMetadataFile.GeometricInfo value) {
            this.geometricInfo = value;
        }

        /**
         * Obtient la valeur de la propriété qualityIndicatorsInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level1AGranule.Level1AGranuleMetadataFile.QualityIndicatorsInfo }
         *     
         */
        public Level1AGranule.Level1AGranuleMetadataFile.QualityIndicatorsInfo getQualityIndicatorsInfo() {
            return qualityIndicatorsInfo;
        }

        /**
         * Définit la valeur de la propriété qualityIndicatorsInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level1AGranule.Level1AGranuleMetadataFile.QualityIndicatorsInfo }
         *     
         */
        public void setQualityIndicatorsInfo(Level1AGranule.Level1AGranuleMetadataFile.QualityIndicatorsInfo value) {
            this.qualityIndicatorsInfo = value;
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
        public static class GeometricInfo {


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
        public static class QualityIndicatorsInfo {


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
    public static class ManifestSafe {


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
     *         &lt;element name="OLQC_Report" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="Quality_Masks" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
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
        "olqcReport",
        "qualityMasks"
    })
    public static class QIDATA {

        @XmlElement(name = "OLQC_Report", required = true)
        protected Object olqcReport;
        @XmlElement(name = "Quality_Masks", required = true)
        protected Object qualityMasks;

        /**
         * Obtient la valeur de la propriété olqcReport.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getOLQCReport() {
            return olqcReport;
        }

        /**
         * Définit la valeur de la propriété olqcReport.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setOLQCReport(Object value) {
            this.olqcReport = value;
        }

        /**
         * Obtient la valeur de la propriété qualityMasks.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getQualityMasks() {
            return qualityMasks;
        }

        /**
         * Définit la valeur de la propriété qualityMasks.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setQualityMasks(Object value) {
            this.qualityMasks = value;
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
    public static class RepInfo {


    }

}
