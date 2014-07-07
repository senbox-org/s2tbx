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
 * <p>Classe Java pour AN_ACQUISITION_CONFIGURATION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_ACQUISITION_CONFIGURATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="COMPRESS_MODE" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="EQUALIZATION_MODE" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Active_Detectors_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ACTIVE_DETECTOR" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" maxOccurs="12"/>
 *                 &lt;/sequence>
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
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Spectral_Band_Information_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SPECTRAL_BAND_INFORMATION_LIST"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_ACQUISITION_CONFIGURATION", propOrder = {
    "compressmode",
    "equalizationmode",
    "activeDetectorsList",
    "tdiConfigurationList",
    "spectralBandInformationList"
})
public class ANACQUISITIONCONFIGURATION {

    @XmlElement(name = "COMPRESS_MODE")
    protected boolean compressmode;
    @XmlElement(name = "EQUALIZATION_MODE")
    protected boolean equalizationmode;
    @XmlElement(name = "Active_Detectors_List", required = true)
    protected ANACQUISITIONCONFIGURATION.ActiveDetectorsList activeDetectorsList;
    @XmlElement(name = "TDI_Configuration_List", required = true)
    protected ANACQUISITIONCONFIGURATION.TDIConfigurationList tdiConfigurationList;
    @XmlElement(name = "Spectral_Band_Information_List", required = true)
    protected ASPECTRALBANDINFORMATIONLIST spectralBandInformationList;

    /**
     * Obtient la valeur de la propriété compressmode.
     * 
     */
    public boolean isCOMPRESSMODE() {
        return compressmode;
    }

    /**
     * Définit la valeur de la propriété compressmode.
     * 
     */
    public void setCOMPRESSMODE(boolean value) {
        this.compressmode = value;
    }

    /**
     * Obtient la valeur de la propriété equalizationmode.
     * 
     */
    public boolean isEQUALIZATIONMODE() {
        return equalizationmode;
    }

    /**
     * Définit la valeur de la propriété equalizationmode.
     * 
     */
    public void setEQUALIZATIONMODE(boolean value) {
        this.equalizationmode = value;
    }

    /**
     * Obtient la valeur de la propriété activeDetectorsList.
     * 
     * @return
     *     possible object is
     *     {@link ANACQUISITIONCONFIGURATION.ActiveDetectorsList }
     *     
     */
    public ANACQUISITIONCONFIGURATION.ActiveDetectorsList getActiveDetectorsList() {
        return activeDetectorsList;
    }

    /**
     * Définit la valeur de la propriété activeDetectorsList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANACQUISITIONCONFIGURATION.ActiveDetectorsList }
     *     
     */
    public void setActiveDetectorsList(ANACQUISITIONCONFIGURATION.ActiveDetectorsList value) {
        this.activeDetectorsList = value;
    }

    /**
     * Obtient la valeur de la propriété tdiConfigurationList.
     * 
     * @return
     *     possible object is
     *     {@link ANACQUISITIONCONFIGURATION.TDIConfigurationList }
     *     
     */
    public ANACQUISITIONCONFIGURATION.TDIConfigurationList getTDIConfigurationList() {
        return tdiConfigurationList;
    }

    /**
     * Définit la valeur de la propriété tdiConfigurationList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANACQUISITIONCONFIGURATION.TDIConfigurationList }
     *     
     */
    public void setTDIConfigurationList(ANACQUISITIONCONFIGURATION.TDIConfigurationList value) {
        this.tdiConfigurationList = value;
    }

    /**
     * Obtient la valeur de la propriété spectralBandInformationList.
     * 
     * @return
     *     possible object is
     *     {@link ASPECTRALBANDINFORMATIONLIST }
     *     
     */
    public ASPECTRALBANDINFORMATIONLIST getSpectralBandInformationList() {
        return spectralBandInformationList;
    }

    /**
     * Définit la valeur de la propriété spectralBandInformationList.
     * 
     * @param value
     *     allowed object is
     *     {@link ASPECTRALBANDINFORMATIONLIST }
     *     
     */
    public void setSpectralBandInformationList(ASPECTRALBANDINFORMATIONLIST value) {
        this.spectralBandInformationList = value;
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
        protected List<ANACQUISITIONCONFIGURATION.TDIConfigurationList.TDICONFIGURATION> tdiconfiguration;

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
         * {@link ANACQUISITIONCONFIGURATION.TDIConfigurationList.TDICONFIGURATION }
         * 
         * 
         */
        public List<ANACQUISITIONCONFIGURATION.TDIConfigurationList.TDICONFIGURATION> getTDICONFIGURATION() {
            if (tdiconfiguration == null) {
                tdiconfiguration = new ArrayList<ANACQUISITIONCONFIGURATION.TDIConfigurationList.TDICONFIGURATION>();
            }
            return this.tdiconfiguration;
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
