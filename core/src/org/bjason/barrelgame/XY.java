package org.bjason.barrelgame;


import java.util.Objects;

class XY {
    int x;
    int y;

    public XY(float x, float y) {
        this.x = (int)x;
        this.y = (int)y;
    }

    @Override
    public String toString() {
        return "XY{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ) return false;
        XY xy = (XY) o;
        return x == xy.x && y == xy.y;
    }

    @Override
    public int hashCode() {
        return y*65536 + x;
    }
}