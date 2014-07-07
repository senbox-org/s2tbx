//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_GENERAL_INFO_DS complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GENERAL_INFO_DS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Datatake_Info">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATATAKE_IDENTIFICATION">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Brief" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Datastrip_Time_Info">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATASTRIP_TIME_INFO">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Brief" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Processing_Info">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PROCESSING_BASELINE" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PROCESSING_BASELINE_IDENTIFICATION"/>
 *                   &lt;element name="DataStrip_Generation_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PROCESSING_STEP_LIST"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Downlink_Info">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DOWNLINK_IDENTIFICATION">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Archiving_Info">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ARCHIVE_IDENTIFICATION">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PROCESSING_SPECIFIC_PARAMETERS" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PROCESSING_SPECIFIC_PARAMETERS" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_GENERAL_INFO_DS", propOrder = {
    "datatakeInfo",
    "datastripTimeInfo",
    "processingInfo",
    "downlinkInfo",
    "archivingInfo",
    "processingspecificparameters"
})
public class AGENERALINFODS {

    @XmlElement(name = "Datatake_Info", required = true)
    protected AGENERALINFODS.DatatakeInfo datatakeInfo;
    @XmlElement(name = "Datastrip_Time_Info", required = true)
    protected AGENERALINFODS.DatastripTimeInfo datastripTimeInfo;
    @XmlElement(name = "Processing_Info", required = true)
    protected AGENERALINFODS.ProcessingInfo processingInfo;
    @XmlElement(name = "Downlink_Info", required = true)
    protected AGENERALINFODS.DownlinkInfo downlinkInfo;
    @XmlElement(name = "Archiving_Info", required = true)
    protected AGENERALINFODS.ArchivingInfo archivingInfo;
    @XmlElement(name = "PROCESSING_SPECIFIC_PARAMETERS")
    protected APROCESSINGSPECIFICPARAMETERS processingspecificparameters;

    /**
     * Obtient la valeur de la propriété datatakeInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFODS.DatatakeInfo }
     *     
     */
    public AGENERALINFODS.DatatakeInfo getDatatakeInfo() {
        return datatakeInfo;
    }

    /**
     * Définit la valeur de la propriété datatakeInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFODS.DatatakeInfo }
     *     
     */
    public void setDatatakeInfo(AGENERALINFODS.DatatakeInfo value) {
        this.datatakeInfo = value;
    }

    /**
     * Obtient la valeur de la propriété datastripTimeInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFODS.DatastripTimeInfo }
     *     
     */
    public AGENERALINFODS.DatastripTimeInfo getDatastripTimeInfo() {
        return datastripTimeInfo;
    }

    /**
     * Définit la valeur de la propriété datastripTimeInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFODS.DatastripTimeInfo }
     *     
     */
    public void setDatastripTimeInfo(AGENERALINFODS.DatastripTimeInfo value) {
        this.datastripTimeInfo = value;
    }

    /**
     * Obtient la valeur de la propriété processingInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFODS.ProcessingInfo }
     *     
     */
    public AGENERALINFODS.ProcessingInfo getProcessingInfo() {
        return processingInfo;
    }

    /**
     * Définit la valeur de la propriété processingInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFODS.ProcessingInfo }
     *     
     */
    public void setProcessingInfo(AGENERALINFODS.ProcessingInfo value) {
        this.processingInfo = value;
    }

    /**
     * Obtient la valeur de la propriété downlinkInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFODS.DownlinkInfo }
     *     
     */
    public AGENERALINFODS.DownlinkInfo getDownlinkInfo() {
        return downlinkInfo;
    }

    /**
     * Définit la valeur de la propriété downlinkInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFODS.DownlinkInfo }
     *     
     */
    public void setDownlinkInfo(AGENERALINFODS.DownlinkInfo value) {
        this.downlinkInfo = value;
    }

    /**
     * Obtient la valeur de la propriété archivingInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFODS.ArchivingInfo }
     *     
     */
    public AGENERALINFODS.ArchivingInfo getArchivingInfo() {
        return archivingInfo;
    }

    /**
     * Définit la valeur de la propriété archivingInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFODS.ArchivingInfo }
     *     
     */
    public void setArchivingInfo(AGENERALINFODS.ArchivingInfo value) {
        this.archivingInfo = value;
    }

    /**
     * Obtient la valeur de la propriété processingspecificparameters.
     * 
     * @return
     *     possible object is
     *     {@link APROCESSINGSPECIFICPARAMETERS }
     *     
     */
    public APROCESSINGSPECIFICPARAMETERS getPROCESSINGSPECIFICPARAMETERS() {
        return processingspecificparameters;
    }

    /**
     * Définit la valeur de la propriété processingspecificparameters.
     * 
     * @param value
     *     allowed object is
     *     {@link APROCESSINGSPECIFICPARAMETERS }
     *     
     */
    public void setPROCESSINGSPECIFICPARAMETERS(APROCESSINGSPECIFICPARAMETERS value) {
        this.processingspecificparameters = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ARCHIVE_IDENTIFICATION">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ArchivingInfo
        extends ANARCHIVEIDENTIFICATION
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
                return "Expertise";
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
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATASTRIP_TIME_INFO">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Brief" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DatastripTimeInfo
        extends ADATASTRIPTIMEINFO
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
                return "Brief";
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
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATATAKE_IDENTIFICATION">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Brief" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DatatakeInfo
        extends ADATATAKEIDENTIFICATION
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
                return "Brief";
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
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DOWNLINK_IDENTIFICATION">
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
    public static class DownlinkInfo
        extends ADOWNLINKIDENTIFICATION
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
     *         &lt;element name="PROCESSING_BASELINE" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PROCESSING_BASELINE_IDENTIFICATION"/>
     *         &lt;element name="DataStrip_Generation_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PROCESSING_STEP_LIST"/>
     *       &lt;/sequence>
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "processingbaseline",
        "dataStripGenerationInfo"
    })
    public static class ProcessingInfo {

        @XmlElement(name = "PROCESSING_BASELINE", required = true)
        protected String processingbaseline;
        @XmlElement(name = "DataStrip_Generation_Info", required = true)
        protected APROCESSINGSTEPLIST dataStripGenerationInfo;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Obtient la valeur de la propriété processingbaseline.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPROCESSINGBASELINE() {
            return processingbaseline;
        }

        /**
         * Définit la valeur de la propriété processingbaseline.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPROCESSINGBASELINE(String value) {
            this.processingbaseline = value;
        }

        /**
         * Obtient la valeur de la propriété dataStripGenerationInfo.
         * 
         * @return
         *     possible object is
         *     {@link APROCESSINGSTEPLIST }
         *     
         */
        public APROCESSINGSTEPLIST getDataStripGenerationInfo() {
            return dataStripGenerationInfo;
        }

        /**
         * Définit la valeur de la propriété dataStripGenerationInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link APROCESSINGSTEPLIST }
         *     
         */
        public void setDataStripGenerationInfo(APROCESSINGSTEPLIST value) {
            this.dataStripGenerationInfo = value;
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
                return "Expertise";
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

}
