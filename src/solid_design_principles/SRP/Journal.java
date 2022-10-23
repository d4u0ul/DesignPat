package solid_design_principles.SRP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Journal {
    private final List<String> entries = new ArrayList<>();
    private static  int count = 0;

    public void addEntry(String text){
        entries.add("" +(++count)+": "+text);
    }

    public void remove(int index){
        entries.remove(index);
    }

    @Override
    public String toString() {
        return String.join(System.lineSeparator(),entries);
    }
}

class Persistence
{
    public void saveToFile(Journal journal, String filename, boolean overwrite) throws FileNotFoundException
    {
        //adding some of the stuff that doesn't necessarily belong to the journal, but it might seem like a good idea to persist the joutnal in a file
        //violation of the SRP
        if(overwrite || new File(filename).exists())
        {
            try( PrintStream out = new PrintStream(filename))
            {
                out.println(journal.toString());
            }
        }

    }


    public void loadFile(String filename){};
    public void load(URL url){};
}


class Demo
{
    public static void main(String[] args) throws FileNotFoundException {
        Journal j = new Journal();
        j.addEntry("I cried today");
        j.addEntry("I ate a bug");
        System.out.println(j);

        Persistence p = new Persistence();
        String filename = "c:\\temp\\journal.txt";
        p.saveToFile(j, filename, true);

        try {
            Runtime.getRuntime().exec("notepad.exe " + filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
