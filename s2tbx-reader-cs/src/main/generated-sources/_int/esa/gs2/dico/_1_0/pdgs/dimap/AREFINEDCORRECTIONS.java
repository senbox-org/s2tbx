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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.sy.image.AFOCALPLANEID;
import _int.esa.gs2.dico._1_0.sy.misc.ANUNCERTAINTIESXYZTYPE;
import _int.esa.gs2.dico._1_0.sy.misc.AROTATIONTRANSLATIONHOMOTHETYUNCERTAINTIESTYPELOWERCASE;


/**
 * <p>Classe Java pour A_REFINED_CORRECTIONS complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_REFINED_CORRECTIONS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Spacecraft_Position" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}AN_UNCERTAINTIES_XYZ_TYPE" minOccurs="0"/>
 *         &lt;element name="MSI_State" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_ROTATION_TRANSLATION_HOMOTHETY_UNCERTAINTIES_TYPE_LOWER_CASE" minOccurs="0"/>
 *         &lt;element name="Focal_Plane_State" maxOccurs="2" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_ROTATION_TRANSLATION_HOMOTHETY_UNCERTAINTIES_TYPE_LOWER_CASE">
 *                 &lt;attribute name="focalPlaneId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_FOCAL_PLANE_ID" />
 *               &lt;/extension>
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
@XmlType(name = "A_REFINED_CORRECTIONS", propOrder = {
    "spacecraftPosition",
    "msiState",
    "focalPlaneState"
})
public class AREFINEDCORRECTIONS {

    @XmlElement(name = "Spacecraft_Position")
    protected ANUNCERTAINTIESXYZTYPE spacecraftPosition;
    @XmlElement(name = "MSI_State")
    protected AROTATIONTRANSLATIONHOMOTHETYUNCERTAINTIESTYPELOWERCASE msiState;
    @XmlElement(name = "Focal_Plane_State")
    protected List<AREFINEDCORRECTIONS.FocalPlaneState> focalPlaneState;

    /**
     * Obtient la valeur de la propriété spacecraftPosition.
     * 
     * @return
     *     possible object is
     *     {@link ANUNCERTAINTIESXYZTYPE }
     *     
     */
    public ANUNCERTAINTIESXYZTYPE getSpacecraftPosition() {
        return spacecraftPosition;
    }

    /**
     * Définit la valeur de la propriété spacecraftPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link ANUNCERTAINTIESXYZTYPE }
     *     
     */
    public void setSpacecraftPosition(ANUNCERTAINTIESXYZTYPE value) {
        this.spacecraftPosition = value;
    }

    /**
     * Obtient la valeur de la propriété msiState.
     * 
     * @return
     *     possible object is
     *     {@link AROTATIONTRANSLATIONHOMOTHETYUNCERTAINTIESTYPELOWERCASE }
     *     
     */
    public AROTATIONTRANSLATIONHOMOTHETYUNCERTAINTIESTYPELOWERCASE getMSIState() {
        return msiState;
    }

    /**
     * Définit la valeur de la propriété msiState.
     * 
     * @param value
     *     allowed object is
     *     {@link AROTATIONTRANSLATIONHOMOTHETYUNCERTAINTIESTYPELOWERCASE }
     *     
     */
    public void setMSIState(AROTATIONTRANSLATIONHOMOTHETYUNCERTAINTIESTYPELOWERCASE value) {
        this.msiState = value;
    }

    /**
     * Gets the value of the focalPlaneState property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the focalPlaneState property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFocalPlaneState().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AREFINEDCORRECTIONS.FocalPlaneState }
     * 
     * 
     */
    public List<AREFINEDCORRECTIONS.FocalPlaneState> getFocalPlaneState() {
        if (focalPlaneState == null) {
            focalPlaneState = new ArrayList<AREFINEDCORRECTIONS.FocalPlaneState>();
        }
        return this.focalPlaneState;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_ROTATION_TRANSLATION_HOMOTHETY_UNCERTAINTIES_TYPE_LOWER_CASE">
     *       &lt;attribute name="focalPlaneId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_FOCAL_PLANE_ID" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class FocalPlaneState
        extends AROTATIONTRANSLATIONHOMOTHETYUNCERTAINTIESTYPELOWERCASE
    {

        @XmlAttribute(name = "focalPlaneId", required = true)
        protected AFOCALPLANEID focalPlaneId;

        /**
         * Obtient la valeur de la propriété focalPlaneId.
         * 
         * @return
         *     possible object is
         *     {@link AFOCALPLANEID }
         *     
         */
        public AFOCALPLANEID getFocalPlaneId() {
            return focalPlaneId;
        }

        /**
         * Définit la valeur de la propriété focalPlaneId.
         * 
         * @param value
         *     allowed object is
         *     {@link AFOCALPLANEID }
         *     
         */
        public void setFocalPlaneId(AFOCALPLANEID value) {
            this.focalPlaneId = value;
        }

    }

}
