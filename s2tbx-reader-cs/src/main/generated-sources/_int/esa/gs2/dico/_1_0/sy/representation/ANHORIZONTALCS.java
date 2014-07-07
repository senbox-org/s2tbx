//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.representation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Horizontal coordinate reference system
 * 
 * <p>Classe Java pour AN_HORIZONTAL_CS complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_HORIZONTAL_CS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HORIZONTAL_CS_TYPE" type="{http://gs2.esa.int/DICO/1.0/SY/representation/}AN_HORIZONTAL_CS_TYPES"/>
 *         &lt;element name="HORIZONTAL_CS_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HORIZONTAL_CS_CODE" type="{http://gs2.esa.int/DICO/1.0/SY/representation/}AN_EPSG_CODE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_HORIZONTAL_CS", propOrder = {
    "horizontalcstype",
    "horizontalcsname",
    "horizontalcscode"
})
public class ANHORIZONTALCS {

    @XmlElement(name = "HORIZONTAL_CS_TYPE", required = true)
    protected ANHORIZONTALCSTYPES horizontalcstype;
    @XmlElement(name = "HORIZONTAL_CS_NAME", required = true)
    protected String horizontalcsname;
    @XmlElement(name = "HORIZONTAL_CS_CODE", required = true)
    protected String horizontalcscode;

    /**
     * Obtient la valeur de la propriété horizontalcstype.
     * 
     * @return
     *     possible object is
     *     {@link ANHORIZONTALCSTYPES }
     *     
     */
    public ANHORIZONTALCSTYPES getHORIZONTALCSTYPE() {
        return horizontalcstype;
    }

    /**
     * Définit la valeur de la propriété horizontalcstype.
     * 
     * @param value
     *     allowed object is
     *     {@link ANHORIZONTALCSTYPES }
     *     
     */
    public void setHORIZONTALCSTYPE(ANHORIZONTALCSTYPES value) {
        this.horizontalcstype = value;
    }

    /**
     * Obtient la valeur de la propriété horizontalcsname.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHORIZONTALCSNAME() {
        return horizontalcsname;
    }

    /**
     * Définit la valeur de la propriété horizontalcsname.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHORIZONTALCSNAME(String value) {
        this.horizontalcsname = value;
    }

    /**
     * Obtient la valeur de la propriété horizontalcscode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHORIZONTALCSCODE() {
        return horizontalcscode;
    }

    /**
     * Définit la valeur de la propriété horizontalcscode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHORIZONTALCSCODE(String value) {
        this.horizontalcscode = value;
    }

}
