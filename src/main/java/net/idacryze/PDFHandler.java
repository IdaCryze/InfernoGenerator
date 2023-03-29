package net.idacryze;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Random;
import java.util.HashSet;
import java.util.Arrays;

public class PDFHandler {
    private static final String DOCSFOLDER_NAME = "src";
    private static final String TASKBEGINNING_FILTER = "Aufgabe (";
    private static final String TARGET_FILENAME = "target.pdf";
    private static final String TEST_FOLDER = "src/test/resources/";
    private static final String TEST_PARAM = "3";


    public static void main(String[] args) {
        if (false) {
            args = new String[2];
            args[0] = TEST_PARAM;
            args[1] = TEST_FOLDER;
        }
        

        // argument #1 is the # of tasks that are to be extracted from each given document
        int tasksnumber;
        try {
            tasksnumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid argument. The first argument must be the amount of tasks that should be extracted from each given document.");
            return;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid amount of arguments.");
            return;
        }
        // argument #2 is the relative?! path to the folder containing all documents#
        
        File docsfolder;
        try {
            docsfolder = new File(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            docsfolder = new File(DOCSFOLDER_NAME);
        }

        
        if (!docsfolder.isDirectory()) {
            System.out.println("The specified folder does not exist.");
            return;
        }
        
        File[] docs = docsfolder.listFiles((File directory, String filename) -> filename.endsWith(".pdf"));

        // Sort the array alphanumberically, in a way that "file1_xxx" takes precedence over "file10_xxx"
        try {
            Arrays.sort(docs, new NaturalOrderComparator());
        } catch (Exception e) {
            System.out.println("Array sorting gone wrong!");
            return;
        }

        PDDocument targetdocument = new PDDocument();
        try {
            targetdocument.save(TARGET_FILENAME);
            targetdocument.close();
            for (File f: docs) {
                targetdocument = PDDocument.load(new File(TARGET_FILENAME));
                PDDocument sourcedoc = PDDocument.load(f);
                addRandomDocTasks(tasksnumber, sourcedoc, targetdocument, TASKBEGINNING_FILTER);
                
                targetdocument.save(TARGET_FILENAME);
                sourcedoc.close();
                targetdocument.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        

        
        
    }

    public static void addRandomDocTasks(int number, PDDocument doc, PDDocument target, String searchstr) {
        //Set that holds the page numbers of erroneus or already checked pages, generally some that should be skipped.
        HashSet<Integer> pagestoignore = new HashSet<>();


        int amountofdocpages = doc.getNumberOfPages();
        int numOfSavedTasks = 0;
        int currentPageNumber = 0;
        PDPage tempPage;
        Random randObj = new Random();
        while (numOfSavedTasks < number && pagestoignore.size() < amountofdocpages) {
            
            currentPageNumber = randObj.nextInt(0, amountofdocpages);
            tempPage = doc.getPage(currentPageNumber);
            
            if (!pagestoignore.contains(currentPageNumber) && isValidPage(tempPage, searchstr)) {
                do {
                    pagestoignore.add(currentPageNumber);

                    target.addPage(tempPage);

                    if (currentPageNumber+1 >= amountofdocpages) break;
                    else {
                        try {
                            tempPage = doc.getPage(++currentPageNumber);
                            
                        } catch (Exception e) {
                            break;
                        }
                    }
                } while (!isValidPage(tempPage, searchstr));
                numOfSavedTasks++;
            } else pagestoignore.add(currentPageNumber);
            
        }
    }

    protected static boolean isValidPage(PDPage page, String searchval) {
        PDDocument tempdoc = new PDDocument();
        boolean retVal = false;
        PDFTextStripper stripObj;
        try {
            tempdoc.addPage(page);
            stripObj = new PDFTextStripper();
            if (stripObj.getText(tempdoc).contains(searchval)) {
                retVal = true;
            }
        } catch (IOException e) {
        } finally {
            try {
                tempdoc.close();
            } catch (Exception e) {
            }
        }
        
        return retVal;
        
        
    }


}
