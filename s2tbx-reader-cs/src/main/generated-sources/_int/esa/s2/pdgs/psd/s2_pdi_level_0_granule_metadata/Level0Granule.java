//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_0_granule_metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.pdgs.dimap.AGENERALINFOL0L1AL1B;
import _int.esa.gs2.dico._1_0.pdgs.dimap.AGEOMETRICINFOGRL0;
import _int.esa.gs2.dico._1_0.pdgs.dimap.AQUALITYINDICATORSINFOGRL0;


/**
 * <p>Classe Java pour Level-0_Granule complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Level-0_Granule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="General_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GENERAL_INFO_L0_L1A_L1B"/>
 *         &lt;element name="Geometric_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_INFO_GRL0"/>
 *         &lt;element name="Quality_Indicators_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_QUALITY_INDICATORS_INFO_GRL0"/>
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
    "generalInfo",
    "geometricInfo",
    "qualityIndicatorsInfo"
})
public class Level0Granule {

    @XmlElement(name = "General_Info", required = true)
    protected AGENERALINFOL0L1AL1B generalInfo;
    @XmlElement(name = "Geometric_Info", required = true)
    protected AGEOMETRICINFOGRL0 geometricInfo;
    @XmlElement(name = "Quality_Indicators_Info", required = true)
    protected AQUALITYINDICATORSINFOGRL0 qualityIndicatorsInfo;

    /**
     * Obtient la valeur de la propriété generalInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFOL0L1AL1B }
     *     
     */
    public AGENERALINFOL0L1AL1B getGeneralInfo() {
        return generalInfo;
    }

    /**
     * Définit la valeur de la propriété generalInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFOL0L1AL1B }
     *     
     */
    public void setGeneralInfo(AGENERALINFOL0L1AL1B value) {
        this.generalInfo = value;
    }

    /**
     * Obtient la valeur de la propriété geometricInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGEOMETRICINFOGRL0 }
     *     
     */
    public AGEOMETRICINFOGRL0 getGeometricInfo() {
        return geometricInfo;
    }

    /**
     * Définit la valeur de la propriété geometricInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOMETRICINFOGRL0 }
     *     
     */
    public void setGeometricInfo(AGEOMETRICINFOGRL0 value) {
        this.geometricInfo = value;
    }

    /**
     * Obtient la valeur de la propriété qualityIndicatorsInfo.
     * 
     * @return
     *     possible object is
     *     {@link AQUALITYINDICATORSINFOGRL0 }
     *     
     */
    public AQUALITYINDICATORSINFOGRL0 getQualityIndicatorsInfo() {
        return qualityIndicatorsInfo;
    }

    /**
     * Définit la valeur de la propriété qualityIndicatorsInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AQUALITYINDICATORSINFOGRL0 }
     *     
     */
    public void setQualityIndicatorsInfo(AQUALITYINDICATORSINFOGRL0 value) {
        this.qualityIndicatorsInfo = value;
    }

}
