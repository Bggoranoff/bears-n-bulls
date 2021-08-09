package com.github.bggoranoff.tradegame.observable;

class ValueKey {
    private ValueName valueName;

    public ValueKey( ValueName valueName ) {
        this.valueName = valueName;
    }

    public ValueName getKey() { return valueName; }
}