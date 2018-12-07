package zk_distributed_lock;

/**
 * 序列化和反序列化自定义
 * @author haibozhang
 * 2018.4.16
 */
import java.io.UnsupportedEncodingException;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

public class Serializer implements ZkSerializer{

	@Override
	public byte[] serialize(Object data) throws ZkMarshallingError {
		if(data != null){
			try {
				return data.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Object deserialize(byte[] bytes) throws ZkMarshallingError {
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
