package com.shopearn.global;

import com.google.android.gms.tagmanager.ContainerHolder;

/**
 * Created by apple on 16/01/17.
 */

public class ContainerHolderSingleton {

    private static ContainerHolder containerHolder;

    /**
     * Utility class; don't instantiate.
     */
    private ContainerHolderSingleton() {
    }

    public static ContainerHolder getContainerHolder() {
        return containerHolder;
    }

    public static void setContainerHolder(ContainerHolder c) {
        containerHolder = c;
    }
}
