//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.orbital;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour OSV_Type complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="OSV_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TAI" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}TAI_Date_Time_Type"/>
 *         &lt;element name="UTC" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}UTC_Date_Time_Type"/>
 *         &lt;element name="UT1" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}UT1_Date_Time_Type"/>
 *         &lt;element name="Absolute_Orbit" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}PositiveInteger_Type"/>
 *         &lt;element name="X" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Position_Component_Type"/>
 *         &lt;element name="Y" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Position_Component_Type"/>
 *         &lt;element name="Z" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Position_Component_Type"/>
 *         &lt;element name="VX" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Velocity_Component_Type"/>
 *         &lt;element name="VY" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Velocity_Component_Type"/>
 *         &lt;element name="VZ" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Velocity_Component_Type"/>
 *         &lt;element name="Quality" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Quality_Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OSV_Type", propOrder = {
    "tai",
    "utc",
    "ut1",
    "absoluteOrbit",
    "x",
    "y",
    "z",
    "vx",
    "vy",
    "vz",
    "quality"
})
public class OSVType {

    @XmlElement(name = "TAI", required = true)
    protected String tai;
    @XmlElement(name = "UTC", required = true)
    protected String utc;
    @XmlElement(name = "UT1", required = true)
    protected String ut1;
    @XmlElement(name = "Absolute_Orbit", required = true)
    protected BigInteger absoluteOrbit;
    @XmlElement(name = "X", required = true)
    protected PositionComponentType x;
    @XmlElement(name = "Y", required = true)
    protected PositionComponentType y;
    @XmlElement(name = "Z", required = true)
    protected PositionComponentType z;
    @XmlElement(name = "VX", required = true)
    protected VelocityComponentType vx;
    @XmlElement(name = "VY", required = true)
    protected VelocityComponentType vy;
    @XmlElement(name = "VZ", required = true)
    protected VelocityComponentType vz;
    @XmlElement(name = "Quality", required = true)
    protected String quality;

    /**
     * Obtient la valeur de la propriété tai.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTAI() {
        return tai;
    }

    /**
     * Définit la valeur de la propriété tai.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTAI(String value) {
        this.tai = value;
    }

    /**
     * Obtient la valeur de la propriété utc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUTC() {
        return utc;
    }

    /**
     * Définit la valeur de la propriété utc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUTC(String value) {
        this.utc = value;
    }

    /**
     * Obtient la valeur de la propriété ut1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUT1() {
        return ut1;
    }

    /**
     * Définit la valeur de la propriété ut1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUT1(String value) {
        this.ut1 = value;
    }

    /**
     * Obtient la valeur de la propriété absoluteOrbit.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAbsoluteOrbit() {
        return absoluteOrbit;
    }

    /**
     * Définit la valeur de la propriété absoluteOrbit.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAbsoluteOrbit(BigInteger value) {
        this.absoluteOrbit = value;
    }

    /**
     * Obtient la valeur de la propriété x.
     * 
     * @return
     *     possible object is
     *     {@link PositionComponentType }
     *     
     */
    public PositionComponentType getX() {
        return x;
    }

    /**
     * Définit la valeur de la propriété x.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionComponentType }
     *     
     */
    public void setX(PositionComponentType value) {
        this.x = value;
    }

    /**
     * Obtient la valeur de la propriété y.
     * 
     * @return
     *     possible object is
     *     {@link PositionComponentType }
     *     
     */
    public PositionComponentType getY() {
        return y;
    }

    /**
     * Définit la valeur de la propriété y.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionComponentType }
     *     
     */
    public void setY(PositionComponentType value) {
        this.y = value;
    }

    /**
     * Obtient la valeur de la propriété z.
     * 
     * @return
     *     possible object is
     *     {@link PositionComponentType }
     *     
     */
    public PositionComponentType getZ() {
        return z;
    }

    /**
     * Définit la valeur de la propriété z.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionComponentType }
     *     
     */
    public void setZ(PositionComponentType value) {
        this.z = value;
    }

    /**
     * Obtient la valeur de la propriété vx.
     * 
     * @return
     *     possible object is
     *     {@link VelocityComponentType }
     *     
     */
    public VelocityComponentType getVX() {
        return vx;
    }

    /**
     * Définit la valeur de la propriété vx.
     * 
     * @param value
     *     allowed object is
     *     {@link VelocityComponentType }
     *     
     */
    public void setVX(VelocityComponentType value) {
        this.vx = value;
    }

    /**
     * Obtient la valeur de la propriété vy.
     * 
     * @return
     *     possible object is
     *     {@link VelocityComponentType }
     *     
     */
    public VelocityComponentType getVY() {
        return vy;
    }

    /**
     * Définit la valeur de la propriété vy.
     * 
     * @param value
     *     allowed object is
     *     {@link VelocityComponentType }
     *     
     */
    public void setVY(VelocityComponentType value) {
        this.vy = value;
    }

    /**
     * Obtient la valeur de la propriété vz.
     * 
     * @return
     *     possible object is
     *     {@link VelocityComponentType }
     *     
     */
    public VelocityComponentType getVZ() {
        return vz;
    }

    /**
     * Définit la valeur de la propriété vz.
     * 
     * @param value
     *     allowed object is
     *     {@link VelocityComponentType }
     *     
     */
    public void setVZ(VelocityComponentType value) {
        this.vz = value;
    }

    /**
     * Obtient la valeur de la propriété quality.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuality() {
        return quality;
    }

    /**
     * Définit la valeur de la propriété quality.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuality(String value) {
        this.quality = value;
    }

}
