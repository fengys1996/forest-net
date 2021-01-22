import com.fnet.common.codec.MyLengthFieldBasedFrameDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

public class LengthFieldBasedFrameDecoderTest {

    @Test
    public void testDiscardTooLongFrame1() {
        EmbeddedChannel channel = new EmbeddedChannel(new MyLengthFieldBasedFrameDecoder());

        CompositeByteBuf cBuf = Unpooled.compositeBuffer();
        ByteBuf inBuf1 = Unpooled.buffer();
        ByteBuf inBuf2 = Unpooled.buffer();
        inBuf1.writeByte(1);
        inBuf1.writeInt(2);
        inBuf1.writeInt(32);

        for (int i = 0; i < 8; i++) {
            inBuf2.writeInt(i);
        }
        cBuf.addComponent(true, inBuf1);
        cBuf.addComponent(true, inBuf2);


        channel.writeInbound(cBuf);

        ByteBuf b = channel.readInbound();
        System.out.println("readableBytes: " + b.readableBytes());
        b.readByte();
        System.out.println("out_id:" + b.readInt());
        System.out.println("length:" + b.readInt());
        for (int i = 0; i < 8; i++) {
            System.out.println(b.readInt());
        }
        b.release();

        Assert.assertNull(channel.readInbound());
        channel.finish();
    }
}
