//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_0_datastrip_structure;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Folder
 * 
 * <p>Classe Java pour Level-0_Datastrip complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Level-0_Datastrip">
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
 *                   &lt;element name="Preliminary_QuickLook" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="5" minOccurs="0"/>
 *                   &lt;element name="OLQC_Report" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ANC_DATA">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="SAD_Raw" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded"/>
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
@XmlType(name = "Level-0_Datastrip", propOrder = {
    "inventoryMetadata",
    "dataStripMetadataFile",
    "qidata",
    "ancdata",
    "manifestSafe",
    "repInfo"
})
public class Level0Datastrip {

    @XmlElement(name = "Inventory_Metadata", required = true)
    protected Level0Datastrip.InventoryMetadata inventoryMetadata;
    @XmlElement(name = "DataStrip_Metadata_File", required = true)
    protected Level0Datastrip.DataStripMetadataFile dataStripMetadataFile;
    @XmlElement(name = "QI_DATA", required = true)
    protected Level0Datastrip.QIDATA qidata;
    @XmlElement(name = "ANC_DATA", required = true)
    protected Level0Datastrip.ANCDATA ancdata;
    @XmlElement(name = "manifest.safe", required = true)
    protected Level0Datastrip.ManifestSafe manifestSafe;
    @XmlElement(name = "rep_info", required = true)
    protected Level0Datastrip.RepInfo repInfo;
    @XmlAttribute(name = "datastripIdentifier")
    protected String datastripIdentifier;

    /**
     * Obtient la valeur de la propriété inventoryMetadata.
     * 
     * @return
     *     possible object is
     *     {@link Level0Datastrip.InventoryMetadata }
     *     
     */
    public Level0Datastrip.InventoryMetadata getInventoryMetadata() {
        return inventoryMetadata;
    }

    /**
     * Définit la valeur de la propriété inventoryMetadata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level0Datastrip.InventoryMetadata }
     *     
     */
    public void setInventoryMetadata(Level0Datastrip.InventoryMetadata value) {
        this.inventoryMetadata = value;
    }

    /**
     * Obtient la valeur de la propriété dataStripMetadataFile.
     * 
     * @return
     *     possible object is
     *     {@link Level0Datastrip.DataStripMetadataFile }
     *     
     */
    public Level0Datastrip.DataStripMetadataFile getDataStripMetadataFile() {
        return dataStripMetadataFile;
    }

    /**
     * Définit la valeur de la propriété dataStripMetadataFile.
     * 
     * @param value
     *     allowed object is
     *     {@link Level0Datastrip.DataStripMetadataFile }
     *     
     */
    public void setDataStripMetadataFile(Level0Datastrip.DataStripMetadataFile value) {
        this.dataStripMetadataFile = value;
    }

    /**
     * Obtient la valeur de la propriété qidata.
     * 
     * @return
     *     possible object is
     *     {@link Level0Datastrip.QIDATA }
     *     
     */
    public Level0Datastrip.QIDATA getQIDATA() {
        return qidata;
    }

    /**
     * Définit la valeur de la propriété qidata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level0Datastrip.QIDATA }
     *     
     */
    public void setQIDATA(Level0Datastrip.QIDATA value) {
        this.qidata = value;
    }

    /**
     * Obtient la valeur de la propriété ancdata.
     * 
     * @return
     *     possible object is
     *     {@link Level0Datastrip.ANCDATA }
     *     
     */
    public Level0Datastrip.ANCDATA getANCDATA() {
        return ancdata;
    }

    /**
     * Définit la valeur de la propriété ancdata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level0Datastrip.ANCDATA }
     *     
     */
    public void setANCDATA(Level0Datastrip.ANCDATA value) {
        this.ancdata = value;
    }

    /**
     * Obtient la valeur de la propriété manifestSafe.
     * 
     * @return
     *     possible object is
     *     {@link Level0Datastrip.ManifestSafe }
     *     
     */
    public Level0Datastrip.ManifestSafe getManifestSafe() {
        return manifestSafe;
    }

    /**
     * Définit la valeur de la propriété manifestSafe.
     * 
     * @param value
     *     allowed object is
     *     {@link Level0Datastrip.ManifestSafe }
     *     
     */
    public void setManifestSafe(Level0Datastrip.ManifestSafe value) {
        this.manifestSafe = value;
    }

    /**
     * Obtient la valeur de la propriété repInfo.
     * 
     * @return
     *     possible object is
     *     {@link Level0Datastrip.RepInfo }
     *     
     */
    public Level0Datastrip.RepInfo getRepInfo() {
        return repInfo;
    }

    /**
     * Définit la valeur de la propriété repInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link Level0Datastrip.RepInfo }
     *     
     */
    public void setRepInfo(Level0Datastrip.RepInfo value) {
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
     *         &lt;element name="SAD_Raw" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded"/>
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
        "sadRaw"
    })
    public static class ANCDATA {

        @XmlElement(name = "SAD_Raw", required = true)
        protected List<Object> sadRaw;

        /**
         * Gets the value of the sadRaw property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sadRaw property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSADRaw().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         * 
         * 
         */
        public List<Object> getSADRaw() {
            if (sadRaw == null) {
                sadRaw = new ArrayList<Object>();
            }
            return this.sadRaw;
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
        protected Level0Datastrip.DataStripMetadataFile.GeneralInfo generalInfo;
        @XmlElement(name = "Image_Data_Info", required = true)
        protected Level0Datastrip.DataStripMetadataFile.ImageDataInfo imageDataInfo;
        @XmlElement(name = "Satellite_Ancillary_Data_Info", required = true)
        protected Object satelliteAncillaryDataInfo;
        @XmlElement(name = "Quality_Indicators_Info", required = true)
        protected Level0Datastrip.DataStripMetadataFile.QualityIndicatorsInfo qualityIndicatorsInfo;
        @XmlElement(name = "Auxiliary_Data_Info", required = true)
        protected Level0Datastrip.DataStripMetadataFile.AuxiliaryDataInfo auxiliaryDataInfo;

        /**
         * Obtient la valeur de la propriété generalInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level0Datastrip.DataStripMetadataFile.GeneralInfo }
         *     
         */
        public Level0Datastrip.DataStripMetadataFile.GeneralInfo getGeneralInfo() {
            return generalInfo;
        }

        /**
         * Définit la valeur de la propriété generalInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level0Datastrip.DataStripMetadataFile.GeneralInfo }
         *     
         */
        public void setGeneralInfo(Level0Datastrip.DataStripMetadataFile.GeneralInfo value) {
            this.generalInfo = value;
        }

        /**
         * Obtient la valeur de la propriété imageDataInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level0Datastrip.DataStripMetadataFile.ImageDataInfo }
         *     
         */
        public Level0Datastrip.DataStripMetadataFile.ImageDataInfo getImageDataInfo() {
            return imageDataInfo;
        }

        /**
         * Définit la valeur de la propriété imageDataInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level0Datastrip.DataStripMetadataFile.ImageDataInfo }
         *     
         */
        public void setImageDataInfo(Level0Datastrip.DataStripMetadataFile.ImageDataInfo value) {
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
         *     {@link Level0Datastrip.DataStripMetadataFile.QualityIndicatorsInfo }
         *     
         */
        public Level0Datastrip.DataStripMetadataFile.QualityIndicatorsInfo getQualityIndicatorsInfo() {
            return qualityIndicatorsInfo;
        }

        /**
         * Définit la valeur de la propriété qualityIndicatorsInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level0Datastrip.DataStripMetadataFile.QualityIndicatorsInfo }
         *     
         */
        public void setQualityIndicatorsInfo(Level0Datastrip.DataStripMetadataFile.QualityIndicatorsInfo value) {
            this.qualityIndicatorsInfo = value;
        }

        /**
         * Obtient la valeur de la propriété auxiliaryDataInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level0Datastrip.DataStripMetadataFile.AuxiliaryDataInfo }
         *     
         */
        public Level0Datastrip.DataStripMetadataFile.AuxiliaryDataInfo getAuxiliaryDataInfo() {
            return auxiliaryDataInfo;
        }

        /**
         * Définit la valeur de la propriété auxiliaryDataInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level0Datastrip.DataStripMetadataFile.AuxiliaryDataInfo }
         *     
         */
        public void setAuxiliaryDataInfo(Level0Datastrip.DataStripMetadataFile.AuxiliaryDataInfo value) {
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
     *         &lt;element name="Preliminary_QuickLook" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="5" minOccurs="0"/>
     *         &lt;element name="OLQC_Report" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded"/>
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
        "preliminaryQuickLook",
        "olqcReport"
    })
    public static class QIDATA {

        @XmlElement(name = "Preliminary_QuickLook")
        protected List<Object> preliminaryQuickLook;
        @XmlElement(name = "OLQC_Report", required = true)
        protected List<Object> olqcReport;

        /**
         * Gets the value of the preliminaryQuickLook property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the preliminaryQuickLook property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPreliminaryQuickLook().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         * 
         * 
         */
        public List<Object> getPreliminaryQuickLook() {
            if (preliminaryQuickLook == null) {
                preliminaryQuickLook = new ArrayList<Object>();
            }
            return this.preliminaryQuickLook;
        }

        /**
         * Gets the value of the olqcReport property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the olqcReport property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOLQCReport().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         * 
         * 
         */
        public List<Object> getOLQCReport() {
            if (olqcReport == null) {
                olqcReport = new ArrayList<Object>();
            }
            return this.olqcReport;
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
