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
 * Dataset identification
 * 
 * <p>Classe Java pour A_DATASET_IDENTIFICATION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DATASET_IDENTIFICATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DATASET_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="COPYRIGHT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_DATASET_IDENTIFICATION", propOrder = {
    "datasetname",
    "copyright"
})
public class ADATASETIDENTIFICATION {

    @XmlElement(name = "DATASET_NAME", required = true)
    protected String datasetname;
    @XmlElement(name = "COPYRIGHT", required = true)
    protected String copyright;

    /**
     * Obtient la valeur de la propriété datasetname.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDATASETNAME() {
        return datasetname;
    }

    /**
     * Définit la valeur de la propriété datasetname.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDATASETNAME(String value) {
        this.datasetname = value;
    }

    /**
     * Obtient la valeur de la propriété copyright.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOPYRIGHT() {
        return copyright;
    }

    /**
     * Définit la valeur de la propriété copyright.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOPYRIGHT(String value) {
        this.copyright = value;
    }

}
