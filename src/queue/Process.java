package queue;
public class Process implements Cloneable{
	// ���̵�״̬
	public static final int STATE_READY = 0;
	public static final int STATE_RUNNING = 1;
	public static final int STATE_WAITING = 2;
	public static final int STATE_TERMINAL = 3;
	public static final int STATE_SUSPAND = 4;

	// ����PCB�Ľṹ
	private String m_proName;
	private int m_proRuntime;
	private int m_proPriority;
	private int m_proState;
	private int m_proStoreSize;
	private int m_proStoreStart;
	private Process m_proPointed;

	public Process(){
		m_proName = "last";
	}
	// ���̹��췽��
	public Process(String m_proName, int m_proRuntime, int m_proPriority,
			int m_proStoreSize) {
		this.m_proName = m_proName;
		this.m_proRuntime = m_proRuntime;
		this.m_proPriority = m_proPriority;
		this.m_proStoreSize = m_proStoreSize;
		// ��ʼ��Ϊ�߼���ַ
		this.m_proStoreStart = 0;
		this.m_proState = 0;
		this.m_proPointed = null;
	}
	// �õ���������/PID
	public String getProName(){
		return m_proName;
	}
	// ���ý�������
	public void setProName(String m_proName){
		this.m_proName = m_proName;
	}
	// ���ý�������ʱ��
	public void setProRuntime(int m_proRuntime){
		this.m_proRuntime = m_proRuntime;
	}
	// �õ���������ʱ��
	public int getProRuntime(){
		return m_proRuntime;
	}
	// ���ý������ȼ�
	public void setProPriority(int m_proPriority){
		this.m_proPriority = m_proPriority;
	}
	// �õ��������ȼ�
	public int getProPriority(){
		return m_proPriority;
	}
	// ���ý���״̬
	public void setProState(int m_proState){
		this.m_proState = m_proState;
	}
	// �õ����������С
	public int getProStoreSize() {
		return m_proStoreSize;
	}
	// �������������С
	public void setProStoreSize(int m_proStoreSize) {
		this.m_proStoreSize = m_proStoreSize;
	}
	// �õ����濪ʼλ��
	public int getProStoreStart() {
		return m_proStoreStart;
	}
	// �������濪ʼλ��
	public void setProStoreStart(int m_proStoreStart) {
		this.m_proStoreStart = m_proStoreStart;
	}
	// �õ�����״̬
	public int getProState(){
		return m_proState;
	}
	// ���ö����н�����ָ�Ľ���
	public void setProPointed(Process m_proPointed){
		this.m_proPointed = m_proPointed;
	}
	// �õ�����ָ����ָ�Ľ���
	public Process getProPointed(){
		return m_proPointed;
	}

	// �����ǰ������Ϣ
	@Override
	public String toString(){
		String proInfo = "";
		proInfo += "��������/UID = "+getProName() + ", ʣ������ʱ��  = " + getProRuntime();
		proInfo += ", ���ȼ� = " + getProPriority() + ", ״̬ = " + formatState(getProState());
		proInfo += ", ��һ������ ";

		if(getProPointed() == null)
			proInfo += "����һ������";
		else
			proInfo += getProPointed().getProName();

		return proInfo;
	}
	// ��intֵ�����Ӧ���ַ���
	public static String formatState(int proState) {
		// TODO Auto-generated method stub
		String state = "";
		switch (proState) {
		case STATE_READY:
			state = "����";
			break;
		case STATE_RUNNING:
			state = "����";
			break;
		case STATE_WAITING:
			state = "�ȴ�";
			break;
		case STATE_SUSPAND:
			state = "����";
			break;
		case STATE_TERMINAL:
			state = "��ֹ";
			break;
		default:
			break;
		}
		return state;
	}

	// ������ֻ��һ������ʱ�������´����ֻ������
	// ��������������������ʱ�����һ�����̲����´������������
	@Override
	public Object clone(){

		Process process = null;

		// ���������ֻ��һ��Ԫ�ص����,ֱ������
		if(this.getProPointed() == null){

			process = this;

			return process;


		}
		// ����������������
		try {

			process = (Process) super.clone();

		} catch (CloneNotSupportedException  e) {

			e.printStackTrace();

		}

		// ������Ҫ����Processָ��
		if(this.getProPointed() != null && this.getProPointed().getProPointed() ==null);    // ����㿽��(this�ǵ�ǰ���е����ڶ�����)

		else{

			if(this.getProPointed()!=null)// ���ж�������ʾ,��ǰ�ݹ鿽�������е����ڶ���Ԫ��ʱ��Processָ�벻ִ�п���

				process.m_proPointed = (Process) this.getProPointed().clone();

		}
		return process;

	}
}