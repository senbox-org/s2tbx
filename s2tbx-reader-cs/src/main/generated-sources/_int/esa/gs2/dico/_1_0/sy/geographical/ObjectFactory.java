//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.geographical;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.gs2.dico._1_0.sy.geographical package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AGMLPOLYGON3DINTPOSLIST_QNAME = new QName("", "INT_POS_LIST");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.gs2.dico._1_0.sy.geographical
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PointingDirectionType }
     * 
     */
    public PointingDirectionType createPointingDirectionType() {
        return new PointingDirectionType();
    }

    /**
     * Create an instance of {@link MispointingAnglesType }
     * 
     */
    public MispointingAnglesType createMispointingAnglesType() {
        return new MispointingAnglesType();
    }

    /**
     * Create an instance of {@link LatitudeType }
     * 
     */
    public LatitudeType createLatitudeType() {
        return new LatitudeType();
    }

    /**
     * Create an instance of {@link APOINTCOORDINATES }
     * 
     */
    public APOINTCOORDINATES createAPOINTCOORDINATES() {
        return new APOINTCOORDINATES();
    }

    /**
     * Create an instance of {@link AzimuthType }
     * 
     */
    public AzimuthType createAzimuthType() {
        return new AzimuthType();
    }

    /**
     * Create an instance of {@link ElevationType }
     * 
     */
    public ElevationType createElevationType() {
        return new ElevationType();
    }

    /**
     * Create an instance of {@link LongitudeType }
     * 
     */
    public LongitudeType createLongitudeType() {
        return new LongitudeType();
    }

    /**
     * Create an instance of {@link GeoLocation2DType }
     * 
     */
    public GeoLocation2DType createGeoLocation2DType() {
        return new GeoLocation2DType();
    }

    /**
     * Create an instance of {@link GeoLocationType }
     * 
     */
    public GeoLocationType createGeoLocationType() {
        return new GeoLocationType();
    }

    /**
     * Create an instance of {@link AGMLPOLYGON3D }
     * 
     */
    public AGMLPOLYGON3D createAGMLPOLYGON3D() {
        return new AGMLPOLYGON3D();
    }

    /**
     * Create an instance of {@link AGMLPOLYGON2D }
     * 
     */
    public AGMLPOLYGON2D createAGMLPOLYGON2D() {
        return new AGMLPOLYGON2D();
    }

    /**
     * Create an instance of {@link PolygonTypeType }
     * 
     */
    public PolygonTypeType createPolygonTypeType() {
        return new PolygonTypeType();
    }

    /**
     * Create an instance of {@link ALATLONPOLYGON }
     * 
     */
    public ALATLONPOLYGON createALATLONPOLYGON() {
        return new ALATLONPOLYGON();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "INT_POS_LIST", scope = AGMLPOLYGON3D.class)
    public JAXBElement<List<Double>> createAGMLPOLYGON3DINTPOSLIST(List<Double> value) {
        return new JAXBElement<List<Double>>(_AGMLPOLYGON3DINTPOSLIST_QNAME, ((Class) List.class), AGMLPOLYGON3D.class, ((List<Double> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "INT_POS_LIST", scope = AGMLPOLYGON2D.class)
    public JAXBElement<List<Double>> createAGMLPOLYGON2DINTPOSLIST(List<Double> value) {
        return new JAXBElement<List<Double>>(_AGMLPOLYGON3DINTPOSLIST_QNAME, ((Class) List.class), AGMLPOLYGON2D.class, ((List<Double> ) value));
    }

}
