/*
  Copyright (C) 2003 EBI, GRL
 
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.
 
  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
 
  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.ensembl.healthcheck.util;

import java.util.logging.*;
import java.util.*;
import java.io.*;

/**
 * <p>Title: MyStreamHandler.java</p>
 * <p>Description: Custom stream handler for logging.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Organisation: EMBL</p>
 * <p>Created on March 12, 2003, 11:30 AM</p>
 * @author Glenn Proctor <glenn@ebi.ac.uk>
 * @version $Revision$
 */

public class MyStreamHandler extends Handler {
  
  OutputStream outputStream;
  
  /** 
   * Creates a new instance of MyStreamHandler 
   */
  public MyStreamHandler(OutputStream os, Formatter formatter) {
    this.outputStream = os;
    setFormatter(formatter);
  }
  
  public void close() throws java.lang.SecurityException {
  }
  
  public void flush() {
    try {
      outputStream.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void publish(java.util.logging.LogRecord logRecord) {
    try {
      outputStream.write((getFormatter().format(logRecord)).getBytes());
      flush();
     } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
