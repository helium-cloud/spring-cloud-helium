package org.helium.common;

import org.helium.common.extension.SPI;

/**
 * @author nick
 **/
@SPI
public interface ByteDecode {
    byte[] decode(byte[] bytes) throws Exception;
}
