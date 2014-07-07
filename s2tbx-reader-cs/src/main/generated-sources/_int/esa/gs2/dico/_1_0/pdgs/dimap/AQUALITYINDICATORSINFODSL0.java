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
 * <p>Classe Java pour A_QUALITY_INDICATORS_INFO_DSL0 complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_QUALITY_INDICATORS_INFO_DSL0">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Geometric_Info">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATASTRIP_COMMON_GEOM_QI">
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Quicklook_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_QUICKLOOK_DESCRIPTOR" minOccurs="0"/>
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
@XmlType(name = "A_QUALITY_INDICATORS_INFO_DSL0", propOrder = {
    "geometricInfo",
    "quicklookInfo"
})
public class AQUALITYINDICATORSINFODSL0 {

    @XmlElement(name = "Geometric_Info", required = true)
    protected AQUALITYINDICATORSINFODSL0 .GeometricInfo geometricInfo;
    @XmlElement(name = "Quicklook_Info")
    protected AQUICKLOOKDESCRIPTOR quicklookInfo;
    @XmlAttribute(name = "metadataLevel")
    protected String metadataLevel;

    /**
     * Obtient la valeur de la propriété geometricInfo.
     * 
     * @return
     *     possible object is
     *     {@link AQUALITYINDICATORSINFODSL0 .GeometricInfo }
     *     
     */
    public AQUALITYINDICATORSINFODSL0 .GeometricInfo getGeometricInfo() {
        return geometricInfo;
    }

    /**
     * Définit la valeur de la propriété geometricInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AQUALITYINDICATORSINFODSL0 .GeometricInfo }
     *     
     */
    public void setGeometricInfo(AQUALITYINDICATORSINFODSL0 .GeometricInfo value) {
        this.geometricInfo = value;
    }

    /**
     * Obtient la valeur de la propriété quicklookInfo.
     * 
     * @return
     *     possible object is
     *     {@link AQUICKLOOKDESCRIPTOR }
     *     
     */
    public AQUICKLOOKDESCRIPTOR getQuicklookInfo() {
        return quicklookInfo;
    }

    /**
     * Définit la valeur de la propriété quicklookInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link AQUICKLOOKDESCRIPTOR }
     *     
     */
    public void setQuicklookInfo(AQUICKLOOKDESCRIPTOR value) {
        this.quicklookInfo = value;
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


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATASTRIP_COMMON_GEOM_QI">
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GeometricInfo
        extends ADATASTRIPCOMMONGEOMQI
    {


    }

}
