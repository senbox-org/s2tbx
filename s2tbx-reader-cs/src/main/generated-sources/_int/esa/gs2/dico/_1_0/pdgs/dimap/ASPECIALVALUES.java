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
 * <p>Classe Java pour A_SPECIAL_VALUES complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_SPECIAL_VALUES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SPECIAL_VALUE_TEXT">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="NODATA"/>
 *               &lt;enumeration value="SATURATED"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="SPECIAL_VALUE_INDEX" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_SPECIAL_VALUES", propOrder = {
    "specialvaluetext",
    "specialvalueindex"
})
public class ASPECIALVALUES {

    @XmlElement(name = "SPECIAL_VALUE_TEXT", required = true)
    protected String specialvaluetext;
    @XmlElement(name = "SPECIAL_VALUE_INDEX")
    protected int specialvalueindex;

    /**
     * Obtient la valeur de la propriété specialvaluetext.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSPECIALVALUETEXT() {
        return specialvaluetext;
    }

    /**
     * Définit la valeur de la propriété specialvaluetext.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSPECIALVALUETEXT(String value) {
        this.specialvaluetext = value;
    }

    /**
     * Obtient la valeur de la propriété specialvalueindex.
     * 
     */
    public int getSPECIALVALUEINDEX() {
        return specialvalueindex;
    }

    /**
     * Définit la valeur de la propriété specialvalueindex.
     * 
     */
    public void setSPECIALVALUEINDEX(int value) {
        this.specialvalueindex = value;
    }

}
