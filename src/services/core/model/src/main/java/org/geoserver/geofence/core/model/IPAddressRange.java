/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import org.geoserver.geofence.core.model.util.SubnetV4Utils;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "IPAddressRange")
@Embeddable
public class IPAddressRange implements Serializable {

    /**
     * The lower 64 bits.
     * For IPv4, only the lower 32 are used.
     */
    private Long low;
    /**
     * The higher 64 bits.
     * For IPv4, this is null
     */
    private Long high;

    /**
     * CIDR based prefix size.
     * It's equivalent to the number of leading 1 bits in the routing prefix mask.
     * http://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing
     */
    private Integer size;

    protected IPAddressRange() {
    }

    public IPAddressRange(String cidrNotation) {
        SubnetV4Utils su = new SubnetV4Utils(cidrNotation);

        low = new Long(su.getInfo().getAddressAsInteger());
        size = su.getInfo().getMaskSize();
    }


    public boolean match(String address) {
        if( ! SubnetV4Utils.isAddress(address))
            return false;

        SubnetV4Utils su = new SubnetV4Utils(low, size);
        return su.getInfo().isInRange(address);
    }


    public boolean match(InetAddress address) {
        if(address instanceof Inet4Address ) {
            return match((Inet4Address)address);        
        } else {
            throw new UnsupportedOperationException("IPv6 non implemented yet");
        }
    }

    public boolean match(Inet4Address address) {
        SubnetV4Utils su = new SubnetV4Utils(low, size);
        return su.getInfo().isInRange(address.getHostAddress());
    }

    public String getAddress() {
        return high == null?
                encodeIPv4():
                encodeIPv6();
    }

    /**
     * @return the range in CIDR format: x.y.z.w/sz
     */
    public String getCidrSignature() {
        if(high == null) {
            SubnetV4Utils su = new SubnetV4Utils(low, size);
            return su.getInfo().getCidrSignature();
        } else {
            throw new UnsupportedOperationException("IPv6 non implemented yet");
        }
    }



    protected String encodeIPv4() {
        SubnetV4Utils su = new SubnetV4Utils(low, size);
        return su.getInfo().getAddress();
    }

    protected String encodeIPv6() {
        throw new UnsupportedOperationException("IPv6 non implemented yet");
    }

    /**
     * The lower 64 bits.
     * For IPv4, only the lower 32 are used.
     */
    public Long getLow() {
        return low;
    }

    public void setLow(Long low) {
        this.low = low;
    }

    /**
     * The higher 64 bits.
     * For IPv4, this is null
     */
    public Long getHigh() {
        return high;
    }

    public void setHigh(Long high) {
        this.high = high;
    }

    /**
     * CIDR based prefix size.
     * It's equivalent to the number of leading 1 bits in the routing prefix mask.
     * http://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing
     */
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.low != null ? this.low.hashCode() : 0);
        hash = 43 * hash + (this.high != null ? this.high.hashCode() : 0);
        hash = 43 * hash + (this.size != null ? this.size.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IPAddressRange other = (IPAddressRange) obj;
        if (this.low != other.low && (this.low == null || !this.low.equals(other.low))) {
            return false;
        }
        if (this.high != other.high && (this.high == null || !this.high.equals(other.high))) {
            return false;
        }
        if (this.size != other.size && (this.size == null || !this.size.equals(other.size))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getCidrSignature();
    }


}
