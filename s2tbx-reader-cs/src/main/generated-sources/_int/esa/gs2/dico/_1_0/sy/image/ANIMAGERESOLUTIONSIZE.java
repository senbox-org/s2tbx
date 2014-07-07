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
import javax.xml.bind.annotation.XmlType;


/**
 * The size (row and column) of each resolution band of an image
 * 
 * <p>Classe Java pour AN_IMAGE_RESOLUTION_SIZE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_IMAGE_RESOLUTION_SIZE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NROWS">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NCOLS">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="XDIM" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="YDIM" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_IMAGE_RESOLUTION_SIZE", propOrder = {
    "nrows",
    "ncols",
    "xdim",
    "ydim"
})
public class ANIMAGERESOLUTIONSIZE {

    @XmlElement(name = "NROWS")
    protected int nrows;
    @XmlElement(name = "NCOLS")
    protected int ncols;
    @XmlElement(name = "XDIM")
    protected double xdim;
    @XmlElement(name = "YDIM")
    protected double ydim;

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
     * Obtient la valeur de la propriété xdim.
     * 
     */
    public double getXDIM() {
        return xdim;
    }

    /**
     * Définit la valeur de la propriété xdim.
     * 
     */
    public void setXDIM(double value) {
        this.xdim = value;
    }

    /**
     * Obtient la valeur de la propriété ydim.
     * 
     */
    public double getYDIM() {
        return ydim;
    }

    /**
     * Définit la valeur de la propriété ydim.
     * 
     */
    public void setYDIM(double value) {
        this.ydim = value;
    }

}
