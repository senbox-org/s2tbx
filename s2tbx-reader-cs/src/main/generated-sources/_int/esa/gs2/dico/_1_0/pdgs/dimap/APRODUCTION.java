//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.pdgs.center.AS2PROCESSINGCENTRE;


/**
 * Production information of a level 1 product
 * 
 * <p>Classe Java pour A_PRODUCTION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_PRODUCTION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="JOB_ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PRODUCT_CODE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DATASET_PRODUCER_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DATASET_PRODUCER_URL">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="href" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DATASET_PRODUCTION_DATE" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Production_Facility">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="SOFTWARE_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="SOFTWARE_VERSION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="PROCESSING_CENTER" type="{http://gs2.esa.int/DICO/1.0/PDGS/center/}A_S2_PROCESSING_CENTRE"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Product_Label" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PRODUCT_LABEL" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_PRODUCTION", propOrder = {
    "jobid",
    "productcode",
    "datasetproducername",
    "datasetproducerurl",
    "datasetproductiondate",
    "productionFacility",
    "productLabel"
})
public class APRODUCTION {

    @XmlElement(name = "JOB_ID", required = true)
    protected String jobid;
    @XmlElement(name = "PRODUCT_CODE", required = true)
    protected String productcode;
    @XmlElement(name = "DATASET_PRODUCER_NAME", required = true)
    protected String datasetproducername;
    @XmlElement(name = "DATASET_PRODUCER_URL", required = true)
    protected APRODUCTION.DATASETPRODUCERURL datasetproducerurl;
    @XmlElement(name = "DATASET_PRODUCTION_DATE", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar datasetproductiondate;
    @XmlElement(name = "Production_Facility", required = true)
    protected APRODUCTION.ProductionFacility productionFacility;
    @XmlElement(name = "Product_Label")
    protected APRODUCTLABEL productLabel;

    /**
     * Obtient la valeur de la propriété jobid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJOBID() {
        return jobid;
    }

    /**
     * Définit la valeur de la propriété jobid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJOBID(String value) {
        this.jobid = value;
    }

    /**
     * Obtient la valeur de la propriété productcode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRODUCTCODE() {
        return productcode;
    }

    /**
     * Définit la valeur de la propriété productcode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRODUCTCODE(String value) {
        this.productcode = value;
    }

    /**
     * Obtient la valeur de la propriété datasetproducername.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDATASETPRODUCERNAME() {
        return datasetproducername;
    }

    /**
     * Définit la valeur de la propriété datasetproducername.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDATASETPRODUCERNAME(String value) {
        this.datasetproducername = value;
    }

    /**
     * Obtient la valeur de la propriété datasetproducerurl.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTION.DATASETPRODUCERURL }
     *     
     */
    public APRODUCTION.DATASETPRODUCERURL getDATASETPRODUCERURL() {
        return datasetproducerurl;
    }

    /**
     * Définit la valeur de la propriété datasetproducerurl.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTION.DATASETPRODUCERURL }
     *     
     */
    public void setDATASETPRODUCERURL(APRODUCTION.DATASETPRODUCERURL value) {
        this.datasetproducerurl = value;
    }

    /**
     * Obtient la valeur de la propriété datasetproductiondate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDATASETPRODUCTIONDATE() {
        return datasetproductiondate;
    }

    /**
     * Définit la valeur de la propriété datasetproductiondate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDATASETPRODUCTIONDATE(XMLGregorianCalendar value) {
        this.datasetproductiondate = value;
    }

    /**
     * Obtient la valeur de la propriété productionFacility.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTION.ProductionFacility }
     *     
     */
    public APRODUCTION.ProductionFacility getProductionFacility() {
        return productionFacility;
    }

    /**
     * Définit la valeur de la propriété productionFacility.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTION.ProductionFacility }
     *     
     */
    public void setProductionFacility(APRODUCTION.ProductionFacility value) {
        this.productionFacility = value;
    }

    /**
     * Obtient la valeur de la propriété productLabel.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTLABEL }
     *     
     */
    public APRODUCTLABEL getProductLabel() {
        return productLabel;
    }

    /**
     * Définit la valeur de la propriété productLabel.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTLABEL }
     *     
     */
    public void setProductLabel(APRODUCTLABEL value) {
        this.productLabel = value;
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
     *       &lt;attribute name="href" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    public static class DATASETPRODUCERURL {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "href")
        protected String href;

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
         * Obtient la valeur de la propriété href.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHref() {
            return href;
        }

        /**
         * Définit la valeur de la propriété href.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHref(String value) {
            this.href = value;
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
     *         &lt;element name="SOFTWARE_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="SOFTWARE_VERSION" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="PROCESSING_CENTER" type="{http://gs2.esa.int/DICO/1.0/PDGS/center/}A_S2_PROCESSING_CENTRE"/>
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
        "softwarename",
        "softwareversion",
        "processingcenter"
    })
    public static class ProductionFacility {

        @XmlElement(name = "SOFTWARE_NAME", required = true)
        protected String softwarename;
        @XmlElement(name = "SOFTWARE_VERSION", required = true)
        protected String softwareversion;
        @XmlElement(name = "PROCESSING_CENTER", required = true)
        protected AS2PROCESSINGCENTRE processingcenter;

        /**
         * Obtient la valeur de la propriété softwarename.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSOFTWARENAME() {
            return softwarename;
        }

        /**
         * Définit la valeur de la propriété softwarename.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSOFTWARENAME(String value) {
            this.softwarename = value;
        }

        /**
         * Obtient la valeur de la propriété softwareversion.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSOFTWAREVERSION() {
            return softwareversion;
        }

        /**
         * Définit la valeur de la propriété softwareversion.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSOFTWAREVERSION(String value) {
            this.softwareversion = value;
        }

        /**
         * Obtient la valeur de la propriété processingcenter.
         * 
         * @return
         *     possible object is
         *     {@link AS2PROCESSINGCENTRE }
         *     
         */
        public AS2PROCESSINGCENTRE getPROCESSINGCENTER() {
            return processingcenter;
        }

        /**
         * Définit la valeur de la propriété processingcenter.
         * 
         * @param value
         *     allowed object is
         *     {@link AS2PROCESSINGCENTRE }
         *     
         */
        public void setPROCESSINGCENTER(AS2PROCESSINGCENTRE value) {
            this.processingcenter = value;
        }

    }

}
