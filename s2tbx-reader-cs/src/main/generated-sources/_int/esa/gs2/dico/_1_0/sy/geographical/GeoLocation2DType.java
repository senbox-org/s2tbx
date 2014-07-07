//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.geographical;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour Geo_Location_2D_Type complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Geo_Location_2D_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Long" type="{http://gs2.esa.int/DICO/1.0/SY/geographical/}Longitude_Type"/>
 *         &lt;element name="Lat" type="{http://gs2.esa.int/DICO/1.0/SY/geographical/}Latitude_Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Geo_Location_2D_Type", propOrder = {
    "_long",
    "lat"
})
@XmlSeeAlso({
    GeoLocationType.class
})
public class GeoLocation2DType {

    @XmlElement(name = "Long", required = true)
    protected LongitudeType _long;
    @XmlElement(name = "Lat", required = true)
    protected LatitudeType lat;

    /**
     * Obtient la valeur de la propriété long.
     * 
     * @return
     *     possible object is
     *     {@link LongitudeType }
     *     
     */
    public LongitudeType getLong() {
        return _long;
    }

    /**
     * Définit la valeur de la propriété long.
     * 
     * @param value
     *     allowed object is
     *     {@link LongitudeType }
     *     
     */
    public void setLong(LongitudeType value) {
        this._long = value;
    }

    /**
     * Obtient la valeur de la propriété lat.
     * 
     * @return
     *     possible object is
     *     {@link LatitudeType }
     *     
     */
    public LatitudeType getLat() {
        return lat;
    }

    /**
     * Définit la valeur de la propriété lat.
     * 
     * @param value
     *     allowed object is
     *     {@link LatitudeType }
     *     
     */
    public void setLat(LatitudeType value) {
        this.lat = value;
    }

}
