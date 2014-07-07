//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_0_granule_structure;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Folder
 * 
 * <p>Classe Java pour Level-0_Granule complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Level-0_Granule">
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
 *         &lt;element name="Level-0_Granule_Metadata_File">
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
 *                   &lt;element name="ISP_Files" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="13" minOccurs="13"/>
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
@XmlType(name = "Level-0_Granule", propOrder = {
    "inventoryMetadata",
    "level0GranuleMetadataFile",
    "imgdata",
    "qidata",
    "manifestSafe",
    "repInfo"
})
public class Level0Granule {

    @XmlElement(name = "Inventory_Metadata", required = true)
    protected Level0Granule.InventoryMetadata inventoryMetadata;
    @XmlElement(name = "Level-0_Granule_Metadata_File", required = true)
    protected Level0Granule.Level0GranuleMetadataFile level0GranuleMetadataFile;
    @XmlElement(name = "IMG_DATA", required = true)
    protected Level0Granule.IMGDATA imgdata;
    @XmlElement(name = "QI_DATA", required = true)
    protected Level0Granule.QIDATA qidata;
    @XmlElement(name = "manifest.safe", required = true)
    protected Level0Granule.ManifestSafe manifestSafe;
    @XmlElement(name = "rep_info", required = true)
    protected Level0Granule.RepInfo repInfo;

    /**
     * Obtient la valeur de la propriété inventoryMetadata.
     * 
     * @return
     *     possible object is
     *     {@link Level0Granule.InventoryMetadata }
     *     
     */
    public Level0Granule.InventoryMetadata getInventoryMetadata() {
        return inventoryMetadata;
    }

    /**
     * Définit la valeur de la propriété inventoryMetadata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level0Granule.InventoryMetadata }
     *     
     */
    public void setInventoryMetadata(Level0Granule.InventoryMetadata value) {
        this.inventoryMetadata = value;
    }

    /**
     * Obtient la valeur de la propriété level0GranuleMetadataFile.
     * 
     * @return
     *     possible object is
     *     {@link Level0Granule.Level0GranuleMetadataFile }
     *     
     */
    public Level0Granule.Level0GranuleMetadataFile getLevel0GranuleMetadataFile() {
        return level0GranuleMetadataFile;
    }

    /**
     * Définit la valeur de la propriété level0GranuleMetadataFile.
     * 
     * @param value
     *     allowed object is
     *     {@link Level0Granule.Level0GranuleMetadataFile }
     *     
     */
    public void setLevel0GranuleMetadataFile(Level0Granule.Level0GranuleMetadataFile value) {
        this.level0GranuleMetadataFile = value;
    }

    /**
     * Obtient la valeur de la propriété imgdata.
     * 
     * @return
     *     possible object is
     *     {@link Level0Granule.IMGDATA }
     *     
     */
    public Level0Granule.IMGDATA getIMGDATA() {
        return imgdata;
    }

    /**
     * Définit la valeur de la propriété imgdata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level0Granule.IMGDATA }
     *     
     */
    public void setIMGDATA(Level0Granule.IMGDATA value) {
        this.imgdata = value;
    }

    /**
     * Obtient la valeur de la propriété qidata.
     * 
     * @return
     *     possible object is
     *     {@link Level0Granule.QIDATA }
     *     
     */
    public Level0Granule.QIDATA getQIDATA() {
        return qidata;
    }

    /**
     * Définit la valeur de la propriété qidata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level0Granule.QIDATA }
     *     
     */
    public void setQIDATA(Level0Granule.QIDATA value) {
        this.qidata = value;
    }

    /**
     * Obtient la valeur de la propriété manifestSafe.
     * 
     * @return
     *     possible object is
     *     {@link Level0Granule.ManifestSafe }
     *     
     */
    public Level0Granule.ManifestSafe getManifestSafe() {
        return manifestSafe;
    }

    /**
     * Définit la valeur de la propriété manifestSafe.
     * 
     * @param value
     *     allowed object is
     *     {@link Level0Granule.ManifestSafe }
     *     
     */
    public void setManifestSafe(Level0Granule.ManifestSafe value) {
        this.manifestSafe = value;
    }

    /**
     * Obtient la valeur de la propriété repInfo.
     * 
     * @return
     *     possible object is
     *     {@link Level0Granule.RepInfo }
     *     
     */
    public Level0Granule.RepInfo getRepInfo() {
        return repInfo;
    }

    /**
     * Définit la valeur de la propriété repInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link Level0Granule.RepInfo }
     *     
     */
    public void setRepInfo(Level0Granule.RepInfo value) {
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
     *         &lt;element name="ISP_Files" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="13" minOccurs="13"/>
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
        "ispFiles"
    })
    public static class IMGDATA {

        @XmlElement(name = "ISP_Files", required = true)
        protected List<Object> ispFiles;

        /**
         * Gets the value of the ispFiles property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the ispFiles property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getISPFiles().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         * 
         * 
         */
        public List<Object> getISPFiles() {
            if (ispFiles == null) {
                ispFiles = new ArrayList<Object>();
            }
            return this.ispFiles;
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
    public static class Level0GranuleMetadataFile {

        @XmlElement(name = "General_Info", required = true)
        protected Object generalInfo;
        @XmlElement(name = "Geometric_Info", required = true)
        protected Level0Granule.Level0GranuleMetadataFile.GeometricInfo geometricInfo;
        @XmlElement(name = "Quality_Indicators_Info", required = true)
        protected Level0Granule.Level0GranuleMetadataFile.QualityIndicatorsInfo qualityIndicatorsInfo;

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
         *     {@link Level0Granule.Level0GranuleMetadataFile.GeometricInfo }
         *     
         */
        public Level0Granule.Level0GranuleMetadataFile.GeometricInfo getGeometricInfo() {
            return geometricInfo;
        }

        /**
         * Définit la valeur de la propriété geometricInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level0Granule.Level0GranuleMetadataFile.GeometricInfo }
         *     
         */
        public void setGeometricInfo(Level0Granule.Level0GranuleMetadataFile.GeometricInfo value) {
            this.geometricInfo = value;
        }

        /**
         * Obtient la valeur de la propriété qualityIndicatorsInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level0Granule.Level0GranuleMetadataFile.QualityIndicatorsInfo }
         *     
         */
        public Level0Granule.Level0GranuleMetadataFile.QualityIndicatorsInfo getQualityIndicatorsInfo() {
            return qualityIndicatorsInfo;
        }

        /**
         * Définit la valeur de la propriété qualityIndicatorsInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level0Granule.Level0GranuleMetadataFile.QualityIndicatorsInfo }
         *     
         */
        public void setQualityIndicatorsInfo(Level0Granule.Level0GranuleMetadataFile.QualityIndicatorsInfo value) {
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
        "olqcReport"
    })
    public static class QIDATA {

        @XmlElement(name = "OLQC_Report", required = true)
        protected Object olqcReport;

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
