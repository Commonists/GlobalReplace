package globalreplace;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.security.auth.login.LoginException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import jwiki.core.Wiki;

/**
 * Static GUI factories to make building tools easier.
 * 
 * @author Fastily
 * 
 */
public class FGUI
{
	/**
	 * Hiding from javadoc
	 */
	private FGUI()
	{

	}

	/**
	 * Creates a form in the form of a JPanel. Fields are dynamically resized when the window size is modified by the
	 * user.
	 * 
	 * @param title Title to use in the border. Specify null if you don't want one. Specify empty string if you want just
	 *           border.
	 * @param cl The list of containers to work with. Elements should be in the order, e.g. JLabel1, JTextField1, JLabel
	 *           2, JTextField2, etc.
	 * 
	 * @return A JPanel with a SpringLayout in a form.
	 * @throws UnsupportedOperationException If cl.length == 0 || cl.length % 2 == 1
	 */
	public static JPanel buildForm(String title, JComponent... cl)
	{
		JPanel pl = new JPanel(new GridBagLayout());

		// Sanity check. There must be at least two elements in cl
		if (cl.length == 0 || cl.length % 2 == 1)
			throw new UnsupportedOperationException("Either cl is empty or has an odd number of elements!");

		if (title != null)
			borderTitleWrap(pl, title);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		for (int i = 0; i < cl.length; i += 2)
		{
			c.gridx = 0;
			c.gridy = i;
			c.anchor = GridBagConstraints.EAST; // should anchor East
			pl.add(cl[i], c);

			c.anchor = GridBagConstraints.CENTER; // reset anchor to default

			c.weightx = 0.5; // Fill weights
			c.gridx = 1;
			c.gridy = i;
			c.ipady = 5; // sometimes components render funky when there is no extra vertical buffer
			pl.add(cl[i + 1], c);

			// reset default values for next iteration
			c.weightx = 0;
			c.ipady = 0;
		}

		return pl;
	}

	/**
	 * Wraps a text component in a border.
	 * @param x The text component to wrap
	 * @param title The title to use
	 * @return A JPanel with a border containing the text component.
	 */
	public static JPanel borderTitleWrap(JTextComponent x, String title)
	{
		JPanel p = new JPanel(new BorderLayout());
		borderTitleWrap(p, title);
		
		p.add(x, BorderLayout.CENTER);
		return p;
	}
	
	/**
	 * Wraps a component in a border. 
	 * @param p The component to wrap 
	 * @param title The title to assign to the border.
	 * 
	 * @return The same component, <code>p</code>, so you can stack statements.
	 */
	public static JComponent borderTitleWrap(JComponent p, String title)
	{
		p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		return p;
	}
	
	
	/**
	 * Provides GUI login. Automatically terminates program after 3 failed logins.
	 * 
	 * @param domain The short style domain to use
	 * @return A Wiki object created by logging in.
	 */
	public static Wiki login(String domain)
	{

		JTextField tf = new JTextField(12);
		JPasswordField pf = new JPasswordField(12);

		for (int i = 0; i < 3; i++)
		{
			if (JOptionPane.showConfirmDialog(null, buildForm("Login", new JLabel("User: "), tf, new JLabel("Password: "), pf),
					"Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION)
				System.exit(0);
			try
			{
				return new Wiki(tf.getText().trim(), new String(pf.getPassword()), domain);
			}
			catch (Throwable e)
			{
				JOptionPane.showConfirmDialog(null, "User/Password not recognized. Try again?");
			}
		}
		showErrorAndExit("Failed login 3 times.  Program exiting", 0);
		return null; // never reaches here - shut up compiler
	}

	/**
	 * Provides GUI login to Wikimedia Commons. Automatically terminates program after 3 failed logins.
	 *
	 * @return A Wiki object created by logging in.
	 */
	public static Wiki login() throws LoginException
	{
		return login("commons.wikimedia.org");
	}
	
	/**
	 * Creates a simple JFrame with some default settings.
	 * 
	 * @param title The title of the JFrame.
	 * @param exitmode The exit mode (e.g. JFrame.EXIT_ON_CLOSE)
	 * @param resizable Set to true if the window should be resizable by user.
	 * @return A JFrame
	 */
	public static JFrame simpleJFrame(String title, int exitmode, boolean resizable)
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame f = new JFrame(title);
		f.setDefaultCloseOperation(exitmode);
		f.setResizable(resizable);

		return f;
	}

	/**
	 * Sets a JFrame to be visible, packs it, and centers it.
	 * 
	 * @param f The frame to perform this operation on.
	 */
	public static void setJFrameVisible(JFrame f)
	{
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	/**
	 * Load Components into a JPanel using a FlowLayout.
	 * 
	 * @param items The items to load
	 * @return The JPanel.
	 */
	public static JPanel simpleJPanel(Component... items)
	{
		JPanel p = new JPanel();
		for (Component c : items)
			p.add(c);
		return p;
	}

	/**
	 * Make a JPanel with a box layout, with the given items.
	 * 
	 * @param axis The direction to go in. See BoxLayout fields.
	 * @param items The Components to add. Components will be added in the order passed in.
	 * @return The JPanel.
	 */
	public static JPanel boxLayout(int axis, Component... items)
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, axis));
		for (Component c : items)
			p.add(c);
		return p;
	}
	
	/**
	 * Merges two components into a JPanel with BorderLayout.
	 * @param top The component to go on top (NORTH)
	 * @param bottom The component to go on bottom (SOUTH)
	 * @return The new JPanel.
	 */
	public static JPanel topBottomBorderMerge(JComponent top, JComponent bottom)
	{
		JPanel p = new JPanel(new BorderLayout());
		p.add(top, BorderLayout.NORTH);
		p.add(bottom, BorderLayout.SOUTH);
		
		return p;
	}
	
	/**
	 * Makes a progress bar with a painted string.  Min set to 0, Max set to 100.
	 * @param initial A string to display on the JProgressBar.  Set to null to disable.  
	 * @return The JProgressBar
	 */
	public static JProgressBar makePB(String initial)
	{
		JProgressBar b = new JProgressBar(0, 100);
		b.setStringPainted(true);
		
		if(initial != null)
			b.setString(initial);
		
		return b;
	}
	
	
	/**
	 * Shows error as Messagebox and exits.
	 * 
	 * @param s The error message
	 * @param err Exit error code.
	 */
	public static void showErrorAndExit(String s, int err)
	{
		JOptionPane.showMessageDialog(null, s);
		System.exit(err);
	}
	
	
	/**
	 * Checks if a text component is empty, ignoring whitespace
	 * @param tc The text component to check
	 * @return True if the text component is empty
	 */
	public static boolean tcIsEmpty(JTextComponent tc)
	{
		return getTCText(tc).isEmpty();
	}
	
	/**
	 * Gets the text of a text component. Removes leading and trailing whitespace.
	 * @param tc The text component to fetch text from
	 * @return The text of the text component.
	 */
	public static String getTCText(JTextComponent tc)
	{
		return tc.getText().trim();
	}
}