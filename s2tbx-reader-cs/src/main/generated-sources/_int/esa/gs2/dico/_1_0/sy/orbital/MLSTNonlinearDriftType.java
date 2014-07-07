//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.orbital;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour MLST_Nonlinear_Drift_Type complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="MLST_Nonlinear_Drift_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Linear_Approx_Validity">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>integer">
 *                 &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}String_Type" fixed="orbit" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Quadratic_Term">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>Decimal_Type">
 *                 &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}String_Type" fixed="s/day^2" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Harmonics_Terms">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="2" minOccurs="0">
 *                   &lt;element name="Harmonic_Term" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Harmonic_Term_Type"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="num" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}NonNegativeInteger_Type" fixed="2" />
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
@XmlType(name = "MLST_Nonlinear_Drift_Type", propOrder = {
    "linearApproxValidity",
    "quadraticTerm",
    "harmonicsTerms"
})
public class MLSTNonlinearDriftType {

    @XmlElement(name = "Linear_Approx_Validity", required = true)
    protected MLSTNonlinearDriftType.LinearApproxValidity linearApproxValidity;
    @XmlElement(name = "Quadratic_Term", required = true)
    protected MLSTNonlinearDriftType.QuadraticTerm quadraticTerm;
    @XmlElement(name = "Harmonics_Terms", required = true)
    protected MLSTNonlinearDriftType.HarmonicsTerms harmonicsTerms;

    /**
     * Obtient la valeur de la propriété linearApproxValidity.
     * 
     * @return
     *     possible object is
     *     {@link MLSTNonlinearDriftType.LinearApproxValidity }
     *     
     */
    public MLSTNonlinearDriftType.LinearApproxValidity getLinearApproxValidity() {
        return linearApproxValidity;
    }

    /**
     * Définit la valeur de la propriété linearApproxValidity.
     * 
     * @param value
     *     allowed object is
     *     {@link MLSTNonlinearDriftType.LinearApproxValidity }
     *     
     */
    public void setLinearApproxValidity(MLSTNonlinearDriftType.LinearApproxValidity value) {
        this.linearApproxValidity = value;
    }

    /**
     * Obtient la valeur de la propriété quadraticTerm.
     * 
     * @return
     *     possible object is
     *     {@link MLSTNonlinearDriftType.QuadraticTerm }
     *     
     */
    public MLSTNonlinearDriftType.QuadraticTerm getQuadraticTerm() {
        return quadraticTerm;
    }

    /**
     * Définit la valeur de la propriété quadraticTerm.
     * 
     * @param value
     *     allowed object is
     *     {@link MLSTNonlinearDriftType.QuadraticTerm }
     *     
     */
    public void setQuadraticTerm(MLSTNonlinearDriftType.QuadraticTerm value) {
        this.quadraticTerm = value;
    }

    /**
     * Obtient la valeur de la propriété harmonicsTerms.
     * 
     * @return
     *     possible object is
     *     {@link MLSTNonlinearDriftType.HarmonicsTerms }
     *     
     */
    public MLSTNonlinearDriftType.HarmonicsTerms getHarmonicsTerms() {
        return harmonicsTerms;
    }

    /**
     * Définit la valeur de la propriété harmonicsTerms.
     * 
     * @param value
     *     allowed object is
     *     {@link MLSTNonlinearDriftType.HarmonicsTerms }
     *     
     */
    public void setHarmonicsTerms(MLSTNonlinearDriftType.HarmonicsTerms value) {
        this.harmonicsTerms = value;
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
     *       &lt;sequence maxOccurs="2" minOccurs="0">
     *         &lt;element name="Harmonic_Term" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Harmonic_Term_Type"/>
     *       &lt;/sequence>
     *       &lt;attribute name="num" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}NonNegativeInteger_Type" fixed="2" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "harmonicTerm"
    })
    public static class HarmonicsTerms {

        @XmlElement(name = "Harmonic_Term")
        protected List<HarmonicTermType> harmonicTerm;
        @XmlAttribute(name = "num", required = true)
        protected BigInteger num;

        /**
         * Gets the value of the harmonicTerm property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the harmonicTerm property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHarmonicTerm().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link HarmonicTermType }
         * 
         * 
         */
        public List<HarmonicTermType> getHarmonicTerm() {
            if (harmonicTerm == null) {
                harmonicTerm = new ArrayList<HarmonicTermType>();
            }
            return this.harmonicTerm;
        }

        /**
         * Obtient la valeur de la propriété num.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getNum() {
            if (num == null) {
                return new BigInteger("2");
            } else {
                return num;
            }
        }

        /**
         * Définit la valeur de la propriété num.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setNum(BigInteger value) {
            this.num = value;
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
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>integer">
     *       &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}String_Type" fixed="orbit" />
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
    public static class LinearApproxValidity {

        @XmlValue
        protected BigInteger value;
        @XmlAttribute(name = "unit", required = true)
        protected String unit;

        /**
         * Obtient la valeur de la propriété value.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setValue(BigInteger value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propriété unit.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUnit() {
            if (unit == null) {
                return "orbit";
            } else {
                return unit;
            }
        }

        /**
         * Définit la valeur de la propriété unit.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUnit(String value) {
            this.unit = value;
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
     *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>Decimal_Type">
     *       &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}String_Type" fixed="s/day^2" />
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
    public static class QuadraticTerm {

        @XmlValue
        protected BigDecimal value;
        @XmlAttribute(name = "unit", required = true)
        protected String unit;

        /**
         * Obtient la valeur de la propriété value.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setValue(BigDecimal value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propriété unit.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUnit() {
            if (unit == null) {
                return "s/day^2";
            } else {
                return unit;
            }
        }

        /**
         * Définit la valeur de la propriété unit.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUnit(String value) {
            this.unit = value;
        }

    }

}
