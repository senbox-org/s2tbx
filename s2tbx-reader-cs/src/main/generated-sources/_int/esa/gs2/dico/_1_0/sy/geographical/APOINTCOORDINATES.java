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
import javax.xml.bind.annotation.XmlType;


/**
 * A Lat/Lon coordinate
 * 
 * <p>Classe Java pour A_POINT_COORDINATES complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_POINT_COORDINATES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LATITUDE" type="{http://gs2.esa.int/DICO/1.0/SY/geographical/}A_LATITUDE"/>
 *         &lt;element name="LONGITUDE" type="{http://gs2.esa.int/DICO/1.0/SY/geographical/}A_LONGITUDE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_POINT_COORDINATES", propOrder = {
    "latitude",
    "longitude"
})
public class APOINTCOORDINATES {

    @XmlElement(name = "LATITUDE")
    protected double latitude;
    @XmlElement(name = "LONGITUDE")
    protected double longitude;

    /**
     * Obtient la valeur de la propriété latitude.
     * 
     */
    public double getLATITUDE() {
        return latitude;
    }

    /**
     * Définit la valeur de la propriété latitude.
     * 
     */
    public void setLATITUDE(double value) {
        this.latitude = value;
    }

    /**
     * Obtient la valeur de la propriété longitude.
     * 
     */
    public double getLONGITUDE() {
        return longitude;
    }

    /**
     * Définit la valeur de la propriété longitude.
     * 
     */
    public void setLONGITUDE(double value) {
        this.longitude = value;
    }

}
