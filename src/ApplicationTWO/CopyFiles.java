package ApplicationTWO;

import java.io.*;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import javax.swing.*;

public class CopyFiles {

    private TextArea statusOutput;
    private int counter = 1;
    private int type;
    private int txtCount = 0;
    private double progressBarNum;
    private ProgressBar formattingProgress;
    private PrintWriter out;


    public void runProgram(File sourceDirectory, int type, TextArea statusOutput, ProgressBar formattingProgress, PrintWriter out) {
        this.type = type;
        this.statusOutput = statusOutput;
        this.formattingProgress = formattingProgress;
        this.out = out;
        countTxtInSourceDir(sourceDirectory);
        copyFilesAndCheckFolders(sourceDirectory);
    }

    private void copyFilesAndCheckFolders(File current) {
        if (!current.equals(new File("LABDIR"))) {

            for (File sourceFile : getAllTxtInDir(current)) {
                Thread checker = new Thread(() -> {
                    File newFile = getNewFile();
                    copyAndWriteInFile(sourceFile, newFile, type);
                    addToTextArea(sourceFile, newFile);
                });
                checker.start();
            }
            getAllFoldersFromDir(current);
        }
    }

    private synchronized void addToTextArea(File sourceFile, File newFile) {
        statusOutput.appendText("File \"" + sourceFile.getPath() + "\" was copied with name \"" + newFile.getName() + "\". \r\n");
        out.println("File \"" + sourceFile.getPath() + "\" was copied with name \"" + newFile.getName() + "\".");
        formattingProgress.progressProperty().setValue(formattingProgress.progressProperty().getValue()+progressBarNum);
        if(formattingProgress.progressProperty().getValue() >= 1d){
            statusOutput.appendText("Copying completed successfully\r\n");
            out.println("Copying completed successfully");
            out.println("quit");
        }
    }

    private File[] getAllTxtInDir(File sFile) {

        File[] sourceFiles = sFile.listFiles((dir, name) -> {
            if (name.endsWith(".txt")) {
                return true;
            } else {
                return false;
            }
        });

        if(sourceFiles.length == 0){
            out.println("ERROR: doesn\'t have any .txt files.");
            JOptionPane.showMessageDialog(null, "ERROR: doesn\'t have any .txt files.");
            System.exit(0);
        }
        return sourceFiles;

    }

    private void copyAndWriteInFile(File fSource, File fTarget, int type) {

        FormatFile formatFile = new FormatFile();
        copyFileUsingStream(fSource, fTarget);
        formatFile.writeInFile(fTarget, type);
    }

    private void copyFileUsingStream(File source, File dest) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (Exception ex) {
            out.println("Unable to copy file:" + ex.getMessage());
        } finally {
            try {
                is.close();
                os.close();
            } catch (Exception ex) {
            }
        }
    }

    private File getNewFile() {
        String name = "var3file" + counter + ".txt";
        File fTarget = new File(new File("LABDIR"), name);
        counter++;
        return fTarget;
    }

    private void getAllFoldersFromDir(File sFile) {
        File[] sourceOfAllFiles = sFile.listFiles();
        for (File folder : sourceOfAllFiles) { //find folder and run txtsearch
            if (folder.isDirectory()) {
                copyFilesAndCheckFolders(folder);
            }
        }
    }

    private void countTxtInSourceDir(File sourceDirectory){
        File[] sourceOfAllFiles = sourceDirectory.listFiles();
        txtCount += getAllTxtInDir(sourceDirectory).length;
        assert sourceOfAllFiles != null;
        for (File folder : sourceOfAllFiles) {
            if (folder.isDirectory()) {
                countTxtInSourceDir(folder);
            }
        }
        progressBarNum = 1d / txtCount;

        if(sourceOfAllFiles.length == 0){
            out.println("ERROR: source folder is empty");
            JOptionPane.showMessageDialog(null, "ERROR: source folder is empty");
            System.exit(0);
        }
    }
}
