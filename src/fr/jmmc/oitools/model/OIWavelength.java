/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: OIWavelength.java,v 1.1 2010-04-28 14:47:37 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.12  2009/12/03 13:08:23  mella
 * format effWave values
 *
 * Revision 1.11  2009/12/03 09:48:31  mella
 * Add effWave list
 *
 * Revision 1.10  2009/09/08 16:10:42  mella
 * add same block for all oitable after optionnal specific data
 *
 * Revision 1.9  2008/10/28 08:39:19  mella
 * fix javadoc comment
 *
 * Revision 1.8  2008/10/28 08:36:38  mella
 * Add javadoc
 *
 * Revision 1.7  2008/04/08 14:22:16  mella
 * Include Evelyne comments
 *
 * Revision 1.6  2008/03/28 09:01:59  mella
 * Set blank identifier error at severe level
 *
 * Revision 1.5  2008/03/20 14:25:06  mella
 * First semantic step
 *
 * Revision 1.4  2008/03/14 12:51:23  mella
 * Add nwave related method
 *
 * Revision 1.3  2008/03/13 07:25:48  mella
 * General commit after first keywords and columns definitions
 *
 * Revision 1.2  2008/03/11 14:48:52  mella
 * commit when evening is comming
 *
 * Revision 1.1  2008/02/28 08:10:40  mella
 * First revision
 *
 ******************************************************************************/
package fr.jmmc.oitools.model;

import fr.jmmc.oitools.OIFitsConstants;
import fr.jmmc.oitools.meta.ColumnMeta;
import fr.jmmc.oitools.meta.KeywordMeta;
import fr.jmmc.oitools.meta.Types;
import fr.jmmc.oitools.meta.Units;
import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * Class for OI_WAVELENGTH table.
 */
public class OIWavelength extends OITable {

  /* constants */

  /* static descriptors */
  /** INSNAME keyword descriptor */
  private final static KeywordMeta KEYWORD_INSNAME = new KeywordMeta(OIFitsConstants.KEYWORD_INSNAME,
          "name of detector for cross-referencing", Types.TYPE_CHAR);
  /** EFF_WAVE column descriptor */
  private final static ColumnMeta COLUMN_EFF_WAVE = new ColumnMeta(OIFitsConstants.COLUMN_EFF_WAVE,
          "effective wavelength of channel", Types.TYPE_REAL, Units.UNIT_METER);
  /** EFF_BAND column descriptor */
  private final static ColumnMeta COLUMN_EFF_BAND = new ColumnMeta(OIFitsConstants.COLUMN_EFF_BAND,
          "effective bandpass of channel", Types.TYPE_REAL, Units.UNIT_METER);

  /* members */
  /** 
   * OIWavelength class constructor.
   *
   * @param oifitsFile main OifitsFile
   */
  public OIWavelength(final OIFitsFile oifitsFile) {
    super(oifitsFile);

    // INSNAME  keyword definition
    addKeywordMeta(KEYWORD_INSNAME);

    // EFF_WAVE  column definition
    addColumnMeta(COLUMN_EFF_WAVE);

    // EFF_BAND  column definition
    addColumnMeta(COLUMN_EFF_BAND);
  }

  /**
   * Get INSNAME keyword value.
   *
   * @return the value of INSNAME keyword if present, NULL otherwise.
   */
  public String getInsName() {
    return getKeyword(OIFitsConstants.KEYWORD_INSNAME);
  }

  /**
   * Get number of wavelengths
   *
   * @return the number of wavelengths.
   */
  public int getNWave() {
    return getNbRows();
  }

  /**
   * Return the effective wavelength of channel
   * @return the wavelength of channel array
   */
  public float[] getEffWave() {
    return this.getColumnFloat(OIFitsConstants.COLUMN_EFF_WAVE);
  }

  /**
   * Return the effective bandpass of channel
   * @return the bandpass of channel array
   */
  public float[] getEffBand() {
    return this.getColumnFloat(OIFitsConstants.COLUMN_EFF_BAND);
  }

  /**
   * Return a string representation of this component.
   *
   * @return a string representation of this component
   */
  @Override
  public String toString() {
    return super.toString() + " [ INSNAME=" + getInsName() + " | NWAVE=" + getNWave() + " ]";
  }
  
  /** 
   * Do syntactical analysis.
   */
  @Override
  public void checkSyntax(final Logger checkLogger) {
    super.checkSyntax(checkLogger);

    if ((getInsName() != null) && (getInsName().length() == 0)) {
      /* Problem: INSNAME keyword has value "", that should not be
       * possible. */
      checkLogger.severe("INSNAME identifier has blank value");
    }

    oifitsFile.checkCrossRefering(this, checkLogger);
  }

  /**
   * Fill the given buffer with the xml serialisation of the table.
   * @param sb string buffer
   * @param detailled if true the result will contain the table content
   */
  @Override
  public void getXmlDesc(final StringBuilder sb, final boolean detailled) {
    sb.append("<insname>");
    sb.append("<name>" + getInsName() + "</name>");
    sb.append("<nwave>" + getNWave() + "</nwave>");
    sb.append("<effwaves>");

    final DecimalFormat formatter = new DecimalFormat("0.00E0");

    float[] effWaves = getEffWave();

    for (int i = 0, len = getNbRows(); i < len; i++) {
      sb.append("<effwave>" + formatter.format(effWaves[i]) + "</effwave>");
    }
    sb.append("</effwaves>");
    
    sb.append("</insname>\n\n");

    super.getXmlDesc(sb, detailled);
  }

}
/*___oOo___*/