package memory;

import schedule.ScheduleController;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// 采用数组实现内存的连接
public class Memory implements Memorys{
	//系统缓冲区个数
	public static final int ZONE_SIZE = 20;

	int maxSize = 400;
	public Zone zones[];
	int current = 0;
	private ScheduleController scheduleController;
	private GraphicsContext gc;
	private double layoutX,layoutY,cWidth;
	public Memory(ScheduleController scheduleController){
		this.scheduleController = scheduleController;
		gc = this.scheduleController.gc;
		layoutX = this.scheduleController.canvas.getLayoutX();
		layoutY = this.scheduleController.canvas.getLayoutY();
		cWidth = this.scheduleController.canvas.getWidth();
	}
	//初始化内存区域，把内存分成两个区域，操作系统内核区，和进程使用区
	@Override
	public void initMemory(){
		//一共有21个内存区域（包括系统内核区）
		zones = new Zone[ZONE_SIZE + 1];
		
		//操作系统运行区域
		zones[0] = new Zone(0, 30, Zone.STATE_USE, "system");
		gc.clearRect(layoutX, layoutY, cWidth, zones[0].getSize());
		gc.setFill(Color.RED);
		gc.fillRect(layoutX, layoutY, cWidth, 30);
		gc.setFill(Color.BLACK);
		gc.fillText(zones[0].getProName(), layoutX, layoutY+zones[0].getStart()+zones[0].getSize()/2);
		
		//进程区
		zones[1] = new Zone();
		zones[1].setStart(30);
		zones[1].setSize(maxSize-30);
		gc.clearRect(layoutX, layoutY + zones[1].getStart(), cWidth, zones[1].getSize());
		gc.setFill(Color.CADETBLUE);
		gc.fillRect(layoutX, layoutY + 30, cWidth, zones[1].getSize());
		gc.setFill(Color.BLACK);
		gc.fillText(zones[1].getProName(), layoutX, layoutY+zones[1].getStart()+zones[1].getSize()/2);
	}
	//为进程分配内存块（参数分别为size：进程所占内存的大小， proName：进程的名字），并且内存分配成功后的起始内存位置，-1：代表分配失败
	@Override
	public int allocateMemory(int size, String proName) {
		int start = -1;
		for(int i = 1;i < zones.length;i++){
			if(zones[i] != null && zones[i].isState() == false && zones[i].getSize() >= size){
				start = zones[i].getStart();
				break;
			}
		}
		return start;
	}
	
	@Override
	public void insertZone(int size, boolean state, String proName) {
		int start = allocateMemory(size,proName);

		for(int i = 1;i < zones.length;i++){
			//试着将第i块内存分配个指定进程
			if(zones[i].getStart() == start){
				int j = zones.length - 1;
				//寻找内存块尾端最后一个被分配的内存块
				while(zones[j]==null)j--;
				j++;
				//调整内存分配（因为有两种情况需要考虑   1：如果分配的时候直接分配在了内存块的末尾  2：分配的时候因为算法原因将中间某一块内存分配给了进程）
				for(;j>i;j--){
					//第二种情况，此时需要从分配出整体向后移动一次
					if(j-i!=1){
						zones[j] = new Zone();
						zones[j].setStart(zones[j-1].getStart());
						zones[j].setSize(zones[j-1].getSize());
						zones[j].setState(zones[j-1].isState());
						zones[j].setProName(zones[j-1].getProName());
					}else{//第一种情况
						zones[j] = new Zone();
						zones[j].setStart(zones[j-1].getStart() + size);
						zones[j].setSize(zones[j-1].getSize() - size);
						zones[j].setState(zones[j-1].isState());
						zones[j].setProName(zones[j-1].getProName());
					}
				}
				//调整完毕，分配内存
				zones[i].setStart(start);
				zones[i].setSize(size);
				zones[i].setState(state);
				zones[i].setProName(proName);
				// canvas 将新的进程图像化展示在内存区域
				gc.clearRect(layoutX, layoutY + zones[i].getStart(), cWidth, zones[i].getSize());
				gc.setFill(Color.AQUA);
				gc.fillRect(layoutX, layoutY + zones[i].getStart(), cWidth, zones[i].getSize());
				gc.setFill(Color.BLACK);
				gc.fillText(zones[i].getProName(), layoutX, layoutY+zones[i].getStart()+zones[i].getSize()/2);
				break;
			}
		}
	}
	@Override
	public void recycleMemory(String proName) {
		//获取进程的内存起始位置
		int address = getAddress(proName);
		if(address!=-1){
			for(int i = 1;i < zones.length;i++){
				//循环到回收进程的起始内存位置
				if(zones[i].getStart() == address){
					// 回收内存
					zones[i].setState(Zone.STATE_SPARE);
					zones[i].setProName(null);
					// canvas 更新内存动画
					gc.clearRect(layoutX, layoutY + zones[i].getStart(), cWidth, zones[i].getSize());
					gc.setFill(Color.CADETBLUE);
					gc.fillRect(layoutX, layoutY + zones[i].getStart(), cWidth, zones[i].getSize());
					// 合并相邻内存,向前合并
					/**
					 * 此处的处理方式非常奇妙！！！
					 * 在回收内存时，分别有三种情况：
					 * 1：当前内存相邻前后的内存块都是被占用的，此时调用merge()函数会返回false，那么在之后调用merge()函数的时候参数变成了当前内存之后的一块内存
					 * 2：当前内存只有前面一块是空的，此时顺利调用merge()，并且返回true，之后					
					 *  * */
					boolean isMerged = merge(i);
					
					if(isMerged){// 合并右侧
						if(zones[i].isState() == Zone.STATE_SPARE)merge(i);
					}
					
					else{
						if(zones[i + 1].isState() == Zone.STATE_SPARE) merge(i + 1);
					}
					
					break;
				}
			}
		}
	}
	//向前合并内存
	@Override
	public boolean merge(int p) {
		//指定内存块之前的内存块也是空闲的情况
		if(zones[p-1].isState() == Zone.STATE_SPARE){
			// 向前合并内存
			zones[p-1].setSize(zones[p-1].getSize() + zones[p].getSize());
			int i = p;
			//内存块整体向前移动
			while(zones[i+1]!=null){
				zones[i].setStart(zones[i+1].getStart());
				zones[i].setSize(zones[i+1].getSize());
				zones[i].setState(zones[i+1].isState());
				zones[i].setProName(zones[i+1].getProName());
				i++;
			}
			zones[i] = null;
			// canvas 更新内存区动画效果
			gc.clearRect(layoutX, layoutY + zones[p-1].getStart(), cWidth, zones[p-1].getSize());
			gc.setFill(Color.CHARTREUSE);
			gc.fillRect(layoutX, layoutY + zones[p-1].getStart(), cWidth, zones[p-1].getSize());
			gc.setFill(Color.BLACK);
			gc.fillText(zones[p-1].getProName(), layoutX, layoutY+zones[p-1].getStart()+zones[p-1].getSize()/2);
			return true;
		}
		return false;
	}
	//通过进程名字获取进程所在的内存起始位置， -1表示指定进程没有在内存
	private int getAddress(String proName){
		int start = -1;
		for(int i = 1;i < zones.length;i++){
			if(zones[i].getStart() + zones[i].getSize() == maxSize)break;
			if(zones[i].isState() == Zone.STATE_USE && zones[i].getProName().equals(proName)){
				start = zones[i].getStart();
				break;
			}
		}
		return start;
	}
	@Override
	public void print() {
		// TODO Auto-generated method stub
		int i = 0;
		while(zones[i++]!=null){
			zones[i-1].print();
		}
	}

}
