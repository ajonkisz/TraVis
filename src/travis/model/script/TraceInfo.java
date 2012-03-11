package travis.model.script;

public class TraceInfo {

	private final int methodId;
	private final boolean returnCall;
	private final long callTime;
	private final long threadId;

	public TraceInfo(int methodId, boolean returnCall, long callTime,
			long threadId) {
		this.methodId = methodId;
		this.returnCall = returnCall;
		this.callTime = callTime;
		this.threadId = threadId;
	}

	public int getMethodId() {
		return methodId;
	}

	public boolean isReturnCall() {
		return returnCall;
	}

	public long getCallTime() {
		return callTime;
	}

	public long getThreadId() {
		return threadId;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(returnCall ? "<-- " : "--> ");
		sb.append(methodId);
		sb.append(' ');
		sb.append(callTime);
		sb.append(' ');
		sb.append(threadId);
		return sb.toString();
	}
}
