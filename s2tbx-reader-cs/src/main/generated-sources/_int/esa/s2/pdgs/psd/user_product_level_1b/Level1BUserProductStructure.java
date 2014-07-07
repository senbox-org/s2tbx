//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.user_product_level_1b;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour Level-1B_User_Product_Structure complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Level-1B_User_Product_Structure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Product_Metadata_File">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="General_Info">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Geometric_Info">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Auxiliary_Data_Info">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Quality_Indicators_Info">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GRANULE">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DATASTRIP">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="AUX_DATA">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Browse_Image" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="manifest.safe" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="rep_info" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="INSPIRE" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="HTML" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Level-1B_User_Product_Structure", propOrder = {
    "productMetadataFile",
    "granule",
    "datastrip",
    "auxdata",
    "browseImage",
    "manifestSafe",
    "repInfo",
    "inspire",
    "html"
})
public class Level1BUserProductStructure {

    @XmlElement(name = "Product_Metadata_File", required = true)
    protected Level1BUserProductStructure.ProductMetadataFile productMetadataFile;
    @XmlElement(name = "GRANULE", required = true)
    protected Level1BUserProductStructure.GRANULE granule;
    @XmlElement(name = "DATASTRIP", required = true)
    protected Level1BUserProductStructure.DATASTRIP datastrip;
    @XmlElement(name = "AUX_DATA", required = true)
    protected Level1BUserProductStructure.AUXDATA auxdata;
    @XmlElement(name = "Browse_Image")
    protected Object browseImage;
    @XmlElement(name = "manifest.safe")
    protected Object manifestSafe;
    @XmlElement(name = "rep_info")
    protected Object repInfo;
    @XmlElement(name = "INSPIRE", required = true)
    protected Object inspire;
    @XmlElement(name = "HTML", required = true)
    protected Object html;

    /**
     * Obtient la valeur de la propriété productMetadataFile.
     * 
     * @return
     *     possible object is
     *     {@link Level1BUserProductStructure.ProductMetadataFile }
     *     
     */
    public Level1BUserProductStructure.ProductMetadataFile getProductMetadataFile() {
        return productMetadataFile;
    }

    /**
     * Définit la valeur de la propriété productMetadataFile.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1BUserProductStructure.ProductMetadataFile }
     *     
     */
    public void setProductMetadataFile(Level1BUserProductStructure.ProductMetadataFile value) {
        this.productMetadataFile = value;
    }

    /**
     * Obtient la valeur de la propriété granule.
     * 
     * @return
     *     possible object is
     *     {@link Level1BUserProductStructure.GRANULE }
     *     
     */
    public Level1BUserProductStructure.GRANULE getGRANULE() {
        return granule;
    }

    /**
     * Définit la valeur de la propriété granule.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1BUserProductStructure.GRANULE }
     *     
     */
    public void setGRANULE(Level1BUserProductStructure.GRANULE value) {
        this.granule = value;
    }

    /**
     * Obtient la valeur de la propriété datastrip.
     * 
     * @return
     *     possible object is
     *     {@link Level1BUserProductStructure.DATASTRIP }
     *     
     */
    public Level1BUserProductStructure.DATASTRIP getDATASTRIP() {
        return datastrip;
    }

    /**
     * Définit la valeur de la propriété datastrip.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1BUserProductStructure.DATASTRIP }
     *     
     */
    public void setDATASTRIP(Level1BUserProductStructure.DATASTRIP value) {
        this.datastrip = value;
    }

    /**
     * Obtient la valeur de la propriété auxdata.
     * 
     * @return
     *     possible object is
     *     {@link Level1BUserProductStructure.AUXDATA }
     *     
     */
    public Level1BUserProductStructure.AUXDATA getAUXDATA() {
        return auxdata;
    }

    /**
     * Définit la valeur de la propriété auxdata.
     * 
     * @param value
     *     allowed object is
     *     {@link Level1BUserProductStructure.AUXDATA }
     *     
     */
    public void setAUXDATA(Level1BUserProductStructure.AUXDATA value) {
        this.auxdata = value;
    }

    /**
     * Obtient la valeur de la propriété browseImage.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getBrowseImage() {
        return browseImage;
    }

    /**
     * Définit la valeur de la propriété browseImage.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setBrowseImage(Object value) {
        this.browseImage = value;
    }

    /**
     * Obtient la valeur de la propriété manifestSafe.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getManifestSafe() {
        return manifestSafe;
    }

    /**
     * Définit la valeur de la propriété manifestSafe.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setManifestSafe(Object value) {
        this.manifestSafe = value;
    }

    /**
     * Obtient la valeur de la propriété repInfo.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getRepInfo() {
        return repInfo;
    }

    /**
     * Définit la valeur de la propriété repInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setRepInfo(Object value) {
        this.repInfo = value;
    }

    /**
     * Obtient la valeur de la propriété inspire.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getINSPIRE() {
        return inspire;
    }

    /**
     * Définit la valeur de la propriété inspire.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setINSPIRE(Object value) {
        this.inspire = value;
    }

    /**
     * Obtient la valeur de la propriété html.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getHTML() {
        return html;
    }

    /**
     * Définit la valeur de la propriété html.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setHTML(Object value) {
        this.html = value;
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
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class AUXDATA {


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
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DATASTRIP {


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
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GRANULE {


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
     *         &lt;element name="General_Info">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Geometric_Info">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Auxiliary_Data_Info">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Quality_Indicators_Info">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
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
        "generalInfo",
        "geometricInfo",
        "auxiliaryDataInfo",
        "qualityIndicatorsInfo"
    })
    public static class ProductMetadataFile {

        @XmlElement(name = "General_Info", required = true)
        protected Level1BUserProductStructure.ProductMetadataFile.GeneralInfo generalInfo;
        @XmlElement(name = "Geometric_Info", required = true)
        protected Level1BUserProductStructure.ProductMetadataFile.GeometricInfo geometricInfo;
        @XmlElement(name = "Auxiliary_Data_Info", required = true)
        protected Level1BUserProductStructure.ProductMetadataFile.AuxiliaryDataInfo auxiliaryDataInfo;
        @XmlElement(name = "Quality_Indicators_Info", required = true)
        protected Level1BUserProductStructure.ProductMetadataFile.QualityIndicatorsInfo qualityIndicatorsInfo;

        /**
         * Obtient la valeur de la propriété generalInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level1BUserProductStructure.ProductMetadataFile.GeneralInfo }
         *     
         */
        public Level1BUserProductStructure.ProductMetadataFile.GeneralInfo getGeneralInfo() {
            return generalInfo;
        }

        /**
         * Définit la valeur de la propriété generalInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level1BUserProductStructure.ProductMetadataFile.GeneralInfo }
         *     
         */
        public void setGeneralInfo(Level1BUserProductStructure.ProductMetadataFile.GeneralInfo value) {
            this.generalInfo = value;
        }

        /**
         * Obtient la valeur de la propriété geometricInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level1BUserProductStructure.ProductMetadataFile.GeometricInfo }
         *     
         */
        public Level1BUserProductStructure.ProductMetadataFile.GeometricInfo getGeometricInfo() {
            return geometricInfo;
        }

        /**
         * Définit la valeur de la propriété geometricInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level1BUserProductStructure.ProductMetadataFile.GeometricInfo }
         *     
         */
        public void setGeometricInfo(Level1BUserProductStructure.ProductMetadataFile.GeometricInfo value) {
            this.geometricInfo = value;
        }

        /**
         * Obtient la valeur de la propriété auxiliaryDataInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level1BUserProductStructure.ProductMetadataFile.AuxiliaryDataInfo }
         *     
         */
        public Level1BUserProductStructure.ProductMetadataFile.AuxiliaryDataInfo getAuxiliaryDataInfo() {
            return auxiliaryDataInfo;
        }

        /**
         * Définit la valeur de la propriété auxiliaryDataInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level1BUserProductStructure.ProductMetadataFile.AuxiliaryDataInfo }
         *     
         */
        public void setAuxiliaryDataInfo(Level1BUserProductStructure.ProductMetadataFile.AuxiliaryDataInfo value) {
            this.auxiliaryDataInfo = value;
        }

        /**
         * Obtient la valeur de la propriété qualityIndicatorsInfo.
         * 
         * @return
         *     possible object is
         *     {@link Level1BUserProductStructure.ProductMetadataFile.QualityIndicatorsInfo }
         *     
         */
        public Level1BUserProductStructure.ProductMetadataFile.QualityIndicatorsInfo getQualityIndicatorsInfo() {
            return qualityIndicatorsInfo;
        }

        /**
         * Définit la valeur de la propriété qualityIndicatorsInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link Level1BUserProductStructure.ProductMetadataFile.QualityIndicatorsInfo }
         *     
         */
        public void setQualityIndicatorsInfo(Level1BUserProductStructure.ProductMetadataFile.QualityIndicatorsInfo value) {
            this.qualityIndicatorsInfo = value;
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
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class AuxiliaryDataInfo {


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
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class GeneralInfo {


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
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class GeometricInfo {


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
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class QualityIndicatorsInfo {


        }

    }

}
