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
    private static W commonsWiki;
    private static final String SIGN_UP = "Commons:GlobalReplace/Sign-in";
    private static final String VERSION = "v0.3.2";
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
        commonsWiki = FGUI.login();
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
    	OLD_TF.setToolTipText("Hint: Use Ctrl+v or Command+v to paste text");
        NEW_TF.setToolTipText("Hint: Use Ctrl+v or Command+v to paste text");
        REASON_TF.setToolTipText("Hint: Enter an optional edit summary");
        BAR.setStringPainted(true);
        BAR.setString(String.format("Hello, %s! :)", commonsWiki.whoami()));
        BUTTON.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new Thread(new GRThread()).start();
            }
        });

        // Create GUI
        JFrame f = FGUI.simpleJFrame(TITLE, 3, true);
        f.getContentPane().add((Component)FGUI.buildForm(TITLE, new JLabel("Old Title: "), OLD_TF, new JLabel("New Title: "), NEW_TF, new JLabel("Summary: "), REASON_TF), "Center");
        f.getContentPane().add((Component)FGUI.boxLayout(1, FGUI.simpleJPanel(BUTTON), BAR), "South");
        FGUI.setJFrameVisible(f);
    }

    /**
     * Sign up any new user of this tool; Do nothing if the user already signed up
     */
    private static void signup() {
        String text = commonsWiki.getPageText(SIGN_UP);
        if (text == null) {
            return;
        }
        if (!text.contains(commonsWiki.whoami())) {
            boolean success = commonsWiki.edit(SIGN_UP, String.valueOf(text.trim()) + "\n#~~~~", "Signing up via "
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
            this.reason = String.valueOf(REASON_TF.getText().trim().replace((CharSequence)"%s", (CharSequence)"%%s")) + " ([[%sCommons:GlobalReplace|%s]])";
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
                commonsWiki.switchDomain("commons.wikimedia.org");
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
            ArrayList<Tuple<String, String>> l = commonsWiki.globalUsage("File:" + this.old_name);
            BUTTON.setEnabled(true);
            if (l == null || l.size() == 0) {
                BAR.setString(String.format("'%s' is not globally used", this.old_name));
            } else {
                BAR.setMaximum(l.size());
                String domain = null;
                String text = null;
                for (int i = 0; i < l.size(); ++i) {
                    if (!this.updateStatus(i, l.get(i))) {
                        return;
                    }
                    if (domain != l.get((int)i).y) {
                        domain = (String)l.get((int)i).y;
                        commonsWiki.switchDomain(domain);
                    }
                    if ((text = commonsWiki.getPageText((String)l.get((int)i).x)) == null) continue;
                    Object[] arrobject = new Object[2];
                    arrobject[0] = domain.contains((CharSequence)"commons") ? "" : "Commons:";
                    arrobject[1] = TITLE;
                    commonsWiki.edit((String)l.get((int)i).x, text.replaceAll(this.regex, this.new_name), String.format(this.reason, arrobject));
                }
                BAR.setString("Done!");
            }
            this.setTextFieldState(true);
            GlobalReplace.negateActivated();
        }

        private boolean updateStatus(int i, Tuple<String, String> t) {
            if (!activated) {
                BAR.setValue(0);
                BAR.setString("Interrupted by user");
                this.setTextFieldState(true);
                BUTTON.setEnabled(true);
                return false;
            }
            BAR.setValue(i + 1);
            BAR.setString(String.format("Edit %s @ %s (%d/%d)", t.x, t.y, i + 1, BAR.getMaximum()));
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

        private boolean sanityCheck() {
            boolean status;
            boolean bl = status = WikiFile.canUpload(this.old_name) && WikiFile.canUpload(this.new_name);
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

