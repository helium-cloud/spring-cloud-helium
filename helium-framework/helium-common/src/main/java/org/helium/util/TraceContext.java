package org.helium.util;

import org.helium.superpojo.type.Guid;

public class TraceContext {

	public static final int EXTENSION_CONTEXT_TRACECTOKEN = 13;
	//public static LogContext Current;
	protected static ThreadLocal<TraceContext> local = new ThreadLocal<TraceContext>();

	public static TraceContext GetContext() {
		TraceContext context = local.get();
		return context;
	}

	public static void SetContext(TraceContext value) {
		local.set(value);
	}

	public static void Allocate(String userIdentity) {
		SetContext(new TraceContext(userIdentity));
	}

	public static void Apply(String token) {
		Clear();
		String[] split = token.split("#");
		if (split.length == 2) {
			TraceContext context = new TraceContext(split[0], split[1]);
			SetContext(context);
		} else if (split.length == 3) {
			TraceContext context = new TraceContext(split[0], split[1], split[2]);
			SetContext(context);
		}
	}

	//收到的response中存在token时，将已有token添加到当前上下文，并拆分_transferNo和_logNo;
	public static void ApplyResponse(String token) {
		Clear();
		String[] split = token.split("#");
		if (split.length == 2) {
			TraceContext context = new TraceContext(split[0], split[1]);
			SetContext(context);
		} else if (split.length == 3) {
			TraceContext context = new TraceContext(split[0], split[1]);
			context.SetTransferNo(split[2]);
			SetContext(context);
		}
	}

	public static void Apply(String userIdentity, String guid) {
		TraceContext context = new TraceContext(userIdentity, guid);
		SetContext(context);
	}


	public void SetTransferNo(String value) {
		String[] split = value.split(".");
		if (split.length > 1) {
			_logNo = Integer.parseInt(split[split.length - 1]);
			_transferNo = value.substring(0, value.length() - split[split.length - 1].length() - 1);
		}
	}

	public String GetTransferNo() {
		return String.format("%s.%s", _transferNo, _logNo);
	}

	public String UserIdentity;
	public String RequestId;
	private String _transferNo;
	private int _logNo;

	private TraceContext(String userIdentity) {
		this(userIdentity, Guid.randomGuid().toStr());
	}

	private TraceContext(String userIdentity, String guid) {
		this(userIdentity, guid, "1");
	}

	private TraceContext(String userIdentity, String guid, String no) {
		UserIdentity = userIdentity != null ? userIdentity : "";
		RequestId = guid != null ? guid : "";
		_transferNo = no != null ? no : "1";
	}

	@Override
	public String toString() {
		return String.format("%s#%s#%s", UserIdentity, RequestId, _transferNo);
	}

	public String ToTransferString() {
		String result = String.format("%s#%s#%s", UserIdentity, RequestId, GetTransferNo());
		_logNo++;
		return result;
	}

	public static void Clear() {
		local.remove();
	}

	;
}
