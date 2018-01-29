import com.tiza.support.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

/**
 * Description: TestMain
 * Author: DIYILIU
 * Update: 2018-01-26 15:32
 */
public class TestMain {


    @Test
    public void test() {
        byte[] bytes = CommonUtil.hexStringToBytes("020600000002");


        bytes = CommonUtil.checkCRC(bytes);
        System.out.println(CommonUtil.bytesToStr(bytes));

    }


    @Test
    public void test1() {
        byte[] bytes = CommonUtil.hexStringToBytes("439B0000");
        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);

        int l = byteBuf.readInt();
        float f = Float.intBitsToFloat(l);

        System.out.println(f);
    }
}
