package org.helium.common.extension.activate.impl;

import org.helium.common.extension.activate.ActivateWrapperExt1;


public class ActivateWrapperExt1Wrapper implements ActivateWrapperExt1 {
    private ActivateWrapperExt1 activateWrapperExt1;

    public ActivateWrapperExt1Wrapper(ActivateWrapperExt1 activateWrapperExt1) {
        this.activateWrapperExt1 = activateWrapperExt1;
    }

    @Override
    public String echo(String msg) {
        return activateWrapperExt1.echo(msg);
    }
}
