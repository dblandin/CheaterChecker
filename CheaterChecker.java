/**
 * Class for checking a directory of .txt files
 * for plaigiarism by counting the number of similar
 * nGrams between files
 *
 * @author      Devon Blandin
 * @contact     dblandin@gmail.com
 * @date        8/11/12
 */
import java.util.*;
import java.io.*;
public class CheaterChecker
{
    public static void main(String[] args)
    {
        // get current system time in ms
        double ms = (double) System.currentTimeMillis();

        // expect a directory and a sensitivity level to be passed in
        // default to current directory and a sensitivity level of 15
        if (args.length <=  2)
        {
            File dir = new File(((args.length > 0) ? args[0] : "."));
            compareFiles(dir, ((args.length == 2) ? Integer.parseInt(args[1]) : 15));

            // calculate execution time
            ms = System.currentTimeMillis() - ms;
            System.out.println("Execution Time: " + (ms / 1000) + " seconds");
        }
        else
            System.out.println("usage: java CheaterChecker [directory of .txt files to check : current] [sensitivity : 15]");

    } // end main

    /**
     * compares all .txt files within dir for plaigiarism 
     * @param dir  directory of .txt files to compare
     * @param sensitivity  determines the size of word groupings
     */
    public static void compareFiles(File dir, int sensitivity)
    {
        // get all files with a .txt extension
        File[] files = dir.listFiles(new FilenameFilter() {
                       public boolean accept(File dir, String name) {
                           return name.toLowerCase().endsWith(".txt");
                       }
        });

        System.out.println("Initialzing program on " + files.length + " files... (sensitivity: " + sensitivity + ")\n");
        System.out.println("Gathering data on Files...");

        // create a Hashtable for files and their nGram Hashtables
        Hashtable<File, Hashtable> fileNGrams = generateFileNGrams(files, sensitivity);

        // initialize cheater factor counter
        int cheaterCount = 0;
        if (fileNGrams.isEmpty())
            System.out.println("No .txt files found.");
        else
        {
            
            // compare files
            System.out.println("Scanning Files...\n");
            for (int i = 0; i < files.length; i++)
            {
                for (int j = i + 1; j < files.length; j++)
                {
                    int cheaterFactor = checkCheaterFactor(fileNGrams.get(files[i]), fileNGrams.get(files[j]));
                    if (cheaterFactor > 0)
                    {
                        cheaterCount++;
                        System.out.println("Compare: " + files[i].getName() + " to " + files[j].getName() + " Count: " + cheaterFactor);
                    }
                }
            }
        }

        // print out plagiarism count
        System.out.println("\n" + cheaterCount + " possible acts of plagiarism detected.");
    }

    /**
     * generate a Hashtable of a Files and their Hashtables of NGram Strings
     * @param File[] files  array of files to scan
     * @param int sensitivity  determines the size of word groupings
     * @return Hashtable<File, Hashtable>
     */
    public static Hashtable<File, Hashtable>  generateFileNGrams(File[] files, int sensitivity)
    {
        Hashtable<File, Hashtable> fileNGrams = new Hashtable<File, Hashtable>();
        // for each file in files, generate NGram Hashtable and add it to the fileNGrams Hashtable
        for (File file : files)
            fileNGrams.put(file, generateNGrams(file, sensitivity));

        return fileNGrams;
    }
    public static Hashtable<String, Integer> generateNGrams(File file, int sensitivity)
    {
        Scanner input;
        Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();

        try
        {
            input = new Scanner(file);
            String gram = "";

            // get the first gram
            for (int i = 0; i < sensitivity; i++ ) 
            {
                if (input.hasNext())
                {
                    if (i != sensitivity - 1)
                        gram += input.next() + " ";
                    else
                        gram += input.next();
                }
            }
            hashtable.put(gram, 1);

            while (input.hasNext())
            {
                // take the previous gram, remove the first word, and append the next
                gram = gram.split(" ", 2)[1] + " " + input.next(); 
                if (hashtable.containsKey(gram))
                {
                    hashtable.put(gram, hashtable.get(gram) + 1);
                }
                else
                    hashtable.put(gram, 1);
            }
            return hashtable;
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
    }
    /**
     * calculate the "cheater factor" of two Hashtables of NGram strings
     * @param Hashtable first  first Hashtable
     * @param Hashtable second  second Hashtable
     * @return int  number of similar NGram strings between Hashtables
     */
    public static int checkCheaterFactor(Hashtable first, Hashtable second)
    {
        int cheaterFactor = 0;
        Enumeration nGrams = first.keys();

        // count shared nGrams between files
        while (nGrams.hasMoreElements())
        {
            if (second.containsKey(nGrams.nextElement()))
                 cheaterFactor++;
        }
        return cheaterFactor;
    }
} // end class
