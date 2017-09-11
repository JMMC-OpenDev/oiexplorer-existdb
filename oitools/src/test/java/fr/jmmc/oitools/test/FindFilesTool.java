/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oitools.test;

import static fr.jmmc.oitools.JUnitBaseTest.getFitsFiles;
import fr.jmmc.oitools.model.OIFitsFile;
import fr.jmmc.oitools.model.OIFitsLoader;
import fr.jmmc.oitools.model.OISpectrum;
import fr.jmmc.oitools.model.OITable;
import fr.jmmc.oitools.model.OIVis;
import fr.jmmc.oitools.model.OIWavelength;
import fr.jmmc.oitools.test.TestEnv;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Allows to carry out numerous tests to choose files that respect these tests.
 * 
 * @author kempsc
 */
public class FindFilesTool implements TestEnv {

    public static void main(String[] args) {
        System.setProperty("java.util.logging.config.file", "./src/test/resources/logging.properties");

        for (String f : getFitsFiles(new File(TEST_DIR))) {
            checkFile(f);
        }
    }

    public static void checkFile(final String absFilePath) {
        logger.info("Checking file : " + absFilePath);

        try {
            OIFitsFile OIFITS = OIFitsLoader.loadOIFits(absFilePath);
            OIFITS.analyze();

            testFile(OIFITS);

        } catch (Throwable th) {
            logger.log(Level.SEVERE, "IO failure occured while reading file : " + absFilePath, th);

        }
    }

    private static void testFile(OIFitsFile oifits) {

        boolean testCase1 = findOIVis(oifits);
        if (testCase1) {
            logger.warning("test vis ok : " + display(oifits));
        }
        boolean testCase2 = findOISpect(oifits);
        if (testCase2) {
            logger.warning("test spect ok : " + display(oifits));
        }
        boolean testCase3 = multiTarget(oifits);
        if (testCase3) {
            logger.warning("test multi-target ok : " + display(oifits));
        }
        boolean testCase4 = oneNWave(oifits);
        if (testCase4) {
            logger.warning("test onewave ok : " + display(oifits));
        }
        boolean testCase5 = multiTable(oifits);
        if (testCase5) {
            logger.warning("test multi-table ok : " + display(oifits));
        }
        boolean testCase6 = isV2(oifits);
        if (testCase6) {
            logger.warning("test V2 ok : " + display(oifits));
        }
        boolean testCase7 = hasImage(oifits);
        if (testCase7) {
            logger.warning("test hasImage ok : " + display(oifits));
        }
    }

    private static boolean findOIVis(OIFitsFile oifits) {
        if (oifits.hasOiVis()) {
            for (OIVis oivis : oifits.getOiVis()) {
                if (oivis.getNbRows() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean findOISpect(OIFitsFile oifits) {
        if (oifits.hasOiSpectrum()) {
            for (OISpectrum oispect : oifits.getOiSpectrum()) {
                if (oispect.getNbRows() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean multiTarget(OIFitsFile oifits) {
        if (oifits.hasOiTarget()) {
            if (oifits.getOiTarget().getNbRows() > 2) {
                return true;
            }
        }
        return false;
    }

    private static boolean oneNWave(OIFitsFile oifits) {
        if (oifits.getNbOiWavelengths() > 0) {
            for (OIWavelength oiwave : oifits.getOiWavelengths()) {
                if (oiwave.getNWave() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean multiTable(OIFitsFile oifits) {
        return oifits.getNbOiArrays() > 1 && oifits.getNbOiWavelengths() > 1;
    }

    private static boolean isV2(OIFitsFile oifits) {
        for (OITable oitable : oifits.getOiDataList()) {
            if (oitable.getOiRevn() == 2) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasImage(OIFitsFile oifits) {
        boolean ok = oifits.getPrimaryImageHDU() != null;
        if (ok) {
            logger.info("image: " + oifits.getPrimaryImageHDU());
        }
        return ok;
    }

    private static String display(OIFitsFile oifits) {
        return " FilePath : " + oifits.getAbsoluteFilePath() + ": [" + oifits.getNbOiTables()
                + "] : arrNames : " + Arrays.toString(oifits.getAcceptedArrNames())
                + " : insNames : " + Arrays.toString(oifits.getAcceptedInsNames())
                + " : Tables : " + oifits.getOITableList();
    }

}