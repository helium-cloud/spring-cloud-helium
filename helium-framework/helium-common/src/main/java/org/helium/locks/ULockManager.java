package org.helium.locks;

/**
 * Created by lvmingwei on 16-6-1.
 */
public class ULockManager {

    private static ULockCentre CENTRE;

    public static final boolean initial(ULockCentre centre) {
        CENTRE = centre;
        return true;
    }

    protected static final ULockCentre getCentre() {
        return CENTRE;
    }

}
