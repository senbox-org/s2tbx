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
import javax.xml.bind.annotation.XmlValue;
import _int.esa.gs2.dico._1_0.sy.image.APROCESSINGLEVEL;


/**
 * General PDGS Product Information
 * 
 * <p>Classe Java pour A_PRODUCT_INFO_SAFE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_PRODUCT_INFO_SAFE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
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
 *         &lt;element name="PREVIEW_IMAGE_URL">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anyURI">
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PREVIEW_GEO_INFO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Datatake" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATATAKE_IDENTIFICATION_SAFE"/>
 *         &lt;element name="Query_Options" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PRODUCT_OPTIONS"/>
 *         &lt;element name="Product_Organisation">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Datastrip_List">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Datastrip" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="DATASTRIP_ID">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;enumeration value="item:DATASTRIP_ID"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="Granule_List">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="Granule" maxOccurs="unbounded">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;sequence>
 *                                                           &lt;element name="IMAGE_DATA_ID">
 *                                                             &lt;complexType>
 *                                                               &lt;simpleContent>
 *                                                                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                                                   &lt;attribute name="imageFormat" default="JPEG2000">
 *                                                                     &lt;simpleType>
 *                                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                                         &lt;enumeration value="JPEG2000"/>
 *                                                                         &lt;enumeration value="BINARY"/>
 *                                                                       &lt;/restriction>
 *                                                                     &lt;/simpleType>
 *                                                                   &lt;/attribute>
 *                                                                 &lt;/extension>
 *                                                               &lt;/simpleContent>
 *                                                             &lt;/complexType>
 *                                                           &lt;/element>
 *                                                         &lt;/sequence>
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
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
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_PRODUCT_INFO_SAFE", propOrder = {
    "producturi",
    "processinglevel",
    "producttype",
    "processingbaseline",
    "previewimageurl",
    "previewgeoinfo",
    "datatake",
    "queryOptions",
    "productOrganisation"
})
public class APRODUCTINFOSAFE {

    @XmlElement(name = "PRODUCT_URI", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String producturi;
    @XmlElement(name = "PROCESSING_LEVEL", required = true)
    protected APRODUCTINFOSAFE.PROCESSINGLEVEL processinglevel;
    @XmlElement(name = "PRODUCT_TYPE", required = true)
    protected String producttype;
    @XmlElement(name = "PROCESSING_BASELINE", required = true)
    protected String processingbaseline;
    @XmlElement(name = "PREVIEW_IMAGE_URL", required = true)
    protected APRODUCTINFOSAFE.PREVIEWIMAGEURL previewimageurl;
    @XmlElement(name = "PREVIEW_GEO_INFO", required = true)
    protected String previewgeoinfo;
    @XmlElement(name = "Datatake", required = true)
    protected ADATATAKEIDENTIFICATIONSAFE datatake;
    @XmlElement(name = "Query_Options", required = true)
    protected APRODUCTOPTIONS queryOptions;
    @XmlElement(name = "Product_Organisation", required = true)
    protected APRODUCTINFOSAFE.ProductOrganisation productOrganisation;

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
     *     {@link APRODUCTINFOSAFE.PROCESSINGLEVEL }
     *     
     */
    public APRODUCTINFOSAFE.PROCESSINGLEVEL getPROCESSINGLEVEL() {
        return processinglevel;
    }

    /**
     * Définit la valeur de la propriété processinglevel.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTINFOSAFE.PROCESSINGLEVEL }
     *     
     */
    public void setPROCESSINGLEVEL(APRODUCTINFOSAFE.PROCESSINGLEVEL value) {
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
     * Obtient la valeur de la propriété previewimageurl.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTINFOSAFE.PREVIEWIMAGEURL }
     *     
     */
    public APRODUCTINFOSAFE.PREVIEWIMAGEURL getPREVIEWIMAGEURL() {
        return previewimageurl;
    }

    /**
     * Définit la valeur de la propriété previewimageurl.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTINFOSAFE.PREVIEWIMAGEURL }
     *     
     */
    public void setPREVIEWIMAGEURL(APRODUCTINFOSAFE.PREVIEWIMAGEURL value) {
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
     *     {@link ADATATAKEIDENTIFICATIONSAFE }
     *     
     */
    public ADATATAKEIDENTIFICATIONSAFE getDatatake() {
        return datatake;
    }

    /**
     * Définit la valeur de la propriété datatake.
     * 
     * @param value
     *     allowed object is
     *     {@link ADATATAKEIDENTIFICATIONSAFE }
     *     
     */
    public void setDatatake(ADATATAKEIDENTIFICATIONSAFE value) {
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
     *     {@link APRODUCTINFOSAFE.ProductOrganisation }
     *     
     */
    public APRODUCTINFOSAFE.ProductOrganisation getProductOrganisation() {
        return productOrganisation;
    }

    /**
     * Définit la valeur de la propriété productOrganisation.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTINFOSAFE.ProductOrganisation }
     *     
     */
    public void setProductOrganisation(APRODUCTINFOSAFE.ProductOrganisation value) {
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
     *         &lt;element name="Datastrip_List">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Datastrip" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="DATASTRIP_ID">
     *                               &lt;simpleType>
     *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                   &lt;enumeration value="item:DATASTRIP_ID"/>
     *                                 &lt;/restriction>
     *                               &lt;/simpleType>
     *                             &lt;/element>
     *                             &lt;element name="Granule_List">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="Granule" maxOccurs="unbounded">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;sequence>
     *                                                 &lt;element name="IMAGE_DATA_ID">
     *                                                   &lt;complexType>
     *                                                     &lt;simpleContent>
     *                                                       &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                                                         &lt;attribute name="imageFormat" default="JPEG2000">
     *                                                           &lt;simpleType>
     *                                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                                               &lt;enumeration value="JPEG2000"/>
     *                                                               &lt;enumeration value="BINARY"/>
     *                                                             &lt;/restriction>
     *                                                           &lt;/simpleType>
     *                                                         &lt;/attribute>
     *                                                       &lt;/extension>
     *                                                     &lt;/simpleContent>
     *                                                   &lt;/complexType>
     *                                                 &lt;/element>
     *                                               &lt;/sequence>
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
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
        "datastripList"
    })
    public static class ProductOrganisation {

        @XmlElement(name = "Datastrip_List", required = true)
        protected APRODUCTINFOSAFE.ProductOrganisation.DatastripList datastripList;

        /**
         * Obtient la valeur de la propriété datastripList.
         * 
         * @return
         *     possible object is
         *     {@link APRODUCTINFOSAFE.ProductOrganisation.DatastripList }
         *     
         */
        public APRODUCTINFOSAFE.ProductOrganisation.DatastripList getDatastripList() {
            return datastripList;
        }

        /**
         * Définit la valeur de la propriété datastripList.
         * 
         * @param value
         *     allowed object is
         *     {@link APRODUCTINFOSAFE.ProductOrganisation.DatastripList }
         *     
         */
        public void setDatastripList(APRODUCTINFOSAFE.ProductOrganisation.DatastripList value) {
            this.datastripList = value;
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
         *         &lt;element name="Datastrip" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="DATASTRIP_ID">
         *                     &lt;simpleType>
         *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                         &lt;enumeration value="item:DATASTRIP_ID"/>
         *                       &lt;/restriction>
         *                     &lt;/simpleType>
         *                   &lt;/element>
         *                   &lt;element name="Granule_List">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="Granule" maxOccurs="unbounded">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;sequence>
         *                                       &lt;element name="IMAGE_DATA_ID">
         *                                         &lt;complexType>
         *                                           &lt;simpleContent>
         *                                             &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *                                               &lt;attribute name="imageFormat" default="JPEG2000">
         *                                                 &lt;simpleType>
         *                                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                                                     &lt;enumeration value="JPEG2000"/>
         *                                                     &lt;enumeration value="BINARY"/>
         *                                                   &lt;/restriction>
         *                                                 &lt;/simpleType>
         *                                               &lt;/attribute>
         *                                             &lt;/extension>
         *                                           &lt;/simpleContent>
         *                                         &lt;/complexType>
         *                                       &lt;/element>
         *                                     &lt;/sequence>
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
            "datastrip"
        })
        public static class DatastripList {

            @XmlElement(name = "Datastrip", required = true)
            protected List<APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip> datastrip;

            /**
             * Gets the value of the datastrip property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the datastrip property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getDatastrip().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip }
             * 
             * 
             */
            public List<APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip> getDatastrip() {
                if (datastrip == null) {
                    datastrip = new ArrayList<APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip>();
                }
                return this.datastrip;
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
             *         &lt;element name="DATASTRIP_ID">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *               &lt;enumeration value="item:DATASTRIP_ID"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
             *         &lt;/element>
             *         &lt;element name="Granule_List">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="Granule" maxOccurs="unbounded">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;sequence>
             *                             &lt;element name="IMAGE_DATA_ID">
             *                               &lt;complexType>
             *                                 &lt;simpleContent>
             *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
             *                                     &lt;attribute name="imageFormat" default="JPEG2000">
             *                                       &lt;simpleType>
             *                                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *                                           &lt;enumeration value="JPEG2000"/>
             *                                           &lt;enumeration value="BINARY"/>
             *                                         &lt;/restriction>
             *                                       &lt;/simpleType>
             *                                     &lt;/attribute>
             *                                   &lt;/extension>
             *                                 &lt;/simpleContent>
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
                "datastripid",
                "granuleList"
            })
            public static class Datastrip {

                @XmlElement(name = "DATASTRIP_ID", required = true)
                protected String datastripid;
                @XmlElement(name = "Granule_List", required = true)
                protected APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList granuleList;

                /**
                 * Obtient la valeur de la propriété datastripid.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getDATASTRIPID() {
                    return datastripid;
                }

                /**
                 * Définit la valeur de la propriété datastripid.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setDATASTRIPID(String value) {
                    this.datastripid = value;
                }

                /**
                 * Obtient la valeur de la propriété granuleList.
                 * 
                 * @return
                 *     possible object is
                 *     {@link APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList }
                 *     
                 */
                public APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList getGranuleList() {
                    return granuleList;
                }

                /**
                 * Définit la valeur de la propriété granuleList.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList }
                 *     
                 */
                public void setGranuleList(APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList value) {
                    this.granuleList = value;
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
                 *         &lt;element name="Granule" maxOccurs="unbounded">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;sequence>
                 *                   &lt;element name="IMAGE_DATA_ID">
                 *                     &lt;complexType>
                 *                       &lt;simpleContent>
                 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                 *                           &lt;attribute name="imageFormat" default="JPEG2000">
                 *                             &lt;simpleType>
                 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
                 *                                 &lt;enumeration value="JPEG2000"/>
                 *                                 &lt;enumeration value="BINARY"/>
                 *                               &lt;/restriction>
                 *                             &lt;/simpleType>
                 *                           &lt;/attribute>
                 *                         &lt;/extension>
                 *                       &lt;/simpleContent>
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
                    "granule"
                })
                public static class GranuleList {

                    @XmlElement(name = "Granule", required = true)
                    protected List<APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList.Granule> granule;

                    /**
                     * Gets the value of the granule property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the granule property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getGranule().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList.Granule }
                     * 
                     * 
                     */
                    public List<APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList.Granule> getGranule() {
                        if (granule == null) {
                            granule = new ArrayList<APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList.Granule>();
                        }
                        return this.granule;
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
                     *         &lt;element name="IMAGE_DATA_ID">
                     *           &lt;complexType>
                     *             &lt;simpleContent>
                     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                     *                 &lt;attribute name="imageFormat" default="JPEG2000">
                     *                   &lt;simpleType>
                     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
                     *                       &lt;enumeration value="JPEG2000"/>
                     *                       &lt;enumeration value="BINARY"/>
                     *                     &lt;/restriction>
                     *                   &lt;/simpleType>
                     *                 &lt;/attribute>
                     *               &lt;/extension>
                     *             &lt;/simpleContent>
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
                        "imagedataid"
                    })
                    public static class Granule {

                        @XmlElement(name = "IMAGE_DATA_ID", required = true)
                        protected APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList.Granule.IMAGEDATAID imagedataid;

                        /**
                         * Obtient la valeur de la propriété imagedataid.
                         * 
                         * @return
                         *     possible object is
                         *     {@link APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList.Granule.IMAGEDATAID }
                         *     
                         */
                        public APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList.Granule.IMAGEDATAID getIMAGEDATAID() {
                            return imagedataid;
                        }

                        /**
                         * Définit la valeur de la propriété imagedataid.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList.Granule.IMAGEDATAID }
                         *     
                         */
                        public void setIMAGEDATAID(APRODUCTINFOSAFE.ProductOrganisation.DatastripList.Datastrip.GranuleList.Granule.IMAGEDATAID value) {
                            this.imagedataid = value;
                        }


                        /**
                         * <p>Classe Java pour anonymous complex type.
                         * 
                         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
                         * 
                         * <pre>
                         * &lt;complexType>
                         *   &lt;simpleContent>
                         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                         *       &lt;attribute name="imageFormat" default="JPEG2000">
                         *         &lt;simpleType>
                         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
                         *             &lt;enumeration value="JPEG2000"/>
                         *             &lt;enumeration value="BINARY"/>
                         *           &lt;/restriction>
                         *         &lt;/simpleType>
                         *       &lt;/attribute>
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
                        public static class IMAGEDATAID {

                            @XmlValue
                            protected String value;
                            @XmlAttribute(name = "imageFormat")
                            protected String imageFormat;

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

                            /**
                             * Obtient la valeur de la propriété imageFormat.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getImageFormat() {
                                if (imageFormat == null) {
                                    return "JPEG2000";
                                } else {
                                    return imageFormat;
                                }
                            }

                            /**
                             * Définit la valeur de la propriété imageFormat.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setImageFormat(String value) {
                                this.imageFormat = value;
                            }

                        }

                    }

                }

            }

        }

    }

}
