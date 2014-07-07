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
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour AN_AUXILIARY_DATA_INFO_DSL1B complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_AUXILIARY_DATA_INFO_DSL1B">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IERS_Bulletin" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_IERS_BULLETIN"/>
 *         &lt;element name="GIPP_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GIPP_LIST"/>
 *         &lt;element name="PRODUCTION_DEM_TYPE">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/DataAccess/item/>DEM_ID">
 *                 &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="IERS_BULLETIN_FILENAME" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}IERS_ID"/>
 *         &lt;element name="GRI_FILENAME" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}GRI_ID"/>
 *         &lt;element name="REFERENCE_BAND" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
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
@XmlType(name = "AN_AUXILIARY_DATA_INFO_DSL1B", propOrder = {
    "iersBulletin",
    "gippList",
    "productiondemtype",
    "iersbulletinfilename",
    "grifilename",
    "referenceband"
})
public class ANAUXILIARYDATAINFODSL1B {

    @XmlElement(name = "IERS_Bulletin", required = true)
    protected ANIERSBULLETIN iersBulletin;
    @XmlElement(name = "GIPP_List", required = true)
    protected AGIPPLIST gippList;
    @XmlElement(name = "PRODUCTION_DEM_TYPE", required = true)
    protected ANAUXILIARYDATAINFODSL1B.PRODUCTIONDEMTYPE productiondemtype;
    @XmlElement(name = "IERS_BULLETIN_FILENAME", required = true)
    protected String iersbulletinfilename;
    @XmlElement(name = "GRI_FILENAME", required = true)
    protected String grifilename;
    @XmlElement(name = "REFERENCE_BAND", required = true)
    protected String referenceband;
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
     * Obtient la valeur de la propriété productiondemtype.
     * 
     * @return
     *     possible object is
     *     {@link ANAUXILIARYDATAINFODSL1B.PRODUCTIONDEMTYPE }
     *     
     */
    public ANAUXILIARYDATAINFODSL1B.PRODUCTIONDEMTYPE getPRODUCTIONDEMTYPE() {
        return productiondemtype;
    }

    /**
     * Définit la valeur de la propriété productiondemtype.
     * 
     * @param value
     *     allowed object is
     *     {@link ANAUXILIARYDATAINFODSL1B.PRODUCTIONDEMTYPE }
     *     
     */
    public void setPRODUCTIONDEMTYPE(ANAUXILIARYDATAINFODSL1B.PRODUCTIONDEMTYPE value) {
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
     * Obtient la valeur de la propriété referenceband.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREFERENCEBAND() {
        return referenceband;
    }

    /**
     * Définit la valeur de la propriété referenceband.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREFERENCEBAND(String value) {
        this.referenceband = value;
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


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/DataAccess/item/>DEM_ID">
     *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    public static class PRODUCTIONDEMTYPE {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "version")
        protected String version;

        /**
         * Product Data Item identification
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
         * Obtient la valeur de la propriété version.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVersion() {
            return version;
        }

        /**
         * Définit la valeur de la propriété version.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVersion(String value) {
            this.version = value;
        }

    }

}
