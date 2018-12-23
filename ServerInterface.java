package ee402;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;


@SuppressWarnings("serial")
public class ServerInterface extends JFrame implements ActionListener, AdjustmentListener, WindowListener, MouseListener {
	
	//Lists that will store the data
	private PacketList[] allPacketsList = null;
	private PointList[] pointList = null;
	
	//Infos for the graph
	private Stroke stroke = new BasicStroke(2f);
	private int HORIZONTAL_GAP = 120, SOUTH_GAP = 550, NORTH_GAP = 50, GRAPH_LENGTH = 800;//
	private int AXIS_X_GAPS = 80, AXIS_Y_GAPS = 25;
	private int MAX_READINGS = 10;
	
	//Swing components
	private JPanel upperPanel, mainPanel;
	private JScrollPane sp = null;
	private JScrollBar sb = null;
	private JButton add = null, clear = null, switchData = null;
	private JCheckBox User0, User1, User2, User3, User4;
	private JComboBox<String> combo = null;
	
	//Needed variables to switch between CPU Usage, Temperature and random generated points
	private boolean isRealData = false, isReadingCPU = true;
	
	public ServerInterface(){
		
		super("Server Interface");
		
		//We can only fill 5 arrays of data (for 5 clients)
		this.allPacketsList = new PacketList[]{new PacketList(), new PacketList(), new PacketList(), new PacketList(), new PacketList()};
		this.pointList = new PointList[]{new PointList(), new PointList(), new PointList(), new PointList(), new PointList()};
		
		this.addWindowListener(this);
		this.upperPanel = new JPanel();
		this.mainPanel = new JPanel(){
			//Overriding the paint function of this panel (which contains the graph)
			protected synchronized void paintComponent(Graphics g){
				super.paintComponent(g);
				drawGraph(g);
			}
		};
		this.sp = new JScrollPane(this.mainPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.sb = new JScrollBar(JScrollBar.HORIZONTAL);
		this.sp.setHorizontalScrollBar(sb);
		this.sb.setUnitIncrement(12);
		this.User0 = new JCheckBox("User 0", true); //This box will be selected when the interface is launched
		this.User1 = new JCheckBox("User 1");
		this.User2 = new JCheckBox("User 2");
		this.User3 = new JCheckBox("User 3");
		this.User4 = new JCheckBox("User 4");
		this.add = new JButton("Add Point");
		this.clear = new JButton("Clear");
		this.switchData = new JButton("CPU Usage");
		String values[] = {"5","10","15","20"};
		this.combo = new JComboBox<String>(values);
		this.combo.setSelectedIndex(1);
		
		this.setPreferredSize(new Dimension(this.GRAPH_LENGTH + this.HORIZONTAL_GAP + 50,700));
		
		this.mainPanel.setPreferredSize(new Dimension(1900,600));
		this.mainPanel.setBackground(Color.LIGHT_GRAY);
		
		this.mainPanel.addMouseListener(this);
		this.sb.addAdjustmentListener(this);
		this.add.addActionListener(this);
		this.clear.addActionListener(this);
		this.switchData.addActionListener(this);
		this.combo.addActionListener(this);
	
		
		this.upperPanel.add(this.switchData);
		this.upperPanel.add(this.User0);
		this.upperPanel.add(this.User1);
		this.upperPanel.add(this.User2);
		this.upperPanel.add(this.User3);
		this.upperPanel.add(this.User4);
		this.upperPanel.add(this.add);
		this.upperPanel.add(this.clear);
		this.upperPanel.add(new JLabel("Samples"));
		this.upperPanel.add(this.combo);
		this.getContentPane().add("North", this.upperPanel);
		this.getContentPane().add("Center", this.sp);
		
		this.pack();
		this.setVisible(true);
		
	}
	
	
	public void actionPerformed(ActionEvent e) {
		//Switch between reading CPU Usage and reading Temperature (will force a clear of everything)
		if(e.getSource().equals(this.switchData)){
			if(this.isReadingCPU==true){
				this.isReadingCPU= false;
				this.switchData.setText("Temperature");
			}else {
				this.isReadingCPU= true;
				this.switchData.setText("CPU Usage");
			}
			clearAll();
		}
		
		//Change both the length of the graph and the number of readings from the combo box
		if(e.getSource().equals(this.combo)){
			this.MAX_READINGS = Integer.parseInt(this.combo.getSelectedItem().toString());
			setGraphLength(this.MAX_READINGS);
			//We exclude the case when the max readings is 5 because the frame would be too small
			if(this.MAX_READINGS != 5){
				this.setSize(new Dimension(this.GRAPH_LENGTH + this.HORIZONTAL_GAP + 50,700));
			}else this.setSize(new Dimension(760,700));
      	  this.repaint();
        }
		//Can't create a new point if an online user is using the color (checked with isReal())
		if(e.getSource().equals(this.add)){
			if(this.User0.isSelected() && !isReal(0))manuallyAddPoints(0);
        	if(this.User1.isSelected() && !isReal(1))manuallyAddPoints(1);
        	if(this.User2.isSelected() && !isReal(2))manuallyAddPoints(2);
        	if(this.User3.isSelected() && !isReal(3))manuallyAddPoints(3);
        	if(this.User4.isSelected() && !isReal(4))manuallyAddPoints(4);
		}
		//Clears the selected users
        if (e.getSource().equals(this.clear))clearSelected();
        
	}
	
	//This function will get called every time an handler receives a packet
	//It will update the packet list and the points list
	public synchronized void updateLists(Packet packet, int row){
		
		//If we already allocated the space we're trying to reach to an Offline user, then override it
		if(this.allPacketsList[row].isEmpty() && !this.pointList[row].isEmpty())this.pointList[row].clear();
		
		this.allPacketsList[row].add(packet);	
		this.updatePoints(packet, row);
		this.repaint();
		
	}
	
	//This function will fill the array of points
	//with directly the position in pixels in the graph and not the raw CPU Usage
	public synchronized void updatePoints(Packet packet, int row){
		
		//Because the axis starts at SOUTH_GAP and its length is SOUTH_GAP - NORTH_GAP
		System.out.println("CPU Usage : " + packet.getCpuUsage());
		System.out.println("Temperature : " + packet.getTemperature());
		float y= 0;
		int data = 0;
		if(this.isReadingCPU==true){
			data = packet.getCpuUsage();
		}else data = packet.getTemperature();
			
		//Assuming the temperature doesn't go beyond 100
		y = this.SOUTH_GAP - ((0.01f)*(this.SOUTH_GAP - this.NORTH_GAP)*data);

		System.out.println(y);
		this.pointList[row].add((int)y);
		
	}
	
	//This function will draw the entire graph
	public void drawGraph(Graphics g2){
		
		Graphics2D g = (Graphics2D) g2;
		drawAxis(g);
		g.setStroke(stroke);
		int COUNTER = 0;
		//For every Client we have (limited to 5), we redraw its entire set of points
		for(int i=0; i<5; i++){
			
			//Different color for each Client
			if (i==0) g.setColor(Color.BLACK);
			if (i==1) g.setColor(Color.BLUE);
			if (i==2) g.setColor(Color.RED);
			if (i==3) g.setColor(new Color(100,0,100)); //Purple
			if (i==4) g.setColor(new Color(165,42,42)); //Brown
			
			//Only draw if we have at least 2 points
			if(this.pointList[i].getSize() > 1){
				//Check if it's real data
				this.isRealData = isReal(i);
				//This is to limit the number of points we draw (to MAX_READINGS)
				if(this.pointList[i].getSize()-1 > this.MAX_READINGS){	
					COUNTER = this.pointList[i].getSize() - 1 - this.MAX_READINGS;
				}else COUNTER = 0;
				
				    //Now the loop that will draw every line
					for(int j = COUNTER; j < this.pointList[i].getSize() - 1; j++)	{
							int x1=0, x2=0, y1=0, y2=0;
							//We read the array of array of the points generated that represent CPU Usage
							x2= this.pointList[i].get(j);
							y2= this.pointList[i].get(j+1);
							//This is to simulate a scrolling when there are more points than our limit (MAX_READINGS)
							x1 = this.AXIS_X_GAPS*(j) - this.AXIS_X_GAPS*COUNTER + this.HORIZONTAL_GAP + 1;
							y1 = this.AXIS_X_GAPS*(j+1) - this.AXIS_X_GAPS*COUNTER + this.HORIZONTAL_GAP + 1;
							//Then we draw the line between the two points
							g.drawLine(x1, x2, y1, y2);		
							//Drawing more infos in the graph
							drawInfos(g, i, j, y1, y2);
					}
			}
		}
		
	}	
	
	//Check if the data is real (not generated by the buttons)
	public boolean isReal(int i){
		if((this.allPacketsList[i].getSize() == this.pointList[i].getSize()) && (!this.allPacketsList[i].isEmpty())){
			return true;
		}else return false;
	}
	
	//Clear everything
	public void clearAll(){
		for(int i=0; i<this.allPacketsList.length; i++)this.allPacketsList[i].clear();
		for(int i=0; i<this.pointList.length; i++)this.pointList[i].clear();
		this.repaint();
	}
	
	//This function will clear an user (manually called or when the clear button is pressed)
	public void clearUser(int i){
		this.allPacketsList[i].clear();
		this.pointList[i].clear();
		this.repaint();
	}
	
	//This will draw all kind of infos
	public void drawInfos(Graphics2D g, int i, int j, int xPos, int yPos){
		//Setting the x position
		float x = this.HORIZONTAL_GAP - (0.9f)*this.HORIZONTAL_GAP;
		//Setting the y position depending on which user it is
		int y = this.NORTH_GAP + 100*i + 10;
		drawName(g, i, j, x, y);
		//If the data is real, we can draw more information
		if(this.isRealData){
		drawAverage(g, i, x, y+15);
		drawMin(g, i, x, y+30);
		drawMax(g, i, x, y+45);
		drawTime(g, xPos, yPos, i, j);
		}		
	}
	
	//This will draw the average CPU Usage 
	public void drawAverage(Graphics2D g, int i, float x, int y){
		float f = this.allPacketsList[i].getAverage(this.isReadingCPU);
		g.drawString("Average : " + f, x, y);
	}
	//This will draw the minimum CPU Usage 
	public void drawMin(Graphics2D g, int i, float x, int y){
		int f = this.allPacketsList[i].getMin(this.isReadingCPU);
		g.drawString("Minimum : " + f, x, y);
	}
	//This will draw the maximum CPU Usage 
	public void drawMax(Graphics2D g, int i, float x, int y){
		int f = this.allPacketsList[i].getMax(this.isReadingCPU);
		g.drawString("Maximum : " + f, x, y);
	}
	
	//This function will draw the name of an user
	public void drawName(Graphics2D g, int i, int j, float x, int y){

		String name = null;
		if(this.isRealData){
			name = this.allPacketsList[i].get(j).getHostName();
		}else{
			name = "Offline user " + i;
		}
		g.drawString(name, x, y);
	}
	
	//This function will display the time of the point getting drawn
	public void drawTime(Graphics2D g, int x, int y, int i, int j){
		//So the text is well aligned
		x-=25;
		y-=10;
		//We only want to display the time (not day/month etc) for practical reasons
		String croppedTime = null, s[] = null;
		s = this.allPacketsList[i].get(j).getTimeDate().split(" ");
		croppedTime = s[3];
		g.drawString(croppedTime, x, y);
		
	}
	
	//This function will draw the axis for the graph
	public void drawAxis(Graphics2D g){
		g.setStroke(new BasicStroke(1f));
		//Y axis then X axis
		g.drawLine(this.HORIZONTAL_GAP,this.NORTH_GAP,this.HORIZONTAL_GAP,this.SOUTH_GAP);
		g.drawLine(this.HORIZONTAL_GAP,this.SOUTH_GAP,this.GRAPH_LENGTH + this.HORIZONTAL_GAP,this.SOUTH_GAP);
		
		int i= this.AXIS_Y_GAPS;
		
		//Marks on the Y axis
		int label = 5;
		while(this.SOUTH_GAP - i >= this.NORTH_GAP){
			g.drawLine(this.HORIZONTAL_GAP, this.SOUTH_GAP-i, this.HORIZONTAL_GAP+10, this.SOUTH_GAP-i);
			g.drawString(Integer.toString(label), this.HORIZONTAL_GAP - 15, this.SOUTH_GAP - i );
			i+=this.AXIS_Y_GAPS;
			label+=5;
		}
		//Marks on the X axis
		int j=this.AXIS_X_GAPS;
		while(j <= this.GRAPH_LENGTH){
			g.drawLine(this.HORIZONTAL_GAP + j, this.SOUTH_GAP, this.HORIZONTAL_GAP + j, this.SOUTH_GAP - 10);
			j+=this.AXIS_X_GAPS;
		}
	}
	
	//Will clear users depending on the selected boxes
	public void clearSelected(){
		if(this.User0.isSelected()){
    		clearUser(0);
    		this.User0.setSelected(false);
    	}
    	if(this.User1.isSelected()){
    		clearUser(1);
    		this.User1.setSelected(false);
    	}
    	if(this.User2.isSelected()){
    		clearUser(2);
    		this.User2.setSelected(false);
    	}
    	if(this.User3.isSelected()){
    		clearUser(3);
    		this.User3.setSelected(false);
    	}
    	if(this.User4.isSelected()){
    		clearUser(4);
    		this.User4.setSelected(false);
    	}
	}
	
	//Add a random point
	public void manuallyAddPoints(int i){
			int y;
			Random rand = new Random();
			y= rand.nextInt(500) + 50;

			this.pointList[i].add(y);
			this.repaint();
	}
	//Setting the length of the X axis
	public void setGraphLength(int i){
		this.GRAPH_LENGTH = i*80;
	}
	
	
	public void windowOpened(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {
		System.out.println("Server Closing ...");
		System.exit(0);
	}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void adjustmentValueChanged(AdjustmentEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
}