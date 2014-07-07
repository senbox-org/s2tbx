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
 * Describes a position in an array (raw_index, column_index)
 * 
 * <p>Classe Java pour A_POSITION_IN_ARRAY complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_POSITION_IN_ARRAY">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ROW_INDEX">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="COL_INDEX">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
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
@XmlType(name = "A_POSITION_IN_ARRAY", propOrder = {
    "rowindex",
    "colindex"
})
public class APOSITIONINARRAY {

    @XmlElement(name = "ROW_INDEX")
    protected int rowindex;
    @XmlElement(name = "COL_INDEX")
    protected int colindex;

    /**
     * Obtient la valeur de la propriété rowindex.
     * 
     */
    public int getROWINDEX() {
        return rowindex;
    }

    /**
     * Définit la valeur de la propriété rowindex.
     * 
     */
    public void setROWINDEX(int value) {
        this.rowindex = value;
    }

    /**
     * Obtient la valeur de la propriété colindex.
     * 
     */
    public int getCOLINDEX() {
        return colindex;
    }

    /**
     * Définit la valeur de la propriété colindex.
     * 
     */
    public void setCOLINDEX(int value) {
        this.colindex = value;
    }

}
