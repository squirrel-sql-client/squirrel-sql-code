package net.sourceforge.squirrel_sql.plugins.mssql.gui;

/*
 * Copyright (C) 2004 Ryan Walberg <generalpf@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Date;
import org.jCharts.chartData.ChartDataException;
import org.jCharts.chartData.PieChartDataSet;
import org.jCharts.nonAxisChart.PieChart2D;
import org.jCharts.properties.*;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.BaseDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;

public class MonitorPanel extends net.sourceforge.squirrel_sql.client.session.mainpanel.BaseMainPanelTab {
    
    private Connection _conn = null;
    
    private Date _refreshDate;
    private JPanel _mainPanel;
    
    private JPanel _cpuHistory = null;
    private JPanel _ioHistory = null;
    private JSlider _frequency = null;
    
    private IDataSetViewer _whoViewer;
    private CallableStatement _whoStmt = null;
    private ResultSetDataSet _whoDataSet = null;
    
    private IDataSetViewer _perfViewer;
    private PreparedStatement _perfStmt = null;
    private ResultSetDataSet _perfDataSet = null;
    
    private CallableStatement _monitorStmt = null;
    
    private Timer _refreshTimer = null;
    
    private boolean _haveSession = false;
    
    private boolean _inRefresh = false;
    
    /** Creates a new instance of MonitorPanel */
    public MonitorPanel() {
        super();
    }
    
    public java.awt.Component getComponent() {
        if (_mainPanel == null) {
            _mainPanel = buildMainPanel();
            
            // create the timer, but DO NOT start it.
            _refreshTimer = new Timer(_frequency.getValue(),new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!_haveSession)
                        return;
                    if (_inRefresh)
                        return;
                    _inRefresh = true;
                    refreshData();
                    _inRefresh = false;
                }
            });
        }
        return _mainPanel;
    }
    
    public String getHint() {
        return "Displays the current activity on the SQL Server.";
    }
    
    public String getTitle() {
        if (_refreshDate == null)
            return "Monitor";
        else {
            java.text.DateFormat fmt = new java.text.SimpleDateFormat();
            return "Monitor (as of " + fmt.format(_refreshDate) + ")";
        }
    }
    
    protected void refreshComponent() {
        if (!_haveSession) {
            try {
                _conn = this.getSession().getSQLConnection().getConnection();

                _whoStmt = _conn.prepareCall("{ call sp_who }");
                _whoDataSet = new ResultSetDataSet();

                _perfStmt = _conn.prepareStatement("SELECT * FROM master.dbo.sysperfinfo");
                _perfDataSet = new ResultSetDataSet();
                
                _monitorStmt = _conn.prepareCall("{ call sp_monitor }");

                _haveSession = true;
            }
            catch (java.sql.SQLException ex) {
                ex.printStackTrace();
            }
        }
        refreshData();
    }
    
    private void refreshData() {
        /*if (!this.getComponent().isFocusOwner())
            return;*/
        
        ResultSet rs;
        
        try {
            _refreshDate = new Date();
            
            rs = _whoStmt.executeQuery();
            _whoDataSet.setResultSet(rs);
            _whoViewer.show(_whoDataSet);
            
            rs = _perfStmt.executeQuery();
            _perfDataSet.setResultSet(rs);
            _perfViewer.show(_perfDataSet);
            
            // don't generate the graph crap.
            if (true)
                return;
            
            if (_monitorStmt.execute()) {
                rs = _monitorStmt.getResultSet();
                if (_monitorStmt.getMoreResults()) {
                    rs = _monitorStmt.getResultSet();
                    if (rs.next()) {
                        // cpu_busy                  io_busy                   idle                      
                        // ------------------------- ------------------------- ------------------------- 
                        // 10071(29)-0%              10195(36)-0%              3084271(11478)-96%  
                        String cpuStatus = rs.getString(1);
                        String ioStatus = rs.getString(2);

                        String cpuPct = cpuStatus.substring(cpuStatus.indexOf("-") + 1,cpuStatus.indexOf("%"));
                        String ioPct = ioStatus.substring(ioStatus.indexOf("-") + 1,ioStatus.indexOf("%"));

                        int cpu;
                        int io;
                        try {
                            cpu = Integer.parseInt(cpuPct);
                        }
                        catch (java.lang.NumberFormatException e) {
                            cpu = 0;
                        }
                        try {
                            io = Integer.parseInt(ioPct);
                        }
                        catch (java.lang.NumberFormatException e) {
                            io = 0;
                        }

                        String[] labels = { "Busy", "Non-busy" };
                        java.awt.Paint[] colors = { java.awt.Color.red, java.awt.Color.blue };
                        
                        double[] cpuData = { cpu, 100 - cpu };
                        double[] ioData = { io, 100 - io };
                        
                        try {
                            PieChart2DProperties cpuProps = new PieChart2DProperties();
                            cpuProps.setPieLabelType(org.jCharts.types.PieLabelType.VALUE_LABELS);
                            cpuProps.setBorderPaint(_cpuHistory.getBackground());
                            PieChartDataSet cpuDataSet = new PieChartDataSet("CPU Usage", cpuData, labels, colors, cpuProps);
                            PieChart2D cpuChart = new PieChart2D(cpuDataSet, new LegendProperties(), new ChartProperties(), _cpuHistory.getWidth(), _cpuHistory.getHeight());
                            cpuChart.setGraphics2D((java.awt.Graphics2D) _cpuHistory.getGraphics());
                            cpuChart.render();

                            PieChart2DProperties ioProps = new PieChart2DProperties();
                            ioProps.setPieLabelType(org.jCharts.types.PieLabelType.VALUE_LABELS);
                            ioProps.setBorderPaint(_ioHistory.getBackground());
                            PieChartDataSet ioDataSet = new PieChartDataSet("I/O Usage", ioData, labels, colors, ioProps);
                            PieChart2D ioChart = new PieChart2D(ioDataSet, new LegendProperties(), new ChartProperties(), _ioHistory.getWidth(), _ioHistory.getHeight());
                            ioChart.setGraphics2D((java.awt.Graphics2D) _ioHistory.getGraphics());
                            ioChart.render();
                        }
                        catch (ChartDataException ex) {
                            ex.printStackTrace();
                        }
                        catch (PropertyException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        catch (java.sql.SQLException ex) {
            ex.printStackTrace();
        }
        catch (DataSetException dse) {
            dse.printStackTrace();
        }
    }
    
    private JPanel buildMainPanel() {
        SessionProperties props = this.getSession().getProperties();
        
        JPanel panel = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        panel.setLayout(gridBag);
        
        //JPanel refreshPanel = new JPanel();
        //refreshPanel.setLayout(new FlowLayout());
        
        _frequency = new JSlider();
        _frequency.setMinimum(0);
        _frequency.setMaximum(20);
        _frequency.setValue(0);         // by default, it is not running.
        _frequency.setMajorTickSpacing(2);
        _frequency.setMinorTickSpacing(1);
        _frequency.setPaintLabels(true);
        _frequency.setPaintTicks(true);
        _frequency.setSnapToTicks(true);
        _frequency.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                if (slider.getValue() == 0) {
                    // don't bother changing the delay, but stop it if it's running.
                    if (_refreshTimer.isRunning())
                        _refreshTimer.stop();
                }
                else {
                    // change the delay, and start it if it was stopped.
                    _refreshTimer.setDelay(slider.getValue() * 1000);
                    if (!_refreshTimer.isRunning())
                        _refreshTimer.start();
                }
                slider.setToolTipText(new Integer(slider.getValue()).toString() + "s delay");
            }
        });
        addComponentToGridBag(0,0,1,1,0.0,0.0,GridBagConstraints.BOTH,gridBag,_frequency,panel);
        
        JButton refreshButton = new JButton("Refresh Now");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!_haveSession)
                    return;
                if (_inRefresh)
                    return;
                _inRefresh = true;
                refreshData();
                _inRefresh = false;
            }
        });
        addComponentToGridBag(GridBagConstraints.RELATIVE,0,1,1,0.0,0.0,GridBagConstraints.NONE,gridBag,refreshButton,panel);
       
        /*
        _cpuHistory = new JPanel();
        _cpuHistory.setBorder(BorderFactory.createTitledBorder("CPU Usage"));
        addComponentToGridBag(0,1,1,1,1.0,1.0,GridBagConstraints.BOTH,gridBag,_cpuHistory,panel);
        
        _ioHistory = new JPanel();
        _ioHistory.setBorder(BorderFactory.createTitledBorder("I/O Usage"));
        addComponentToGridBag(GridBagConstraints.RELATIVE,1,1,1,1.0,1.0,GridBagConstraints.BOTH,gridBag,_ioHistory,panel);
        */
        
        _whoViewer = BaseDataSetViewerDestination.getInstance(props.getReadOnlySQLResultsOutputClassName(), null);
        JScrollPane whoScroll = new JScrollPane(_whoViewer.getComponent());
        whoScroll.setBorder(BorderFactory.createTitledBorder("Current Activity"));
        addComponentToGridBag(0,1,1,1,1.0,1.0,GridBagConstraints.BOTH,gridBag,whoScroll,panel);
        
        _perfViewer = BaseDataSetViewerDestination.getInstance(props.getReadOnlySQLResultsOutputClassName(), null);
        JScrollPane perfScroll = new JScrollPane(_perfViewer.getComponent());
        perfScroll.setBorder(BorderFactory.createTitledBorder("Performance Counters"));
        addComponentToGridBag(GridBagConstraints.RELATIVE,1,1,1,1.0,1.0,GridBagConstraints.BOTH,gridBag,perfScroll,panel);
        
        return panel;
    }
    
    private void addComponentToGridBag(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int fill, GridBagLayout gridBag, java.awt.Component component, java.awt.Container container) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = gridwidth;
        c.gridheight = gridheight;
        c.fill = fill;
        c.weightx = weightx;
        c.weighty = weighty;
        gridBag.setConstraints(component,c);
        container.add(component);
    }
}
