/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.util;

import fbot.lib.core.WMFWiki;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class FGUI {
    private FGUI() {
    }

    public static JPanel buildForm(String title, JComponent ... cl) {
        JPanel pl = new JPanel(new GridBagLayout());
        if (cl.length == 0 || cl.length % 2 == 1) {
            throw new UnsupportedOperationException("Either cl is empty or has an odd number of elements!");
        }
        if (title != null) {
            pl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        }
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        for (int i = 0; i < cl.length; i+=2) {
            c.gridx = 0;
            c.gridy = i;
            c.anchor = 13;
            pl.add((Component)cl[i], c);
            c.anchor = 10;
            c.weightx = 0.5;
            c.gridx = 1;
            c.gridy = i;
            c.ipady = 5;
            pl.add((Component)cl[i + 1], c);
            c.weightx = 0.0;
            c.ipady = 0;
        }
        return pl;
    }

    /**
     * Create a confirm dialog to ask the user for username and password; Exit if the login fails three times; otherwise return the wiki
     * @param domain the domain of the wiki
     * @return the wiki
     */
    public static WMFWiki login(String domain) {
        JTextField tf = new JTextField(12);
        JPasswordField pf = new JPasswordField(12);
        for (int i = 0; i < 3; ++i) {
            WMFWiki wiki;
            if (JOptionPane.showConfirmDialog(null, FGUI.buildForm("Login", new JLabel("User: "), tf, new JLabel("Password: "), pf), "Login", 2, -1) != 0) {
                System.exit(0);
            }
            if ((wiki = new WMFWiki(tf.getText().trim(), new String(pf.getPassword()), domain)).isVerified(domain)) {
                return wiki;
            }
            JOptionPane.showMessageDialog(null, "User/Password not recognized. Try again?", "Login error", JOptionPane.WARNING_MESSAGE);
        }
        JOptionPane.showMessageDialog(null, "Failed login 3 times. Program exiting","Login failed", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
        return null;
    }

    /**
     * Create a confirm dialog to ask the user for their commons.wikimedia.org username and password; Exit if the login fails three times; otherwise return a commons wikimedia wiki
     * @return the commons wiki
     */
    public static WMFWiki login() {
        return FGUI.login("commons.wikimedia.org");
    }

    public static JFrame simpleJFrame(String title, int exitmode, boolean resizable) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(exitmode);
        f.setResizable(resizable);
        return f;
    }

    public static void setJFrameVisible(JFrame f) {
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public static JPanel simpleJPanel(Component ... items) {
        JPanel p = new JPanel();
        for (Component c : items) {
            p.add(c);
        }
        return p;
    }

    public static JPanel boxLayout(int axis, Component ... items) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, axis));
        for (Component c : items) {
            p.add(c);
        }
        return p;
    }
}

