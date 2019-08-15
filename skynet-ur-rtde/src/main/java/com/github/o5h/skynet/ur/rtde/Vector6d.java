package com.github.o5h.skynet.ur.rtde;

public class Vector6d {
    public double x, y, z, rx, ry, rz;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector6d vector6d = (Vector6d) o;

        if (Double.compare(vector6d.x, x) != 0) return false;
        if (Double.compare(vector6d.y, y) != 0) return false;
        if (Double.compare(vector6d.z, z) != 0) return false;
        if (Double.compare(vector6d.rx, rx) != 0) return false;
        if (Double.compare(vector6d.ry, ry) != 0) return false;
        return Double.compare(vector6d.rz, rz) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rx);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ry);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rz);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "," + rx + "," + ry + "," + rz + "]";
    }
}
