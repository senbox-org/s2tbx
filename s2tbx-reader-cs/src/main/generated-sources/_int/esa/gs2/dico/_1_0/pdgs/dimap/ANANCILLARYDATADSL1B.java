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
 * <p>Classe Java pour AN_ANCILLARY_DATA_DSL1B complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_ANCILLARY_DATA_DSL1B">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ANC_FROM_L0" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ANCILLARY_DATA_DSL0"/>
 *         &lt;element name="Radiometric_Info">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_RADIOMETRIC_DATA_L1B">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Geometric_Refining_Info">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_DATA">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
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
@XmlType(name = "AN_ANCILLARY_DATA_DSL1B", propOrder = {
    "ancfroml0",
    "radiometricInfo",
    "geometricRefiningInfo"
})
public class ANANCILLARYDATADSL1B {

    @XmlElement(name = "ANC_FROM_L0", required = true)
    protected ANANCILLARYDATADSL0 ancfroml0;
    @XmlElement(name = "Radiometric_Info", required = true)
    protected ANANCILLARYDATADSL1B.RadiometricInfo radiometricInfo;
    @XmlElement(name = "Geometric_Refining_Info", required = true)
    protected ANANCILLARYDATADSL1B.GeometricRefiningInfo geometricRefiningInfo;

    /**
     * Obtient la valeur de la propriété ancfroml0.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATADSL0 }
     *     
     */
    public ANANCILLARYDATADSL0 getANCFROML0() {
        return ancfroml0;
    }

    /**
     * Définit la valeur de la propriété ancfroml0.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATADSL0 }
     *     
     */
    public void setANCFROML0(ANANCILLARYDATADSL0 value) {
        this.ancfroml0 = value;
    }

    /**
     * Obtient la valeur de la propriété radiometricInfo.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATADSL1B.RadiometricInfo }
     *     
     */
    public ANANCILLARYDATADSL1B.RadiometricInfo getRadiometricInfo() {
        return radiometricInfo;
    }

    /**
     * Définit la valeur de la propriété radiometricInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATADSL1B.RadiometricInfo }
     *     
     */
    public void setRadiometricInfo(ANANCILLARYDATADSL1B.RadiometricInfo value) {
        this.radiometricInfo = value;
    }

    /**
     * Obtient la valeur de la propriété geometricRefiningInfo.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATADSL1B.GeometricRefiningInfo }
     *     
     */
    public ANANCILLARYDATADSL1B.GeometricRefiningInfo getGeometricRefiningInfo() {
        return geometricRefiningInfo;
    }

    /**
     * Définit la valeur de la propriété geometricRefiningInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATADSL1B.GeometricRefiningInfo }
     *     
     */
    public void setGeometricRefiningInfo(ANANCILLARYDATADSL1B.GeometricRefiningInfo value) {
        this.geometricRefiningInfo = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_DATA">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GeometricRefiningInfo
        extends AGEOMETRICDATA
    {

        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

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


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_RADIOMETRIC_DATA_L1B">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class RadiometricInfo
        extends ARADIOMETRICDATAL1B
    {

        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

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

}
