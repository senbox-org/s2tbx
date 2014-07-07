//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.pdgs.dimap.AGENERALINFOL1C;
import _int.esa.gs2.dico._1_0.pdgs.dimap.AGEOMETRICINFOTILE;
import _int.esa.gs2.dico._1_0.pdgs.dimap.AQUALITYINDICATORSINFOTILE;


/**
 * <p>Classe Java pour Level-1C_Tile complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Level-1C_Tile">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="General_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GENERAL_INFO_L1C"/>
 *         &lt;element name="Geometric_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_INFO_TILE"/>
 *         &lt;element name="Quality_Indicators_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_QUALITY_INDICATORS_INFO_TILE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Level-1C_Tile", propOrder = {
    "generalInfo",
    "geometricInfo",
    "qualityIndicatorsInfo"
})
public class Level1CTile {

    @XmlElement(name = "General_Info", required = true)
    protected AGENERALINFOL1C generalInfo;
    @XmlElement(name = "Geometric_Info", required = true)
    protected AGEOMETRICINFOTILE geometricInfo;
    @XmlElement(name = "Quality_Indicators_Info", required = true)
    protected AQUALITYINDICATORSINFOTILE qualityIndicatorsInfo;

    /**
     * Obtient la valeur de la propriété generalInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGENERALINFOL1C }
     *     
     */
    public AGENERALINFOL1C getGeneralInfo() {
        return generalInfo;
    }

    /**
     * Définit la valeur de la propriété generalInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGENERALINFOL1C }
     *     
     */
    public void setGeneralInfo(AGENERALINFOL1C value) {
        this.generalInfo = value;
    }

    /**
     * Obtient la valeur de la propriété geometricInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGEOMETRICINFOTILE }
     *     
     */
    public AGEOMETRICINFOTILE getGeometricInfo() {
        return geometricInfo;
    }

    /**
     * Définit la valeur de la propriété geometricInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOMETRICINFOTILE }
     *     
     */
    public void setGeometricInfo(AGEOMETRICINFOTILE value) {
        this.geometricInfo = value;
    }

    /**
     * Obtient la valeur de la propriété qualityIndicatorsInfo.
     * 
     * @return
     *     possible object is
     *     {@link AQUALITYINDICATORSINFOTILE }
     *     
     */
    public AQUALITYINDICATORSINFOTILE getQualityIndicatorsInfo() {
        return qualityIndicatorsInfo;
    }

    /**
     * Définit la valeur de la propriété qualityIndicatorsInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AQUALITYINDICATORSINFOTILE }
     *     
     */
    public void setQualityIndicatorsInfo(AQUALITYINDICATORSINFOTILE value) {
        this.qualityIndicatorsInfo = value;
    }

}
