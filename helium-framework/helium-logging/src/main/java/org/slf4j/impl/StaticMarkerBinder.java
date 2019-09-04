/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2011, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package org.slf4j.impl;

import org.helium.logging.spi.SimpleMarkerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

/**
 * 
 * The binding of {@link MarkerFactory} class with an actual instance of 
 * {@link IMarkerFactory} is performed using information returned by this class. 
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class StaticMarkerBinder implements MarkerFactoryBinder {
  /**
   * The unique instance of this class.
   */
  public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();
  
  final IMarkerFactory markerFactory = new SimpleMarkerFactory();
  
  private StaticMarkerBinder() {
  }
  
  /**
   * Currently this method always returns an instance of 
   * {@link SimpleMarkerFactory}.
   */
  public IMarkerFactory getMarkerFactory() {
    return markerFactory;
  }
  
  /**
   * Currently, this method returns the class name of
   * {@link SimpleMarkerFactory}.
   */
  public String getMarkerFactoryClassStr() {
    return SimpleMarkerFactory.class.getName();
  }
}
