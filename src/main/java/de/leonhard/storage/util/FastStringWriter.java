package de.leonhard.storage.util;

import java.io.Writer;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * A Writer writing in a StringBuilder. This is NOT Thread safe.
 *
 * @author TheElectronWill Edited by JavaFactoryDev.
 */
@Getter
public class FastStringWriter extends Writer {

  /**
   * The underlying StringBuilder. Everything is appended to it.
   */
  private final StringBuilder stringBuilder;

  public FastStringWriter() {
    this.stringBuilder = new StringBuilder();
  }

  public FastStringWriter(@NonNull final StringBuilder stringBuilder) {
    this.stringBuilder = stringBuilder;
  }

  /**
   * Returns the content of the underlying StringBuilder, as a String. Equivalent to {@link FastStringWriter#getStringBuilder}.
   *
   * @return the content of the underlying StringBuilder.
   */
  @Override
  public String toString() {
    return this.stringBuilder.toString();
  }

  @Override
  public FastStringWriter append(final char c) {
    this.stringBuilder.append(c);
    return this;
  }

  @Override
  public FastStringWriter append(final CharSequence csq, final int start, final int end) {
    this.stringBuilder.append(csq, start, end);
    return this;
  }

  @Override
  public FastStringWriter append(final CharSequence csq) {
    this.stringBuilder.append(csq);
    return this;
  }

  @Override
  public void write(final @NotNull String str, final int off, final int len) {
    this.stringBuilder.append(str, off, off + len);
  }

  @Override
  public void write(final @NotNull String str) {
    this.stringBuilder.append(str);
  }

  @Override
  public void write(final char @NotNull [] cbuf, final int off, final int len) {
    this.stringBuilder.append(cbuf, off, len);
  }

  @Override
  public void write(final int c) {
    this.stringBuilder.append(c);
  }

  @Override
  public void flush() {
  }

  @Override
  public void close() {
  }
}
