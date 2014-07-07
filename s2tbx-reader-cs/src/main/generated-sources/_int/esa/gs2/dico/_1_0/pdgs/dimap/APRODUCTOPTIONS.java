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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.sy.image.APHYSICALBANDNAME;
import _int.esa.gs2.dico._1_0.sy.image.APROCESSINGLEVEL;
import _int.esa.gs2.dico._1_0.sy.image.APRODUCTS2FORMAT;


/**
 * Product Download Options according to ngEO download options
 * 
 * <p>Classe Java pour A_PRODUCT_OPTIONS complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_PRODUCT_OPTIONS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Area_Of_Interest" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_AREA_OF_INTEREST"/>
 *         &lt;element name="FULL_SWATH_DATATAKE " type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="PREVIEW_IMAGE" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Band_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="BAND_NAME" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_PHYSICAL_BAND_NAME" maxOccurs="13"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="METADATA_LEVEL">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Brief"/>
 *               &lt;enumeration value="Standard"/>
 *               &lt;enumeration value="Expertise"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Aux_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="aux" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;choice>
 *                               &lt;element name="GIPP" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                               &lt;element name="IERS" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                             &lt;/choice>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="productLevel" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_PROCESSING_LEVEL" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PRODUCT_FORMAT" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_PRODUCT_S2_FORMAT"/>
 *         &lt;element name="AGGREGATION_FLAG" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_VALIDITY_FLAG"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_PRODUCT_OPTIONS", propOrder = {
    "areaOfInterest",
    "fullswathdatatake0020",
    "previewimage",
    "bandList",
    "metadatalevel",
    "auxList",
    "productformat",
    "aggregationflag"
})
public class APRODUCTOPTIONS {

    @XmlElement(name = "Area_Of_Interest", required = true)
    protected ANAREAOFINTEREST areaOfInterest;
    @XmlElement(name = "FULL_SWATH_DATATAKE ")
    protected boolean fullswathdatatake0020;
    @XmlElement(name = "PREVIEW_IMAGE")
    protected boolean previewimage;
    @XmlElement(name = "Band_List", required = true)
    protected APRODUCTOPTIONS.BandList bandList;
    @XmlElement(name = "METADATA_LEVEL", required = true)
    protected String metadatalevel;
    @XmlElement(name = "Aux_List", required = true)
    protected APRODUCTOPTIONS.AuxList auxList;
    @XmlElement(name = "PRODUCT_FORMAT", required = true)
    protected APRODUCTS2FORMAT productformat;
    @XmlElement(name = "AGGREGATION_FLAG")
    protected boolean aggregationflag;

    /**
     * Obtient la valeur de la propriété areaOfInterest.
     * 
     * @return
     *     possible object is
     *     {@link ANAREAOFINTEREST }
     *     
     */
    public ANAREAOFINTEREST getAreaOfInterest() {
        return areaOfInterest;
    }

    /**
     * Définit la valeur de la propriété areaOfInterest.
     * 
     * @param value
     *     allowed object is
     *     {@link ANAREAOFINTEREST }
     *     
     */
    public void setAreaOfInterest(ANAREAOFINTEREST value) {
        this.areaOfInterest = value;
    }

    /**
     * Obtient la valeur de la propriété fullswathdatatake0020.
     * 
     */
    public boolean isFULLSWATHDATATAKE_0020() {
        return fullswathdatatake0020;
    }

    /**
     * Définit la valeur de la propriété fullswathdatatake0020.
     * 
     */
    public void setFULLSWATHDATATAKE_0020(boolean value) {
        this.fullswathdatatake0020 = value;
    }

    /**
     * Obtient la valeur de la propriété previewimage.
     * 
     */
    public boolean isPREVIEWIMAGE() {
        return previewimage;
    }

    /**
     * Définit la valeur de la propriété previewimage.
     * 
     */
    public void setPREVIEWIMAGE(boolean value) {
        this.previewimage = value;
    }

    /**
     * Obtient la valeur de la propriété bandList.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTOPTIONS.BandList }
     *     
     */
    public APRODUCTOPTIONS.BandList getBandList() {
        return bandList;
    }

    /**
     * Définit la valeur de la propriété bandList.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTOPTIONS.BandList }
     *     
     */
    public void setBandList(APRODUCTOPTIONS.BandList value) {
        this.bandList = value;
    }

    /**
     * Obtient la valeur de la propriété metadatalevel.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMETADATALEVEL() {
        return metadatalevel;
    }

    /**
     * Définit la valeur de la propriété metadatalevel.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMETADATALEVEL(String value) {
        this.metadatalevel = value;
    }

    /**
     * Obtient la valeur de la propriété auxList.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTOPTIONS.AuxList }
     *     
     */
    public APRODUCTOPTIONS.AuxList getAuxList() {
        return auxList;
    }

    /**
     * Définit la valeur de la propriété auxList.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTOPTIONS.AuxList }
     *     
     */
    public void setAuxList(APRODUCTOPTIONS.AuxList value) {
        this.auxList = value;
    }

    /**
     * Obtient la valeur de la propriété productformat.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTS2FORMAT }
     *     
     */
    public APRODUCTS2FORMAT getPRODUCTFORMAT() {
        return productformat;
    }

    /**
     * Définit la valeur de la propriété productformat.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTS2FORMAT }
     *     
     */
    public void setPRODUCTFORMAT(APRODUCTS2FORMAT value) {
        this.productformat = value;
    }

    /**
     * Obtient la valeur de la propriété aggregationflag.
     * 
     */
    public boolean isAGGREGATIONFLAG() {
        return aggregationflag;
    }

    /**
     * Définit la valeur de la propriété aggregationflag.
     * 
     */
    public void setAGGREGATIONFLAG(boolean value) {
        this.aggregationflag = value;
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
     *         &lt;element name="aux" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;choice>
     *                     &lt;element name="GIPP" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *                     &lt;element name="IERS" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *                   &lt;/choice>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="productLevel" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_PROCESSING_LEVEL" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "aux"
    })
    public static class AuxList {

        @XmlElement(required = true)
        protected List<APRODUCTOPTIONS.AuxList.Aux> aux;
        @XmlAttribute(name = "productLevel", required = true)
        protected APROCESSINGLEVEL productLevel;

        /**
         * Gets the value of the aux property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the aux property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAux().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link APRODUCTOPTIONS.AuxList.Aux }
         * 
         * 
         */
        public List<APRODUCTOPTIONS.AuxList.Aux> getAux() {
            if (aux == null) {
                aux = new ArrayList<APRODUCTOPTIONS.AuxList.Aux>();
            }
            return this.aux;
        }

        /**
         * Obtient la valeur de la propriété productLevel.
         * 
         * @return
         *     possible object is
         *     {@link APROCESSINGLEVEL }
         *     
         */
        public APROCESSINGLEVEL getProductLevel() {
            return productLevel;
        }

        /**
         * Définit la valeur de la propriété productLevel.
         * 
         * @param value
         *     allowed object is
         *     {@link APROCESSINGLEVEL }
         *     
         */
        public void setProductLevel(APROCESSINGLEVEL value) {
            this.productLevel = value;
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
         *         &lt;choice>
         *           &lt;element name="GIPP" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
         *           &lt;element name="IERS" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
         *         &lt;/choice>
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
            "gipp",
            "iers"
        })
        public static class Aux {

            @XmlElement(name = "GIPP")
            @XmlSchemaType(name = "anyURI")
            protected String gipp;
            @XmlElement(name = "IERS")
            @XmlSchemaType(name = "anyURI")
            protected String iers;

            /**
             * Obtient la valeur de la propriété gipp.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getGIPP() {
                return gipp;
            }

            /**
             * Définit la valeur de la propriété gipp.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setGIPP(String value) {
                this.gipp = value;
            }

            /**
             * Obtient la valeur de la propriété iers.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getIERS() {
                return iers;
            }

            /**
             * Définit la valeur de la propriété iers.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setIERS(String value) {
                this.iers = value;
            }

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
     *         &lt;element name="BAND_NAME" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_PHYSICAL_BAND_NAME" maxOccurs="13"/>
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
        "bandname"
    })
    public static class BandList {

        @XmlElement(name = "BAND_NAME", required = true)
        protected List<APHYSICALBANDNAME> bandname;

        /**
         * Gets the value of the bandname property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the bandname property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getBANDNAME().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link APHYSICALBANDNAME }
         * 
         * 
         */
        public List<APHYSICALBANDNAME> getBANDNAME() {
            if (bandname == null) {
                bandname = new ArrayList<APHYSICALBANDNAME>();
            }
            return this.bandname;
        }

    }

}
