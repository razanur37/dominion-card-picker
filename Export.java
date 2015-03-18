// Copyright (c) 2015. by Casey English

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Export {

    private String path;

    public Export(String path) {
        this.path = path;
    }

    public void writeToFile(String textLine) throws IOException {
        FileWriter write = new FileWriter(path);
        PrintWriter printLine = new PrintWriter(write);

        printLine.print(textLine);

        printLine.close();
    }
}
