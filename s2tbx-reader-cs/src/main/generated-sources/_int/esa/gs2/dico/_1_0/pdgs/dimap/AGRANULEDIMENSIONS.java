//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Granule dimensions in terms of NROW and NCOL
 * 
 * <p>Classe Java pour A_GRANULE_DIMENSIONS complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GRANULE_DIMENSIONS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Size" maxOccurs="3" minOccurs="3">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="NROWS">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                         &lt;minInclusive value="1"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="NCOLS">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                         &lt;minInclusive value="1"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="resolution" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_RESOLUTION" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
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
@XmlType(name = "A_GRANULE_DIMENSIONS", propOrder = {
    "size"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGEOMETRICINFO.GranuleDimensions.class
})
public class AGRANULEDIMENSIONS {

    @XmlElement(name = "Size", required = true)
    protected List<AGRANULEDIMENSIONS.Size> size;

    /**
     * Gets the value of the size property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the size property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSize().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AGRANULEDIMENSIONS.Size }
     * 
     * 
     */
    public List<AGRANULEDIMENSIONS.Size> getSize() {
        if (size == null) {
            size = new ArrayList<AGRANULEDIMENSIONS.Size>();
        }
        return this.size;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
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
     *       &lt;/sequence>
     *       &lt;attribute name="resolution" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_RESOLUTION" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "nrows",
        "ncols"
    })
    public static class Size {

        @XmlElement(name = "NROWS")
        protected int nrows;
        @XmlElement(name = "NCOLS")
        protected int ncols;
        @XmlAttribute(name = "resolution", required = true)
        protected int resolution;

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
         * Obtient la valeur de la propriété resolution.
         * 
         */
        public int getResolution() {
            return resolution;
        }

        /**
         * Définit la valeur de la propriété resolution.
         * 
         */
        public void setResolution(int value) {
            this.resolution = value;
        }

    }

}
