/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: OIFitsFile.java,v 1.10 2010-09-02 15:48:49 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2010/08/18 14:29:33  bourgesl
 * use the visitor pattern to refactor the XML serialization
 *
 * Revision 1.8  2010/08/18 08:31:30  mella
 * Clean old xml output
 *
 * Revision 1.7  2010/06/28 14:33:55  bourgesl
 * added beautified output for XML description using custom number formatters
 *
 * Revision 1.6  2010/06/21 15:43:54  bourgesl
 * properly set extNb and extVer for added tables
 *
 * Revision 1.5  2010/06/18 15:42:36  bourgesl
 * new constructors to create OI_* tables from scratch
 *
 * Revision 1.4  2010/06/17 15:02:27  bourgesl
 * added getter / setter methods for keywords and columns
 *
 * Revision 1.3  2010/05/27 16:13:29  bourgesl
 * javadoc + small refactoring to expose getters/setters for keywords and getters for columns
 *
 * Revision 1.2  2010/04/29 15:47:01  bourgesl
 * use OIFitsChecker instead of CheckLogger / Handler to make OIFits validation
 *
 * Revision 1.1  2010/04/28 14:47:38  bourgesl
 * refactored OIValidator classes to represent the OIFits data model
 *
 * Revision 1.24  2009/09/20 15:55:09  mella
 * Add new getter
 *
 * Revision 1.23  2009/08/25 13:41:01  mella
 * fix bug
 *
 * Revision 1.22  2009/08/25 13:00:18  mella
 * add minimal implementation to output xml onto data tables
 *
 * Revision 1.21  2009/04/10 07:10:03  mella
 * Add check one table that get one arrname without corresponding oi_array table
 *
 * Revision 1.20  2009/03/21 10:20:36  mella
 * Add todo
 *
 * Revision 1.19  2009/03/20 09:16:42  mella
 * Fix exception throwing
 *
 * Revision 1.18  2009/03/17 11:49:04  mella
 * Be more precise on throwed exceptions
 *
 * Revision 1.17  2009/03/17 08:09:34  mella
 * Improve logged message
 *
 * Revision 1.16  2009/03/16 15:41:37  mella
 * Find the difference ;)
 *
 * Revision 1.15  2009/03/09 10:27:24  mella
 * Add spacialFreq and spacialCoord getter
 *
 * Revision 1.14  2009/01/06 13:30:51  mella
 * GuiValidator clear report before analysis display
 *
 * Revision 1.13  2008/10/28 08:37:17  mella
 * Add javadoc
 *
 * Revision 1.12  2008/09/01 12:20:43  mella
 * Improve code when no Oitarget is present
 *
 * Revision 1.11  2008/04/08 14:22:16  mella
 * Include Evelyne comments
 *
 * Revision 1.10  2008/04/07 07:29:03  mella
 * Add nb of errors  in log file
 *
 * Revision 1.9  2008/04/01 11:24:44  mella
 * Add fine log to be able to collect data offline
 *
 * Revision 1.8  2008/04/01 07:40:17  mella
 * fix simpler logging usage
 *
 * Revision 1.7  2008/03/31 10:39:24  mella
 * remove debug code which saves fits file at end of check
 *
 * Revision 1.6  2008/03/31 08:05:58  mella
 * Add quick and dirty support of url and gzipped files
 *
 * Revision 1.5  2008/03/28 09:00:29  mella
 * - Add common log handler
 * - Add accessor method for acceptedStaIndexes
 *
 * Revision 1.4  2008/03/20 14:25:06  mella
 * First semantic step
 *
 * Revision 1.3  2008/03/14 12:52:11  mella
 * Add cardinality check
 *
 * Revision 1.2  2008/03/11 14:48:52  mella
 * commit when evening is comming
 *
 * Revision 1.1  2008/02/28 08:10:40  mella
 * First revision
 *
 ******************************************************************************/
package fr.jmmc.oitools.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * This class represents the data model of an OIFits standard file.
 */
public class OIFitsFile extends OIFits {

  /* members */
  /** absolute file path */
  private String absoluteFilePath = null;
  /** Hashtable connecting each ARRNAME keyword value with associated OI_ARRAY table */
  private final Map<String, List<OIArray>> arrNameToOiArray = new HashMap<String, List<OIArray>>();
  /** Hashtable connecting each INSNAME keyword value with associated OI_WAVELENGTH table */
  private final Map<String, List<OIWavelength>> insNameToOiWavelength = new HashMap<String, List<OIWavelength>>();

  /**
   * Public constructor
   */
  public OIFitsFile() {
    super();
  }

  /**
   * Public constructor
   * @param absoluteFilePath absolute file path
   */
  public OIFitsFile(final String absoluteFilePath) {
    super();
    setAbsoluteFilePath(absoluteFilePath);
  }

  /**
   * Add the given OI_* tables to this OIFitsFile structure
   * @param oiTable new OI_* table
   */
  public final void addOiTable(final OITable oiTable) {
    // Prepare other fields :
    // ext number :
    oiTable.setExtNb(getNbOiTables());

    // ext version :
    int extVer = 0;
    if (oiTable instanceof OITarget) {
      // only 1 OI_TARGET table allowed.
      if (hasOiTarget()) {
        throw new IllegalArgumentException("OI_TARGET is already defined !");
      }
    } else if (oiTable instanceof OIWavelength) {
      extVer = getNbOiWavelengths();
    } else if (oiTable instanceof OIArray) {
      extVer = getNbOiArrays();
    } else if (oiTable instanceof OIVis) {
      extVer = getNbOiVis();
    } else if (oiTable instanceof OIVis2) {
      extVer = getNbOiVis2();
    } else if (oiTable instanceof OIT3) {
      extVer = getNbOiT3();
    }

    extVer++;
    oiTable.setExtVer(extVer);

    this.registerOiTable(oiTable);
  }

  /**
   * Remove the given OI_* tables from this OIFitsFile structure.
   * Only valid for data tables (OI_VIS, OI_VIS2, OI_T3) tables
   * @param oiTable OI_* table to remove
   */
  public final void removeOiTable(final OIData oiTable) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("Unregistering object for " + oiTable.getExtName());
    }
    super.unregisterOiTable(oiTable);
  }

  /**
   * Register valid OI_* tables (keyword and column values must be defined).
   * @param oiTable reference on one OI_* table
   */
  @Override
  protected final void registerOiTable(final OITable oiTable) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("Registering object for " + oiTable.getExtName());
    }
    super.registerOiTable(oiTable);

    if (oiTable instanceof OIWavelength) {
      final OIWavelength o = (OIWavelength) oiTable;
      final String insName = o.getInsName();

      if (insName != null) {
        List<OIWavelength> v = this.insNameToOiWavelength.get(insName);

        if (v == null) {
          v = new LinkedList<OIWavelength>();
          this.insNameToOiWavelength.put(insName, v);
        }

        v.add((OIWavelength) oiTable);
      } else {
        logger.warning("INSNAME of OI_WAVELENGTH table is null during building step");
      }
    } else if (oiTable instanceof OIArray) {
      final OIArray o = (OIArray) oiTable;
      final String arrName = o.getArrName();

      if (arrName != null) {
        List<OIArray> v = this.arrNameToOiArray.get(arrName);

        if (v == null) {
          v = new LinkedList<OIArray>();
          this.arrNameToOiArray.put(arrName, v);
        }

        v.add((OIArray) oiTable);
      } else {
        logger.warning("ARRNAME of OI_ARRAY table is null during building step");
      }
    }
  }

  /** 
   * Mediator method to resolve cross references. Returns OiArray associated
   * to input parameter
   *
   * @param arrName string containing ARRNAME value
   * @return the OI_ARRAY table reference associated. If none is associated,
   *  returns NULL
   */
  public final OIArray getOiArray(final String arrName) {
    final List<OIArray> v = this.arrNameToOiArray.get(arrName);
    if (v == null) {
      return null;
    }
    return v.get(0);
  }

  /**
   * Mediator method to resolve cross references. Returns OiWavelength
   * associated to input parameter.
   *
   * @param insName string containing INSNAME value
   * @return the OI_WAVELENGTH table reference associated. If none is
   *  associated, returns NULL
   */
  public final OIWavelength getOiWavelength(final String insName) {
    List<OIWavelength> v = this.insNameToOiWavelength.get(insName);
    if (v == null) {
      return null;
    }
    return v.get(0);
  }

  /**
   * Mediator method to resolve cross references. Returns the accepted (ie
   * valid) station indexes.
   *
   * @param oiArray OiArray where station indexes are defined
   * @return the array containing the indexes.
   */
  public final short[] getAcceptedStaIndexes(final OIArray oiArray) {
    if (oiArray == null) {
      return EMPTY_SHORT_ARRAY;
    }
    return oiArray.getStaIndex();
  }

  /**
   * Get all INSNAME values already defined.
   * @return an string array containing all accepted values.
   */
  public final String[] getAcceptedInsNames() {
    final int len = this.insNameToOiWavelength.size();
    if (len == 0) {
      return EMPTY_STRING;
    }
    return this.insNameToOiWavelength.keySet().toArray(new String[len]);
  }

  /**
   * Get all ARRNAME values already defined.
   * @return an string array containing all accepted values.
   */
  public final String[] getAcceptedArrNames() {
    final int len = this.arrNameToOiArray.size();
    if (len == 0) {
      return EMPTY_STRING;
    }
    return this.arrNameToOiArray.keySet().toArray(new String[len]);
  }

  /**
   * Get all target identifiers defined.
   * @return an integer array containing all accepted values.
   */
  public final short[] getAcceptedTargetIds() {
    final OITarget oiTarget = getOiTarget();
    if (oiTarget == null) {
      return EMPTY_SHORT_ARRAY;
    }

    return oiTarget.getTargetId();
  }

  /** 
   * Return a short description of OIFITS content.
   * @return short description of OIFITS content
   */
  @Override
  public String toString() {
    return "\narrNameToOiArray:" + this.arrNameToOiArray
            + "\ninsNameToOiWavelength:" + this.insNameToOiWavelength + "\n";
  }

  /**
   * Check the global structure of oifits file, including table presence and
   * syntax correction.
   *
   * @param checker checker component
   */
  public final void check(final OIFitsChecker checker) {
    checker.info("Analysing values and references");

    logger.finest("Checking mandatory tables");

    /* Checking presence of one and only one OI_TARGET table */
    if (!hasOiTarget()) {
      checker.severe(
              "No OI_TARGET table found: one and only one must be present");
    }

    /* Checking presence of at least one OI_WAVELENGTH table */
    if (this.insNameToOiWavelength.isEmpty()) {
      checker.severe(
              "No OI_WAVELENGTH table found: one or more must be present");
    }

    /* Starting syntactical analysis */
    logger.finest("Building list of table for keywords analysis");

    for (OITable oiTable : getOITableList()) {
      oiTable.checkSyntax(checker);
    }

    // Collect some data for post analysis (at fine level)
    // post analysis is done by one external tool that read the xml log
    if (checker.isFineEnabled()) {
      if (getOiTarget() != null) {
        checker.fine("NBTARGETS");
        checker.fine("" + getOiTarget().getNbTargets());
      }

      for (OITable oiTable : getOITableList()) {
        checker.fine("TABLENAME");
        checker.fine(oiTable.getExtName());
      }

      for (Iterator<String> it = this.arrNameToOiArray.keySet().iterator(); it.hasNext();) {
        checker.fine("ARRNAME");
        checker.fine(it.next());
      }

      for (Iterator<String> it = this.insNameToOiWavelength.keySet().iterator(); it.hasNext();) {
        checker.fine("INSNAME");
        checker.fine(it.next());
      }

      checker.fine("WARNINGS");
      checker.fine("" + checker.getCheckHandler().getNbWarnings());

      checker.fine("SEVERES");
      checker.fine("" + checker.getCheckHandler().getNbSeveres());
    }
  }

  /**
   * Check validity of cross references of non-data tables, ie check both
   * tables have different identifiers, or no mandatory identifier is not
   * defined.
   *
   * @param oiTable reference on table to check
   * @param checker checker component
   */
  public final void checkCrossRefering(final OITable oiTable, final OIFitsChecker checker) {
    if (checker.isFineEnabled()) {
      checker.fine("Checking cross references for " + oiTable.getExtName());
    }

    if (oiTable instanceof OITarget) {
      OITarget o = (OITarget) oiTable;

      if (o.getNbTargets() < 1) {
        checker.severe("No target defined");
      }
    } else if (oiTable instanceof OIWavelength) {
      OIWavelength o = (OIWavelength) oiTable;
      String insName = o.getInsName();

      if (insName != null) {
        /* Get OiWavelength associated to INSNAME value */
        List<OIWavelength> v = this.insNameToOiWavelength.get(insName);

        if (v == null) {
          /* Problem: INSNAME value has not been encoutered during
           * building step, that should be impossible */
          checker.severe("invalid INSNAME identifier");
        } else {
          if (v.size() > 1) {
            /* Problem: more that one OiWavelength table associated
             * to INSNAME value, that is strictly forbiden */
            final StringBuilder sb = new StringBuilder();

            for (Iterator<OIWavelength> it = v.iterator(); it.hasNext();) {
              o = it.next();
              sb.append("|").append(o.getExtNb());
            }

            checker.severe("OI_WAVELENGTH tables [" + sb.toString().substring(1) + "] are identified by same INSNAME='" + o.getInsName() + "'");
          }
        }
      } else {
        /* Problem: INSNAME value is "", that should not be possible */
        checker.severe(
                "INSNAME identifier is missing during reference checking step");
      }
    } else if (oiTable instanceof OIArray) {
      OIArray o = (OIArray) oiTable;
      String arrName = o.getArrName();

      if (arrName != null) {
        /* Get OiArray associated to ARRNAME value */
        List<OIArray> v = arrNameToOiArray.get(arrName);

        if (v == null) {
          /* Problem: ARRNAME value has not been encoutered during
           * building step, that should be impossible */
          checker.severe("invalid ARRNAME identifier");
        } else {
          if (v.size() > 1) {
            /* Problem: more that one OiArray table associated
             * to ARRNAME value, that is strictly forbiden */
            final StringBuilder sb = new StringBuilder();

            for (Iterator<OIArray> it = v.iterator(); it.hasNext();) {
              o = it.next();
              sb.append("|").append(o.getExtNb());
            }

            checker.severe("OI_ARRAY tables [" + sb.toString().substring(1) + "] are identified by same ARRNAME='" + o.getArrName() + "'");
          }
        }
      } else {
        /* Problem: ARRNAME value is "", that should not be possible */
        checker.severe(
                "ARRNAME identifier is missing during reference checking step");
      }
    }
  }

  /**
   * Implements the Visitor pattern
   * @param visitor visitor implementation
   */
  public final void accept(final ModelVisitor visitor) {
    visitor.visit(this);
  }

  /*
   * Getter - Setter -----------------------------------------------------------
   */
  /**
   * Get the name of this OIFits file.
   *  @return a string containing the name of the OIFits file.
   */
  public final String getName() {
    return getAbsoluteFilePath();
  }

  /**
   * Return the absolute file path
   * @return absolute file path or null if the file does not exist
   */
  public final String getAbsoluteFilePath() {
    return absoluteFilePath;
  }

  /**
   * Define the absolute file path
   * @param absoluteFilePath absolute file path
   */
  public final void setAbsoluteFilePath(final String absoluteFilePath) {
    this.absoluteFilePath = absoluteFilePath;
  }
}
/*___oOo___*/