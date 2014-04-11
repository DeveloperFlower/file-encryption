package org.developercookie.file.encryption;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.*;
import java.util.Collection;

/**
 * Encrypts and decrypts files and places them into a directories. Created by developerCookie on 08.04.14.
 */
public class FileEncryption {
    /**
     * The file extension for the encrypted files to use.
     */
    public static final String ENCRYPTION_EXTENSION = ".enc";

    /**
     * The ContentTransformer to use for encryption/decryption of the file content.
     */
    private ContentTransformer contentTransformer;

    /**
     * Files are be encrypted and decrypted with the given <code>contentTransformer</code>.
     */
    public FileEncryption(ContentTransformer contentTransformer) {
        this.contentTransformer = contentTransformer;
    }

    /**
     * Encrypt the folder given by <code>folderToEncrypt</code>. Only files are encrypted that have the given
     * <code>fileExtension</code>. The resulting files will be written to the <code>outputFolder</code>.
     * <code>Key</code> is used for the encryption.
     */
    public void encryptFolder(String folderToEncrypt, String outputFolder, String fileExtension, String key) throws IOException {
        Collection<File> foundFiles = FileUtils.listFiles(new File(folderToEncrypt), new SuffixFileFilter(fileExtension), null);
        for (File oneFile : foundFiles) {
            String fullFilename = oneFile.getAbsolutePath();
            encrypt(fullFilename, outputFolder, key);
        }
    }

    /**
     * The file with the given <code>filename</code> will be encrypted with the given <code>key</code> and the result
     * will be stored into the <code>outputFolder</code>.
     */
    public void encrypt(String filename, String outputFolder, String key) throws IOException {
        byte[] fileContent = readFileContent(filename);
        byte[] encryptedContent = contentTransformer.encrypt(fileContent, key);
        String newFilename = buildNewFilenameEncrypt(filename, outputFolder);
        writeFile(newFilename, encryptedContent);
    }

    /**
     * Decrypts all files that are located in the <code>folderToDecrypt</code>. The decrypted files will be stored intot
     * the <code>outputFolder</code>. Decryption is made with the <code>key</code>.
     */
    public void decryptFolder(String folderToDecrypt, String outputFolder, String key) throws IOException, IllegalKeyException {
        Collection<File> foundFiles = FileUtils.listFiles(new File(folderToDecrypt), TrueFileFilter.INSTANCE, null);
        for (File oneFile : foundFiles) {
            String fullFilename = oneFile.getAbsolutePath();
            decrypt(fullFilename, outputFolder, key);
        }
    }

    /**
     * Decrypts the file denoted by <code>filename</code> and write the decrypted file to <code>outputFolder</code>. To
     * decrypt the file the <code>key</code> will be used.
     */
    public void decrypt(String filename, String outputFolder, String key) throws IOException, IllegalKeyException {
        byte[] fileContent = readFileContent(filename);
        byte[] decryptedContent = contentTransformer.decrypt(fileContent, key);
        String newFilename = buildNewFileNameDecrypt(filename, outputFolder);
        writeFile(newFilename, decryptedContent);
    }

    /**
     * Reads the content of the whole file. Note: if the file is huge it can cause an OutOfMemoryError!
     */
    private byte[] readFileContent(String filename) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filename);
        BufferedInputStream bufferedStream = new BufferedInputStream(fileInputStream);

        byte[] content = null;
        try {
            content = IOUtils.toByteArray(bufferedStream);
        } finally {
            IOUtils.closeQuietly(bufferedStream);
        }
        return content;
    }

    /**
     * Takes the <code>oldFilename</code> and will generate a file name that has the default encryption file extension
     * ".enc". The new filename will be placed in the <code>outputFolder</code>.
     */
    private String buildNewFilenameEncrypt(String oldFilename, String outputFolder) {
        String pureFilename = FilenameUtils.getName(oldFilename);
        StringBuilder buffer = new StringBuilder();
        buffer.append(outputFolder);
        buffer.append('/');
        buffer.append(pureFilename);
        buffer.append(ENCRYPTION_EXTENSION);
        return buffer.toString();
    }

    /**
     * Builds a new file name for the file that will be decrypted. The method assumed that the last file extension is
     * the "enc". And it will be removed. The new file name will get the <code>outputFolder</code> as prefix.
     */
    private String buildNewFileNameDecrypt(String oldFilename, String outputFolder) {
        String pureFilename = FilenameUtils.getName(oldFilename);
        String currentFileExtension = FilenameUtils.getExtension(pureFilename);
        int indexOfFileExtension = pureFilename.indexOf(currentFileExtension) - 1;
        if (indexOfFileExtension >= 0) {
            String newFilename = pureFilename.substring(0, indexOfFileExtension);
            return outputFolder + "/" + newFilename;
        } else {
            throw new IllegalStateException("Must not happen! Filename was: " + oldFilename);
        }
    }

    /**
     * Writes the given <code>content</code> to the file denoted by the <code>filename</code>.
     */
    private void writeFile(String filename, byte[] content) throws IOException {
        FileOutputStream stream = new FileOutputStream(filename);
        BufferedOutputStream bufferedStream = new BufferedOutputStream(stream);
        try {
            bufferedStream.write(content);
        } finally {
            IOUtils.closeQuietly(bufferedStream);
        }
    }
}
