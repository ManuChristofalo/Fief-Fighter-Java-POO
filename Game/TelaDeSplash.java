import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;


public class TelaDeSplash extends JWindow{
	private JLabel label=new JLabel();
	
	public TelaDeSplash(){
		setSize(1300,700);
		ImageIcon icon=new ImageIcon("Splash.png");

		label.setIcon(icon);
		this.add(label);
		setLocationRelativeTo(null);
		setVisible(true);

		try{
			Thread.sleep(2500);
			setVisible(false);
			dispose();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
