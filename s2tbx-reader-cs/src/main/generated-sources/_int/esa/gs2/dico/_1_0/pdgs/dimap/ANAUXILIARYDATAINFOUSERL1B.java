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
 * <p>Classe Java pour AN_AUXILIARY_DATA_INFO_USERL1B complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_AUXILIARY_DATA_INFO_USERL1B">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GIPP_List_Ref" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GIPP_LIST"/>
 *         &lt;element name="PRODUCTION_DEM_TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IERS_BULLETIN_FILENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GRI_FILENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_AUXILIARY_DATA_INFO_USERL1B", propOrder = {
    "gippListRef",
    "productiondemtype",
    "iersbulletinfilename",
    "grifilename"
})
public class ANAUXILIARYDATAINFOUSERL1B {

    @XmlElement(name = "GIPP_List_Ref", required = true)
    protected AGIPPLIST gippListRef;
    @XmlElement(name = "PRODUCTION_DEM_TYPE", required = true)
    protected String productiondemtype;
    @XmlElement(name = "IERS_BULLETIN_FILENAME", required = true)
    protected String iersbulletinfilename;
    @XmlElement(name = "GRI_FILENAME", required = true)
    protected String grifilename;

    /**
     * Obtient la valeur de la propriété gippListRef.
     * 
     * @return
     *     possible object is
     *     {@link AGIPPLIST }
     *     
     */
    public AGIPPLIST getGIPPListRef() {
        return gippListRef;
    }

    /**
     * Définit la valeur de la propriété gippListRef.
     * 
     * @param value
     *     allowed object is
     *     {@link AGIPPLIST }
     *     
     */
    public void setGIPPListRef(AGIPPLIST value) {
        this.gippListRef = value;
    }

    /**
     * Obtient la valeur de la propriété productiondemtype.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRODUCTIONDEMTYPE() {
        return productiondemtype;
    }

    /**
     * Définit la valeur de la propriété productiondemtype.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRODUCTIONDEMTYPE(String value) {
        this.productiondemtype = value;
    }

    /**
     * Obtient la valeur de la propriété iersbulletinfilename.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIERSBULLETINFILENAME() {
        return iersbulletinfilename;
    }

    /**
     * Définit la valeur de la propriété iersbulletinfilename.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIERSBULLETINFILENAME(String value) {
        this.iersbulletinfilename = value;
    }

    /**
     * Obtient la valeur de la propriété grifilename.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGRIFILENAME() {
        return grifilename;
    }

    /**
     * Définit la valeur de la propriété grifilename.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGRIFILENAME(String value) {
        this.grifilename = value;
    }

}
