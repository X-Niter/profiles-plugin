package com.awooga.ProfilesPaperGui.chestgui;


import com.awooga.ProfilesPaperGui.ProfilesPaperGui;

public class ChestGuiGenerator {

    ProfilesPaperGui plugin;
    public ChestGuiGenerator(ProfilesPaperGui main) {
        plugin = main;
    }

    public <S> ChestGui<S> createNewGui(String configKey, StatefulItemStackSupplier<S> supplier) {
        return ChestGui.<S>builder()
            .supplier(supplier)
            .configKey(configKey)
        .build();
    }
}
