package kr.hs.dgsw.mdv.item;

/**
 * Created by DH on 2018-04-26.
 */

public class ByteBuffer {
    public byte[] buffer = new byte[256];

    public int write;

    public void put(byte[] buf, int len) {
        ensure(len);
        System.arraycopy(buf, 0, buffer, write, len);
        write += len;
    }

    private void ensure(int amt) {
        int req = write + amt;
        if (buffer.length <= req) {
            byte[] temp = new byte[req * 2];
            System.arraycopy(buffer, 0, temp, 0, write);
            buffer = temp;
        }
    }
}
