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
 * <p>Classe Java pour A_QUALITY_INDICATORS_INFO_DSL1B_DSL1C complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_QUALITY_INDICATORS_INFO_DSL1B_DSL1C">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Geometric_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATASTRIP_L1B_L1C_GEOM_QI"/>
 *         &lt;element name="Radiometric_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_RADIOMETRIC_L1PRODUCT_QUALITY_ASSESSMENT"/>
 *         &lt;element name="DEGRADED_ANC_DATA_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
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
@XmlType(name = "A_QUALITY_INDICATORS_INFO_DSL1B_DSL1C", propOrder = {
    "geometricInfo",
    "radiometricInfo",
    "degradedancdatapercentage"
})
public class AQUALITYINDICATORSINFODSL1BDSL1C {

    @XmlElement(name = "Geometric_Info", required = true)
    protected ADATASTRIPL1BL1CGEOMQI geometricInfo;
    @XmlElement(name = "Radiometric_Info", required = true)
    protected ARADIOMETRICL1PRODUCTQUALITYASSESSMENT radiometricInfo;
    @XmlElement(name = "DEGRADED_ANC_DATA_PERCENTAGE")
    protected double degradedancdatapercentage;
    @XmlAttribute(name = "metadataLevel")
    protected String metadataLevel;

    /**
     * Obtient la valeur de la propriété geometricInfo.
     * 
     * @return
     *     possible object is
     *     {@link ADATASTRIPL1BL1CGEOMQI }
     *     
     */
    public ADATASTRIPL1BL1CGEOMQI getGeometricInfo() {
        return geometricInfo;
    }

    /**
     * Définit la valeur de la propriété geometricInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ADATASTRIPL1BL1CGEOMQI }
     *     
     */
    public void setGeometricInfo(ADATASTRIPL1BL1CGEOMQI value) {
        this.geometricInfo = value;
    }

    /**
     * Obtient la valeur de la propriété radiometricInfo.
     * 
     * @return
     *     possible object is
     *     {@link ARADIOMETRICL1PRODUCTQUALITYASSESSMENT }
     *     
     */
    public ARADIOMETRICL1PRODUCTQUALITYASSESSMENT getRadiometricInfo() {
        return radiometricInfo;
    }

    /**
     * Définit la valeur de la propriété radiometricInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ARADIOMETRICL1PRODUCTQUALITYASSESSMENT }
     *     
     */
    public void setRadiometricInfo(ARADIOMETRICL1PRODUCTQUALITYASSESSMENT value) {
        this.radiometricInfo = value;
    }

    /**
     * Obtient la valeur de la propriété degradedancdatapercentage.
     * 
     */
    public double getDEGRADEDANCDATAPERCENTAGE() {
        return degradedancdatapercentage;
    }

    /**
     * Définit la valeur de la propriété degradedancdatapercentage.
     * 
     */
    public void setDEGRADEDANCDATAPERCENTAGE(double value) {
        this.degradedancdatapercentage = value;
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
