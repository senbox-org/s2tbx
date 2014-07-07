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


/**
 * Technical quality assessment: A percentage of degraded MSI and ancillary data over the product is provided (consolidated from the metadata expressed at granule-level)
 * 
 * <p>Classe Java pour A_DATA_CONTENT_QUALITY complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DATA_CONTENT_QUALITY">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DEGRADED_ANC_DATA_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_PERCENTAGE"/>
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
@XmlType(name = "A_DATA_CONTENT_QUALITY", propOrder = {
    "degradedancdatapercentage",
    "degradedmsidatapercentage"
})
public class ADATACONTENTQUALITY {

    @XmlElement(name = "DEGRADED_ANC_DATA_PERCENTAGE")
    protected double degradedancdatapercentage;
    @XmlElement(name = "DEGRADED_MSI_DATA_PERCENTAGE")
    protected double degradedmsidatapercentage;

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

}
