package queue;
public interface Queues {

	public 	void insertProcess(String pid, String runtime, String priority, String storeSize);
	                                     // �����½��̵��������λ��

	public void sort();                  // �����ȼ�����

	public boolean isEmpty();            // �ж������Ƿ�Ϊ��

	public Process remove();             // ɾ��

	public Process remove(int index);    // ɾ��ָ���±�Ľ���

//	public Process find(String proName); // ����ָ�����ƵĽ���

	public int length();                 // ����ĳ���

	void print();


//	public void RR(int timeslice);       // ʱ��Ƭ��ת����

//	public void priority();              // ����Ȩ����

}
