package test.org.helium.rpc.sample;

public class RpcSampleServiceImpl implements RpcSampleService{
	@Override
	public HelloResult hello(HelloArgs args) {
		System.out.println(args.toJsonObject());
		return new HelloResult();
	}

	@Override
	public RpcSampleResults add(RpcSampleArgs args) {
		System.out.println(args.toJsonObject());
		return new RpcSampleResults();
	}
}
