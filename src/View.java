import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.*;


public class View extends JFrame{

	private JTextField jtfFName = new JTextField(20);
	private JComboBox jcbFunc = new JComboBox(new String[]{"h1", "h2"});
	private JButton jbShow = new JButton("Show Strategy");
	private JTextArea jtaOutput = new JTextArea("Strategy state output...", 5, 30);		
	private JPanel p2 = new JPanel(new BorderLayout(10, 10));
	private JButton jbNext = new JButton("Next");
	private JPanel p4 = new JPanel(new GridLayout(2, 10));
	
	private ImageIcon[] image = new ImageIcon[9];
	private JLabel[] jlImage = new JLabel[9];
	
	private int[] order = new int[9];
	private SearchTree solution = new SearchTree();
	
	public View() {
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 2,2));
		JLabel jlFName = new JLabel("  File Name: ");
		JLabel jlChoice = new JLabel("   Choose a function: ");
		p1.add(jlFName);
		p1.add(jtfFName);
		p1.add(jlChoice);
		p1.add(jcbFunc);
		p1.setBorder(new TitledBorder("Input"));
		
		JPanel p3 = new JPanel(new BorderLayout(10, 10));
		p3.add(jbShow, BorderLayout.NORTH);
		jtaOutput.setLineWrap(true);
		JScrollPane jscPane = new JScrollPane(jtaOutput);
		p3.add(jscPane, BorderLayout.CENTER);
		p2.add(p3, BorderLayout.NORTH);
		
		for(int i = 1; i < 10; i++) {//Í¼Æ¬³õÊ¼»¯
			image[i-1] = new ImageIcon(i+".png");
		}
		for(int i = 0; i < 9; i++) {
			jlImage[i] = new JLabel(image[i]);
		}
		
		for(int i = 0; i < 9; i++) {
			p4.add(jlImage[i]);
			if (i == 3)
				p4.add(jbNext);
		}

		p2.add(p4, BorderLayout.CENTER);
		p2.setBorder(new TitledBorder("Output"));
		
		setLayout(new BorderLayout(10, 10));
		add(p1, BorderLayout.NORTH);
		add(p2, BorderLayout.CENTER);
		
		jbShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				try {
					readFile();
					p4.removeAll();
					showPics();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				solution.solute(order, jcbFunc.getSelectedIndex(), jtaOutput);
			
			}
		});
		
		jbNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				order = solution.next(jtaOutput);
				if (order != null)
					showPics();
				else 
					jtaOutput.append("already completed!\n");
			}
		});
	}
	
	public void readFile() throws Exception {
		for(int i = 0; i < 9; i++)
			order[i] = 9;
		
		File file = new File(jtfFName.getText());
		Scanner input = new Scanner(file);	
		
		while(input.hasNext()) {
			String data = input.next();
			int n1 = data.indexOf(',');
			int n2 = data.indexOf(',', n1+1);
			int row = Integer.parseInt(data.substring(0, n1));
			int col = Integer.parseInt(data.substring(n1+1, n2));
			int num = Integer.parseInt(data.substring(n2+1));
			System.out.println(row + " " + col + " " + num);
			order[(row-1)*4+col-1] = num;
		}
		
		input.close();
	}
	
	public void showPics() {
		for(int i = 0; i < 9; i++) {
			System.out.println(i + " " + order[i]);
			int n = order[i];
			p4.add(jlImage[n-1]);
			if (i == 3)
				p4.add(jbNext);
		}
		p2.validate();		
	}
	
	public static void main(String[] args) {
		JFrame frame = new View();
		frame.setTitle("PUZZLE");
		frame.setSize(600, 500);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
