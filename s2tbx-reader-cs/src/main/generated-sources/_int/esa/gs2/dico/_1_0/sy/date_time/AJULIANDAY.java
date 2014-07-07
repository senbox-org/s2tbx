//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.date_time;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Jour julien CNES
 * 
 * <p>Classe Java pour A_JULIAN_DAY complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_JULIAN_DAY">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="D">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="S">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="100000"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_JULIAN_DAY", propOrder = {
    "d",
    "s"
})
public class AJULIANDAY {

    @XmlElement(name = "D")
    protected int d;
    @XmlElement(name = "S")
    protected double s;

    /**
     * Obtient la valeur de la propriété d.
     * 
     */
    public int getD() {
        return d;
    }

    /**
     * Définit la valeur de la propriété d.
     * 
     */
    public void setD(int value) {
        this.d = value;
    }

    /**
     * Obtient la valeur de la propriété s.
     * 
     */
    public double getS() {
        return s;
    }

    /**
     * Définit la valeur de la propriété s.
     * 
     */
    public void setS(double value) {
        this.s = value;
    }

}
