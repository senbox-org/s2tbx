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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.sy.geographical.AGMLPOLYGON3D;


/**
 * <p>Classe Java pour A_GRANULE_FOOTPRINT complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GRANULE_FOOTPRINT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Granule_Footprint">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Footprint" type="{http://gs2.esa.int/DICO/1.0/SY/geographical/}A_GML_POLYGON_3D"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RASTER_CS_TYPE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="POINT"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PIXEL_ORIGIN">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
 *               &lt;enumeration value="1"/>
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
@XmlType(name = "A_GRANULE_FOOTPRINT", propOrder = {
    "granuleFootprint",
    "rastercstype",
    "pixelorigin"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGEOMETRICINFOGRL0 .GranuleFootprint.class,
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGEOMETRICINFO.GranuleFootprint.class
})
public class AGRANULEFOOTPRINT {

    @XmlElement(name = "Granule_Footprint", required = true)
    protected AGRANULEFOOTPRINT.GranuleFootprint granuleFootprint;
    @XmlElement(name = "RASTER_CS_TYPE", required = true)
    protected String rastercstype;
    @XmlElement(name = "PIXEL_ORIGIN")
    protected int pixelorigin;

    /**
     * Obtient la valeur de la propriété granuleFootprint.
     * 
     * @return
     *     possible object is
     *     {@link AGRANULEFOOTPRINT.GranuleFootprint }
     *     
     */
    public AGRANULEFOOTPRINT.GranuleFootprint getGranuleFootprint() {
        return granuleFootprint;
    }

    /**
     * Définit la valeur de la propriété granuleFootprint.
     * 
     * @param value
     *     allowed object is
     *     {@link AGRANULEFOOTPRINT.GranuleFootprint }
     *     
     */
    public void setGranuleFootprint(AGRANULEFOOTPRINT.GranuleFootprint value) {
        this.granuleFootprint = value;
    }

    /**
     * Obtient la valeur de la propriété rastercstype.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRASTERCSTYPE() {
        return rastercstype;
    }

    /**
     * Définit la valeur de la propriété rastercstype.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRASTERCSTYPE(String value) {
        this.rastercstype = value;
    }

    /**
     * Obtient la valeur de la propriété pixelorigin.
     * 
     */
    public int getPIXELORIGIN() {
        return pixelorigin;
    }

    /**
     * Définit la valeur de la propriété pixelorigin.
     * 
     */
    public void setPIXELORIGIN(int value) {
        this.pixelorigin = value;
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
     *         &lt;element name="Footprint" type="{http://gs2.esa.int/DICO/1.0/SY/geographical/}A_GML_POLYGON_3D"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "footprint"
    })
    public static class GranuleFootprint {

        @XmlElement(name = "Footprint", required = true)
        protected AGMLPOLYGON3D footprint;

        /**
         * Obtient la valeur de la propriété footprint.
         * 
         * @return
         *     possible object is
         *     {@link AGMLPOLYGON3D }
         *     
         */
        public AGMLPOLYGON3D getFootprint() {
            return footprint;
        }

        /**
         * Définit la valeur de la propriété footprint.
         * 
         * @param value
         *     allowed object is
         *     {@link AGMLPOLYGON3D }
         *     
         */
        public void setFootprint(AGMLPOLYGON3D value) {
            this.footprint = value;
        }

    }

}
