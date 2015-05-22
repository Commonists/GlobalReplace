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
import java.awt.Container;
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
    private static final String signin = "Commons:GlobalReplace/Sign-in";
    private static final String title = "GlobalReplace v0.3";
    private static final JTextField old_tf;
    private static final JTextField new_tf;
    private static final JTextField r_tf;
    private static final JProgressBar bar;
    private static final JButton button;
    private static boolean activated;

    static {
        old_tf = new JTextField(30);
        new_tf = new JTextField(30);
        r_tf = new JTextField(30);
        bar = new JProgressBar(0, 100);
        button = new JButton("Start/Stop");
        activated = false;
    }

    public static void main(String[] args) {
        wiki = FGUI.login();
        GlobalReplace.signin();
        GlobalReplace.randomSettings();
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                GlobalReplace.createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame f = FGUI.simpleJFrame("GlobalReplace v0.3", 3, true);
        f.getContentPane().add((Component)FGUI.buildForm("GlobalReplace v0.3", new JLabel("Old Title: "), old_tf, new JLabel("New Title: "), new_tf, new JLabel("Summary: "), r_tf), "Center");
        f.getContentPane().add((Component)FGUI.boxLayout(1, FGUI.simpleJPanel(button), bar), "South");
        FGUI.setJFrameVisible(f);
    }

    private static void randomSettings() {
        old_tf.setToolTipText("Hint: Use Ctrl+v or Command+v to paste text");
        new_tf.setToolTipText("Hint: Use Ctrl+v or Command+v to paste text");
        r_tf.setToolTipText("Hint: Enter an optional edit summary");
        bar.setStringPainted(true);
        bar.setString(String.format("Hello, %s! :)", wiki.whoami()));
        button.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new Thread(new GRThread()).start();
            }
        });
    }

    private static void signin() {
        String text = wiki.getPageText("Commons:GlobalReplace/Sign-in");
        if (text == null) {
            return;
        }
        if (!text.contains((CharSequence)wiki.whoami())) {
            wiki.edit("Commons:GlobalReplace/Sign-in", String.valueOf(text.trim()) + "\n#~~~~", "Signing in");
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
            this.old_name = Namespace.nss(old_tf.getText()).trim();
            this.new_name = Namespace.nss(new_tf.getText()).trim();
            this.reason = String.valueOf(r_tf.getText().trim().replace((CharSequence)"%s", (CharSequence)"%%s")) + " ([[%sCommons:GlobalReplace|%s]])";
            this.makeRegex();
        }

        @Override
        public void run() {
            if (!activated) {
                if (!this.sanityCheck()) {
                    return;
                }
                button.setText("Stop");
                GlobalReplace.negateActivated();
                this.doJob();
                wiki.switchDomain("commons.wikimedia.org");
                button.setText("Start");
            } else {
                button.setEnabled(false);
                GlobalReplace.negateActivated();
            }
        }

        private void doJob() {
            bar.setValue(0);
            button.setEnabled(false);
            this.setTextFieldState(false);
            ArrayList<Tuple<String, String>> l = wiki.globalUsage("File:" + this.old_name);
            button.setEnabled(true);
            if (l == null || l.size() == 0) {
                bar.setString(String.format("'%s' is not globally used", this.old_name));
            } else {
                bar.setMaximum(l.size());
                String domain = null;
                String text = null;
                for (int i = 0; i < l.size(); ++i) {
                    if (!this.updateStatus(i, l.get(i))) {
                        return;
                    }
                    if (domain != l.get((int)i).y) {
                        domain = (String)l.get((int)i).y;
                        wiki.switchDomain(domain);
                    }
                    if ((text = wiki.getPageText((String)l.get((int)i).x)) == null) continue;
                    Object[] arrobject = new Object[2];
                    arrobject[0] = domain.contains((CharSequence)"commons") ? "" : "Commons:";
                    arrobject[1] = "GlobalReplace v0.3";
                    wiki.edit((String)l.get((int)i).x, text.replaceAll(this.regex, this.new_name), String.format(this.reason, arrobject));
                }
                bar.setString("Done!");
            }
            this.setTextFieldState(true);
            GlobalReplace.negateActivated();
        }

        private boolean updateStatus(int i, Tuple<String, String> t) {
            if (!activated) {
                bar.setValue(0);
                bar.setString("Interrupted by user");
                this.setTextFieldState(true);
                button.setEnabled(true);
                return false;
            }
            bar.setValue(i + 1);
            bar.setString(String.format("Edit %s @ %s (%d/%d)", t.x, t.y, i + 1, bar.getMaximum()));
            return true;
        }

        private void setTextFieldState(boolean editable) {
            old_tf.setEditable(editable);
            new_tf.setEditable(editable);
            r_tf.setEditable(editable);
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

