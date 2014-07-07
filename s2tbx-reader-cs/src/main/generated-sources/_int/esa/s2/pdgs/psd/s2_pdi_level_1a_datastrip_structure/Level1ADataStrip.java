//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_1a_datastrip_structure;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Folder
 * 
 * <p>Classe Java pour Level-1A_DataStrip complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Level-1A_DataStrip">
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
 *         &lt;element name="DataStrip_Metadata_File">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="General_Info">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Image_Data_Info">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Satellite_Ancillary_Data_Info" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="Quality_Indicators_Info">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Auxiliary_Data_Info">
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
 *       &lt;attribute name="datastripIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Level-1A_DataStrip", propOrder = {
    "inventoryMetadata",
    "dataStripMetadataFile",
    "qidata",
    "manifestSafe",
    "repInfo"
})
public class Level1ADataStrip {

    @XmlElement(name = "Inventory_Metadata", required = true)
    protected Level1ADataStrip.InventoryMetadata inventoryMetadata;
    @XmlElement(name = "DataStrip_Metadata_File", required = true)
    protected Level1ADataStrip.DataStripMetadataFile dataStripMetadataFile;
    @XmlElement(name = "QI_DATA", required = true)
    protected Level1ADataStrip.QIDATA qidata;
    @XmlElement(name = "manifest.safe", required = true)
    protected Level1ADataStrip.ManifestSafe manifestSafe;
    @XmlElement(name = "rep_info", required = true)
    protected Level1ADataStrip.RepInfo repInfo;
    @XmlAttribute(name = "datastripIdentifier")
    protected String datastripIdentifier;

    /**
     * Obtient la valeur de la propriété inventoryMetadata.
     * 
     * @return
     *     possible object is
     *     {@link Level1ADataStrip.InventoryMetadata }
     *     
     */
    public Level1ADataStrip.InventoryMetadata getInventoryMetadata() {
        return inventoryMetadata;
    }

    /**
     * Définit la valeur de la propriété inventoryMetadata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1ADataStrip.InventoryMetadata }
     *     
     */
    public void setInventoryMetadata(Level1ADataStrip.InventoryMetadata value) {
        this.inventoryMetadata = value;
    }

    /**
     * Obtient la valeur de la propriété dataStripMetadataFile.
     * 
     * @return
     *     possible object is
     *     {@link Level1ADataStrip.DataStripMetadataFile }
     *     
     */
    public Level1ADataStrip.DataStripMetadataFile getDataStripMetadataFile() {
        return dataStripMetadataFile;
    }

    /**
     * Définit la valeur de la propriété dataStripMetadataFile.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1ADataStrip.DataStripMetadataFile }
     *     
     */
    public void setDataStripMetadataFile(Level1ADataStrip.DataStripMetadataFile value) {
        this.dataStripMetadataFile = value;
    }

    /**
     * Obtient la valeur de la propriété qidata.
     * 
     * @return
     *     possible object is
     *     {@link Level1ADataStrip.QIDATA }
     *     
     */
    public Level1ADataStrip.QIDATA getQIDATA() {
        return qidata;
    }

    /**
     * Définit la valeur de la propriété qidata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1ADataStrip.QIDATA }
     *     
     */
    public void setQIDATA(Level1ADataStrip.QIDATA value) {
        this.qidata = value;
    }

    /**
     * Obtient la valeur de la propriété manifestSafe.
     * 
     * @return
     *     possible object is
     *     {@link Level1ADataStrip.ManifestSafe }
     *     
     */
    public Level1ADataStrip.ManifestSafe getManifestSafe() {
        return manifestSafe;
    }

    /**
     * Définit la valeur de la propriété manifestSafe.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1ADataStrip.ManifestSafe }
     *     
     */
    public void setManifestSafe(Level1ADataStrip.ManifestSafe value) {
        this.manifestSafe = value;
    }

    /**
     * Obtient la valeur de la propriété repInfo.
     * 
     * @return
     *     possible object is
     *     {@link Level1ADataStrip.RepInfo }
     *     
     */
    public Level1ADataStrip.RepInfo getRepInfo() {
        return repInfo;
    }

    /**
     * Définit la valeur de la propriété repInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1ADataStrip.RepInfo }
     *     
     */
    public void setRepInfo(Level1ADataStrip.RepInfo value) {
        this.repInfo = value;
    }

    /**
     * Obtient la valeur de la propriété datastripIdentifier.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatastripIdentifier() {
        return datastripIdentifier;
    }

    /**
     * Définit la valeur de la propriété datastripIdentifier.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatastripIdentifier(String value) {
        this.datastripIdentifier = value;
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
     *         &lt;element name="General_Info">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Image_Data_Info">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Satellite_Ancillary_Data_Info" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="Quality_Indicators_Info">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Auxiliary_Data_Info">
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
        "imageDataInfo",
        "satelliteAncillaryDataInfo",
        "qualityIndicatorsInfo",
        "auxiliaryDataInfo"
    })
    public static class DataStripMetadataFile {

        @XmlElement(name = "General_Info", required = true)
        protected Level1ADataStrip.DataStripMetadataFile.GeneralInfo generalInfo;
        @XmlElement(name = "Image_Data_Info", required = true)
        protected Level1ADataStrip.DataStripMetadataFile.ImageDataInfo imageDataInfo;
        @XmlElement(name = "Satellite_Ancillary_Data_Info", required = true)
        protected Object satelliteAncillaryDataInfo;
        @XmlElement(name = "Quality_Indicators_Info", required = true)
        protected Level1ADataStrip.DataStripMetadataFile.QualityIndicatorsInfo qualityIndicatorsInfo;
        @XmlElement(name = "Auxiliary_Data_Info", required = true)
        protected Level1ADataStrip.DataStripMetadataFile.AuxiliaryDataInfo auxiliaryDataInfo;

        /**
         * Obtient la valeur de la propriété generalInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level1ADataStrip.DataStripMetadataFile.GeneralInfo }
         *     
         */
        public Level1ADataStrip.DataStripMetadataFile.GeneralInfo getGeneralInfo() {
            return generalInfo;
        }

        /**
         * Définit la valeur de la propriété generalInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level1ADataStrip.DataStripMetadataFile.GeneralInfo }
         *     
         */
        public void setGeneralInfo(Level1ADataStrip.DataStripMetadataFile.GeneralInfo value) {
            this.generalInfo = value;
        }

        /**
         * Obtient la valeur de la propriété imageDataInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level1ADataStrip.DataStripMetadataFile.ImageDataInfo }
         *     
         */
        public Level1ADataStrip.DataStripMetadataFile.ImageDataInfo getImageDataInfo() {
            return imageDataInfo;
        }

        /**
         * Définit la valeur de la propriété imageDataInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level1ADataStrip.DataStripMetadataFile.ImageDataInfo }
         *     
         */
        public void setImageDataInfo(Level1ADataStrip.DataStripMetadataFile.ImageDataInfo value) {
            this.imageDataInfo = value;
        }

        /**
         * Obtient la valeur de la propriété satelliteAncillaryDataInfo.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getSatelliteAncillaryDataInfo() {
            return satelliteAncillaryDataInfo;
        }

        /**
         * Définit la valeur de la propriété satelliteAncillaryDataInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setSatelliteAncillaryDataInfo(Object value) {
            this.satelliteAncillaryDataInfo = value;
        }

        /**
         * Obtient la valeur de la propriété qualityIndicatorsInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level1ADataStrip.DataStripMetadataFile.QualityIndicatorsInfo }
         *     
         */
        public Level1ADataStrip.DataStripMetadataFile.QualityIndicatorsInfo getQualityIndicatorsInfo() {
            return qualityIndicatorsInfo;
        }

        /**
         * Définit la valeur de la propriété qualityIndicatorsInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level1ADataStrip.DataStripMetadataFile.QualityIndicatorsInfo }
         *     
         */
        public void setQualityIndicatorsInfo(Level1ADataStrip.DataStripMetadataFile.QualityIndicatorsInfo value) {
            this.qualityIndicatorsInfo = value;
        }

        /**
         * Obtient la valeur de la propriété auxiliaryDataInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level1ADataStrip.DataStripMetadataFile.AuxiliaryDataInfo }
         *     
         */
        public Level1ADataStrip.DataStripMetadataFile.AuxiliaryDataInfo getAuxiliaryDataInfo() {
            return auxiliaryDataInfo;
        }

        /**
         * Définit la valeur de la propriété auxiliaryDataInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level1ADataStrip.DataStripMetadataFile.AuxiliaryDataInfo }
         *     
         */
        public void setAuxiliaryDataInfo(Level1ADataStrip.DataStripMetadataFile.AuxiliaryDataInfo value) {
            this.auxiliaryDataInfo = value;
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
        public static class AuxiliaryDataInfo {


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
        public static class GeneralInfo {


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
        public static class ImageDataInfo {


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
