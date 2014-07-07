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
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHARCSECUNITATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHSUNITATTR;


/**
 * <p>Classe Java pour AN_IERS_BULLETIN complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_IERS_BULLETIN">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UT1_UTC">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;restriction base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_S_UNIT_ATTR">
 *                 &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_S_UNIT" />
 *               &lt;/restriction>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GPS_TIME_UTC" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_S_UNIT_ATTR"/>
 *         &lt;element name="GPS_TIME_TAI">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;restriction base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_S_UNIT_ATTR">
 *               &lt;/restriction>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="POLE_U_ANGLE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_ARCSEC_UNIT_ATTR"/>
 *         &lt;element name="POLE_V_ANGLE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_ARCSEC_UNIT_ATTR"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_IERS_BULLETIN", propOrder = {
    "ut1UTC",
    "gpstimeutc",
    "gpstimetai",
    "poleuangle",
    "polevangle"
})
public class ANIERSBULLETIN {

    @XmlElement(name = "UT1_UTC", required = true)
    protected ANIERSBULLETIN.UT1UTC ut1UTC;
    @XmlElement(name = "GPS_TIME_UTC", required = true)
    protected ADOUBLEWITHSUNITATTR gpstimeutc;
    @XmlElement(name = "GPS_TIME_TAI", required = true)
    protected ANIERSBULLETIN.GPSTIMETAI gpstimetai;
    @XmlElement(name = "POLE_U_ANGLE", required = true)
    protected ADOUBLEWITHARCSECUNITATTR poleuangle;
    @XmlElement(name = "POLE_V_ANGLE", required = true)
    protected ADOUBLEWITHARCSECUNITATTR polevangle;

    /**
     * Obtient la valeur de la propriété ut1UTC.
     * 
     * @return
     *     possible object is
     *     {@link ANIERSBULLETIN.UT1UTC }
     *     
     */
    public ANIERSBULLETIN.UT1UTC getUT1UTC() {
        return ut1UTC;
    }

    /**
     * Définit la valeur de la propriété ut1UTC.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIERSBULLETIN.UT1UTC }
     *     
     */
    public void setUT1UTC(ANIERSBULLETIN.UT1UTC value) {
        this.ut1UTC = value;
    }

    /**
     * Obtient la valeur de la propriété gpstimeutc.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHSUNITATTR }
     *     
     */
    public ADOUBLEWITHSUNITATTR getGPSTIMEUTC() {
        return gpstimeutc;
    }

    /**
     * Définit la valeur de la propriété gpstimeutc.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHSUNITATTR }
     *     
     */
    public void setGPSTIMEUTC(ADOUBLEWITHSUNITATTR value) {
        this.gpstimeutc = value;
    }

    /**
     * Obtient la valeur de la propriété gpstimetai.
     * 
     * @return
     *     possible object is
     *     {@link ANIERSBULLETIN.GPSTIMETAI }
     *     
     */
    public ANIERSBULLETIN.GPSTIMETAI getGPSTIMETAI() {
        return gpstimetai;
    }

    /**
     * Définit la valeur de la propriété gpstimetai.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIERSBULLETIN.GPSTIMETAI }
     *     
     */
    public void setGPSTIMETAI(ANIERSBULLETIN.GPSTIMETAI value) {
        this.gpstimetai = value;
    }

    /**
     * Obtient la valeur de la propriété poleuangle.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHARCSECUNITATTR }
     *     
     */
    public ADOUBLEWITHARCSECUNITATTR getPOLEUANGLE() {
        return poleuangle;
    }

    /**
     * Définit la valeur de la propriété poleuangle.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHARCSECUNITATTR }
     *     
     */
    public void setPOLEUANGLE(ADOUBLEWITHARCSECUNITATTR value) {
        this.poleuangle = value;
    }

    /**
     * Obtient la valeur de la propriété polevangle.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHARCSECUNITATTR }
     *     
     */
    public ADOUBLEWITHARCSECUNITATTR getPOLEVANGLE() {
        return polevangle;
    }

    /**
     * Définit la valeur de la propriété polevangle.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHARCSECUNITATTR }
     *     
     */
    public void setPOLEVANGLE(ADOUBLEWITHARCSECUNITATTR value) {
        this.polevangle = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;restriction base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_S_UNIT_ATTR">
     *     &lt;/restriction>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GPSTIMETAI
        extends ADOUBLEWITHSUNITATTR
    {


    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;restriction base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_S_UNIT_ATTR">
     *       &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_S_UNIT" />
     *     &lt;/restriction>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class UT1UTC
        extends ADOUBLEWITHSUNITATTR
    {


    }

}
