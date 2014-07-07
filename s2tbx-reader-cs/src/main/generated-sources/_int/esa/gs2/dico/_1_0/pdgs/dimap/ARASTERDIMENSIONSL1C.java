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
 * <p>Classe Java pour A_RASTER_DIMENSIONS_L1C complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_RASTER_DIMENSIONS_L1C">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NBANDS">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="1"/>
 *               &lt;maxInclusive value="13"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NBITS">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;enumeration value="8"/>
 *               &lt;enumeration value="16"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="COMPRESSION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="NONE"/>
 *               &lt;enumeration value="LOSSY"/>
 *               &lt;enumeration value="LOSSLESS"/>
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
@XmlType(name = "A_RASTER_DIMENSIONS_L1C", propOrder = {
    "nbands",
    "nbits",
    "compression"
})
public class ARASTERDIMENSIONSL1C {

    @XmlElement(name = "NBANDS")
    protected int nbands;
    @XmlElement(name = "NBITS")
    protected int nbits;
    @XmlElement(name = "COMPRESSION", required = true)
    protected String compression;

    /**
     * Obtient la valeur de la propriété nbands.
     * 
     */
    public int getNBANDS() {
        return nbands;
    }

    /**
     * Définit la valeur de la propriété nbands.
     * 
     */
    public void setNBANDS(int value) {
        this.nbands = value;
    }

    /**
     * Obtient la valeur de la propriété nbits.
     * 
     */
    public int getNBITS() {
        return nbits;
    }

    /**
     * Définit la valeur de la propriété nbits.
     * 
     */
    public void setNBITS(int value) {
        this.nbits = value;
    }

    /**
     * Obtient la valeur de la propriété compression.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOMPRESSION() {
        return compression;
    }

    /**
     * Définit la valeur de la propriété compression.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOMPRESSION(String value) {
        this.compression = value;
    }

}
