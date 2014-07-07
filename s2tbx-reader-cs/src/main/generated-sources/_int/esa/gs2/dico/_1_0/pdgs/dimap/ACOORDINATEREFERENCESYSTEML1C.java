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
import _int.esa.gs2.dico._1_0.sy.representation.AGEOTABLES;
import _int.esa.gs2.dico._1_0.sy.representation.ANHORIZONTALCSTYPES;


/**
 * Description of the coordinate reference system used in the dataset. Set to WGS84 by Init_Loc_Prod and updated accordingly to requested product by RESAMPLE_S2.
 * 
 * <p>Classe Java pour A_COORDINATE_REFERENCE_SYSTEM_L1C complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_COORDINATE_REFERENCE_SYSTEM_L1C">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GEO_TABLES" type="{http://gs2.esa.int/DICO/1.0/SY/representation/}A_GEO_TABLES"/>
 *         &lt;element name="HORIZONTAL_CS_TYPE" type="{http://gs2.esa.int/DICO/1.0/SY/representation/}AN_HORIZONTAL_CS_TYPES"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_COORDINATE_REFERENCE_SYSTEM_L1C", propOrder = {
    "geotables",
    "horizontalcstype"
})
public class ACOORDINATEREFERENCESYSTEML1C {

    @XmlElement(name = "GEO_TABLES", required = true)
    protected AGEOTABLES geotables;
    @XmlElement(name = "HORIZONTAL_CS_TYPE", required = true)
    protected ANHORIZONTALCSTYPES horizontalcstype;

    /**
     * Obtient la valeur de la propriété geotables.
     * 
     * @return
     *     possible object is
     *     {@link AGEOTABLES }
     *     
     */
    public AGEOTABLES getGEOTABLES() {
        return geotables;
    }

    /**
     * Définit la valeur de la propriété geotables.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOTABLES }
     *     
     */
    public void setGEOTABLES(AGEOTABLES value) {
        this.geotables = value;
    }

    /**
     * Obtient la valeur de la propriété horizontalcstype.
     * 
     * @return
     *     possible object is
     *     {@link ANHORIZONTALCSTYPES }
     *     
     */
    public ANHORIZONTALCSTYPES getHORIZONTALCSTYPE() {
        return horizontalcstype;
    }

    /**
     * Définit la valeur de la propriété horizontalcstype.
     * 
     * @param value
     *     allowed object is
     *     {@link ANHORIZONTALCSTYPES }
     *     
     */
    public void setHORIZONTALCSTYPE(ANHORIZONTALCSTYPES value) {
        this.horizontalcstype = value;
    }

}
