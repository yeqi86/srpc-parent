import com.iflytek.iptv.api.DemoApi;
import com.iflytek.iptv.dto.EventMsg;
import com.iflytek.sdk.client.rpc.RpcServiceFactory;
import com.iflytek.sdk.exception.ClassNotQualifiedException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = testClient.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class testClient {

    @Test
   public void contextLoads() {

        try {
            DemoApi api  =  RpcServiceFactory.getClass(DemoApi.class,"127.0.0.1:1232");
            EventMsg msg = new EventMsg();
            msg.setMsg("12345");
            msg.setName("232説的都是");
            api.sendMsg(msg);
        } catch (ClassNotQualifiedException e) {
            e.printStackTrace();
        }

    }
}
