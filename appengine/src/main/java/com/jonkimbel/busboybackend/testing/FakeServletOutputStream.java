package com.jonkimbel.busboybackend.testing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class FakeServletOutputStream extends ServletOutputStream {
  private ByteArrayOutputStream baos = new ByteArrayOutputStream();

  @Override
  public void write(int i) throws IOException {
    baos.write(i);
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setWriteListener(WriteListener listener) {
    // no-op.
  }

  public byte[] toByteArray() {
    return baos.toByteArray();
  }
}
