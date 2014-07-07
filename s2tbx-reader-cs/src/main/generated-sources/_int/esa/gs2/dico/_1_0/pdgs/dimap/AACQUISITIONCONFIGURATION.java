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
import _int.esa.gs2.dico._1_0.sy.platform.ATDICONFIG;


/**
 * <p>Classe Java pour A_ACQUISITION_CONFIGURATION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_ACQUISITION_CONFIGURATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="COMPRESS_MODE">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EQUALIZATION_MODE">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="NUC_TABLE_ID">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Active_Detectors_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ACTIVE_DETECTOR" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" maxOccurs="12"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Brief" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="TDI_Configuration_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="TDI_CONFIGURATION" maxOccurs="4">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/platform/>A_TDI_CONFIG">
 *                           &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER_WITH_TDI" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Spectral_Band_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SPECTRAL_BAND_INFORMATION_LIST"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_ACQUISITION_CONFIGURATION", propOrder = {
    "compressmode",
    "equalizationmode",
    "nuctableid",
    "activeDetectorsList",
    "tdiConfigurationList",
    "spectralBandInfo"
})
public class AACQUISITIONCONFIGURATION {

    @XmlElement(name = "COMPRESS_MODE", required = true)
    protected AACQUISITIONCONFIGURATION.COMPRESSMODE compressmode;
    @XmlElement(name = "EQUALIZATION_MODE", required = true)
    protected AACQUISITIONCONFIGURATION.EQUALIZATIONMODE equalizationmode;
    @XmlElement(name = "NUC_TABLE_ID", required = true)
    protected AACQUISITIONCONFIGURATION.NUCTABLEID nuctableid;
    @XmlElement(name = "Active_Detectors_List", required = true)
    protected AACQUISITIONCONFIGURATION.ActiveDetectorsList activeDetectorsList;
    @XmlElement(name = "TDI_Configuration_List", required = true)
    protected AACQUISITIONCONFIGURATION.TDIConfigurationList tdiConfigurationList;
    @XmlElement(name = "Spectral_Band_Info", required = true)
    protected ASPECTRALBANDINFORMATIONLIST spectralBandInfo;

    /**
     * Obtient la valeur de la propriété compressmode.
     * 
     * @return
     *     possible object is
     *     {@link AACQUISITIONCONFIGURATION.COMPRESSMODE }
     *     
     */
    public AACQUISITIONCONFIGURATION.COMPRESSMODE getCOMPRESSMODE() {
        return compressmode;
    }

    /**
     * Définit la valeur de la propriété compressmode.
     * 
     * @param value
     *     allowed object is
     *     {@link AACQUISITIONCONFIGURATION.COMPRESSMODE }
     *     
     */
    public void setCOMPRESSMODE(AACQUISITIONCONFIGURATION.COMPRESSMODE value) {
        this.compressmode = value;
    }

    /**
     * Obtient la valeur de la propriété equalizationmode.
     * 
     * @return
     *     possible object is
     *     {@link AACQUISITIONCONFIGURATION.EQUALIZATIONMODE }
     *     
     */
    public AACQUISITIONCONFIGURATION.EQUALIZATIONMODE getEQUALIZATIONMODE() {
        return equalizationmode;
    }

    /**
     * Définit la valeur de la propriété equalizationmode.
     * 
     * @param value
     *     allowed object is
     *     {@link AACQUISITIONCONFIGURATION.EQUALIZATIONMODE }
     *     
     */
    public void setEQUALIZATIONMODE(AACQUISITIONCONFIGURATION.EQUALIZATIONMODE value) {
        this.equalizationmode = value;
    }

    /**
     * Obtient la valeur de la propriété nuctableid.
     * 
     * @return
     *     possible object is
     *     {@link AACQUISITIONCONFIGURATION.NUCTABLEID }
     *     
     */
    public AACQUISITIONCONFIGURATION.NUCTABLEID getNUCTABLEID() {
        return nuctableid;
    }

    /**
     * Définit la valeur de la propriété nuctableid.
     * 
     * @param value
     *     allowed object is
     *     {@link AACQUISITIONCONFIGURATION.NUCTABLEID }
     *     
     */
    public void setNUCTABLEID(AACQUISITIONCONFIGURATION.NUCTABLEID value) {
        this.nuctableid = value;
    }

    /**
     * Obtient la valeur de la propriété activeDetectorsList.
     * 
     * @return
     *     possible object is
     *     {@link AACQUISITIONCONFIGURATION.ActiveDetectorsList }
     *     
     */
    public AACQUISITIONCONFIGURATION.ActiveDetectorsList getActiveDetectorsList() {
        return activeDetectorsList;
    }

    /**
     * Définit la valeur de la propriété activeDetectorsList.
     * 
     * @param value
     *     allowed object is
     *     {@link AACQUISITIONCONFIGURATION.ActiveDetectorsList }
     *     
     */
    public void setActiveDetectorsList(AACQUISITIONCONFIGURATION.ActiveDetectorsList value) {
        this.activeDetectorsList = value;
    }

    /**
     * Obtient la valeur de la propriété tdiConfigurationList.
     * 
     * @return
     *     possible object is
     *     {@link AACQUISITIONCONFIGURATION.TDIConfigurationList }
     *     
     */
    public AACQUISITIONCONFIGURATION.TDIConfigurationList getTDIConfigurationList() {
        return tdiConfigurationList;
    }

    /**
     * Définit la valeur de la propriété tdiConfigurationList.
     * 
     * @param value
     *     allowed object is
     *     {@link AACQUISITIONCONFIGURATION.TDIConfigurationList }
     *     
     */
    public void setTDIConfigurationList(AACQUISITIONCONFIGURATION.TDIConfigurationList value) {
        this.tdiConfigurationList = value;
    }

    /**
     * Obtient la valeur de la propriété spectralBandInfo.
     * 
     * @return
     *     possible object is
     *     {@link ASPECTRALBANDINFORMATIONLIST }
     *     
     */
    public ASPECTRALBANDINFORMATIONLIST getSpectralBandInfo() {
        return spectralBandInfo;
    }

    /**
     * Définit la valeur de la propriété spectralBandInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ASPECTRALBANDINFORMATIONLIST }
     *     
     */
    public void setSpectralBandInfo(ASPECTRALBANDINFORMATIONLIST value) {
        this.spectralBandInfo = value;
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
     *         &lt;element name="ACTIVE_DETECTOR" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" maxOccurs="12"/>
     *       &lt;/sequence>
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Brief" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "activedetector"
    })
    public static class ActiveDetectorsList {

        @XmlElement(name = "ACTIVE_DETECTOR", required = true)
        protected List<String> activedetector;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Gets the value of the activedetector property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the activedetector property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getACTIVEDETECTOR().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getACTIVEDETECTOR() {
            if (activedetector == null) {
                activedetector = new ArrayList<String>();
            }
            return this.activedetector;
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


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
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
    public static class COMPRESSMODE {

        @XmlValue
        protected boolean value;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Obtient la valeur de la propriété value.
         * 
         */
        public boolean isValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         * 
         */
        public void setValue(boolean value) {
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
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
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
    public static class EQUALIZATIONMODE {

        @XmlValue
        protected boolean value;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Obtient la valeur de la propriété value.
         * 
         */
        public boolean isValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         * 
         */
        public void setValue(boolean value) {
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
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
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
    public static class NUCTABLEID {

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
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="TDI_CONFIGURATION" maxOccurs="4">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/platform/>A_TDI_CONFIG">
     *                 &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER_WITH_TDI" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
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
        "tdiconfiguration"
    })
    public static class TDIConfigurationList {

        @XmlElement(name = "TDI_CONFIGURATION", required = true)
        protected List<AACQUISITIONCONFIGURATION.TDIConfigurationList.TDICONFIGURATION> tdiconfiguration;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Gets the value of the tdiconfiguration property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the tdiconfiguration property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTDICONFIGURATION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AACQUISITIONCONFIGURATION.TDIConfigurationList.TDICONFIGURATION }
         * 
         * 
         */
        public List<AACQUISITIONCONFIGURATION.TDIConfigurationList.TDICONFIGURATION> getTDICONFIGURATION() {
            if (tdiconfiguration == null) {
                tdiconfiguration = new ArrayList<AACQUISITIONCONFIGURATION.TDIConfigurationList.TDICONFIGURATION>();
            }
            return this.tdiconfiguration;
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
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/platform/>A_TDI_CONFIG">
         *       &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER_WITH_TDI" />
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
        public static class TDICONFIGURATION {

            @XmlValue
            protected ATDICONFIG value;
            @XmlAttribute(name = "bandId", required = true)
            protected int bandId;

            /**
             * Obtient la valeur de la propriété value.
             * 
             * @return
             *     possible object is
             *     {@link ATDICONFIG }
             *     
             */
            public ATDICONFIG getValue() {
                return value;
            }

            /**
             * Définit la valeur de la propriété value.
             * 
             * @param value
             *     allowed object is
             *     {@link ATDICONFIG }
             *     
             */
            public void setValue(ATDICONFIG value) {
                this.value = value;
            }

            /**
             * Obtient la valeur de la propriété bandId.
             * 
             */
            public int getBandId() {
                return bandId;
            }

            /**
             * Définit la valeur de la propriété bandId.
             * 
             */
            public void setBandId(int value) {
                this.bandId = value;
            }

        }

    }

}
