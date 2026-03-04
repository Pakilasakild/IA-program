package com.ia.ia_base.util;

import javafx.application.Platform;

import java.util.concurrent.CopyOnWriteArrayList;

public final class FlashcardReloadBus {

    private static final CopyOnWriteArrayList<Runnable> listeners = new CopyOnWriteArrayList<>();

    private FlashcardReloadBus() {}

    public static void register(Runnable r) {
        listeners.add(r);
    }

    public static void unregister(Runnable r) {
        listeners.remove(r);
    }

    public static void requestReload() {
        Platform.runLater(() -> listeners.forEach(Runnable::run));
    }
}
