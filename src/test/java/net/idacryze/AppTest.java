package net.idacryze;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("kap1_ml.pdf").getFile());
        File file2 = new File(classLoader.getResource("WICHTIGE_HINWEISE.pdf").getFile());
        File file3 = new File(classLoader.getResource("kap6_ml.pdf").getFile());
        
        PDFHandler testHandler = new PDFHandler();
        try {
            PDDocument test = PDDocument.load(file);
            PDDocument test2 = PDDocument.load(file2);
            PDDocument test3 = PDDocument.load(file3);

            assertTrue(PDFHandler.isValidPage(test.getPage(0), "Aufgabe ("));
            assertFalse(PDFHandler.isValidPage(test2.getPage(0), "Aufgabe ("));
            assertFalse(PDFHandler.isValidPage(test3.getPage(143), "Aufgabe ("));

            PDDocument output = new PDDocument();
            PDFHandler.addRandomDocTasks(3, test, output, "Aufgabe (");
            /*PDFHandler.addRandomDocTasks(3, test2, output, "Aufgabe (");
            PDFHandler.addRandomDocTasks(3, test3, output, "Aufgabe (");*/
            output.save(new File("testoutput.pdf"));
            output.close();

            
        } catch (IOException e) {
            e.printStackTrace();
            
        }

        
        



    }
    @Test
    public void sortingTest() {
        File[] docs = new File("src/test/resources/").listFiles((File directory, String filename) -> filename.endsWith(".pdf"));
        for (File f: docs) {
            System.out.println(f);
        }
        Arrays.sort(docs, new NaturalOrderComparator());
        for (File f: docs) {
            System.out.println(f);
        }
    }

    @Test
    public void mainMethodTest() {
        String[] argsarray = {"3", "src/test/resources/"};
        PDFHandler.main(argsarray);
    }
}
