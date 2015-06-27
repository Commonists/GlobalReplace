/*
 * Decompiled with CFR 0_101.
 */
package fbot.ft;

import fbot.lib.core.Namespace;
import fbot.lib.core.W;
import fbot.lib.core.auxi.Tuple;
import fbot.lib.util.FGUI;
import fbot.lib.util.WikiFile;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class GlobalReplace {
    private static W wiki;
    private static final String SIGN_UP = "Commons:GlobalReplace/Sign-in";
    private static final String VERSION = "v0.3.3";
    private static final String TITLE = "GlobalReplace " + VERSION;
    private static final JTextField OLD_TF;
    private static final JTextField NEW_TF;
    private static final JTextField REASON_TF;
    private static final JProgressBar BAR;
    private static final JButton BUTTON;
    private static boolean activated;

    static {
        OLD_TF = new JTextField(30);
        NEW_TF = new JTextField(30);
        REASON_TF = new JTextField(30);
        BAR = new JProgressBar(0, 100);
        BUTTON = new JButton("Start/Stop");
        activated = false;
    }

    public static void main(String[] args) {
        wiki = FGUI.login();
        GlobalReplace.signup();
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                GlobalReplace.createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
    	// Settings
    	OLD_TF.setToolTipText("Use Ctrl+v or Command+v to paste text");
        NEW_TF.setToolTipText("Use Ctrl+v or Command+v to paste text");
        REASON_TF.setToolTipText("Enter an optional edit summary");
        BAR.setStringPainted(true);
        BAR.setString(String.format("Hello, %s! :)", wiki.whoami()));
        BUTTON.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new Thread(new GRThread()).start();
            }
        });

        // Create GUI
        JFrame f = FGUI.simpleJFrame(TITLE, 3, true);
        f.getContentPane().add((Component)FGUI.buildForm("Replacement", new JLabel("Old Title: "), OLD_TF, new JLabel("New Title: "), NEW_TF, new JLabel("Summary: "), REASON_TF), "Center");
        f.getContentPane().add((Component)FGUI.boxLayout(1, FGUI.simpleJPanel(BUTTON), BAR), "South");
        FGUI.setJFrameVisible(f);
    }

    /**
     * Sign up any new user of this tool; Do nothing if the user already signed up
     */
    private static void signup() {
        String text = wiki.getPageText(SIGN_UP);
        if (text == null) {
            return;
        }
        if (!text.contains(wiki.whoami())) {
            boolean success = wiki.edit(SIGN_UP, text.trim() + "\n#~~~~", "Signing up via "
            		+ TITLE);
            if (!success) {
                JOptionPane.showConfirmDialog(null, "You are not allowed to use this tool; Please request permission at "
                		+ SIGN_UP
                		+ ".  Program exiting","Missing permission",JOptionPane.OK_CANCEL_OPTION);
                System.exit(0);
            }
        }
    }

    private static synchronized void negateActivated() {
        activated = !activated;
    }

    private static class GRThread
    implements Runnable {
        private String old_name;
        private String new_name;
        private String reason;
        private String regex;

        private GRThread() {
            this.old_name = Namespace.nss(OLD_TF.getText()).trim();
            this.new_name = Namespace.nss(NEW_TF.getText()).trim();
            this.reason = REASON_TF.getText().trim().replace("%s", "%%s") + " ([[%sCommons:GlobalReplace|%s]])";
            this.makeRegex();
        }

        @Override
        public void run() {
            if (!activated) {
                if (!this.sanityCheck()) {
                    return;
                }
                BUTTON.setText("Stop");
                GlobalReplace.negateActivated();
                this.doJob();
                wiki.switchDomain("commons.wikimedia.org");
                BUTTON.setText("Start");
            } else {
                BUTTON.setEnabled(false);
                GlobalReplace.negateActivated();
            }
        }

        private void doJob() {
            BAR.setValue(0);
            BUTTON.setEnabled(false);
            this.setTextFieldState(false);
            ArrayList<Tuple<String, String>> list = wiki.globalUsage("File:" + this.old_name);
            BUTTON.setEnabled(true);
            if (list == null || list.size() == 0) {
                BAR.setString(String.format("'%s' is not globally used", this.old_name));
            } else {
                BAR.setMaximum(list.size());
                String domain = null;
                String text = null;
                for (int i = 0; i < list.size(); ++i) {
                    if (!this.updateStatus(i, list.get(i))) {
                        return;
                    }
                    if (domain != list.get(i).y) {
                        domain = list.get(i).y;
                        wiki.switchDomain(domain);
                    }
                    if ((text = wiki.getPageText(list.get(i).x)) == null) continue;
                    Object[] arrobject = new Object[2];
                    arrobject[0] = domain.contains("commons") ? "" : "Commons:";
                    arrobject[1] = TITLE;
                    wiki.edit(list.get(i).x, text.replaceAll(this.regex, this.new_name), String.format(this.reason, arrobject));
                }
                BAR.setValue(list.size());
                BAR.setString("Done!");
            }
            this.setTextFieldState(true);
            GlobalReplace.negateActivated();
        }

        /**
         * Check if the program is still activated and update the progress bar
         * @param progress how much work was done already
         * @param tuple the string tuple with ...
         * @return if the program is activated
         */
        private boolean updateStatus(int progress, Tuple<String, String> tuple) {
            if (!activated) {
                BAR.setValue(0);
                BAR.setString("Interrupted by user");
                this.setTextFieldState(true);
                BUTTON.setEnabled(true);
                return false;
            }
            BAR.setValue(progress);
            BAR.setString(String.format("Edit %s @ %s (%d/%d)", tuple.x, tuple.y, progress + 1, BAR.getMaximum()));
            return true;
        }

        private void setTextFieldState(boolean editable) {
            OLD_TF.setEditable(editable);
            NEW_TF.setEditable(editable);
            REASON_TF.setEditable(editable);
        }

        private void makeRegex() {
            this.regex = this.old_name;
            for (String s : new String[]{"(", ")", "[", "]", "{", "}", "^", "-", "=", "$", "!", "|", "?", "*", "+", ".", "<", ">"}) {
                this.regex = this.regex.replace((CharSequence)s, (CharSequence)("\\" + s));
            }
            this.regex = this.regex.replaceAll("( |_)", "( |_)");
        }

        /**
         * Check if old and new name are valid; Notify user if they are not 
         * @return if the names are valid
         */
        private boolean sanityCheck() {
            boolean status = WikiFile.canUpload(this.old_name) && WikiFile.canUpload(this.new_name);
            if (!status) {
                JOptionPane.showMessageDialog(null, "You can only replace valid file names");
            }
            return status;
        }

        /* synthetic GRThread(GRThread gRThread) {
            GRThread gRThread2;
            gRThread2();
        } */
    }

}

