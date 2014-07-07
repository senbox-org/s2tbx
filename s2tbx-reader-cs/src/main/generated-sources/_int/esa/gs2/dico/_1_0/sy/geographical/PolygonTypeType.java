//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.geographical;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour Polygon_Type_Type complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Polygon_Type_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Polygon_Pt" type="{http://gs2.esa.int/DICO/1.0/SY/geographical/}Geo_Location_2D_Type" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="count" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}NonNegativeInteger_Type" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Polygon_Type_Type", propOrder = {
    "polygonPt"
})
public class PolygonTypeType {

    @XmlElement(name = "Polygon_Pt")
    protected List<GeoLocation2DType> polygonPt;
    @XmlAttribute(name = "count", required = true)
    protected BigInteger count;

    /**
     * Gets the value of the polygonPt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the polygonPt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolygonPt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GeoLocation2DType }
     * 
     * 
     */
    public List<GeoLocation2DType> getPolygonPt() {
        if (polygonPt == null) {
            polygonPt = new ArrayList<GeoLocation2DType>();
        }
        return this.polygonPt;
    }

    /**
     * Obtient la valeur de la propriété count.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCount() {
        return count;
    }

    /**
     * Définit la valeur de la propriété count.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCount(BigInteger value) {
        this.count = value;
    }

}
