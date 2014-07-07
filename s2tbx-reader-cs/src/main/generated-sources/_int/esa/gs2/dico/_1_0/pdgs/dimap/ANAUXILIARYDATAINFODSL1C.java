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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour AN_AUXILIARY_DATA_INFO_DSL1C complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_AUXILIARY_DATA_INFO_DSL1C">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IERS_Bulletin" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_IERS_BULLETIN"/>
 *         &lt;element name="GIPP_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GIPP_LIST"/>
 *         &lt;element name="ECMWF_DATA_REF" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}ECMWF_ID"/>
 *         &lt;element name="PRODUCTION_DEM_TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IERS_BULLETIN_FILENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GRI_FILENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_AUXILIARY_DATA_INFO_DSL1C", propOrder = {
    "iersBulletin",
    "gippList",
    "ecmwfdataref",
    "productiondemtype",
    "iersbulletinfilename",
    "grifilename"
})
public class ANAUXILIARYDATAINFODSL1C {

    @XmlElement(name = "IERS_Bulletin", required = true)
    protected ANIERSBULLETIN iersBulletin;
    @XmlElement(name = "GIPP_List", required = true)
    protected AGIPPLIST gippList;
    @XmlElement(name = "ECMWF_DATA_REF", required = true)
    protected String ecmwfdataref;
    @XmlElement(name = "PRODUCTION_DEM_TYPE", required = true)
    protected String productiondemtype;
    @XmlElement(name = "IERS_BULLETIN_FILENAME", required = true)
    protected String iersbulletinfilename;
    @XmlElement(name = "GRI_FILENAME", required = true)
    protected String grifilename;
    @XmlAttribute(name = "metadataLevel")
    protected String metadataLevel;

    /**
     * Obtient la valeur de la propriété iersBulletin.
     * 
     * @return
     *     possible object is
     *     {@link ANIERSBULLETIN }
     *     
     */
    public ANIERSBULLETIN getIERSBulletin() {
        return iersBulletin;
    }

    /**
     * Définit la valeur de la propriété iersBulletin.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIERSBULLETIN }
     *     
     */
    public void setIERSBulletin(ANIERSBULLETIN value) {
        this.iersBulletin = value;
    }

    /**
     * Obtient la valeur de la propriété gippList.
     * 
     * @return
     *     possible object is
     *     {@link AGIPPLIST }
     *     
     */
    public AGIPPLIST getGIPPList() {
        return gippList;
    }

    /**
     * Définit la valeur de la propriété gippList.
     * 
     * @param value
     *     allowed object is
     *     {@link AGIPPLIST }
     *     
     */
    public void setGIPPList(AGIPPLIST value) {
        this.gippList = value;
    }

    /**
     * Obtient la valeur de la propriété ecmwfdataref.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getECMWFDATAREF() {
        return ecmwfdataref;
    }

    /**
     * Définit la valeur de la propriété ecmwfdataref.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setECMWFDATAREF(String value) {
        this.ecmwfdataref = value;
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

    /**
     * Obtient la valeur de la propriété metadataLevel.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetadataLevel() {
        if (metadataLevel == null) {
            return "Standard";
        } else {
            return metadataLevel;
        }
    }

    /**
     * Définit la valeur de la propriété metadataLevel.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetadataLevel(String value) {
        this.metadataLevel = value;
    }

}
