package com.fnet.out.server.handler;


import com.fnet.out.server.domainCenter.DomainDataService;
import com.fnet.out.server.sender.TransferCache;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.AppendableCharSequence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckHostHandler extends ChannelInboundHandlerAdapter {

    public static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
    public static final int DEFAULT_MAX_HEADER_SIZE = 8192;
    public static final int DEFAULT_INITIAL_BUFFER_SIZE = 128;

    private final AppendableCharSequence appendableCharSequence;
    private final LineParser lineParser;
    private final HeaderParser headerParser;

    private State currentState = State.SKIP_CONTROL_CHARS;
    private CharSequence name;
    private CharSequence value;

    public static final String HOST_HEADER_NAME = "Host";

    DomainDataService domainDataService;

    public CheckHostHandler(DomainDataService domainDataService) {
        this.appendableCharSequence = new AppendableCharSequence(DEFAULT_INITIAL_BUFFER_SIZE);
        this.lineParser = new LineParser(appendableCharSequence, DEFAULT_MAX_INITIAL_LINE_LENGTH);
        this.headerParser = new HeaderParser(appendableCharSequence, DEFAULT_MAX_HEADER_SIZE);
        this.domainDataService = domainDataService;
    }

    public CheckHostHandler(int initialBufferSize, int maxInitialLineLength, int maxHeaderSize, DomainDataService domainDataService) {
        appendableCharSequence = new AppendableCharSequence(initialBufferSize);
        lineParser = new LineParser(appendableCharSequence, maxInitialLineLength);
        headerParser = new HeaderParser(appendableCharSequence, maxHeaderSize);
        this.domainDataService = domainDataService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        ByteBuf slice = buffer.slice();
        switch (currentState) {
        case SKIP_CONTROL_CHARS:
            // Fall-through
        case READ_INITIAL: try {
            AppendableCharSequence line = lineParser.parse(buffer);
            if (line == null) {
                return;
            }
            String[] initialLine = splitInitialLine(line);
            if (initialLine.length < 3) {
                currentState = State.SKIP_CONTROL_CHARS;
                ctx.channel().close();
                return;
            }
            currentState = State.READ_HEADER;
            // fall-through
        } catch (Exception e) {
            ctx.channel().close();
            return;
        }
        case READ_HEADER:
            CharSequence host = findHost(buffer);
            if (host != null && doSomethingAfterFindHost((String) host, slice, ctx)) {
                return;
            }
            // fall-through
        default:
            ctx.channel().close();
        }
    }

    private boolean doSomethingAfterFindHost(String host, ByteBuf slice , ChannelHandlerContext ctx) throws Exception {
        Channel transferChannel = domainDataService.getTransferChannelByDomainName(host);
        if (transferChannel != null) {
            TransferCache.addOuterChannel(ctx.channel() , transferChannel);
            ctx.pipeline().remove(this);
            super.channelRead(ctx, slice);
            return true;
        }
        return false;
    }

    private enum State {
        SKIP_CONTROL_CHARS,
        READ_INITIAL,
        READ_HEADER
    }

    private static String[] splitInitialLine(AppendableCharSequence sb) {
        int aStart;
        int aEnd;
        int bStart;
        int bEnd;
        int cStart;
        int cEnd;

        aStart = findNonSPLenient(sb, 0);
        aEnd = findSPLenient(sb, aStart);

        bStart = findNonSPLenient(sb, aEnd);
        bEnd = findSPLenient(sb, bStart);

        cStart = findNonSPLenient(sb, bEnd);
        cEnd = findEndOfString(sb);

        return new String[] {
                sb.subStringUnsafe(aStart, aEnd),
                sb.subStringUnsafe(bStart, bEnd),
                cStart < cEnd? sb.subStringUnsafe(cStart, cEnd) : "" };
    }

    private static int findNonSPLenient(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            char c = sb.charAtUnsafe(result);
            if (isSPLenient(c)) {
                continue;
            }
            if (Character.isWhitespace(c)) {
                throw new IllegalArgumentException("Invalid separator");
            }
            return result;
        }
        return sb.length();
    }

    private static int findSPLenient(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            if (isSPLenient(sb.charAtUnsafe(result))) {
                return result;
            }
        }
        return sb.length();
    }

    private static int findEndOfString(AppendableCharSequence sb) {
        for (int result = sb.length() - 1; result > 0; --result) {
            if (!Character.isWhitespace(sb.charAtUnsafe(result))) {
                return result + 1;
            }
        }
        return 0;
    }

    private static boolean isSPLenient(char c) {
        return c == ' ' || c == (char) 0x09 || c == (char) 0x0B || c == (char) 0x0C || c == (char) 0x0D;
    }

    private CharSequence findHost(ByteBuf buffer) {
        AppendableCharSequence line = headerParser.parse(buffer);
        if (line == null) {
            return null;
        }
        if (line.length() > 0) {
            do {
                char firstChar = line.charAtUnsafe(0);
                if (name != null && (firstChar == ' ' || firstChar == '\t')) {
                    String trimmedLine = line.toString().trim();
                    String valueStr = String.valueOf(value);
                    value = valueStr + ' ' + trimmedLine;
                } else {
                    if (name != null && HOST_HEADER_NAME.contentEquals(name)) {
                        return value;
                    }
                    splitHeader(line);
                }

                line = headerParser.parse(buffer);
                if (line == null) {
                    return null;
                }
            } while (line.length() > 0);
        }
        return null;
    }

    private void splitHeader(AppendableCharSequence sb) {
        final int length = sb.length();
        int nameStart;
        int nameEnd;
        int colonEnd;
        int valueStart;
        int valueEnd;

        nameStart = findNonWhitespace(sb, 0, false);
        for (nameEnd = nameStart; nameEnd < length; nameEnd ++) {
            char ch = sb.charAtUnsafe(nameEnd);
            if (ch == ':' ||
                isOWS(ch)) {
                break;
            }
        }
        if (nameEnd == length) {
            throw new IllegalArgumentException("No colon found");
        }
        for (colonEnd = nameEnd; colonEnd < length; colonEnd ++) {
            if (sb.charAtUnsafe(colonEnd) == ':') {
                colonEnd ++;
                break;
            }
        }
        name = sb.subStringUnsafe(nameStart, nameEnd);
        valueStart = findNonWhitespace(sb, colonEnd, true);
        if (valueStart == length) {
            value = "";
        } else {
            valueEnd = findEndOfString(sb);
            value = sb.subStringUnsafe(valueStart, valueEnd);
        }
    }

    private static int findNonWhitespace(AppendableCharSequence sb, int offset, boolean validateOWS) {
        for (int result = offset; result < sb.length(); ++result) {
            char c = sb.charAtUnsafe(result);
            if (!Character.isWhitespace(c)) {
                return result;
            } else if (validateOWS && !isOWS(c)) {
                throw new IllegalArgumentException("Invalid separator, only a single space or horizontal tab allowed," +
                                                   " but received a '" + c + "'");
            }
        }
        return sb.length();
    }

    private static boolean isOWS(char ch) {
        return ch == ' ' || ch == (char) 0x09;
    }

    private static class HeaderParser implements ByteProcessor {

        private final AppendableCharSequence seq;
        private final int maxLength;
        private int size;

        public HeaderParser(AppendableCharSequence seq, int maxLength) {
            this.seq = seq;
            this.maxLength = maxLength;
        }

        public AppendableCharSequence parse(ByteBuf buffer) {
            final int oldSize = size;
            seq.reset();
            int i = buffer.forEachByte(this);
            if (i == -1) {
                size = oldSize;
                return null;
            }
            buffer.readerIndex(i + 1);
            return seq;
        }

        public void reset() {
            size = 0;
        }

        @Override
        public boolean process(byte value) throws Exception {
            char nextByte = (char) (value & 0xFF);

            if (nextByte == HttpConstants.LF) {
                int len = seq.length();
                // Drop CR if we had a CRLF pair
                if (len >= 1 && seq.charAtUnsafe(len - 1) == HttpConstants.CR) {
                    -- size;
                    seq.setLength(len - 1);
                }
                return false;
            }
            increaseCount();
            seq.append(nextByte);
            return true;
        }

        protected final void increaseCount() {
            if (++size  > maxLength) {
                throw newException(maxLength);
            }
        }

        protected TooLongFrameException newException(int maxLength) {
            return new TooLongFrameException("HTTP header is larger than " + maxLength + " bytes.");
        }
    }

    private final class LineParser extends HeaderParser {

        public LineParser(AppendableCharSequence seq, int maxLength) {
            super(seq, maxLength);
        }

        @Override
        public AppendableCharSequence parse(ByteBuf buffer) {
            reset();
            return super.parse(buffer);
        }

        @Override
        public boolean process(byte value) throws Exception {
            if (currentState == State.SKIP_CONTROL_CHARS) {
                char c = (char) (value & 0xFF);
                if (Character.isISOControl(c) || Character.isWhitespace(c)) {
                    increaseCount();
                    return true;
                }
                currentState = State.READ_INITIAL;
            }
            return super.process(value);
        }

        @Override
        protected TooLongFrameException newException(int maxLength) {
            return new TooLongFrameException("An HTTP line is larger than " + maxLength + " bytes.");
        }
    }
}
