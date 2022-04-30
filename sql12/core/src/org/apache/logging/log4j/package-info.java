/**
 * This package was introduced to be able to use version 5.2.2 of
 * "Apache POI - the Java API for Microsoft Documents".
 * See <a href="https://poi.apache.org/">https://poi.apache.org/</a>.
 * SQuirreL needs Apache POI to import and export MS-Excel files.
 *
 * Apache POI 5.2.2 depends on Log4j2.
 * SQuirreL does not use any Log4j libraries.
 * The part of Log4j's interface used by POI 5.2.2 when SQuirreL imports
 * and exports MS-Excel files is implemented here.
 *
 * POI's Log4j2 calls are redirected to SQuirreL's own logging framework,
 * see SQuirreL's reimplementation of {@link org.apache.logging.log4j.LogManager}
 */
package org.apache.logging.log4j;

