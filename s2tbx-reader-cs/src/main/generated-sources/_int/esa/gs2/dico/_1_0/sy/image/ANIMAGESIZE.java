//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.image;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * The size (row and column) of an image
 * 
 * <p>Classe Java pour AN_IMAGE_SIZE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_IMAGE_SIZE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NCOLS">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NROWS">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="1"/>
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
@XmlType(name = "AN_IMAGE_SIZE", propOrder = {
    "ncols",
    "nrows"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.ARASTERDIMENSIONSL1A.DimensionsList.Dimensions.DetectorDimensions.class,
    _int.esa.gs2.dico._1_0.pdgs.dimap.ARASTERDIMENSIONS.DimensionsList.Dimensions.DetectorDimensions.class
})
public class ANIMAGESIZE {

    @XmlElement(name = "NCOLS")
    protected int ncols;
    @XmlElement(name = "NROWS")
    protected int nrows;

    /**
     * Obtient la valeur de la propriété ncols.
     * 
     */
    public int getNCOLS() {
        return ncols;
    }

    /**
     * Définit la valeur de la propriété ncols.
     * 
     */
    public void setNCOLS(int value) {
        this.ncols = value;
    }

    /**
     * Obtient la valeur de la propriété nrows.
     * 
     */
    public int getNROWS() {
        return nrows;
    }

    /**
     * Définit la valeur de la propriété nrows.
     * 
     */
    public void setNROWS(int value) {
        this.nrows = value;
    }

}
