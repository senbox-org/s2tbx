//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_FIRM_ADDRESS complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_FIRM_ADDRESS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FIRM_NAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ADDRESS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ADDRESS2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PHONE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EMAIL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CITY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="POSTAL_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_FIRM_ADDRESS", propOrder = {
    "name",
    "firmname",
    "address",
    "address2",
    "phone",
    "email",
    "city",
    "country",
    "postalcode"
})
public class AFIRMADDRESS {

    @XmlElement(name = "NAME")
    protected String name;
    @XmlElement(name = "FIRM_NAME")
    protected String firmname;
    @XmlElement(name = "ADDRESS")
    protected String address;
    @XmlElement(name = "ADDRESS2")
    protected String address2;
    @XmlElement(name = "PHONE")
    protected String phone;
    @XmlElement(name = "EMAIL")
    protected String email;
    @XmlElement(name = "CITY")
    protected String city;
    @XmlElement(name = "COUNTRY")
    protected String country;
    @XmlElement(name = "POSTAL_CODE")
    protected String postalcode;

    /**
     * Obtient la valeur de la propriété name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNAME() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNAME(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété firmname.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFIRMNAME() {
        return firmname;
    }

    /**
     * Définit la valeur de la propriété firmname.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFIRMNAME(String value) {
        this.firmname = value;
    }

    /**
     * Obtient la valeur de la propriété address.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getADDRESS() {
        return address;
    }

    /**
     * Définit la valeur de la propriété address.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setADDRESS(String value) {
        this.address = value;
    }

    /**
     * Obtient la valeur de la propriété address2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getADDRESS2() {
        return address2;
    }

    /**
     * Définit la valeur de la propriété address2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setADDRESS2(String value) {
        this.address2 = value;
    }

    /**
     * Obtient la valeur de la propriété phone.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPHONE() {
        return phone;
    }

    /**
     * Définit la valeur de la propriété phone.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPHONE(String value) {
        this.phone = value;
    }

    /**
     * Obtient la valeur de la propriété email.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEMAIL() {
        return email;
    }

    /**
     * Définit la valeur de la propriété email.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEMAIL(String value) {
        this.email = value;
    }

    /**
     * Obtient la valeur de la propriété city.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCITY() {
        return city;
    }

    /**
     * Définit la valeur de la propriété city.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCITY(String value) {
        this.city = value;
    }

    /**
     * Obtient la valeur de la propriété country.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOUNTRY() {
        return country;
    }

    /**
     * Définit la valeur de la propriété country.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOUNTRY(String value) {
        this.country = value;
    }

    /**
     * Obtient la valeur de la propriété postalcode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPOSTALCODE() {
        return postalcode;
    }

    /**
     * Définit la valeur de la propriété postalcode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPOSTALCODE(String value) {
        this.postalcode = value;
    }

}
