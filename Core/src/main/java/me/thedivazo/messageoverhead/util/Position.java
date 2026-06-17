package me.thedivazo.messageoverhead.util;

import java.util.Objects;

public class Position implements Positionc {
    public double x;
    public double y;
    public double z;

    public Position() {
    }

    public Position(double x, double y, double z) {
        set(x, y, z);
    }

    public Position(Positionc position) {
        set(position);
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    public Position set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Position set(Positionc position) {
        Objects.requireNonNull(position);
        return set(position.x(), position.y(), position.z());
    }

    public Position zero() {
        return set(0.0, 0.0, 0.0);
    }

    public Position plus(double x, double y, double z) {
        return set(this.x + x, this.y + y, this.z + z);
    }

    public Position plus(Positionc position) {
        Objects.requireNonNull(position);
        return plus(position.x(), position.y(), position.z());
    }

    public Position minus(double x, double y, double z) {
        return set(this.x - x, this.y - y, this.z - z);
    }

    public Position minus(Positionc position) {
        Objects.requireNonNull(position);
        return minus(position.x(), position.y(), position.z());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Positionc)) {
            return false;
        }
        Positionc position = (Positionc) object;
        return Double.compare(position.x(), x) == 0
                && Double.compare(position.y(), y) == 0
                && Double.compare(position.z(), z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "(" + x + " " + y + " " + z + ")";
    }
}
