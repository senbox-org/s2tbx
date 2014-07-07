//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour A_GRANULE_COMMON_IMG_CONTENT_QI complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GRANULE_COMMON_IMG_CONTENT_QI">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CLOUDY_PIXEL_PERCENTAGE">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/PDGS/dimap/>A_CLOUDY_PIXEL_PERCENTAGE">
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DEGRADED_MSI_DATA_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_GRANULE_COMMON_IMG_CONTENT_QI", propOrder = {
    "cloudypixelpercentage",
    "degradedmsidatapercentage"
})
public class AGRANULECOMMONIMGCONTENTQI {

    @XmlElement(name = "CLOUDY_PIXEL_PERCENTAGE", required = true)
    protected AGRANULECOMMONIMGCONTENTQI.CLOUDYPIXELPERCENTAGE cloudypixelpercentage;
    @XmlElement(name = "DEGRADED_MSI_DATA_PERCENTAGE")
    protected double degradedmsidatapercentage;

    /**
     * Obtient la valeur de la propriété cloudypixelpercentage.
     * 
     * @return
     *     possible object is
     *     {@link AGRANULECOMMONIMGCONTENTQI.CLOUDYPIXELPERCENTAGE }
     *     
     */
    public AGRANULECOMMONIMGCONTENTQI.CLOUDYPIXELPERCENTAGE getCLOUDYPIXELPERCENTAGE() {
        return cloudypixelpercentage;
    }

    /**
     * Définit la valeur de la propriété cloudypixelpercentage.
     * 
     * @param value
     *     allowed object is
     *     {@link AGRANULECOMMONIMGCONTENTQI.CLOUDYPIXELPERCENTAGE }
     *     
     */
    public void setCLOUDYPIXELPERCENTAGE(AGRANULECOMMONIMGCONTENTQI.CLOUDYPIXELPERCENTAGE value) {
        this.cloudypixelpercentage = value;
    }

    /**
     * Obtient la valeur de la propriété degradedmsidatapercentage.
     * 
     */
    public double getDEGRADEDMSIDATAPERCENTAGE() {
        return degradedmsidatapercentage;
    }

    /**
     * Définit la valeur de la propriété degradedmsidatapercentage.
     * 
     */
    public void setDEGRADEDMSIDATAPERCENTAGE(double value) {
        this.degradedmsidatapercentage = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/PDGS/dimap/>A_CLOUDY_PIXEL_PERCENTAGE">
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
    public static class CLOUDYPIXELPERCENTAGE {

        @XmlValue
        protected double value;

        /**
         * Quality assessement: percentage of cloudy pixels detected in the quicklook image
         * 
         */
        public double getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         * 
         */
        public void setValue(double value) {
            this.value = value;
        }

    }

}
