package org.developercookie.file.encryption;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests the FileEncryption methods that handles wholes directories. Created by developerCookie on 10.04.14.
 */
public class FileEncryptionWholeFolderTest {
    /**
     * Folder to which the test data are written.
     */
    private static final String testFolder = "/tmp/test";

    /**
     * Folder to which the encrypted data are written.
     */
    private static final String encryptionFolder = "/tmp/enc";

    /**
     * Folder to which the decrypted data are written.
     */
    private static final String decryptionFolder = "/tmp/dec";

    /**
     * Folder will be created.
     */
    @Before
    public void init() {
        new File(testFolder).mkdirs();
        new File(encryptionFolder).mkdirs();
        new File(decryptionFolder).mkdirs();
    }

    /**
     * Folder will be removed.
     */
    @After
    public void cleanup() {
        FileUtils.deleteQuietly(new File(testFolder));
        FileUtils.deleteQuietly(new File(encryptionFolder));
        FileUtils.deleteQuietly(new File(decryptionFolder));
    }

    /**
     * We write only one file in the test folder and wants to encrypt and decrypt this file.
     */
    @Test
    public void oneFile() throws Exception {
        Map<String, String> testdata = writeTestfiles(testFolder, 1, "test", "txt");
        AESContentTransformer transformer = new AESContentTransformer();
        FileEncryption fileEncryption = new FileEncryption(transformer);
        fileEncryption.encryptFolder(testFolder, encryptionFolder, "txt", "12");
        fileEncryption.decryptFolder(encryptionFolder, decryptionFolder, "12");

        Map<String, String> decryptedData = readFiles(decryptionFolder, "txt");
        Assert.assertEquals(testdata.size(), decryptedData.size());
        Assert.assertEquals(testdata, decryptedData);
    }

    /**
     * We write several files in the test folder and wants to encrypt and decrypt these files.
     */
    @Test
    public void severalFiles() throws Exception {
        Map<String, String> testdata = writeTestfiles(testFolder, 123, "test", "txt");
        AESContentTransformer transformer = new AESContentTransformer();
        FileEncryption fileEncryption = new FileEncryption(transformer);
        fileEncryption.encryptFolder(testFolder, encryptionFolder, "txt", "12");
        fileEncryption.decryptFolder(encryptionFolder, decryptionFolder, "12");

        Map<String, String> decryptedData = readFiles(decryptionFolder, "txt");
        Assert.assertEquals(testdata.size(), decryptedData.size());
        Assert.assertEquals(testdata, decryptedData);
    }

    /**
     * Writes the given <code>numberOfFiles</code> to the <code>outputFolder</code>. The content is randomly chosen.
     * Each file gets the defined <code>prefix</code> and <code>extension</code></code>. The resulting map contains the
     * filename with its file content. The filename is without the folder.
     */
    private Map<String, String> writeTestfiles(String outputFolder, int numberOfFiles, String prefix, String extension) throws IOException {
        Map<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < numberOfFiles; i++) {
            String pureFilename = RandomStringUtils.random(8, true, true) + i + "." + extension;
            String filename = outputFolder + "/" + pureFilename;
            String testContent = RandomStringUtils.random(2234, true, true);
            FileOutputStream stream = new FileOutputStream(filename);
            stream.write(testContent.getBytes());
            IOUtils.closeQuietly(stream);
            result.put(pureFilename, testContent);
        }
        return result;
    }

    /**
     * Reads in the files in the given <code>folder</code>, considering only files with the given
     * <code>extension</code>. The result will be a map that contains the filenames without the folder and the content.
     */
    private Map<String, String> readFiles(String folder, String extension) throws IOException {
        Map<String, String> result = new HashMap<String, String>();
        Collection<File> foundFiles = FileUtils.listFiles(new File(folder), new SuffixFileFilter(extension), null);
        for (File oneFile : foundFiles) {
            String pureFilename = FilenameUtils.getName(oneFile.getAbsolutePath());
            FileReader reader = new FileReader(oneFile);
            String fileContent = IOUtils.toString(reader);
            IOUtils.closeQuietly(reader);
            result.put(pureFilename, fileContent);
        }
        return result;
    }
}
