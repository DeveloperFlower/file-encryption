package org.developercookie.file.encryption.ui;

import org.developercookie.file.encryption.AESContentTransformer;
import org.developercookie.file.encryption.FileEncryption;
import org.developercookie.file.encryption.IllegalKeyException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * The Controller manages the control flow between the dialogs. Here it will open the ModeChooser. Dependent of the
 * clicked button it will show the EncryptionDialog or DecryptionDialog or it will close the whole application. The
 * event handler of all buttons of the dialogs are defined here. Created by developerCookie on 10.04.14.
 */
public class Controller {

    private JFrame dummyFrame;

    /**
     * The first dialog to display. The user can choose between encryption and decryption.
     */
    private ModeChooser modeChooser;

    /**
     * The encryption dialog to use for starting the encryption process.
     */
    private EncryptionDialog encryptionDialog;

    /**
     * The decryption dialog to use for starting the decryption process.
     */
    private DecryptionDialog decryptionDialog;

    /**
     * FileEncryption is used to make the encryption and decryption happen.
     */
    private FileEncryption fileEncryption;

    /**
     * All dialogs and the FileEncryption must be added to this Controller. After that the event handler can be filled.
     */
    public Controller(ModeChooser modeChooser, EncryptionDialog encryptionDialog, DecryptionDialog decryptionDialog, FileEncryption fileEncryption) {
        this.modeChooser = modeChooser;
        this.encryptionDialog = encryptionDialog;
        this.decryptionDialog = decryptionDialog;
        this.fileEncryption = fileEncryption;
        fillAllEventHandlers();
        locateDialogsToMiddle();
    }

    /**
     * Starts the controller by showing the ModeChooser dialog.
     */
    public void showGUI() {
        modeChooser.pack();
        encryptionDialog.pack();
        decryptionDialog.pack();
        modeChooser.setVisible(true);
    }

    /**
     * Fill all event handlers.
     */
    private void fillAllEventHandlers() {
        fillEventHandlerModeChooser();
        fillEventHandlersEncryptionDialog();
        fillEventHandlersDecryptionDialog();
    }

    /**
     * Defines the actions of the buttons of the ModeChooser dialog.
     */
    private void fillEventHandlerModeChooser() {
        modeChooser.addListenerEncryptionButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modeChooser.setVisible(false);
                encryptionDialog.setVisible(true);
            }
        });

        modeChooser.addListenerDecryptionButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modeChooser.setVisible(false);
                decryptionDialog.setVisible(true);
            }
        });

        modeChooser.addListenerCancelButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        modeChooser.addWindowListener(new CloseAppListener());
    }

    /**
     * Defines the actions for the encryption dialog.
     */
    private void fillEventHandlersEncryptionDialog() {
        encryptionDialog.addButtonCancelListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        encryptionDialog.addButtonOkListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choosing = JOptionPane.showConfirmDialog(null, "Dateien im Zielverzeichnis werden ggf. überschrieben.\nFortfahren?", "Bestätigung", JOptionPane.YES_NO_OPTION);
                if (choosing == JOptionPane.YES_OPTION) {
                    String sourceFolder = encryptionDialog.getSourceFolder();
                    String targetFolder = encryptionDialog.getTargetFolder();
                    String fileExtension = encryptionDialog.getFileExtension();
                    String key = encryptionDialog.getKey();

                    try {
                        fileEncryption.encryptFolder(sourceFolder, targetFolder, fileExtension, key);
                        JOptionPane.showMessageDialog(null, "Verschlüsslung abgeschlossen!", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Ein Fehler ist aufgetreten!. Fehlerbeschreibung:\n" + ex.getCause().toString(), "Fehler", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        encryptionDialog.addWindowListener(new CloseAppListener());
    }

    /**
     * Defines the action for the decryption dialog.
     */
    private void fillEventHandlersDecryptionDialog() {
        decryptionDialog.addButtonCancelListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        decryptionDialog.addButtonOkListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choosing = JOptionPane.showConfirmDialog(null, "Dateien im Zielverzeichnis werden ggf. überschrieben.\nFortfahren?", "Bestätigung", JOptionPane.YES_NO_OPTION);
                if (choosing == JOptionPane.YES_OPTION) {
                    String sourceFolder = decryptionDialog.getSourceFolder();
                    String targetFolder = decryptionDialog.getTargetFolder();
                    String key = decryptionDialog.getKey();

                    try {
                        fileEncryption.decryptFolder(sourceFolder, targetFolder, key);
                        JOptionPane.showMessageDialog(null, "Entschlüsslung abgeschlossen!", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Ein Fehler ist aufgetreten!. Fehlerbeschreibung:\n" + ex.getCause().toString(), "Fehler", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalKeyException ex) {
                        JOptionPane.showMessageDialog(null, "Schlüssel ist inkorrekt! Bitte versuchen Sie es erneut", "Fehler", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        decryptionDialog.addWindowListener(new CloseAppListener());
    }

    /**
     * Sets the location of all dialog to the middle of the screen.
     */
    private void locateDialogsToMiddle() {
        modeChooser.setLocationRelativeTo(null);
        encryptionDialog.setLocationRelativeTo(null);
        decryptionDialog.setLocationRelativeTo(null);
    }

    /**
     * Window listener that closes the whole application when a closing event is recognised, meaning someone pressed the
     * little x at the border of the window.
     */
    private class CloseAppListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    /**
     * Main method for starting the entire application.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Verschlüssluns-Applikation");
        frame.setUndecorated(true);
        frame.setVisible(true);

        ModeChooser modeChooser = new ModeChooser(frame);
        EncryptionDialog encryptionDialog = new EncryptionDialog(frame);
        DecryptionDialog decryptionDialog = new DecryptionDialog(frame);

        AESContentTransformer transformer = new AESContentTransformer();
        FileEncryption fileEncrypter = new FileEncryption(transformer);

        Controller controller = new Controller(modeChooser, encryptionDialog, decryptionDialog, fileEncrypter);
        controller.showGUI();
    }
}
