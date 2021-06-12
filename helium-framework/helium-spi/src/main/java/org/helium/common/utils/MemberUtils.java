
package org.helium.common.utils;

import java.lang.reflect.*;

/**
 * Java Reflection {@link Member} Utilities class
 *
 * @since 2.7.6
 */
public interface MemberUtils {

    /**
     * check the specified {@link Member member} is static or not ?
     *
     * @param member {@link Member} instance, e.g, {@link Constructor}, {@link Method} or {@link Field}
     * @return Iff <code>member</code> is static one, return <code>true</code>, or <code>false</code>
     */
    static boolean isStatic(Member member) {
        return member != null && Modifier.isStatic(member.getModifiers());
    }

    /**
     * check the specified {@link Member member} is private or not ?
     *
     * @param member {@link Member} instance, e.g, {@link Constructor}, {@link Method} or {@link Field}
     * @return Iff <code>member</code> is private one, return <code>true</code>, or <code>false</code>
     */
    static boolean isPrivate(Member member) {
        return member != null && Modifier.isPrivate(member.getModifiers());
    }

    /**
     * check the specified {@link Member member} is public or not ?
     *
     * @param member {@link Member} instance, e.g, {@link Constructor}, {@link Method} or {@link Field}
     * @return Iff <code>member</code> is public one, return <code>true</code>, or <code>false</code>
     */
    static boolean isPublic(Member member) {
        return member != null && Modifier.isPublic(member.getModifiers());
    }

}