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
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;


/**
 * Description of a tile with its bounding box in a given representation
 * 
 * <p>Classe Java pour A_TILE_DESCRIPTION_GIPP complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_TILE_DESCRIPTION_GIPP">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TILE_IDENTIFIER" type="{http://gs2.esa.int/DICO/1.0/SY/tile/}A_TILE_IDENTIFIER"/>
 *         &lt;element name="HORIZONTAL_CS_CODE" type="{http://gs2.esa.int/DICO/1.0/SY/representation/}AN_EPSG_CODE"/>
 *         &lt;element name="UNIT">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="m"/>
 *               &lt;enumeration value="deg"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ULX" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="ULY" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="TILE_SIZE_LIST">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="TILE_SIZE" maxOccurs="3" minOccurs="3">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="NROWS">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                   &lt;minInclusive value="1"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="NCOLS">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                   &lt;minInclusive value="1"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="XDIM" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                             &lt;element name="YDIM" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="resolution" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_RESOLUTION" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="B_BOX" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_8_DOUBLE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_TILE_DESCRIPTION_GIPP", propOrder = {
    "tileidentifier",
    "horizontalcscode",
    "unit",
    "ulx",
    "uly",
    "tilesizelist",
    "bbox"
})
public class ATILEDESCRIPTIONGIPP {

    @XmlElement(name = "TILE_IDENTIFIER", required = true)
    protected String tileidentifier;
    @XmlElement(name = "HORIZONTAL_CS_CODE", required = true)
    protected String horizontalcscode;
    @XmlElement(name = "UNIT", required = true)
    protected String unit;
    @XmlElement(name = "ULX")
    protected double ulx;
    @XmlElement(name = "ULY")
    protected double uly;
    @XmlElement(name = "TILE_SIZE_LIST", required = true)
    protected ATILEDESCRIPTIONGIPP.TILESIZELIST tilesizelist;
    @XmlList
    @XmlElement(name = "B_BOX", type = Double.class)
    protected List<Double> bbox;

    /**
     * Obtient la valeur de la propriété tileidentifier.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTILEIDENTIFIER() {
        return tileidentifier;
    }

    /**
     * Définit la valeur de la propriété tileidentifier.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTILEIDENTIFIER(String value) {
        this.tileidentifier = value;
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
     * Obtient la valeur de la propriété unit.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUNIT() {
        return unit;
    }

    /**
     * Définit la valeur de la propriété unit.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUNIT(String value) {
        this.unit = value;
    }

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
     * Obtient la valeur de la propriété tilesizelist.
     * 
     * @return
     *     possible object is
     *     {@link ATILEDESCRIPTIONGIPP.TILESIZELIST }
     *     
     */
    public ATILEDESCRIPTIONGIPP.TILESIZELIST getTILESIZELIST() {
        return tilesizelist;
    }

    /**
     * Définit la valeur de la propriété tilesizelist.
     * 
     * @param value
     *     allowed object is
     *     {@link ATILEDESCRIPTIONGIPP.TILESIZELIST }
     *     
     */
    public void setTILESIZELIST(ATILEDESCRIPTIONGIPP.TILESIZELIST value) {
        this.tilesizelist = value;
    }

    /**
     * Gets the value of the bbox property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bbox property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBBOX().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getBBOX() {
        if (bbox == null) {
            bbox = new ArrayList<Double>();
        }
        return this.bbox;
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
     *         &lt;element name="TILE_SIZE" maxOccurs="3" minOccurs="3">
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
    @XmlType(name = "", propOrder = {
        "tilesize"
    })
    public static class TILESIZELIST {

        @XmlElement(name = "TILE_SIZE", required = true)
        protected List<ATILEDESCRIPTIONGIPP.TILESIZELIST.TILESIZE> tilesize;

        /**
         * Gets the value of the tilesize property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the tilesize property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTILESIZE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ATILEDESCRIPTIONGIPP.TILESIZELIST.TILESIZE }
         * 
         * 
         */
        public List<ATILEDESCRIPTIONGIPP.TILESIZELIST.TILESIZE> getTILESIZE() {
            if (tilesize == null) {
                tilesize = new ArrayList<ATILEDESCRIPTIONGIPP.TILESIZELIST.TILESIZE>();
            }
            return this.tilesize;
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
            "nrows",
            "ncols",
            "xdim",
            "ydim"
        })
        public static class TILESIZE {

            @XmlElement(name = "NROWS")
            protected int nrows;
            @XmlElement(name = "NCOLS")
            protected int ncols;
            @XmlElement(name = "XDIM")
            protected double xdim;
            @XmlElement(name = "YDIM")
            protected double ydim;
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

    }

}
