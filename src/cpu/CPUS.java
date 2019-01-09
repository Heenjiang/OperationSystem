package cpu;

import queue.Process;
import schedule.ScheduleController;

public class CPUS {

	public final int CPU_STATE_SPARE = 0;  // cpu����
	public final int CPU_STATE_WORK = 1;   // cpu����

	public int cpuCount;                          // cpu��Ŀ
	public CPU[] cpus;                            // ����ÿ��CPU
	private ScheduleController scheduleController;

	public CPUS(int cpuCount,ScheduleController scheduleController){

		this.cpuCount = cpuCount;

		cpus = new CPU[cpuCount];

		// ��ʼ������cpu
		for(int i = 0;i < cpus.length;i++){

			cpus[i] = new CPU("cpu"+(i+1),scheduleController);

		}
		this.scheduleController = scheduleController;
	}

	public void setCPUCount(int cpuCount){
		this.cpuCount = cpuCount;

		cpus = new CPU[cpuCount];

		// ��ʼ������cpu
		for(int i = 0;i < cpus.length;i++){

			cpus[i] = new CPU("cpu"+(i+1),scheduleController);

		}
	}
	// ��ӡ����cpu����Ϣ
	public void print(){

		for(int i = 0;i < cpus.length;i++){

			System.out.println(cpus[i].toString());

		}
	}
	// ����cpu�ĸ���
	public int count(){

		return cpuCount;
	}
	// �ж��Ƿ��п��е�cpu
	public boolean isSpare(){

		for(int i = 0;i < count();i++){

			if(cpus[i].state == CPU_STATE_SPARE)

				return true;
		}

		return false;
	}

	public void work(Process process) {
		//�����̷�����д��ڿ���״̬��cpu
		for(int i = 0;i < count();i++){

			if(cpus[i].state == CPU_STATE_SPARE){
				cpus[i].setProcess(process);
				break;

			}
		}
	}
}