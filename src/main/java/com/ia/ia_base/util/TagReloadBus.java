package com.ia.ia_base.util;

import java.util.ArrayList;
import java.util.List;

public class TagReloadBus {
    private static final List<Runnable> listeners = new ArrayList<>();

    public static void register(Runnable listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void unregister(Runnable listener) {
        listeners.remove(listener);
    }

    public static void notifyReload() {
        List<Runnable> copy = new ArrayList<>(listeners);
        for (Runnable listener : copy) {
            listener.run();
        }
    }
}