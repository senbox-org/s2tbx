//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.tile;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Description of a tile in a given representation
 * 
 * <p>Classe Java pour A_TILE_DESCRIPTION_S2 complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_TILE_DESCRIPTION_S2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HORIZONTAL_CS_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HORIZONTAL_CS_CODE" type="{http://gs2.esa.int/DICO/1.0/SY/representation/}AN_EPSG_CODE"/>
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
 *         &lt;element name="Geoposition" maxOccurs="3" minOccurs="3">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ULX" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                   &lt;element name="ULY" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                   &lt;element name="XDIM" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                   &lt;element name="YDIM" type="{http://www.w3.org/2001/XMLSchema}double"/>
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
@XmlType(name = "A_TILE_DESCRIPTION_S2", propOrder = {
    "horizontalcsname",
    "horizontalcscode",
    "size",
    "geoposition"
})
public class ATILEDESCRIPTIONS2 {

    @XmlElement(name = "HORIZONTAL_CS_NAME", required = true)
    protected String horizontalcsname;
    @XmlElement(name = "HORIZONTAL_CS_CODE", required = true)
    protected String horizontalcscode;
    @XmlElement(name = "Size", required = true)
    protected List<ATILEDESCRIPTIONS2 .Size> size;
    @XmlElement(name = "Geoposition", required = true)
    protected List<ATILEDESCRIPTIONS2 .Geoposition> geoposition;

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
     * {@link ATILEDESCRIPTIONS2 .Size }
     * 
     * 
     */
    public List<ATILEDESCRIPTIONS2 .Size> getSize() {
        if (size == null) {
            size = new ArrayList<ATILEDESCRIPTIONS2 .Size>();
        }
        return this.size;
    }

    /**
     * Gets the value of the geoposition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the geoposition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGeoposition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ATILEDESCRIPTIONS2 .Geoposition }
     * 
     * 
     */
    public List<ATILEDESCRIPTIONS2 .Geoposition> getGeoposition() {
        if (geoposition == null) {
            geoposition = new ArrayList<ATILEDESCRIPTIONS2 .Geoposition>();
        }
        return this.geoposition;
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
     *         &lt;element name="ULX" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *         &lt;element name="ULY" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *         &lt;element name="XDIM" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *         &lt;element name="YDIM" type="{http://www.w3.org/2001/XMLSchema}double"/>
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
        "ulx",
        "uly",
        "xdim",
        "ydim"
    })
    public static class Geoposition {

        @XmlElement(name = "ULX")
        protected double ulx;
        @XmlElement(name = "ULY")
        protected double uly;
        @XmlElement(name = "XDIM")
        protected double xdim;
        @XmlElement(name = "YDIM")
        protected double ydim;
        @XmlAttribute(name = "resolution", required = true)
        protected int resolution;

        /**
         * Obtient la valeur de la propriété ulx.
         * 
         */
        public double getULX() {
            return ulx;
        }

        /**
         * Définit la valeur de la propriété ulx.
         * 
         */
        public void setULX(double value) {
            this.ulx = value;
        }

        /**
         * Obtient la valeur de la propriété uly.
         * 
         */
        public double getULY() {
            return uly;
        }

        /**
         * Définit la valeur de la propriété uly.
         * 
         */
        public void setULY(double value) {
            this.uly = value;
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
