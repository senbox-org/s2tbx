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
 * Metadata file identification
 * 
 * <p>Classe Java pour A_METADATA_IDENTIFICATION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_METADATA_IDENTIFICATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="METADATA_PROFILE" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_METADATA_PROFILE"/>
 *         &lt;element name="METADATA_FORMAT" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_METADATA_FORMAT"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_METADATA_IDENTIFICATION", propOrder = {
    "metadataprofile",
    "metadataformat"
})
public class AMETADATAIDENTIFICATION {

    @XmlElement(name = "METADATA_PROFILE", required = true)
    protected String metadataprofile;
    @XmlElement(name = "METADATA_FORMAT", required = true)
    protected AMETADATAFORMAT metadataformat;

    /**
     * Obtient la valeur de la propriété metadataprofile.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMETADATAPROFILE() {
        return metadataprofile;
    }

    /**
     * Définit la valeur de la propriété metadataprofile.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMETADATAPROFILE(String value) {
        this.metadataprofile = value;
    }

    /**
     * Obtient la valeur de la propriété metadataformat.
     * 
     * @return
     *     possible object is
     *     {@link AMETADATAFORMAT }
     *     
     */
    public AMETADATAFORMAT getMETADATAFORMAT() {
        return metadataformat;
    }

    /**
     * Définit la valeur de la propriété metadataformat.
     * 
     * @param value
     *     allowed object is
     *     {@link AMETADATAFORMAT }
     *     
     */
    public void setMETADATAFORMAT(AMETADATAFORMAT value) {
        this.metadataformat = value;
    }

}
