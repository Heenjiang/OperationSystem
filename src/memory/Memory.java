package memory;

import schedule.ScheduleController;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// ��������ʵ���ڴ������
public class Memory implements Memorys{
	//ϵͳ����������
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
	//��ʼ���ڴ����򣬰��ڴ�ֳ��������򣬲���ϵͳ�ں������ͽ���ʹ����
	@Override
	public void initMemory(){
		//һ����21���ڴ����򣨰���ϵͳ�ں�����
		zones = new Zone[ZONE_SIZE + 1];
		
		//����ϵͳ��������
		zones[0] = new Zone(0, 30, Zone.STATE_USE, "system");
		gc.clearRect(layoutX, layoutY, cWidth, zones[0].getSize());
		gc.setFill(Color.RED);
		gc.fillRect(layoutX, layoutY, cWidth, 30);
		gc.setFill(Color.BLACK);
		gc.fillText(zones[0].getProName(), layoutX, layoutY+zones[0].getStart()+zones[0].getSize()/2);
		
		//������
		zones[1] = new Zone();
		zones[1].setStart(30);
		zones[1].setSize(maxSize-30);
		gc.clearRect(layoutX, layoutY + zones[1].getStart(), cWidth, zones[1].getSize());
		gc.setFill(Color.CADETBLUE);
		gc.fillRect(layoutX, layoutY + 30, cWidth, zones[1].getSize());
		gc.setFill(Color.BLACK);
		gc.fillText(zones[1].getProName(), layoutX, layoutY+zones[1].getStart()+zones[1].getSize()/2);
	}
	//Ϊ���̷����ڴ�飨�����ֱ�Ϊsize��������ռ�ڴ�Ĵ�С�� proName�����̵����֣��������ڴ����ɹ������ʼ�ڴ�λ�ã�-1���������ʧ��
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
			//���Ž���i���ڴ�����ָ������
			if(zones[i].getStart() == start){
				int j = zones.length - 1;
				//Ѱ���ڴ��β�����һ����������ڴ��
				while(zones[j]==null)j--;
				j++;
				//�����ڴ���䣨��Ϊ�����������Ҫ����   1����������ʱ��ֱ�ӷ��������ڴ���ĩβ  2�������ʱ����Ϊ�㷨ԭ���м�ĳһ���ڴ������˽��̣�
				for(;j>i;j--){
					//�ڶ����������ʱ��Ҫ�ӷ������������ƶ�һ��
					if(j-i!=1){
						zones[j] = new Zone();
						zones[j].setStart(zones[j-1].getStart());
						zones[j].setSize(zones[j-1].getSize());
						zones[j].setState(zones[j-1].isState());
						zones[j].setProName(zones[j-1].getProName());
					}else{//��һ�����
						zones[j] = new Zone();
						zones[j].setStart(zones[j-1].getStart() + size);
						zones[j].setSize(zones[j-1].getSize() - size);
						zones[j].setState(zones[j-1].isState());
						zones[j].setProName(zones[j-1].getProName());
					}
				}
				//������ϣ������ڴ�
				zones[i].setStart(start);
				zones[i].setSize(size);
				zones[i].setState(state);
				zones[i].setProName(proName);
				// canvas ���µĽ���ͼ��չʾ���ڴ�����
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
		//��ȡ���̵��ڴ���ʼλ��
		int address = getAddress(proName);
		if(address!=-1){
			for(int i = 1;i < zones.length;i++){
				//ѭ�������ս��̵���ʼ�ڴ�λ��
				if(zones[i].getStart() == address){
					// �����ڴ�
					zones[i].setState(Zone.STATE_SPARE);
					zones[i].setProName(null);
					// canvas �����ڴ涯��
					gc.clearRect(layoutX, layoutY + zones[i].getStart(), cWidth, zones[i].getSize());
					gc.setFill(Color.CADETBLUE);
					gc.fillRect(layoutX, layoutY + zones[i].getStart(), cWidth, zones[i].getSize());
					// �ϲ������ڴ�,��ǰ�ϲ�
					/**
					 * �˴��Ĵ���ʽ�ǳ��������
					 * �ڻ����ڴ�ʱ���ֱ������������
					 * 1����ǰ�ڴ�����ǰ����ڴ�鶼�Ǳ�ռ�õģ���ʱ����merge()�����᷵��false����ô��֮�����merge()������ʱ���������˵�ǰ�ڴ�֮���һ���ڴ�
					 * 2����ǰ�ڴ�ֻ��ǰ��һ���ǿյģ���ʱ˳������merge()�����ҷ���true��֮��					
					 *  * */
					boolean isMerged = merge(i);
					
					if(isMerged){// �ϲ��Ҳ�
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
	//��ǰ�ϲ��ڴ�
	@Override
	public boolean merge(int p) {
		//ָ���ڴ��֮ǰ���ڴ��Ҳ�ǿ��е����
		if(zones[p-1].isState() == Zone.STATE_SPARE){
			// ��ǰ�ϲ��ڴ�
			zones[p-1].setSize(zones[p-1].getSize() + zones[p].getSize());
			int i = p;
			//�ڴ��������ǰ�ƶ�
			while(zones[i+1]!=null){
				zones[i].setStart(zones[i+1].getStart());
				zones[i].setSize(zones[i+1].getSize());
				zones[i].setState(zones[i+1].isState());
				zones[i].setProName(zones[i+1].getProName());
				i++;
			}
			zones[i] = null;
			// canvas �����ڴ�������Ч��
			gc.clearRect(layoutX, layoutY + zones[p-1].getStart(), cWidth, zones[p-1].getSize());
			gc.setFill(Color.CHARTREUSE);
			gc.fillRect(layoutX, layoutY + zones[p-1].getStart(), cWidth, zones[p-1].getSize());
			gc.setFill(Color.BLACK);
			gc.fillText(zones[p-1].getProName(), layoutX, layoutY+zones[p-1].getStart()+zones[p-1].getSize()/2);
			return true;
		}
		return false;
	}
	//ͨ���������ֻ�ȡ�������ڵ��ڴ���ʼλ�ã� -1��ʾָ������û�����ڴ�
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
