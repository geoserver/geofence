package org.geoserver.geofence.core.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 */
@XmlRootElement(name = "LayerBoundinBox")
@XmlAccessorType(XmlAccessType.FIELD)
@Embeddable
public class LayerBoundinBox implements Serializable {

    private static final long serialVersionUID = -8205311631705719601L;
    //
    @Column(name = "min_x")
    private double minX;
    //
    @Column(name = "min_y")
    private double minY;
    //
    @Column(name = "max_x")
    private double maxX;
    //
    @Column(name = "max_y")
    private double maxY;

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("LayerBoundinBox {");
        str.append("minX=").append(minX);
        str.append(", minY=").append(minY);
        str.append(", maxX=").append(maxX);
        str.append(", maxY=").append(maxY);
        return str.append('}').toString();
    }
}
