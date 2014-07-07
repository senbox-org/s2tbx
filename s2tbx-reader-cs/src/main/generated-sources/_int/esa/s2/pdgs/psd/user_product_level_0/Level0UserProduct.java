//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.user_product_level_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.pdgs.dimap.AGEOMETRICINFOUSERL0L1AL1B;
import _int.esa.gs2.dico._1_0.pdgs.dimap.ANAUXILIARYDATAINFOUSERL0L1A;
import _int.esa.gs2.dico._1_0.pdgs.dimap.APRODUCTINFOUSERL0;
import _int.esa.gs2.dico._1_0.pdgs.dimap.AQUALITYINDICATORSINFOUSERPRODL0L1A;


/**
 * <p>Classe Java pour Level-0_User_Product complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Level-0_User_Product">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="General_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PRODUCT_INFO_USERL0"/>
 *         &lt;element name="Geometric_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_INFO_USERL0L1AL1B"/>
 *         &lt;element name="Auxiliary_Data_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_AUXILIARY_DATA_INFO_USERL0L1A"/>
 *         &lt;element name="Quality_Indicators_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_QUALITY_INDICATORS_INFO_USER_PROD_L0_L1A"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Level-0_User_Product", namespace = "http://pdgs.s2.esa.int/PSD/User_Product_Level-0.xsd", propOrder = {
    "generalInfo",
    "geometricInfo",
    "auxiliaryDataInfo",
    "qualityIndicatorsInfo"
})
public class Level0UserProduct {

    @XmlElement(name = "General_Info", required = true)
    protected APRODUCTINFOUSERL0 generalInfo;
    @XmlElement(name = "Geometric_Info", required = true)
    protected AGEOMETRICINFOUSERL0L1AL1B geometricInfo;
    @XmlElement(name = "Auxiliary_Data_Info", required = true)
    protected ANAUXILIARYDATAINFOUSERL0L1A auxiliaryDataInfo;
    @XmlElement(name = "Quality_Indicators_Info", required = true)
    protected AQUALITYINDICATORSINFOUSERPRODL0L1A qualityIndicatorsInfo;

    /**
     * Obtient la valeur de la propriété generalInfo.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTINFOUSERL0 }
     *     
     */
    public APRODUCTINFOUSERL0 getGeneralInfo() {
        return generalInfo;
    }

    /**
     * Définit la valeur de la propriété generalInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTINFOUSERL0 }
     *     
     */
    public void setGeneralInfo(APRODUCTINFOUSERL0 value) {
        this.generalInfo = value;
    }

    /**
     * Obtient la valeur de la propriété geometricInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGEOMETRICINFOUSERL0L1AL1B }
     *     
     */
    public AGEOMETRICINFOUSERL0L1AL1B getGeometricInfo() {
        return geometricInfo;
    }

    /**
     * Définit la valeur de la propriété geometricInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOMETRICINFOUSERL0L1AL1B }
     *     
     */
    public void setGeometricInfo(AGEOMETRICINFOUSERL0L1AL1B value) {
        this.geometricInfo = value;
    }

    /**
     * Obtient la valeur de la propriété auxiliaryDataInfo.
     * 
     * @return
     *     possible object is
     *     {@link ANAUXILIARYDATAINFOUSERL0L1A }
     *     
     */
    public ANAUXILIARYDATAINFOUSERL0L1A getAuxiliaryDataInfo() {
        return auxiliaryDataInfo;
    }

    /**
     * Définit la valeur de la propriété auxiliaryDataInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ANAUXILIARYDATAINFOUSERL0L1A }
     *     
     */
    public void setAuxiliaryDataInfo(ANAUXILIARYDATAINFOUSERL0L1A value) {
        this.auxiliaryDataInfo = value;
    }

    /**
     * Obtient la valeur de la propriété qualityIndicatorsInfo.
     * 
     * @return
     *     possible object is
     *     {@link AQUALITYINDICATORSINFOUSERPRODL0L1A }
     *     
     */
    public AQUALITYINDICATORSINFOUSERPRODL0L1A getQualityIndicatorsInfo() {
        return qualityIndicatorsInfo;
    }

    /**
     * Définit la valeur de la propriété qualityIndicatorsInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AQUALITYINDICATORSINFOUSERPRODL0L1A }
     *     
     */
    public void setQualityIndicatorsInfo(AQUALITYINDICATORSINFOUSERPRODL0L1A value) {
        this.qualityIndicatorsInfo = value;
    }

}
