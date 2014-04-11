package org.developercookie.file.encryption;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Tests the FileEncryption class. It only tests the methods for encryption and decryption a single file. Created by
 * developerCookie on 10.04.14.
 */
public class FileEncryptionTest {
    /**
     * The original file that contains some test content.
     */
    private static final String testFilename = "/tmp/test.txt";

    /* The output folder for the encryption.
    */
    private static final String encryptionOutputFolder = "/tmp/enc";

    /**
     * The output folder for the decryption.
     */
    private static final String decryptionOutputFolder = "/tmp/dec";

    /**
     * The file that will result in encryption.
     */
    private static final String encryptedFile = encryptionOutputFolder + "/test.txt.enc";

    /**
     * The file that will result in decryption.
     */
    private static final String decryptedFile = decryptionOutputFolder + "/test.txt";

    /**
     * The test file will be created and both output folders are created.
     */
    @Before
    public void init() throws IOException {
        FileWriter fileWriter = new FileWriter(testFilename);
        String text = RandomStringUtils.random(2048, true, true);
        fileWriter.append(text);
        IOUtils.closeQuietly(fileWriter);

        new File(encryptionOutputFolder).mkdirs();
        new File(decryptionOutputFolder).mkdirs();
    }

    /**
     * The test file and the output folder with content are removed.
     */
    @After
    public void cleanup() {
        FileUtils.deleteQuietly(new File(testFilename));
        FileUtils.deleteQuietly(new File(encryptionOutputFolder));
        FileUtils.deleteQuietly(new File(decryptionOutputFolder));
    }

    /**
     * We write a file with some test data and will encrypt that file. It is stored to the encryption output folder.
     * Then it will be decrypted and stored to the decryption folder. The content must stay the same.
     */
    @Test
    public void writeLittleFile() throws Exception {
        NothingTransformer transformer = new NothingTransformer();
        FileEncryption fileEncryption = new FileEncryption(transformer);

        fileEncryption.encrypt(testFilename, encryptionOutputFolder, "12");
        fileEncryption.decrypt(encryptedFile, decryptionOutputFolder, "12");

        List<String> filesInOutputFolder = listFileFrom(encryptionOutputFolder, FileEncryption.ENCRYPTION_EXTENSION);
        Assert.assertThat(filesInOutputFolder, CoreMatchers.hasItem(encryptedFile));
        Assert.assertTrue(FileUtils.contentEquals(new File(testFilename), new File(encryptedFile)));

        List<String> filesInDecryptionFolder = listFileFrom(decryptionOutputFolder, "txt");
        Assert.assertThat(filesInDecryptionFolder, CoreMatchers.hasItem(decryptedFile));
        Assert.assertTrue(FileUtils.contentEquals(new File(testFilename), new File(decryptedFile)));
    }

    /**
     * We take the AESContentTransformer to encrypt and decrypt the test file.
     */
    @Test
    public void writeLittleFileWithAES() throws Exception {
        AESContentTransformer transformer = new AESContentTransformer();
        FileEncryption fileEncryption = new FileEncryption(transformer);

        fileEncryption.encrypt(testFilename, encryptionOutputFolder, "12");
        fileEncryption.decrypt(encryptedFile, decryptionOutputFolder, "12");

        List<String> filesInEncryptedOutputFolder = listFileFrom(encryptionOutputFolder, FileEncryption.ENCRYPTION_EXTENSION);
        Assert.assertThat(filesInEncryptedOutputFolder, CoreMatchers.hasItem(encryptedFile));

        List<String> filesInDecryptedOutputFolder = listFileFrom(decryptionOutputFolder, "txt");
        Assert.assertThat(filesInDecryptedOutputFolder, CoreMatchers.hasItem(decryptedFile));

        Assert.assertTrue(FileUtils.contentEquals(new File(testFilename), new File(decryptedFile)));
    }

    @Test(expected = IllegalKeyException.class)
    public void writeFileWrongKey() throws Exception {
        AESContentTransformer transformer = new AESContentTransformer();
        FileEncryption fileEncryption = new FileEncryption(transformer);

        fileEncryption.encrypt(testFilename, encryptionOutputFolder, "12");
        fileEncryption.decrypt(encryptedFile, decryptionOutputFolder, "13");
    }

    /**
     * Returns the full files names of all files in the given <code>folder</coder> that have the given
     * <code>fileExtension</code>.
     */
    private List<String> listFileFrom(String folder, String fileExtension) {
        Collection<File> foundFiles = FileUtils.listFiles(new File(folder), new SuffixFileFilter(fileExtension), null);

        List<String> result = new ArrayList<String>();
        for (File oneFile : foundFiles) {
            result.add(oneFile.getAbsolutePath());
        }
        return result;
    }
}
