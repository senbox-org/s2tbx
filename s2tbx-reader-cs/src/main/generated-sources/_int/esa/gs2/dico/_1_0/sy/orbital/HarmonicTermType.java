//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.orbital;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour Harmonic_Term_Type complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Harmonic_Term_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Reference_Time">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/date_time/>No_Ref_Date_Time_Type">
 *                 &lt;attribute name="time_ref" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}String_Type" fixed="UT1" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Period">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>Decimal_Type">
 *                 &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}String_Type" fixed="day" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Amplitude_Sin">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>Decimal_Type">
 *                 &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}String_Type" fixed="s" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Amplitude_Cos">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>Decimal_Type">
 *                 &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}String_Type" fixed="s" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="seq" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}NonNegativeInteger_Type" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Harmonic_Term_Type", propOrder = {
    "referenceTime",
    "period",
    "amplitudeSin",
    "amplitudeCos"
})
public class HarmonicTermType {

    @XmlElement(name = "Reference_Time", required = true)
    protected HarmonicTermType.ReferenceTime referenceTime;
    @XmlElement(name = "Period", required = true)
    protected HarmonicTermType.Period period;
    @XmlElement(name = "Amplitude_Sin", required = true)
    protected HarmonicTermType.AmplitudeSin amplitudeSin;
    @XmlElement(name = "Amplitude_Cos", required = true)
    protected HarmonicTermType.AmplitudeCos amplitudeCos;
    @XmlAttribute(name = "seq", required = true)
    protected BigInteger seq;

    /**
     * Obtient la valeur de la propriété referenceTime.
     * 
     * @return
     *     possible object is
     *     {@link HarmonicTermType.ReferenceTime }
     *     
     */
    public HarmonicTermType.ReferenceTime getReferenceTime() {
        return referenceTime;
    }

    /**
     * Définit la valeur de la propriété referenceTime.
     * 
     * @param value
     *     allowed object is
     *     {@link HarmonicTermType.ReferenceTime }
     *     
     */
    public void setReferenceTime(HarmonicTermType.ReferenceTime value) {
        this.referenceTime = value;
    }

    /**
     * Obtient la valeur de la propriété period.
     * 
     * @return
     *     possible object is
     *     {@link HarmonicTermType.Period }
     *     
     */
    public HarmonicTermType.Period getPeriod() {
        return period;
    }

    /**
     * Définit la valeur de la propriété period.
     * 
     * @param value
     *     allowed object is
     *     {@link HarmonicTermType.Period }
     *     
     */
    public void setPeriod(HarmonicTermType.Period value) {
        this.period = value;
    }

    /**
     * Obtient la valeur de la propriété amplitudeSin.
     * 
     * @return
     *     possible object is
     *     {@link HarmonicTermType.AmplitudeSin }
     *     
     */
    public HarmonicTermType.AmplitudeSin getAmplitudeSin() {
        return amplitudeSin;
    }

    /**
     * Définit la valeur de la propriété amplitudeSin.
     * 
     * @param value
     *     allowed object is
     *     {@link HarmonicTermType.AmplitudeSin }
     *     
     */
    public void setAmplitudeSin(HarmonicTermType.AmplitudeSin value) {
        this.amplitudeSin = value;
    }

    /**
     * Obtient la valeur de la propriété amplitudeCos.
     * 
     * @return
     *     possible object is
     *     {@link HarmonicTermType.AmplitudeCos }
     *     
     */
    public HarmonicTermType.AmplitudeCos getAmplitudeCos() {
        return amplitudeCos;
    }

    /**
     * Définit la valeur de la propriété amplitudeCos.
     * 
     * @param value
     *     allowed object is
     *     {@link HarmonicTermType.AmplitudeCos }
     *     
     */
    public void setAmplitudeCos(HarmonicTermType.AmplitudeCos value) {
        this.amplitudeCos = value;
    }

    /**
     * Obtient la valeur de la propriété seq.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSeq() {
        return seq;
    }

    /**
     * Définit la valeur de la propriété seq.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSeq(BigInteger value) {
        this.seq = value;
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
     *       &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}String_Type" fixed="s" />
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
    public static class AmplitudeCos {

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
                return "s";
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
     *       &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}String_Type" fixed="s" />
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
    public static class AmplitudeSin {

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
                return "s";
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
     *       &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}String_Type" fixed="day" />
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
    public static class Period {

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
                return "day";
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
     *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/date_time/>No_Ref_Date_Time_Type">
     *       &lt;attribute name="time_ref" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}String_Type" fixed="UT1" />
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
    public static class ReferenceTime {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "time_ref", required = true)
        protected String timeRef;

        /**
         * Obtient la valeur de la propriété value.
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
         * Obtient la valeur de la propriété timeRef.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTimeRef() {
            if (timeRef == null) {
                return "UT1";
            } else {
                return timeRef;
            }
        }

        /**
         * Définit la valeur de la propriété timeRef.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTimeRef(String value) {
            this.timeRef = value;
        }

    }

}
