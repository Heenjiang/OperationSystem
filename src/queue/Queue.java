package queue;

import tableclass.TProcess;

public class Queue implements Queues{

	public Process first;           // ����,ָ�����root����
	public Process pointer;         // ָ�룬����ָ���������
	String queueType;               // �������ƣ�����"��������"
	int length;
	// ���й��캯��
	public Queue(String queueType){
		first = new Process();
		pointer = null;
		this.queueType = queueType;
		length = 0;
	}

	public void insertProcess(TProcess tProcess){
		insertProcess(tProcess.getProName().getValue(),
				tProcess.getProRuntime().getValue(),
				tProcess.getProPriority().getValue(),
				tProcess.getProStoreSize().getValue());
	}

	@Override
	public void insertProcess(String pid,String runtime,String priority,String storeSize) {

		Process newProcess;

		Process currentProcess;
		newProcess = new Process(pid,Integer.parseInt(runtime),
				Integer.parseInt(priority),Integer.parseInt(storeSize));

		if(length == 0){// ��һ���ڵ�Ϊ�գ�������Ԫ��

			first = newProcess;// �½ڵ㸴�Ƹ���һ���ڵ�
			first.setProPointed(new Process());// �����µ�β��
			length++;

		}else {

			currentProcess = first;

			while(!currentProcess.getProPointed().getProName().equals("last")){// �ƶ�����β

				currentProcess = currentProcess.getProPointed();
			}

			currentProcess.setProPointed(newProcess);
			currentProcess.getProPointed().setProPointed(new Process());
			length++;
		}
	}

	// �����ȼ��������,����
	@Override
	public void sort() {
		//����������Ƚ��ʺ�ѡ������
		String proName;
		int proRuntime;
		int proPriority;
		int proState;

		Process current;
		Process next;

		for(current = first; !current.getProName().equals("last");current = current.getProPointed()){
			proName = current.getProName();
			proRuntime = current.getProRuntime();
			proPriority = current.getProPriority();
			proState = current.getProState();
			pointer = current;
			for(next = current; !next.getProName().equals("last"); next = next.getProPointed()){
				if(proPriority > next.getProPriority()){
					proName = next.getProName();
					proRuntime = next.getProRuntime();
					proPriority = next.getProPriority();
					proState = next	.getProState();
					pointer = next;

				}
			}
			pointer.setProName(current.getProName());
			pointer.setProRuntime(current.getProRuntime());
			pointer.setProPriority(current.getProPriority());
			pointer.setProState(current.getProState());

			current.setProName(proName);
			current.setProRuntime(proRuntime);
			current.setProPriority(proPriority);
			current.setProState(proState);

		}
	}
	// �ж϶����Ƿ�Ϊ��
	@Override
	public boolean isEmpty() {

		return length() == 0;
	}

	// �����̽���ʱ���Ӿ����������Ƴ�
	// �򵱽���״̬��Ϊ�ȴ�ʱ����Ҫ�Ӿ����������Ƴ�
	// ���ر�ɾ����Ԫ��,���ܷ���null
	@Override
	public Process remove() {
		// TODO Auto-generated method stub
		Process process;
		process = first;
		// ����Ϊ��
		if(process.getProName().equals("last"))
			return null;
		// ����root�ڵ���ƣ�ɾ����һ������
		first = first.getProPointed();
		length--;
		return process;
	}

	@Override
	public int length() {

		return length;
	}

	@Override
	public Process remove(int index) {
		// TODO Auto-generated method stub
		if(index < 0 || index >= length){
			throw new ArrayIndexOutOfBoundsException(index);
		}
		Process process = null;
		Process current;
		int target = 0;
		for(current = first; !current.getProName().equals("last");current = current.getProPointed()){
			if(index == 0){
				first = first.getProPointed();
				break;
			}
			if(target == index-1){
				process = current.getProPointed();
				current.setProPointed(current.getProPointed().getProPointed());
				length--;
				return process;
			}
			target++;

		}
		length--;
		return current;
	}
	@Override
	public void print() {

		Process current;

		System.out.println(queueType + "��������");

		if(first != null){

			current = (Process) first.clone();

		}else {

			System.out.println("��ǰ�޽���");
			return;
		}

		while(current != null){
			System.out.println(current.toString());
			current = current.getProPointed();
		}
		System.out.println("���н����� = " + length());
	}
}
