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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.image.ADATATAKETYPE;
import _int.esa.gs2.dico._1_0.sy.orbital.ANORBITDIRECTION;


/**
 * <p>Classe Java pour A_DATATAKE_IDENTIFICATION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DATATAKE_IDENTIFICATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SPACECRAFT_NAME">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/PDGS/base/>A_SATELLITE_ID">
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DATATAKE_TYPE" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DATATAKE_TYPE"/>
 *         &lt;element name="DATATAKE_SENSING_START" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_DATE_TIME"/>
 *         &lt;element name="SENSING_ORBIT_NUMBER">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="1"/>
 *               &lt;maxInclusive value="143"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="SENSING_ORBIT_DIRECTION" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}AN_ORBIT_DIRECTION" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="datatakeIdentifier" use="required" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}DATATAKE_ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_DATATAKE_IDENTIFICATION", propOrder = {
    "spacecraftname",
    "datataketype",
    "datatakesensingstart",
    "sensingorbitnumber",
    "sensingorbitdirection"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGENERALINFODS.DatatakeInfo.class
})
public class ADATATAKEIDENTIFICATION {

    @XmlElement(name = "SPACECRAFT_NAME", required = true)
    protected ADATATAKEIDENTIFICATION.SPACECRAFTNAME spacecraftname;
    @XmlElement(name = "DATATAKE_TYPE", required = true)
    protected ADATATAKETYPE datataketype;
    @XmlElement(name = "DATATAKE_SENSING_START", required = true)
    protected XMLGregorianCalendar datatakesensingstart;
    @XmlElement(name = "SENSING_ORBIT_NUMBER")
    protected int sensingorbitnumber;
    @XmlElement(name = "SENSING_ORBIT_DIRECTION", defaultValue = "ASCENDING")
    protected ANORBITDIRECTION sensingorbitdirection;
    @XmlAttribute(name = "datatakeIdentifier", required = true)
    protected String datatakeIdentifier;

    /**
     * Obtient la valeur de la propriété spacecraftname.
     * 
     * @return
     *     possible object is
     *     {@link ADATATAKEIDENTIFICATION.SPACECRAFTNAME }
     *     
     */
    public ADATATAKEIDENTIFICATION.SPACECRAFTNAME getSPACECRAFTNAME() {
        return spacecraftname;
    }

    /**
     * Définit la valeur de la propriété spacecraftname.
     * 
     * @param value
     *     allowed object is
     *     {@link ADATATAKEIDENTIFICATION.SPACECRAFTNAME }
     *     
     */
    public void setSPACECRAFTNAME(ADATATAKEIDENTIFICATION.SPACECRAFTNAME value) {
        this.spacecraftname = value;
    }

    /**
     * Obtient la valeur de la propriété datataketype.
     * 
     * @return
     *     possible object is
     *     {@link ADATATAKETYPE }
     *     
     */
    public ADATATAKETYPE getDATATAKETYPE() {
        return datataketype;
    }

    /**
     * Définit la valeur de la propriété datataketype.
     * 
     * @param value
     *     allowed object is
     *     {@link ADATATAKETYPE }
     *     
     */
    public void setDATATAKETYPE(ADATATAKETYPE value) {
        this.datataketype = value;
    }

    /**
     * Obtient la valeur de la propriété datatakesensingstart.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDATATAKESENSINGSTART() {
        return datatakesensingstart;
    }

    /**
     * Définit la valeur de la propriété datatakesensingstart.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDATATAKESENSINGSTART(XMLGregorianCalendar value) {
        this.datatakesensingstart = value;
    }

    /**
     * Obtient la valeur de la propriété sensingorbitnumber.
     * 
     */
    public int getSENSINGORBITNUMBER() {
        return sensingorbitnumber;
    }

    /**
     * Définit la valeur de la propriété sensingorbitnumber.
     * 
     */
    public void setSENSINGORBITNUMBER(int value) {
        this.sensingorbitnumber = value;
    }

    /**
     * Obtient la valeur de la propriété sensingorbitdirection.
     * 
     * @return
     *     possible object is
     *     {@link ANORBITDIRECTION }
     *     
     */
    public ANORBITDIRECTION getSENSINGORBITDIRECTION() {
        return sensingorbitdirection;
    }

    /**
     * Définit la valeur de la propriété sensingorbitdirection.
     * 
     * @param value
     *     allowed object is
     *     {@link ANORBITDIRECTION }
     *     
     */
    public void setSENSINGORBITDIRECTION(ANORBITDIRECTION value) {
        this.sensingorbitdirection = value;
    }

    /**
     * Obtient la valeur de la propriété datatakeIdentifier.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatatakeIdentifier() {
        return datatakeIdentifier;
    }

    /**
     * Définit la valeur de la propriété datatakeIdentifier.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatatakeIdentifier(String value) {
        this.datatakeIdentifier = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/PDGS/base/>A_SATELLITE_ID">
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
    public static class SPACECRAFTNAME {

        @XmlValue
        protected String value;

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

    }

}
