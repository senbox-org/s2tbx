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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.image.APROCESSINGLEVEL;


/**
 * General PDGS Product Information
 * 
 * <p>Classe Java pour A_PRODUCT_INFO complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_PRODUCT_INFO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PRODUCT_START_TIME" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="PRODUCT_STOP_TIME" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="PRODUCT_URI" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="PROCESSING_LEVEL">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/image/>A_PROCESSING_LEVEL">
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PRODUCT_TYPE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="S2MSI0"/>
 *               &lt;enumeration value="S2MSI1A"/>
 *               &lt;enumeration value="S2MSI1B"/>
 *               &lt;enumeration value="S2MSI1C"/>
 *               &lt;enumeration value="S2MSI2Ap"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PROCESSING_BASELINE" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PROCESSING_BASELINE_IDENTIFICATION"/>
 *         &lt;element name="GENERATION_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_DATE_TIME"/>
 *         &lt;element name="PREVIEW_IMAGE_URL">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anyURI">
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PREVIEW_GEO_INFO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Datatake" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATATAKE_IDENTIFICATION"/>
 *         &lt;element name="Query_Options" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PRODUCT_OPTIONS"/>
 *         &lt;element name="Product_Organisation">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Granule_List" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PRODUCT_ORGANIZATION">
 *                         &lt;/extension>
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
@XmlType(name = "A_PRODUCT_INFO", propOrder = {
    "productstarttime",
    "productstoptime",
    "producturi",
    "processinglevel",
    "producttype",
    "processingbaseline",
    "generationtime",
    "previewimageurl",
    "previewgeoinfo",
    "datatake",
    "queryOptions",
    "productOrganisation"
})
public class APRODUCTINFO {

    @XmlElement(name = "PRODUCT_START_TIME", required = true)
    protected Object productstarttime;
    @XmlElement(name = "PRODUCT_STOP_TIME", required = true)
    protected Object productstoptime;
    @XmlElement(name = "PRODUCT_URI", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String producturi;
    @XmlElement(name = "PROCESSING_LEVEL", required = true)
    protected APRODUCTINFO.PROCESSINGLEVEL processinglevel;
    @XmlElement(name = "PRODUCT_TYPE", required = true)
    protected String producttype;
    @XmlElement(name = "PROCESSING_BASELINE", required = true)
    protected String processingbaseline;
    @XmlElement(name = "GENERATION_TIME", required = true)
    protected XMLGregorianCalendar generationtime;
    @XmlElement(name = "PREVIEW_IMAGE_URL", required = true)
    protected APRODUCTINFO.PREVIEWIMAGEURL previewimageurl;
    @XmlElement(name = "PREVIEW_GEO_INFO", required = true)
    protected String previewgeoinfo;
    @XmlElement(name = "Datatake", required = true)
    protected ADATATAKEIDENTIFICATION datatake;
    @XmlElement(name = "Query_Options", required = true)
    protected APRODUCTOPTIONS queryOptions;
    @XmlElement(name = "Product_Organisation", required = true)
    protected APRODUCTINFO.ProductOrganisation productOrganisation;

    /**
     * Obtient la valeur de la propriété productstarttime.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getPRODUCTSTARTTIME() {
        return productstarttime;
    }

    /**
     * Définit la valeur de la propriété productstarttime.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setPRODUCTSTARTTIME(Object value) {
        this.productstarttime = value;
    }

    /**
     * Obtient la valeur de la propriété productstoptime.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getPRODUCTSTOPTIME() {
        return productstoptime;
    }

    /**
     * Définit la valeur de la propriété productstoptime.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setPRODUCTSTOPTIME(Object value) {
        this.productstoptime = value;
    }

    /**
     * Obtient la valeur de la propriété producturi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRODUCTURI() {
        return producturi;
    }

    /**
     * Définit la valeur de la propriété producturi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRODUCTURI(String value) {
        this.producturi = value;
    }

    /**
     * Obtient la valeur de la propriété processinglevel.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTINFO.PROCESSINGLEVEL }
     *     
     */
    public APRODUCTINFO.PROCESSINGLEVEL getPROCESSINGLEVEL() {
        return processinglevel;
    }

    /**
     * Définit la valeur de la propriété processinglevel.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTINFO.PROCESSINGLEVEL }
     *     
     */
    public void setPROCESSINGLEVEL(APRODUCTINFO.PROCESSINGLEVEL value) {
        this.processinglevel = value;
    }

    /**
     * Obtient la valeur de la propriété producttype.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRODUCTTYPE() {
        return producttype;
    }

    /**
     * Définit la valeur de la propriété producttype.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRODUCTTYPE(String value) {
        this.producttype = value;
    }

    /**
     * Obtient la valeur de la propriété processingbaseline.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPROCESSINGBASELINE() {
        return processingbaseline;
    }

    /**
     * Définit la valeur de la propriété processingbaseline.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPROCESSINGBASELINE(String value) {
        this.processingbaseline = value;
    }

    /**
     * Obtient la valeur de la propriété generationtime.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getGENERATIONTIME() {
        return generationtime;
    }

    /**
     * Définit la valeur de la propriété generationtime.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGENERATIONTIME(XMLGregorianCalendar value) {
        this.generationtime = value;
    }

    /**
     * Obtient la valeur de la propriété previewimageurl.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTINFO.PREVIEWIMAGEURL }
     *     
     */
    public APRODUCTINFO.PREVIEWIMAGEURL getPREVIEWIMAGEURL() {
        return previewimageurl;
    }

    /**
     * Définit la valeur de la propriété previewimageurl.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTINFO.PREVIEWIMAGEURL }
     *     
     */
    public void setPREVIEWIMAGEURL(APRODUCTINFO.PREVIEWIMAGEURL value) {
        this.previewimageurl = value;
    }

    /**
     * Obtient la valeur de la propriété previewgeoinfo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPREVIEWGEOINFO() {
        return previewgeoinfo;
    }

    /**
     * Définit la valeur de la propriété previewgeoinfo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPREVIEWGEOINFO(String value) {
        this.previewgeoinfo = value;
    }

    /**
     * Obtient la valeur de la propriété datatake.
     * 
     * @return
     *     possible object is
     *     {@link ADATATAKEIDENTIFICATION }
     *     
     */
    public ADATATAKEIDENTIFICATION getDatatake() {
        return datatake;
    }

    /**
     * Définit la valeur de la propriété datatake.
     * 
     * @param value
     *     allowed object is
     *     {@link ADATATAKEIDENTIFICATION }
     *     
     */
    public void setDatatake(ADATATAKEIDENTIFICATION value) {
        this.datatake = value;
    }

    /**
     * Obtient la valeur de la propriété queryOptions.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTOPTIONS }
     *     
     */
    public APRODUCTOPTIONS getQueryOptions() {
        return queryOptions;
    }

    /**
     * Définit la valeur de la propriété queryOptions.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTOPTIONS }
     *     
     */
    public void setQueryOptions(APRODUCTOPTIONS value) {
        this.queryOptions = value;
    }

    /**
     * Obtient la valeur de la propriété productOrganisation.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTINFO.ProductOrganisation }
     *     
     */
    public APRODUCTINFO.ProductOrganisation getProductOrganisation() {
        return productOrganisation;
    }

    /**
     * Définit la valeur de la propriété productOrganisation.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTINFO.ProductOrganisation }
     *     
     */
    public void setProductOrganisation(APRODUCTINFO.ProductOrganisation value) {
        this.productOrganisation = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anyURI">
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class PREVIEWIMAGEURL {

        @XmlValue
        @XmlSchemaType(name = "anyURI")
        protected String value;

        /**
         * Obtient la valeur de la propriété value.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/image/>A_PROCESSING_LEVEL">
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class PROCESSINGLEVEL {

        @XmlValue
        protected APROCESSINGLEVEL value;

        /**
         * Obtient la valeur de la propriété value.
         * 
         * @return
         *     possible object is
         *     {@link APROCESSINGLEVEL }
         *     
         */
        public APROCESSINGLEVEL getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         * 
         * @param value
         *     allowed object is
         *     {@link APROCESSINGLEVEL }
         *     
         */
        public void setValue(APROCESSINGLEVEL value) {
            this.value = value;
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
     *         &lt;element name="Granule_List" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PRODUCT_ORGANIZATION">
     *               &lt;/extension>
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
        "granuleList"
    })
    public static class ProductOrganisation {

        @XmlElement(name = "Granule_List", required = true)
        protected List<APRODUCTINFO.ProductOrganisation.GranuleList> granuleList;

        /**
         * Gets the value of the granuleList property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the granuleList property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGranuleList().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link APRODUCTINFO.ProductOrganisation.GranuleList }
         * 
         * 
         */
        public List<APRODUCTINFO.ProductOrganisation.GranuleList> getGranuleList() {
            if (granuleList == null) {
                granuleList = new ArrayList<APRODUCTINFO.ProductOrganisation.GranuleList>();
            }
            return this.granuleList;
        }


        /**
         * <p>Classe Java pour anonymous complex type.
         * 
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PRODUCT_ORGANIZATION">
         *     &lt;/extension>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class GranuleList
            extends APRODUCTORGANIZATION
        {


        }

    }

}
