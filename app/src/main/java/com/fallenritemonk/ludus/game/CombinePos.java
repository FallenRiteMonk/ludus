package com.fallenritemonk.ludus.game;

/**
 * Created by FallenRiteMonk on 9/19/15.
 */
class CombinePos {
    private final int id1;
    private final int id2;

    public CombinePos(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CombinePos that = (CombinePos) o;

        return that.getId1() == id1 && that.getId2() == id2 || that.getId2() == id1 && that.getId1() == id2;

    }

    /*public boolean equals(CombinePos combine) {
        return combine.getId1() == id1 && combine.getId2() == id2 || combine.getId2() == id1 && combine.getId1() == id2;
    }*/

    public int getId1() {
        return id1;
    }

    public int getId2() {
        return id2;
    }
}
