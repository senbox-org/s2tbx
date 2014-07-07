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
 * <p>Classe Java pour AN_AUXILIARY_DATA_INFO_USER_PROD complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_AUXILIARY_DATA_INFO_USER_PROD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IERS_Bulletin_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_IERS_BULLETIN"/>
 *         &lt;element name="PHYSICAL_GAINS">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
 *                 &lt;attribute name="bandId" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;enumeration value="0"/>
 *                       &lt;enumeration value="1"/>
 *                       &lt;enumeration value="2"/>
 *                       &lt;enumeration value="3"/>
 *                       &lt;enumeration value="4"/>
 *                       &lt;enumeration value="5"/>
 *                       &lt;enumeration value="6"/>
 *                       &lt;enumeration value="7"/>
 *                       &lt;enumeration value="8"/>
 *                       &lt;enumeration value="9"/>
 *                       &lt;enumeration value="10"/>
 *                       &lt;enumeration value="11"/>
 *                       &lt;enumeration value="12"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="REFERENCE_BAND" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DataStrip_Generation_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PROCESSING_STEP_LIST"/>
 *         &lt;element name="Aux_Data">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="GIPP_List_Ref" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GIPP_LIST"/>
 *                   &lt;element name="PRODUCTION_DEM_TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="IERS_BULLETIN_FILENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="GRI_FILENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ECMWF_FILENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "AN_AUXILIARY_DATA_INFO_USER_PROD", propOrder = {
    "iersBulletinInfo",
    "physicalgains",
    "referenceband",
    "dataStripGenerationInfo",
    "auxData"
})
public class ANAUXILIARYDATAINFOUSERPROD {

    @XmlElement(name = "IERS_Bulletin_Info", required = true)
    protected ANIERSBULLETIN iersBulletinInfo;
    @XmlElement(name = "PHYSICAL_GAINS", required = true)
    protected ANAUXILIARYDATAINFOUSERPROD.PHYSICALGAINS physicalgains;
    @XmlElement(name = "REFERENCE_BAND")
    protected int referenceband;
    @XmlElement(name = "DataStrip_Generation_Info", required = true)
    protected APROCESSINGSTEPLIST dataStripGenerationInfo;
    @XmlElement(name = "Aux_Data", required = true)
    protected ANAUXILIARYDATAINFOUSERPROD.AuxData auxData;

    /**
     * Obtient la valeur de la propriété iersBulletinInfo.
     * 
     * @return
     *     possible object is
     *     {@link ANIERSBULLETIN }
     *     
     */
    public ANIERSBULLETIN getIERSBulletinInfo() {
        return iersBulletinInfo;
    }

    /**
     * Définit la valeur de la propriété iersBulletinInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIERSBULLETIN }
     *     
     */
    public void setIERSBulletinInfo(ANIERSBULLETIN value) {
        this.iersBulletinInfo = value;
    }

    /**
     * Obtient la valeur de la propriété physicalgains.
     * 
     * @return
     *     possible object is
     *     {@link ANAUXILIARYDATAINFOUSERPROD.PHYSICALGAINS }
     *     
     */
    public ANAUXILIARYDATAINFOUSERPROD.PHYSICALGAINS getPHYSICALGAINS() {
        return physicalgains;
    }

    /**
     * Définit la valeur de la propriété physicalgains.
     * 
     * @param value
     *     allowed object is
     *     {@link ANAUXILIARYDATAINFOUSERPROD.PHYSICALGAINS }
     *     
     */
    public void setPHYSICALGAINS(ANAUXILIARYDATAINFOUSERPROD.PHYSICALGAINS value) {
        this.physicalgains = value;
    }

    /**
     * Obtient la valeur de la propriété referenceband.
     * 
     */
    public int getREFERENCEBAND() {
        return referenceband;
    }

    /**
     * Définit la valeur de la propriété referenceband.
     * 
     */
    public void setREFERENCEBAND(int value) {
        this.referenceband = value;
    }

    /**
     * Obtient la valeur de la propriété dataStripGenerationInfo.
     * 
     * @return
     *     possible object is
     *     {@link APROCESSINGSTEPLIST }
     *     
     */
    public APROCESSINGSTEPLIST getDataStripGenerationInfo() {
        return dataStripGenerationInfo;
    }

    /**
     * Définit la valeur de la propriété dataStripGenerationInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link APROCESSINGSTEPLIST }
     *     
     */
    public void setDataStripGenerationInfo(APROCESSINGSTEPLIST value) {
        this.dataStripGenerationInfo = value;
    }

    /**
     * Obtient la valeur de la propriété auxData.
     * 
     * @return
     *     possible object is
     *     {@link ANAUXILIARYDATAINFOUSERPROD.AuxData }
     *     
     */
    public ANAUXILIARYDATAINFOUSERPROD.AuxData getAuxData() {
        return auxData;
    }

    /**
     * Définit la valeur de la propriété auxData.
     * 
     * @param value
     *     allowed object is
     *     {@link ANAUXILIARYDATAINFOUSERPROD.AuxData }
     *     
     */
    public void setAuxData(ANAUXILIARYDATAINFOUSERPROD.AuxData value) {
        this.auxData = value;
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
     *         &lt;element name="GIPP_List_Ref" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GIPP_LIST"/>
     *         &lt;element name="PRODUCTION_DEM_TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="IERS_BULLETIN_FILENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="GRI_FILENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ECMWF_FILENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "gippListRef",
        "productiondemtype",
        "iersbulletinfilename",
        "grifilename",
        "ecmwffilename"
    })
    public static class AuxData {

        @XmlElement(name = "GIPP_List_Ref", required = true)
        protected AGIPPLIST gippListRef;
        @XmlElement(name = "PRODUCTION_DEM_TYPE", required = true)
        protected String productiondemtype;
        @XmlElement(name = "IERS_BULLETIN_FILENAME", required = true)
        protected String iersbulletinfilename;
        @XmlElement(name = "GRI_FILENAME", required = true)
        protected String grifilename;
        @XmlElement(name = "ECMWF_FILENAME", required = true)
        protected String ecmwffilename;

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

        /**
         * Obtient la valeur de la propriété ecmwffilename.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getECMWFFILENAME() {
            return ecmwffilename;
        }

        /**
         * Définit la valeur de la propriété ecmwffilename.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setECMWFFILENAME(String value) {
            this.ecmwffilename = value;
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
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
     *       &lt;attribute name="bandId" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;enumeration value="0"/>
     *             &lt;enumeration value="1"/>
     *             &lt;enumeration value="2"/>
     *             &lt;enumeration value="3"/>
     *             &lt;enumeration value="4"/>
     *             &lt;enumeration value="5"/>
     *             &lt;enumeration value="6"/>
     *             &lt;enumeration value="7"/>
     *             &lt;enumeration value="8"/>
     *             &lt;enumeration value="9"/>
     *             &lt;enumeration value="10"/>
     *             &lt;enumeration value="11"/>
     *             &lt;enumeration value="12"/>
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
    public static class PHYSICALGAINS {

        @XmlValue
        protected double value;
        @XmlAttribute(name = "bandId", required = true)
        protected int bandId;

        /**
         * Obtient la valeur de la propriété value.
         * 
         */
        public double getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         * 
         */
        public void setValue(double value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propriété bandId.
         * 
         */
        public int getBandId() {
            return bandId;
        }

        /**
         * Définit la valeur de la propriété bandId.
         * 
         */
        public void setBandId(int value) {
            this.bandId = value;
        }

    }

}
