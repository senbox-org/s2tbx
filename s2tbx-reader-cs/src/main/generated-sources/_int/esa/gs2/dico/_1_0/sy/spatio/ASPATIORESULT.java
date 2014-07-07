//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.spatio;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Sigma quality report for spatiotriangulation
 * 
 * <p>Classe Java pour A_SPATIO_RESULT complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_SPATIO_RESULT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Ground_Residual">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="HISTOGRAM" type="{http://gs2.esa.int/DICO/1.0/SY/spatio/}A_LOCAL_HISTOGRAM_DEFINITION" maxOccurs="3" minOccurs="3"/>
 *                   &lt;element name="ALTI_ACCURACY" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *                   &lt;element name="PLANI_ACCURACY" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Image_Residual">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="HISTOGRAM" type="{http://gs2.esa.int/DICO/1.0/SY/spatio/}A_LOCAL_HISTOGRAM_DEFINITION" maxOccurs="2" minOccurs="2"/>
 *                   &lt;element name="PIX_ACCURACY" type="{http://www.w3.org/2001/XMLSchema}float"/>
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
@XmlType(name = "A_SPATIO_RESULT", propOrder = {
    "groundResidual",
    "imageResidual"
})
public class ASPATIORESULT {

    @XmlElement(name = "Ground_Residual", required = true)
    protected ASPATIORESULT.GroundResidual groundResidual;
    @XmlElement(name = "Image_Residual", required = true)
    protected ASPATIORESULT.ImageResidual imageResidual;

    /**
     * Obtient la valeur de la propriété groundResidual.
     * 
     * @return
     *     possible object is
     *     {@link ASPATIORESULT.GroundResidual }
     *     
     */
    public ASPATIORESULT.GroundResidual getGroundResidual() {
        return groundResidual;
    }

    /**
     * Définit la valeur de la propriété groundResidual.
     * 
     * @param value
     *     allowed object is
     *     {@link ASPATIORESULT.GroundResidual }
     *     
     */
    public void setGroundResidual(ASPATIORESULT.GroundResidual value) {
        this.groundResidual = value;
    }

    /**
     * Obtient la valeur de la propriété imageResidual.
     * 
     * @return
     *     possible object is
     *     {@link ASPATIORESULT.ImageResidual }
     *     
     */
    public ASPATIORESULT.ImageResidual getImageResidual() {
        return imageResidual;
    }

    /**
     * Définit la valeur de la propriété imageResidual.
     * 
     * @param value
     *     allowed object is
     *     {@link ASPATIORESULT.ImageResidual }
     *     
     */
    public void setImageResidual(ASPATIORESULT.ImageResidual value) {
        this.imageResidual = value;
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
     *         &lt;element name="HISTOGRAM" type="{http://gs2.esa.int/DICO/1.0/SY/spatio/}A_LOCAL_HISTOGRAM_DEFINITION" maxOccurs="3" minOccurs="3"/>
     *         &lt;element name="ALTI_ACCURACY" type="{http://www.w3.org/2001/XMLSchema}float"/>
     *         &lt;element name="PLANI_ACCURACY" type="{http://www.w3.org/2001/XMLSchema}float"/>
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
        "histogram",
        "altiaccuracy",
        "planiaccuracy"
    })
    public static class GroundResidual {

        @XmlElement(name = "HISTOGRAM", required = true)
        protected List<ALOCALHISTOGRAMDEFINITION> histogram;
        @XmlElement(name = "ALTI_ACCURACY")
        protected float altiaccuracy;
        @XmlElement(name = "PLANI_ACCURACY")
        protected float planiaccuracy;

        /**
         * Gets the value of the histogram property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the histogram property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHISTOGRAM().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ALOCALHISTOGRAMDEFINITION }
         * 
         * 
         */
        public List<ALOCALHISTOGRAMDEFINITION> getHISTOGRAM() {
            if (histogram == null) {
                histogram = new ArrayList<ALOCALHISTOGRAMDEFINITION>();
            }
            return this.histogram;
        }

        /**
         * Obtient la valeur de la propriété altiaccuracy.
         * 
         */
        public float getALTIACCURACY() {
            return altiaccuracy;
        }

        /**
         * Définit la valeur de la propriété altiaccuracy.
         * 
         */
        public void setALTIACCURACY(float value) {
            this.altiaccuracy = value;
        }

        /**
         * Obtient la valeur de la propriété planiaccuracy.
         * 
         */
        public float getPLANIACCURACY() {
            return planiaccuracy;
        }

        /**
         * Définit la valeur de la propriété planiaccuracy.
         * 
         */
        public void setPLANIACCURACY(float value) {
            this.planiaccuracy = value;
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
     *         &lt;element name="HISTOGRAM" type="{http://gs2.esa.int/DICO/1.0/SY/spatio/}A_LOCAL_HISTOGRAM_DEFINITION" maxOccurs="2" minOccurs="2"/>
     *         &lt;element name="PIX_ACCURACY" type="{http://www.w3.org/2001/XMLSchema}float"/>
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
        "histogram",
        "pixaccuracy"
    })
    public static class ImageResidual {

        @XmlElement(name = "HISTOGRAM", required = true)
        protected List<ALOCALHISTOGRAMDEFINITION> histogram;
        @XmlElement(name = "PIX_ACCURACY")
        protected float pixaccuracy;

        /**
         * Gets the value of the histogram property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the histogram property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHISTOGRAM().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ALOCALHISTOGRAMDEFINITION }
         * 
         * 
         */
        public List<ALOCALHISTOGRAMDEFINITION> getHISTOGRAM() {
            if (histogram == null) {
                histogram = new ArrayList<ALOCALHISTOGRAMDEFINITION>();
            }
            return this.histogram;
        }

        /**
         * Obtient la valeur de la propriété pixaccuracy.
         * 
         */
        public float getPIXACCURACY() {
            return pixaccuracy;
        }

        /**
         * Définit la valeur de la propriété pixaccuracy.
         * 
         */
        public void setPIXACCURACY(float value) {
            this.pixaccuracy = value;
        }

    }

}
