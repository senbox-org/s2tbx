//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.sy.misc.DistanceType;


/**
 * <p>Classe Java pour AN_AREA_OF_INTEREST complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_AREA_OF_INTEREST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Bbox">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="LOWER_CORNER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_2_DOUBLE"/>
 *                     &lt;element name="APPER_CORNER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_2_DOUBLE"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="Polygon">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="EXT_POS_LIST" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="Radius">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="CENTER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_2_DOUBLE"/>
 *                     &lt;element name="RADIUS_LENGHT" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}Distance_Type"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_AREA_OF_INTEREST", propOrder = {
    "bbox",
    "polygon",
    "radius"
})
public class ANAREAOFINTEREST {

    @XmlElement(name = "Bbox")
    protected ANAREAOFINTEREST.Bbox bbox;
    @XmlElement(name = "Polygon")
    protected ANAREAOFINTEREST.Polygon polygon;
    @XmlElement(name = "Radius")
    protected ANAREAOFINTEREST.Radius radius;

    /**
     * Obtient la valeur de la propriété bbox.
     * 
     * @return
     *     possible object is
     *     {@link ANAREAOFINTEREST.Bbox }
     *     
     */
    public ANAREAOFINTEREST.Bbox getBbox() {
        return bbox;
    }

    /**
     * Définit la valeur de la propriété bbox.
     * 
     * @param value
     *     allowed object is
     *     {@link ANAREAOFINTEREST.Bbox }
     *     
     */
    public void setBbox(ANAREAOFINTEREST.Bbox value) {
        this.bbox = value;
    }

    /**
     * Obtient la valeur de la propriété polygon.
     * 
     * @return
     *     possible object is
     *     {@link ANAREAOFINTEREST.Polygon }
     *     
     */
    public ANAREAOFINTEREST.Polygon getPolygon() {
        return polygon;
    }

    /**
     * Définit la valeur de la propriété polygon.
     * 
     * @param value
     *     allowed object is
     *     {@link ANAREAOFINTEREST.Polygon }
     *     
     */
    public void setPolygon(ANAREAOFINTEREST.Polygon value) {
        this.polygon = value;
    }

    /**
     * Obtient la valeur de la propriété radius.
     * 
     * @return
     *     possible object is
     *     {@link ANAREAOFINTEREST.Radius }
     *     
     */
    public ANAREAOFINTEREST.Radius getRadius() {
        return radius;
    }

    /**
     * Définit la valeur de la propriété radius.
     * 
     * @param value
     *     allowed object is
     *     {@link ANAREAOFINTEREST.Radius }
     *     
     */
    public void setRadius(ANAREAOFINTEREST.Radius value) {
        this.radius = value;
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
     *         &lt;element name="LOWER_CORNER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_2_DOUBLE"/>
     *         &lt;element name="APPER_CORNER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_2_DOUBLE"/>
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
        "lowercorner",
        "appercorner"
    })
    public static class Bbox {

        @XmlList
        @XmlElement(name = "LOWER_CORNER", type = Double.class)
        protected List<Double> lowercorner;
        @XmlList
        @XmlElement(name = "APPER_CORNER", type = Double.class)
        protected List<Double> appercorner;

        /**
         * Gets the value of the lowercorner property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the lowercorner property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLOWERCORNER().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Double }
         * 
         * 
         */
        public List<Double> getLOWERCORNER() {
            if (lowercorner == null) {
                lowercorner = new ArrayList<Double>();
            }
            return this.lowercorner;
        }

        /**
         * Gets the value of the appercorner property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the appercorner property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAPPERCORNER().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Double }
         * 
         * 
         */
        public List<Double> getAPPERCORNER() {
            if (appercorner == null) {
                appercorner = new ArrayList<Double>();
            }
            return this.appercorner;
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
     *         &lt;element name="EXT_POS_LIST" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE"/>
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
        "extposlist"
    })
    public static class Polygon {

        @XmlList
        @XmlElement(name = "EXT_POS_LIST", type = Double.class)
        protected List<Double> extposlist;

        /**
         * Gets the value of the extposlist property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the extposlist property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEXTPOSLIST().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Double }
         * 
         * 
         */
        public List<Double> getEXTPOSLIST() {
            if (extposlist == null) {
                extposlist = new ArrayList<Double>();
            }
            return this.extposlist;
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
     *         &lt;element name="CENTER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_2_DOUBLE"/>
     *         &lt;element name="RADIUS_LENGHT" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}Distance_Type"/>
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
        "center",
        "radiuslenght"
    })
    public static class Radius {

        @XmlList
        @XmlElement(name = "CENTER", type = Double.class)
        protected List<Double> center;
        @XmlElement(name = "RADIUS_LENGHT", required = true)
        protected DistanceType radiuslenght;

        /**
         * Gets the value of the center property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the center property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCENTER().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Double }
         * 
         * 
         */
        public List<Double> getCENTER() {
            if (center == null) {
                center = new ArrayList<Double>();
            }
            return this.center;
        }

        /**
         * Obtient la valeur de la propriété radiuslenght.
         * 
         * @return
         *     possible object is
         *     {@link DistanceType }
         *     
         */
        public DistanceType getRADIUSLENGHT() {
            return radiuslenght;
        }

        /**
         * Définit la valeur de la propriété radiuslenght.
         * 
         * @param value
         *     allowed object is
         *     {@link DistanceType }
         *     
         */
        public void setRADIUSLENGHT(DistanceType value) {
            this.radiuslenght = value;
        }

    }

}
