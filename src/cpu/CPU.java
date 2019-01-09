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
	public final int CPU_STATE_SPARE = 0;  // cpu空闲
	public final int CPU_STATE_WORK = 1;   // cpu工作

	public String cpuName;                     // cpu名称
	public Process process1;                   // cpu当前处理的进程1
	public Process process2;                   // cpu当前处理的进程2
	public int tracks;                         // 单个CPU的道数
	public int state;                          // cpu当前状态
	public ScheduleController scheduleController;

	public CPU(String cpuName,ScheduleController scheduleController){
		this.cpuName = cpuName;
		process1 = null;
		process2 = null;
		state = CPU_STATE_SPARE;
		this.scheduleController = scheduleController;
	}
	// 设置CPU处理的进程
	public void setProcess(Process process){
		int which;
		System.out.println(getCpuName());
		if(process1 == null){
			process1 = process;
			// 更新此cpu的状态
			updateState();
			// 确定在那个道上执行
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
	//更新cpu的工作状态
	private void updateState() {
		// 根据当前cpu中的运行进程数来确定cpu的工作状态
		if(process1 != null && process2 != null)
			state = CPU_STATE_WORK;
		else
			state = CPU_STATE_SPARE;
		updateUI(state);
		System.out.println(getCpuName() + "状态更新成功" + state);
	}
	//更新cpu图标
	private void updateUI(int state){
		//主界面中显示cpu工作状态的图标
		ImageView imageview;
		//根据cpu的名字来获取对应cpu工作状态的图标
		if(getCpuName().equals("cpu1"))
			imageview = scheduleController.iv_cpu1;
		else
			imageview = scheduleController.iv_cpu2;

		switch (state) {
		//如果当前cpu有两个进程，则设置为正在工作，否则设置为空闲
		case CPU_STATE_SPARE:
			//设置cpu不同工作状态下的图标
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
	//产生进度条动画的函数
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
		// 进程开始执行前,设置进程执行的动画效果时间
		final int totalTime = process.getProRuntime();
		// 分配主存空间操作可在主窗口控制器里边实现
		// 界面绘图

		// 进程开始执行动画效果
		 Animation animation = new Transition() {
		     {
		    	 //设置动画持续时间
		         setCycleDuration(Duration.millis(totalTime*1000));
		     }
		     //当Transition执行时，interpolate()也会执行（called in every frame），frac是当前动画运动的位置参数（范围0~1.0）
		     @Override
			protected void interpolate(double frac) {
		         final int n = Math.round(totalTime * (float) frac);
		         //设置当前进程剩余运行时间
		         lb.setText(""+Math.round(totalTime * (float)(1- frac)));
		         pb.setProgress(frac);
		         //当动画线程完成后相应进程回收，
		         if(frac == 1.0){
		        	 //设置label时间为0.0
		        	 lb.setText("0");
		        	 //设置progressBar回到0%
		        	 pb.setProgress(0F);
		        	// 进程完成，回收主存，相邻区间合并
					// 界面UI刷新
					scheduleController.table.recycleMemory(process.getProName());
					//根据参数which来判断工作的cpu是哪一个
					if(which % 2 == 0)
						process1 = null;
					else
						process2 = null;
					updateState();
		         }
		     }

		 };
		 //动画开始
		 animation.play();
	}
}