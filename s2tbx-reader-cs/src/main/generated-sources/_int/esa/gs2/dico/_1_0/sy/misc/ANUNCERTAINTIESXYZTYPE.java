//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.misc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * X,Y,Z uncertainties 
 * 
 * <p>Classe Java pour AN_UNCERTAINTIES_XYZ_TYPE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_UNCERTAINTIES_XYZ_TYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="X" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POLYNOMIAL_MODEL" minOccurs="0"/>
 *         &lt;element name="Y" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POLYNOMIAL_MODEL" minOccurs="0"/>
 *         &lt;element name="Z" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POLYNOMIAL_MODEL" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_UNCERTAINTIES_XYZ_TYPE", propOrder = {
    "x",
    "y",
    "z"
})
public class ANUNCERTAINTIESXYZTYPE {

    @XmlElement(name = "X")
    protected APOLYNOMIALMODEL x;
    @XmlElement(name = "Y")
    protected APOLYNOMIALMODEL y;
    @XmlElement(name = "Z")
    protected APOLYNOMIALMODEL z;

    /**
     * Obtient la valeur de la propriété x.
     * 
     * @return
     *     possible object is
     *     {@link APOLYNOMIALMODEL }
     *     
     */
    public APOLYNOMIALMODEL getX() {
        return x;
    }

    /**
     * Définit la valeur de la propriété x.
     * 
     * @param value
     *     allowed object is
     *     {@link APOLYNOMIALMODEL }
     *     
     */
    public void setX(APOLYNOMIALMODEL value) {
        this.x = value;
    }

    /**
     * Obtient la valeur de la propriété y.
     * 
     * @return
     *     possible object is
     *     {@link APOLYNOMIALMODEL }
     *     
     */
    public APOLYNOMIALMODEL getY() {
        return y;
    }

    /**
     * Définit la valeur de la propriété y.
     * 
     * @param value
     *     allowed object is
     *     {@link APOLYNOMIALMODEL }
     *     
     */
    public void setY(APOLYNOMIALMODEL value) {
        this.y = value;
    }

    /**
     * Obtient la valeur de la propriété z.
     * 
     * @return
     *     possible object is
     *     {@link APOLYNOMIALMODEL }
     *     
     */
    public APOLYNOMIALMODEL getZ() {
        return z;
    }

    /**
     * Définit la valeur de la propriété z.
     * 
     * @param value
     *     allowed object is
     *     {@link APOLYNOMIALMODEL }
     *     
     */
    public void setZ(APOLYNOMIALMODEL value) {
        this.z = value;
    }

}
