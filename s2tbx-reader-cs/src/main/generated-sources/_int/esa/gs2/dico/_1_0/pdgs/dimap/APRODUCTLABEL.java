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
 * <p>Classe Java pour A_PRODUCT_LABEL complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_PRODUCT_LABEL">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PLACE_NAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CUSTOMER_REFERENCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="INTERNAL_REFERENCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="COMMERCIAL_REFERENCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="COMMERCIAL_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="COMMENT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_PRODUCT_LABEL", propOrder = {
    "placename",
    "customerreference",
    "internalreference",
    "commercialreference",
    "commercialitem",
    "comment"
})
public class APRODUCTLABEL {

    @XmlElement(name = "PLACE_NAME")
    protected String placename;
    @XmlElement(name = "CUSTOMER_REFERENCE")
    protected String customerreference;
    @XmlElement(name = "INTERNAL_REFERENCE")
    protected String internalreference;
    @XmlElement(name = "COMMERCIAL_REFERENCE")
    protected String commercialreference;
    @XmlElement(name = "COMMERCIAL_ITEM")
    protected String commercialitem;
    @XmlElement(name = "COMMENT")
    protected String comment;

    /**
     * Obtient la valeur de la propriété placename.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPLACENAME() {
        return placename;
    }

    /**
     * Définit la valeur de la propriété placename.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPLACENAME(String value) {
        this.placename = value;
    }

    /**
     * Obtient la valeur de la propriété customerreference.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCUSTOMERREFERENCE() {
        return customerreference;
    }

    /**
     * Définit la valeur de la propriété customerreference.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCUSTOMERREFERENCE(String value) {
        this.customerreference = value;
    }

    /**
     * Obtient la valeur de la propriété internalreference.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINTERNALREFERENCE() {
        return internalreference;
    }

    /**
     * Définit la valeur de la propriété internalreference.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINTERNALREFERENCE(String value) {
        this.internalreference = value;
    }

    /**
     * Obtient la valeur de la propriété commercialreference.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOMMERCIALREFERENCE() {
        return commercialreference;
    }

    /**
     * Définit la valeur de la propriété commercialreference.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOMMERCIALREFERENCE(String value) {
        this.commercialreference = value;
    }

    /**
     * Obtient la valeur de la propriété commercialitem.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOMMERCIALITEM() {
        return commercialitem;
    }

    /**
     * Définit la valeur de la propriété commercialitem.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOMMERCIALITEM(String value) {
        this.commercialitem = value;
    }

    /**
     * Obtient la valeur de la propriété comment.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOMMENT() {
        return comment;
    }

    /**
     * Définit la valeur de la propriété comment.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOMMENT(String value) {
        this.comment = value;
    }

}
