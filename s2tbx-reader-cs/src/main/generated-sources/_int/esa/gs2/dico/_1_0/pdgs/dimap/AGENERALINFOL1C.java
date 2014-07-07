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
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour A_GENERAL_INFO_L1C complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GENERAL_INFO_L1C">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TILE_ID">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/DataAccess/item/>TILE_ID">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Brief" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DATASTRIP_ID">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/DataAccess/item/>DATASTRIP_ID">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DOWNLINK_PRIORITY">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SENSING_TIME">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/date_time/>AN_UTC_DATE_TIME_TWO">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
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
 *         &lt;element name="Processing_Specific_Parameters" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PROCESSING_SPECIFIC_PARAMETERS" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_GENERAL_INFO_L1C", propOrder = {
    "tileid",
    "datastripid",
    "downlinkpriority",
    "sensingtime",
    "archivingInfo",
    "processingSpecificParameters"
})
public class AGENERALINFOL1C {

    @XmlElement(name = "TILE_ID", required = true)
    protected AGENERALINFOL1C.TILEID tileid;
    @XmlElement(name = "DATASTRIP_ID", required = true)
    protected AGENERALINFOL1C.DATASTRIPID datastripid;
    @XmlElement(name = "DOWNLINK_PRIORITY", required = true)
    protected AGENERALINFOL1C.DOWNLINKPRIORITY downlinkpriority;
    @XmlElement(name = "SENSING_TIME", required = true)
    protected AGENERALINFOL1C.SENSINGTIME sensingtime;
    @XmlElement(name = "Archiving_Info", required = true)
    protected AGENERALINFOL1C.ArchivingInfo archivingInfo;
    @XmlElement(name = "Processing_Specific_Parameters")
    protected APROCESSINGSPECIFICPARAMETERS processingSpecificParameters;

    /**
     * Obtient la valeur de la propriété tileid.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFOL1C.TILEID }
     *     
     */
    public AGENERALINFOL1C.TILEID getTILEID() {
        return tileid;
    }

    /**
     * Définit la valeur de la propriété tileid.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFOL1C.TILEID }
     *     
     */
    public void setTILEID(AGENERALINFOL1C.TILEID value) {
        this.tileid = value;
    }

    /**
     * Obtient la valeur de la propriété datastripid.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFOL1C.DATASTRIPID }
     *     
     */
    public AGENERALINFOL1C.DATASTRIPID getDATASTRIPID() {
        return datastripid;
    }

    /**
     * Définit la valeur de la propriété datastripid.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFOL1C.DATASTRIPID }
     *     
     */
    public void setDATASTRIPID(AGENERALINFOL1C.DATASTRIPID value) {
        this.datastripid = value;
    }

    /**
     * Obtient la valeur de la propriété downlinkpriority.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFOL1C.DOWNLINKPRIORITY }
     *     
     */
    public AGENERALINFOL1C.DOWNLINKPRIORITY getDOWNLINKPRIORITY() {
        return downlinkpriority;
    }

    /**
     * Définit la valeur de la propriété downlinkpriority.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFOL1C.DOWNLINKPRIORITY }
     *     
     */
    public void setDOWNLINKPRIORITY(AGENERALINFOL1C.DOWNLINKPRIORITY value) {
        this.downlinkpriority = value;
    }

    /**
     * Obtient la valeur de la propriété sensingtime.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFOL1C.SENSINGTIME }
     *     
     */
    public AGENERALINFOL1C.SENSINGTIME getSENSINGTIME() {
        return sensingtime;
    }

    /**
     * Définit la valeur de la propriété sensingtime.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFOL1C.SENSINGTIME }
     *     
     */
    public void setSENSINGTIME(AGENERALINFOL1C.SENSINGTIME value) {
        this.sensingtime = value;
    }

    /**
     * Obtient la valeur de la propriété archivingInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFOL1C.ArchivingInfo }
     *     
     */
    public AGENERALINFOL1C.ArchivingInfo getArchivingInfo() {
        return archivingInfo;
    }

    /**
     * Définit la valeur de la propriété archivingInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFOL1C.ArchivingInfo }
     *     
     */
    public void setArchivingInfo(AGENERALINFOL1C.ArchivingInfo value) {
        this.archivingInfo = value;
    }

    /**
     * Obtient la valeur de la propriété processingSpecificParameters.
     * 
     * @return
     *     possible object is
     *     {@link APROCESSINGSPECIFICPARAMETERS }
     *     
     */
    public APROCESSINGSPECIFICPARAMETERS getProcessingSpecificParameters() {
        return processingSpecificParameters;
    }

    /**
     * Définit la valeur de la propriété processingSpecificParameters.
     * 
     * @param value
     *     allowed object is
     *     {@link APROCESSINGSPECIFICPARAMETERS }
     *     
     */
    public void setProcessingSpecificParameters(APROCESSINGSPECIFICPARAMETERS value) {
        this.processingSpecificParameters = value;
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
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/DataAccess/item/>DATASTRIP_ID">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
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
    public static class DATASTRIPID {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Product Data Item identification
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
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
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
    public static class DOWNLINKPRIORITY {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

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
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/date_time/>AN_UTC_DATE_TIME_TWO">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
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
    public static class SENSINGTIME {

        @XmlValue
        protected XMLGregorianCalendar value;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * An UTC date-time value
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setValue(XMLGregorianCalendar value) {
            this.value = value;
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
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/DataAccess/item/>TILE_ID">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Brief" />
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
    public static class TILEID {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Product Data Item identification
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

}
