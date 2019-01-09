package cpu;
import queue.Process;
import schedule.ScheduleController;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
public class CPU {
	public final int CPU_STATE_SPARE = 0;  // cpu����
	public final int CPU_STATE_WORK = 1;   // cpu����

	public String cpuName;                     // cpu����
	public Process process1;                   // cpu��ǰ����Ľ���1
	public Process process2;                   // cpu��ǰ����Ľ���2
	public int tracks;                         // ����CPU�ĵ���
	public int state;                          // cpu��ǰ״̬
	public ScheduleController scheduleController;

	public CPU(String cpuName,ScheduleController scheduleController){
		this.cpuName = cpuName;
		process1 = null;
		process2 = null;
		state = CPU_STATE_SPARE;
		this.scheduleController = scheduleController;
	}
	// ����CPU����Ľ���
	public void setProcess(Process process){
		int which;
		System.out.println(getCpuName());
		if(process1 == null){
			process1 = process;
			// ���´�cpu��״̬
			updateState();
			// ȷ�����Ǹ�����ִ��
			if(getCpuName().equals("cpu1")){
				which = 0;
			}else{
				which = 2;
			}
		}else{
			process2 = process;
			updateState();
			if(getCpuName().equals("cpu1")){
				which = 1;
			}else{
				which = 3;
			}
		}
		Thread thread = new Thread(new Runnable() {
			final int whichPB = which;
			@Override
			public void run() {
				animation(process,whichPB);
			}
		});

		thread.start();

	}
	//����cpu�Ĺ���״̬
	private void updateState() {
		// ���ݵ�ǰcpu�е����н�������ȷ��cpu�Ĺ���״̬
		if(process1 != null && process2 != null)
			state = CPU_STATE_WORK;
		else
			state = CPU_STATE_SPARE;
		updateUI(state);
		System.out.println(getCpuName() + "״̬���³ɹ�" + state);
	}
	//����cpuͼ��
	private void updateUI(int state){
		//����������ʾcpu����״̬��ͼ��
		ImageView imageview;
		//����cpu����������ȡ��Ӧcpu����״̬��ͼ��
		if(getCpuName().equals("cpu1"))
			imageview = scheduleController.iv_cpu1;
		else
			imageview = scheduleController.iv_cpu2;

		switch (state) {
		//�����ǰcpu���������̣�������Ϊ���ڹ�������������Ϊ����
		case CPU_STATE_SPARE:
			//����cpu��ͬ����״̬�µ�ͼ��
			imageview.setImage(new Image(ScheduleController.image_url_1));
			break;
		case CPU_STATE_WORK:
			imageview.setImage(new Image(ScheduleController.image_url_2));
			break;
		default:
			break;
		}
	}
	public String getCpuName() {
		return cpuName;
	}
	//���������������ĺ���
	protected void animation(Process process,int which) {
		switch (which) {
		case 0:
			animation(scheduleController.pb_cpu1_1, scheduleController.lb_cpu1_1, process, 0);
			break;
		case 1:
			animation(scheduleController.pb_cpu1_2, scheduleController.lb_cpu1_2, process, 1);
			break;
		case 2:
			animation(scheduleController.pb_cpu2_1, scheduleController.lb_cpu2_1, process, 2);
			break;
		case 3:
			animation(scheduleController.pb_cpu2_2, scheduleController.lb_cpu2_2, process, 3);
			break;
		default:
			break;
		}
	}

	public void animation(ProgressBar pb,Label lb,Process process,int which){
		// ���̿�ʼִ��ǰ,���ý���ִ�еĶ���Ч��ʱ��
		final int totalTime = process.getProRuntime();
		// ��������ռ�������������ڿ��������ʵ��
		// �����ͼ

		// ���̿�ʼִ�ж���Ч��
		 Animation animation = new Transition() {
		     {
		    	 //���ö�������ʱ��
		         setCycleDuration(Duration.millis(totalTime*1000));
		     }
		     //��Transitionִ��ʱ��interpolate()Ҳ��ִ�У�called in every frame����frac�ǵ�ǰ�����˶���λ�ò�������Χ0~1.0��
		     @Override
			protected void interpolate(double frac) {
		         final int n = Math.round(totalTime * (float) frac);
		         //���õ�ǰ����ʣ������ʱ��
		         lb.setText(""+Math.round(totalTime * (float)(1- frac)));
		         pb.setProgress(frac);
		         //�������߳���ɺ���Ӧ���̻��գ�
		         if(frac == 1.0){
		        	 //����labelʱ��Ϊ0.0
		        	 lb.setText("0");
		        	 //����progressBar�ص�0%
		        	 pb.setProgress(0F);
		        	// ������ɣ��������棬��������ϲ�
					// ����UIˢ��
					scheduleController.table.recycleMemory(process.getProName());
					//���ݲ���which���жϹ�����cpu����һ��
					if(which % 2 == 0)
						process1 = null;
					else
						process2 = null;
					updateState();
		         }
		     }

		 };
		 //������ʼ
		 animation.play();
	}
}