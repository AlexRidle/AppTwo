package ApplicationTWO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import javafx.scene.control.TextArea;

public class FormatFile {

    public void writeInFile(File fTarget, int type) {

        try {
            StringBuilder text = new StringBuilder(new Scanner(fTarget).useDelimiter("\\Z").next());
            format(type, text);
            writer(fTarget, text);
        } catch (Exception e) {
            writer(fTarget, new StringBuilder());
        }

    }

    private void format(int type, StringBuilder text) {

        switch (type) {
            case 1:
                formatType1(text);
                break;
            case 2:
                formatType2(text);
                break;
            case 3:
                formatType3(text);
                break;
            case 4:
                formatType4(text);
                break;
            case 5:
                formatType5(text);
                break;
        }
    }


    private void formatType1(StringBuilder format) {

        String lines = format.toString().replaceAll(" ", "");
        format.delete(0, format.length());
        format.append(lines);

//        for (int i = 0; i < format.length(); i++) {
//            if (format.charAt(i) == ' ') {
//                format.deleteCharAt(i);
//                i--;
//            }
//        }

    }

    private void formatType2(StringBuilder format) {
        //Make all words with 'd' underline

        String lines = format.toString().replaceAll("\\b\\w*d+\\w*\\b", "<S>$0</S>");
        format.delete(0, format.length());
        format.append(lines);

//        findWordAndSurround(format, "d", "<S>", "</S>");

    }

    private void formatType3(StringBuilder format) {
        //Make all words with 'a' underline

        String lines = format.toString().replaceAll("\\b\\w*a+\\w*\\b", "<B>$0</B>");
        format.delete(0, format.length());
        format.append(lines);

//        findWordAndSurround(format, "a", "<B>", "</B>");
    }

    private void formatType4(StringBuilder format) {
        //Delete all words with 'e'

        String lines = format.toString().replaceAll("\\b\\w*e+\\w*\\b", "");
        format.delete(0, format.length());
        format.append(lines);

//        findWordAndDelete(format);
    }

    private void formatType5(StringBuilder format) {
        //If word starts with capital char make it underline

        String lines = format.toString().replaceAll("\\b[A-Z]\\w*\\b", "<S>$0</S>");
        format.delete(0, format.length());
        format.append(lines);

//        underlineCapitalWords(format);
    }

    private void writer(File fTarget, StringBuilder format) {

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fTarget);
            writer.println("<html><body>");
            writer.println(format);
            writer.println("</body></html>");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
    }
}
