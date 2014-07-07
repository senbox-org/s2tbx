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
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.sy.geographical.AGMLPOLYGON2D;


/**
 * <p>Classe Java pour AN_INIT_LOC_INV_PRODUCT_FOOTPRINT complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_INIT_LOC_INV_PRODUCT_FOOTPRINT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Product_Footprint">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Global_Footprint" type="{http://gs2.esa.int/DICO/1.0/SY/geographical/}A_GML_POLYGON_2D"/>
 *                   &lt;element name="Unitary_Footprint_List" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Unitary_Footprint" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="Footprint" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;extension base="{http://gs2.esa.int/DICO/1.0/SY/geographical/}A_GML_POLYGON_2D">
 *                                               &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
 *                                             &lt;/extension>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                     &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
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
@XmlType(name = "AN_INIT_LOC_INV_PRODUCT_FOOTPRINT", propOrder = {
    "productFootprint",
    "rastercstype",
    "pixelorigin"
})
public class ANINITLOCINVPRODUCTFOOTPRINT {

    @XmlElement(name = "Product_Footprint", required = true)
    protected ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint productFootprint;
    @XmlElement(name = "RASTER_CS_TYPE", required = true)
    protected String rastercstype;
    @XmlElement(name = "PIXEL_ORIGIN")
    protected int pixelorigin;

    /**
     * Obtient la valeur de la propriété productFootprint.
     * 
     * @return
     *     possible object is
     *     {@link ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint }
     *     
     */
    public ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint getProductFootprint() {
        return productFootprint;
    }

    /**
     * Définit la valeur de la propriété productFootprint.
     * 
     * @param value
     *     allowed object is
     *     {@link ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint }
     *     
     */
    public void setProductFootprint(ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint value) {
        this.productFootprint = value;
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
     *         &lt;element name="Global_Footprint" type="{http://gs2.esa.int/DICO/1.0/SY/geographical/}A_GML_POLYGON_2D"/>
     *         &lt;element name="Unitary_Footprint_List" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Unitary_Footprint" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="Footprint" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;extension base="{http://gs2.esa.int/DICO/1.0/SY/geographical/}A_GML_POLYGON_2D">
     *                                     &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
     *                                   &lt;/extension>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                           &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
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
        "globalFootprint",
        "unitaryFootprintList"
    })
    public static class ProductFootprint {

        @XmlElement(name = "Global_Footprint", required = true)
        protected AGMLPOLYGON2D globalFootprint;
        @XmlElement(name = "Unitary_Footprint_List")
        protected ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList unitaryFootprintList;

        /**
         * Obtient la valeur de la propriété globalFootprint.
         * 
         * @return
         *     possible object is
         *     {@link AGMLPOLYGON2D }
         *     
         */
        public AGMLPOLYGON2D getGlobalFootprint() {
            return globalFootprint;
        }

        /**
         * Définit la valeur de la propriété globalFootprint.
         * 
         * @param value
         *     allowed object is
         *     {@link AGMLPOLYGON2D }
         *     
         */
        public void setGlobalFootprint(AGMLPOLYGON2D value) {
            this.globalFootprint = value;
        }

        /**
         * Obtient la valeur de la propriété unitaryFootprintList.
         * 
         * @return
         *     possible object is
         *     {@link ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList }
         *     
         */
        public ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList getUnitaryFootprintList() {
            return unitaryFootprintList;
        }

        /**
         * Définit la valeur de la propriété unitaryFootprintList.
         * 
         * @param value
         *     allowed object is
         *     {@link ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList }
         *     
         */
        public void setUnitaryFootprintList(ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList value) {
            this.unitaryFootprintList = value;
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
         *         &lt;element name="Unitary_Footprint" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="Footprint" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;extension base="{http://gs2.esa.int/DICO/1.0/SY/geographical/}A_GML_POLYGON_2D">
         *                           &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
         *                         &lt;/extension>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *                 &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
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
            "unitaryFootprint"
        })
        public static class UnitaryFootprintList {

            @XmlElement(name = "Unitary_Footprint", required = true)
            protected List<ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList.UnitaryFootprint> unitaryFootprint;

            /**
             * Gets the value of the unitaryFootprint property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the unitaryFootprint property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getUnitaryFootprint().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList.UnitaryFootprint }
             * 
             * 
             */
            public List<ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList.UnitaryFootprint> getUnitaryFootprint() {
                if (unitaryFootprint == null) {
                    unitaryFootprint = new ArrayList<ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList.UnitaryFootprint>();
                }
                return this.unitaryFootprint;
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
             *         &lt;element name="Footprint" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/SY/geographical/}A_GML_POLYGON_2D">
             *                 &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
             *               &lt;/extension>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *       &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
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
            public static class UnitaryFootprint {

                @XmlElement(name = "Footprint", required = true)
                protected List<ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList.UnitaryFootprint.Footprint> footprint;
                @XmlAttribute(name = "bandId", required = true)
                protected String bandId;

                /**
                 * Gets the value of the footprint property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the footprint property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getFootprint().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList.UnitaryFootprint.Footprint }
                 * 
                 * 
                 */
                public List<ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList.UnitaryFootprint.Footprint> getFootprint() {
                    if (footprint == null) {
                        footprint = new ArrayList<ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList.UnitaryFootprint.Footprint>();
                    }
                    return this.footprint;
                }

                /**
                 * Obtient la valeur de la propriété bandId.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getBandId() {
                    return bandId;
                }

                /**
                 * Définit la valeur de la propriété bandId.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setBandId(String value) {
                    this.bandId = value;
                }


                /**
                 * <p>Classe Java pour anonymous complex type.
                 * 
                 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/SY/geographical/}A_GML_POLYGON_2D">
                 *       &lt;attribute name="detectorId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
                 *     &lt;/extension>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class Footprint
                    extends AGMLPOLYGON2D
                {

                    @XmlAttribute(name = "detectorId", required = true)
                    protected String detectorId;

                    /**
                     * Obtient la valeur de la propriété detectorId.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getDetectorId() {
                        return detectorId;
                    }

                    /**
                     * Définit la valeur de la propriété detectorId.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setDetectorId(String value) {
                        this.detectorId = value;
                    }

                }

            }

        }

    }

}
