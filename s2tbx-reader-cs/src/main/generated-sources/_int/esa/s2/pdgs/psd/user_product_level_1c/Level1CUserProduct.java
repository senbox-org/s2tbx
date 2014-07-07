//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.user_product_level_1c;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.pdgs.dimap.AGEOMETRICINFOUSER1C;
import _int.esa.gs2.dico._1_0.pdgs.dimap.ANAUXILIARYDATAINFOUSERL1C;
import _int.esa.gs2.dico._1_0.pdgs.dimap.APRODUCTINFOUSERL1C;
import _int.esa.gs2.dico._1_0.pdgs.dimap.AQUALITYINDICATORSINFOUSERPRODL1BL1C;


/**
 * <p>Classe Java pour Level-1C_User_Product complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Level-1C_User_Product">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="General_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PRODUCT_INFO_USERL1C"/>
 *         &lt;element name="Geometric_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_INFO_USER1C"/>
 *         &lt;element name="Auxiliary_Data_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_AUXILIARY_DATA_INFO_USERL1C"/>
 *         &lt;element name="Quality_Indicators_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_QUALITY_INDICATORS_INFO_USER_PROD_L1B_L1C"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Level-1C_User_Product", namespace = "http://pdgs.s2.esa.int/PSD/User_Product_Level-1C.xsd", propOrder = {
    "generalInfo",
    "geometricInfo",
    "auxiliaryDataInfo",
    "qualityIndicatorsInfo"
})
public class Level1CUserProduct {

    @XmlElement(name = "General_Info", required = true)
    protected APRODUCTINFOUSERL1C generalInfo;
    @XmlElement(name = "Geometric_Info", required = true)
    protected AGEOMETRICINFOUSER1C geometricInfo;
    @XmlElement(name = "Auxiliary_Data_Info", required = true)
    protected ANAUXILIARYDATAINFOUSERL1C auxiliaryDataInfo;
    @XmlElement(name = "Quality_Indicators_Info", required = true)
    protected AQUALITYINDICATORSINFOUSERPRODL1BL1C qualityIndicatorsInfo;

    /**
     * Obtient la valeur de la propriété generalInfo.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTINFOUSERL1C }
     *     
     */
    public APRODUCTINFOUSERL1C getGeneralInfo() {
        return generalInfo;
    }

    /**
     * Définit la valeur de la propriété generalInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTINFOUSERL1C }
     *     
     */
    public void setGeneralInfo(APRODUCTINFOUSERL1C value) {
        this.generalInfo = value;
    }

    /**
     * Obtient la valeur de la propriété geometricInfo.
     * 
     * @return
     *     possible object is
     *     {@link AGEOMETRICINFOUSER1C }
     *     
     */
    public AGEOMETRICINFOUSER1C getGeometricInfo() {
        return geometricInfo;
    }

    /**
     * Définit la valeur de la propriété geometricInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOMETRICINFOUSER1C }
     *     
     */
    public void setGeometricInfo(AGEOMETRICINFOUSER1C value) {
        this.geometricInfo = value;
    }

    /**
     * Obtient la valeur de la propriété auxiliaryDataInfo.
     * 
     * @return
     *     possible object is
     *     {@link ANAUXILIARYDATAINFOUSERL1C }
     *     
     */
    public ANAUXILIARYDATAINFOUSERL1C getAuxiliaryDataInfo() {
        return auxiliaryDataInfo;
    }

    /**
     * Définit la valeur de la propriété auxiliaryDataInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ANAUXILIARYDATAINFOUSERL1C }
     *     
     */
    public void setAuxiliaryDataInfo(ANAUXILIARYDATAINFOUSERL1C value) {
        this.auxiliaryDataInfo = value;
    }

    /**
     * Obtient la valeur de la propriété qualityIndicatorsInfo.
     * 
     * @return
     *     possible object is
     *     {@link AQUALITYINDICATORSINFOUSERPRODL1BL1C }
     *     
     */
    public AQUALITYINDICATORSINFOUSERPRODL1BL1C getQualityIndicatorsInfo() {
        return qualityIndicatorsInfo;
    }

    /**
     * Définit la valeur de la propriété qualityIndicatorsInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AQUALITYINDICATORSINFOUSERPRODL1BL1C }
     *     
     */
    public void setQualityIndicatorsInfo(AQUALITYINDICATORSINFOUSERPRODL1BL1C value) {
        this.qualityIndicatorsInfo = value;
    }

}
