//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_0_datastrip_metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.pdgs.dimap.AGENERALINFODS;
import _int.esa.gs2.dico._1_0.pdgs.dimap.ANANCILLARYDATADSL0;
import _int.esa.gs2.dico._1_0.pdgs.dimap.ANAUXILIARYDATAINFODSL0;
import _int.esa.gs2.dico._1_0.pdgs.dimap.ANIMAGEDATAINFODSL0;
import _int.esa.gs2.dico._1_0.pdgs.dimap.AQUALITYINDICATORSINFODSL0;


/**
 * <p>Classe Java pour Level-0_Datastrip complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Level-0_Datastrip">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="General_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GENERAL_INFO_DS"/>
 *         &lt;element name="Image_Data_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_IMAGE_DATA_INFO_DSL0"/>
 *         &lt;element name="Satellite_Ancillary_Data_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ANCILLARY_DATA_DSL0"/>
 *         &lt;element name="Quality_Indicators_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_QUALITY_INDICATORS_INFO_DSL0"/>
 *         &lt;element name="Auxiliary_Data_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_AUXILIARY_DATA_INFO_DSL0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="datastripIdentifier" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATASTRIP_ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Level-0_Datastrip", propOrder = {
    "generalInfo",
    "imageDataInfo",
    "satelliteAncillaryDataInfo",
    "qualityIndicatorsInfo",
    "auxiliaryDataInfo"
})
public class Level0Datastrip {

    @XmlElement(name = "General_Info", required = true)
    protected AGENERALINFODS generalInfo;
    @XmlElement(name = "Image_Data_Info", required = true)
    protected ANIMAGEDATAINFODSL0 imageDataInfo;
    @XmlElement(name = "Satellite_Ancillary_Data_Info", required = true)
    protected ANANCILLARYDATADSL0 satelliteAncillaryDataInfo;
    @XmlElement(name = "Quality_Indicators_Info", required = true)
    protected AQUALITYINDICATORSINFODSL0 qualityIndicatorsInfo;
    @XmlElement(name = "Auxiliary_Data_Info", required = true)
    protected ANAUXILIARYDATAINFODSL0 auxiliaryDataInfo;
    @XmlAttribute(name = "datastripIdentifier")
    protected String datastripIdentifier;

    /**
     * Obtient la valeur de la propriété generalInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFODS }
     *     
     */
    public AGENERALINFODS getGeneralInfo() {
        return generalInfo;
    }

    /**
     * Définit la valeur de la propriété generalInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFODS }
     *     
     */
    public void setGeneralInfo(AGENERALINFODS value) {
        this.generalInfo = value;
    }

    /**
     * Obtient la valeur de la propriété imageDataInfo.
     * 
     * @return
     *     possible object is
     *     {@link ANIMAGEDATAINFODSL0 }
     *     
     */
    public ANIMAGEDATAINFODSL0 getImageDataInfo() {
        return imageDataInfo;
    }

    /**
     * Définit la valeur de la propriété imageDataInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIMAGEDATAINFODSL0 }
     *     
     */
    public void setImageDataInfo(ANIMAGEDATAINFODSL0 value) {
        this.imageDataInfo = value;
    }

    /**
     * Obtient la valeur de la propriété satelliteAncillaryDataInfo.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATADSL0 }
     *     
     */
    public ANANCILLARYDATADSL0 getSatelliteAncillaryDataInfo() {
        return satelliteAncillaryDataInfo;
    }

    /**
     * Définit la valeur de la propriété satelliteAncillaryDataInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATADSL0 }
     *     
     */
    public void setSatelliteAncillaryDataInfo(ANANCILLARYDATADSL0 value) {
        this.satelliteAncillaryDataInfo = value;
    }

    /**
     * Obtient la valeur de la propriété qualityIndicatorsInfo.
     * 
     * @return
     *     possible object is
     *     {@link AQUALITYINDICATORSINFODSL0 }
     *     
     */
    public AQUALITYINDICATORSINFODSL0 getQualityIndicatorsInfo() {
        return qualityIndicatorsInfo;
    }

    /**
     * Définit la valeur de la propriété qualityIndicatorsInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AQUALITYINDICATORSINFODSL0 }
     *     
     */
    public void setQualityIndicatorsInfo(AQUALITYINDICATORSINFODSL0 value) {
        this.qualityIndicatorsInfo = value;
    }

    /**
     * Obtient la valeur de la propriété auxiliaryDataInfo.
     * 
     * @return
     *     possible object is
     *     {@link ANAUXILIARYDATAINFODSL0 }
     *     
     */
    public ANAUXILIARYDATAINFODSL0 getAuxiliaryDataInfo() {
        return auxiliaryDataInfo;
    }

    /**
     * Définit la valeur de la propriété auxiliaryDataInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ANAUXILIARYDATAINFODSL0 }
     *     
     */
    public void setAuxiliaryDataInfo(ANAUXILIARYDATAINFODSL0 value) {
        this.auxiliaryDataInfo = value;
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

}
