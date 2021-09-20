package com.awooga.ProfilesPaperGui.chestgui;

@FunctionalInterface
public interface StatefulItemStackSupplier<S> {
    StatefulItemStack<S> get(Integer slot, String legendName, TextSupplier getText);
}
