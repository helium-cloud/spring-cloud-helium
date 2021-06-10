
package org.helium.common.compiler;

import org.helium.common.extension.SPI;

/**
 * Compiler. (SPI, Singleton, ThreadSafe)
 */
@SPI("jdk")
public interface Compiler {

    /**
     * Compile java source code.
     *
     * @param code        Java source code
     * @param classLoader classloader
     * @return Compiled class
     */
    Class<?> compile(String code, ClassLoader classLoader);

}
