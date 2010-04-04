package com.digitprop.tonicdemo;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.digitprop.tonic.VariableGridLayout;


/**	Demo dialog for the Tonic look and feel.
 * 
 * 	@author Markus Fischer
 *
 *  	<p>This software is under the <a href="http://www.gnu.org/copyleft/lesser.html" target="_blank">GNU Lesser General Public License</a>
 */

/*
 * ------------------------------------------------------------------------
 * Copyright (C) 2004 Markus Fischer
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307  USA
 * 
 * You can contact the author at:
 *    Markus Fischer
 *    www.digitprop.com
 *    info@digitprop.com
 * ------------------------------------------------------------------------
 */
public class TonicDemoDialog extends JDialog implements ActionListener
{
	/**	Creates an instance for the specified frame, with the 
	 * 	specified title.
	 */
	public TonicDemoDialog(Frame owner, String title)
	{
		super(owner, title, true);
		
		initGUI();
		
		pack();
		setSize(400, getHeight());
	}
	
	
	/**	Initializes the GUI components of this dialog. The central part
	 * 	of the dialog is a JTabbedPane
	 */
	public void initGUI()
	{
		JTabbedPane tabbedPane=new JTabbedPane();
		
		JPanel mainPanel=new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		mainPanel.add(new TitledSeparator("Magnetic core settings"));
		mainPanel.add(createMagneticCoreSettings());
		
		mainPanel.add(Box.createVerticalStrut(20));
		
		mainPanel.add(new TitledSeparator("Stratospheric extractor unit"));
		mainPanel.add(createStratosphericSettings());
		
		mainPanel.add(Box.createVerticalStrut(20));
		
		mainPanel.add(new TitledSeparator("Itinerary"));
		mainPanel.add(createItinerary());
				
		mainPanel.add(Box.createVerticalStrut(30));
		mainPanel.add(createButtonPanel());
		
		JPanel firstPanel=new JPanel(new BorderLayout());
		firstPanel.add(BorderLayout.NORTH, mainPanel);
		firstPanel.add(BorderLayout.CENTER, new JPanel());
		
		tabbedPane.addTab("General", firstPanel);
		
		JPanel emptyPanel=new JPanel(new BorderLayout());
		JLabel label=new JLabel("This panel intentionally left blank");
		label.setHorizontalAlignment(JLabel.CENTER);
		emptyPanel.add(BorderLayout.CENTER, label);
		tabbedPane.addTab("Advanced", emptyPanel);

		emptyPanel=new JPanel(new BorderLayout());
		label=new JLabel("This panel intentionally left blank");
		label.setHorizontalAlignment(JLabel.CENTER);
		emptyPanel.add(BorderLayout.CENTER, label);		
		tabbedPane.addTab("Ultra", emptyPanel);
		
		emptyPanel=new JPanel(new BorderLayout());
		label=new JLabel("This panel intentionally left blank");
		label.setHorizontalAlignment(JLabel.CENTER);
		emptyPanel.add(BorderLayout.CENTER, label);
		tabbedPane.addTab("Time", emptyPanel);
		
		JPanel centerPanel=new JPanel(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		centerPanel.add(tabbedPane);
		
		getContentPane().add(BorderLayout.CENTER, centerPanel);		
	}
	
	
	/**	Creates the button panel at the bottom of the dialog */
	private JPanel createButtonPanel()
	{
		JPanel result=new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		
		JButton button=new JButton("Ok");
		setPreferredButtonSize(button, 75, 0);
		button.addActionListener(this);
		result.add(button);

		button=new JButton("Cancel");
		setPreferredButtonSize(button, 75, 0);
		button.addActionListener(this);		
		result.add(button);
		
		return result;
	}
	
	
	private void setPreferredButtonSize(JButton button, int width, int height)
	{
		Dimension d=button.getPreferredSize();
		width=Math.max(width, d.width);
		height=Math.max(height, d.height);
		
		button.setPreferredSize(new Dimension(width, height));
	}
	
	
	/**	Creates a panel with some components */
	private JPanel createMagneticCoreSettings()
	{
		String fields[]=
			{
				"Heat dissipation:", "22.5",
				"Buoancy:", "0.54",
				"Bearing:", "14° 30' 45\"",
				"C", "Activate gyroscope compensation",
				"C", "Seal loading bay",
				"C", "More magic"
			};
			
			
		JPanel result=new JPanel(new BorderLayout());
		result.add(BorderLayout.WEST, Box.createHorizontalStrut(60));
		
		JPanel p2=new JPanel(new BorderLayout());
		
		JPanel p=new JPanel(new VariableGridLayout(fields.length/2, 2, 2, 2));
		p2.add(BorderLayout.WEST, p);
		p2.add(BorderLayout.CENTER, new JPanel());
		result.add(BorderLayout.CENTER, p2);
		
		for(int i=0; i<fields.length; i+=2)
		{
			if(fields[i].equals("C"))
			{
				p.add(new JLabel(""));
				p.add(new JCheckBox(fields[i+1]));
			}
			else
			{
				JLabel label=new JLabel(fields[i], JLabel.RIGHT);
				label.setAlignmentX(1.0f);
				p.add(label);
			
				p.add(new JTextField(fields[i+1], 5));
			}
		}		
		
		return result;
	}
	
	
	/**	Creates a panel with some more components */
	private JPanel createStratosphericSettings()
	{
		String fields[]=
			{
				"R", "Enhanced for plasma effect",
				"R", "Reduced for fancy color effect",
				"R", "Sideways to allow singularity"
			};
			
			
		JPanel result=new JPanel(new BorderLayout());
		result.add(BorderLayout.WEST, Box.createHorizontalStrut(60));
		
		JPanel p2=new JPanel(new BorderLayout());
		
		JPanel p=new JPanel(new VariableGridLayout(fields.length/2, 2, 2, 2));
		p2.add(BorderLayout.WEST, p);
		p2.add(BorderLayout.CENTER, new JPanel());
		result.add(BorderLayout.CENTER, p2);
		
		for(int i=0; i<fields.length; i+=2)
		{
			if(fields[i].equals("R"))
			{
				p.add(new JLabel(""));
				p.add(new JRadioButton(fields[i+1]));
			}
			else
 			{
				JLabel label=new JLabel(fields[i], JLabel.RIGHT);
				label.setAlignmentX(1.0f);
				p.add(label);
			
				p.add(new JTextField(fields[i+1], 5));
			}
		}		
		
		return result;
	}
	
	
	/**	Creates a panel with even more components */
	private JPanel createItinerary()
	{		
		String fields[]=
			{
				"[Spinner]Central station:", "",
				"[Progressbar]Journey progress:", "",
				"Story scaffold:", "Laotse",
				"Echo:", "Barely discernible"
			};
			
			
		JPanel result=new JPanel(new BorderLayout());
		result.add(BorderLayout.WEST, Box.createHorizontalStrut(60));
		
		JPanel p2=new JPanel(new BorderLayout());
		
		JPanel p=new JPanel(new VariableGridLayout(fields.length/2, 2, 2, 2));
		p2.add(BorderLayout.WEST, p);
		p2.add(BorderLayout.CENTER, new JPanel());
		result.add(BorderLayout.CENTER, p2);
		
		for(int i=0; i<fields.length; i+=2)
		{
			if(fields[i].startsWith("[Spinner]"))
			{
				p.add(new JLabel(fields[i].substring(9)));
				JSpinner sp=new JSpinner();
				sp.setModel(new SpinnerNumberModel(837, 0, 1000, 1));
				
				JPanel tmpPanel=new JPanel(new BorderLayout());
				tmpPanel.add(BorderLayout.WEST, sp);
				tmpPanel.add(BorderLayout.CENTER, new JPanel());
				p.add(tmpPanel);
			}
			else if(fields[i].startsWith("[Progressbar]"))
			{
				p.add(new JLabel(fields[i].substring(13)));
				JProgressBar pb=new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
				pb.setValue(68);
				p.add(pb);
			}
			else
			{
				JLabel label=new JLabel(fields[i], JLabel.RIGHT);
				label.setAlignmentX(1.0f);
				p.add(label);
			
				p.add(new JTextField(fields[i+1], 5));
			}
		}		
		
		return result;
	}
	
	
	/**	Closes this dialog when the user presses one of the buttons */
	public void actionPerformed(ActionEvent e)
	{
		setVisible(false);
	}	


	/**	A horizontal line with a title */	
	class TitledSeparator extends JPanel
	{
		private JLabel label;
		
		private JSeparator sep;
		
		
		public TitledSeparator(String title)
		{
			setLayout(null);
			
			label=new JLabel(title);
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			add(label);
			
			sep=new JSeparator();
			add(sep);
		}
		
		
		public void doLayout()
		{
			label.setBounds(10, 0, label.getPreferredSize().width, label.getPreferredSize().height+10);
			sep.setBounds(label.getWidth()+10, label.getHeight()/2, getWidth()-label.getWidth()-10, 1);
		}

				
		public Dimension getPreferredSize()
		{
			return new Dimension(2000, label.getPreferredSize().height+10);
		}
	}
}
